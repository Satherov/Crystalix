package com.satherov.crystalix.content;

import java.util.function.Supplier;

import com.satherov.crystalix.Crystalix;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CrystalixRegistry {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Crystalix.MOD_ID);
    public static DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(Crystalix.MOD_ID);
    public static DeferredRegister.Items ITEMS = DeferredRegister.createItems(Crystalix.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("creative_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable(String.format("itemGroup.%s", Crystalix.MOD_ID)))
            .icon(() -> CrystalixRegistry.WHITE.LIGHT_BLOCK.get().asItem().getDefaultInstance())
            .displayItems((parameters, output) ->
                    ITEMS.getEntries().stream()
                            .map(Supplier::get)
                            .map(Item::getDefaultInstance)
                            .forEach(output::accept)
            ).build()
    );

    public static final BlockSet WHITE = new BlockSet(DyeColor.WHITE);
    public static final BlockSet LIGHT_GRAY = new BlockSet(DyeColor.LIGHT_GRAY);
    public static final BlockSet GRAY = new BlockSet(DyeColor.GRAY);
    public static final BlockSet BLACK = new BlockSet(DyeColor.BLACK);
    public static final BlockSet BROWN = new BlockSet(DyeColor.BROWN);
    public static final BlockSet RED = new BlockSet(DyeColor.RED);
    public static final BlockSet ORANGE = new BlockSet(DyeColor.ORANGE);
    public static final BlockSet YELLOW = new BlockSet(DyeColor.YELLOW);
    public static final BlockSet LIME = new BlockSet(DyeColor.LIME);
    public static final BlockSet GREEN = new BlockSet(DyeColor.GREEN);
    public static final BlockSet CYAN = new BlockSet(DyeColor.CYAN);
    public static final BlockSet LIGHT_BLUE = new BlockSet(DyeColor.LIGHT_BLUE);
    public static final BlockSet BLUE = new BlockSet(DyeColor.BLUE);
    public static final BlockSet PURPLE = new BlockSet(DyeColor.PURPLE);
    public static final BlockSet MAGENTA = new BlockSet(DyeColor.MAGENTA);
    public static final BlockSet PINK = new BlockSet(DyeColor.PINK);
}
