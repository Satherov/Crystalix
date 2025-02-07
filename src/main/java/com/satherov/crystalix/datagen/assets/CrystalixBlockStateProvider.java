package com.satherov.crystalix.datagen.assets;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.CrystalixRegistry;
import com.satherov.crystalix.content.block.CrystalixBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.data.DataGenerator;

public class CrystalixBlockStateProvider extends BlockStateProvider {

    public static final BooleanProperty SHADED = CrystalixBlock.SHADED;

    public CrystalixBlockStateProvider(DataGenerator generator, ExistingFileHelper fileHelper) {
        super(generator.getPackOutput(), Crystalix.MOD_ID, fileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        CrystalixRegistry.BLOCKS.getEntries().forEach(this::registerCrystalixBlock);
    }

    private void registerCrystalixBlock(DeferredHolder<Block, ? extends Block> block) {
        VariantBlockStateBuilder builder = getVariantBuilder(block.get());

        ModelFile shadedModel = models().getExistingFile(modLoc(block.getId().getPath()));
        ModelFile noShadedModel = models().getExistingFile(modLoc(block.getId().getPath() + "_no_shade"));

        builder.partialState().with(SHADED, true).modelForState().modelFile(shadedModel).addModel();
        builder.partialState().with(SHADED, false).modelForState().modelFile(noShadedModel).addModel();
    }
}
