package com.satherov.crystalix.datagen.assets;

import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.CrystalixRegistry;

public class CrystalixItemModelProvider extends ItemModelProvider {

    public CrystalixItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Crystalix.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        CrystalixRegistry.BLOCKS_MAP.forEach((color, set) -> {
            set.forEach((type, block) -> {
                withExistingParent(block.getId().toString(), modLoc("block/" + type.getSerializedName() + "/" + block.getId().getPath()));
            });
        });

        CrystalixRegistry.ITEMS.getEntries().stream()
                .filter(item -> !(item.get() instanceof BlockItem))
                .forEach(item -> handheldItem(item.get()));
    }

}
