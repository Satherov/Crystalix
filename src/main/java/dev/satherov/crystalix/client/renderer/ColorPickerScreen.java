package dev.satherov.crystalix.client.renderer;

import dev.satherov.crystalix.client.lang.CSLanguage;
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

import org.jetbrains.annotations.NotNull;

public class ColorPickerScreen extends Screen {
    
    private static final int BORDER = 1;
    private static final int PADDING = 6;
    private static final int SV_SIZE = 180;
    private static final int HUE_WIDTH = 14;
    private static final int PANEL_PADDING = 10;
    private static final int CLEAR_BTN_W = 90;
    private static final int CLEAR_BTN_H = 16;
    
    // State
    private boolean draggingSvArea = false;
    private boolean draggingHue = false;
    private boolean pressingClear = false;
    private boolean clearSelected = false;
    
    // HSV color (0..1)
    private float hue = 0.0f;         // 0..1
    private float saturation = 1.0f;         // 0..1
    private float value = 1.0f;         // 0..1
    
    // Cached textures to avoid per-pixel rendering every frame
    private DynamicTexture svTexture;           // Saturation/Value square
    private ResourceLocation svTextureId;
    private float svCachedHue = -1.0f;          // Last hue used to build SV texture
    private boolean svDirty = true;             // Mark SV texture for rebuild
    
    private DynamicTexture hueTexture;          // Vertical hue bar
    private ResourceLocation hueTextureId;
    private boolean hueBuilt = false;
    
    // Hex input box
    private EditBox hexField;
    private static final int HEX_H = 16; // height of the hex input field
    private boolean updatingHexFromCode = false; // guard to avoid recursion
    
    public ColorPickerScreen() {
        super(Component.literal("Color Picker"));
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    // Computed panel dimensions
    private int panelWidth() {
        return PANEL_PADDING * 2 + SV_SIZE + PADDING + HUE_WIDTH;
    }
    
    private int panelHeight() {
        return PANEL_PADDING * 2 + SV_SIZE + PADDING + 20 + 4 + HEX_H + 4 + CLEAR_BTN_H;
    }
    
    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, width, height, 0x88000000);
        
        int pw = panelWidth();
        int ph = panelHeight();
        int x0 = (width - pw) / 2;
        int y0 = (height - ph) / 2;
        
        g.fill(x0 - 4, y0 - 4, x0 + pw + 4, y0 + ph + 4, 0xF0151515);
        drawBorder(g, x0 - 4, y0 - 4, x0 + pw + 4, y0 + ph + 4, 0xFFFFFFFF);
        
        int svX = x0 + PANEL_PADDING;
        int svY = y0 + PANEL_PADDING;
        int hueX = svX + SV_SIZE + PADDING;
        int hueY = svY;
        
        // Ensure textures exist / are up-to-date
        ensureHueTexture();
        ensureSvTexture();
        
        // draw SV square (for current hue)
        blitSvTexture(g, svX, svY);
        
        // draw SV cursor
        int cx = svX + Math.round(saturation * (SV_SIZE - 1));
        int cy = svY + Math.round((1.0f - value) * (SV_SIZE - 1));
        drawCursor(g, cx, cy);
        
        // draw vertical hue bar
        blitHueTexture(g, hueX, hueY);
        
        // draw hue cursor
        int hy = hueY + Math.round(hue * (SV_SIZE - 1));
        drawBorder(g, hueX - 1, hy - 2, hueX + HUE_WIDTH + 1, hy + 2, 0xFFFFFFFF);
        
        // preview + hex text area
        int previewY = svY + SV_SIZE + PADDING;
        int previewH = 20;
        int rgb = currentRgb();
        int previewX1 = svX;
        int previewX2 = hueX + HUE_WIDTH;
        if (clearSelected) {
            drawChecker(g, previewX1, previewY, previewX2 - previewX1, previewH);
        } else {
            g.fill(previewX1, previewY, previewX2, previewY + previewH, 0xFF000000 | rgb);
        }
        drawBorder(g, previewX1, previewY, previewX2, previewY + previewH, 0xFF000000);
        
