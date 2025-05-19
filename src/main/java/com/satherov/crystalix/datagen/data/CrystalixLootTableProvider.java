package com.satherov.crystalix.datagen.data;

import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.registries.RegistryObject;

import com.satherov.crystalix.content.CrystalixRegistry;

import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class CrystalixLootTableProvider extends VanillaBlockLoot {

    @Override
    public void generate() {
        getKnownBlocks().forEach(this::dropSelf);
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return CrystalixRegistry.BLOCKS.getEntries()
                .stream()
                .map(RegistryObject::get)
                .collect(Collectors.toList());
    }
}
