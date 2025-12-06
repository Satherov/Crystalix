package dev.satherov.crystalix.client.renderer;

import dev.satherov.crystalix.common.item.CrystalixWand;
import dev.satherov.crystalix.core.CSRegistry;
import dev.satherov.crystalix.core.network.CSNetwork;
import dev.satherov.crystalix.core.network.SetColorPayload;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.platform.NativeImage;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class ColorPickerScreen extends Screen {
    
    private static final int BORDER = 1;
    private static final int PADDING = 6;
    private static final int PANEL_PADDING = 10;
    private static final int SV_SIZE = 180;
    private static final int HUE_WIDTH = 18;
    private static final int PREVIEW_HEIGHT = 18;
    private static final int TEXT_HEIGHT = 16;
    
    private boolean isDraggingColor = false;
    private boolean isDraggingHue = false;
    
    private float hue = 0.0f;
    private float saturation = 1.0f;
    private float value = 1.0f;
    
    private DynamicTexture hueTexture;
    private ResourceLocation hueTextureId;
    private boolean isHueBuilt = false;
    
    private DynamicTexture svTexture;
    private ResourceLocation svTextureId;
    private float svCache = 0xFFFFFF;
    private boolean isSvDirty = false;
    
    private EditBox textbox;
    private boolean isUpdating = false;
    
    protected ColorPickerScreen() {
        super(Component.literal("Color Selection"));
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    private int width() {
        return ColorPickerScreen.PANEL_PADDING + ColorPickerScreen.SV_SIZE + ColorPickerScreen.PADDING + ColorPickerScreen.HUE_WIDTH + ColorPickerScreen.PANEL_PADDING;
    }
    
    private int height() {
        return ColorPickerScreen.PANEL_PADDING + ColorPickerScreen.SV_SIZE + ColorPickerScreen.PADDING + ColorPickerScreen.PREVIEW_HEIGHT + ColorPickerScreen.PADDING + ColorPickerScreen.TEXT_HEIGHT + ColorPickerScreen.PANEL_PADDING;
    }
    
    private int svX() {
        return (this.width - this.width()) / 2 + ColorPickerScreen.PANEL_PADDING;
    }
    
    private int svY() {
        return (this.height - this.height()) / 2 + ColorPickerScreen.PANEL_PADDING;
    }
    
    private int hueX() {
        return this.svX() + ColorPickerScreen.SV_SIZE + ColorPickerScreen.PADDING;
    }
    
    private int hueY() {
        return this.svY();
    }
    
    @Override
    @SuppressWarnings("UnnecessaryLocalVariable")
    protected void init() {
        if (Minecraft.getInstance().player == null) return;
        ItemStack wand = CrystalixWand.find(Minecraft.getInstance().player);
        if (wand.isEmpty()) return;
        Integer color = wand.get(CSRegistry.COLOR);
        if (color != null) this.rgb(color);
        
        int x0 = (this.width - this.width()) / 2;
        int svX = x0 + ColorPickerScreen.PANEL_PADDING;
        int hueX = svX + ColorPickerScreen.SV_SIZE + ColorPickerScreen.PADDING;
        int previewY = ((this.height - this.height()) / 2 + ColorPickerScreen.PANEL_PADDING) + ColorPickerScreen.SV_SIZE + ColorPickerScreen.PADDING;
        int textY = previewY + 20 + 4;
        int fieldX1 = svX;
        int fieldW = (hueX + ColorPickerScreen.HUE_WIDTH) - svX;
        
        this.textbox = new EditBox(this.font, fieldX1 + 1, textY, fieldW - 2, ColorPickerScreen.TEXT_HEIGHT, Component.literal("Hex")) {
            
            @Override
            public void setFocused(boolean focused) {
                boolean old = this.isFocused();
                super.setFocused(focused);
                if (old && !focused) {
                    ColorPickerScreen.this.updateTextbox();
                    CSNetwork.sendToServer(new SetColorPayload(ColorPickerScreen.this.rgb()));
                }
            }
        };
        
        this.textbox.setMaxLength(7);
        this.textbox.setBordered(true);
        this.textbox.setEditable(true);
        this.textbox.setValue(this.hex(this.rgb()));
        this.textbox.setResponder(this::textBoxResponder);
        this.addRenderableWidget(this.textbox);
    }
    
    @Override
    @SuppressWarnings("UnnecessaryLocalVariable")
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        graphics.fill(0, 0, this.width, this.height, 0x88000000);
        super.render(graphics, mouseX, mouseY, partial);
        
        int x0 = (this.width - this.width()) / 2;
        int y0 = (this.height - this.height()) / 2;
        
        graphics.fill(x0 - ColorPickerScreen.PADDING, y0 - ColorPickerScreen.PADDING, x0 + this.width() + ColorPickerScreen.PADDING, y0 + this.height() + ColorPickerScreen.PADDING, 0xF0151515);
        this.border(graphics, x0 - ColorPickerScreen.PADDING, y0 - ColorPickerScreen.PADDING, x0 + this.width() + ColorPickerScreen.PADDING, y0 + this.height() + ColorPickerScreen.PADDING, 0xFFFFFFFF);
        
        int svX = x0 + ColorPickerScreen.PANEL_PADDING;
        int svY = y0 + ColorPickerScreen.PANEL_PADDING;
        
        this.updateSV();
        
        if (this.svTextureId != null) {
            graphics.blit(this.svTextureId, svX, svY, 0, 0, ColorPickerScreen.SV_SIZE, ColorPickerScreen.SV_SIZE, ColorPickerScreen.SV_SIZE, ColorPickerScreen.SV_SIZE);
            this.border(graphics, svX, svY, svX + ColorPickerScreen.SV_SIZE, svY + ColorPickerScreen.SV_SIZE, 0xFF000000);
        }
        
        int cursorX = svX + Math.round(this.saturation * (ColorPickerScreen.SV_SIZE - 1));
        int cursorY = svY + Math.round((1.0f - this.value) * (ColorPickerScreen.SV_SIZE - 1));
        
        this.drawCursor(graphics, cursorX, cursorY);
        
        int hueX = svX + ColorPickerScreen.SV_SIZE + ColorPickerScreen.PADDING;
        int hueY = svY;
        
        this.updateHue();
        
        if (this.hueTextureId != null) {
            graphics.blit(this.hueTextureId, hueX, hueY, 0, 0, ColorPickerScreen.HUE_WIDTH, ColorPickerScreen.SV_SIZE, ColorPickerScreen.HUE_WIDTH, ColorPickerScreen.SV_SIZE);
            this.border(graphics, hueX, hueY, hueX + ColorPickerScreen.HUE_WIDTH, hueY + ColorPickerScreen.SV_SIZE, 0xFF000000);
        }
        
        int barY = hueY + Math.round(this.hue * (ColorPickerScreen.SV_SIZE - 1));
        int barX = hueX;
        
        this.drawSelector(graphics, barX, barY);
        
        int previewY = svY + ColorPickerScreen.SV_SIZE + ColorPickerScreen.PADDING;
        int previewH = ColorPickerScreen.PREVIEW_HEIGHT;
        int previewX1 = svX;
        int previewX2 = hueX + ColorPickerScreen.HUE_WIDTH;
        
        graphics.fill(previewX1, previewY, previewX2, previewY + previewH, 0xFF000000 | this.rgb());
        this.border(graphics, previewX1, previewY, previewX2, previewY + previewH, 0xFF000000);
        
        int textY = previewY + previewH + 4;
        if (this.textbox != null) {
            int fieldX1 = svX;
            int fieldW = (hueX + ColorPickerScreen.HUE_WIDTH) - svX;
            this.textbox.setX(fieldX1 + 1);
            this.textbox.setY(textY);
            this.textbox.setWidth(fieldW - 2);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int svX = this.svX();
        int svY = this.svY();
        int hueX = this.hueX();
        int hueY = this.hueY();
        
        this.textbox.setFocused(this.textbox.isHovered());
        
        if (mouseX >= svX && mouseX <= svX + ColorPickerScreen.SV_SIZE && mouseY >= svY && mouseY <= svY + ColorPickerScreen.SV_SIZE) {
            this.isDraggingColor = true;
            this.updateSV((int) mouseX, (int) mouseY);
            this.updateTextbox();
            return true;
        }
        
        if (mouseX >= hueX && mouseX <= hueX + ColorPickerScreen.HUE_WIDTH && mouseY >= hueY && mouseY <= hueY + ColorPickerScreen.SV_SIZE) {
            this.isDraggingHue = true;
            this.updateHue((int) mouseY);
            this.updateTextbox();
            return true;
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (this.isDraggingColor) {
            this.updateSV((int) mouseX, (int) mouseY);
            this.isSvDirty = true;
            this.updateTextbox();
        }
        
        if (this.isDraggingHue) {
            this.updateHue((int) mouseY);
            this.isSvDirty = true;
            this.updateTextbox();
        }
        
        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean wasDragging = this.isDraggingColor || this.isDraggingHue;
        this.isDraggingColor = false;
        this.isDraggingHue = false;
        
        if (wasDragging) {
            CSNetwork.sendToServer(new SetColorPayload(this.rgb()));
            return true;
        }
        
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER && this.textbox.isFocused()) {
            String value = this.textbox.getValue();
            this.textBoxResponder(value);
            CSNetwork.sendToServer(new SetColorPayload(this.rgb()));
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    private void textBoxResponder(String content) {
        if (this.isUpdating) return;
        if (content == null) return;
        
        String text = content.trim();
        if (text.startsWith("#")) text = text.substring(1);
        
        StringBuilder filtered = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')) filtered.append(c);
        }
        
        StringBuilder result = new StringBuilder(filtered.toString());
        if (result.length() > 6) result = new StringBuilder(result.substring(0, 6));
        while (result.length() < 6) result.append('F');
        
        String hex = result.toString().toUpperCase();
        int rgb = Integer.parseInt(hex, 16) & 0xFFFFFF;
        this.rgb(rgb);
    }
    
    private void border(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        graphics.fill(x1, y1, x2, y1 + ColorPickerScreen.BORDER, color);
        graphics.fill(x1, y2 - ColorPickerScreen.BORDER, x2, y2, color);
        graphics.fill(x1, y1, x1 + ColorPickerScreen.BORDER, y2, color);
        graphics.fill(x2 - ColorPickerScreen.BORDER, y1, x2, y2, color);
    }
    
    private int rgb() {
        return Mth.hsvToRgb(this.hue, this.saturation, this.value) & 0xFFFFFF;
    }
    
    private void rgb(int rgb) {
        float r = ((rgb >> 16) & 0xFF) / 255.0f;
        float g = ((rgb >> 8) & 0xFF) / 255.0f;
        float b = (rgb & 0xFF) / 255.0f;
        
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;
        
        this.value = max;
        this.saturation = max == 0.0f ? 0.0f : (delta / max);
        
        float h;
        if (delta == 0.0f) h = 0.0f;
        else if (max == r) h = ((g - b) / delta) % 6.0f;
        else if (max == g) h = ((b - r) / delta) + 2.0f;
        else h = ((r - g) / delta) + 4.0f;
        
        h /= 6.0f;
        if (h < 0.0f) h += 1.0f;
        
        this.hue = Mth.clamp(h, 0.0f, 1.0f);
        this.isSvDirty = true;
    }
    
    private String hex(int rgb) {
        return String.format("#%06X", (0xFFFFFF & rgb));
    }
    
    private void updateHue() {
        if (this.isHueBuilt && this.hueTexture != null && this.hueTextureId != null) return;
        
        this.hueTexture = new DynamicTexture(ColorPickerScreen.HUE_WIDTH, ColorPickerScreen.SV_SIZE, true);
        NativeImage image = this.hueTexture.getPixels();
        if (image != null) {
            
            for (int y = 0; y < ColorPickerScreen.SV_SIZE; y++) {
                float hue = y / (float) (ColorPickerScreen.SV_SIZE - 1);
                int rgb = Mth.hsvToRgb(hue, 1.0f, 1.0f) & 0xFFFFFF;
                int abgr = 0xFF000000 | (rgb & 0xFF) << 16 | rgb & 0xFF00 | (rgb >> 16) & 0xFF;
                
                for (int x = 0; x < ColorPickerScreen.HUE_WIDTH; x++) {
                    image.setPixelRGBA(x, y, abgr);
                }
            }
            
            this.hueTexture.upload();
        }
        
        this.hueTextureId = Minecraft.getInstance().getTextureManager().register("crystalix/color_picker/hue", this.hueTexture);
        this.isHueBuilt = true;
    }
    
    private void updateHue(int mouseY) {
        int hueY = this.hueY();
        
        float h = (mouseY - hueY) / (float) (ColorPickerScreen.SV_SIZE - 1);
        float hue = Mth.clamp(h, 0.0f, 1.0f);
        
        if (hue != this.hue) {
            this.hue = hue;
            this.isSvDirty = true;
        }
    }
    
    private void updateSV() {
        if (!this.isSvDirty && this.svTexture != null && this.svTextureId != null && this.svCache == this.hue) return;
        if (this.svTexture == null) this.svTexture = new DynamicTexture(ColorPickerScreen.SV_SIZE, ColorPickerScreen.SV_SIZE, true);
        
        NativeImage image = this.svTexture.getPixels();
        if (image != null) {
            
            for (int y = 0; y < ColorPickerScreen.SV_SIZE; y++) {
                float value = 1.0f - (y / (float) (ColorPickerScreen.SV_SIZE - 1));
                
                for (int x = 0; x < ColorPickerScreen.SV_SIZE; x++) {
                    float saturation = x / (float) (ColorPickerScreen.SV_SIZE - 1);
                    int rgb = Mth.hsvToRgb(this.hue, saturation, value) & 0xFFFFFF;
                    int abgr = 0xFF000000 | (rgb & 0xFF) << 16 | rgb & 0xFF00 | (rgb >> 16) & 0xFF;
                    image.setPixelRGBA(x, y, abgr);
                }
            }
            
            this.svTexture.upload();
        }
        
        if (this.svTextureId == null) this.svTextureId = Minecraft.getInstance().getTextureManager().register("crystalix/color_picker/sv", this.svTexture);
        
        this.svCache = this.hue;
        this.isSvDirty = false;
    }
    
    private void updateSV(int mouseX, int mouseY) {
        int svX = this.svX();
        int svY = this.svY();
        
        float saturation = (mouseX - svX) / (float) (ColorPickerScreen.SV_SIZE - 1);
        float value = 1.0f - (mouseY - svY) / (float) (ColorPickerScreen.SV_SIZE - 1);
        
        this.saturation = Mth.clamp(saturation, 0.0f, 1.0f);
        this.value = Mth.clamp(value, 0.0f, 1.0f);
    }
    
    private void updateTextbox() {
        if (this.textbox == null) return;
        this.isUpdating = true;
        
        try {
            int rgb = this.rgb();
            this.textbox.setValue(this.hex(rgb));
        } finally {
            this.isUpdating = false;
        }
    }
    
    private void drawCursor(GuiGraphics graphics, int x, int y) {
        final int r = 4;
        this.border(graphics, x - r, y - r, x + r, y + r, 0xFFFFFFFF);
        this.border(graphics, x - r + ColorPickerScreen.BORDER, y - r + ColorPickerScreen.BORDER, x + r - ColorPickerScreen.BORDER, y + r - ColorPickerScreen.BORDER, 0xFF000000);
    }
    
    private void drawSelector(GuiGraphics graphics, int x, int y) {
        this.border(graphics, x - ColorPickerScreen.BORDER - 1, y - (ColorPickerScreen.BORDER * 2) - 1, x + ColorPickerScreen.HUE_WIDTH + ColorPickerScreen.BORDER + 1, y + (ColorPickerScreen.BORDER * 2) + 1, 0xFFFFFFFF);
        this.border(graphics, x - ColorPickerScreen.BORDER, y - (ColorPickerScreen.BORDER * 2), x + ColorPickerScreen.HUE_WIDTH + ColorPickerScreen.BORDER, y + (ColorPickerScreen.BORDER * 2), 0xFF000000);
    }
}
