package com.pulse.visuals.client.util;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class RenderUtils {

    /**
     * Render a line between two points in 3D space.
     * Points should already be relative to the camera position.
     */
    public static void drawLine(VertexConsumer vertexConsumer, MatrixStack.Entry entry,
                               Vec3d start, Vec3d end, int color) {
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = ((color >> 24) & 0xFF) / 255.0f;
        if (a == 0) a = 1.0f;

        float dx = (float) (end.x - start.x);
        float dy = (float) (end.y - start.y);
        float dz = (float) (end.z - start.z);
        float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length > 0) {
            dx /= length;
            dy /= length;
            dz /= length;
        } else {
            dx = 0;
            dy = 1;
            dz = 0;
        }

        vertexConsumer.vertex(entry.getPositionMatrix(), (float) start.x, (float) start.y, (float) start.z)
            .color(r, g, b, a)
            .normal(entry, dx, dy, dz);
        vertexConsumer.vertex(entry.getPositionMatrix(), (float) end.x, (float) end.y, (float) end.z)
            .color(r, g, b, a)
            .normal(entry, dx, dy, dz);
    }

    /**
     * Draw a box in 3D space
     */
    public static void drawBox(VertexConsumer vertexConsumer, MatrixStack.Entry entry,
                              Vec3d min, Vec3d max, int color) {
        // Bottom face
        drawLine(vertexConsumer, entry,
            new Vec3d(min.x, min.y, min.z),
            new Vec3d(max.x, min.y, min.z), color);
        drawLine(vertexConsumer, entry,
            new Vec3d(max.x, min.y, min.z),
            new Vec3d(max.x, min.y, max.z), color);
        drawLine(vertexConsumer, entry,
            new Vec3d(max.x, min.y, max.z),
            new Vec3d(min.x, min.y, max.z), color);
        drawLine(vertexConsumer, entry,
            new Vec3d(min.x, min.y, max.z),
            new Vec3d(min.x, min.y, min.z), color);

        // Top face
        drawLine(vertexConsumer, entry,
            new Vec3d(min.x, max.y, min.z),
            new Vec3d(max.x, max.y, min.z), color);
        drawLine(vertexConsumer, entry,
            new Vec3d(max.x, max.y, min.z),
            new Vec3d(max.x, max.y, max.z), color);
        drawLine(vertexConsumer, entry,
            new Vec3d(max.x, max.y, max.z),
            new Vec3d(min.x, max.y, max.z), color);
        drawLine(vertexConsumer, entry,
            new Vec3d(min.x, max.y, max.z),
            new Vec3d(min.x, max.y, min.z), color);

        // Vertical edges
        drawLine(vertexConsumer, entry,
            new Vec3d(min.x, min.y, min.z),
            new Vec3d(min.x, max.y, min.z), color);
        drawLine(vertexConsumer, entry,
            new Vec3d(max.x, min.y, min.z),
            new Vec3d(max.x, max.y, min.z), color);
        drawLine(vertexConsumer, entry,
            new Vec3d(max.x, min.y, max.z),
            new Vec3d(max.x, max.y, max.z), color);
        drawLine(vertexConsumer, entry,
            new Vec3d(min.x, min.y, max.z),
            new Vec3d(min.x, max.y, max.z), color);
    }

    /**
     * Convert hex color to ARGB integer
     */
    public static int hexToARGB(String hex) {
        hex = hex.replace("#", "");
        if (hex.length() == 6) {
            hex = "FF" + hex;
        }
        return (int) Long.parseLong(hex, 16);
    }

    /**
     * Get RGB component from color
     */
    public static float[] getRGB(int color) {
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        return new float[]{r, g, b};
    }
}
