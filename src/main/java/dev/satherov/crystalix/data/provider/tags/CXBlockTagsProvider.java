package dev.satherov.crystalix.data.provider.tags;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.sathlib.core.annotations.NothingNull;

import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

@NothingNull
public class CXBlockTagsProvider extends BlockTagsProvider {
    
    public CXBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, Crystalix.MOD_ID);
    }
    
    @Override
    protected void addTags(HolderLookup.Provider registries) {
        CXRegistry.BLOCKS.getEntries().forEach(entry -> this.tag(CXRegistry.CRYSTALIX_BLOCK_TAG).add(entry.get()));
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).addTag(CXRegistry.CRYSTALIX_BLOCK_TAG);
        this.tag(Tags.Blocks.GLASS_BLOCKS).addTag(CXRegistry.CRYSTALIX_BLOCK_TAG);
    }
}
