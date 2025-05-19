package com.satherov.crystalix.datagen;

import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.datagen.assets.CrystalixBlockModelProvider;
import com.satherov.crystalix.datagen.assets.CrystalixBlockStateProvider;
import com.satherov.crystalix.datagen.assets.CrystalixFusionModelProvider;
import com.satherov.crystalix.datagen.assets.CrystalixItemModelProvider;
import com.satherov.crystalix.datagen.assets.lang.EN_USProvider;
import com.satherov.crystalix.datagen.data.CrystalixLootTableProvider;
import com.satherov.crystalix.datagen.data.CrystalixRecipeProvider;
import com.satherov.crystalix.datagen.data.tags.CrystalixBlockTagProvider;
import com.satherov.crystalix.datagen.data.tags.CrystalixItemTagProvider;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Crystalix.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CrystalixDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());

        CrystalixDataProvider provider = new CrystalixDataProvider();

        // Assets
        provider.addSubProvider(event.includeClient(), new CrystalixBlockModelProvider(packOutput, fileHelper));
        provider.addSubProvider(event.includeClient(), new CrystalixFusionModelProvider(packOutput, fileHelper));
        provider.addSubProvider(event.includeClient(), new CrystalixBlockStateProvider(packOutput, fileHelper));
        provider.addSubProvider(event.includeClient(), new CrystalixItemModelProvider(packOutput, fileHelper));

        //Languages
        provider.addSubProvider(event.includeClient(), new EN_USProvider(packOutput));

        // Tags
        CrystalixBlockTagProvider blockTags = new CrystalixBlockTagProvider(packOutput, lookupProvider, fileHelper);
        provider.addSubProvider(event.includeServer(), blockTags);
        provider.addSubProvider(event.includeServer(), new CrystalixItemTagProvider(packOutput, lookupProvider, blockTags.contentsGetter()));

        // Recipes
        provider.addSubProvider(event.includeServer(), new CrystalixRecipeProvider(packOutput));

        // Loot Tables
        provider.addSubProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(CrystalixLootTableProvider::new, LootContextParamSets.BLOCK))));


        generator.addProvider(true, provider);
    }
}
