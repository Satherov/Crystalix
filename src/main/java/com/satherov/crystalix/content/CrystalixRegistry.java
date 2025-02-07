package com.satherov.crystalix.content;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.block.CrystalixBlock;
import com.satherov.crystalix.content.item.CrystalixWand;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
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
            .icon(() -> CrystalixRegistry.BLOCKS_MAP.get(DyeColor.WHITE).get().asItem().getDefaultInstance())
            .displayItems((parameters, output) ->
                    ITEMS.getEntries().stream()
                            .map(Supplier::get)
                            .map(Item::getDefaultInstance)
                            .forEach(output::accept)
            ).build()
    );

    public static final TagKey<Block> BLOCKTAG_BLOCKS = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "blocks"));
    public static final TagKey<Item> ITEMTAG_BLOCKS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "blocks"));

    public static final DeferredHolder<Item, CrystalixWand> WAND = ITEMS.register("crystalix_wand", () -> new CrystalixWand(new Item.Properties()));

    public static final Map<DyeColor, DeferredHolder<Block, CrystalixBlock>> BLOCKS_MAP = Arrays.stream(DyeColor.values())
            .collect(Collectors.toMap(
                    color -> color,
                    color -> register(String.format("%s_crystalix_glass", color.getName()), () -> new CrystalixBlock(color))
            ));


    private static DeferredHolder<Block, CrystalixBlock> register(String name, Supplier<CrystalixBlock> properties) {
        DeferredHolder<Block, CrystalixBlock> block;
        block = CrystalixRegistry.BLOCKS.register(name, properties);
        CrystalixRegistry.ITEMS.registerSimpleBlockItem(name, block);
        return block;
    }
}
