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
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;

@NothingNull
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum LightState implements StringRepresentable, PropertyEnum {
    // @formatter:off
    NONE      (GenericLang.NONE,                     CXLanguage.TOOLTIP_LIGHT_NONE,       ChatFormatting.GRAY),
    LIGHT     (CXLanguage.PROPERTY_LIGHT_LIGHT,      CXLanguage.TOOLTIP_LIGHT_LIGHT,      ChatFormatting.GOLD),
    DARK      (CXLanguage.PROPERTY_LIGHT_DARK,       CXLanguage.TOOLTIP_LIGHT_DARK,       ChatFormatting.DARK_GRAY),
    FAKE_LIGHT(CXLanguage.PROPERTY_LIGHT_FAKE_LIGHT, CXLanguage.TOOLTIP_LIGHT_FAKE_LIGHT, ChatFormatting.LIGHT_PURPLE),
    FAKE_DARK (CXLanguage.PROPERTY_LIGHT_FAKE_DARK,  CXLanguage.TOOLTIP_LIGHT_FAKE_DARK,  ChatFormatting.BLUE),
    // @formatter:on
    ;
    
    public static final Codec<LightState> CODEC = SLCodec.fromEnum(LightState.class);
    public static final StreamCodec<ByteBuf, LightState> STREAM_CODEC = SLStreamCodec.forEnum(LightState.class);
    
    private final SLTranslatable display;
    private final SLTranslatable tooltip;
    private final ChatFormatting color;
    
    @Override
    public String getSerializedName() {
        return SLStringUtils.lower(this.name());
    }
    
    @Override
    public SLComponent display(Object... args) {
        return this.display.translate(this.color);
    }
    
    @Override
    public SLComponent tooltip() {
        return this.tooltip.translate();
    }
}
