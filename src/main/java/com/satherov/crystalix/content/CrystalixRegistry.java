package com.satherov.crystalix.content;

import net.neoforged.neoforge.common.Tags;
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
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import com.mojang.serialization.Codec;
import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.block.CrystalixGlass;
import com.satherov.crystalix.content.item.CrystalixWand;
import com.satherov.crystalix.content.properties.BlockProperties;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CrystalixRegistry {
    
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Crystalix.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Crystalix.MOD_ID);
    public static final TagKey<Item> ITEM_TAG = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "blocks"));
    public static final TagKey<Block> BLOCK_TAG = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "blocks"));
    public static final Map<BlockTypes, TagKey<Block>> BLOCK_TAGS = Arrays.stream(BlockTypes.values())
            .collect(Collectors.toMap(
                    Function.identity(),
                    type -> BlockTags.create(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, type.getSerializedName())),
                    (a, b) -> b,
                    () -> new EnumMap<>(BlockTypes.class)
            ));
    public static final Map<BlockTypes, TagKey<Item>> ITEM_TAGS = Arrays.stream(BlockTypes.values())
            .collect(Collectors.toMap(
                    Function.identity(),
                    type -> ItemTags.create(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, type.getSerializedName())),
                    (a, b) -> b,
                    () -> new EnumMap<>(BlockTypes.class)
            ));
    public static final Supplier<DataComponentType<Boolean>> INVISIBLE = DATA_COMPONENT_TYPES.register("invisible", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    public static final Supplier<DataComponentType<Boolean>> SHADELESS = DATA_COMPONENT_TYPES.register("shadeless", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    public static final Supplier<DataComponentType<Boolean>> REINFORCED = DATA_COMPONENT_TYPES.register("reinforced", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    
    public static final Supplier<DataComponentType<Boolean>> WATERLOGGABLE = DATA_COMPONENT_TYPES.register("waterloggable", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    public static final Supplier<DataComponentType<BlockProperties.Ghost>> GHOST = DATA_COMPONENT_TYPES.register("ghost", () ->
            DataComponentType.<BlockProperties.Ghost>builder()
                    .persistent(BlockProperties.Ghost.CODEC)
                    .networkSynchronized(BlockProperties.Ghost.STREAM_CODEC)
                    .build()
    );
    public static final Supplier<DataComponentType<BlockProperties.Light>> LIGHT = DATA_COMPONENT_TYPES.register("light", () ->
            DataComponentType.<BlockProperties.Light>builder()
                    .persistent(BlockProperties.Light.CODEC)
                    .networkSynchronized(BlockProperties.Light.STREAM_CODEC)
                    .build()
    );
    public static DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(Crystalix.MOD_ID);
    public static DeferredRegister.Items ITEMS = DeferredRegister.createItems(Crystalix.MOD_ID);
    public static final DeferredHolder<Item, CrystalixWand> WAND = ITEMS.register("crystalix_wand", () -> new CrystalixWand(new Item.Properties()));
    public static final Map<Colors, Map<BlockTypes, DeferredHolder<Block, CrystalixGlass>>> BLOCKS_MAP =
            Arrays.stream(Colors.values()).collect(Collectors.toMap(
                    Function.identity(),
                    color -> Arrays.stream(BlockTypes.values()).collect(Collectors.toMap(
                            Function.identity(),
                            type -> register(color.format(type), () -> new CrystalixGlass(color)),
                            (a, b) -> b,
                            () -> new EnumMap<>(BlockTypes.class)
                    )),
                    (a, b) -> b,
                    () -> new EnumMap<>(Colors.class)
            ));
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("creative_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable(String.format("itemGroup.%s", Crystalix.MOD_ID)))
            .icon(() -> CrystalixRegistry.BLOCKS_MAP.get(Colors.WHITE)
                    .get(BlockTypes.GLASS)
                    .get()
                    .asItem()
                    .getDefaultInstance())
            .displayItems((parameters, output) ->
                                  ITEMS.getEntries().stream()
                                          .map(Supplier::get)
                                          .map(Item::getDefaultInstance)
                                          .forEach(output::accept)
            ).build()
    );
    
    private static DeferredHolder<Block, CrystalixGlass> register(String name, Supplier<CrystalixGlass> properties) {
        DeferredHolder<Block, CrystalixGlass> block;
        block = CrystalixRegistry.BLOCKS.register(name, properties);
        CrystalixRegistry.ITEMS.registerSimpleBlockItem(name, block);
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
    
    public enum Colors implements StringRepresentable, CrystalixColor {
        CLEAR(-1, MapColor.NONE, Tags.Items.NUGGETS_IRON),
        WHITE(16383998, MapColor.SNOW),
        ORANGE(16351261, MapColor.COLOR_ORANGE),
        MAGENTA(13061821, MapColor.COLOR_MAGENTA),
        LIGHT_BLUE(3847130, MapColor.COLOR_LIGHT_BLUE),
        YELLOW(16701501, MapColor.COLOR_YELLOW),
        LIME(8439583, MapColor.COLOR_LIGHT_GREEN),
        PINK(15961002, MapColor.COLOR_PINK),
        GRAY(4673362, MapColor.COLOR_GRAY),
        LIGHT_GRAY(10329495, MapColor.COLOR_LIGHT_GRAY),
        CYAN(1481884, MapColor.COLOR_CYAN),
        PURPLE(8991416, MapColor.COLOR_PURPLE),
        BLUE(3949738, MapColor.COLOR_BLUE),
        BROWN(8606770, MapColor.COLOR_BROWN),
        GREEN(6192150, MapColor.COLOR_GREEN),
        RED(11546150, MapColor.COLOR_RED),
        BLACK(1908001, MapColor.COLOR_BLACK);
        
        private static final String BASE = "crystalix_glass";
        private final int color;
        private final MapColor map;
        private final TagKey<Item> tag;
        
        Colors(int color, MapColor map, TagKey<Item> tag) {
            this.color = color;
            this.map = map;
            this.tag = tag;
        }
        
        Colors(int color, MapColor map) {
            this.color = color;
            this.map = map;
            this.tag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "dyes/" + this.name().toLowerCase(Locale.ROOT)));
        }
        
        public int getColor() {
            return color;
        }
        
        public MapColor getMapColor() {
            return map;
        }
        
        public TagKey<Item> getTag() {
            return tag;
        }
        
        public String format(BlockTypes type) {
            return switch (type) {
                case GLASS -> this.equals(CLEAR) ? BASE : String.format("%s_%s", getSerializedName(), BASE);
                case CLEAR -> this.equals(CLEAR) ? String.format("clear_%s", BASE) : String.format("%s_clear_%s", getSerializedName(), BASE);
                case BORDERED -> this.equals(CLEAR) ? String.format("bordered_%s", BASE) : String.format("%s_bordered_%s", getSerializedName(), BASE);
            };
        }
        
        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
    
    public interface CrystalixColor {
        
        int getColor();
        
        MapColor getMapColor();
    }
}
