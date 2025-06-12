package com.satherov.crystalix.content.properties;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.EntityCollisionContext;

import com.mojang.serialization.Codec;

import com.satherov.crystalix.content.CrystalixRegistry;
import com.satherov.crystalix.core.annotations.NothingNull;
import com.satherov.crystalix.core.lang.CrystalixLanguage;
import com.satherov.crystalix.core.lang.ILangEntry;
import com.satherov.crystalix.core.lang.ITranslatable;

import org.jetbrains.annotations.Nullable;

import io.netty.buffer.ByteBuf;

import java.util.Locale;
import java.util.function.Predicate;

@NothingNull
public class BlockProperties {

    public final ItemStack wand;
    public final BooleanProperty invisible;
    public final BooleanProperty shadeless;
    public final BooleanProperty reinforced;
    public final EnumProperty<Light> light;
    public final EnumProperty<Ghost> ghost;
    public final ITranslatableProperty<?>[] properties;
    public BlockProperties(ItemStack wand) {
        this.wand = wand;

        invisible = new BooleanProperty(wand, CrystalixRegistry.INVISIBLE.get(), "invisible", false, CrystalixLanguage.PROPERTY_INVISIBLE);
        shadeless = new BooleanProperty(wand, CrystalixRegistry.SHADELESS.get(), "shadeless", false, CrystalixLanguage.PROPERTY_SHADELESS);
        reinforced = new BooleanProperty(wand, CrystalixRegistry.REINFORCED.get(), "reinforced", false, CrystalixLanguage.PROPERTY_REINFORCED);
        light = new EnumProperty<>(wand, CrystalixRegistry.LIGHT.get(), "light", Light.class, Light.NONE);
        ghost = new EnumProperty<>(wand, CrystalixRegistry.GHOST.get(), "ghost", Ghost.class, Ghost.BLOCK_ALL);

        properties = new ITranslatableProperty<?>[]{invisible, shadeless, reinforced, light, ghost};
    }

    @Nullable
    public ITranslatableProperty<?> get(String key) {
        for (ITranslatableProperty<?> option : properties) {
            if (option.getKey().equals(key)) return option;
        }
        return null;
    }

    public enum Ghost implements StringRepresentable, ITranslatable {
        BLOCK_ALL(CrystalixLanguage.PROPERTY_GHOST_ALL, ChatFormatting.DARK_GRAY, false, context -> false),
        ALLOW_ALL(CrystalixLanguage.PROPERTY_GHOST_ALL, ChatFormatting.DARK_GRAY, true, context -> true),
        BLOCK_PLAYER(CrystalixLanguage.PROPERTY_GHOST_PLAYER, ChatFormatting.AQUA, false, context -> !(context.getEntity() instanceof Player)),
        ALLOW_PLAYER(CrystalixLanguage.PROPERTY_GHOST_PLAYER, ChatFormatting.AQUA, true, context -> context.getEntity() instanceof Player),
        BLOCK_MONSTER(CrystalixLanguage.PROPERTY_GHOST_MONSTER, ChatFormatting.RED, false, context -> !(context.getEntity() instanceof Monster)),
        ALLOW_MONSTER(CrystalixLanguage.PROPERTY_GHOST_MONSTER, ChatFormatting.RED, true, context -> context.getEntity() instanceof Monster),
        BLOCK_ANIMAL(CrystalixLanguage.PROPERTY_GHOST_ANIMAL, ChatFormatting.GREEN, false, context -> !(context.getEntity() instanceof Animal)),
        ALLOW_ANIMAL(CrystalixLanguage.PROPERTY_GHOST_ANIMAL, ChatFormatting.GREEN, true, context -> context.getEntity() instanceof Animal);

        public static final Codec<Ghost> CODEC = IEnumCodec.makeCodec(Ghost.class);
        public static final StreamCodec<ByteBuf, Ghost> STREAM_CODEC = IEnumCodec.makeStreamCodec(Ghost.class);
        private final ILangEntry langEntry;
        private final ChatFormatting color;
        private final boolean state;
        private final Predicate<EntityCollisionContext> collisionPredicate;
        Ghost(ILangEntry langEntry, ChatFormatting color, boolean state, Predicate<EntityCollisionContext> collisionPredicate) {
            this.langEntry = langEntry;
            this.color = color;
            this.state = state;
            this.collisionPredicate = collisionPredicate;
        }

        public boolean canCollide(EntityCollisionContext context) {
            return this.collisionPredicate.test(context);
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        @Override
        public MutableComponent getTranslation() {
            return CrystalixLanguage.PROPERTY_GHOST.translateFormatted(ChatFormatting.GRAY)
                    .append(Component.literal(" "))
                    .append(this.state
                            ? CrystalixLanguage.PROPERTY_GHOST_ALLOW.translateFormatted(ChatFormatting.DARK_GREEN)
                            : CrystalixLanguage.PROPERTY_GHOST_DENY.translateFormatted(ChatFormatting.DARK_RED))
                    .append(Component.literal(" "))
                    .append(langEntry.translateFormatted(color));
        }
    }

    public enum Light implements StringRepresentable, ITranslatable {
        NONE(CrystalixLanguage.PROPERTY_LIGHT_NONE, ChatFormatting.GRAY),
        LIGHT(CrystalixLanguage.PROPERTY_LIGHT_LIGHT, ChatFormatting.GOLD),
        DARK(CrystalixLanguage.PROPERTY_LIGHT_DARK, ChatFormatting.DARK_GRAY),
        FAKE_LIGHT(CrystalixLanguage.PROPERTY_LIGHT_FAKE, ChatFormatting.LIGHT_PURPLE),
        ;

        public static final Codec<Light> CODEC = IEnumCodec.makeCodec(Light.class);
        public static final StreamCodec<ByteBuf, Light> STREAM_CODEC = IEnumCodec.makeStreamCodec(Light.class);
        private final ILangEntry langEntry;
        private final ChatFormatting color;
        Light(ILangEntry langEntry, ChatFormatting color) {
            this.langEntry = langEntry;
            this.color = color;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        @Override
        public MutableComponent getTranslation() {
            return CrystalixLanguage.PROPERTY_LIGHT.translateFormatted(ChatFormatting.GRAY)
                    .append(Component.literal(" "))
                    .append(langEntry.translateFormatted(color));
        }
    }
}
