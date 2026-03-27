package dev.satherov.crystalix.compat.framedblocks;

import dev.satherov.crystalix.common.block.CrystalixGlass;
import dev.satherov.crystalix.common.item.CrystalixWand;
import dev.satherov.crystalix.common.properties.CSProperties;
import dev.satherov.crystalix.core.CSRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jetbrains.annotations.Nullable;

import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.api.camo.TriggerRegistrar;
import xfacthd.framedblocks.api.camo.block.AbstractBlockCamoContainerFactory;
import xfacthd.framedblocks.api.util.CamoMessageVerbosity;
import xfacthd.framedblocks.api.util.ConfigView;
import xfacthd.framedblocks.api.util.Utils;

final class CrystalixGlassCamoContainerFactory extends AbstractBlockCamoContainerFactory<CrystalixGlassCamoContainer> {
    
    private static final MapCodec<CrystalixGlassCamoContainer> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BlockState.CODEC.fieldOf("state").forGetter(CrystalixGlassCamoContainer::getState),
            Codec.INT.fieldOf("color").forGetter(CrystalixGlassCamoContainer::getTintColor)
    ).apply(inst, CrystalixGlassCamoContainer::new));
    private static final StreamCodec<RegistryFriendlyByteBuf, CrystalixGlassCamoContainer> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY),
            CrystalixGlassCamoContainer::getState,
            ByteBufCodecs.INT,
            CrystalixGlassCamoContainer::getTintColor,
            CrystalixGlassCamoContainer::new
    );
    private static final int DEFAULT_TINT = 0xFFFFFF;
    
    @Override
    protected CrystalixGlassCamoContainer createContainer(BlockState camoState, Level level, BlockPos pos, Player player, ItemStack stack) {
        return CrystalixGlassCamoContainerFactory.createContainer(camoState, CrystalixWand.find(player), CrystalixGlassCamoContainerFactory.DEFAULT_TINT);
    }
    
    private static CrystalixGlassCamoContainer createContainer(BlockState camoState, ItemStack wandStack, int tintColor) {
        if (!wandStack.getOrDefault(CSRegistry.APPLY_COLORLESS, false)) {
            tintColor = wandStack.getOrDefault(CSRegistry.COLOR, CrystalixGlassCamoContainerFactory.DEFAULT_TINT);
        }
        return new CrystalixGlassCamoContainer(camoState, tintColor);
    }
    
    @Override
    @Nullable
    protected BlockState getStateFromItemStack(Level level, BlockPos pos, Player player, ItemStack stack) {
        if (stack.getItem() instanceof BlockItem item) {
            return CrystalixGlassCamoContainerFactory.applyWandModifiers(item.getBlock().defaultBlockState(), CrystalixWand.find(player));
        }
        return null;
    }
    
    private static BlockState applyWandModifiers(BlockState state, ItemStack wand) {
        if (!(state.getBlock() instanceof CrystalixGlass glass)) return state;
        
        // Apply properties from wand and reset unsupported ones to their default values
        BlockState newState = glass.fromStack(state, wand).setValue(CrystalixGlass.GHOST, CSProperties.Ghost.BLOCK_ALL);
        CSProperties.Light light = newState.getValue(CrystalixGlass.LIGHT);
        if (light != CSProperties.Light.NONE && light != CSProperties.Light.LIGHT) {
            newState = newState.setValue(CrystalixGlass.LIGHT, CSProperties.Light.NONE);
        }
        return newState;
    }
    
    @Override
    protected CrystalixGlassCamoContainer copyContainerWithState(CrystalixGlassCamoContainer container, BlockState newCamoState) {
        return new CrystalixGlassCamoContainer(newCamoState, container.getTintColor());
    }
    
    @Override
    protected ItemStack createItemStack(Level level, BlockPos pos, Player player, ItemStack stack, CrystalixGlassCamoContainer container) {
        return this.dropCamo(container);
    }
    
    @Override
    public ItemStack dropCamo(CrystalixGlassCamoContainer container) {
        return new ItemStack(container.getState().getBlock());
    }
    
    @Override
    public boolean canApplyInCraftingRecipe(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem item) {
            return this.isValidBlock(item.getBlock().defaultBlockState(), EmptyBlockGetter.INSTANCE, BlockPos.ZERO, null);
        }
        return false;
    }
    
    @Override
    public CrystalixGlassCamoContainer applyCamoInCraftingRecipe(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem item) {
            BlockState state = item.getBlock().defaultBlockState();
            if (this.isValidBlock(state, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, null)) {
                return new CrystalixGlassCamoContainer(state, CrystalixGlassCamoContainerFactory.DEFAULT_TINT);
            }
        }
        throw new IllegalStateException("applyCamoInCraftingRecipe() called without canApplyInCraftingRecipe() check");
    }
    
    @Override
    public ItemStack getCraftingRemainder(ItemStack stack) {
        if (!ConfigView.Server.INSTANCE.shouldConsumeCamoItem()) {
            return stack.copyWithCount(1);
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public CrystalixGlassCamoContainer handleInteraction(Level level, BlockPos pos, Player player, CrystalixGlassCamoContainer camo, ItemStack stack, InteractionHand hand) {
        if (stack.is(CSRegistry.WAND)) {
            BlockState state = CrystalixGlassCamoContainerFactory.applyWandModifiers(camo.getState(), stack);
            return CrystalixGlassCamoContainerFactory.createContainer(state, stack, camo.getTintColor());
        }
        return camo;
    }
    
    @Override
    protected boolean isValidBlock(BlockState camoState, BlockGetter level, BlockPos pos, @Nullable Player player) {
        if (!(camoState.getBlock() instanceof CrystalixGlass)) {
            return false;
        }
        if (camoState.is(Utils.BLOCK_BLACKLIST)) {
            CamoContainerFactory.displayValidationMessage(player, CamoContainerFactory.MSG_BLACKLISTED, CamoMessageVerbosity.DEFAULT);
            return false;
        }
        return true;
    }
    
    @Override
    public boolean canTriviallyConvertToItemStack() {
        return true;
    }
    
    @Override
    protected void writeToNetwork(CompoundTag tag, CrystalixGlassCamoContainer container) {
        tag.putInt("state", Block.getId(container.getState()));
        tag.putInt("color", container.getTintColor());
    }
    
    @Override
    protected CrystalixGlassCamoContainer readFromNetwork(CompoundTag tag) {
        return new CrystalixGlassCamoContainer(Block.stateById(tag.getInt("state")), tag.getInt("color"));
    }
    
    @Override
    public MapCodec<CrystalixGlassCamoContainer> codec() {
        return CrystalixGlassCamoContainerFactory.CODEC;
    }
    
    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CrystalixGlassCamoContainer> streamCodec() {
        return CrystalixGlassCamoContainerFactory.STREAM_CODEC;
    }
    
    @Override
    public void registerTriggerItems(TriggerRegistrar registrar) {
        CSRegistry.ENTRIES.values()
                .stream()
                .map(Holder::value)
                .map(Block::asItem)
                .forEach(registrar::registerApplicationItem);
        registrar.registerRemovalItem(Utils.FRAMED_HAMMER.value());
    }
}
