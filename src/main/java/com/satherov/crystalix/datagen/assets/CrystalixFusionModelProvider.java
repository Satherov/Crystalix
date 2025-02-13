package com.satherov.crystalix.datagen.assets;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.CrystalixRegistry;

import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

import com.supermartijn642.fusion.api.model.DefaultModelTypes;
import com.supermartijn642.fusion.api.model.ModelInstance;
import com.supermartijn642.fusion.api.model.data.ConnectingModelData;
import com.supermartijn642.fusion.api.predicate.DefaultConnectionPredicates;
import com.supermartijn642.fusion.api.provider.FusionModelProvider;

public class CrystalixFusionModelProvider extends FusionModelProvider {

    public CrystalixFusionModelProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(Crystalix.MOD_ID, packOutput, existingFileHelper);
    }

    @Override
    protected void generate() {
        CrystalixRegistry.BLOCKS_MAP.forEach((color, set) -> set.forEach((name, block) -> {
            this.shadedBlock(block, color, name);
            this.shadelessBlock(block, color, name);
        }));
    }

    private void shadedBlock(DeferredHolder<Block, ? extends Block> block, DyeColor color, String name) {
        this.addModel(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/" + name + "/" + block.getId().getPath()),
                ModelInstance.of(
                        DefaultModelTypes.CONNECTING,
                        ConnectingModelData.builder()
                                .parent(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/block"))
                                .texture("all", ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/" + name + "/" + color.getName()))
                                .connection(DefaultConnectionPredicates.isSameBlock())
                                .build()
                ));
    }

    private void shadelessBlock(DeferredHolder<Block, ? extends Block> block, DyeColor color, String name) {
        this.addModel(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/" + name + "/" + block.getId().getPath() + "_no_shade"),
                ModelInstance.of(
                        DefaultModelTypes.CONNECTING,
                        ConnectingModelData.builder()
                                .parent(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/no_shade_block"))
                                .texture("all", ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/" + name + "/" + color.getName()))
                                .connection(DefaultConnectionPredicates.isSameBlock())
                                .build()
                ));
    }
}
