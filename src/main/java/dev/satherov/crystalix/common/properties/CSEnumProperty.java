package dev.satherov.crystalix.common.properties;

import lombok.Getter;
import lombok.experimental.Accessors;

import dev.satherov.crystalix.client.lang.CSTranslatable;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.google.common.base.Suppliers;

import java.util.function.Supplier;

@Accessors(fluent = true)
public class CSEnumProperty<E extends Enum<E> & CSTranslatable> implements IProperty<E> {
    
    private final ItemStack stack;
    private final @Getter ResourceLocation location;
    private final @Getter String translation;
    private final @Getter String key;
    private final Supplier<DataComponentType<E>> type;
    private final Class<E> clazz;
    private @Getter E value;
    
    protected CSEnumProperty(ItemStack stack, CSTranslatable translation, ResourceLocation location, Class<E> clazz, E defaultValue, Supplier<DataComponentType<E>> supplier) {
        this.stack = stack;
        this.location = location;
        this.clazz = clazz;
        this.translation = translation.translation();
        this.key = translation.key();
        this.type = Suppliers.memoize(supplier::get);
        this.value = stack.getOrDefault(this.type, defaultValue);
    }
    
    public static <E extends Enum<E> & CSTranslatable> CSEnumProperty<E> create(ItemStack stack, CSTranslatable translation, ResourceLocation location, Class<E> clazz, E defaultValue, Supplier<DataComponentType<E>> supplier) {
        return new CSEnumProperty<>(stack, translation, location, clazz, defaultValue, supplier);
    }
    
    @Override
    public E next(boolean forward) {
        E[] values = this.clazz.getEnumConstants();
        int i = value.ordinal() + (forward ? 1 : -1);
        if (i < 0) i += values.length;
        return set(values[i % values.length]);
    }
    
    @Override
    public E set(E value) {
        this.value = value;
        stack.set(this.type, this.value);
        return this.value;
    }
    
    @Override
    public DataComponentType<E> type() {
        return this.type.get();
    }
    
    @Override
    public MutableComponent name() {
        return Component.translatable(this.key);
    }
    
    @Override
    public MutableComponent display() {
        return this.value.text();
    }
}
