package dev.satherov.crystalix.data.providers;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.common.block.CrystalixGlass;
import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

public class CSBlockStateProvider extends BlockStateProvider {
    
    public CSBlockStateProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, Crystalix.MOD_ID, fileHelper);
    }
    
    @Override
    protected void registerStatesAndModels() {
        CSRegistry.ENTRIES.forEach(this::registerCrystalixBlock);
    }
    
    private void registerCrystalixBlock(@Nullable CSRegistry.Types type, @Nullable DeferredHolder<Block, ? extends Block> block) {
        if (type == null || block == null) return;
        VariantBlockStateBuilder builder = getVariantBuilder(block.get());
        
        String path = "block/" + block.getId().getPath();
        ModelFile modelColor = models().getExistingFile(modLoc(path + "_colored"));
        ModelFile modelColorNoShade = models().getExistingFile(modLoc(path + "_no_shade_colored"));
        ModelFile model = models().getExistingFile(modLoc(path));
        ModelFile modelNoShade = models().getExistingFile(modLoc(path + "_no_shade"));
        
        builder.partialState().with(CrystalixGlass.COLORED, true).with(CrystalixGlass.SHADELESS, false)
                .modelForState().modelFile(modelColor).addModel()
                .partialState().with(CrystalixGlass.COLORED, true).with(CrystalixGlass.SHADELESS, true)
                .modelForState().modelFile(modelColorNoShade).addModel()
                .partialState().with(CrystalixGlass.COLORED, false).with(CrystalixGlass.SHADELESS, false)
                .modelForState().modelFile(model).addModel()
                .partialState().with(CrystalixGlass.COLORED, false).with(CrystalixGlass.SHADELESS, true)
                .modelForState().modelFile(modelNoShade).addModel();
    }
    
}
