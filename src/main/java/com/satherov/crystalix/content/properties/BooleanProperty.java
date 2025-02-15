package com.satherov.crystalix.content.properties;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class BooleanProperty implements IProperty<Boolean> {
    private final ItemStack stack;
    private final DataComponentType<Boolean> componentType;
    private final String key;
    private final boolean enabled;
    private boolean value;

    public BooleanProperty(ItemStack stack, DataComponentType<Boolean> componentType, String key, boolean defaultValue, boolean enabled) {
        this.stack = stack;
        this.componentType = componentType;
        this.key = key;
        this.enabled = enabled;

        value = stack.getOrDefault(componentType, defaultValue);
    }

    public BooleanProperty(ItemStack stack, DataComponentType<Boolean> componentType, String key, boolean defaultValue) {
        this(stack, componentType, key, defaultValue, true);
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
    public Component toComponent() {
        return Component.translatable(this.getKeyTranslation()).withStyle(ChatFormatting.GRAY)
                .append(Component.literal(": "))
                .append(Component.translatable(this.getValueTranslation()).withStyle(this.get() ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED));
    }
}
