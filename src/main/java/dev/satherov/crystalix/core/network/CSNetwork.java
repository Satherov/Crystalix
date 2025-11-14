package dev.satherov.crystalix.core.network;

import dev.satherov.crystalix.Crystalix;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

@EventBusSubscriber(modid = Crystalix.MOD_ID)
public final class CSNetwork {
    
    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Crystalix.MOD_ID);
        
        registrar.playToServer(CyclePropertyPayload.TYPE, CyclePropertyPayload.STREAM_CODEC, CyclePropertyPayload::handle);
        registrar.playToServer(SwapPropertiesPayload.TYPE, SwapPropertiesPayload.STREAM_CODEC, SwapPropertiesPayload::handle);
        registrar.playToServer(SetColorPayload.TYPE, SetColorPayload.STREAM_CODEC, SetColorPayload::handle);
        registrar.playToServer(ToggleColorless.TYPE, ToggleColorless.STREAM_CODEC, ToggleColorless::handle);
    }
    
    public static void sendToServer(CustomPacketPayload message) {
        PacketDistributor.sendToServer(message);
    }
    
    public static void sendToPlayer(CustomPacketPayload message, ServerPlayer player) {
        player.connection.send(message);
    }
}
