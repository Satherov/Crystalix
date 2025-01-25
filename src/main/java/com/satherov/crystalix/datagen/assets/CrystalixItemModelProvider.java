package com.satherov.crystalix.datagen.assets;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.CrystalixRegistry;

import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.minecraft.data.PackOutput;

public class CrystalixItemModelProvider extends ItemModelProvider {

    public CrystalixItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Crystalix.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        CrystalixRegistry.BLOCKS.getEntries().forEach(block -> simpleBlockItem(block.get()));
    }
}
