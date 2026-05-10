package dev.satherov.crystalix.core.registry;

import lombok.experimental.UtilityClass;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.client.lang.CXLanguage;
import dev.satherov.crystalix.common.block.CrystalixGlassBlock;
import dev.satherov.crystalix.common.block.CrystalixGlassBlockEntity;
import dev.satherov.crystalix.common.item.CrystalixWandItem;
import dev.satherov.crystalix.common.properties.GhostState;
import dev.satherov.crystalix.common.properties.GlassMaterial;
import dev.satherov.crystalix.common.properties.LightState;
import dev.satherov.sathlib.common.block.SLBlockProperties;
import dev.satherov.sathlib.common.item.SLItemProperties;
import dev.satherov.sathlib.common.properties.BlockItemPropertyContainer;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import com.mojang.serialization.Codec;

import java.util.function.Supplier;

@UtilityClass
public class CXRegistry {
    
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Crystalix.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Crystalix.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Crystalix.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Crystalix.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Crystalix.MOD_ID);
    
    public static final DeferredHolder<Block, CrystalixGlassBlock> CRYSTALIX_BLOCK = CXRegistry.BLOCKS.register("crystalix_glass", k -> new CrystalixGlassBlock(SLBlockProperties.create(k)));
    public static final DeferredHolder<Item, BlockItem> CRYSTALIX_ITEM = CXRegistry.ITEMS.register("crystalix_glass", k -> new BlockItem(CXRegistry.CRYSTALIX_BLOCK.get(), SLItemProperties.create(k).useBlockDescriptionPrefix()));
    public static final DeferredHolder<Item, CrystalixWandItem> CRYSTALIX_WAND = CXRegistry.ITEMS.register("crystalix_wand", k -> new CrystalixWandItem(SLItemProperties.create(k)));
    
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrystalixGlassBlockEntity>> GLASS_BLOCK_ENTITY = CXRegistry.BLOCK_ENTITIES.register("glass_block_entity", () ->
            new BlockEntityType<>(CrystalixGlassBlockEntity::new, CXRegistry.CRYSTALIX_BLOCK.get())
    );
    
    public static final TagKey<Block> CRYSTALIX_BLOCK_TAG = BlockTags.create(Crystalix.id("blocks"));
    public static final TagKey<Item> CRYSTALIX_ITEM_TAG = ItemTags.create(Crystalix.id("blocks"));
    
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CXRegistry.TABS.register("creative_tab", () -> CreativeModeTab.builder()
            .title(CXLanguage.CREATIVE_TAB.translate())
            .icon(() -> CXRegistry.CRYSTALIX_ITEM.get().getDefaultInstance())
            .displayItems((_, out) -> CXRegistry.ITEMS.getEntries().stream().map(DeferredHolder::get).map(Item::getDefaultInstance).forEach(out::accept))
            .build()
    );
    
    public static final Supplier<DataComponentType<Boolean>> INVISIBLE = CXRegistry.COMPONENTS.register("invisible", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    public static final Supplier<DataComponentType<LightState>> LIGHT = CXRegistry.COMPONENTS.register("light", () ->
            DataComponentType.<LightState>builder()
                    .persistent(LightState.CODEC)
                    .networkSynchronized(LightState.STREAM_CODEC)
                    .build()
    );
    public static final Supplier<DataComponentType<GhostState>> GHOST = CXRegistry.COMPONENTS.register("ghost", () ->
            DataComponentType.<GhostState>builder()
                    .persistent(GhostState.CODEC)
                    .networkSynchronized(GhostState.STREAM_CODEC)
                    .build()
    );
    
    public static final Supplier<DataComponentType<Integer>> COLOR = CXRegistry.COMPONENTS.register("color", () ->
            DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build()
    );
    public static final Supplier<DataComponentType<GlassMaterial>> MATERIAL = CXRegistry.COMPONENTS.register("material", () ->
            DataComponentType.<GlassMaterial>builder()
                    .persistent(GlassMaterial.CODEC)
                    .networkSynchronized(GlassMaterial.STREAM_CODEC)
                    .build()
    );
    public static final Supplier<DataComponentType<Boolean>> SHADELESS = CXRegistry.COMPONENTS.register("shadeless", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    public static final Supplier<DataComponentType<Boolean>> TINTED = CXRegistry.COMPONENTS.register("tinted", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    
    public static final Supplier<DataComponentType<Boolean>> REINFORCED = CXRegistry.COMPONENTS.register("reinforced", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    public static final Supplier<DataComponentType<Boolean>> WATERLOGGABLE = CXRegistry.COMPONENTS.register("waterloggable", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    public static final Supplier<DataComponentType<Boolean>> CONDUCTOR = CXRegistry.COMPONENTS.register("conductor", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    public static final Supplier<DataComponentType<Integer>> REDSTONE = CXRegistry.COMPONENTS.register("redstone", () ->
            DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build()
    );
    
    public static final Supplier<DataComponentType<Boolean>> APPLY_COLORLESS = CXRegistry.COMPONENTS.register("apply_colorless", () ->
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    
    public static void register(IEventBus bus) {
        CXRegistry.BLOCKS.register(bus);
        CXRegistry.ITEMS.register(bus);
        CXRegistry.TABS.register(bus);
        CXRegistry.COMPONENTS.register(bus);
        CXRegistry.BLOCK_ENTITIES.register(bus);
    }
}
