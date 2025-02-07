package com.satherov.crystalix.content.block;

import java.util.List;
import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CrystalixBlock extends TransparentBlock implements BeaconBeamBlock, LiquidBlockContainer {

    private final DyeColor color;

    public static final BooleanProperty SHADED = BooleanProperty.create("shaded");
    public static final BooleanProperty REINFORCED = BooleanProperty.create("reinforced");
    public static final EnumProperty<Light> LIGHT = EnumProperty.create("light", Light.class);
    public static final EnumProperty<Ghost> GHOST = EnumProperty.create("ghost", Ghost.class);

    public CrystalixBlock(DyeColor dyeColor, BlockBehaviour.Properties properties) {
        super(properties);
        this.color = dyeColor;
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(SHADED) ? 15 : 0;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(LIGHT) != Light.DARK && super.propagatesSkylightDown(state, level, pos);
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(LIGHT) == Light.NONE ? 0 : state.getValue(LIGHT) == Light.LIGHT ? 15 : level.getMaxLightLevel();
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
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }

    public enum Light implements StringRepresentable {
        NONE,
        LIGHT,
        DARK;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }

    public enum Ghost implements StringRepresentable {
        BLOCK_ALL(context -> false),
        ALLOW_ALL(context -> true),
        BLOCK_PLAYER(context -> !(context.getEntity() instanceof Player)),
        ALLOW_PLAYER(context -> context.getEntity() instanceof Player),
        BLOCK_MONSTER(context -> !(context.getEntity() instanceof Monster)),
        ALLOW_MONSTER(context -> context.getEntity() instanceof Monster),
        BLOCK_ANIMAL(context -> !(context.getEntity() instanceof Animal)),
        ALLOW_ANIMAL(context -> context.getEntity() instanceof Animal);

        private final Predicate<EntityCollisionContext> collisionPredicate;

        Ghost(Predicate<EntityCollisionContext> collisionPredicate) {
            this.collisionPredicate = collisionPredicate;
        }

        public boolean canCollide(EntityCollisionContext context) {
            return this.collisionPredicate.test(context);
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}
