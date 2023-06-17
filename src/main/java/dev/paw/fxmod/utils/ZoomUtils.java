package dev.paw.fxmod.utils;

import dev.paw.fxmod.FXMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class ZoomUtils {
    public static int zoomStep = 0;

    static double zoomDivisor = 4.0;
    static double minimumZoomDivisor = 1.0;
    static double maximumZoomDivisor = 50.0;
    static int upperScrollStep = 10;
    static int lowerScrollStep = 5;

    // The method used for changing the zoom divisor, used by zoom scrolling and the key binds
    public static void changeZoomDivisor(boolean increase) {
        zoomStep = increase ? Math.min(zoomStep + 1, upperScrollStep) :  Math.max(zoomStep - 1, -lowerScrollStep);

        if (zoomStep > 0) {
            FXMod.okZoom.setZoomDivisor(zoomDivisor + ((maximumZoomDivisor - zoomDivisor) / upperScrollStep * zoomStep));
        } else if (zoomStep == 0) {
            FXMod.okZoom.setZoomDivisor(zoomDivisor);
        } else {
            FXMod.okZoom.setZoomDivisor(zoomDivisor + ((minimumZoomDivisor - zoomDivisor) / lowerScrollStep * -zoomStep));
        }
    }

    // The method used for unbinding the "Save Toolbar Activator"
    public static void unbindConflictingKey(MinecraftClient client) {
        if (FXMod.INSTANCE.zoomKeyBind.isDefault()) {
            if (client.options.saveToolbarActivatorKey.isDefault()) {
                client.options.saveToolbarActivatorKey.setBoundKey(InputUtil.UNKNOWN_KEY);
                client.options.write();
                KeyBinding.updateKeysByCode();
            }
        }
    }
}
