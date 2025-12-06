package dev.satherov.crystalix.data.providers.loot;

import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class CSBlockLootProvider extends VanillaBlockLoot {
    
    public CSBlockLootProvider(Provider provider) {
        super(provider);
    }
    
    @Override
    public void generate() {
        this.getKnownBlocks().forEach(this::dropSelf);
    }
    
    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return CSRegistry.ENTRIES.values()
                .stream()
                .map(DeferredHolder::get)
                .collect(Collectors.toList());
    }
}
