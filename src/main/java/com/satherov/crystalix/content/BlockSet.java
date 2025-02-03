package com.satherov.crystalix.content;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.satherov.crystalix.content.block.CrystalixBlock;
import com.satherov.crystalix.content.block.DarkCrystalixBlock;

import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BlockSet {

    public final String name;
    public final DyeColor color;

    private static final List<BlockSet> instances = new ArrayList<>();

    public static List<BlockSet> getSets() {
        return instances;
    }

    public final DeferredHolder<Block, CrystalixBlock> BLOCK;

    public final DeferredHolder<Block, CrystalixBlock> SHADELESS_BLOCK;
    public final DeferredHolder<Block, CrystalixBlock> REINFORCED_BLOCK;
    public final DeferredHolder<Block, CrystalixBlock> LIGHT_BLOCK;
    public final DeferredHolder<Block, DarkCrystalixBlock> DARK_BLOCK;
    public final DeferredHolder<Block, CrystalixBlock> GHOST_BLOCK;

    public final DeferredHolder<Block, CrystalixBlock> SHADELESS_REINFORCED_BLOCK;
    public final DeferredHolder<Block, CrystalixBlock> SHADELESS_LIGHT_BLOCK;
    public final DeferredHolder<Block, DarkCrystalixBlock> SHADELESS_DARK_BLOCK;
    public final DeferredHolder<Block, CrystalixBlock> SHADELESS_GHOST_BLOCK;

    public final DeferredHolder<Block, CrystalixBlock> REINFORCED_LIGHT_BLOCK;
    public final DeferredHolder<Block, DarkCrystalixBlock> REINFORCED_DARK_BLOCK;
    public final DeferredHolder<Block, CrystalixBlock> REINFORCED_GHOST_BLOCK;

    public final DeferredHolder<Block, CrystalixBlock> LIGHT_GHOST_BLOCK;
    public final DeferredHolder<Block, DarkCrystalixBlock> DARK_GHOST_BLOCK;

    public final DeferredHolder<Block, CrystalixBlock> SHADELESS_REINFORCED_LIGHT_BLOCK;
    public final DeferredHolder<Block, DarkCrystalixBlock> SHADELESS_REINFORCED_DARK_BLOCK;
    public final DeferredHolder<Block, CrystalixBlock> SHADELESS_REINFORCED_GHOST_BLOCK;

    public final DeferredHolder<Block, CrystalixBlock> SHADELESS_LIGHT_GHOST_BLOCK;
    public final DeferredHolder<Block, DarkCrystalixBlock> SHADELESS_DARK_GHOST_BLOCK;

    public final DeferredHolder<Block, CrystalixBlock> REINFORCED_LIGHT_GHOST_BLOCK;
    public final DeferredHolder<Block, DarkCrystalixBlock> REINFORCED_DARK_GHOST_BLOCK;

    public final DeferredHolder<Block, CrystalixBlock> SHADELESS_REINFORCED_LIGHT_GHOST_BLOCK;
    public final DeferredHolder<Block, DarkCrystalixBlock> SHADELESS_REINFORCED_DARK_GHOST_BLOCK;


    public BlockSet(DyeColor color) {
        this.name = color.getName();
        this.color = color;
        instances.add(this);

        BLOCK = register(String.format("%s_block", name), () -> new CrystalixBlock(color, glass(color)));

        SHADELESS_BLOCK = register(String.format("%s_shadeless_block", name), () -> new CrystalixBlock(color, glass(color)));
        REINFORCED_BLOCK = register(String.format("%s_reinforced_block", name), () -> new CrystalixBlock(color, glass(color).explosionResistance(1200.0F)));
        LIGHT_BLOCK = register(String.format("%s_light_block", name), () -> new CrystalixBlock(color, glass(color).lightLevel((BlockState state) -> 15)));
        DARK_BLOCK = register(String.format("%s_dark_block", name), () -> new DarkCrystalixBlock(color, glass(color)));
        GHOST_BLOCK = register(String.format("%s_ghost_block", name), () -> new CrystalixBlock(color, glass(color).noCollission()));

        SHADELESS_REINFORCED_BLOCK = register(String.format("%s_shadeless_reinforced_block", name), () -> new CrystalixBlock(color, glass(color).explosionResistance(1200.0F)));
        SHADELESS_LIGHT_BLOCK = register(String.format("%s_shadeless_light_block", name), () -> new CrystalixBlock(color, glass(color).lightLevel((BlockState state) -> 15)));
        SHADELESS_DARK_BLOCK = register(String.format("%s_shadeless_dark_block", name), () -> new DarkCrystalixBlock(color, glass(color).noCollission()));
        SHADELESS_GHOST_BLOCK = register(String.format("%s_shadeless_ghost_block", name), () -> new CrystalixBlock(color, glass(color).noCollission()));

        REINFORCED_LIGHT_BLOCK = register(String.format("%s_reinforced_light_block", name), () -> new CrystalixBlock(color, glass(color).explosionResistance(1200.0F).lightLevel((BlockState state) -> 15)));
        REINFORCED_DARK_BLOCK = register(String.format("%s_reinforced_dark_block", name), () -> new DarkCrystalixBlock(color, glass(color).explosionResistance(1200.0F)));
        REINFORCED_GHOST_BLOCK = register(String.format("%s_reinforced_ghost_block", name), () -> new CrystalixBlock(color, glass(color).explosionResistance(1200.0F).noCollission()));

        LIGHT_GHOST_BLOCK = register(String.format("%s_light_ghost_block", name), () -> new CrystalixBlock(color, glass(color).noCollission().lightLevel((BlockState state) -> 15)));
        DARK_GHOST_BLOCK = register(String.format("%s_dark_ghost_block", name), () -> new DarkCrystalixBlock(color, glass(color).noCollission()));

        SHADELESS_REINFORCED_LIGHT_BLOCK = register(String.format("%s_shadeless_reinforced_light_block", name), () -> new CrystalixBlock(color, glass(color).explosionResistance(1200.0F).lightLevel((BlockState state) -> 15)));
        SHADELESS_REINFORCED_DARK_BLOCK = register(String.format("%s_shadeless_reinforced_dark_block", name), () -> new DarkCrystalixBlock(color, glass(color).explosionResistance(1200.0F)));
        SHADELESS_REINFORCED_GHOST_BLOCK = register(String.format("%s_shadeless_reinforced_ghost_block", name), () -> new CrystalixBlock(color, glass(color).explosionResistance(1200.0F).noCollission()));

        SHADELESS_LIGHT_GHOST_BLOCK = register(String.format("%s_shadeless_light_ghost_block", name), () -> new CrystalixBlock(color, glass(color).noCollission().lightLevel((BlockState state) -> 15)));
        SHADELESS_DARK_GHOST_BLOCK = register(String.format("%s_shadeless_dark_ghost_block", name), () -> new DarkCrystalixBlock(color, glass(color).noCollission()));

        REINFORCED_LIGHT_GHOST_BLOCK = register(String.format("%s_reinforced_light_ghost_block", name), () -> new CrystalixBlock(color, glass(color).explosionResistance(1200.0F).noCollission().lightLevel((BlockState state) -> 15)));
        REINFORCED_DARK_GHOST_BLOCK = register(String.format("%s_reinforced_dark_ghost_block", name), () -> new DarkCrystalixBlock(color, glass(color).explosionResistance(1200.0F).noCollission()));

        SHADELESS_REINFORCED_LIGHT_GHOST_BLOCK = register(String.format("%s_shadeless_reinforced_light_ghost_block", name), () -> new CrystalixBlock(color, glass(color).explosionResistance(1200.0F).noCollission().lightLevel((BlockState state) -> 15)));
        SHADELESS_REINFORCED_DARK_GHOST_BLOCK = register(String.format("%s_shadeless_reinforced_dark_ghost_block", name), () -> new DarkCrystalixBlock(color, glass(color).explosionResistance(1200.0F).noCollission()));

    }

    private static BlockBehaviour.Properties glass(DyeColor color) {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_STAINED_GLASS).mapColor(color);
    }

    public static void apply(Consumer<BlockSet> consumer) {
        getSets().forEach(consumer);
    }

    private static <T extends Block> DeferredHolder<Block, T> register(String name, Supplier<T> properties) {
        DeferredHolder<Block, T> block;
        block = CrystalixRegistry.BLOCKS.register(name, properties);
        CrystalixRegistry.ITEMS.registerSimpleBlockItem(name, block);
        return block;
    }
}
