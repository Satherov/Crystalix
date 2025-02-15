package com.satherov.crystalix.content.properties;

import com.satherov.crystalix.Crystalix;

import net.minecraft.network.chat.Component;

public interface IProperty<T> {

    String getKey();

    String getValueString();

    void setValueString(String val);

    default String getKeyTranslation() {
        return Crystalix.MOD_ID + ".property." + getKey();
    }

    default String getValueTranslation() {
        return Crystalix.MOD_ID + ".property." + getKey() + "." + getValueString();
    }

    void set(T val);

    T get();

    T next(boolean dir);

    default T next() {
        return next(true);
    }

    Component toComponent();
}
