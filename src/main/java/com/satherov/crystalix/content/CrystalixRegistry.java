package com.satherov.crystalix.content;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.block.CrystalixGlass;
import com.satherov.crystalix.content.item.CrystalixWand;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CrystalixRegistry {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Crystalix.MOD_ID);
    public static final TagKey<Item> ITEM_TAG = ItemTags.create(new ResourceLocation(Crystalix.MOD_ID, "blocks"));
    public static final TagKey<Block> BLOCK_TAG = BlockTags.create(new ResourceLocation(Crystalix.MOD_ID, "blocks"));
    public static final Map<BlockTypes, TagKey<Block>> BLOCK_TAGS = Arrays.stream(BlockTypes.values())
            .collect(Collectors.toMap(
                    Function.identity(),
                    type -> BlockTags.create(new ResourceLocation(Crystalix.MOD_ID, type.getSerializedName())),
                    (a, b) -> b,
                    () -> new EnumMap<>(BlockTypes.class)
            ));
    public static final Map<BlockTypes, TagKey<Item>> ITEM_TAGS = Arrays.stream(BlockTypes.values())
            .collect(Collectors.toMap(
                    Function.identity(),
                    type -> ItemTags.create(new ResourceLocation(Crystalix.MOD_ID, type.getSerializedName())),
                    (a, b) -> b,
                    () -> new EnumMap<>(BlockTypes.class)
            ));

    public static final String SHADELESS = "shadeless";
    public static final String REINFORCED = "reinforced";
    public static final String LIGHT = "light";
    public static final String GHOST = "ghost";

    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Crystalix.MOD_ID);
    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Crystalix.MOD_ID);
    public static final RegistryObject<CrystalixWand> WAND = ITEMS.register("crystalix_wand", () -> new CrystalixWand(new Item.Properties()));

    public static final Map<DyeColor, Map<BlockTypes, RegistryObject<CrystalixGlass>>> BLOCKS_MAP =
            Arrays.stream(DyeColor.values()).collect(Collectors.toMap(
                    Function.identity(),
                    color -> Arrays.stream(BlockTypes.values()).collect(Collectors.toMap(
                            Function.identity(),
                            type -> {
                                String name = type == BlockTypes.GLASS
                                        ? String.format("%s_crystalix_glass", color.getName())
                                        : String.format("%s_%s_crystalix_glass", color.getName(), type.getSerializedName());
                                return register(name, () -> new CrystalixGlass(color));
                            },
                            (a, b) -> b,
                            () -> new EnumMap<>(BlockTypes.class)
                    )),
                    (a, b) -> b,
                    () -> new EnumMap<>(DyeColor.class)
            ));

    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("creative_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable(String.format("itemGroup.%s", Crystalix.MOD_ID)))
            .icon(() -> CrystalixRegistry.BLOCKS_MAP.get(DyeColor.WHITE).get(BlockTypes.GLASS).get().asItem().getDefaultInstance())
            .displayItems((parameters, output) ->
                    ITEMS.getEntries().stream()
                            .map(Supplier::get)
                            .map(Item::getDefaultInstance)
                            .forEach(output::accept)
            ).build()
    );

    private static RegistryObject<CrystalixGlass> register(String name, Supplier<CrystalixGlass> properties) {
        RegistryObject<CrystalixGlass> block;
        block = CrystalixRegistry.BLOCKS.register(name, properties);
        CrystalixRegistry.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    public enum BlockTypes implements StringRepresentable {
        GLASS(Tags.Items.GEMS_AMETHYST),
        CLEAR(Tags.Items.GEMS_QUARTZ),
        BORDERED(Tags.Items.INGOTS_IRON);

        private final TagKey<Item> tag;

        BlockTypes(TagKey<Item> tag) {
            this.tag = tag;
        }

        public TagKey<Item> getTag() {
            return tag;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

}
