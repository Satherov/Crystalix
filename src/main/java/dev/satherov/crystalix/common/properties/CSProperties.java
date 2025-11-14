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
import net.minecraft.world.entity.animal.Animal;
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
    public static final ResourceLocation WATERLOGGABLE = Crystalix.rl("waterloggable");
    public static final ResourceLocation SHADELESS = Crystalix.rl("shadeless");
    public static final ResourceLocation REINFORCED = Crystalix.rl("reinforced");
    public static final ResourceLocation GHOST = Crystalix.rl("ghost");
    public static final ResourceLocation LIGHT = Crystalix.rl("light");
    public static final ResourceLocation COLOR = Crystalix.rl("color");
    
    private final @Getter Map<ResourceLocation, IProperty<?>> properties;
    
    private final @Getter CSBooleanProperty waterloggable;
    private final @Getter CSBooleanProperty invisible;
    private final @Getter CSBooleanProperty shadeless;
    private final @Getter CSBooleanProperty reinforced;
    private final @Getter CSEnumProperty<Ghost> ghost;
    private final @Getter CSEnumProperty<Light> light;
    private final @Getter CSEnumProperty<CSRegistry.Colors> color;
    
    private CSProperties(ItemStack stack) {
        invisible = CSBooleanProperty.create(stack, CSLanguage.PROPERTY_INVISIBLE, CSProperties.INVISIBLE, false, CSRegistry.INVISIBLE);
        waterloggable = CSBooleanProperty.create(stack, CSLanguage.PROPERTY_WATERLOGGABLE, CSProperties.WATERLOGGABLE, false, CSRegistry.WATERLOGGABLE);
        shadeless = CSBooleanProperty.create(stack, CSLanguage.PROPERTY_SHADELESS, CSProperties.SHADELESS, false, CSRegistry.SHADELESS);
        reinforced = CSBooleanProperty.create(stack, CSLanguage.PROPERTY_REINFORCED, CSProperties.REINFORCED, false, CSRegistry.REINFORCED);
        ghost = CSEnumProperty.create(stack, CSLanguage.PROPERTY_GHOST, CSProperties.GHOST, Ghost.class, Ghost.BLOCK_ALL, CSRegistry.GHOST);
        light = CSEnumProperty.create(stack, CSLanguage.PROPERTY_LIGHT, CSProperties.LIGHT, Light.class, Light.NONE, CSRegistry.LIGHT);
        color = CSEnumProperty.create(stack, CSLanguage.PROPERTY_COLOR, CSProperties.COLOR, CSRegistry.Colors.class, CSRegistry.Colors.CLEAR, CSRegistry.COLOR);
        
        properties = new LinkedHashMap<>() {{
            put(CSProperties.INVISIBLE, invisible);
            put(CSProperties.WATERLOGGABLE, waterloggable);
            put(CSProperties.REINFORCED, reinforced);
            put(CSProperties.SHADELESS, shadeless);
            put(CSProperties.GHOST, ghost);
            put(CSProperties.LIGHT, light);
            put(CSProperties.COLOR, color);
        }};
    }
    
    public static CSProperties of(ItemStack stack) {
        return new CSProperties(stack);
    }
    
    public IProperty<?> get(int index) {
        return properties.values().toArray(new IProperty[0])[index];
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
        ALLOW_ANIMAL(CSLanguage.PROPERTY_GHOST_ANIMAL, ChatFormatting.GREEN, true, context -> context.getEntity() instanceof Animal);
        
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
        DARK(CSLanguage.PROPERTY_LIGHT_DARK, ChatFormatting.DARK_GRAY),
        FAKE_LIGHT(CSLanguage.PROPERTY_LIGHT_FAKE, ChatFormatting.LIGHT_PURPLE),
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
