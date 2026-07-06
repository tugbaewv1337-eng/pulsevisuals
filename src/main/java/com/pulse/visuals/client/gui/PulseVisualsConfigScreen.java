package com.pulse.visuals.client.gui;

import com.pulse.visuals.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class PulseVisualsConfigScreen extends Screen {
    private static final int PURPLE = 0xFF7F77DD;
    private static final int PANEL_BG = 0xE6161616;
    private static final int ROW_BG = 0xFF1E1E22;
    private static final int TRACK_OFF = 0xFF3A3A3E;
    private static final int TEXT_MUTED = 0xFF8A8A8E;

    // Stub toggle state shared across screen opens, for features not yet implemented.
    private static final Map<String, Boolean> stubStates = new HashMap<>();
    private final Map<String, Float> knobAnim = new HashMap<>();
    private final Map<String, Float> clickFlash = new HashMap<>();

    private static int lerpColor(int colorA, int colorB, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int aA = (colorA >> 24) & 0xFF;
        int rA = (colorA >> 16) & 0xFF;
        int gA = (colorA >> 8) & 0xFF;
        int bA = colorA & 0xFF;
        int aB = (colorB >> 24) & 0xFF;
        int rB = (colorB >> 16) & 0xFF;
        int gB = (colorB >> 8) & 0xFF;
        int bB = colorB & 0xFF;
        int a = (int) (aA + (aB - aA) * t);
        int r = (int) (rA + (rB - rA) * t);
        int g = (int) (gA + (gB - gA) * t);
        int b = (int) (bA + (bB - bA) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Draws a rectangle with smoothly stair-stepped (approximately rounded) corners.
     * bgColor should match whatever sits directly behind this rectangle so the
     * cut-away corner pixels blend seamlessly into it.
     */
    private static void fillRounded(DrawContext context, int x1, int y1, int x2, int y2, int color, int bgColor, int radius) {
        context.fill(x1, y1, x2, y2, color);
        for (int i = 0; i < radius; i++) {
            int cut = radius - i;
            context.fill(x1, y1 + i, x1 + cut, y1 + i + 1, bgColor);
            context.fill(x2 - cut, y1 + i, x2, y1 + i + 1, bgColor);
            context.fill(x1, y2 - i - 1, x1 + cut, y2 - i, bgColor);
            context.fill(x2 - cut, y2 - i - 1, x2, y2 - i, bgColor);
        }
    }

    private int previousBlurriness = 0;

    private final Screen parent;
    private int currentTab = 0; // 0 = Visuals, 1 = HUD, 2 = Utilities

    private TextFieldWidget searchField;

    private final List<FeatureDef> visualsFeatures = new ArrayList<>();
    private final List<FeatureDef> hudFeatures = new ArrayList<>();
    private final List<FeatureDef> utilitiesFeatures = new ArrayList<>();

    private final List<ToggleRow> visibleRows = new ArrayList<>();

    private final int[] tabStartX = new int[3];
    private final int[] tabEndX = new int[3];
    private int tabY;

    private int panelX, panelY, panelWidth, panelHeight;

    private record FeatureDef(String name, boolean implemented, BooleanSupplier getter, Consumer<Boolean> setter) {}

    private record ToggleRow(FeatureDef feature, int x, int y, int w, int h) {}

    public PulseVisualsConfigScreen(Screen parent) {
        super(Text.literal("PulseVisuals"));
        this.parent = parent;
        buildFeatureLists();
    }

    private BooleanSupplier stubGetter(String key) {
        return () -> stubStates.getOrDefault(key, false);
    }

    private Consumer<Boolean> stubSetter(String key) {
        return value -> stubStates.put(key, value);
    }

    private void buildFeatureLists() {
        // Visuals tab
        visualsFeatures.add(new FeatureDef("Hit Particles", true,
            () -> ModConfig.ENABLE_HIT_PARTICLES, v -> ModConfig.ENABLE_HIT_PARTICLES = v));
        visualsFeatures.add(new FeatureDef("Critical Hit Effect", true,
            () -> ModConfig.ENABLE_CRITICAL_EFFECT, v -> ModConfig.ENABLE_CRITICAL_EFFECT = v));
        visualsFeatures.add(new FeatureDef("Hit Sounds", true,
            () -> ModConfig.ENABLE_HIT_SOUNDS, v -> ModConfig.ENABLE_HIT_SOUNDS = v));
        visualsFeatures.add(new FeatureDef("Full Bright", true,
            () -> ModConfig.ENABLE_FULL_BRIGHT, v -> ModConfig.ENABLE_FULL_BRIGHT = v));
        visualsFeatures.add(new FeatureDef("Sprint", true,
            () -> ModConfig.ENABLE_SPRINT_HUD, v -> ModConfig.ENABLE_SPRINT_HUD = v));
        visualsFeatures.add(new FeatureDef("Totem Tracker", true,
            () -> ModConfig.ENABLE_TOTEM_TRACKER, v -> ModConfig.ENABLE_TOTEM_TRACKER = v));
        String[] visualStubs = {
            "Animations", "Aspect Ratio", "Block Overlay", "China Hat", "Crosshair",
            "Custom Hand", "Hit Bubble", "Hit Color",
            "Free Look", "Zoom", "Jump Circles", "Hitbox",
            "No Fluid", "Render Rest", "Self Name", "Time Changer",
            "World Customizer", "World Particles", "Halo", "Trails", "Target ESP"
        };
        for (String name : visualStubs) {
            visualsFeatures.add(new FeatureDef(name, false, stubGetter(name), stubSetter(name)));
        }

        // HUD tab
        hudFeatures.add(new FeatureDef("Target HUD", true,
            () -> ModConfig.ENABLE_TARGET_HUD, v -> ModConfig.ENABLE_TARGET_HUD = v));
        hudFeatures.add(new FeatureDef("Damage Numbers", true,
            () -> ModConfig.ENABLE_DAMAGE_NUMBERS, v -> ModConfig.ENABLE_DAMAGE_NUMBERS = v));
        hudFeatures.add(new FeatureDef("Armor HUD", true,
            () -> ModConfig.ENABLE_ARMOR_HUD, v -> ModConfig.ENABLE_ARMOR_HUD = v));
        hudFeatures.add(new FeatureDef("Saturation HUD", true,
            () -> ModConfig.ENABLE_SATURATION_HUD, v -> ModConfig.ENABLE_SATURATION_HUD = v));
        hudFeatures.add(new FeatureDef("Potions", true,
            () -> ModConfig.ENABLE_POTIONS_HUD, v -> ModConfig.ENABLE_POTIONS_HUD = v));
        hudFeatures.add(new FeatureDef("Cooldowns HUD", true,
            () -> ModConfig.ENABLE_COOLDOWNS_HUD, v -> ModConfig.ENABLE_COOLDOWNS_HUD = v));
        hudFeatures.add(new FeatureDef("Watermark", true,
            () -> ModConfig.ENABLE_WATERMARK, v -> ModConfig.ENABLE_WATERMARK = v));
        String[] hudStubs = {
            "Effect Notify", "Hotkeys", "Inventory HUD"
        };
        for (String name : hudStubs) {
            hudFeatures.add(new FeatureDef(name, false, stubGetter(name), stubSetter(name)));
        }

        // Utilities tab
        utilitiesFeatures.add(new FeatureDef("Predictions", true,
            () -> ModConfig.ENABLE_TRAJECTORY_PREDICTION, v -> ModConfig.ENABLE_TRAJECTORY_PREDICTION = v));
        String[] utilityStubs = {
            "Item Highlighter", "Item Pickup Logger", "Item Scroller", "Item Swap",
            "Lock Slot", "Shift Tab", "Shift Tap", "Sound Controller", "Shulker Preview", "Streamer Mode",
            "Healing Helper", "PvP Save", "Auto Leave", "Auto Reconnect", "Auto Resell",
            "Fake Player", "Anarchy Finder"
        };
        for (String name : utilityStubs) {
            utilitiesFeatures.add(new FeatureDef(name, false, stubGetter(name), stubSetter(name)));
        }
    }

    @Override
    protected void init() {
        MinecraftClient client = MinecraftClient.getInstance();
        try {
            previousBlurriness = client.options.getMenuBackgroundBlurriness().getValue();
            client.options.getMenuBackgroundBlurriness().setValue(0);
        } catch (Exception ignored) {
            // Option not present on this version; nothing to disable.
        }

        panelWidth = Math.min(920, this.width - 40);
        panelHeight = Math.min(560, this.height - 60);
        panelX = (this.width - panelWidth) / 2;
        panelY = (this.height - panelHeight) / 2;

        searchField = new TextFieldWidget(this.textRenderer, panelX + panelWidth - 220, panelY + 20, 190, 20, Text.literal("Search"));
        searchField.setPlaceholder(Text.literal("Search..."));
        addDrawableChild(searchField);

        tabY = panelY + 55;
    }

    private List<FeatureDef> currentFeatureList() {
        return switch (currentTab) {
            case 1 -> hudFeatures;
            case 2 -> utilitiesFeatures;
            default -> visualsFeatures;
        };
    }

    private void rebuildRows() {
        visibleRows.clear();
        String query = searchField != null ? searchField.getText().toLowerCase() : "";

        int startY = tabY + 30;
        int colWidth = (panelWidth - 60) / 2;
        int rowHeight = 30;
        int gap = 8;

        int col = 0;
        int row = 0;
        for (FeatureDef feature : currentFeatureList()) {
            if (!query.isEmpty() && !feature.name().toLowerCase().contains(query)) {
                continue;
            }
            int x = panelX + 20 + col * (colWidth + gap);
            int y = startY + row * (rowHeight + gap);
            if (y + rowHeight > panelY + panelHeight - 10) {
                break;
            }
            visibleRows.add(new ToggleRow(feature, x, y, colWidth, rowHeight));
            col++;
            if (col >= 2) {
                col = 0;
                row++;
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Darken the background without the vanilla blur shader
        context.fill(0, 0, this.width, this.height, 0x99000000);

        // Main panel background
        fillRounded(context, panelX, panelY, panelX + panelWidth, panelY + panelHeight, PANEL_BG, 0x99000000, 12);

        // Title
        context.drawCenteredTextWithShadow(this.textRenderer, "PulseVisuals", panelX + panelWidth / 2, panelY + 8, 0xFFFFFFFF);

        // Tabs
        String[] tabNames = {"Visuals", "HUD", "Utilities"};
        int tabX = panelX + 20;
        for (int i = 0; i < tabNames.length; i++) {
            int color = (i == currentTab) ? 0xFFFFFFFF : (int) TEXT_MUTED;
            tabStartX[i] = tabX;
            context.drawTextWithShadow(this.textRenderer, tabNames[i], tabX, tabY, color);
            tabEndX[i] = tabX + this.textRenderer.getWidth(tabNames[i]);
            tabX = tabEndX[i] + 24;
        }
        context.fill(panelX + 20, tabY + 12, panelX + panelWidth - 20, tabY + 13, 0x33FFFFFF);

        rebuildRows();

        for (ToggleRow toggleRow : visibleRows) {
            boolean hovered = mouseX >= toggleRow.x() && mouseX <= toggleRow.x() + toggleRow.w()
                && mouseY >= toggleRow.y() && mouseY <= toggleRow.y() + toggleRow.h();
            int rowColor = hovered ? 0xFF29293A : ROW_BG;

            int rx = toggleRow.x();
            int ry = toggleRow.y();
            int rw = toggleRow.w();
            int rh = toggleRow.h();

            // Main body with smooth rounded corners
            fillRounded(context, rx, ry, rx + rw, ry + rh, rowColor, PANEL_BG, 10);

            if (hovered) {
                // Accent bar on the left, like an active/selected row
                context.fill(rx, ry + 8, rx + 3, ry + rh - 8, PURPLE);
            }

            // Click flash: brief bright pulse that fades out after pressing
            String flashKey = toggleRow.feature().name();
            float flash = clickFlash.getOrDefault(flashKey, 0f);
            if (flash > 0f) {
                int alpha = (int) (flash * 90);
                int flashColor = (alpha << 24) | 0xFFFFFF;
                fillRounded(context, rx, ry, rx + rw, ry + rh, flashColor, rowColor, 10);
                flash -= 0.12f;
                clickFlash.put(flashKey, Math.max(0f, flash));
            }

            int textColor = toggleRow.feature().implemented() ? 0xFFFFFFFF : (int) TEXT_MUTED;
            context.drawTextWithShadow(this.textRenderer, toggleRow.feature().name(), toggleRow.x() + 10, toggleRow.y() + 10, textColor);

            // Toggle switch (track + knob) with smooth sliding animation
            boolean on = toggleRow.feature().getter().getAsBoolean();
            String animKey = toggleRow.feature().name();
            float target = on ? 1f : 0f;
            float current = knobAnim.getOrDefault(animKey, target);
            current += (target - current) * 0.35f;
            if (Math.abs(target - current) < 0.01f) {
                current = target;
            }
            knobAnim.put(animKey, current);

            int trackX2 = toggleRow.x() + toggleRow.w() - 10;
            int trackX1 = trackX2 - 34;
            int trackY1 = toggleRow.y() + 9;
            int trackY2 = trackY1 + 12;
            int trackColor = lerpColor((int) TRACK_OFF, PURPLE, current);
            fillRounded(context, trackX1, trackY1, trackX2, trackY2, trackColor, rowColor, 6);

            int knobSize = 10;
            int knobTravel = (trackX2 - knobSize - 1) - (trackX1 + 1);
            int knobX = trackX1 + 1 + Math.round(current * knobTravel);
            fillRounded(context, knobX, trackY1 + 1, knobX + knobSize, trackY2 - 1, 0xFFFFFFFF, trackColor, 3);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int i = 0; i < 3; i++) {
                if (mouseX >= tabStartX[i] && mouseX <= tabEndX[i] && mouseY >= tabY - 10 && mouseY <= tabY + 14) {
                    currentTab = i;
                    return true;
                }
            }

            for (ToggleRow toggleRow : visibleRows) {
                if (mouseX >= toggleRow.x() && mouseX <= toggleRow.x() + toggleRow.w()
                    && mouseY >= toggleRow.y() && mouseY <= toggleRow.y() + toggleRow.h()) {
                    boolean current = toggleRow.feature().getter().getAsBoolean();
                    toggleRow.feature().setter().accept(!current);
                    clickFlash.put(toggleRow.feature().name(), 1f);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        MinecraftClient client = MinecraftClient.getInstance();
        try {
            client.options.getMenuBackgroundBlurriness().setValue(previousBlurriness);
        } catch (Exception ignored) {
            // Option not present on this version; nothing to restore.
        }
        if (client != null) {
            client.setScreen(parent);
        }
    }
}
