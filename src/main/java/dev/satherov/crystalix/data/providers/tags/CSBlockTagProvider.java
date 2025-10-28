package dev.satherov.crystalix.data.providers.tags;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.common.block.CrystalixGlass;
import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;

import com.google.common.collect.Table;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class CSBlockTagProvider extends BlockTagsProvider {
    
    public CSBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Crystalix.MOD_ID, existingFileHelper);
    }
    
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        Arrays.stream(CSRegistry.Types.values()).forEach(type -> tag(CSRegistry.BLOCK_TAGS.get(type))
                .add(CSRegistry.ENTRIES.cellSet().stream()
                             .filter(cell -> cell.getRowKey().equals(type))
                             .map(Table.Cell::getValue)
                             .filter(Objects::nonNull)
                             .map(DeferredHolder::get)
                             .toArray(CrystalixGlass[]::new)
                )
        );
        CSRegistry.BLOCK_TAGS.values().forEach(tag -> tag(CSRegistry.BLOCK_TAG).addTag(tag));
        tag(BlockTags.MINEABLE_WITH_PICKAXE).addTag(CSRegistry.BLOCK_TAG);
        tag(Tags.Blocks.GLASS_BLOCKS).addTag(CSRegistry.BLOCK_TAG);
    }
}
