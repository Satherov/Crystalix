package com.satherov.crystalix;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;

import com.satherov.crystalix.client.KeybindManager;
import com.satherov.crystalix.content.CrystalixRegistry;
import com.satherov.crystalix.network.CrystalixNetworking;

@Mod(Crystalix.MOD_ID)
public class Crystalix {
    public static final String MOD_ID = "crystalix";

    public Crystalix(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(CrystalixNetworking::registerPayloads);

        CrystalixRegistry.DATA_COMPONENT_TYPES.register(modEventBus);
        CrystalixRegistry.BLOCKS.register(modEventBus);
        CrystalixRegistry.ITEMS.register(modEventBus);
        CrystalixRegistry.CREATIVE_TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, CrystalixConfig.SPEC);

        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(Client::ClientSetup);
            modEventBus.addListener(Client::registerKeys);
            modEventBus.addListener(Client::renderTypeSetup);
            Client.registerConfigScreen(modContainer);
        }
    }

    static class Client {
        public static void ClientSetup(final FMLClientSetupEvent event) {
            NeoForge.EVENT_BUS.register(new KeybindManager());
        }

        public static void registerKeys(final RegisterKeyMappingsEvent event) {
            event.register(KeybindManager.CYCLE_SHADELESS);
            event.register(KeybindManager.CYCLE_REINFORCED);
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