        // place hex field below preview (rendered by widget system automatically)
        int textY = previewY + previewH + 4;
        if (hexField != null) {
            int fieldX1 = svX;
            int fieldW = (hueX + HUE_WIDTH) - svX;
            hexField.setX(fieldX1 + 1);
            hexField.setY(textY);
            hexField.setWidth(fieldW - 2);
            hexField.setHeight(HEX_H);
        }
        
        // clear button centered below
        int btnY = textY + HEX_H + 4;
        int btnX1 = x0 + (pw - CLEAR_BTN_W) / 2;
        int btnX2 = btnX1 + CLEAR_BTN_W;
        int btnY2 = btnY + CLEAR_BTN_H;
        int bg = clearSelected ? 0xFF3A7D44 : 0xFF2A2A2A; // greenish when active, dark otherwise
        g.fill(btnX1, btnY, btnX2, btnY2, bg);
        drawBorder(g, btnX1, btnY, btnX2, btnY2, 0xFFFFFFFF);
        Component text = CSLanguage.PROPERTY_CLEAR.text().append(": ").append(clearSelected ? CSLanguage.PROPERTY_ENABLED.text() : CSLanguage.PROPERTY_DISABLED.text());
        int btw = this.font.width(text);
        int btx = btnX1 + (CLEAR_BTN_W - btw) / 2;
        int bty = btnY + (CLEAR_BTN_H - this.font.lineHeight + 1) / 2; // +1 for better visual centering
        g.drawString(this.font, text, btx, bty, 0xFFFFFFFF);
        
