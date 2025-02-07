package com.satherov.crystalix.datagen.data.tags;

import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.CrystalixRegistry;

import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;

public class CrystalixBlockTagProvider extends BlockTagsProvider {

    public CrystalixBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Crystalix.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).addTag(CrystalixRegistry.BLOCKTAG_BLOCKS);
        CrystalixRegistry.BLOCKS.getEntries().forEach(block -> tag(CrystalixRegistry.BLOCKTAG_BLOCKS).add(block.get()));
    }
}
