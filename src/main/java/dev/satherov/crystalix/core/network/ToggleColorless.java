package dev.satherov.crystalix.core.network;

import lombok.extern.slf4j.Slf4j;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.client.lang.CSLanguage;
import dev.satherov.crystalix.common.item.CrystalixWand;
import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

@Slf4j
public record ToggleColorless(boolean enabled) implements CustomPacketPayload {
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleColorless> STREAM_CODEC = CustomPacketPayload.codec(
            ToggleColorless::encode,
            ToggleColorless::new
    );
    
    public static final Type<ToggleColorless> TYPE = new Type<>(Crystalix.rl("toggle_colorless"));
    
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(enabled);
    }
    
    private ToggleColorless(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBoolean());
    }
    
    public static void handle(final ToggleColorless message, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.flow().isServerbound() && ctx.player() instanceof ServerPlayer player) {
                ItemStack wand = CrystalixWand.find(player);
                if (wand.isEmpty()) return;
                wand.set(CSRegistry.APPLY_COLORLESS, message.enabled());
                player.displayClientMessage(
                        CSLanguage.PROPERTY_APPLY_COLORLESS.text(ChatFormatting.GRAY, message.enabled() ? CSLanguage.PROPERTY_ENABLED.text(ChatFormatting.DARK_GREEN) : CSLanguage.PROPERTY_DISABLED.text(ChatFormatting.DARK_RED)),
                        true
                );
                player.getInventory().setChanged();
            }
        }).exceptionally(e -> {
            log.error("Failed to pick wand properties from BlockState", e);
            return null;
        });
    }
}
