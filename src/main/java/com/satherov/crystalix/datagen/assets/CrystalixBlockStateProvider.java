package com.satherov.crystalix.datagen.assets;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.CrystalixRegistry;
import com.satherov.crystalix.content.block.CrystalixGlass;

import net.minecraft.world.level.block.Block;

import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.data.DataGenerator;

public class CrystalixBlockStateProvider extends BlockStateProvider {

    public CrystalixBlockStateProvider(DataGenerator generator, ExistingFileHelper fileHelper) {
        super(generator.getPackOutput(), Crystalix.MOD_ID, fileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        CrystalixRegistry.BLOCKS_MAP.forEach((color, set) -> set.forEach(this::registerCrystalixBlock));
    }

    private void registerCrystalixBlock(String name, DeferredHolder<Block, ? extends Block> block) {
        VariantBlockStateBuilder builder = getVariantBuilder(block.get());

        ModelFile shadedModel = models().getExistingFile(modLoc("block/" + name + "/" + block.getId().getPath()));
        ModelFile noShadedModel = models().getExistingFile(modLoc("block/" + name + "/" + block.getId().getPath() + "_no_shade"));

        builder.partialState().with(CrystalixGlass.SHADELESS, false).modelForState().modelFile(shadedModel).addModel();
        builder.partialState().with(CrystalixGlass.SHADELESS, true).modelForState().modelFile(noShadedModel).addModel();
    }
}
