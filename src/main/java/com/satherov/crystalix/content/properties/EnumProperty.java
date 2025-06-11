package com.satherov.crystalix.content.properties;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import com.google.common.base.Enums;
import com.satherov.crystalix.core.annotations.NothingNull;
import com.satherov.crystalix.core.lang.ITranslatable;

@NothingNull
public class EnumProperty<E extends Enum<E> & ITranslatable> implements ITranslatableValueProperty<E> {
    private final ItemStack stack;
    private final DataComponentType<E> componentType;
    private final String key;
    private final Class<E> enumClass;
    private final boolean enabled;
    private final E defaultValue;
    private E value;

    public EnumProperty(ItemStack stack, DataComponentType<E> componentType, String key, Class<E> enumClass, E defaultValue, boolean enabled) {
        this.stack = stack;
        this.componentType = componentType;
        this.key = key;
        this.enumClass = enumClass;
        this.enabled = enabled;
        this.defaultValue = defaultValue;

        value = stack.getOrDefault(componentType, defaultValue);
    }

    public EnumProperty(ItemStack stack, DataComponentType<E> componentType, String key, Class<E> enumClass, E defaultValue) {
        this(stack, componentType, key, enumClass, defaultValue, true);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValueString() {
        return value.name().toLowerCase();
    }

    @Override
    public void setValueString(String val) {
        set(Enums.getIfPresent(enumClass, val.toUpperCase()).or(defaultValue));
    }

    @Override
    public void set(E val) {
        if (!enabled) return;
        value = val;
        stack.set(componentType, val);
    }

    @Override
    public E get() {
        return value;
    }

    @Override
    public E next(boolean dir) {
        E[] enumValues = enumClass.getEnumConstants();
        int i = value.ordinal() + (dir ? 1 : -1);
        if (i < 0) i += enumValues.length;
        set(enumValues[i % enumValues.length]);
        return value;
    }
}
