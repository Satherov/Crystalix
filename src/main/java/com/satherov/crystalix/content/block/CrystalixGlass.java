package com.satherov.crystalix.content.block;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.WaterloggedTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.satherov.crystalix.content.item.CrystalixWand;
import com.satherov.crystalix.content.properties.BlockProperties;
import com.satherov.crystalix.content.CrystalixRegistry;
import com.satherov.crystalix.core.annotations.NothingNull;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@NothingNull
public class CrystalixGlass extends WaterloggedTransparentBlock {

    public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");
    public static final BooleanProperty SHADELESS = BooleanProperty.create("shadeless");
    public static final BooleanProperty REINFORCED = BooleanProperty.create("reinforced");
    public static final BooleanProperty WATERLOGGABLE = BooleanProperty.create("waterloggable");
    public static final EnumProperty<BlockProperties.Light> LIGHT = EnumProperty.create("light", BlockProperties.Light.class);
    public static final EnumProperty<BlockProperties.Ghost> GHOST = EnumProperty.create("ghost", BlockProperties.Ghost.class);
    private final DyeColor color;

    public CrystalixGlass(DyeColor dyeColor) {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_STAINED_GLASS).mapColor(dyeColor));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, false)
                .setValue(INVISIBLE, false)
                .setValue(SHADELESS, false)
                .setValue(REINFORCED, false)
                .setValue(WATERLOGGABLE, false)
                .setValue(LIGHT, BlockProperties.Light.NONE)
                .setValue(GHOST, BlockProperties.Ghost.BLOCK_ALL));
        this.color = dyeColor;
    }

    @Override
    public Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return this.color.getTextureDiffuseColor();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, INVISIBLE, SHADELESS, REINFORCED, WATERLOGGABLE, LIGHT, GHOST);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Player player = context.getPlayer();
        if (player == null) return this.defaultBlockState();

        if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof CrystalixWand) {
            ItemStack wand = player.getItemInHand(InteractionHand.OFF_HAND);
            return this.modifyFromWand(this.defaultBlockState(), wand);
        }

        return this.defaultBlockState();
    }
    
    public BlockState modifyFromWand(BlockState state, ItemStack wand) {
        return state
                   .setValue(INVISIBLE, Objects.requireNonNull(wand.get(CrystalixRegistry.INVISIBLE)))
                   .setValue(SHADELESS, Objects.requireNonNull(wand.get(CrystalixRegistry.SHADELESS)))
                   .setValue(REINFORCED, Objects.requireNonNull(wand.get(CrystalixRegistry.REINFORCED)))
                   .setValue(WATERLOGGABLE, Objects.requireNonNull(wand.get(CrystalixRegistry.WATERLOGGABLE)))
                   .setValue(LIGHT, Objects.requireNonNull(wand.get(CrystalixRegistry.LIGHT)))
                   .setValue(GHOST, Objects.requireNonNull(wand.get(CrystalixRegistry.GHOST)));
    }
    
    // Waterlogging
    
    @Override
    public boolean canPlaceLiquid(@javax.annotation.Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return state.getValue(WATERLOGGABLE) && fluid == Fluids.WATER;
    }
    
    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.getValue(WATERLOGGABLE)) return false;
        if (state.getValue(BlockStateProperties.WATERLOGGED) || fluidState.getType() != Fluids.WATER) return false;
        
        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, Boolean.TRUE), 3);
            level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
        }
        
        return true;
    }
    
    

    // Invisible

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return state.getValue(INVISIBLE) ? RenderShape.INVISIBLE : super.getRenderShape(state);
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.is(this) && !adjacentBlockState.getValue(INVISIBLE);
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
    @SuppressWarnings("deprecation")
    public float getExplosionResistance(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion) {
        return state.getValue(REINFORCED) ? Float.MAX_VALUE : super.getExplosionResistance();
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        float destroyProgress = super.getDestroyProgress(state, player, level, pos);
        if (state.getValue(REINFORCED)) destroyProgress *= 0.1f;
        return destroyProgress;
    }


    // Light

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(LIGHT) != BlockProperties.Light.DARK;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
       if (level instanceof ServerLevel) return;
       if (!((state.getValue(LIGHT) == BlockProperties.Light.FAKE_LIGHT))) return;
       level.getLightEngine().checkBlock(pos);
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(LIGHT) == BlockProperties.Light.DARK ? level.getMaxLightLevel() : 0;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        var light = state.getValue(LIGHT);

        if (light == BlockProperties.Light.FAKE_LIGHT)
            return FMLLoader.getDist() == Dist.CLIENT ? 15 : 0;

        if (light == BlockProperties.Light.LIGHT)
            return 15;

        return 0;
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
}
