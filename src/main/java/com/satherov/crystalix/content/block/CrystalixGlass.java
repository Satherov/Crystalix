package com.satherov.crystalix.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
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

import com.satherov.crystalix.content.item.CrystalixWand;
import com.satherov.crystalix.content.properties.BlockProperties;

import org.jetbrains.annotations.Nullable;

public class CrystalixGlass extends AbstractGlassBlock implements LiquidBlockContainer {

    public static final BooleanProperty SHADELESS = BooleanProperty.create("shadeless");
    public static final BooleanProperty REINFORCED = BooleanProperty.create("reinforced");
    public static final EnumProperty<BlockProperties.Light> LIGHT = EnumProperty.create("light", BlockProperties.Light.class);
    public static final EnumProperty<BlockProperties.Ghost> GHOST = EnumProperty.create("ghost", BlockProperties.Ghost.class);
    private final DyeColor color;

    public CrystalixGlass(DyeColor dyeColor) {
        super(BlockBehaviour.Properties.copy(Blocks.WHITE_STAINED_GLASS).mapColor(dyeColor));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(SHADELESS, false)
                .setValue(REINFORCED, false)
                .setValue(LIGHT, BlockProperties.Light.NONE)
                .setValue(GHOST, BlockProperties.Ghost.BLOCK_ALL));
        this.color = dyeColor;
    }

    @Override
    public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return this.color.getTextureDiffuseColors();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHADELESS, REINFORCED, LIGHT, GHOST);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return defaultBlockState();
        }

        ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
        if (offhand.getItem() instanceof CrystalixWand) {
            BlockProperties properties = new BlockProperties(offhand);
            return defaultBlockState()
                    .setValue(SHADELESS, properties.shadeless.get())
                    .setValue(REINFORCED, properties.reinforced.get())
                    .setValue(LIGHT, properties.light.get())
                    .setValue(GHOST, properties.ghost.get());
        }

        return defaultBlockState();
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

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
        return !state.getValue(REINFORCED);
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return state.getValue(REINFORCED) ? Float.MAX_VALUE : super.getExplosionResistance(state, level, pos, explosion);
    }

    //––– Light behavior –––
    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return state.getValue(LIGHT) != BlockProperties.Light.DARK;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIGHT) == BlockProperties.Light.FAKE_LIGHT && level.getGameTime() % 4 == 0) {
            level.getLightEngine().checkBlock(pos);
            level.sendBlockUpdated(pos, state, state, 2);
        }
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return state.getValue(LIGHT) == BlockProperties.Light.DARK
                ? world.getMaxLightLevel()
                : 0;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        switch (state.getValue(LIGHT)) {
            case FAKE_LIGHT -> {
                // fake light only on client
                if (!(world instanceof ServerLevel)) {
                    return 15;
                }
            }
            case LIGHT -> {
                return 15;
            }
            default -> {
            }
        }
        return 0;
    }

    // Ghost

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityCtx && !entityCtx.equals(CollisionContext.empty())) {
            if (state.getValue(GHOST).canCollide(entityCtx)) {
                return Shapes.empty();
            }
        }
        return super.getCollisionShape(state, world, pos, context);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType pathType) {
        if (pathType == PathComputationType.LAND) {
            var ghost = state.getValue(GHOST);
            return ghost != BlockProperties.Ghost.ALLOW_ALL
                    && ghost != BlockProperties.Ghost.ALLOW_MONSTER
                    && ghost != BlockProperties.Ghost.ALLOW_ANIMAL;
        }
        return false;
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }
}
