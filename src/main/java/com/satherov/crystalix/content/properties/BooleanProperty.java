package com.satherov.crystalix.content.properties;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import com.satherov.crystalix.core.annotations.NothingNull;
import com.satherov.crystalix.core.lang.CrystalixLanguage;
import com.satherov.crystalix.core.lang.ILangEntry;

@NothingNull
public class BooleanProperty implements ITranslatableProperty<Boolean> {
    private final ItemStack stack;
    private final DataComponentType<Boolean> componentType;
    private final String key;
    private final boolean enabled;
    private final ILangEntry langEntry;
    private boolean value;

    public BooleanProperty(ItemStack stack, DataComponentType<Boolean> componentType, String key, boolean defaultValue, boolean enabled, ILangEntry langEntry) {
        this.stack = stack;
        this.componentType = componentType;
        this.key = key;
        this.enabled = enabled;
        this.langEntry = langEntry;

        value = stack.getOrDefault(componentType, defaultValue);
    }

    public BooleanProperty(ItemStack stack, DataComponentType<Boolean> componentType, String key, boolean defaultValue, ILangEntry langEntry) {
        this(stack, componentType, key, defaultValue, true, langEntry);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValueString() {
        return value ? "enabled" : "disabled";
    }

    @Override
    public void setValueString(String val) {
        set(val.equals("enabled"));
    }

    @Override
    public void set(Boolean val) {
        if (!enabled) return;
        value = val;
        stack.set(componentType, val);
    }

    @Override
    public Boolean get() {
        return value;
    }

    @Override
    public Boolean next(boolean dir) {
        set(!value);
        return value;
    }

    @Override
    public MutableComponent getTranslation() {
        return this.langEntry.translateFormatted(ChatFormatting.GRAY)
                .append(Component.literal(" "))
                .append(value
                        ? CrystalixLanguage.PROPERTY_ENABLED.translateFormatted(ChatFormatting.DARK_GREEN)
                        : CrystalixLanguage.PROPERTY_DISABLED.translateFormatted(ChatFormatting.DARK_RED));
    }
}
