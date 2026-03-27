package dev.satherov.crystalix.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.client.lang.CSLanguage;
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
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public final class CSRegistry {
    
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Crystalix.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Crystalix.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Crystalix.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Crystalix.MOD_ID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Crystalix.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Crystalix.MOD_ID);
    
    public static final Map<Types, DeferredHolder<Block, CrystalixGlass>> ENTRIES = CSRegistry.create();
    
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
            BlockEntityType.Builder.of(CrystalixGlassTile::new, CSRegistry.ENTRIES.values().stream()
                    .filter(Objects::nonNull)
                    .map(DeferredHolder::get)
                    .toArray(Block[]::new)
            ).build(null));
    
    private static Map<Types, DeferredHolder<Block, CrystalixGlass>> create() {
        Map<Types, DeferredHolder<Block, CrystalixGlass>> map = new HashMap<>();
        for (Types type : Types.values()) {
            String name = type.format();
            DeferredHolder<Block, CrystalixGlass> holder = CSRegistry.BLOCKS.register(name, () -> new CrystalixGlass(type));
            CSRegistry.ITEMS.register(name, () -> new BlockItem(holder.get(), new Item.Properties()));
            map.put(type, holder);
        }
        return map;
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
        
        public static final Codec<Types> CODEC = EnumCodecs.makeCodec(Types.class);
        
        private final @Getter TagKey<Item> tag;
        
        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
        
        public String format() {
            return (this.equals(Types.GLASS) ? "" : this.getSerializedName() + "_") + "crystalix_glass";
        }
    }
}
