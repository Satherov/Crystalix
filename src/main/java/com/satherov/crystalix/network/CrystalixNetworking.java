package com.satherov.crystalix.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import com.satherov.crystalix.Crystalix;

public final class CrystalixNetworking {

    private static final String PROTOCOL_VERSION = "1";
    private static final ResourceLocation CHANNEL_NAME =
            new ResourceLocation(Crystalix.MOD_ID, "main");

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(CHANNEL_NAME)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private CrystalixNetworking() {
    }

    public static void register(FMLCommonSetupEvent event) {
        int id = 0;
        CHANNEL.messageBuilder(CyclePropertyPayload.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CyclePropertyPayload::encode)
                .decoder(CyclePropertyPayload::decode)
                .consumerNetworkThread(CyclePropertyPayload.Handler::handle)
                .add();
    }

    public static void sendToServer(CyclePropertyPayload msg) {
        CHANNEL.sendToServer(msg);
    }

    public static void sendToPlayer(CyclePropertyPayload msg, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static void init() {
        FMLJavaModLoadingContext.get()
                .getModEventBus()
                .addListener(CrystalixNetworking::register);
    }
}
