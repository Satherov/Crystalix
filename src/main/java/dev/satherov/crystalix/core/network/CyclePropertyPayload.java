package dev.satherov.crystalix.core.network;

import lombok.extern.slf4j.Slf4j;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.client.lang.CSLanguage;
import dev.satherov.crystalix.common.item.CrystalixWand;
import dev.satherov.crystalix.common.properties.CSProperties;
import dev.satherov.crystalix.common.properties.IProperty;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

@Slf4j
public record CyclePropertyPayload(ResourceLocation key, boolean dir) implements CustomPacketPayload {
    
    public static final StreamCodec<FriendlyByteBuf, CyclePropertyPayload> STREAM_CODEC = CustomPacketPayload.codec(
            CyclePropertyPayload::encode,
            CyclePropertyPayload::new
    );
    
    public static final Type<CyclePropertyPayload> TYPE = new Type<>(Crystalix.rl("cycle_property"));
    
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(key.toString());
        buffer.writeBoolean(dir);
    }
    
    private CyclePropertyPayload(FriendlyByteBuf buffer) {
        this(ResourceLocation.parse(buffer.readUtf(100)), buffer.readBoolean());
    }
    
    public static void handle(final CyclePropertyPayload message, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.flow().isServerbound() && ctx.player() instanceof ServerPlayer player) {
                ItemStack wand = CrystalixWand.find(player);
                if (wand.isEmpty()) return;
                CSProperties properties = CSProperties.of(wand);
                
                IProperty<?> property = properties.properties().get(message.key());
                if (property == null) {
                    log.warn("Unknown property: {}", message.key());
                    return;
                }
                
                property.next(message.dir());
                player.getInventory().setChanged();
            }
        }).exceptionally(e -> {
            ctx.disconnect(CSLanguage.NETWORK_CYCLE_FAILED.text(e.getMessage()));
            return null;
        });
    }
}