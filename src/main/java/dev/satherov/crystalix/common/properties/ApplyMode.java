package dev.satherov.crystalix.common.properties;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import dev.satherov.crystalix.client.lang.CXLanguage;
import dev.satherov.sathlib.client.lang.SLTranslatable;
import dev.satherov.sathlib.common.properties.PropertyEnum;
import dev.satherov.sathlib.core.annotations.NothingNull;
import dev.satherov.sathlib.network.chat.SLComponent;
import dev.satherov.sathlib.network.codec.SLCodec;
import dev.satherov.sathlib.network.codec.SLStreamCodec;
import dev.satherov.sathlib.util.SLStringUtils;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;

@NothingNull
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum ApplyMode implements StringRepresentable, PropertyEnum {
    // @formatter:off
    DEFAULT  (CXLanguage.PROPERTY_APPLY_DEFAULT,   CXLanguage.TOOLTIP_APPLY_DEFAULT),
    COLORLESS(CXLanguage.PROPERTY_APPLY_COLORLESS, CXLanguage.TOOLTIP_APPLY_COLORLESS),
    EXACT    (CXLanguage.PROPERTY_APPLY_EXACT,     CXLanguage.TOOLTIP_APPLY_EXACT),
    // @formatter:on
    ;
    
    public static final Codec<ApplyMode> CODEC = SLCodec.fromEnum(ApplyMode.class);
    public static final StreamCodec<ByteBuf, ApplyMode> STREAM_CODEC = SLStreamCodec.forEnum(ApplyMode.class);
    
    private final SLTranslatable display;
    private final SLTranslatable tooltip;
    
    @Override
    public String getSerializedName() {
        return SLStringUtils.lower(this.name());
    }
    
    @Override
    public SLComponent tooltip() {
        return this.tooltip.translate();
    }
    
    @Override
    public SLComponent display(Object... args) {
        return this.display.translate();
    }
}
