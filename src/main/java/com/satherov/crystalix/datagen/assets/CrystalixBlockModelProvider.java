package com.satherov.crystalix.datagen.assets;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.CrystalixRegistry;

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
        CrystalixRegistry.BLOCKS_MAP.forEach((color, set) -> set.forEach((name, block) -> {
            shadedBlock(block, color, name);
            shadelessBlock(block, color, name);
        }));
    }

    private void shadedBlock(DeferredHolder<Block, ? extends Block> block, DyeColor color, String name) {
        this.singleTexture("block/" + name + "/" + block.getId().getPath(), ResourceLocation.withDefaultNamespace("block/cube_all"), "all", modLoc("block/" + color.getName())).renderType("translucent");
    }

    private void shadelessBlock(DeferredHolder<Block, ? extends Block> block, DyeColor color, String name) {
        this.singleTexture("block/" + name + "/" + block.getId().getPath() + "_no_shade", modLoc("block/no_shade_block"), "all", modLoc("block/" + color.getName())).renderType("translucent");
    }

}
