package dev.satherov.crystalix.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CSBlock extends Block {
    
    private StateBuilder stateBuilder;
    
    public CSBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(stateBuilder.defaultBlockState());
        stateBuilder.clear();
    }
    
    public static VoxelShape column(double size, double y1, double y2) {
        return column(size, size, y1, y2);
    }
    
    public static VoxelShape column(double xSize, double zSize, double y1, double y2) {
        double dx = xSize / 2.0;
        double dz = zSize / 2.0;
        return box(8.0 - dx, y1, 8.0 - dz, 8.0 + dx, y2, 8.0 + dz);
    }
    
    @Override
    protected final void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        this.stateBuilder = StateBuilder.create(this);
        this.registerState(stateBuilder);
        this.stateBuilder.createDefinition(builder);
    }
    
    /**
     * Used to register BlockProperties to this block.
     *
     * @param builder State builder to register properties to
     */
    protected void registerState(StateBuilder builder) {}
    
    @Override
    protected final void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!newState.is(state.getBlock())) {
            onRemoved((ServerLevel) level, pos, state, newState, movedByPiston);
            if (state.hasBlockEntity()) level.removeBlockEntity(pos);
        } else {
            onChanged((ServerLevel) level, pos, state, newState, movedByPiston);
        }
    }
    
    @Override
    protected final void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!state.is(oldState.getBlock())) {
            onPlaced((ServerLevel) level, pos, state, oldState, movedByPiston);
        }
    }
    
    /**
     * Called when the block is actually removed from the world
     *
     * @param level         Level this block is in
     * @param pos           Position of this block
     * @param state         Block state before removal
     * @param newState      Block state after removal
     * @param movedByPiston Whether this block was moved by a piston
     */
    protected void onRemoved(ServerLevel level, BlockPos pos, BlockState state, BlockState newState, boolean movedByPiston) {}
    
    /**
     * Called when the block state changes.
     *
     * @param level         Level this block is in
     * @param pos           Position of this block
     * @param state         Block state before change
     * @param newState      Block state after change
     * @param movedByPiston Whether this block was moved by a piston
     */
    protected void onChanged(ServerLevel level, BlockPos pos, BlockState state, BlockState newState, boolean movedByPiston) {}
    
    /**
     * Called when this block is actually placed into the world.
     *
     * @param level         Level this block is in
     * @param pos           Position of this block
     * @param state         Block state after placement
     * @param oldState      Block state before placement
     * @param movedByPiston Whether this block was moved by a piston
     */
    protected void onPlaced(ServerLevel level, BlockPos pos, BlockState state, BlockState oldState, boolean movedByPiston) {}
    
    public static class StateBuilder {
        
        private final CSBlock block;
        private final Map<Property<?>, Comparable<?>> properties = new HashMap<>();
        
        private StateBuilder(CSBlock block) {
            this.block = block;
        }
        
        protected static <B extends CSBlock> StateBuilder create(B block) {
            return new StateBuilder(block);
        }
        
        @SuppressWarnings("unchecked")
        private static <T extends Comparable<T>> BlockState setUnchecked(BlockState state, Property<?> property, Comparable<?> value) {
            return state.setValue((Property<T>) property, (T) value);
        }
        
        /**
         * Add a BlockProperty to this block.
         *
         * @param property     Property to add
         * @param defaultValue Default value for this property
         * @param <T>          Property type
         * @param <V>          Default value type
         *
         * @return This builder
         */
        public <T extends Comparable<T>, V extends T> StateBuilder addValue(Property<T> property, V defaultValue) {
            properties.put(property, defaultValue);
            return this;
        }
        
        private void createDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            for (Property<?> property : properties.keySet()) {
                builder.add(property);
            }
        }
        
        private BlockState defaultBlockState() {
            BlockState state = block.defaultBlockState();
            for (Map.Entry<Property<?>, Comparable<?>> entry : properties.entrySet()) {
                state = setUnchecked(state, entry.getKey(), entry.getValue());
            }
            return state;
        }
        
        private void clear() {
            properties.clear();
            block.stateBuilder = null;
        }
    }
}
