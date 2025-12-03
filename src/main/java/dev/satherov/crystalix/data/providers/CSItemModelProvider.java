package dev.satherov.crystalix.data.providers;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;

public class CSItemModelProvider extends ItemModelProvider {
    
    public CSItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Crystalix.MOD_ID, existingFileHelper);
    }
    
    @Override
    protected void registerModels() {
        CSRegistry.ENTRIES.forEach((type, holder) -> {
            withExistingParent(holder.getId().toString(), modLoc("block/" + holder.getId().getPath()));
        });
        
        CSRegistry.ITEMS.getEntries().stream()
                .filter(item -> !(item.get() instanceof BlockItem))
                .forEach(item -> handheldItem(item.get()));
    }
    
}
