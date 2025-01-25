package com.satherov.crystalix.datagen.data.tags;

import java.util.concurrent.CompletableFuture;

import com.satherov.crystalix.content.BlockSet;
import com.satherov.crystalix.content.CrystalixTags;

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

        BlockSet.apply(set -> {

            // General Tags
            tag(CrystalixTags.ITEMTAG_BLOCKS)
                    .addTag(CrystalixTags.ITEMTAG_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_REINFORCED_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_LIGHT_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_DARK_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED)
                    .addTag(CrystalixTags.ITEMTAG_REINFORCED)
                    .addTag(CrystalixTags.ITEMTAG_LIGHT)
                    .addTag(CrystalixTags.ITEMTAG_DARK)
                    .addTag(CrystalixTags.ITEMTAG_GHOST);

            // Shaded Tags
            tag(CrystalixTags.ITEMTAG_SHADED)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_LIGHT_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_DARK_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_LIGHT_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_DARK_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_DARK_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_DARK_GHOST_BLOCK);

            // Reinforced Tags
            tag(CrystalixTags.ITEMTAG_REINFORCED)
                    .addTag(CrystalixTags.ITEMTAG_REINFORCED_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_REINFORCED_LIGHT_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_REINFORCED_DARK_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_REINFORCED_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_REINFORCED_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_REINFORCED_DARK_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_LIGHT_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_DARK_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_DARK_GHOST_BLOCK);

            // Light Tags
            tag(CrystalixTags.ITEMTAG_LIGHT)
                    .addTag(CrystalixTags.ITEMTAG_LIGHT_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_REINFORCED_LIGHT_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_REINFORCED_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_LIGHT_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_LIGHT_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_LIGHT_GHOST_BLOCK);

            // Dark Tags
            tag(CrystalixTags.ITEMTAG_DARK)
                    .addTag(CrystalixTags.ITEMTAG_DARK_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_DARK_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_REINFORCED_DARK_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_REINFORCED_DARK_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_DARK_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_DARK_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_DARK_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_DARK_GHOST_BLOCK);

            // Ghost Tags
            tag(CrystalixTags.ITEMTAG_GHOST)
                    .addTag(CrystalixTags.ITEMTAG_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_DARK_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_REINFORCED_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_DARK_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_REINFORCED_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_REINFORCED_DARK_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_DARK_GHOST_BLOCK);


            // Individual Blocks
            tag(CrystalixTags.ITEMTAG_BLOCK).add(set.BLOCK.get().asItem());
            tag(CrystalixTags.ITEMTAG_SHADED_BLOCK).add(set.SHADELESS_BLOCK.get().asItem());
            tag(CrystalixTags.ITEMTAG_REINFORCED_BLOCK).add(set.REINFORCED_BLOCK.get().asItem());
            tag(CrystalixTags.ITEMTAG_LIGHT_BLOCK).add(set.LIGHT_BLOCK.get().asItem());
            tag(CrystalixTags.ITEMTAG_DARK_BLOCK).add(set.DARK_BLOCK.get().asItem());
            tag(CrystalixTags.ITEMTAG_GHOST_BLOCK).add(set.GHOST_BLOCK.get().asItem());

            tag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_BLOCK).add(set.SHADELESS_REINFORCED_BLOCK.get().asItem());
            tag(CrystalixTags.ITEMTAG_SHADED_LIGHT_BLOCK).add(set.SHADELESS_LIGHT_BLOCK.get().asItem());
            tag(CrystalixTags.ITEMTAG_SHADED_DARK_BLOCK).add(set.SHADELESS_DARK_BLOCK.get().asItem());
            tag(CrystalixTags.ITEMTAG_SHADED_GHOST_BLOCK).add(set.SHADELESS_GHOST_BLOCK.get().asItem());

            tag(CrystalixTags.ITEMTAG_REINFORCED_LIGHT_BLOCK).add(set.REINFORCED_LIGHT_BLOCK.get().asItem());
            tag(CrystalixTags.ITEMTAG_REINFORCED_DARK_BLOCK).add(set.REINFORCED_DARK_BLOCK.get().asItem());
            tag(CrystalixTags.ITEMTAG_REINFORCED_GHOST_BLOCK).add(set.REINFORCED_GHOST_BLOCK.get().asItem());

            tag(CrystalixTags.ITEMTAG_LIGHT_GHOST_BLOCK).add(set.LIGHT_GHOST_BLOCK.get().asItem());
            tag(CrystalixTags.ITEMTAG_DARK_GHOST_BLOCK).add(set.DARK_GHOST_BLOCK.get().asItem());

            tag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_LIGHT_BLOCK).add(set.SHADELESS_REINFORCED_LIGHT_BLOCK.get().asItem());
            tag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_DARK_BLOCK).add(set.SHADELESS_REINFORCED_DARK_BLOCK.get().asItem());
            tag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_GHOST_BLOCK).add(set.SHADELESS_REINFORCED_GHOST_BLOCK.get().asItem());

            tag(CrystalixTags.ITEMTAG_SHADED_LIGHT_GHOST_BLOCK).add(set.SHADELESS_LIGHT_GHOST_BLOCK.get().asItem());
            tag(CrystalixTags.ITEMTAG_SHADED_DARK_GHOST_BLOCK).add(set.SHADELESS_DARK_GHOST_BLOCK.get().asItem());

            tag(CrystalixTags.ITEMTAG_REINFORCED_LIGHT_GHOST_BLOCK).add(set.REINFORCED_LIGHT_GHOST_BLOCK.get().asItem());
            tag(CrystalixTags.ITEMTAG_REINFORCED_DARK_GHOST_BLOCK).add(set.REINFORCED_DARK_GHOST_BLOCK.get().asItem());

            tag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_LIGHT_GHOST_BLOCK).add(set.SHADELESS_REINFORCED_LIGHT_GHOST_BLOCK.get().asItem());
            tag(CrystalixTags.ITEMTAG_SHADED_REINFORCED_DARK_GHOST_BLOCK).add(set.SHADELESS_REINFORCED_DARK_GHOST_BLOCK.get().asItem());
        });
    }
}
