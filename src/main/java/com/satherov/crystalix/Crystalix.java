package com.satherov.crystalix;

import com.satherov.crystalix.content.BlockSet;
import com.satherov.crystalix.content.CrystalixRegistry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

@Mod(Crystalix.MOD_ID)
public class Crystalix {
    public static final String MOD_ID = "crystalix";

    public Crystalix(IEventBus modEventBus, ModContainer modContainer) {
        CrystalixRegistry.BLOCKS.register(modEventBus);
        CrystalixRegistry.ITEMS.register(modEventBus);
        CrystalixRegistry.CREATIVE_TABS.register(modEventBus);
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    private static class DataFixer {
        @SubscribeEvent
        public static void registerAliases(FMLLoadCompleteEvent event) {

            BlockSet.apply(blockSet -> {
                BuiltInRegistries.BLOCK.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_block", blockSet.name)), blockSet.SHADELESS_BLOCK.getId());
                BuiltInRegistries.BLOCK.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_reinforced_block", blockSet.name)), blockSet.SHADELESS_REINFORCED_BLOCK.getId());
                BuiltInRegistries.BLOCK.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_light_block", blockSet.name)), blockSet.SHADELESS_LIGHT_BLOCK.getId());
                BuiltInRegistries.BLOCK.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_dark_block", blockSet.name)), blockSet.SHADELESS_DARK_BLOCK.getId());
                BuiltInRegistries.BLOCK.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_ghost_block", blockSet.name)), blockSet.SHADELESS_GHOST_BLOCK.getId());
                BuiltInRegistries.BLOCK.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_reinforced_light_block", blockSet.name)), blockSet.SHADELESS_REINFORCED_LIGHT_BLOCK.getId());
                BuiltInRegistries.BLOCK.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_reinforced_dark_block", blockSet.name)), blockSet.SHADELESS_REINFORCED_DARK_BLOCK.getId());
                BuiltInRegistries.BLOCK.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_reinforced_ghost_block", blockSet.name)), blockSet.SHADELESS_REINFORCED_GHOST_BLOCK.getId());
                BuiltInRegistries.BLOCK.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_light_ghost_block", blockSet.name)), blockSet.SHADELESS_LIGHT_GHOST_BLOCK.getId());
                BuiltInRegistries.BLOCK.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_dark_ghost_block", blockSet.name)), blockSet.SHADELESS_DARK_GHOST_BLOCK.getId());
                BuiltInRegistries.BLOCK.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_reinforced_light_ghost_block", blockSet.name)), blockSet.SHADELESS_REINFORCED_LIGHT_GHOST_BLOCK.getId());
                BuiltInRegistries.BLOCK.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_reinforced_dark_ghost_block", blockSet.name)), blockSet.SHADELESS_REINFORCED_DARK_GHOST_BLOCK.getId());

                BuiltInRegistries.ITEM.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_block", blockSet.name)), blockSet.SHADELESS_BLOCK.getId());
                BuiltInRegistries.ITEM.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_reinforced_block", blockSet.name)), blockSet.SHADELESS_REINFORCED_BLOCK.getId());
                BuiltInRegistries.ITEM.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_light_block", blockSet.name)), blockSet.SHADELESS_LIGHT_BLOCK.getId());
                BuiltInRegistries.ITEM.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_dark_block", blockSet.name)), blockSet.SHADELESS_DARK_BLOCK.getId());
                BuiltInRegistries.ITEM.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_ghost_block", blockSet.name)), blockSet.SHADELESS_GHOST_BLOCK.getId());
                BuiltInRegistries.ITEM.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_reinforced_light_block", blockSet.name)), blockSet.SHADELESS_REINFORCED_LIGHT_BLOCK.getId());
                BuiltInRegistries.ITEM.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_reinforced_dark_block", blockSet.name)), blockSet.SHADELESS_REINFORCED_DARK_BLOCK.getId());
                BuiltInRegistries.ITEM.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_reinforced_ghost_block", blockSet.name)), blockSet.SHADELESS_REINFORCED_GHOST_BLOCK.getId());
                BuiltInRegistries.ITEM.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_light_ghost_block", blockSet.name)), blockSet.SHADELESS_LIGHT_GHOST_BLOCK.getId());
                BuiltInRegistries.ITEM.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_dark_ghost_block", blockSet.name)), blockSet.SHADELESS_DARK_GHOST_BLOCK.getId());
                BuiltInRegistries.ITEM.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_reinforced_light_ghost_block", blockSet.name)), blockSet.SHADELESS_REINFORCED_LIGHT_GHOST_BLOCK.getId());
                BuiltInRegistries.ITEM.addAlias(ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("%s_shaded_reinforced_dark_ghost_block", blockSet.name)), blockSet.SHADELESS_REINFORCED_DARK_GHOST_BLOCK.getId());
            });
        }
    }
}
