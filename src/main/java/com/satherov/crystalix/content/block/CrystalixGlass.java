package com.satherov.crystalix.content.block;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;

import com.satherov.crystalix.content.CrystalixRegistry;
import com.satherov.crystalix.content.item.CrystalixWand;
import com.satherov.crystalix.content.properties.BlockProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CrystalixGlass extends TransparentBlock implements BeaconBeamBlock, LiquidBlockContainer {

    private final DyeColor color;

    public static final BooleanProperty SHADELESS = BooleanProperty.create("shadeless");
    public static final BooleanProperty REINFORCED = BooleanProperty.create("reinforced");
    public static final EnumProperty<BlockProperties.Light> LIGHT = EnumProperty.create("light", BlockProperties.Light.class);
    public static final EnumProperty<BlockProperties.Ghost> GHOST = EnumProperty.create("ghost", BlockProperties.Ghost.class);

    public CrystalixGlass(DyeColor dyeColor) {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_STAINED_GLASS).mapColor(dyeColor));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(SHADELESS, false)
                .setValue(REINFORCED, false)
                .setValue(LIGHT, BlockProperties.Light.NONE)
                .setValue(GHOST, BlockProperties.Ghost.BLOCK_ALL));
        this.color = dyeColor;
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHADELESS, REINFORCED, LIGHT, GHOST);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Player player = context.getPlayer();
        if (player == null) return this.defaultBlockState();

        if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof CrystalixWand) {
            ItemStack wand = player.getItemInHand(InteractionHand.OFF_HAND);
            return this.defaultBlockState()
                    .setValue(SHADELESS, Objects.requireNonNull(wand.get(CrystalixRegistry.SHADELESS)))
                    .setValue(REINFORCED, Objects.requireNonNull(wand.get(CrystalixRegistry.REINFORCED)))
                    .setValue(LIGHT, Objects.requireNonNull(wand.get(CrystalixRegistry.LIGHT)))
                    .setValue(GHOST, Objects.requireNonNull(wand.get(CrystalixRegistry.GHOST)));
        }

        return this.defaultBlockState();
    }

    // Reinforced

    @Override
    public boolean canDropFromExplosion(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return !state.getValue(REINFORCED) && super.canDropFromExplosion(state, level, pos, explosion);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return !state.getValue(REINFORCED) && super.canBeReplaced(state, context);
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        if (!state.getValue(REINFORCED)) {
            super.onBlockExploded(state, level, pos, explosion);
        }
    }

    // Light

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(LIGHT) != BlockProperties.Light.DARK && super.propagatesSkylightDown(state, level, pos);
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(LIGHT) == BlockProperties.Light.DARK ? level.getMaxLightLevel() : 0;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(LIGHT) == BlockProperties.Light.LIGHT ? 15 : 0;
    }

    // Ghost

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext != CollisionContext.empty()) {
            if (state.getValue(GHOST).canCollide(entityCollisionContext)) {
                return Shapes.empty();
            }
        }
        return super.getCollisionShape(state, level, pos, context);
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        if (Objects.requireNonNull(pathComputationType) == PathComputationType.LAND) {
            return !(state.getValue(GHOST) == BlockProperties.Ghost.ALLOW_ALL) ||
                   !(state.getValue(GHOST) == BlockProperties.Ghost.ALLOW_MONSTER) ||
                   !(state.getValue(GHOST) == BlockProperties.Ghost.ALLOW_ANIMAL);
        }
        return false;
    }

    @Override
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }
}
