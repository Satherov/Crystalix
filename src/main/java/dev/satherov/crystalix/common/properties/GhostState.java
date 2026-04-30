package dev.satherov.crystalix.common.properties;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import dev.satherov.crystalix.client.lang.CXLanguage;
import dev.satherov.sathlib.client.lang.GenericLang;
import dev.satherov.sathlib.client.lang.SLTranslatable;
import dev.satherov.sathlib.common.properties.PropertyEnum;
import dev.satherov.sathlib.core.annotations.NothingNull;
import dev.satherov.sathlib.network.chat.SLComponent;
import dev.satherov.sathlib.network.codec.SLCodec;
import dev.satherov.sathlib.network.codec.SLStreamCodec;
import dev.satherov.sathlib.util.SLStringUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.shapes.EntityCollisionContext;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;

import java.util.function.Predicate;

@NothingNull
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum GhostState implements StringRepresentable, PropertyEnum {
    // @formatter:off
    BLOCK_ALL    (GenericLang.ALL,                   CXLanguage.TOOLTIP_GHOST_NONE,          ChatFormatting.DARK_GRAY,      false, _ -> false),
    ALLOW_ALL    (GenericLang.ALL,                   CXLanguage.TOOLTIP_GHOST_ALL,           ChatFormatting.DARK_GRAY,      true,  _ -> true),
    BLOCK_PLAYER (CXLanguage.PROPERTY_GHOST_PLAYER,  CXLanguage.TOOLTIP_GHOST_BLOCK_PLAYER,  ChatFormatting.AQUA,           false, context -> !(context.getEntity() instanceof Player)),
    ALLOW_PLAYER (CXLanguage.PROPERTY_GHOST_PLAYER,  CXLanguage.TOOLTIP_GHOST_ALLOW_PLAYER,  ChatFormatting.AQUA,           true,  context ->   context.getEntity() instanceof Player),
    BLOCK_MONSTER(CXLanguage.PROPERTY_GHOST_MONSTER, CXLanguage.TOOLTIP_GHOST_BLOCK_MONSTER, ChatFormatting.RED,            false, context -> !(context.getEntity() instanceof Monster)),
    ALLOW_MONSTER(CXLanguage.PROPERTY_GHOST_MONSTER, CXLanguage.TOOLTIP_GHOST_ALLOW_MONSTER, ChatFormatting.RED,            true,  context ->   context.getEntity() instanceof Monster),
    BLOCK_ANIMAL (CXLanguage.PROPERTY_GHOST_ANIMAL,  CXLanguage.TOOLTIP_GHOST_BLOCK_ANIMAL,  ChatFormatting.GREEN,          false, context -> !(context.getEntity() instanceof Animal)),
    ALLOW_ANIMAL (CXLanguage.PROPERTY_GHOST_ANIMAL,  CXLanguage.TOOLTIP_GHOST_ALLOW_ANIMAL,  ChatFormatting.GREEN,          true,  context ->   context.getEntity() instanceof Animal),
    BLOCK_ADULT  (CXLanguage.PROPERTY_GHOST_ADULT,   CXLanguage.TOOLTIP_GHOST_BLOCK_ADULT,   ChatFormatting.LIGHT_PURPLE,   false, context ->   context.getEntity() instanceof LivingEntity mob &&  mob.isBaby() ||  context.getEntity() instanceof Tadpole),
    ALLOW_ADULT  (CXLanguage.PROPERTY_GHOST_ADULT,   CXLanguage.TOOLTIP_GHOST_ALLOW_ADULT,   ChatFormatting.LIGHT_PURPLE,   true,  context ->    context.getEntity() instanceof LivingEntity mob && !mob.isBaby() && !(context.getEntity() instanceof Tadpole)),
    // @formatter:on
    ;
    
    public static final Codec<GhostState> CODEC = SLCodec.fromEnum(GhostState.class);
    public static final StreamCodec<ByteBuf, GhostState> STREAM_CODEC = SLStreamCodec.forEnum(GhostState.class);
    
    private final SLTranslatable display;
    private final SLTranslatable tooltip;
    private final ChatFormatting color;
    private final boolean allowed;
    private final Predicate<EntityCollisionContext> predicate;
    
    @Override
    public String getSerializedName() {
        return SLStringUtils.lower(this.name());
    }
    
    public boolean test(EntityCollisionContext context) {
        return this.predicate.test(context);
    }
    
    @Override
    public SLComponent display(Object... args) {
        return SLComponent.of(Component.empty()
                .append(SLComponent.allowedDenied(this.allowed))
                .append(" ")
                .append(this.display.translate(this.color)));
    }
    
    @Override
    public SLComponent tooltip() {
        return this.tooltip.translate();
    }
}
