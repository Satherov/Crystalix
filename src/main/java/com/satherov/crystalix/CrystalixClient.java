package com.satherov.crystalix;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;

import com.mojang.blaze3d.platform.InputConstants;
import com.satherov.crystalix.client.KeybindManager;
import com.satherov.crystalix.content.CrystalixRegistry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Mod(value = Crystalix.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Crystalix.MOD_ID, value = Dist.CLIENT)
public class CrystalixClient {
    
    public static final Logger LOGGER = LogManager.getLogger();
    
    public CrystalixClient(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(CrystalixClient::onClientSetup);
        modEventBus.addListener(CrystalixClient::registerKeys);
        modEventBus.addListener(CrystalixClient::renderTypeSetup);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
    
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onClientSetup(final FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(new KeybindManager());
        
        if (ModList.get().isLoaded("keybindbundles")) {
            event.enqueueWork(() -> {
                Minecraft mc = Minecraft.getInstance();
                File dir = mc.gameDirectory;
                
                try {
                    Path targetPath = dir.toPath().resolve("keybind_bundles.json");
                    if (Files.notExists(targetPath)) {
                        Path temp = targetPath.resolveSibling(targetPath.getFileName() + ".tmp");
                        
                        try (InputStream inputStream = Crystalix.class.getClassLoader().getResourceAsStream("keybind_bundles.json")) {
                            
                            if (inputStream == null) {
                                throw new FileNotFoundException("Resource not found: keybind_bundles.json");
                            }
                            
                            Files.copy(inputStream, temp);
                            Files.move(temp, targetPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                            LOGGER.info("Successfully copied keybind_bundles.json to {}", targetPath.toAbsolutePath());
                            
                            KeybindManager.CYCLE_SHADELESS.setKey(InputConstants.UNKNOWN);
                            KeybindManager.CYCLE_REINFORCED.setKey(InputConstants.UNKNOWN);
                            KeybindManager.CYCLE_INVISIBLE.setKey(InputConstants.UNKNOWN);
                            KeybindManager.CYCLE_WATERLOGGABLE.setKey(InputConstants.UNKNOWN);
                            KeybindManager.CYCLE_LIGHT.setKey(InputConstants.UNKNOWN);
                            KeybindManager.CYCLE_GHOST.setKey(InputConstants.UNKNOWN);
                            
                            mc.execute(mc.options::save);
                        } catch (IOException e) {
                            try {
                                Files.deleteIfExists(temp);
                            } catch (IOException ex2) {
                                LOGGER.warn("Failed to delete temporary file: {}", ex2.getMessage());
                            }
                            throw e;
                        }
                        
                    }
                } catch (IOException ex) {
                    LOGGER.warn("Failed to copy keybind_bundles.json: {}", ex.getMessage());
                }
            });
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
    
    
    public static void renderTypeSetup(EntityRenderersEvent.RegisterRenderers event) {
        CrystalixRegistry.BLOCKS_MAP.forEach((color, set) -> set.forEach((name, block) -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.translucent())));
    }
}
