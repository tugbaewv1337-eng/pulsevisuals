package com.pulse.visuals.client.visual;

import com.pulse.visuals.config.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class HitParticleRenderer {

    public void addHitParticle(Vec3d position, boolean isCritical) {
        if (!ModConfig.ENABLE_HIT_PARTICLES) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        float r;
        float g;
        float b;
        if (isCritical) {
            // Red-orange for critical hits
            r = 1.0f;
            g = 0.42f;
            b = 0.23f;
        } else {
            // Gold for normal hits
            r = 1.0f;
            g = 0.84f;
            b = 0.0f;
        }

        float scale = (isCritical ? 1.5f : 1.0f) * ModConfig.HIT_PARTICLE_SCALE;
        int count = isCritical ? 8 : 4;

        DustParticleEffect effect = new DustParticleEffect(new Vector3f(r, g, b), scale);

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
