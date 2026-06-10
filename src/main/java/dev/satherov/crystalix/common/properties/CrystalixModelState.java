package dev.satherov.crystalix.common.properties;

import lombok.AllArgsConstructor;
import lombok.Data;

import dev.satherov.sathlib.client.model.data.SLModelPropertyField;
import dev.satherov.sathlib.client.model.data.SLModelPropertyFieldHolder;
import dev.satherov.sathlib.client.model.data.SLModelPropertyValue;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

@Data
@AllArgsConstructor
public class CrystalixModelState implements SLModelPropertyValue {
    boolean shadeless;
    boolean tinted;
    GlassMaterial material;
    
    public static final SLModelPropertyField<Boolean> SHADELESS = new SLModelPropertyField<>("shadeless", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final SLModelPropertyField<Boolean> TINTED = new SLModelPropertyField<>("tinted", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final SLModelPropertyField<GlassMaterial> MATERIAL = new SLModelPropertyField<>("material", GlassMaterial.CODEC, GlassMaterial.STREAM_CODEC);
    
    public static CrystalixModelState empty() {
        return new CrystalixModelState(false, false, GlassMaterial.defaultMaterial());
    }
    
    public static final Codec<CrystalixModelState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("shadeless").forGetter(CrystalixModelState::isShadeless),
            Codec.BOOL.fieldOf("tinted").forGetter(CrystalixModelState::isTinted),
            GlassMaterial.CODEC.fieldOf("material").forGetter(CrystalixModelState::getMaterial)
    ).apply(instance, CrystalixModelState::new));
    
    public static final StreamCodec<FriendlyByteBuf, CrystalixModelState> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, CrystalixModelState::isShadeless,
            ByteBufCodecs.BOOL, CrystalixModelState::isTinted,
            GlassMaterial.STREAM_CODEC, CrystalixModelState::getMaterial,
            CrystalixModelState::new
    );
    
    @Override
    public Codec<CrystalixModelState> codec() {
        return CrystalixModelState.CODEC;
    }
    
    @Override
    public StreamCodec<? extends FriendlyByteBuf, CrystalixModelState> streamCodec() {
        return CrystalixModelState.STREAM_CODEC;
    }
    
    @Override
    public SLModelPropertyFieldHolder asHolder() {
        return SLModelPropertyFieldHolder.builder()
                .put(CrystalixModelState.SHADELESS, this.shadeless)
                .put(CrystalixModelState.TINTED, this.tinted)
                .put(CrystalixModelState.MATERIAL, this.material)
                .build();
    }
}
