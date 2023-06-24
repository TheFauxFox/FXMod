package dev.paw.fxmod.utils;

import dev.paw.fxmod.FXMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.util.Pair;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PanoramaMaker {
    public boolean isRunning = false;
    public int tick = 0;
    public int timer = 0;
    private Pair<Integer, Integer> initWidthHeight;
    private MinecraftClient mc;
    private String packName;
    private File packInner, packRoot;
    private final Logger logger = FXMod.LOGGER;

    public void start() {
        this.isRunning = true;
        ChatUtils.sendChatMessage("fxmod.mod.panomaker.onEnable");
        mc = MinecraftClient.getInstance();
        initWidthHeight = new Pair<>(mc.getWindow().getWidth(), mc.getWindow().getHeight());
        logger.info("Initial screen size: w{} h{}", initWidthHeight.getLeft(), initWidthHeight.getRight());
        logger.info("Building resource pack file structure");
        mc.getWindow().setWindowedSize(1024,1024);
        timer = 0;
        tick = 0;
        buildFS();
    }

    private void buildFS() {
        int nameOffset = 1;
        packName = "FXMod_Panorama";
        packRoot = new File(mc.runDirectory, "resourcepacks/FXMod_Panorama/");
        while (packRoot.exists()) {
            packName = "FXMod_Panorama_" + nameOffset;
            packRoot = new File(mc.runDirectory, "resourcepacks/"+packName+"/");
            nameOffset ++;
        }
        packInner = new File(packRoot, "assets/minecraft/textures/gui/title/background/");
        packInner.mkdirs();
        try (FileWriter writer = new FileWriter(new File(packRoot, "pack.mcmeta"))) {
            writer.write("""
                    {
                      "pack": {
                        "pack_format": 15,
                        "description": "Custom title panorama by \\u00a76FXMod\\u00a7r"
                      }
                    }""");
        } catch (IOException ignored){}
    }

    public void finish() {
        logger.info("Panorama finished as: {}", packName);
        mc.getWindow().setWindowedSize(initWidthHeight.getLeft(), initWidthHeight.getRight());
        ChatUtils.sendChatMessage("fxmod.mod.panomaker.onDisable", packName);
        ChatUtils.sendChatMessage("fxmod.mod.panomaker.note");
    }

    public void takeScreenshot() {
        NativeImage image = ScreenshotRecorder.takeScreenshot(mc.getFramebuffer());
        File file = new File(packInner,"panorama_" + tick + ".png");
        try {
            image.writeTo(file);
        } catch (IOException ignored) {}
        if (tick == 0) {
            File icon = new File(packRoot, "pack.png");
            try {
                image.writeTo(icon);
            } catch (IOException ignored) {}
        }
    }

    public void doTick() {
        if (timer >= 2 && tick < 6) {
            takeScreenshot();
            tick ++;
        }

        if (tick == 6) {
            isRunning = false;
            tick = 0;
            timer = 0;
            finish();
        }

        if (tick < 6 && timer < 2) {
            timer ++;
        }
    }

    public enum Facing {
        SOUTH(0.0F, 0.0F),
        WEST(90.0F, 0.0F),
        NORTH(180.0F, 0.0F),
        EAST(-90.0F, 0.0F),
        UP(0.0F, -90.0F),
        DOWN(0.0F, 90.0F);

        public final float yaw;

        public final float pitch;

        Facing(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public static Facing getIndex(int index) {
            return values()[index];
        }
    }
}
