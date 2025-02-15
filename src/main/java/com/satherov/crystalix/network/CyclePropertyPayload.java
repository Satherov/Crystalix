package com.satherov.crystalix.network;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.CrystalixUtil;
import com.satherov.crystalix.content.item.CrystalixWand;
import com.satherov.crystalix.content.properties.BlockProperties;
import com.satherov.crystalix.content.properties.IProperty;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record CyclePropertyPayload(String key, String value) implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, CyclePropertyPayload> STREAM_CODEC = CustomPacketPayload.codec(
            CyclePropertyPayload::encode,
            CyclePropertyPayload::new);

    public static final Type<CyclePropertyPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "cycle_property"));

    private CyclePropertyPayload(FriendlyByteBuf buffer) {
        this(buffer.readUtf(100), buffer.readUtf(100));
    }

    public CyclePropertyPayload(IProperty<?> property) {
        this(property.getKey(), property.getValueString());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(key);
        buffer.writeUtf(value);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler {
        public static void handle(final CyclePropertyPayload message, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                        if (ctx.flow().isServerbound() && ctx.player() instanceof ServerPlayer player) {
                            ItemStack wand = CrystalixUtil.getWand(player);
                            if (wand.isEmpty()) return;
                            BlockProperties properties = new BlockProperties(wand);

                            IProperty<?> property = properties.get(message.key);
                            if (property == null) return;
                            property.setValueString(message.value);

                            CrystalixWand.sendMessage(player, property);
                            player.getInventory().setChanged();
                        }
            }).exceptionally(e -> {
                ctx.disconnect(Component.translatable(String.format("%s.networking.cycle_property.failed", Crystalix.MOD_ID), e.getMessage()));
                return null;
            });
        }
    }
}