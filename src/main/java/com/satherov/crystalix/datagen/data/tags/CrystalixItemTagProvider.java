package com.satherov.crystalix.datagen.data.tags;

import java.util.concurrent.CompletableFuture;

import com.satherov.crystalix.content.CrystalixRegistry;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;

public class CrystalixItemTagProvider extends ItemTagsProvider {

    public CrystalixItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        CrystalixRegistry.ITEMS.getEntries().forEach(block -> tag(CrystalixRegistry.ITEMTAG_BLOCKS).add(block.get()));
        CrystalixRegistry.BLOCKS_MAP.forEach((color, set) -> set.forEach((name, block) -> {
            if (name.equals("glass")) tag(CrystalixRegistry.ITEMTAG_GLASS).add(block.get().asItem());
            if (name.equals("clear")) tag(CrystalixRegistry.ITEMTAG_CLEAR).add(block.get().asItem());
            if (name.equals("bordered")) tag(CrystalixRegistry.ITEMTAG_BORDERED).add(block.get().asItem());
        }));
    }
}
