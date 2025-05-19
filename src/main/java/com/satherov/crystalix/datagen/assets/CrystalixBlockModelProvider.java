package com.satherov.crystalix.datagen.assets;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.CrystalixRegistry;

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

    private void shadedBlock(RegistryObject<? extends Block> block, DyeColor color, CrystalixRegistry.BlockTypes type) {
        this.singleTexture("block/" + type.getSerializedName() + "/" + block.getId().getPath(),
                        new ResourceLocation("block/cube_all"),
                        "all", modLoc("block/" + type.getSerializedName() + "/" + color.getName()))
                .renderType("translucent");
    }

    private void shadelessBlock(RegistryObject<? extends Block> block, DyeColor color, CrystalixRegistry.BlockTypes type) {
        this.singleTexture("block/" + type.getSerializedName() + "/" + block.getId().getPath() + "_no_shade",
                        modLoc("block/no_shade_block"),
                        "all", modLoc("block/" + type.getSerializedName() + "/" + color.getName()))
                .renderType("translucent");
    }
}
