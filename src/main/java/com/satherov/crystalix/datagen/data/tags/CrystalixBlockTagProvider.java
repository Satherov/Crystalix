package com.satherov.crystalix.datagen.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;

import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.CrystalixRegistry;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CrystalixBlockTagProvider extends BlockTagsProvider {

    public CrystalixBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Crystalix.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (var type : CrystalixRegistry.BlockTypes.values()) {
            var tagKey = CrystalixRegistry.BLOCK_TAGS.get(type);
            CrystalixRegistry.BLOCKS_MAP.values().stream()
                    .map(map -> map.get(type))
                    .map(RegistryObject::get)
                    .toList()
                    .forEach(entry -> tag(tagKey).add(entry));

            tag(CrystalixRegistry.BLOCK_TAG).addTag(tagKey);
        }

        tag(BlockTags.MINEABLE_WITH_PICKAXE).addTag(CrystalixRegistry.BLOCK_TAGS.get(CrystalixRegistry.BlockTypes.GLASS));
        tag(Tags.Blocks.GLASS).addTag(CrystalixRegistry.BLOCK_TAGS.get(CrystalixRegistry.BlockTypes.GLASS));
    }
}
