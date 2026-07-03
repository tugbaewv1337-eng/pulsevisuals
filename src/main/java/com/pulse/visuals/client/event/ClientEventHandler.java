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
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

import java.util.Collection;

public class ClientEventHandler {
    private final MinecraftClient client;

    private boolean fullBrightApplied = false;
    private double previousGamma = 1.0;

    private int previousTotemStat = -1;
    private int totemPopCount = 0;

    public ClientEventHandler() {
        this.client = MinecraftClient.getInstance();
    }

    public void onClientTick(MinecraftClient client) {
        // Open the settings panel when the configured key is pressed
        while (PulseVisualsClient.openPanelKey.wasPressed()) {
            client.setScreen(new PulseVisualsConfigScreen(client.currentScreen));
        }

        updateFullBright(client);
        updateTotemTracker(client);

        if (!ModConfig.ENABLE_MOD || client.world == null || client.player == null) {
            return;
        }

        // Update target HUD every tick
        PulseVisualsClient.getTargetHUD().updateTarget(client.player);

        // Update trajectory prediction for projectiles
        PulseVisualsClient.getTrajectoryRenderer().update(client.world, client.player);
    }

    private void updateFullBright(MinecraftClient client) {
        boolean shouldBeOn = ModConfig.ENABLE_MOD && ModConfig.ENABLE_FULL_BRIGHT;
        if (shouldBeOn && !fullBrightApplied) {
            previousGamma = client.options.getGamma().getValue();
            client.options.getGamma().setValue(16.0);
            fullBrightApplied = true;
        } else if (!shouldBeOn && fullBrightApplied) {
            client.options.getGamma().setValue(previousGamma);
            fullBrightApplied = false;
        }
    }

    private void updateTotemTracker(MinecraftClient client) {
        if (!ModConfig.ENABLE_TOTEM_TRACKER || client.player == null) {
            return;
        }
        int currentStat = client.player.getStatHandler().getStat(
            Stats.USED.getOrCreateStat(Items.TOTEM_OF_UNDYING)
        );
        if (previousTotemStat == -1) {
            previousTotemStat = currentStat;
        } else if (currentStat > previousTotemStat) {
            totemPopCount += (currentStat - previousTotemStat);
            previousTotemStat = currentStat;
        }
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
        if (!ModConfig.ENABLE_MOD) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.currentScreen != null) {
            return;
        }

        TextRenderer textRenderer = client.textRenderer;

        if (ModConfig.ENABLE_TARGET_HUD) {
            TargetHUD hud = PulseVisualsClient.getTargetHUD();
            if (hud.hasTarget()) {
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

        renderSelfHud(drawContext, client, textRenderer);
    }

    private void renderSelfHud(DrawContext drawContext, MinecraftClient client, TextRenderer textRenderer) {
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        if (ModConfig.ENABLE_WATERMARK) {
            drawContext.drawTextWithShadow(textRenderer, "PulseVisuals", 5, 5, 0xFFAFA9EC);
        }

        if (client.player == null) {
            return;
        }

        int rightY = 5;
        if (ModConfig.ENABLE_ARMOR_HUD) {
            String armorText = "Armor: " + client.player.getArmor();
            int width = textRenderer.getWidth(armorText);
            drawContext.drawTextWithShadow(textRenderer, armorText, screenWidth - width - 5, rightY, 0xAAAAAA);
            rightY += 10;
        }

        if (ModConfig.ENABLE_SATURATION_HUD) {
            String satText = String.format("Saturation: %.1f", client.player.getHungerManager().getSaturationLevel());
            int width = textRenderer.getWidth(satText);
            drawContext.drawTextWithShadow(textRenderer, satText, screenWidth - width - 5, rightY, 0xFFAA00);
            rightY += 10;
        }

        if (ModConfig.ENABLE_POTIONS_HUD) {
            Collection<StatusEffectInstance> effects = client.player.getStatusEffects();
            int line = 0;
            for (StatusEffectInstance effect : effects) {
                String name = effect.getEffectType().value().getName().getString();
                int seconds = effect.getDuration() / 20;
                String text = name + " " + (effect.getAmplifier() + 1) + " (" + seconds + "s)";
                drawContext.drawTextWithShadow(textRenderer, text, 5, screenHeight / 2 - (effects.size() * 10) / 2 + line * 10, 0x55FFFF);
                line++;
            }
        }

        if (ModConfig.ENABLE_COOLDOWNS_HUD) {
            ItemStack held = client.player.getMainHandStack();
            if (!held.isEmpty() && client.player.getItemCooldownManager().isCoolingDown(held)) {
                float progress = client.player.getItemCooldownManager().getCooldownProgress(held, 0f);
                String itemName = Registries.ITEM.getId(held.getItem()).getPath();
                String text = itemName + ": " + Math.round((1f - progress) * 100) + "%";
                drawContext.drawTextWithShadow(textRenderer, text, screenWidth / 2 - textRenderer.getWidth(text) / 2, screenHeight / 2 + 20, 0xFF5555);
            }
        }

        if (ModConfig.ENABLE_SPRINT_HUD && client.player.isSprinting()) {
            String text = "Sprinting";
            drawContext.drawTextWithShadow(textRenderer, text, screenWidth / 2 - textRenderer.getWidth(text) / 2, screenHeight / 2 + 35, 0x55FF55);
        }

        if (ModConfig.ENABLE_TOTEM_TRACKER) {
            String text = "Totems used: " + totemPopCount;
            drawContext.drawTextWithShadow(textRenderer, text, 5, screenHeight - 15, 0xFFFFAA);
        }
    }
}
