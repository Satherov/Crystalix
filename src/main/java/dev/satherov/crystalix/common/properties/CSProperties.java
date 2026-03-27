package dev.satherov.crystalix.common.properties;

import lombok.Getter;
import lombok.experimental.Accessors;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.client.lang.CSLanguage;
import dev.satherov.crystalix.client.lang.CSTranslatable;
import dev.satherov.crystalix.core.CSRegistry;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.EntityCollisionContext;

import com.mojang.serialization.Codec;

import org.jetbrains.annotations.NotNull;

import io.netty.buffer.ByteBuf;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

@Accessors(fluent = true)
public final class CSProperties {
    
    public static final ResourceLocation INVISIBLE = Crystalix.rl("invisible");
    public static final ResourceLocation CLEAR = Crystalix.rl("clear");
    public static final ResourceLocation WATERLOGGABLE = Crystalix.rl("waterloggable");
    public static final ResourceLocation REINFORCED = Crystalix.rl("reinforced");
    public static final ResourceLocation SHADELESS = Crystalix.rl("shadeless");
    public static final ResourceLocation REDSTONE = Crystalix.rl("redstone");
    public static final ResourceLocation CONDUCTOR = Crystalix.rl("conductor");
    public static final ResourceLocation GHOST = Crystalix.rl("ghost");
    public static final ResourceLocation LIGHT = Crystalix.rl("light");
    public static final ResourceLocation COLOR = Crystalix.rl("color");
    
    private final @Getter Map<ResourceLocation, IProperty<?>> properties;
    
    private final @Getter CSBooleanProperty waterloggable;
    private final @Getter CSBooleanProperty invisible;
    private final @Getter CSBooleanProperty shadeless;
    private final @Getter CSBooleanProperty redstone;
    private final @Getter CSBooleanProperty conductor;
    private final @Getter CSBooleanProperty reinforced;
    private final @Getter CSBooleanProperty clear;
    private final @Getter CSEnumProperty<Ghost> ghost;
    private final @Getter CSEnumProperty<Light> light;
    private final @Getter CSIntegerProperty color;
    
    private CSProperties(ItemStack stack) {
        this.invisible = CSBooleanProperty.create(stack, CSLanguage.PROPERTY_INVISIBLE, CSLanguage.PROPERTY_INVISIBLE_TOOLTIP, CSProperties.INVISIBLE, false, CSRegistry.INVISIBLE);
        this.waterloggable = CSBooleanProperty.create(stack, CSLanguage.PROPERTY_WATERLOGGABLE, CSLanguage.PROPERTY_WATERLOGGABLE_TOOLTIP, CSProperties.WATERLOGGABLE, false, CSRegistry.WATERLOGGABLE);
        this.clear = CSBooleanProperty.create(stack, CSLanguage.PROPERTY_TRANSPARENT, CSLanguage.PROPERTY_TRANSPARENT_TOOLTIP, CSProperties.CLEAR, true, CSRegistry.TRANSPARENT);
        this.shadeless = CSBooleanProperty.create(stack, CSLanguage.PROPERTY_SHADELESS, CSLanguage.PROPERTY_SHADELESS_TOOLTIP, CSProperties.SHADELESS, false, CSRegistry.SHADELESS);
        this.reinforced = CSBooleanProperty.create(stack, CSLanguage.PROPERTY_REINFORCED, CSLanguage.PROPERTY_REINFORCED_TOOLTIP, CSProperties.REINFORCED, false, CSRegistry.REINFORCED);
        this.redstone = CSBooleanProperty.create(stack, CSLanguage.PROPERTY_REDSTONE, CSLanguage.PROPERTY_REDSTONE_TOOLTIP, CSProperties.REDSTONE, false, CSRegistry.REDSTONE);
        this.conductor = CSBooleanProperty.create(stack, CSLanguage.PROPERTY_CONDUCTOR, CSLanguage.PROPERTY_CONDUCTOR_TOOLTIP, CSProperties.CONDUCTOR, false, CSRegistry.CONDUCTOR);
        this.ghost = CSEnumProperty.create(stack, CSLanguage.PROPERTY_GHOST, CSLanguage.PROPERTY_GHOST_TOOLTIP, CSProperties.GHOST, Ghost.class, Ghost.BLOCK_ALL, CSRegistry.GHOST);
        this.light = CSEnumProperty.create(stack, CSLanguage.PROPERTY_LIGHT, CSLanguage.PROPERTY_LIGHT_TOOLTIP, CSProperties.LIGHT, Light.class, Light.NONE, CSRegistry.LIGHT);
        this.color = CSIntegerProperty.create(stack, CSLanguage.PROPERTY_COLOR, CSLanguage.PROPERTY_COLOR_TOOLTIP, CSProperties.COLOR, 0xFFFFFF, CSRegistry.COLOR);
        
        this.properties = new LinkedHashMap<>() {{
            this.put(CSProperties.INVISIBLE, CSProperties.this.invisible);
            this.put(CSProperties.CLEAR, CSProperties.this.clear);
            this.put(CSProperties.WATERLOGGABLE, CSProperties.this.waterloggable);
            this.put(CSProperties.REINFORCED, CSProperties.this.reinforced);
            this.put(CSProperties.SHADELESS, CSProperties.this.shadeless);
            this.put(CSProperties.REDSTONE, CSProperties.this.redstone);
            this.put(CSProperties.CONDUCTOR, CSProperties.this.conductor);
            this.put(CSProperties.GHOST, CSProperties.this.ghost);
            this.put(CSProperties.LIGHT, CSProperties.this.light);
            this.put(CSProperties.COLOR, CSProperties.this.color);
        }};
    }
    
