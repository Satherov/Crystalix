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
    
    private static final int INNER_RADIUS = 50;
    private static final int OUTER_RADIUS = 125;
    private static final int HOVER_EXTEND = 20;
    private static final int CENTER_DEAD_ZONE = 20;
    
    private final List<RadialMenuItem> menuItems = new ArrayList<>();
    private int hoveredIndex = -1;
    
    public RadialMenuScreen() {
        super(Component.literal("Radial Menu"));
    }
    
    public void addMenuItem(IProperty<?> property, Consumer<Boolean> action) {
        this.menuItems.add(new RadialMenuItem(property, action));
    }
    
    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        this.drawRadialOverlay(graphics, centerX, centerY);
        
        super.render(graphics, mouseX, mouseY, partialTick);
        
        this.hoveredIndex = this.getHoveredSection(mouseX, mouseY, centerX, centerY);
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        for (int i = 0; i < this.menuItems.size(); i++) {
            boolean isHovered = i == this.hoveredIndex;
            this.renderSection(graphics, mouseX, mouseY, centerX, centerY, i, this.menuItems.size(), isHovered, this.menuItems.get(i).property());
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
        
        this.drawLines(graphics, centerX, centerY, start);
        
        int outerRadius = isHovered ? RadialMenuScreen.OUTER_RADIUS + RadialMenuScreen.HOVER_EXTEND : RadialMenuScreen.OUTER_RADIUS;
        
        float labelRadius = RadialMenuScreen.INNER_RADIUS + (outerRadius - RadialMenuScreen.INNER_RADIUS) * 0.6f;
        
        float labelCenterX = centerX + (float) (Math.cos(midRad) * labelRadius);
        float labelCenterY = centerY + (float) (Math.sin(midRad) * labelRadius);
        
        int textColor = isHovered ? 0xFFFFFFFF : 0xFFAAAAAA;
        
        int labelWidth = this.font.width(property.name());
        int valueWidth = this.font.width(property.display());
        
        int totalTextHeight = this.font.lineHeight * 2;
        
        int textX1 = (int) (labelCenterX - labelWidth / 2.0f);
        int textX2 = (int) (labelCenterX - valueWidth / 2.0f);
        int textY = (int) (labelCenterY - totalTextHeight / 2.0f);
        
        graphics.drawString(this.font, property.name(), textX1, textY, textColor);
        graphics.drawString(this.font, property.display(), textX2, textY + this.font.lineHeight, 0xFFFFFFFF);
        
        if (!isHovered) return;
        
        List<Component> lines = new ArrayList<>();
        lines.add(property.name().withStyle(ChatFormatting.DARK_GRAY));
        lines.add(property.tooltip().withStyle(ChatFormatting.DARK_GRAY));
        
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
                this.font,
                lines,
                mouseX,
                mouseY
        );
    }
    
    private void drawLines(GuiGraphics graphics, float centerX, float centerY, float angle) {
        Matrix4f matrix = graphics.pose().last().pose();
        
        int argb = 0xFF878787;
        
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        
        float rad = (float) Math.toRadians(angle);
        float x1 = centerX + (float) (Math.cos(rad) * RadialMenuScreen.INNER_RADIUS);
        float y1 = centerY + (float) (Math.sin(rad) * RadialMenuScreen.INNER_RADIUS);
        float x2 = centerX + (float) (Math.cos(rad) * RadialMenuScreen.OUTER_RADIUS);
        float y2 = centerY + (float) (Math.sin(rad) * RadialMenuScreen.OUTER_RADIUS);
        
        buffer.addVertex(matrix, x1, y1, 0).setColor(argb);
        buffer.addVertex(matrix, x2, y2, 0).setColor(argb);
        
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.lineWidth(2.5f);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.lineWidth(1.0f);
    }
    
    private int getHoveredSection(int mouseX, int mouseY, int centerX, int centerY) {
        float dx = mouseX - centerX;
        float dy = mouseY - centerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance < RadialMenuScreen.CENTER_DEAD_ZONE || distance > RadialMenuScreen.OUTER_RADIUS + RadialMenuScreen.HOVER_EXTEND) {
            return -1;
        }
        
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx)) + 90;
        if (angle < 0) angle += 360;
        
        float anglePerSection = 360.0f / this.menuItems.size();
        int section = (int) (angle / anglePerSection);
        
        return section % this.menuItems.size();
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.hoveredIndex < 0 || this.hoveredIndex > this.menuItems.size()) return super.mouseClicked(mouseX, mouseY, button);
        if (button == 0 || button == 1) {
            RadialMenuItem item = this.menuItems.get(this.hoveredIndex);
            item.action().accept(button == 0);
            item.property().next(button == 0);
            return true;
        } else if (button == 2 && this.menuItems.get(this.hoveredIndex).property().location().equals(CSProperties.COLOR)) {
            Minecraft.getInstance().setScreen(new ColorPickerScreen());
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mx, double my, double dx, double dy) {
        if (this.hoveredIndex >= 0 && this.hoveredIndex < this.menuItems.size()) {
            RadialMenuItem item = this.menuItems.get(this.hoveredIndex);
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
    
    //    @Override
    //    protected void renderBlurredBackground(float partialTick) { }
    
    private record RadialMenuItem(IProperty<?> property, Consumer<Boolean> action) { }
}