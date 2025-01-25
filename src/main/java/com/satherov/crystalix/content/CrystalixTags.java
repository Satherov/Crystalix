package com.satherov.crystalix.content;

import com.satherov.crystalix.Crystalix;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CrystalixTags {

    private static TagKey<Block> createBlockTag(String path) {
        return BlockTags.create(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, path));
    }

    private static TagKey<Item> createItemTag(String path) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, path));
    }

    public static final TagKey<Block> BLOCKTAG_BLOCKS = createBlockTag("blocks");
    public static final TagKey<Block> BLOCKTAG_GHOST = createBlockTag("ghost");
    public static final TagKey<Block> BLOCKTAG_LIGHT = createBlockTag("light");
    public static final TagKey<Block> BLOCKTAG_DARK = createBlockTag("dark");
    public static final TagKey<Block> BLOCKTAG_REINFORCED = createBlockTag("reinforced");
    public static final TagKey<Block> BLOCKTAG_SHADED = createBlockTag("bordered");

    public static final TagKey<Block> BLOCKTAG_BLOCK = createBlockTag("block");
    public static final TagKey<Block> BLOCKTAG_SHADED_BLOCK = createBlockTag("shaded_block");
    public static final TagKey<Block> BLOCKTAG_REINFORCED_BLOCK = createBlockTag("reinforced_block");
    public static final TagKey<Block> BLOCKTAG_LIGHT_BLOCK = createBlockTag("light_block");
    public static final TagKey<Block> BLOCKTAG_DARK_BLOCK = createBlockTag("dark_block");
    public static final TagKey<Block> BLOCKTAG_GHOST_BLOCK = createBlockTag("ghost_block");

    public static final TagKey<Block> BLOCKTAG_SHADED_REINFORCED_BLOCK = createBlockTag("shaded_reinforced_block");
    public static final TagKey<Block> BLOCKTAG_SHADED_LIGHT_BLOCK = createBlockTag("shaded_light_block");
    public static final TagKey<Block> BLOCKTAG_SHADED_DARK_BLOCK = createBlockTag("shaded_dark_block");
    public static final TagKey<Block> BLOCKTAG_SHADED_GHOST_BLOCK = createBlockTag("shaded_ghost_block");

    public static final TagKey<Block> BLOCKTAG_REINFORCED_LIGHT_BLOCK = createBlockTag("reinforced_light_block");
    public static final TagKey<Block> BLOCKTAG_REINFORCED_DARK_BLOCK = createBlockTag("reinforced_dark_block");
    public static final TagKey<Block> BLOCKTAG_REINFORCED_GHOST_BLOCK = createBlockTag("reinforced_ghost_block");

    public static final TagKey<Block> BLOCKTAG_LIGHT_GHOST_BLOCK = createBlockTag("light_ghost_block");
    public static final TagKey<Block> BLOCKTAG_DARK_GHOST_BLOCK = createBlockTag("dark_ghost_block");

    public static final TagKey<Block> BLOCKTAG_SHADED_REINFORCED_LIGHT_BLOCK = createBlockTag("shaded_reinforced_light_block");
    public static final TagKey<Block> BLOCKTAG_SHADED_REINFORCED_DARK_BLOCK = createBlockTag("shaded_reinforced_dark_block");
    public static final TagKey<Block> BLOCKTAG_SHADED_REINFORCED_GHOST_BLOCK = createBlockTag("shaded_reinforced_ghost_block");

    public static final TagKey<Block> BLOCKTAG_SHADED_LIGHT_GHOST_BLOCK = createBlockTag("shaded_light_ghost_block");
    public static final TagKey<Block> BLOCKTAG_SHADED_DARK_GHOST_BLOCK = createBlockTag("shaded_dark_ghost_block");

    public static final TagKey<Block> BLOCKTAG_REINFORCED_LIGHT_GHOST_BLOCK = createBlockTag("reinforced_light_ghost_block");
    public static final TagKey<Block> BLOCKTAG_REINFORCED_DARK_GHOST_BLOCK = createBlockTag("reinforced_dark_ghost_block");

    public static final TagKey<Block> BLOCKTAG_SHADED_REINFORCED_LIGHT_GHOST_BLOCK = createBlockTag("shaded_reinforced_light_ghost_block");
    public static final TagKey<Block> BLOCKTAG_SHADED_REINFORCED_DARK_GHOST_BLOCK = createBlockTag("shaded_reinforced_dark_ghost_block");


    public static final TagKey<Item> ITEMTAG_BLOCKS = createItemTag("blocks");
    public static final TagKey<Item> ITEMTAG_GHOST = createItemTag("ghost");
    public static final TagKey<Item> ITEMTAG_LIGHT = createItemTag("light");
    public static final TagKey<Item> ITEMTAG_DARK = createItemTag("dark");
    public static final TagKey<Item> ITEMTAG_REINFORCED = createItemTag("reinforced");
    public static final TagKey<Item> ITEMTAG_SHADED = createItemTag("bordered");

    public static final TagKey<Item> ITEMTAG_BLOCK = createItemTag("block");
    public static final TagKey<Item> ITEMTAG_SHADED_BLOCK = createItemTag("shaded_block");
    public static final TagKey<Item> ITEMTAG_REINFORCED_BLOCK = createItemTag("reinforced_block");
    public static final TagKey<Item> ITEMTAG_LIGHT_BLOCK = createItemTag("light_block");
    public static final TagKey<Item> ITEMTAG_DARK_BLOCK = createItemTag("dark_block");
    public static final TagKey<Item> ITEMTAG_GHOST_BLOCK = createItemTag("ghost_block");

    public static final TagKey<Item> ITEMTAG_SHADED_REINFORCED_BLOCK = createItemTag("shaded_reinforced_block");
    public static final TagKey<Item> ITEMTAG_SHADED_LIGHT_BLOCK = createItemTag("shaded_light_block");
    public static final TagKey<Item> ITEMTAG_SHADED_DARK_BLOCK = createItemTag("shaded_dark_block");
    public static final TagKey<Item> ITEMTAG_SHADED_GHOST_BLOCK = createItemTag("shaded_ghost_block");

    public static final TagKey<Item> ITEMTAG_REINFORCED_LIGHT_BLOCK = createItemTag("reinforced_light_block");
    public static final TagKey<Item> ITEMTAG_REINFORCED_DARK_BLOCK = createItemTag("reinforced_dark_block");
    public static final TagKey<Item> ITEMTAG_REINFORCED_GHOST_BLOCK = createItemTag("reinforced_ghost_block");

    public static final TagKey<Item> ITEMTAG_LIGHT_GHOST_BLOCK = createItemTag("light_ghost_block");
    public static final TagKey<Item> ITEMTAG_DARK_GHOST_BLOCK = createItemTag("dark_ghost_block");

    public static final TagKey<Item> ITEMTAG_SHADED_REINFORCED_LIGHT_BLOCK = createItemTag("shaded_reinforced_light_block");
    public static final TagKey<Item> ITEMTAG_SHADED_REINFORCED_DARK_BLOCK = createItemTag("shaded_reinforced_dark_block");
    public static final TagKey<Item> ITEMTAG_SHADED_REINFORCED_GHOST_BLOCK = createItemTag("shaded_reinforced_ghost_block");

    public static final TagKey<Item> ITEMTAG_SHADED_LIGHT_GHOST_BLOCK = createItemTag("shaded_light_ghost_block");
    public static final TagKey<Item> ITEMTAG_SHADED_DARK_GHOST_BLOCK = createItemTag("shaded_dark_ghost_block");

    public static final TagKey<Item> ITEMTAG_REINFORCED_LIGHT_GHOST_BLOCK = createItemTag("reinforced_light_ghost_block");
    public static final TagKey<Item> ITEMTAG_REINFORCED_DARK_GHOST_BLOCK = createItemTag("reinforced_dark_ghost_block");

    public static final TagKey<Item> ITEMTAG_SHADED_REINFORCED_LIGHT_GHOST_BLOCK = createItemTag("shaded_reinforced_light_ghost_block");
    public static final TagKey<Item> ITEMTAG_SHADED_REINFORCED_DARK_GHOST_BLOCK = createItemTag("shaded_reinforced_dark_ghost_block");

}