        super.render(g, mouseX, mouseY, partialTick);
    }
    
    @Override
    protected void renderBlurredBackground(float partialTick) { }
    
    @Override
    protected void init() {
        if (Minecraft.getInstance().player == null) return;
        ItemStack wand = CrystalixWand.find(Minecraft.getInstance().player);
        if (wand.isEmpty()) return;
        Integer col = wand.get(CSRegistry.COLOR);
        if (col != null) {
            if (col == -1) {
                clearSelected = true;
            } else {
                clearSelected = false;
                setFromRgb(col);
            }
        }
        
        // Create hex input box
        int pw = panelWidth();
        int x0 = (width - pw) / 2;
        int svX = x0 + PANEL_PADDING;
        int hueX = svX + SV_SIZE + PADDING;
        int previewY = ((height - panelHeight()) / 2 + PANEL_PADDING) + SV_SIZE + PADDING;
        int textY = previewY + 20 + 4;
        int fieldX1 = svX;
        int fieldW = (hueX + HUE_WIDTH) - svX;
        
        hexField = new EditBox(this.font, fieldX1 + 1, textY, fieldW - 2, HEX_H, Component.literal("Hex"));
        // Allow free-form input; interpretation (truncate/pad) happens when reading
        hexField.setMaxLength(12);
        hexField.setBordered(true);
        hexField.setEditable(true);
        hexField.setValue(clearSelected ? "-1" : toHex(currentRgb()));
        hexField.setResponder(this::onHexEdited);
        this.addRenderableWidget(hexField);
    }
    
    private void drawBorder(GuiGraphics g, int x1, int y1, int x2, int y2, int color) {
        g.fill(x1, y1, x2, y1 + BORDER, color);
        g.fill(x1, y2 - BORDER, x2, y2, color);
        g.fill(x1, y1, x1 + BORDER, y2, color);
        g.fill(x2 - BORDER, y1, x2, y2, color);
    }
    
    private void blitSvTexture(GuiGraphics g, int x, int y) {
        if (svTextureId != null) {
            g.blit(svTextureId, x, y, 0, 0, SV_SIZE, SV_SIZE, SV_SIZE, SV_SIZE);
            drawBorder(g, x, y, x + SV_SIZE, y + SV_SIZE, 0xFF000000);
        }
    }
    
    private void blitHueTexture(GuiGraphics g, int x, int y) {
        if (hueTextureId != null) {
            g.blit(hueTextureId, x, y, 0, 0, HUE_WIDTH, SV_SIZE, HUE_WIDTH, SV_SIZE);
            drawBorder(g, x, y, x + HUE_WIDTH, y + SV_SIZE, 0xFF000000);
        }
    }
    
    private void drawCursor(GuiGraphics g, int x, int y) {
        int r = 4;
        drawBorder(g, x - r, y - r, x + r, y + r, 0xFFFFFFFF);
        drawBorder(g, x - r + 1, y - r + 1, x + r - 1, y + r - 1, 0xFF000000);
    }
    
    private String toHex(int rgb) {
        return String.format("#%06X", (0xFFFFFF & rgb));
    }
    
    private int currentRgb() {
        return clearSelected ? -1 : (Mth.hsvToRgb(hue, saturation, value) & 0xFFFFFF);
    }
    
    private void setFromRgb(int rgb) {
        if (rgb == -1) {
            clearSelected = true;
            return;
        }
        float r = ((rgb >> 16) & 0xFF) / 255.0f;
        float g = ((rgb >> 8) & 0xFF) / 255.0f;
        float b = (rgb & 0xFF) / 255.0f;
        
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;
        
        // Value
        this.value = max;
        
        // Saturation
        this.saturation = max == 0.0f ? 0.0f : (delta / max);
        
        // Hue
        float h;
        if (delta == 0.0f) {
            h = 0.0f; // undefined, set to 0
        } else if (max == r) {
            h = ((g - b) / delta) % 6.0f;
        } else if (max == g) {
            h = ((b - r) / delta) + 2.0f;
        } else {
            h = ((r - g) / delta) + 4.0f;
        }
        h /= 6.0f; // convert to 0..1
        if (h < 0.0f) h += 1.0f;
        this.hue = Mth.clamp(h, 0.0f, 1.0f);
        // Rebuild SV texture on next render since hue changed
        this.svDirty = true;
    }
    
    private int svLeft() { return (width - panelWidth()) / 2 + PANEL_PADDING; }
    
    private int svY() { return (height - panelHeight()) / 2 + PANEL_PADDING; }
    
    private int hueX() { return svLeft() + SV_SIZE + PADDING; }
    
    private int hueY() { return svY(); }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx = (int) mouseX;
        int my = (int) mouseY;
        int sx = svLeft();
        int sy = svY();
        int hx = hueX();
        int hy = hueY();
        int pw = panelWidth();
        int x0 = (width - pw) / 2;
        int textY = sy + SV_SIZE + PADDING + 20 + 4; // previewY + previewH + 4
        int btnY = textY + HEX_H + 4;
        int btnX1 = x0 + (pw - CLEAR_BTN_W) / 2;
        int btnX2 = btnX1 + CLEAR_BTN_W;
        int btnY2 = btnY + CLEAR_BTN_H;
        
        if (mx >= sx && mx < sx + SV_SIZE && my >= sy && my < sy + SV_SIZE) {
            draggingSvArea = true;
            clearSelected = false;
            updateSVFromMouse(mx, my);
            syncHexFieldFromCurrentColor();
            return true;
        }
        if (mx >= hx && mx < hx + HUE_WIDTH && my >= hy && my < hy + SV_SIZE) {
            draggingHue = true;
            clearSelected = false;
            updateHueFromMouse(my);
            syncHexFieldFromCurrentColor();
            return true;
        }
        // clear button click
        if (mx >= btnX1 && mx < btnX2 && my >= btnY && my < btnY2) {
            pressingClear = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        int mx = (int) mouseX;
        int my = (int) mouseY;
        if (draggingSvArea) {
            updateSVFromMouse(mx, my);
            svDirty = true; // update preview and SV texture cursor; SV gradient unchanged unless hue changes
            syncHexFieldFromCurrentColor();
            return true;
        } else if (draggingHue) {
            updateHueFromMouse(my);
            svDirty = true; // hue changed: SV gradient must rebuild
            syncHexFieldFromCurrentColor();
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean wasDragging = draggingSvArea || draggingHue;
        draggingSvArea = false;
        draggingHue = false;
        if (pressingClear) {
            pressingClear = false;
            clearSelected = true;
            CSNetwork.sendToServer(new SetColorPayload(-1));
            syncHexFieldFromCurrentColor();
            return true;
        }
        if (wasDragging) {
            // send update packet immediately when cursor is released
            CSNetwork.sendToServer(new SetColorPayload(currentRgb()));
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    private void onHexEdited(String text) {
        if (updatingHexFromCode) return;
        if (text == null) return;
        String t = text.trim();
        // Allow clear via -1
        if (t.equalsIgnoreCase("-1")) {
            clearSelected = true;
            return;
        }
        // Interpret input without mutating the text field:
        // remove '#', keep only hex chars; then truncate to 6 or pad with 'F' to 6
        if (t.startsWith("#")) t = t.substring(1);
        StringBuilder filtered = new StringBuilder();
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            if (isHexChar(c)) filtered.append(c);
        }
        String hexFiltered = filtered.toString();
        if (hexFiltered.length() > 6) hexFiltered = hexFiltered.substring(0, 6);
        while (hexFiltered.length() < 6) hexFiltered += 'F';
        String hex = hexFiltered.toUpperCase();
        int rgb = Integer.parseInt(hex, 16) & 0xFFFFFF;
        clearSelected = false;
        setFromRgb(rgb);
    }
    
    private static boolean isHexChar(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }
    
    private void syncHexFieldFromCurrentColor() {
        if (hexField == null) return;
        updatingHexFromCode = true;
        try {
            if (clearSelected) {
                hexField.setValue("-1");
            } else {
                int rgb = currentRgb();
                hexField.setValue(toHex(rgb));
            }
        } finally {
            updatingHexFromCode = false;
        }
    }
    
    private void updateSVFromMouse(int mx, int my) {
        int sx = svLeft();
        int sy = svY();
        float s = (mx - sx) / (float) (SV_SIZE - 1);
        float v = 1.0f - (my - sy) / (float) (SV_SIZE - 1);
        saturation = Mth.clamp(s, 0.0f, 1.0f);
        value = Mth.clamp(v, 0.0f, 1.0f);
    }
    
    private void updateHueFromMouse(int my) {
        int hy = hueY();
        float h = (my - hy) / (float) (SV_SIZE - 1);
        float newHue = Mth.clamp(h, 0.0f, 1.0f);
        if (newHue != hue) {
            hue = newHue;
            svDirty = true; // SV gradient depends on hue
        }
    }
    
    private void drawChecker(GuiGraphics g, int x, int y, int w, int h) {
        int s = 6;
        int c1 = 0xFFCCCCCC;
        int c2 = 0xFF999999;
        for (int yy = 0; yy < h; yy += s) {
            for (int xx = 0; xx < w; xx += s) {
                boolean alt = ((xx / s) + (yy / s)) % 2 == 0;
                int x2 = Math.min(x + xx + s, x + w);
                int y2 = Math.min(y + yy + s, y + h);
                g.fill(x + xx, y + yy, x2, y2, alt ? c1 : c2);
            }
        }
    }
    
    // --- Texture management ---
    private void ensureHueTexture() {
        if (hueBuilt && hueTexture != null && hueTextureId != null) return;
        // Build hue texture once
        hueTexture = new DynamicTexture(HUE_WIDTH, SV_SIZE, true);
        var img = hueTexture.getPixels();
        if (img != null) {
            for (int y = 0; y < SV_SIZE; y++) {
                float h = y / (float) (SV_SIZE - 1);
                int rgb = Mth.hsvToRgb(h, 1.0f, 1.0f) & 0xFFFFFF;
                int abgr = 0xFF000000 | ((rgb & 0xFF) << 16) | (rgb & 0xFF00) | ((rgb >> 16) & 0xFF); // convert RRGGBB -> A BB GG RR
                for (int x = 0; x < HUE_WIDTH; x++) {
                    img.setPixelRGBA(x, y, abgr);
                }
            }
            hueTexture.upload();
        }
        hueTextureId = Minecraft.getInstance().getTextureManager().register("crystalix/color_picker/hue", hueTexture);
        hueBuilt = true;
    }
    
    private void ensureSvTexture() {
        if (!svDirty && svTexture != null && svTextureId != null && svCachedHue == hue) return;
        if (svTexture == null) svTexture = new DynamicTexture(SV_SIZE, SV_SIZE, true);
        var img = svTexture.getPixels();
        if (img != null) {
            for (int y = 0; y < SV_SIZE; y++) {
                float v = 1.0f - (y / (float) (SV_SIZE - 1));
                for (int x = 0; x < SV_SIZE; x++) {
                    float s = x / (float) (SV_SIZE - 1);
                    int rgb = Mth.hsvToRgb(hue, s, v) & 0xFFFFFF;
                    int abgr = 0xFF000000 | ((rgb & 0xFF) << 16) | (rgb & 0xFF00) | ((rgb >> 16) & 0xFF);
                    img.setPixelRGBA(x, y, abgr);
                }
            }
            svTexture.upload();
        }
        if (svTextureId == null) {
            svTextureId = Minecraft.getInstance().getTextureManager().register("crystalix/color_picker/sv", svTexture);
        }
        svCachedHue = hue;
        svDirty = false;
    }
    
    @Override
    public void removed() {
        super.removed();
        // Clean up textures
        var tm = Minecraft.getInstance().getTextureManager();
        if (svTextureId != null) {
            tm.release(svTextureId);
            svTextureId = null;
        }
        if (hueTextureId != null) {
            tm.release(hueTextureId);
            hueTextureId = null;
        }
        if (svTexture != null) {
            svTexture.close();
            svTexture = null;
        }
        if (hueTexture != null) {
            hueTexture.close();
            hueTexture = null;
        }
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Enter applies the currently typed color and sends it to the server
        if (keyCode == 257 /* GLFW_KEY_ENTER */ || keyCode == 335 /* GLFW_KEY_KP_ENTER */) {
            if (hexField != null && hexField.isFocused()) {
                String t = hexField.getValue();
                if (t != null) {
                    t = t.trim();
                    if (t.equalsIgnoreCase("-1")) {
                        clearSelected = true;
                        CSNetwork.sendToServer(new SetColorPayload(-1));
                        syncHexFieldFromCurrentColor();
                        return true;
                    }
                    if (t.startsWith("#")) t = t.substring(1);
                    StringBuilder filtered = new StringBuilder();
                    for (int i = 0; i < t.length(); i++) {
                        char c = t.charAt(i);
                        if (isHexChar(c)) filtered.append(c);
                    }
                    String hexFiltered = filtered.toString();
                    if (hexFiltered.length() > 6) hexFiltered = hexFiltered.substring(0, 6);
                    while (hexFiltered.length() < 6) hexFiltered += 'F';
                    String hex = hexFiltered.toUpperCase();
                    try {
                        int rgb = Integer.parseInt(hex, 16) & 0xFFFFFF;
                        clearSelected = false;
                        setFromRgb(rgb);
                        svDirty = true;
                        CSNetwork.sendToServer(new SetColorPayload(rgb));
                        syncHexFieldFromCurrentColor();
                        return true;
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