    public static CSProperties of(ItemStack stack) {
        return new CSProperties(stack);
    }
    
    public IProperty<?> get(int index) {
        return this.properties.values().toArray(IProperty<?>[]::new)[index];
    }
    
    @Accessors(fluent = true)
    public enum Ghost implements StringRepresentable, CSTranslatable {
        BLOCK_ALL(CSLanguage.PROPERTY_GHOST_ALL, ChatFormatting.DARK_GRAY, false, context -> false),
        ALLOW_ALL(CSLanguage.PROPERTY_GHOST_ALL, ChatFormatting.DARK_GRAY, true, context -> true),
        BLOCK_PLAYER(CSLanguage.PROPERTY_GHOST_PLAYER, ChatFormatting.AQUA, false, context -> !(context.getEntity() instanceof Player)),
        ALLOW_PLAYER(CSLanguage.PROPERTY_GHOST_PLAYER, ChatFormatting.AQUA, true, context -> context.getEntity() instanceof Player),
        BLOCK_MONSTER(CSLanguage.PROPERTY_GHOST_MONSTER, ChatFormatting.RED, false, context -> !(context.getEntity() instanceof Monster)),
        ALLOW_MONSTER(CSLanguage.PROPERTY_GHOST_MONSTER, ChatFormatting.RED, true, context -> context.getEntity() instanceof Monster),
        BLOCK_ANIMAL(CSLanguage.PROPERTY_GHOST_ANIMAL, ChatFormatting.GREEN, false, context -> !(context.getEntity() instanceof Animal)),
        ALLOW_ANIMAL(CSLanguage.PROPERTY_GHOST_ANIMAL, ChatFormatting.GREEN, true, context -> context.getEntity() instanceof Animal),
        BLOCK_ADULT(CSLanguage.PROPERTY_GHOST_ADULT, ChatFormatting.LIGHT_PURPLE, false, context -> context.getEntity() instanceof LivingEntity mob && mob.isBaby() || context.getEntity() instanceof Tadpole),
        ALLOW_ADULT(CSLanguage.PROPERTY_GHOST_ADULT, ChatFormatting.LIGHT_PURPLE, true, context -> context.getEntity() instanceof LivingEntity mob && !mob.isBaby()),
        ;
        
        public static final Codec<Ghost> CODEC = EnumCodecs.makeCodec(Ghost.class);
        public static final StreamCodec<ByteBuf, Ghost> STREAM_CODEC = EnumCodecs.makeStreamCodec(Ghost.class);
        
        private final @Getter String key;
        private final @Getter String translation;
        private final @Getter ChatFormatting color;
        private final boolean state;
        private final Predicate<EntityCollisionContext> collisionPredicate;
        
        Ghost(CSLanguage language, ChatFormatting color, boolean state, Predicate<EntityCollisionContext> collisionPredicate) {
            this.key = language.key();
            this.translation = language.translation();
            this.color = color;
            this.state = state;
            this.collisionPredicate = collisionPredicate;
        }
        
        public boolean canCollide(EntityCollisionContext context) {
            return this.collisionPredicate.test(context);
        }
        
        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
        
        @Override
        public MutableComponent text() {
            MutableComponent component = Component.empty();
            component.append(this.state ? CSLanguage.PROPERTY_ALLOW.text(ChatFormatting.GREEN) : CSLanguage.PROPERTY_DENY.text(ChatFormatting.RED));
            component.append(" ");
            component.append(CSTranslatable.super.text(this.color));
            return component;
        }
    }
    
    @Accessors(fluent = true)
    public enum Light implements StringRepresentable, CSTranslatable {
        NONE(CSLanguage.PROPERTY_LIGHT_NONE, ChatFormatting.GRAY),
        LIGHT(CSLanguage.PROPERTY_LIGHT_LIGHT, ChatFormatting.GOLD),
        FAKE_LIGHT(CSLanguage.PROPERTY_LIGHT_FAKE_LIGHT, ChatFormatting.LIGHT_PURPLE),
        DARK(CSLanguage.PROPERTY_LIGHT_DARK, ChatFormatting.DARK_GRAY),
        FAKE_DARK(CSLanguage.PROPERTY_LIGHT_FAKE_DARK, ChatFormatting.BLUE),
        ;
        
        public static final Codec<Light> CODEC = EnumCodecs.makeCodec(Light.class);
        public static final StreamCodec<ByteBuf, Light> STREAM_CODEC = EnumCodecs.makeStreamCodec(Light.class);
        
        private final @Getter String key;
        private final @Getter String translation;
        private final @Getter ChatFormatting color;
        
        Light(CSLanguage language, ChatFormatting color) {
            this.key = language.key();
            this.translation = language.translation();
            this.color = color;
        }
        
        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
        
        @Override
        public MutableComponent text() {
            return CSTranslatable.super.text(this.color);
        }
    }
}
