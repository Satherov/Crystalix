package com.satherov.crystalix.datagen.data;

import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

import com.satherov.crystalix.content.CrystalixRegistry;

import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;

public class CrystalixLootTableProvider extends VanillaBlockLoot {

    public CrystalixLootTableProvider(Provider provider) {
        super(provider);
    }

    @Override
    public void generate() {
        getKnownBlocks().forEach(this::dropSelf);
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return CrystalixRegistry.BLOCKS.getEntries()
                .stream()
                .map(DeferredHolder::get)
                .collect(Collectors.toList());
    }
}
