package dev.satherov.crystalix.common.properties;


import lombok.Getter;
import lombok.experimental.Accessors;

import dev.satherov.crystalix.client.lang.CSLanguage;
import dev.satherov.crystalix.client.lang.CSTranslatable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.google.common.base.Suppliers;

import java.util.function.Supplier;

@Accessors(fluent = true)
public class CSBooleanProperty implements IProperty<Boolean> {
    
    private final ItemStack stack;
    private final @Getter ResourceLocation location;
    private final @Getter String translation;
    private final @Getter String key;
    private final Supplier<DataComponentType<Boolean>> type;
    private @Getter Boolean value;
    
    protected CSBooleanProperty(ItemStack stack, CSTranslatable translation, ResourceLocation location, boolean defaultValue, Supplier<DataComponentType<Boolean>> supplier) {
        this.stack = stack;
        this.location = location;
        this.translation = translation.translation();
        this.key = translation.key();
        this.type = Suppliers.memoize(supplier::get);
        this.value = stack.getOrDefault(this.type, defaultValue);
    }
    
    public static CSBooleanProperty create(ItemStack stack, CSTranslatable translation, ResourceLocation location, boolean defaultValue, Supplier<DataComponentType<Boolean>> supplier) {
        return new CSBooleanProperty(stack, translation, location, defaultValue, supplier);
    }
    
    @Override
    public Boolean next(boolean forward) {
        return set(!this.value);
    }
    
    @Override
    public Boolean set(Boolean value) {
        this.value = value;
        stack.set(this.type, this.value);
        return this.value;
    }
    
    @Override
    public DataComponentType<Boolean> type() {
        return this.type.get();
    }
    
    @Override
    public MutableComponent name() {
        return Component.translatable(this.key);
    }
    
    public MutableComponent display() {
        return this.value ? CSLanguage.PROPERTY_ENABLED.text(ChatFormatting.DARK_GREEN) : CSLanguage.PROPERTY_DISABLED.text(ChatFormatting.DARK_RED);
    }
}
