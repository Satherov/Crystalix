package dev.satherov.crystalix.client.renderer;

import dev.satherov.crystalix.core.CSRegistry;
import dev.satherov.crystalix.core.network.CSNetwork;
import dev.satherov.crystalix.core.network.SetColorPayload;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;

public class ColorPickerScreen extends Screen {
    
    private static final int CELL_SIZE = 18;
    private static final int PADDING = 4;
    private static final int BORDER = 1;
    private static final int COLUMNS = 12;
    
    private transient boolean mouseDown = false;
    
    public ColorPickerScreen() {
        super(Component.literal("Color Picker"));
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    private int gridWidth() {
        return COLUMNS * CELL_SIZE + (COLUMNS - 1) * PADDING;
    }
    
    private int gridHeight() {
        int rows = (int) Math.ceil(CSRegistry.Colors.values().length / (double) COLUMNS);
        return rows * CELL_SIZE + (rows - 1) * PADDING;
    }
    
    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, width, height, 0x88000000);
        
        int gw = gridWidth();
        int gh = gridHeight();
        int x0 = (width - gw) / 2;
        int y0 = (height - gh) / 2;
        
        g.fill(x0 - 6, y0 - 6, x0 + gw + 6, y0 + gh + 6, 0xF0151515);
        drawBorder(g, x0 - 6, y0 - 6, x0 + gw + 6, y0 + gh + 6, 0xFFFFFFFF);
        
        CSRegistry.Colors[] colors = CSRegistry.Colors.values();
        int hovered = indexAt(mouseX, mouseY, x0, y0);
        
        for (int i = 0; i < colors.length; i++) {
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            int x = x0 + col * (CELL_SIZE + PADDING);
            int y = y0 + row * (CELL_SIZE + PADDING);
            
            int rgb = colors[i].color();
            if (rgb < 0) {
                drawChecker(g, x, y, CELL_SIZE, CELL_SIZE);
            }
            
            int fill = 0xFF000000 | (rgb < 0 ? 0xFFFFFF : rgb);
            g.fill(x + BORDER, y + BORDER, x + CELL_SIZE - BORDER, y + CELL_SIZE - BORDER, fill);
            
            if (i == hovered) {
                int overlay = mouseDown ? 0x44000000 : 0x22FFFFFF;
                g.fill(x + BORDER, y + BORDER, x + CELL_SIZE - BORDER, y + CELL_SIZE - BORDER, overlay);
            }
            
            drawBorder(g, x, y, x + CELL_SIZE, y + CELL_SIZE, 0xFF000000);
        }
        
        super.render(g, mouseX, mouseY, partialTick);
    }
    
    @Override
    protected void renderBlurredBackground(float partialTick) { }
    
    private void drawBorder(GuiGraphics g, int x1, int y1, int x2, int y2, int color) {
        g.fill(x1, y1, x2, y1 + BORDER, color);
        g.fill(x1, y2 - BORDER, x2, y2, color);
        g.fill(x1, y1, x1 + BORDER, y2, color);
        g.fill(x2 - BORDER, y1, x2, y2, color);
    }
    
    private void drawChecker(GuiGraphics g, int x, int y, int w, int h) {
        int s = 3;
        int c1 = 0xFFCCCCCC;
        int c2 = 0xFF999999;
        for (int yy = 0; yy < h; yy += s) {
            for (int xx = 0; xx < w; xx += s) {
                boolean alt = ((xx / s) + (yy / s)) % 2 == 0;
                g.fill(x + xx, y + yy, x + Math.min(xx + s, w), y + Math.min(yy + s, h), alt ? c1 : c2);
            }
        }
    }
    
    private int indexAt(int mouseX, int mouseY, int x0, int y0) {
        int gx = mouseX - x0;
        int gy = mouseY - y0;
        if (gx < 0 || gy < 0) return -1;
        int stride = CELL_SIZE + PADDING;
        int col = gx / stride;
        int row = gy / stride;
        if (col >= COLUMNS) return -1;
        if (gx % stride >= CELL_SIZE || gy % stride >= CELL_SIZE) return -1;
        int idx = row * COLUMNS + col;
        return idx < CSRegistry.Colors.values().length ? idx : -1;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        mouseDown = true;
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        mouseDown = false;
        int gw = gridWidth();
        int gh = gridHeight();
        int x0 = (width - gw) / 2;
        int y0 = (height - gh) / 2;
        int idx = indexAt((int) mouseX, (int) mouseY, x0, y0);
        if (idx >= 0) {
            CSRegistry.Colors color = CSRegistry.Colors.values()[idx];
            CSNetwork.sendToServer(new SetColorPayload(color));
            Minecraft.getInstance().setScreen(null);
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
