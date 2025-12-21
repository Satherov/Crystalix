package dev.satherov.crystalix.common.block;

import lombok.Getter;
import lombok.experimental.Accessors;

import dev.satherov.crystalix.common.item.CrystalixWand;
import dev.satherov.crystalix.common.properties.CSProperties;
import dev.satherov.crystalix.core.CSRegistry;
import dev.satherov.crystalix.core.annotations.NothingNull;

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
import net.minecraft.world.level.chunk.ChunkAccess;
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
    
    public static final MapCodec<WaterloggedTransparentBlock> CODEC = BlockBehaviour.simpleCodec(WaterloggedTransparentBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    
    public static final BooleanProperty WATERLOGGABLE = BooleanProperty.create("waterloggable");
    public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");
    public static final BooleanProperty SHADELESS = BooleanProperty.create("shadeless");
    public static final BooleanProperty TRANSPARENT = BooleanProperty.create("transparent");
    public static final BooleanProperty REDSTONE = BooleanProperty.create("redstone");
    public static final BooleanProperty REINFORCED = BooleanProperty.create("reinforced");
    
    public static final EnumProperty<CSProperties.Light> LIGHT = EnumProperty.create("light", CSProperties.Light.class);
    public static final EnumProperty<CSProperties.Ghost> GHOST = EnumProperty.create("ghost", CSProperties.Ghost.class);
    
    private final @Getter CSRegistry.Types type;
    private final @Getter @Nullable CSRegistry.Colors color;
    
    public CrystalixGlass(CSRegistry.Types type, @Nullable CSRegistry.Colors color) {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_STAINED_GLASS)
                .isRedstoneConductor(CrystalixGlassTile.isRedstoneConductor())
                .lightLevel(state -> state.getValue(CrystalixGlass.LIGHT).equals(CSProperties.Light.LIGHT) ? 15 : 0)
        );
        this.type = type;
        this.color = color;
    }
    
    @Override
    protected MapCodec<? extends WaterloggedTransparentBlock> codec() {
        return CrystalixGlass.CODEC;
    }
    
    @Override
    protected void registerState(StateBuilder builder) {
        builder.addValue(CrystalixGlass.WATERLOGGED, false);
        builder.addValue(CrystalixGlass.WATERLOGGABLE, false);
        builder.addValue(CrystalixGlass.INVISIBLE, false);
        builder.addValue(CrystalixGlass.SHADELESS, false);
        builder.addValue(CrystalixGlass.REDSTONE, false);
        builder.addValue(CrystalixGlass.TRANSPARENT, true);
        builder.addValue(CrystalixGlass.REINFORCED, false);
        builder.addValue(CrystalixGlass.LIGHT, CSProperties.Light.NONE);
        builder.addValue(CrystalixGlass.GHOST, CSProperties.Ghost.BLOCK_ALL);
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
        state.setValue(CrystalixGlass.WATERLOGGED, !fluidState.is(Fluids.EMPTY));
        
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
        return state.getValue(CrystalixGlass.WATERLOGGED) ? Fluids.WATER.getSource(true) : super.getFluidState(state);
    }
    
    @Override
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return state.getValue(CrystalixGlass.WATERLOGGABLE) && fluid == Fluids.WATER;
    }
    
    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.getValue(CrystalixGlass.WATERLOGGABLE) || state.getValue(CrystalixGlass.WATERLOGGED) || fluidState.getType() != Fluids.WATER) return false;
        
        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(CrystalixGlass.WATERLOGGED, true), 3);
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        
        return true;
    }
    
    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }
    
    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter getter, BlockPos pos) {
        return 1.0F;
    }
    
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return state.getValue(CrystalixGlass.INVISIBLE) ? RenderShape.INVISIBLE : super.getRenderShape(state);
    }
    
    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacent, Direction side) {
        return adjacent.is(this) && !adjacent.getValue(CrystalixGlass.INVISIBLE);
    }
    
    @Override
    public boolean canDropFromExplosion(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return CrystalixGlassTile.isReinforced(level, pos) && super.canDropFromExplosion(state, level, pos, explosion);
    }
    
    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return CrystalixGlassTile.isReinforced(context.getLevel(), context.getClickedPos()) && super.canBeReplaced(state, context);
    }
    
    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        if (!CrystalixGlassTile.isReinforced(level, pos)) super.onBlockExploded(state, level, pos, explosion);
    }
    
    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter getter, BlockPos pos, Entity entity) {
        return !CrystalixGlassTile.isReinforced(getter, pos);
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public float getExplosionResistance(BlockState state, BlockGetter getter, BlockPos pos, Explosion explosion) {
        return CrystalixGlassTile.isReinforced(getter, pos) ? Float.MAX_VALUE : super.getExplosionResistance();
    }
    
    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter getter, BlockPos pos) {
        float progress = super.getDestroyProgress(state, player, getter, pos);
        if (CrystalixGlassTile.isReinforced(getter, pos)) progress *= 0.1f;
        return progress;
    }
    
    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(CrystalixGlass.LIGHT) != CSProperties.Light.DARK;
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (level instanceof ServerLevel) return;
        if (!((state.getValue(CrystalixGlass.LIGHT) == CSProperties.Light.FAKE_LIGHT))) return;
        level.getLightEngine().checkBlock(pos);
    }
    
    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(CrystalixGlass.LIGHT) == CSProperties.Light.DARK ? level.getMaxLightLevel() : 0;
    }
    
    @Override
    public int getLightEmission(BlockState state, BlockGetter getter, BlockPos pos) {
        if (state.getValue(CrystalixGlass.LIGHT).equals(CSProperties.Light.FAKE_LIGHT)) {
            return switch (getter) { // Are we on the server?
                case ChunkAccess chunk -> chunk.getLevel() instanceof ServerLevel;
                case Level level -> level instanceof ServerLevel;
                default -> true;
            } ? 0 : 15;
        } else return super.getLightEmission(state, getter, pos);
    }
    
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext != CollisionContext.empty()) {
            if (state.getValue(CrystalixGlass.GHOST).canCollide(entityCollisionContext)) {
                return Shapes.empty();
            }
        }
        return super.getCollisionShape(state, level, pos, context);
    }
    
    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        if (pathComputationType == PathComputationType.LAND) {
            return switch (state.getValue(CrystalixGlass.GHOST)) {
                case ALLOW_ALL, ALLOW_ANIMAL, ALLOW_MONSTER -> true;
                default -> false;
            };
        }
        return false;
    }
    
    @Override
    protected boolean isSignalSource(BlockState state) {
        return state.getValue(CrystalixGlass.REDSTONE);
    }
    
    @Override
    protected int getSignal(BlockState state, BlockGetter getter, BlockPos pos, Direction side) {
        return state.getValue(CrystalixGlass.REDSTONE) ? 15 : 0;
    }
    
    public BlockState fromStack(BlockState state, ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof CrystalixWand)) return state;
        if (!(state.getBlock() instanceof CrystalixGlass)) return state;
        
        state = state
                .setValue(CrystalixGlass.INVISIBLE, Objects.requireNonNull(stack.get(CSRegistry.INVISIBLE)))
                .setValue(CrystalixGlass.SHADELESS, Objects.requireNonNull(stack.get(CSRegistry.SHADELESS)))
                .setValue(CrystalixGlass.WATERLOGGABLE, Objects.requireNonNull(stack.get(CSRegistry.WATERLOGGABLE)))
                .setValue(CrystalixGlass.TRANSPARENT, Objects.requireNonNull(stack.get(CSRegistry.TRANSPARENT)))
                .setValue(CrystalixGlass.REDSTONE, Objects.requireNonNull(stack.get(CSRegistry.REDSTONE)))
                .setValue(CrystalixGlass.LIGHT, Objects.requireNonNull(stack.get(CSRegistry.LIGHT)))
                .setValue(CrystalixGlass.GHOST, Objects.requireNonNull(stack.get(CSRegistry.GHOST)));
        
        return state;
    }
    
    public boolean setEntityProperties(BlockGetter getter, BlockPos pos, BlockState state, ItemStack stack) {
        BlockEntity be = getter.getBlockEntity(pos);
        if (!(be instanceof CrystalixGlassTile tile) || !(getter instanceof Level level)) return false;
        
        if (!stack.getOrDefault(CSRegistry.APPLY_COLORLESS, false)) {
            tile.setColor(state, stack.getOrDefault(CSRegistry.COLOR, 0xFFFFFF));
        }
        
        tile.setReinforced(stack.getOrDefault(CSRegistry.REINFORCED, false));
        tile.setConductor(stack.getOrDefault(CSRegistry.CONDUCTOR, false));
        
        level.setBlockAndUpdate(pos, state);
        level.updateNeighborsAt(pos, this);
        return true;
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
        this.setEntityProperties(world, pos, state, wand);
        
        super.setPlacedBy(level, pos, state, placer, stack);
    }
}
