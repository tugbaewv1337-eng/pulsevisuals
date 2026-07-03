package com.pulse.visuals;

import com.pulse.visuals.client.event.ClientEventHandler;
import com.pulse.visuals.client.visual.HitParticleRenderer;
import com.pulse.visuals.client.visual.TargetHUD;
import com.pulse.visuals.client.visual.DamageNumberRenderer;
import com.pulse.visuals.client.visual.TrajectoryRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PulseVisualsClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PulseVisuals");
    public static final String MOD_ID = "pulsevisuals";

    private static HitParticleRenderer hitParticleRenderer;
    private static TargetHUD targetHUD;
    private static DamageNumberRenderer damageNumberRenderer;
    private static TrajectoryRenderer trajectoryRenderer;
    private static ClientEventHandler eventHandler;

    public static KeyBinding openPanelKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing PulseVisuals...");

        // Initialize renderers
        hitParticleRenderer = new HitParticleRenderer();
        targetHUD = new TargetHUD();
        damageNumberRenderer = new DamageNumberRenderer();
        trajectoryRenderer = new TrajectoryRenderer();
        eventHandler = new ClientEventHandler();

        // Register the key used to open the settings panel (default: Right Shift)
        openPanelKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.pulsevisuals.openpanel",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.pulsevisuals"
        ));

        // Register event listeners
        ClientTickEvents.END_CLIENT_TICK.register(eventHandler::onClientTick);
        WorldRenderEvents.END.register(eventHandler::onWorldRenderEnd);
        HudRenderCallback.EVENT.register(eventHandler::onHudRender);

        LOGGER.info("PulseVisuals initialized successfully!");
    }

    public static HitParticleRenderer getHitParticleRenderer() {
        return hitParticleRenderer;
    }

    public static TargetHUD getTargetHUD() {
        return targetHUD;
    }

    public static DamageNumberRenderer getDamageNumberRenderer() {
        return damageNumberRenderer;
    }

    public static TrajectoryRenderer getTrajectoryRenderer() {
        return trajectoryRenderer;
    }
}
