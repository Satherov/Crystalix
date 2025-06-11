package com.satherov.crystalix.datagen.data.tags;

import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;

import com.satherov.crystalix.core.CrystalixRegistry;

import java.util.concurrent.CompletableFuture;

public class CrystalixItemTagProvider extends ItemTagsProvider {

    public CrystalixItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (var type : CrystalixRegistry.BlockTypes.values()) {
            var tagKey = CrystalixRegistry.ITEM_TAGS.get(type);
            tag(tagKey).addAll(
                    CrystalixRegistry.BLOCKS_MAP.values().stream()
                            .map(map -> map.get(type))
                            .map(holder -> CrystalixRegistry.ITEMS.getEntries().stream()
                                    .filter(item -> item.get() == holder.get().asItem())
                                    .findFirst()
                                    .map(DeferredHolder::getKey)
                                    .orElseThrow())
                            .toList()
            );

            tag(CrystalixRegistry.ITEM_TAG).addTag(tagKey);
        }
    }
}
