package dev.satherov.crystalix.data.provider.loot;

import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.sathlib.core.annotations.NothingNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Collections;
import java.util.Set;

@NothingNull
public class CXBlockLootProvider extends BlockLootSubProvider {
    
    protected CXBlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
    }
    
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Collections.singleton(CXRegistry.CRYSTALIX_BLOCK.get());
    }
    
    @Override
    protected void generate() {
        this.dropSelf(CXRegistry.CRYSTALIX_BLOCK.get());
    }
}
