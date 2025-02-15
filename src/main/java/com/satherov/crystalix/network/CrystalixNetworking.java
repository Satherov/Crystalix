package com.satherov.crystalix.network;

import com.satherov.crystalix.Crystalix;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public final class CrystalixNetworking {

    private CrystalixNetworking() { }

    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Crystalix.MOD_ID);

        registrar.playToServer(CyclePropertyPayload.TYPE, CyclePropertyPayload.STREAM_CODEC, CyclePropertyPayload.Handler::handle);
    }

    public static void sendToServer(CustomPacketPayload message) {
        PacketDistributor.sendToServer(message);
    }

    public static void sendToPlayer(CustomPacketPayload message, ServerPlayer player) {
        player.connection.send(message);
    }
}
