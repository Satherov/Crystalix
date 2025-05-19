package com.satherov.crystalix.content.properties;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.EntityCollisionContext;

import com.satherov.crystalix.content.CrystalixRegistry;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.IntFunction;
import java.util.function.Predicate;

public class BlockProperties {

    private static final String TAG_SHADELESS = CrystalixRegistry.SHADELESS;
    private static final String TAG_REINFORCED = CrystalixRegistry.REINFORCED;
    private static final String TAG_LIGHT = CrystalixRegistry.LIGHT;
    private static final String TAG_GHOST = CrystalixRegistry.GHOST;
    public final IProperty<?>[] properties;
    public final IProperty<Boolean> shadeless;
    public final IProperty<Boolean> reinforced;
    public final IProperty<Light> light;
    public final IProperty<Ghost> ghost;
    private final CompoundTag wandTag;

    public BlockProperties(ItemStack wand) {
        this.wandTag = wand.getOrCreateTag();

        this.shadeless = new NbtBooleanProperty(TAG_SHADELESS, false);
        this.reinforced = new NbtBooleanProperty(TAG_REINFORCED, false);
        this.light = new NbtEnumProperty<>(TAG_LIGHT, Light.class, Light.NONE);
        this.ghost = new NbtEnumProperty<>(TAG_GHOST, Ghost.class, Ghost.BLOCK_ALL);

        this.properties = new IProperty<?>[]{shadeless, reinforced, light, ghost};
    }

    /**
     * Find a property by its key
     **/
    @Nullable
    public IProperty<?> get(String key) {
        for (IProperty<?> prop : properties) {
            if (prop.getKey().equals(key)) {
                return prop;
            }
        }
        return null;
    }

    public enum Ghost implements StringRepresentable {
        BLOCK_ALL(0, ctx -> false),
        ALLOW_ALL(1, ctx -> true),
        BLOCK_PLAYER(2, ctx -> !(ctx.getEntity() instanceof Player)),
        ALLOW_PLAYER(3, ctx -> ctx.getEntity() instanceof Player),
        BLOCK_MONSTER(4, ctx -> !(ctx.getEntity() instanceof Monster)),
        ALLOW_MONSTER(5, ctx -> ctx.getEntity() instanceof Monster),
        BLOCK_ANIMAL(6, ctx -> !(ctx.getEntity() instanceof Animal)),
        ALLOW_ANIMAL(7, ctx -> ctx.getEntity() instanceof Animal);

        public static final IntFunction<Ghost> BY_ID = id -> switch (id) {
            case 1 -> ALLOW_ALL;
            case 2 -> BLOCK_PLAYER;
            case 3 -> ALLOW_PLAYER;
            case 4 -> BLOCK_MONSTER;
            case 5 -> ALLOW_MONSTER;
            case 6 -> BLOCK_ANIMAL;
            case 7 -> ALLOW_ANIMAL;
            default -> BLOCK_ALL;
        };
        private final int id;
        private final Predicate<EntityCollisionContext> collisionPredicate;

        Ghost(int id, Predicate<EntityCollisionContext> collisionPredicate) {
            this.id = id;
            this.collisionPredicate = collisionPredicate;
        }

        public boolean canCollide(EntityCollisionContext ctx) {
            return collisionPredicate.test(ctx);
        }

        public int getId() {
            return id;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum Light implements StringRepresentable {
        NONE(0), LIGHT(1), DARK(2), FAKE_LIGHT(3);

        public static final IntFunction<Light> BY_ID = id -> switch (id) {
            case 1 -> LIGHT;
            case 2 -> DARK;
            case 3 -> FAKE_LIGHT;
            default -> NONE;
        };
        private final int id;

        Light(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    private class NbtBooleanProperty implements IProperty<Boolean> {
        private final String key;
        private boolean value;

        NbtBooleanProperty(String key, boolean defaultValue) {
            this.key = key;
            if (wandTag.contains(key)) {
                this.value = wandTag.getBoolean(key);
            } else {
                this.value = defaultValue;
                wandTag.putBoolean(key, defaultValue);
            }
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getValueString() {
            return value ? "enabled" : "disabled";
        }

        @Override
        public void setValueString(String val) {
            set("enabled".equals(val));
        }

        @Override
        public void set(Boolean val) {
            this.value = val;
            wandTag.putBoolean(key, val);
        }

        @Override
        public Boolean get() {
            return value;
        }

        @Override
        public Boolean next(boolean forward) {
            boolean nextVal = !value;
            set(nextVal);
            return nextVal;
        }

        @Override
        public Component toComponent() {
            return Component
                    .translatable(getKeyTranslation())
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(": "))
                    .append(Component
                            .translatable(getValueTranslation())
                            .withStyle(value ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED)
                    );
        }
    }

    //––– NBT‐backed Enum property –––
    private class NbtEnumProperty<T extends Enum<T> & StringRepresentable> implements IProperty<T> {
        private final String key;
        private final Class<T> type;
        private final T defaultValue;
        private T value;

        NbtEnumProperty(String key, Class<T> type, T defaultValue) {
            this.key = key;
            this.type = type;
            this.defaultValue = defaultValue;

            String stored = wandTag.contains(key)
                    ? wandTag.getString(key)
                    : defaultValue.getSerializedName();

            this.value = findConstant(stored);
            wandTag.putString(key, this.value.getSerializedName());
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getValueString() {
            return value.getSerializedName();
        }

        @Override
        public void setValueString(String val) {
            set(findConstant(val));
        }

        @Override
        public void set(T val) {
            this.value = val;
            wandTag.putString(key, val.getSerializedName());
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public T next(boolean forward) {
            T[] constants = type.getEnumConstants();
            int currentIndex = value.ordinal();
            int nextIndex = (currentIndex + (forward ? 1 : -1) + constants.length) % constants.length;
            T nextVal = constants[nextIndex];
            set(nextVal);
            return nextVal;
        }

        @Override
        public Component toComponent() {
            return Component
                    .translatable(getKeyTranslation())
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(": "))
                    .append(Component.translatable(getValueTranslation()));
        }

        private T findConstant(String name) {
            for (T constant : type.getEnumConstants()) {
                if (constant.getSerializedName().equals(name)) {
                    return constant;
                }
            }
            return defaultValue;
        }
    }
}