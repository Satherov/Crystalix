package com.satherov.crystalix.datagen.assets;

import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.CrystalixRegistry;
import com.satherov.crystalix.content.block.CrystalixGlass;
import com.supermartijn642.fusion.api.model.DefaultModelTypes;
import com.supermartijn642.fusion.api.model.ModelInstance;
import com.supermartijn642.fusion.api.model.data.ConnectingModelData;
import com.supermartijn642.fusion.api.predicate.DefaultConnectionPredicates;
import com.supermartijn642.fusion.api.provider.FusionModelProvider;
import com.supermartijn642.fusion.api.util.Pair;

import java.nio.file.Path;

@SuppressWarnings("unchecked")
public class CrystalixFusionModelProvider extends FusionModelProvider {
    
    public CrystalixFusionModelProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(Crystalix.MOD_ID, new PackOutput(Path.of(packOutput.getOutputFolder().toAbsolutePath().toString(), "crystalix-fusion-overrides")), existingFileHelper);
    }
    
    @Override
    protected void generate() {
        CrystalixRegistry.BLOCKS_MAP.forEach((color, set) ->
                                                     set.forEach((type, block) -> {
                                                         shadedBlock(block, color, type);
                                                         shadelessBlock(block, color, type);
                                                     })
        );
    }
    
    private void shadedBlock(DeferredHolder<Block, ? extends Block> block, DyeColor color, CrystalixRegistry.BlockTypes type) {
        this.addModel(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/" + type.getSerializedName() + "/" + block.getId().getPath()),
                      ModelInstance.of(
                              DefaultModelTypes.CONNECTING,
                              ConnectingModelData.builder()
                                                 .parent(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/block"))
                                                 .texture("all", ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/" + type.getSerializedName() + "/" + color.getName()))
                                                 .connection(DefaultConnectionPredicates.isSameBlock().and(DefaultConnectionPredicates.matchState(block.get(), Pair.of(CrystalixGlass.INVISIBLE, false))))
                                                 .build()
                      )
        );
    }
    
    private void shadelessBlock(DeferredHolder<Block, ? extends Block> block, DyeColor color, CrystalixRegistry.BlockTypes type) {
        this.addModel(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/" + type.getSerializedName() + "/" + block.getId().getPath() + "_no_shade"),
                      ModelInstance.of(
                              DefaultModelTypes.CONNECTING,
                              ConnectingModelData.builder()
                                                 .parent(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/no_shade_block"))
                                                 .texture("all", ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/" + type.getSerializedName() + "/" + color.getName()))
                                                 .connection(DefaultConnectionPredicates.isSameBlock().and(DefaultConnectionPredicates.matchState(block.get(), Pair.of(CrystalixGlass.INVISIBLE, false))))
                                                 .build()
                      )
        );
    }
}
