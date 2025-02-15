package com.satherov.crystalix.content.properties;

import java.util.Locale;
import java.util.function.IntFunction;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.satherov.crystalix.content.CrystalixRegistry;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.EntityCollisionContext;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;

public class BlockProperties {

    public enum Ghost implements StringRepresentable {
        BLOCK_ALL(    0, context -> false),
        ALLOW_ALL(    1, context -> true),
        BLOCK_PLAYER( 2, context -> !(context.getEntity() instanceof Player)),
        ALLOW_PLAYER( 3, context ->   context.getEntity() instanceof Player),
        BLOCK_MONSTER(4, context -> !(context.getEntity() instanceof Monster)),
        ALLOW_MONSTER(5, context ->   context.getEntity() instanceof Monster),
        BLOCK_ANIMAL( 6, context -> !(context.getEntity() instanceof Animal)),
        ALLOW_ANIMAL( 7, context ->   context.getEntity() instanceof Animal);

        private final int id;
        private final Predicate<EntityCollisionContext> collisionPredicate;

        Ghost(int id, Predicate<EntityCollisionContext> collisionPredicate) {
            this.id = id;
            this.collisionPredicate = collisionPredicate;
        }

        public boolean canCollide(EntityCollisionContext context) {
            return this.collisionPredicate.test(context);
        }

        public int getId() {
            return this.id;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public static final Codec<Ghost> CODEC = StringRepresentable.fromEnum(Ghost::values);
        private static final IntFunction<Ghost> BY_ID = ByIdMap.continuous(Ghost::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, Ghost> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Ghost::getId);
    }

    public enum Light implements StringRepresentable {
        NONE( 0),
        LIGHT(1),
        DARK( 2),;

        private final int id;

        Light(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public static final Codec<Light> CODEC = StringRepresentable.fromEnum(Light::values);
        private static final IntFunction<Light> BY_ID = ByIdMap.continuous(Light::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, Light> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Light::getId);
    }

    public final ItemStack wand;

    public final BooleanProperty shadeless;
    public final BooleanProperty reinforced;
    public final EnumProperty<Light> light;
    public final EnumProperty<Ghost> ghost;

    public final IProperty<?>[] properties;

    public BlockProperties(ItemStack wand) {
        this.wand = wand;

        shadeless = new BooleanProperty(wand, CrystalixRegistry.SHADELESS.get(), "shadeless", false);
        reinforced = new BooleanProperty(wand, CrystalixRegistry.REINFORCED.get(), "reinforced", false);
        light = new EnumProperty<>(wand, CrystalixRegistry.LIGHT.get(), "light", Light.class, Light.NONE);
        ghost = new EnumProperty<>(wand, CrystalixRegistry.GHOST.get(), "ghost", Ghost.class, Ghost.BLOCK_ALL);

        properties = new IProperty<?>[] {shadeless, reinforced, light, ghost};
    }

    @Nullable
    public IProperty<?> get(String key) {
        for (IProperty<?> option : properties) {
            if (option.getKey().equals(key)) return option;
        }
        return null;
    }
}
