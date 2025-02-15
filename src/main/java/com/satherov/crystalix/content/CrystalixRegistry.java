package com.satherov.crystalix.content;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.block.CrystalixGlass;
import com.satherov.crystalix.content.item.CrystalixWand;
import com.satherov.crystalix.content.properties.BlockProperties;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import com.mojang.serialization.Codec;

public class CrystalixRegistry {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Crystalix.MOD_ID);
    public static DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(Crystalix.MOD_ID);
    public static DeferredRegister.Items ITEMS = DeferredRegister.createItems(Crystalix.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Crystalix.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("creative_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable(String.format("itemGroup.%s", Crystalix.MOD_ID)))
            .icon(() -> CrystalixRegistry.BLOCKS_MAP.get(DyeColor.WHITE).get("glass").get().asItem().getDefaultInstance())
            .displayItems((parameters, output) ->
                    ITEMS.getEntries().stream()
                            .map(Supplier::get)
                            .map(Item::getDefaultInstance)
                            .forEach(output::accept)
            ).build()
    );

    public static final TagKey<Block> BLOCKTAG_BLOCKS = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "blocks"));
    public static final TagKey<Block> BLOCKTAG_GLASS = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "glass"));
    public static final TagKey<Block> BLOCKTAG_CLEAR = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "clear"));
    public static final TagKey<Block> BLOCKTAG_BORDERED = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "bordered"));

    public static final TagKey<Item> ITEMTAG_BLOCKS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "blocks"));
    public static final TagKey<Item> ITEMTAG_GLASS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "glass"));
    public static final TagKey<Item> ITEMTAG_CLEAR = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "clear"));
    public static final TagKey<Item> ITEMTAG_BORDERED = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "bordered"));


    public static final DeferredHolder<Item, CrystalixWand> WAND = ITEMS.register("crystalix_wand", () -> new CrystalixWand(new Item.Properties(), false, false, BlockProperties.Light.NONE, BlockProperties.Ghost.BLOCK_ALL));

    public static final Map<DyeColor, Map<String, DeferredHolder<Block, CrystalixGlass>>> BLOCKS_MAP = Arrays.stream(DyeColor.values())
            .collect(Collectors.toMap(
                    color -> color,
                    color -> Map.of(
                            "glass", register(String.format("%s_crystalix_glass", color.getName()), () -> new CrystalixGlass(color)),
                            "clear", register(String.format("%s_clear_crystalix_glass", color.getName()), () -> new CrystalixGlass(color)),
                            "bordered", register(String.format("%s_bordered_crystalix_glass", color.getName()), () -> new CrystalixGlass(color)))
            ));

    private static DeferredHolder<Block, CrystalixGlass> register(String name, Supplier<CrystalixGlass> properties) {
        DeferredHolder<Block, CrystalixGlass> block;
        block = CrystalixRegistry.BLOCKS.register(name, properties);
        CrystalixRegistry.ITEMS.registerSimpleBlockItem(name, block);
        return block;
    }

    public static final Supplier<DataComponentType<Boolean>> SHADELESS = DATA_COMPONENT_TYPES.register("shadeless", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    public static final Supplier<DataComponentType<Boolean>> REINFORCED = DATA_COMPONENT_TYPES.register("reinforced", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    public static final Supplier<DataComponentType<BlockProperties.Ghost>> GHOST = DATA_COMPONENT_TYPES.register("ghost", () ->
            DataComponentType.<BlockProperties.Ghost>builder()
                    .persistent(BlockProperties.Ghost.CODEC)
                    .networkSynchronized(BlockProperties.Ghost.STREAM_CODEC)
                    .build());

    public static final Supplier<DataComponentType<BlockProperties.Light>> LIGHT = DATA_COMPONENT_TYPES.register("light", () ->
            DataComponentType.<BlockProperties.Light>builder()
                    .persistent(BlockProperties.Light.CODEC)
                    .networkSynchronized(BlockProperties.Light.STREAM_CODEC)
                    .build());
}
