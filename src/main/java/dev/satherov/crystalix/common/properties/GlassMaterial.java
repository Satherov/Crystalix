package dev.satherov.crystalix.common.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.client.lang.CXLanguage;
import dev.satherov.sathlib.client.lang.SLTranslatable;
import dev.satherov.sathlib.common.properties.PropertyEnum;
import dev.satherov.sathlib.core.annotations.NothingNull;
import dev.satherov.sathlib.network.chat.SLComponent;
import dev.satherov.sathlib.network.codec.SLCodec;
import dev.satherov.sathlib.network.codec.SLStreamCodec;
import dev.satherov.sathlib.util.SLStringUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;

@NothingNull
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum GlassMaterial implements StringRepresentable, PropertyEnum {
    // @formatter:off
    NORMAL  (true,  CXLanguage.MATERIAL_NORMAL,   CXLanguage.TOOLTIP_MATERIAL_NORMAL,   ChatFormatting.AQUA),
    CLEAR   (false, CXLanguage.MATERIAL_CLEAR,    CXLanguage.TOOLTIP_MATERIAL_CLEAR,    ChatFormatting.WHITE),
    BORDERED(true,  CXLanguage.MATERIAL_BORDERED, CXLanguage.TOOLTIP_MATERIAL_BORDERED, ChatFormatting.GOLD),
    // @formatter:off
    ;

    public static final Codec<GlassMaterial> CODEC = SLCodec.fromEnum(GlassMaterial.class);
    public static final StreamCodec<ByteBuf, GlassMaterial> STREAM_CODEC = SLStreamCodec.forEnum(GlassMaterial.class);

    private final boolean connected;
    private final SLTranslatable display;
    private final SLTranslatable tooltip;
    private final ChatFormatting color;

    public static GlassMaterial defaultMaterial() {
        return GlassMaterial.NORMAL;
    }

    public Identifier sprite(boolean tinted) {
        final String name = this.getSerializedName();
        final String path = String.format("block/%s", tinted ? name + "_colored" : name);
        return Crystalix.id(path);
    }

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
