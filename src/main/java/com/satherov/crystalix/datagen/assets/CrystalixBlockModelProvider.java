package com.satherov.crystalix.datagen.assets;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.BlockSet;

import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

public class CrystalixBlockModelProvider extends BlockModelProvider {

    public CrystalixBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Crystalix.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        BlockSet.apply(set -> {
            shadedBlock(set.BLOCK, set.color);
            shadelessBlock(set.SHADELESS_BLOCK, set.color);
            shadedBlock(set.REINFORCED_BLOCK, set.color);
            shadedBlock(set.LIGHT_BLOCK, set.color);
            shadedBlock(set.DARK_BLOCK, set.color);
            shadedBlock(set.GHOST_BLOCK, set.color);

            shadelessBlock(set.SHADELESS_REINFORCED_BLOCK, set.color);
            shadelessBlock(set.SHADELESS_LIGHT_BLOCK, set.color);
            shadelessBlock(set.SHADELESS_DARK_BLOCK, set.color);
            shadelessBlock(set.SHADELESS_GHOST_BLOCK, set.color);

            shadedBlock(set.REINFORCED_LIGHT_BLOCK, set.color);
            shadedBlock(set.REINFORCED_DARK_BLOCK, set.color);
            shadedBlock(set.REINFORCED_GHOST_BLOCK, set.color);

            shadedBlock(set.LIGHT_GHOST_BLOCK, set.color);
            shadedBlock(set.DARK_GHOST_BLOCK, set.color);

            shadelessBlock(set.SHADELESS_REINFORCED_LIGHT_BLOCK, set.color);
            shadelessBlock(set.SHADELESS_REINFORCED_DARK_BLOCK, set.color);
            shadelessBlock(set.SHADELESS_REINFORCED_GHOST_BLOCK, set.color);

            shadelessBlock(set.SHADELESS_LIGHT_GHOST_BLOCK, set.color);
            shadelessBlock(set.SHADELESS_DARK_GHOST_BLOCK, set.color);

            shadedBlock(set.REINFORCED_LIGHT_GHOST_BLOCK, set.color);
            shadedBlock(set.REINFORCED_DARK_GHOST_BLOCK, set.color);

            shadelessBlock(set.SHADELESS_REINFORCED_LIGHT_GHOST_BLOCK, set.color);
            shadelessBlock(set.SHADELESS_REINFORCED_DARK_GHOST_BLOCK, set.color);
        });
    }

    private void shadedBlock(DeferredHolder<Block, ? extends Block> block, DyeColor color) {
        this.singleTexture("block/" + block.getId().getPath(), ResourceLocation.withDefaultNamespace("block/cube_all"), "all", modLoc("block/" + color.getName())).renderType("translucent");
    }

    private void shadelessBlock(DeferredHolder<Block, ? extends Block> block, DyeColor color) {
        this.singleTexture("block/" + block.getId().getPath(), modLoc("block/no_shade_block"), "all", modLoc("block/" + color.getName())).renderType("translucent");
    }
}
