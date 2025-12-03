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
public class CSIntegerProperty implements IProperty<Integer> {
    
    private final ItemStack stack;
    private final @Getter ResourceLocation location;
    private final @Getter String translation;
    private final @Getter String key;
    private final Supplier<DataComponentType<Integer>> type;
    private @Getter Integer value;
    
    protected CSIntegerProperty(ItemStack stack, CSTranslatable translation, ResourceLocation location, int defaultValue, Supplier<DataComponentType<Integer>> supplier) {
        this.stack = stack;
        this.location = location;
        this.translation = translation.translation();
        this.key = translation.key();
        this.type = Suppliers.memoize(supplier::get);
        this.value = stack.getOrDefault(this.type, defaultValue);
    }
    
    public static CSIntegerProperty create(ItemStack stack, CSTranslatable translation, ResourceLocation location, int defaultValue, Supplier<DataComponentType<Integer>> supplier) {
        return new CSIntegerProperty(stack, translation, location, defaultValue, supplier);
    }
    
    @Override
    public DataComponentType<Integer> type() {
        return this.type.get();
    }
    
    @Override
    public Integer next(boolean forward) {
        return (this.value + (forward ? 1 : -1)) & 0xFFFFFF;
    }
    
    @Override
    public Integer set(Integer value) {
        this.value = value;
        stack.set(this.type, this.value);
        return this.value;
    }
    
    @Override
    public MutableComponent name() {
        return Component.translatable(this.key);
    }
    
    @Override
    public MutableComponent display() {
        return Component.literal("#" + Integer.toHexString(this.value).toUpperCase()).withColor(this.value);
    }
}
