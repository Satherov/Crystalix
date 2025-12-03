package dev.satherov.crystalix.core.network;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.client.lang.CSLanguage;
import dev.satherov.crystalix.common.item.CrystalixWand;
import dev.satherov.crystalix.common.properties.CSIntegerProperty;
import dev.satherov.crystalix.common.properties.CSProperties;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

public record SetColorPayload(int color) implements CustomPacketPayload {
    
    public static final StreamCodec<FriendlyByteBuf, SetColorPayload> STREAM_CODEC = CustomPacketPayload.codec(
            SetColorPayload::encode,
            SetColorPayload::new
    );
    
    public static final Type<SetColorPayload> TYPE = new Type<>(Crystalix.rl("set_color"));
    
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(color);
    }
    
    private SetColorPayload(FriendlyByteBuf buf) {
        this(buf.readVarInt());
    }
    
    public static void handle(final SetColorPayload message, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.flow().isServerbound() && ctx.player() instanceof ServerPlayer player) {
                ItemStack wand = CrystalixWand.find(player);
                if (wand.isEmpty()) return;
                
                CSProperties properties = CSProperties.of(wand);
                CSIntegerProperty prop = (CSIntegerProperty) properties.properties().get(CSProperties.COLOR);
                if (prop == null) return;
                prop.set(message.color());
                player.getInventory().setChanged();
            }
        }).exceptionally(e -> {
            ctx.disconnect(CSLanguage.NETWORK_CYCLE_FAILED.text(e.getMessage()));
            return null;
        });
    }
}
