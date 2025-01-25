package com.satherov.crystalix.datagen.assets;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.CrystalixRegistry;

import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.minecraft.data.DataGenerator;

public class CrystalixBlockStateProvider extends BlockStateProvider {

    public CrystalixBlockStateProvider(DataGenerator generator, ExistingFileHelper fileHelper) {
        super(generator.getPackOutput(), Crystalix.MOD_ID, fileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        CrystalixRegistry.BLOCKS.getEntries().forEach(block -> simpleBlock(block.get(), getExistingFile(block.getId().getPath())));
    }

    private ModelFile getExistingFile(String path) {
        return models().getExistingFile(modLoc(path));
    }
}
