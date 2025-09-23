package com.satherov.crystalix;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import com.mojang.blaze3d.platform.InputConstants;
import com.satherov.crystalix.client.KeybindManager;
import com.satherov.crystalix.content.BatchProcessor;
import com.satherov.crystalix.content.CrystalixRegistry;
import com.satherov.crystalix.network.CrystalixNetworking;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Mod(Crystalix.MOD_ID)
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
        
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(Client::ClientSetup);
            modEventBus.addListener(Client::registerKeys);
            modEventBus.addListener(Client::renderTypeSetup);
            Client.registerConfigScreen(modContainer);
        }
        
        NeoForge.EVENT_BUS.addListener(Crystalix::opInDev);
    }
    
    private static void opInDev(PlayerEvent.PlayerLoggedInEvent event) {
        if (FMLLoader.isProduction()) return;
        if (!(event.getEntity() instanceof ServerPlayer entity)) return;
        ServerLevel level = entity.serverLevel();
        MinecraftServer server = level.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(UUID.fromString("380df991-f603-344c-a090-369bad2a924a")); // Dev UUID
        if (player == null) return;
        server.getPlayerList().op(player.getGameProfile());
    }
    
    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
    
    static class Client {
        public static void ClientSetup(final FMLClientSetupEvent event) {
            NeoForge.EVENT_BUS.register(new KeybindManager());
            
            if (ModList.get().isLoaded("keybindbundles")) {
                Minecraft mc = Minecraft.getInstance();
                File dir = mc.gameDirectory;
                try {
                    Path targetPath = dir.toPath().resolve("keybind_bundles.json");
                    if (Files.notExists(targetPath)) {
                        try (InputStream inputStream = Crystalix.class.getClassLoader().getResourceAsStream("keybind_bundles.json")) {
                            
                            if (inputStream == null) {
                                throw new FileNotFoundException("Resource not found: keybind_bundles.json");
                            }
                            
                            Files.copy(inputStream, targetPath);
                            LOGGER.info("Successfully copied keybind_bundles.json to {}", targetPath.toAbsolutePath());
                            KeybindManager.CYCLE_SHADELESS.setKey(InputConstants.UNKNOWN);
                            KeybindManager.CYCLE_REINFORCED.setKey(InputConstants.UNKNOWN);
                            KeybindManager.CYCLE_INVISIBLE.setKey(InputConstants.UNKNOWN);
                            KeybindManager.CYCLE_WATERLOGGABLE.setKey(InputConstants.UNKNOWN);
                            KeybindManager.CYCLE_LIGHT.setKey(InputConstants.UNKNOWN);
                            KeybindManager.CYCLE_GHOST.setKey(InputConstants.UNKNOWN);
                            mc.options.save();
                        }
                        
                    }
                } catch (IOException ex) {
                    LOGGER.warn("Failed to copy keybind_bundles.json: {}", ex.getMessage());
                }
            }
            
        }
        
        public static void registerKeys(final RegisterKeyMappingsEvent event) {
            event.register(KeybindManager.DIRECTION_MODIFIER);
            event.register(KeybindManager.CYCLE_INVISIBLE);
            event.register(KeybindManager.CYCLE_SHADELESS);
            event.register(KeybindManager.CYCLE_REINFORCED);
            event.register(KeybindManager.CYCLE_WATERLOGGABLE);
            event.register(KeybindManager.CYCLE_LIGHT);
            event.register(KeybindManager.CYCLE_GHOST);
        }
        
        public static void registerConfigScreen(ModContainer modContainer) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
        
        public static void renderTypeSetup(EntityRenderersEvent.RegisterRenderers event) {
            CrystalixRegistry.BLOCKS_MAP.forEach((color, set) -> set.forEach((name, block) -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.translucent())));
        }
    }
}
