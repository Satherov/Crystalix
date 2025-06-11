package com.satherov.crystalix.datagen.assets;

import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.core.CrystalixRegistry;

public class CrystalixBlockModelProvider extends BlockModelProvider {

    public CrystalixBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Crystalix.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        CrystalixRegistry.BLOCKS_MAP.forEach((color, set) ->
                set.forEach((type, block) -> {
                    shadedBlock(block, color, type);
                    shadelessBlock(block, color, type);
                })
        );
    }

    private void shadedBlock(DeferredHolder<Block, ? extends Block> block, DyeColor color, CrystalixRegistry.BlockTypes type) {
        this.singleTexture("block/" + type.getSerializedName() + "/" + block.getId().getPath(),
                        ResourceLocation.withDefaultNamespace("block/cube_all"),
                        "all", modLoc("block/" + type.getSerializedName() + "/" + color.getName()))
                .renderType("translucent");
    }

    private void shadelessBlock(DeferredHolder<Block, ? extends Block> block, DyeColor color, CrystalixRegistry.BlockTypes type) {
        this.singleTexture("block/" + type.getSerializedName() + "/" + block.getId().getPath() + "_no_shade",
                        modLoc("block/no_shade_block"),
                        "all", modLoc("block/" + type.getSerializedName() + "/" + color.getName()))
                .renderType("translucent");
    }
}
