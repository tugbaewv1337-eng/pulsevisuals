package com.pulse.visuals.client.gui;

import com.pulse.visuals.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class PulseVisualsConfigScreen extends Screen {
    private final Screen parent;

    public PulseVisualsConfigScreen(Screen parent) {
        super(Text.literal("Pulse Visuals"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = 40;
        int spacing = 24;

        addToggleButton(centerX, y, "Hit Particles",
            () -> ModConfig.ENABLE_HIT_PARTICLES, v -> ModConfig.ENABLE_HIT_PARTICLES = v);
        y += spacing;

        addToggleButton(centerX, y, "Target HUD",
            () -> ModConfig.ENABLE_TARGET_HUD, v -> ModConfig.ENABLE_TARGET_HUD = v);
        y += spacing;

        addToggleButton(centerX, y, "Damage Numbers",
            () -> ModConfig.ENABLE_DAMAGE_NUMBERS, v -> ModConfig.ENABLE_DAMAGE_NUMBERS = v);
        y += spacing;

        addToggleButton(centerX, y, "Trajectory Prediction",
            () -> ModConfig.ENABLE_TRAJECTORY_PREDICTION, v -> ModConfig.ENABLE_TRAJECTORY_PREDICTION = v);
        y += spacing;

        addToggleButton(centerX, y, "Critical Hit Effect",
            () -> ModConfig.ENABLE_CRITICAL_EFFECT, v -> ModConfig.ENABLE_CRITICAL_EFFECT = v);
        y += spacing + 10;

        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> close())
            .dimensions(centerX - 100, y, 200, 20)
            .build());
    }

    private void addToggleButton(int centerX, int y, String label, BooleanSupplier getter, Consumer<Boolean> setter) {
        ButtonWidget button = ButtonWidget.builder(
            Text.literal(label + ": " + (getter.getAsBoolean() ? "ON" : "OFF")),
            btn -> {
                boolean newValue = !getter.getAsBoolean();
                setter.accept(newValue);
                btn.setMessage(Text.literal(label + ": " + (newValue ? "ON" : "OFF")));
            }
        ).dimensions(centerX - 100, y, 200, 20).build();
        addDrawableChild(button);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    @Override
    public boolean shouldPauseGame() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
    }
}
