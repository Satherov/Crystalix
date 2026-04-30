package dev.satherov.crystalix.network;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.client.lang.CXLanguage;
import dev.satherov.crystalix.common.item.CrystalixWandItem;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.sathlib.core.annotations.NothingNull;
import dev.satherov.sathlib.network.chat.SLComponent;
import dev.satherov.sathlib.network.handling.SLPayload;
import dev.satherov.sathlib.network.handling.ServerPayloadProvider;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

@NothingNull
public record ToggleColorless(boolean enabled) implements SLPayload<ToggleColorless> {
    
    private static final CustomPacketPayload.Type<ToggleColorless> TYPE = SLPayload.type(Crystalix.id("toggle_colorless"));
    
    private static final StreamCodec<RegistryFriendlyByteBuf, ToggleColorless> STREAM_CODEC = SLPayload.codec(ToggleColorless::encode, ToggleColorless::new);
    
    @Override
    public CustomPacketPayload.Type<ToggleColorless> type() {
        return ToggleColorless.TYPE;
    }
    
    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(this.enabled);
    }
    
    private ToggleColorless(RegistryFriendlyByteBuf buf) {
        this(buf.readBoolean());
    }
    
    public static final class Provider implements ServerPayloadProvider<ToggleColorless> {
        
        @Override
        public void handle(ToggleColorless payload, IPayloadContext context, ServerPlayer player) {
            final ItemStack stack = CrystalixWandItem.find(player);
            if (stack.isEmpty()) return;
            
            stack.set(CXRegistry.TINTED, payload.enabled());
            player.sendSystemMessage(Component.empty()
                    .append(CXLanguage.PROPERTY_TINTED.translate(ChatFormatting.GRAY))
                    .append(": ")
                    .append(SLComponent.enabledDisabled(payload.enabled()))
            );
            
            player.getInventory().setChanged();
        }
        
        @Override
        public CustomPacketPayload.Type<ToggleColorless> type() {
            return ToggleColorless.TYPE;
        }
        
        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ToggleColorless> codec() {
            return ToggleColorless.STREAM_CODEC;
        }
    }
}
