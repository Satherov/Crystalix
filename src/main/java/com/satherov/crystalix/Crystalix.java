package com.satherov.crystalix;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import com.satherov.crystalix.content.BatchProcessor;
import com.satherov.crystalix.content.CrystalixRegistry;
import com.satherov.crystalix.network.CrystalixNetworking;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

@Mod(Crystalix.MOD_ID)
@EventBusSubscriber(modid = Crystalix.MOD_ID)
public class Crystalix {
    
    public static final String MOD_ID = "crystalix";
    public static final Logger LOGGER = LogManager.getLogger();
    
    public Crystalix(IEventBus modEventBus, ModContainer modContainer) {
        
        modEventBus.addListener(CrystalixNetworking::registerPayloads);
        NeoForge.EVENT_BUS.addListener(BatchProcessor::tick);
        
        CrystalixRegistry.DATA_COMPONENT_TYPES.register(modEventBus);
        CrystalixRegistry.BLOCKS.register(modEventBus);
        CrystalixRegistry.ITEMS.register(modEventBus);
        CrystalixRegistry.CREATIVE_TABS.register(modEventBus);
        
        modContainer.registerConfig(ModConfig.Type.CLIENT, CrystalixConfig.Client.SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, CrystalixConfig.Common.SPEC);
    }
    
    @SubscribeEvent
    private static void opInDev(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer entity)) return;
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            ServerLevel level = entity.serverLevel();
            MinecraftServer server = level.getServer();
            ServerPlayer player = server.getPlayerList().getPlayer(UUID.fromString("380df991-f603-344c-a090-369bad2a924a")); // Dev UUID
            if (player == null) return;
            server.getPlayerList().op(player.getGameProfile());
        }
    }
    
    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
