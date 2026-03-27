package dev.satherov.crystalix;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import dev.satherov.crystalix.config.ConfigLoader;
import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

@Slf4j
@Mod(Crystalix.MOD_ID)
@EventBusSubscriber(modid = Crystalix.MOD_ID)
public class Crystalix {
    
    public static final String MOD_ID = "crystalix";
    private static @Getter Crystalix instance;
    private final @Getter ConfigLoader loader = ConfigLoader.create();
    
    public Crystalix(IEventBus bus, FMLModContainer container) {
        if (Crystalix.instance != null) throw new IllegalStateException("Already initialized");
        Crystalix.instance = this;
        this.loader.discover(container);
        CSRegistry.register(bus);
    }
    
    @SubscribeEvent
    private static void opInDev(final PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer entity)) return;
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            ServerLevel level = entity.serverLevel();
            MinecraftServer server = level.getServer();
            server.getPlayerList().op(entity.getGameProfile());
        }
    }
    
    @SubscribeEvent
    private static void onConfigLoad(final ModConfigEvent.Loading event) {
        Crystalix.getInstance().getLoader().update(event.getConfig().getSpec());
    }
    
    @SubscribeEvent
    private static void onConfigReload(final ModConfigEvent.Reloading event) {
        Crystalix.getInstance().getLoader().update(event.getConfig().getSpec());
    }
    
    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, path);
    }
}
