package dev.satherov.crystalix.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.client.lang.CSLanguage;
import dev.satherov.crystalix.client.lang.CSTranslatable;
import dev.satherov.crystalix.common.block.CrystalixGlass;
import dev.satherov.crystalix.common.block.CrystalixGlassTile;
import dev.satherov.crystalix.common.item.CrystalixWand;
import dev.satherov.crystalix.common.properties.CSProperties;
import dev.satherov.crystalix.common.properties.EnumCodecs;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.serialization.Codec;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public final class CSRegistry {
    
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Crystalix.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Crystalix.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Crystalix.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Crystalix.MOD_ID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Crystalix.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Crystalix.MOD_ID);
    
    public static final Map<Types, DeferredHolder<Block, CrystalixGlass>> ENTRIES = CSRegistry.create();
    
    @Deprecated(forRemoval = true)
    public static final Table<Types, Colors, DeferredHolder<Block, CrystalixGlass>> OLD_ENTRIES = CSRegistry.old();
    
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CSRegistry.TABS.register("creative_tab", () -> CreativeModeTab.builder()
            .title(CSLanguage.CREATIVE_TAB_DEFAULT.text())
            .icon(() -> Objects.requireNonNull(CSRegistry.ENTRIES.get(Types.GLASS)).get().asItem().getDefaultInstance())
            .displayItems((param, out) -> CSRegistry.ITEMS.getEntries().stream().map(DeferredHolder::get).map(Item::getDefaultInstance).forEach(out::accept))
            .build()
    );
    public static final Supplier<DataComponentType<Boolean>> WATERLOGGABLE = CSRegistry.COMPONENTS.register("waterloggable", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    public static final Supplier<DataComponentType<Boolean>> INVISIBLE = CSRegistry.COMPONENTS.register("invisible", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    public static final Supplier<DataComponentType<Boolean>> SHADELESS = CSRegistry.COMPONENTS.register("shadeless", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    public static final Supplier<DataComponentType<Boolean>> REINFORCED = CSRegistry.COMPONENTS.register("reinforced", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    public static final Supplier<DataComponentType<Boolean>> TRANSPARENT = CSRegistry.COMPONENTS.register("transparent", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    public static final Supplier<DataComponentType<Boolean>> REDSTONE = CSRegistry.COMPONENTS.register("redstone", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    public static final Supplier<DataComponentType<Boolean>> CONDUCTOR = CSRegistry.COMPONENTS.register("conductor", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    public static final Supplier<DataComponentType<CSProperties.Ghost>> GHOST = CSRegistry.COMPONENTS.register("ghost", () ->
            DataComponentType.<CSProperties.Ghost>builder()
                    .persistent(CSProperties.Ghost.CODEC)
                    .networkSynchronized(CSProperties.Ghost.STREAM_CODEC)
                    .build()
    );
    public static final Supplier<DataComponentType<CSProperties.Light>> LIGHT = CSRegistry.COMPONENTS.register("light", () ->
            DataComponentType.<CSProperties.Light>builder()
                    .persistent(CSProperties.Light.CODEC)
                    .networkSynchronized(CSProperties.Light.STREAM_CODEC)
                    .build()
    );
    public static final Supplier<DataComponentType<Integer>> COLOR = CSRegistry.COMPONENTS.register("color", () ->
            DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build()
    );
    public static final Supplier<DataComponentType<Boolean>> APPLY_COLORLESS = CSRegistry.COMPONENTS.register("apply_colorless", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    public static final Supplier<AttachmentType<Boolean>> MIGRATED = CSRegistry.ATTACHMENTS.register("migrated", () ->
            AttachmentType.builder(() -> Boolean.FALSE)
                    .serialize(Codec.BOOL)
                    .sync(ByteBufCodecs.BOOL)
                    .build()
    );
    public static final TagKey<Item> ITEM_TAG = TagKey.create(Registries.ITEM, Crystalix.rl("blocks"));
    public static final Map<Types, TagKey<Item>> ITEM_TAGS = Arrays.stream(Types.values())
            .collect(Collectors.toMap(
                    Function.identity(),
                    type -> ItemTags.create(Crystalix.rl(type.getSerializedName())),
                    (a, b) -> b,
                    () -> new EnumMap<>(Types.class)
            ));
    public static final TagKey<Block> BLOCK_TAG = TagKey.create(Registries.BLOCK, Crystalix.rl("blocks"));
    public static final Map<Types, TagKey<Block>> BLOCK_TAGS = Arrays.stream(Types.values())
            .collect(Collectors.toMap(
                    Function.identity(),
                    type -> BlockTags.create(Crystalix.rl(type.getSerializedName())),
                    (a, b) -> b,
                    () -> new EnumMap<>(Types.class)
            ));
    
    public static final DeferredHolder<Item, CrystalixWand> WAND = CSRegistry.ITEMS.register("crystalix_wand", () -> new CrystalixWand(new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrystalixGlassTile>> GLASS_TILE = CSRegistry.BLOCK_ENTITIES.register("glass_tile", () ->
            BlockEntityType.Builder.of(CrystalixGlassTile::new,
                    Stream.concat(CSRegistry.ENTRIES.values().stream(), CSRegistry.OLD_ENTRIES.cellSet().stream().map(Table.Cell::getValue))
                            .filter(Objects::nonNull)
                            .map(DeferredHolder::get)
                            .toArray(Block[]::new)
            ).build(null));
    
    private static Map<Types, DeferredHolder<Block, CrystalixGlass>> create() {
        Map<Types, DeferredHolder<Block, CrystalixGlass>> map = new HashMap<>();
        for (Types type : Types.values()) {
            String name = type.format();
            DeferredHolder<Block, CrystalixGlass> holder = CSRegistry.BLOCKS.register(name, () -> new CrystalixGlass(type, null));
            CSRegistry.ITEMS.register(name, () -> new BlockItem(holder.get(), new Item.Properties()));
            map.put(type, holder);
        }
        return map;
    }
    
    @Deprecated(forRemoval = true)
    private static Table<Types, Colors, DeferredHolder<Block, CrystalixGlass>> old() {
        Table<Types, Colors, DeferredHolder<Block, CrystalixGlass>> table = HashBasedTable.create();
        for (Colors color : Colors.values()) {
            for (Types type : Types.values()) {
                String name = color.format(type);
                if (name.equalsIgnoreCase(type.format())) continue;
                DeferredHolder<Block, CrystalixGlass> holder = CSRegistry.BLOCKS.register(name, () -> new CrystalixGlass(type, color));
                table.put(type, color, holder);
            }
        }
        return table;
    }
    
    public static void register(IEventBus bus) {
        CSRegistry.BLOCKS.register(bus);
        CSRegistry.ITEMS.register(bus);
        CSRegistry.TABS.register(bus);
        CSRegistry.COMPONENTS.register(bus);
        CSRegistry.ATTACHMENTS.register(bus);
        CSRegistry.BLOCK_ENTITIES.register(bus);
    }
    
    @RequiredArgsConstructor
    @Accessors(fluent = true)
    public enum Types implements StringRepresentable {
        GLASS(Tags.Items.GEMS_AMETHYST),
        CLEAR(Tags.Items.GEMS_QUARTZ),
        BORDERED(Tags.Items.INGOTS_IRON);
        
        private final @Getter TagKey<Item> tag;
        
        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
        
        public String format() {
            return (this.equals(Types.GLASS) ? "" : this.getSerializedName() + "_") + "crystalix_glass";
        }
    }
    
    @RequiredArgsConstructor
    @Accessors(fluent = true)
    public enum Colors implements StringRepresentable, CSTranslatable {
        CLEAR(-1, MapColor.NONE),
        
        // Whites & Creams
        WHITE(0xF9FFFE, MapColor.SNOW),
        CREAM(0xFFFDD0, MapColor.TERRACOTTA_WHITE),
        
        // Yellows & Golds
        FLUORESCENT(0xEED3A6, MapColor.SAND),
        YELLOW(0xFED83D, MapColor.COLOR_YELLOW),
        GOLD(0xFFD700, MapColor.GOLD),
        HONEY(0xFFB546, MapColor.TERRACOTTA_YELLOW),
        
        // Oranges
        PEACH(0xBF8963, MapColor.SAND),
        AMBER(0xD37D39, MapColor.TERRACOTTA_ORANGE),
        ORANGE(0xF9801D, MapColor.COLOR_ORANGE),
        PERSIMMON(0xD23F29, MapColor.TERRACOTTA_ORANGE),
        
        // Reds & Pinks
        CORAL(0xFF6B6B, MapColor.TERRACOTTA_RED),
        SALMON(0xFA8072, MapColor.TERRACOTTA_PINK),
        ROSE(0xFF0AD5, MapColor.TERRACOTTA_RED),
        RED(0xB02E26, MapColor.COLOR_RED),
        CRIMSON(0xDC143C, MapColor.CRIMSON_STEM),
        SCARLET(0xFF2400, MapColor.COLOR_RED),
        MAROON(0x910000, MapColor.COLOR_RED),
        BUBBLEGUM(0xF7B6D6, MapColor.COLOR_PINK),
        PINK(0xF38BAA, MapColor.COLOR_PINK),
        
        // Purples & Magentas
        MAGENTA(0xC74EBD, MapColor.COLOR_MAGENTA),
        FUCHSIA(0xFF00FF, MapColor.COLOR_MAGENTA),
        LAVENDER(0xDD7DFF, MapColor.COLOR_PURPLE),
        PURPLE(0x8932B8, MapColor.COLOR_PURPLE),
        INDIGO(0x4B0082, MapColor.TERRACOTTA_PURPLE),
        WINE(0x722563, MapColor.COLOR_PURPLE),
        
        // Blues
        LIGHT_BLUE(0x3AB3DA, MapColor.COLOR_LIGHT_BLUE),
        ICY_BLUE(0x9EC3DF, MapColor.TERRACOTTA_BLUE),
        SKY_BLUE(0x87CEEB, MapColor.COLOR_LIGHT_BLUE),
        CHERENKOV(0x01988F, MapColor.COLOR_LIGHT_BLUE),
        CYAN(0x169C9C, MapColor.COLOR_CYAN),
        ULTRAMARINE(0x56507F, MapColor.COLOR_BLUE),
        BLUE(0x3C44AA, MapColor.COLOR_BLUE),
        COBALT(0x0047AB, MapColor.COLOR_BLUE),
        NAVY(0x1C2951, MapColor.TERRACOTTA_BLUE),
        
        // Cyans & Teals
        AQUAMARINE(0x2C7F7F, MapColor.TERRACOTTA_CYAN),
        TEAL(0x008080, MapColor.DIAMOND),
        TURQUOISE(0x40E0D0, MapColor.WARPED_STEM),
        
        // Greens
        MINT(0x89D898, MapColor.COLOR_GREEN),
        LIME(0x80C71F, MapColor.COLOR_LIGHT_GREEN),
        SPRING_GREEN(0xC3E789, MapColor.TERRACOTTA_LIGHT_GREEN),
        CHARTREUSE(0x7FFF00, MapColor.COLOR_LIGHT_GREEN),
        CONIFER(0xBAB735, MapColor.TERRACOTTA_LIGHT_GREEN),
        GREEN(0x5E7C16, MapColor.COLOR_GREEN),
        EMERALD(0x50C878, MapColor.COLOR_GREEN),
        FOREST_GREEN(0x228B22, MapColor.TERRACOTTA_GREEN),
        OLIVE(0x556B2F, MapColor.TERRACOTTA_GREEN),
        
        // Grays & Neutrals
        LIGHT_GRAY(0x9D9D97, MapColor.COLOR_LIGHT_GRAY),
        SILVER(0xC0C0C0, MapColor.COLOR_LIGHT_GRAY),
        GRAY(0x474F52, MapColor.COLOR_GRAY),
        
        // Browns & Earth Tones
        BEIGE(0xF5F5DC, MapColor.SAND),
        TAN(0xD2B48C, MapColor.TERRACOTTA_BROWN),
        BROWN(0x835432, MapColor.COLOR_BROWN),
        
        // Darkest
        BLACK(0x1D1D21, MapColor.COLOR_BLACK);
        
        public static final Codec<Colors> CODEC = EnumCodecs.makeCodec(Colors.class);
        public static final StreamCodec<ByteBuf, Colors> STREAM_CODEC = EnumCodecs.makeStreamCodec(Colors.class);
        private static final String BASE = "crystalix_glass";
        private final @Getter int color;
        private final @Getter MapColor map;
        
        public static @Nullable Colors fromState(BlockState state) {
            String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
            
            if (!path.endsWith(Colors.BASE)) return null;
            path = path.substring(0, path.length() - Colors.BASE.length());
            
            if (path.endsWith("clear_")) {
                path = path.substring(0, path.length() - "clear_".length());
            } else if (path.endsWith("bordered_")) {
                path = path.substring(0, path.length() - "bordered_".length());
            }
            if (path.endsWith("_")) {
                path = path.substring(0, path.length() - "_".length());
            }
            
            if (path.isEmpty()) return Colors.CLEAR;
            
            for (Colors color : Colors.values()) {
                if (color.getSerializedName().equals(path)) {
                    return color;
                }
            }
            
            CSRegistry.log.warn("Unknown color: {}", path);
            return null;
        }
        
        public @NotNull String format(Types type) {
            String prefix = this.equals(Colors.CLEAR) ? "" : this.getSerializedName() + "_";
            String suffix = switch (type) {
                case GLASS -> "";
                case CLEAR -> "clear_";
                case BORDERED -> "bordered_";
            };
            return prefix + suffix + Colors.BASE;
        }
        
        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
        
        @Override
        public MutableComponent text() {
            return Component.literal(this.translation()).withStyle(style -> style.withColor(this.color()));
        }
        
        @Override
        public MutableComponent text(Object... args) {
            return Component.literal(this.translation()).withStyle(style -> style.withColor(this.color()));
        }
        
        @Override
        public String key() {
            return this.name().toLowerCase(Locale.ROOT);
        }
        
        @Override
        public String translation() {
            return String.format("#%06X", this.color() & 0xFFFFFF);
        }
    }
}
