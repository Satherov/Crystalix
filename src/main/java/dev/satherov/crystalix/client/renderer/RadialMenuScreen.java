package dev.satherov.crystalix.client.renderer;

import dev.satherov.crystalix.client.KeybindManager;
import dev.satherov.crystalix.client.lang.CSLanguage;
import dev.satherov.crystalix.common.properties.CSProperties;
import dev.satherov.crystalix.common.properties.IProperty;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RadialMenuScreen extends Screen {
    private static final int INNER_RADIUS = 40;
    private static final int OUTER_RADIUS = 100;
    private static final int HOVER_EXTEND = 15;
    private static final int CENTER_DEAD_ZONE = 20;
    private final List<RadialMenuItem> menuItems = new ArrayList<>();
    private int hoveredIndex = -1;
    
    public RadialMenuScreen() {
        super(Component.literal("Radial Menu"));
    }
    
    public void addMenuItem(IProperty<?> property, Consumer<Boolean> action) {
        menuItems.add(new RadialMenuItem(property, action));
    }
    
    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        drawRadialOverlay(graphics, centerX, centerY);
        
        super.render(graphics, mouseX, mouseY, partialTick);
        
        hoveredIndex = getHoveredSection(mouseX, mouseY, centerX, centerY);
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        for (int i = 0; i < menuItems.size(); i++) {
            boolean isHovered = i == hoveredIndex;
            renderSection(graphics, mouseX, mouseY, centerX, centerY, i, menuItems.size(), isHovered, menuItems.get(i).property());
        }
        
        RenderSystem.disableBlend();
    }
    
    private void drawRadialOverlay(GuiGraphics graphics, int centerX, int centerY) {
        Matrix4f matrix = graphics.pose().last().pose();
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        
        buffer.addVertex(matrix, centerX, centerY, 0).setColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        int segments = 64;
        for (int i = 0; i <= segments; i++) {
            float angle = (float) (2 * Math.PI * i / segments);
            float x = centerX + (float) Math.cos(angle);
            float y = centerY + (float) Math.sin(angle);
            
            buffer.addVertex(matrix, x, y, 0).setColor(0.0f, 0.0f, 0.0f, 0.6f);
        }
        
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.disableBlend();
    }
    
    private void renderSection(GuiGraphics graphics, int mouseX, int mouseY, int centerX, int centerY, int index, int totalSections, boolean isHovered, IProperty<?> property) {
        float anglePerSection = 360.0f / totalSections;
        float start = anglePerSection * index - 90;
        
        float midRad = (float) Math.toRadians(start + anglePerSection / 2);
        
        drawLines(graphics, centerX, centerY, start);
        
        int outerRadius = isHovered ? OUTER_RADIUS + HOVER_EXTEND : OUTER_RADIUS;
        float labelRadius = (INNER_RADIUS + outerRadius) / 2.0f;
        int labelX = centerX + (int) (Math.cos(midRad) * labelRadius);
        int labelY = centerY + (int) (Math.sin(midRad) * labelRadius);
        
        int textColor = isHovered ? 0xFFFFFFFF : 0xFFCCCCCC;
        
        int labelWidth = this.font.width(property.name());
        int valueWidth = this.font.width(property.display());
        graphics.drawString(this.font, property.name(), labelX - labelWidth / 2, labelY - 4, textColor);
        graphics.drawString(this.font, property.display(), labelX - valueWidth / 2, labelY - 4 + font.lineHeight, 0xFFFFFFFF);
        
        if (!isHovered) return;
        
        List<Component> lines = new ArrayList<>();
        lines.add(property.name().withStyle(ChatFormatting.DARK_GRAY));
        
        if (property.location().equals(CSProperties.COLOR)) {
            lines.add(CSLanguage.TOOLTIP_MMB.text(
                    ChatFormatting.DARK_GRAY,
                    ComponentUtils.wrapInSquareBrackets(InputConstants.Type.MOUSE.getOrCreate(GLFW.GLFW_MOUSE_BUTTON_MIDDLE).getDisplayName().copy().withStyle(ChatFormatting.GOLD))
            ));
        } else {
            lines.add(CSLanguage.TOOLTIP_LMB.text(
                    ChatFormatting.DARK_GRAY,
                    ComponentUtils.wrapInSquareBrackets(InputConstants.Type.MOUSE.getOrCreate(GLFW.GLFW_MOUSE_BUTTON_LEFT).getDisplayName().copy().withStyle(ChatFormatting.GOLD)),
                    ComponentUtils.wrapInSquareBrackets(CSLanguage.INPUT_WHEEL_DOWN.text(ChatFormatting.GOLD))
            ));
            lines.add(CSLanguage.TOOLTIP_RMB.text(
                    ChatFormatting.DARK_GRAY,
                    ComponentUtils.wrapInSquareBrackets(InputConstants.Type.MOUSE.getOrCreate(GLFW.GLFW_MOUSE_BUTTON_RIGHT).getDisplayName().copy().withStyle(ChatFormatting.GOLD)),
                    ComponentUtils.wrapInSquareBrackets(CSLanguage.INPUT_WHEEL_UP.text(ChatFormatting.GOLD))
            ));
        }
        
        graphics.renderComponentTooltip(
                font,
                lines,
                mouseX,
                mouseY
        );
    }
    
    private void drawLines(GuiGraphics graphics, float centerX, float centerY, float angle) {
        Matrix4f matrix = graphics.pose().last().pose();
        
        float r = 0.53f;
        float g = 0.53f;
        float b = 0.53f;
        float a = 1.0f;
        
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        
        float rad = (float) Math.toRadians(angle);
        float x1 = centerX + (float) (Math.cos(rad) * INNER_RADIUS);
        float y1 = centerY + (float) (Math.sin(rad) * INNER_RADIUS);
        float x2 = centerX + (float) (Math.cos(rad) * OUTER_RADIUS);
        float y2 = centerY + (float) (Math.sin(rad) * OUTER_RADIUS);
        
        buffer.addVertex(matrix, x1, y1, 0).setColor(r, g, b, a);
        buffer.addVertex(matrix, x2, y2, 0).setColor(r, g, b, a);
        
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.lineWidth(1.5f);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.lineWidth(1.0f);
    }
    
    private int getHoveredSection(int mouseX, int mouseY, int centerX, int centerY) {
        float dx = mouseX - centerX;
        float dy = mouseY - centerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance < CENTER_DEAD_ZONE || distance > OUTER_RADIUS + HOVER_EXTEND) {
            return -1;
        }
        
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx)) + 90;
        if (angle < 0) angle += 360;
        
        float anglePerSection = 360.0f / menuItems.size();
        int section = (int) (angle / anglePerSection);
        
        return section % menuItems.size();
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hoveredIndex < 0 || hoveredIndex > menuItems.size()) return super.mouseClicked(mouseX, mouseY, button);
        if (button == 0 || button == 1) {
            RadialMenuItem item = menuItems.get(hoveredIndex);
            item.action().accept(button == 0);
            item.property().next(button == 0);
            return true;
        } else if (button == 2 && menuItems.get(hoveredIndex).property().location().equals(CSProperties.COLOR)) {
            Minecraft.getInstance().setScreen(new ColorPickerScreen());
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mx, double my, double dx, double dy) {
        if (hoveredIndex >= 0 && hoveredIndex < menuItems.size()) {
            RadialMenuItem item = menuItems.get(hoveredIndex);
            item.action().accept(dy < 0);
            item.property().next(dy < 0);
            return true;
        }
        return super.mouseScrolled(mx, my, dx, dy);
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (KeybindManager.SCREEN_OPENER.matches(keyCode, scanCode)) {
            this.onClose();
        }
        return true;
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    protected void renderBlurredBackground(float partialTick) {
        // No blur
    }
    
    private record RadialMenuItem(IProperty<?> property, Consumer<Boolean> action) { }
}