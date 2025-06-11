package com.satherov.crystalix.content.properties;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;

import java.util.function.IntFunction;

public interface IEnumCodec {

    static <E extends Enum<E> & StringRepresentable> Codec<E> makeCodec(Class<E> enumClass) {
        return StringRepresentable.fromEnum(enumClass::getEnumConstants);
    }

    static <E extends Enum<E> & StringRepresentable> StreamCodec<ByteBuf, E> makeStreamCodec(Class<E> enumClass) {
        return ByteBufCodecs.idMapper(makeById(enumClass), E::ordinal);
    }

    private static <E extends Enum<E> & StringRepresentable> IntFunction<E> makeById(Class<E> enumClass) {
        return ByIdMap.continuous(E::ordinal, enumClass.getEnumConstants(), ByIdMap.OutOfBoundsStrategy.CLAMP);
    }
}
