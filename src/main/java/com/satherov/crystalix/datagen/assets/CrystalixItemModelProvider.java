package com.satherov.crystalix.datagen.assets;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;

import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

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
                .forEach(item -> handheldItem(item.getId()));
    }

    private void handheldItem(ResourceLocation item) {
        getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", new ResourceLocation(item.getNamespace(), "item/" + item.getPath()));
    }
}
