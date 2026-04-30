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
import dev.satherov.crystalix.core.mixin.RedStoneWireBlockAccessor;
import dev.satherov.sathlib.common.block.SLBlockProperties;
import dev.satherov.sathlib.common.item.SLItemProperties;
import dev.satherov.sathlib.common.properties.SLProperty;
import dev.satherov.sathlib.common.properties.SLPropertyContainer;
import dev.satherov.sathlib.common.properties.SLPropertyValue;
import dev.satherov.sathlib.network.chat.SLComponent;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
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
    
    public static final SLProperty<Boolean, CrystalixGlassBlockEntity> INVISIBLE_PROPERTY = SLProperty.builder(Crystalix.id("invisible"), Boolean.class, CrystalixGlassBlockEntity.class)
            .name(CXLanguage.PROPERTY_INVISIBLE)
            .tooltipDisplayer(SLProperty.boolDisplayer(CXLanguage.TOOLTIP_INVISIBLE_ON, CXLanguage.TOOLTIP_INVISIBLE_OFF))
            .cycler(SLProperty.BOOLEAN_CYCLER)
            .stack(stack -> SLPropertyValue.of(stack.getOrDefault(CXRegistry.INVISIBLE, false)), (stack, val) -> stack.set(CXRegistry.INVISIBLE, val))
            .stateResolved(state -> SLPropertyValue.of(state.getValue(CrystalixGlassBlock.INVISIBLE)), (state, val) -> state.setValue(CrystalixGlassBlock.INVISIBLE, val))
            .build();
    
    public static final SLProperty<LightState, CrystalixGlassBlockEntity> LIGHT_PROPERTY = SLProperty.builder(Crystalix.id("light"), LightState.class, CrystalixGlassBlockEntity.class)
            .name(CXLanguage.PROPERTY_LIGHT)
            .tooltipDisplayer(value -> SLComponent.of(value.tooltip().copy()))
            .cycler(SLProperty.enumCycler(LightState.class))
            .stack(stack -> SLPropertyValue.of(stack.getOrDefault(CXRegistry.LIGHT, LightState.NONE)), (stack, val) -> stack.set(CXRegistry.LIGHT, val))
            .stateResolved(state -> SLPropertyValue.of(state.getValue(CrystalixGlassBlock.LIGHT)), (state, val) -> state.setValue(CrystalixGlassBlock.LIGHT, val))
            .build();
    
    public static final SLProperty<GhostState, CrystalixGlassBlockEntity> GHOST_PROPERTY = SLProperty.builder(Crystalix.id("ghost"), GhostState.class, CrystalixGlassBlockEntity.class)
            .name(CXLanguage.PROPERTY_GHOST)
            .tooltipDisplayer(value -> SLComponent.of(value.tooltip().copy()))
            .cycler(SLProperty.enumCycler(GhostState.class))
            .stack(stack -> SLPropertyValue.of(stack.getOrDefault(CXRegistry.GHOST, GhostState.ALLOW_ALL)), (stack, val) -> stack.set(CXRegistry.GHOST, val))
            .stateResolved(state -> SLPropertyValue.of(state.getValue(CrystalixGlassBlock.GHOST)), (state, val) -> state.setValue(CrystalixGlassBlock.GHOST, val))
            .build();
    
    public static final SLProperty<Integer, CrystalixGlassBlockEntity> COLOR_PROPERTY = SLProperty.builder(Crystalix.id("color"), Integer.class, CrystalixGlassBlockEntity.class)
            .name(CXLanguage.PROPERTY_COLOR)
            .tooltipDisplayer(SLProperty.directDisplayer(CXLanguage.TOOLTIP_COLOR.translate()))
            .valueDisplayer(val -> {
                String hex = String.format("#%06X", (0xFFFFFF & val));
                return SLComponent.of(Component.literal(hex).withColor(val));
            })
            .cycler(SLProperty.numberCycler(0x00000, 0xFFFFFF))
            .stack(stack -> SLPropertyValue.of(stack.getOrDefault(CXRegistry.COLOR, 0xFFFFFF)), (stack, val) -> stack.set(CXRegistry.COLOR, val))
            .blockEntity(entity -> SLPropertyValue.of(entity.getColor()), (entity, val) -> {
                if (val == Integer.MIN_VALUE) return;
                entity.setColor(val);
            })
            .build();
    
    public static final SLProperty<GlassMaterial, CrystalixGlassBlockEntity> MATERIAL_PROPERTY = SLProperty.builder(Crystalix.id("material"), GlassMaterial.class, CrystalixGlassBlockEntity.class)
            .name(CXLanguage.PROPERTY_MATERIAL)
            .tooltipDisplayer(value -> SLComponent.of(value.tooltip().copy()))
            .valueDisplayer(value -> value.display())
            .cycler(SLProperty.enumCycler(GlassMaterial.class))
            .stack(stack -> SLPropertyValue.of(stack.getOrDefault(CXRegistry.MATERIAL, GlassMaterial.defaultMaterial())), (stack, val) -> stack.set(CXRegistry.MATERIAL, val))
            .stateResolved(state -> SLPropertyValue.of(state.getValue(CrystalixGlassBlock.MATERIAL)), (state, val) -> state.setValue(CrystalixGlassBlock.MATERIAL, val))
            .build();
    
    public static final SLProperty<Boolean, CrystalixGlassBlockEntity> SHADELESS_PROPERTY = SLProperty.builder(Crystalix.id("shadeless"), Boolean.class, CrystalixGlassBlockEntity.class)
            .name(CXLanguage.PROPERTY_SHADELESS)
            .tooltipDisplayer(SLProperty.boolDisplayer(CXLanguage.TOOLTIP_SHADELESS_ON, CXLanguage.TOOLTIP_SHADELESS_OFF))
            .cycler(SLProperty.BOOLEAN_CYCLER)
            .stack(stack -> SLPropertyValue.of(stack.getOrDefault(CXRegistry.SHADELESS, false)), (stack, val) -> stack.set(CXRegistry.SHADELESS, val))
            .stateResolved(state -> SLPropertyValue.of(state.getValue(CrystalixGlassBlock.SHADELESS)), (state, val) -> state.setValue(CrystalixGlassBlock.SHADELESS, val))
            .build();
    
    public static final SLProperty<Boolean, CrystalixGlassBlockEntity> TINTED_PROPERTY = SLProperty.builder(Crystalix.id("tinted"), Boolean.class, CrystalixGlassBlockEntity.class)
            .name(CXLanguage.PROPERTY_TINTED)
            .tooltipDisplayer(SLProperty.boolDisplayer(CXLanguage.TOOLTIP_TINTED_ON, CXLanguage.TOOLTIP_TINTED_OFF))
            .cycler(SLProperty.BOOLEAN_CYCLER)
            .stack(stack -> SLPropertyValue.of(stack.getOrDefault(CXRegistry.TINTED, false)), (stack, val) -> stack.set(CXRegistry.TINTED, val))
            .stateResolved(state -> SLPropertyValue.of(state.getValue(CrystalixGlassBlock.TINTED)), (state, val) -> state.setValue(CrystalixGlassBlock.TINTED, val))
            .build();
    
    public static final SLProperty<Boolean, CrystalixGlassBlockEntity> REINFORCED_PROPERTY = SLProperty.builder(Crystalix.id("reinforced"), Boolean.class, CrystalixGlassBlockEntity.class)
            .name(CXLanguage.PROPERTY_REINFORCED)
            .tooltipDisplayer(SLProperty.boolDisplayer(CXLanguage.TOOLTIP_REINFORCED_ON, CXLanguage.TOOLTIP_REINFORCED_OFF))
            .cycler(SLProperty.BOOLEAN_CYCLER)
            .stack(stack -> SLPropertyValue.of(stack.getOrDefault(CXRegistry.REINFORCED, false)), (stack, val) -> stack.set(CXRegistry.REINFORCED, val))
            .blockEntity(entity -> SLPropertyValue.of(entity.isReinforced()), CrystalixGlassBlockEntity::setReinforced)
            .build();
    
    public static final SLProperty<Boolean, CrystalixGlassBlockEntity> WATERLOGGABLE_PROPERTY = SLProperty.builder(Crystalix.id("waterloggable"), Boolean.class, CrystalixGlassBlockEntity.class)
            .name(CXLanguage.PROPERTY_WATERLOGGABLE)
            .tooltipDisplayer(SLProperty.boolDisplayer(CXLanguage.TOOLTIP_WATERLOGGABLE_ON, CXLanguage.TOOLTIP_WATERLOGGABLE_OFF))
            .cycler(SLProperty.BOOLEAN_CYCLER)
            .stack(stack -> SLPropertyValue.of(stack.getOrDefault(CXRegistry.WATERLOGGABLE, false)), (stack, val) -> stack.set(CXRegistry.WATERLOGGABLE, val))
            .blockEntity(entity -> SLPropertyValue.of(entity.isWaterloggable()), CrystalixGlassBlockEntity::setWaterloggable)
            .build();
    
    public static final SLProperty<Boolean, CrystalixGlassBlockEntity> CONDUCTOR_PROPERTY = SLProperty.builder(Crystalix.id("conductor"), Boolean.class, CrystalixGlassBlockEntity.class)
            .name(CXLanguage.PROPERTY_CONDUCTOR)
            .tooltipDisplayer(SLProperty.boolDisplayer(CXLanguage.TOOLTIP_CONDUCTOR_ON, CXLanguage.TOOLTIP_CONDUCTOR_OFF))
            .cycler(SLProperty.BOOLEAN_CYCLER)
            .stack(stack -> SLPropertyValue.of(stack.getOrDefault(CXRegistry.CONDUCTOR, false)), (stack, val) -> stack.set(CXRegistry.CONDUCTOR, val))
            .blockEntity(entity -> SLPropertyValue.of(entity.isConductor()), CrystalixGlassBlockEntity::setConductor)
            .build();
    
    public static final SLProperty<Integer, CrystalixGlassBlockEntity> REDSTONE_PROPERTY = SLProperty.builder(Crystalix.id("redstone"), Integer.class, CrystalixGlassBlockEntity.class)
            .name(CXLanguage.PROPERTY_REDSTONE)
            .tooltipDisplayer(SLProperty.directDisplayer(CXLanguage.TOOLTIP_REDSTONE.translate()))
            .valueDisplayer(val -> SLComponent.of(Component.literal(String.valueOf(val)).withColor(RedStoneWireBlockAccessor.getColors()[val])))
            .cycler(SLProperty.numberCycler(0, 15))
            .stack(stack -> SLPropertyValue.of(stack.getOrDefault(CXRegistry.REDSTONE, 0)), (stack, val) -> stack.set(CXRegistry.REDSTONE, val))
            .blockEntity(entity -> SLPropertyValue.of(entity.getRedstone()), CrystalixGlassBlockEntity::setRedstone)
            .build();
    
    public static final SLPropertyContainer<CrystalixGlassBlock, CrystalixWandItem, CrystalixGlassBlockEntity> CONTAINER = SLPropertyContainer.builder(CrystalixGlassBlock.class, CrystalixWandItem.class, CrystalixGlassBlockEntity.class)
            .property(CXRegistry.INVISIBLE_PROPERTY)
            .property(CXRegistry.LIGHT_PROPERTY)
            .property(CXRegistry.GHOST_PROPERTY)
            .property(CXRegistry.COLOR_PROPERTY)
            .property(CXRegistry.MATERIAL_PROPERTY)
            .property(CXRegistry.SHADELESS_PROPERTY)
            .property(CXRegistry.TINTED_PROPERTY)
            .property(CXRegistry.REINFORCED_PROPERTY)
            .property(CXRegistry.WATERLOGGABLE_PROPERTY)
            .property(CXRegistry.CONDUCTOR_PROPERTY)
            .property(CXRegistry.REDSTONE_PROPERTY)
            .build();
    
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
