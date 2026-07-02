package com.pulse.visuals.client.visual;

import com.pulse.visuals.config.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class DamageNumberRenderer {
    private final List<DamageNumber> damageNumbers = new ArrayList<>();

    public void addDamageNumber(Vec3d position, float damage, boolean isCritical) {
        if (!ModConfig.ENABLE_DAMAGE_NUMBERS) return;
        DamageNumber.DamageType type = isCritical ?
            DamageNumber.DamageType.CRITICAL :
            DamageNumber.DamageType.NORMAL;
        damageNumbers.add(new DamageNumber(position, damage, type));
    }

    public void addHealNumber(Vec3d position, float amount) {
        if (!ModConfig.ENABLE_DAMAGE_NUMBERS) return;
        damageNumbers.add(new DamageNumber(position, amount, DamageNumber.DamageType.HEAL));
    }

    public void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;
        if (damageNumbers.isEmpty()) return;

        // Update and remove dead damage numbers
        damageNumbers.removeIf(num -> !num.isAlive());
        damageNumbers.forEach(DamageNumber::update);
        if (damageNumbers.isEmpty()) return;

        Camera camera = client.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();
        MatrixStack matrices = context.matrixStack();
        VertexConsumerProvider.Immediate consumers = client.getBufferBuilders().getEntityVertexConsumers();
        if (matrices == null) return;

        TextRenderer textRenderer = client.textRenderer;

        for (DamageNumber damageNum : damageNumbers) {
            renderDamageNumber(damageNum, textRenderer, matrices, cameraPos, camera, consumers);
        }

        consumers.draw();
    }

    private void renderDamageNumber(DamageNumber damageNum, TextRenderer textRenderer, MatrixStack matrices,
                                     Vec3d cameraPos, Camera camera, VertexConsumerProvider.Immediate consumers) {
        Vec3d pos = damageNum.getPosition();

        matrices.push();
        matrices.translate(pos.x - cameraPos.x, pos.y - cameraPos.y, pos.z - cameraPos.z);
        matrices.multiply(camera.getRotation());

        float scale = -0.025f * damageNum.getScale();
        matrices.scale(scale, scale, scale);

        String text = damageNum.getDamageText();
        float textWidth = textRenderer.getWidth(text);

        int alpha = Math.max(0, Math.min(255, (int) (damageNum.getAlpha() * 255)));
        int color = (damageNum.getColor() & 0xFFFFFF) | (alpha << 24);

        textRenderer.draw(
            text,
            -textWidth / 2,
            0,
            color,
            false,
            matrices.peek().getPositionMatrix(),
            consumers,
            TextRenderer.TextLayerType.NORMAL,
            0,
            0xF000F0
        );

        matrices.pop();
    }

    public void clear() {
        damageNumbers.clear();
    }
}
