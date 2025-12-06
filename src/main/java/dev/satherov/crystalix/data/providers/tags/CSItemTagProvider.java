package dev.satherov.crystalix.data.providers.tags;

import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CSItemTagProvider extends ItemTagsProvider {
    
    public CSItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, CompletableFuture.completedFuture(TagLookup.empty()));
    }
    
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        Arrays.stream(CSRegistry.Types.values()).forEach(type -> this.tag(CSRegistry.ITEM_TAGS.get(type))
                .add(CSRegistry.ENTRIES.entrySet().stream()
                        .filter(cell -> cell.getKey().equals(type))
                        .map(Map.Entry::getValue)
                        .map(DeferredHolder::get)
                        .map(Block::asItem)
                        .toArray(Item[]::new)
                )
        );
        CSRegistry.ITEM_TAGS.values().forEach(tag -> this.tag(CSRegistry.ITEM_TAG).addTag(tag));
        
        this.tag(Tags.Items.GLASS_BLOCKS).addTag(CSRegistry.ITEM_TAG);
    }
}
