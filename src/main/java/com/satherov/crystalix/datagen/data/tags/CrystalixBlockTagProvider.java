package com.satherov.crystalix.datagen.data.tags;

import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.BlockSet;
import com.satherov.crystalix.content.CrystalixRegistry;
import com.satherov.crystalix.content.CrystalixTags;

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

        CrystalixRegistry.BLOCKS.getEntries().forEach(block -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.get()));

        BlockSet.apply(set -> {

            tag(BlockTags.WITHER_IMMUNE).addTag(CrystalixTags.BLOCKTAG_REINFORCED);

            // General Tags
            tag(CrystalixTags.BLOCKTAG_BLOCKS)
                    .addTag(CrystalixTags.BLOCKTAG_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_REINFORCED_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_LIGHT_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_DARK_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED)
                    .addTag(CrystalixTags.BLOCKTAG_REINFORCED)
                    .addTag(CrystalixTags.BLOCKTAG_LIGHT)
                    .addTag(CrystalixTags.BLOCKTAG_DARK)
                    .addTag(CrystalixTags.BLOCKTAG_GHOST);

            // Shaded Tags
            tag(CrystalixTags.BLOCKTAG_SHADED)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_LIGHT_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_DARK_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_LIGHT_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_DARK_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_DARK_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_DARK_GHOST_BLOCK);

            // Reinforced Tags
            tag(CrystalixTags.BLOCKTAG_REINFORCED)
                    .addTag(CrystalixTags.BLOCKTAG_REINFORCED_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_REINFORCED_LIGHT_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_REINFORCED_DARK_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_REINFORCED_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_REINFORCED_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_REINFORCED_DARK_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_LIGHT_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_DARK_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_DARK_GHOST_BLOCK);

            // Light Tags
            tag(CrystalixTags.BLOCKTAG_LIGHT)
                    .addTag(CrystalixTags.BLOCKTAG_LIGHT_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_REINFORCED_LIGHT_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_REINFORCED_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_LIGHT_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_LIGHT_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_LIGHT_GHOST_BLOCK);

            // Dark Tags
            tag(CrystalixTags.BLOCKTAG_DARK)
                    .addTag(CrystalixTags.BLOCKTAG_DARK_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_DARK_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_REINFORCED_DARK_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_REINFORCED_DARK_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_DARK_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_DARK_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_DARK_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_DARK_GHOST_BLOCK);

            // Ghost Tags
            tag(CrystalixTags.BLOCKTAG_GHOST)
                    .addTag(CrystalixTags.BLOCKTAG_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_DARK_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_REINFORCED_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_DARK_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_REINFORCED_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_REINFORCED_DARK_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_LIGHT_GHOST_BLOCK)
                    .addTag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_DARK_GHOST_BLOCK);


            // Individual Blocks
            tag(CrystalixTags.BLOCKTAG_BLOCK).add(set.BLOCK.get());
            tag(CrystalixTags.BLOCKTAG_SHADED_BLOCK).add(set.SHADELESS_BLOCK.get());
            tag(CrystalixTags.BLOCKTAG_REINFORCED_BLOCK).add(set.REINFORCED_BLOCK.get());
            tag(CrystalixTags.BLOCKTAG_LIGHT_BLOCK).add(set.LIGHT_BLOCK.get());
            tag(CrystalixTags.BLOCKTAG_DARK_BLOCK).add(set.DARK_BLOCK.get());
            tag(CrystalixTags.BLOCKTAG_GHOST_BLOCK).add(set.GHOST_BLOCK.get());

            tag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_BLOCK).add(set.SHADELESS_REINFORCED_BLOCK.get());
            tag(CrystalixTags.BLOCKTAG_SHADED_LIGHT_BLOCK).add(set.SHADELESS_LIGHT_BLOCK.get());
            tag(CrystalixTags.BLOCKTAG_SHADED_DARK_BLOCK).add(set.SHADELESS_DARK_BLOCK.get());
            tag(CrystalixTags.BLOCKTAG_SHADED_GHOST_BLOCK).add(set.SHADELESS_GHOST_BLOCK.get());

            tag(CrystalixTags.BLOCKTAG_REINFORCED_LIGHT_BLOCK).add(set.REINFORCED_LIGHT_BLOCK.get());
            tag(CrystalixTags.BLOCKTAG_REINFORCED_DARK_BLOCK).add(set.REINFORCED_DARK_BLOCK.get());
            tag(CrystalixTags.BLOCKTAG_REINFORCED_GHOST_BLOCK).add(set.REINFORCED_GHOST_BLOCK.get());

            tag(CrystalixTags.BLOCKTAG_LIGHT_GHOST_BLOCK).add(set.LIGHT_GHOST_BLOCK.get());
            tag(CrystalixTags.BLOCKTAG_DARK_GHOST_BLOCK).add(set.DARK_GHOST_BLOCK.get());

            tag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_LIGHT_BLOCK).add(set.SHADELESS_REINFORCED_LIGHT_BLOCK.get());
            tag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_DARK_BLOCK).add(set.SHADELESS_REINFORCED_DARK_BLOCK.get());
            tag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_GHOST_BLOCK).add(set.SHADELESS_REINFORCED_GHOST_BLOCK.get());

            tag(CrystalixTags.BLOCKTAG_SHADED_LIGHT_GHOST_BLOCK).add(set.SHADELESS_LIGHT_GHOST_BLOCK.get());
            tag(CrystalixTags.BLOCKTAG_SHADED_DARK_GHOST_BLOCK).add(set.SHADELESS_DARK_GHOST_BLOCK.get());

            tag(CrystalixTags.BLOCKTAG_REINFORCED_LIGHT_GHOST_BLOCK).add(set.REINFORCED_LIGHT_GHOST_BLOCK.get());
            tag(CrystalixTags.BLOCKTAG_REINFORCED_DARK_GHOST_BLOCK).add(set.REINFORCED_DARK_GHOST_BLOCK.get());

            tag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_LIGHT_GHOST_BLOCK).add(set.SHADELESS_REINFORCED_LIGHT_GHOST_BLOCK.get());
            tag(CrystalixTags.BLOCKTAG_SHADED_REINFORCED_DARK_GHOST_BLOCK).add(set.SHADELESS_REINFORCED_DARK_GHOST_BLOCK.get());
        });
    }
}
