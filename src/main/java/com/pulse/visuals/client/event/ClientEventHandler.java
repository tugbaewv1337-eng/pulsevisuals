package com.pulse.visuals.client.event;

import com.pulse.visuals.PulseVisualsClient;
import com.pulse.visuals.client.gui.PulseVisualsConfigScreen;
import com.pulse.visuals.client.visual.TargetHUD;
import com.pulse.visuals.config.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class ClientEventHandler {
    private final MinecraftClient client;

    public ClientEventHandler() {
        this.client = MinecraftClient.getInstance();
    }

    public void onClientTick(MinecraftClient client) {
        // Open the settings panel when the configured key is pressed
        while (PulseVisualsClient.openPanelKey.wasPressed()) {
            client.setScreen(new PulseVisualsConfigScreen(client.currentScreen));
        }

        if (!ModConfig.ENABLE_MOD || client.world == null || client.player == null) {
            return;
        }

        // Update target HUD every tick
        PulseVisualsClient.getTargetHUD().updateTarget(client.player);

        // Update trajectory prediction for projectiles
        PulseVisualsClient.getTrajectoryRenderer().update(client.world, client.player);
    }

    public void onWorldRenderEnd(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!ModConfig.ENABLE_MOD || client.world == null || client.player == null) {
            return;
        }

        // Render all visual effects
        PulseVisualsClient.getHitParticleRenderer().render(context);
        PulseVisualsClient.getDamageNumberRenderer().render(context);
        PulseVisualsClient.getTrajectoryRenderer().render(context);
    }

    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        if (!ModConfig.ENABLE_MOD || !ModConfig.ENABLE_TARGET_HUD) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.currentScreen != null) {
            return;
        }

        TargetHUD hud = PulseVisualsClient.getTargetHUD();
        if (!hud.hasTarget()) {
            return;
        }

        TextRenderer textRenderer = client.textRenderer;
        int x = ModConfig.TARGET_HUD_X;
        int y = ModConfig.TARGET_HUD_Y;
        int lineHeight = 10;
        int line = 0;

        if (ModConfig.SHOW_TARGET_NAME) {
            drawContext.drawTextWithShadow(textRenderer, hud.getTargetName(), x, y + line * lineHeight, 0xFFFFFF);
            line++;
        }
        if (ModConfig.SHOW_TARGET_HEALTH) {
            String healthText = String.format("HP: %.1f / %.1f", hud.getTargetHealth(), hud.getTargetMaxHealth());
            drawContext.drawTextWithShadow(textRenderer, healthText, x, y + line * lineHeight, 0xFF5555);
            line++;
        }
        if (ModConfig.SHOW_TARGET_ARMOR) {
            drawContext.drawTextWithShadow(textRenderer, "Armor: " + hud.getTargetArmor(), x, y + line * lineHeight, 0xAAAAAA);
            line++;
        }
        String distanceText = String.format("Distance: %.1fm", hud.getTargetDistance());
        drawContext.drawTextWithShadow(textRenderer, distanceText, x, y + line * lineHeight, 0xFFFFFF);
    }
}
