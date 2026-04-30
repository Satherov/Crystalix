package dev.satherov.crystalix.data.provider.tags;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.sathlib.core.annotations.NothingNull;

import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;

import java.util.concurrent.CompletableFuture;

@NothingNull
public class CXItemTagsProvider extends ItemTagsProvider {
    
    public CXItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, Crystalix.MOD_ID);
    }
    
    @Override
    protected void addTags(HolderLookup.Provider registries) {
        CXRegistry.ITEMS.getEntries().forEach(entry -> {
            if (!(entry.get() instanceof BlockItem)) return;
            this.tag(CXRegistry.CRYSTALIX_ITEM_TAG).add(entry.get());
        });
        this.tag(Tags.Items.GLASS_BLOCKS).addTag(CXRegistry.CRYSTALIX_ITEM_TAG);
    }
}
