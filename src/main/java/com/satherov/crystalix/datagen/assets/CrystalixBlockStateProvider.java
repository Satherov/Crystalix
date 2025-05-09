package com.satherov.crystalix.datagen.assets;

import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.CrystalixRegistry;
import com.satherov.crystalix.content.block.CrystalixGlass;

public class CrystalixBlockStateProvider extends BlockStateProvider {

    public CrystalixBlockStateProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, Crystalix.MOD_ID, fileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        CrystalixRegistry.BLOCKS_MAP.forEach((color, set) ->
                set.forEach(this::registerCrystalixBlock)
        );
    }

    private void registerCrystalixBlock(CrystalixRegistry.BlockTypes type, DeferredHolder<Block, ? extends Block> block) {
        VariantBlockStateBuilder builder = getVariantBuilder(block.get());

        String path = "block/" + type.getSerializedName() + "/" + block.getId().getPath();
        ModelFile shadedModel = models().getExistingFile(modLoc(path));
        ModelFile noShadedModel = models().getExistingFile(modLoc(path + "_no_shade"));

        builder.partialState().with(CrystalixGlass.SHADELESS, false).modelForState().modelFile(shadedModel).addModel();
        builder.partialState().with(CrystalixGlass.SHADELESS, true).modelForState().modelFile(noShadedModel).addModel();
    }

}
