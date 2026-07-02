package com.pulse.visuals.client.visual;

import com.pulse.visuals.config.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

public class HitParticleRenderer {

    public void addHitParticle(Vec3d position, boolean isCritical) {
        if (!ModConfig.ENABLE_HIT_PARTICLES) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        ParticleEffect effect = isCritical ? ParticleTypes.CRIT : ParticleTypes.DAMAGE_INDICATOR;
        int count = isCritical ? 8 : 4;

        for (int i = 0; i < count; i++) {
            double offsetX = (client.world.random.nextDouble() - 0.5) * 0.5;
            double offsetY = client.world.random.nextDouble() * 0.5;
            double offsetZ = (client.world.random.nextDouble() - 0.5) * 0.5;

            double velocityX = (client.world.random.nextDouble() - 0.5) * 0.1;
            double velocityY = client.world.random.nextDouble() * 0.1;
            double velocityZ = (client.world.random.nextDouble() - 0.5) * 0.1;

            client.world.addParticle(
                effect,
                position.x + offsetX,
                position.y + offsetY,
                position.z + offsetZ,
                velocityX, velocityY, velocityZ
            );
        }
    }

    // Vanilla particles render and animate themselves, nothing to do per-frame here.
    public void render(WorldRenderContext context) {
    }

    public void clear() {
    }
}
