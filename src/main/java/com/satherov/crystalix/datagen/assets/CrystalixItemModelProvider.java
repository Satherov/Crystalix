package com.satherov.crystalix.datagen.assets;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.CrystalixRegistry;

import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;

public class CrystalixItemModelProvider extends ItemModelProvider {

    public CrystalixItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Crystalix.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        CrystalixRegistry.BLOCKS_MAP.forEach((color, set) -> set.forEach((name, block) -> this.withExistingParent(block.getId().toString(), ResourceLocation.fromNamespaceAndPath(block.getId().getNamespace(), "block/" + name + "/" + block.getId().getPath()))));
        CrystalixRegistry.ITEMS.getEntries().stream().filter(item -> !(item.get() instanceof BlockItem)).forEach(item -> handheldItem(item.get()));
    }
}
