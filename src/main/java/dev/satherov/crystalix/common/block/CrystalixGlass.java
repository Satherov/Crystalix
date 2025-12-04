package dev.satherov.crystalix.common.block;

import lombok.Getter;
import lombok.experimental.Accessors;

import dev.satherov.crystalix.common.item.CrystalixWand;
import dev.satherov.crystalix.common.properties.CSProperties;
import dev.satherov.crystalix.core.CSRegistry;
import dev.satherov.crystalix.core.annotations.NothingNull;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.WaterloggedTransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.mojang.serialization.MapCodec;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@NothingNull
@Accessors(fluent = true)
public class CrystalixGlass extends SLBlock implements EntityBlock, SimpleWaterloggedBlock {
    
    public static final MapCodec<WaterloggedTransparentBlock> CODEC = simpleCodec(WaterloggedTransparentBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty WATERLOGGABLE = BooleanProperty.create("waterloggable");
    public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");
    public static final BooleanProperty SHADELESS = BooleanProperty.create("shadeless");
    public static final BooleanProperty REINFORCED = BooleanProperty.create("reinforced");
    public static final BooleanProperty CLEAR = BooleanProperty.create("clear");
    public static final EnumProperty<CSProperties.Light> LIGHT = EnumProperty.create("light", CSProperties.Light.class);
    public static final EnumProperty<CSProperties.Ghost> GHOST = EnumProperty.create("ghost", CSProperties.Ghost.class);
    private final @Getter CSRegistry.Types type;
    private final @Getter
    @Nullable CSRegistry.Colors color;
    
    public CrystalixGlass(CSRegistry.Types type, @Nullable CSRegistry.Colors color) {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_STAINED_GLASS));
        this.type = type;
        this.color = color;
    }
    
    @Override
    protected MapCodec<? extends WaterloggedTransparentBlock> codec() {
        return CODEC;
    }
    
    @Override
    protected void registerState(StateBuilder builder) {
        builder.addValue(WATERLOGGED, false);
        builder.addValue(WATERLOGGABLE, false);
        builder.addValue(INVISIBLE, false);
        builder.addValue(SHADELESS, false);
        builder.addValue(REINFORCED, false);
        builder.addValue(CLEAR, true);
        builder.addValue(LIGHT, CSProperties.Light.NONE);
        builder.addValue(GHOST, CSProperties.Ghost.BLOCK_ALL);
    }
    
    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        BlockState state = this.defaultBlockState();
        if (player == null) return state;
        
        FluidState fluidState = level.getFluidState(pos);
        state.setValue(WATERLOGGED, fluidState.is(Fluids.WATER));
        
        ItemStack stack = player.getItemInHand(InteractionHand.OFF_HAND);
        
        if (stack.getItem() instanceof CrystalixWand) {
            return this.fromStack(state, stack);
        }
        
        return state;
    }
    
    @Override
    public Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return level.getBlockEntity(pos) instanceof CrystalixGlassTile tile ? tile.getColor() : 0xFFFFFF;
    }
    
    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(true) : super.getFluidState(state);
    }
    
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
    
    @Override
    protected VoxelShape getVisualShape(BlockState p_309057_, BlockGetter p_308936_, BlockPos p_308956_, CollisionContext p_309006_) {
        return Shapes.empty();
    }
    
    @Override
    protected float getShadeBrightness(BlockState p_308911_, BlockGetter p_308952_, BlockPos p_308918_) {
        return 1.0F;
    }
    
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return state.getValue(INVISIBLE) ? RenderShape.INVISIBLE : super.getRenderShape(state);
    }
    
    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacent, Direction side) {
        return adjacent.is(this) && !adjacent.getValue(INVISIBLE);
    }
    
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
        float progress = super.getDestroyProgress(state, player, level, pos);
        if (state.getValue(REINFORCED)) progress *= 0.1f;
        return progress;
    }
    
    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(LIGHT) != CSProperties.Light.DARK;
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (level instanceof ServerLevel) return;
        if (!((state.getValue(LIGHT) == CSProperties.Light.FAKE_LIGHT))) return;
        level.getLightEngine().checkBlock(pos);
    }
    
    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(LIGHT) == CSProperties.Light.DARK ? level.getMaxLightLevel() : 0;
    }
    
    @Override
    public int getLightEmission(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        var light = state.getValue(LIGHT);
        
        if (light == CSProperties.Light.FAKE_LIGHT)
            return FMLLoader.getDist() == Dist.CLIENT ? 15 : 0;
        
        if (light == CSProperties.Light.LIGHT)
            return 15;
        
        return 0;
    }
    
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext != CollisionContext.empty()) {
            if (state.getValue(GHOST).canCollide(entityCollisionContext)) {
                return Shapes.empty();
            }
        }
        return super.getCollisionShape(state, level, pos, context);
    }
    
    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        if (pathComputationType == PathComputationType.LAND) {
            return switch (state.getValue(GHOST)) {
                case ALLOW_ALL, ALLOW_ANIMAL, ALLOW_MONSTER -> true;
                default -> false;
            };
        }
        return false;
    }
    
    public BlockState fromStack(BlockState state, ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof CrystalixWand)) return state;
        if (!(state.getBlock() instanceof CrystalixGlass)) return state;
        
        state = state
                .setValue(INVISIBLE, Objects.requireNonNull(stack.get(CSRegistry.INVISIBLE)))
                .setValue(SHADELESS, Objects.requireNonNull(stack.get(CSRegistry.SHADELESS)))
                .setValue(REINFORCED, Objects.requireNonNull(stack.get(CSRegistry.REINFORCED)))
                .setValue(WATERLOGGABLE, Objects.requireNonNull(stack.get(CSRegistry.WATERLOGGABLE)))
                .setValue(CLEAR, Objects.requireNonNull(stack.get(CSRegistry.CLEAR)))
                .setValue(LIGHT, Objects.requireNonNull(stack.get(CSRegistry.LIGHT)))
                .setValue(GHOST, Objects.requireNonNull(stack.get(CSRegistry.GHOST)));
        
        return state;
    }
    
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrystalixGlassTile(pos, state);
    }
    
    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!(world instanceof ServerLevel level)) return;
        if (!(placer instanceof ServerPlayer player)) return;
        
        ItemStack wand = CrystalixWand.find(player);
        if (level.getBlockEntity(pos) instanceof CrystalixGlassTile tile) {
            int rgb = wand.getOrDefault(CSRegistry.COLOR, -1);
            tile.setColor(state, rgb);
        }
        
        super.setPlacedBy(level, pos, state, placer, stack);
    }
}
