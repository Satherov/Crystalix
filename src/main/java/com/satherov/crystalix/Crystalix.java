package com.satherov.crystalix;


import com.satherov.crystalix.client.KeybindManager;
import com.satherov.crystalix.content.CrystalixRegistry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

@Mod(Crystalix.MOD_ID)
public class Crystalix {
    public static final String MOD_ID = "crystalix";

    public Crystalix(IEventBus modEventBus, ModContainer modContainer) {
        CrystalixRegistry.BLOCKS.register(modEventBus);
        CrystalixRegistry.ITEMS.register(modEventBus);
        CrystalixRegistry.CREATIVE_TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, CrystalixConfig.SPEC);

        if(FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(Client::ClientSetup);
            modEventBus.addListener(Client::registerKeys);
            Client.registerConfigScreen(modContainer);
            modEventBus.addListener(Client::renderTypeSetup);
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

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    private static class DataFixer {
        @SubscribeEvent
        public static void registerAliases(FMLLoadCompleteEvent event) {
            for (DyeColor color : DyeColor.values()) {
                for (String blockName : new String[]{
                        "block",
                        "shadeless_block",
                        "reinforced_block",
                        "light_block",
                        "dark_block",
                        "ghost_block",
                        "shadeless_reinforced_block",
                        "shadeless_light_block",
                        "shadeless_dark_block",
                        "shadeless_ghost_block",
                        "reinforced_light_block",
                        "reinforced_dark_block",
                        "reinforced_ghost_block",
                        "light_ghost_block",
                        "dark_ghost_block",
                        "shadeless_reinforced_light_block",
                        "shadeless_reinforced_dark_block",
                        "shadeless_reinforced_ghost_block",
                        "shadeless_light_ghost_block",
                        "shadeless_dark_ghost_block",
                        "reinforced_light_ghost_block",
                        "reinforced_dark_ghost_block",
                        "shadeless_reinforced_light_ghost_block",
                        "shadeless_reinforced_dark_ghost_block"
                }) BuiltInRegistries.BLOCK.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_%s", color.getName(), blockName)), ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_crystalix_glass", color.getName())));
            }
        }
    }
}
