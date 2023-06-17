package dev.paw.fxmod;

import dev.paw.fxmod.settings.FXOptions;
import dev.paw.fxmod.settings.FXSettingsScreen;
import dev.paw.fxmod.utils.*;
import io.github.ennuil.libzoomer.api.ZoomInstance;
import io.github.ennuil.libzoomer.api.modifiers.ZoomDivisorMouseModifier;
import io.github.ennuil.libzoomer.api.transitions.SmoothTransitionMode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.chunk.ChunkStatus;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FXMod implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("fxmod");
    public static FXMod INSTANCE;
    public static MinecraftClient MC;
    public static FXOptions OPTIONS;
    public static final FXModVars VARS = new FXModVars();
    public static final PreciseBlockPlacing precisePlacing = new PreciseBlockPlacing();

    public KeyBinding toolBreakingOverrideKeybind, zoomKeyBind;

    public static final ZoomInstance okZoom = new ZoomInstance(
        new Identifier("fxmod:zoom"),
        4.0F, new SmoothTransitionMode(),
        new ZoomDivisorMouseModifier(), null
    );

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        MC = MinecraftClient.getInstance();
        OPTIONS = new FXOptions();

        registerKeybinds();
        registerCallbacks();

        LOGGER.info("Loaded");
    }

    private void registerKeybinds()
    {
        KeyBinding openSettingsMenuKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fxmod.options.keybind.name", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FXMod"));
        KeyBinding fullbrightKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fxmod.mod.fullbright.name", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FXMod"));
        KeyBinding beeespKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fxmod.mod.beeesp.name", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FXMod"));
        KeyBinding freecamKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fxmod.mod.freecam.name", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FXMod"));
        KeyBinding stepKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fxmod.mod.step.name", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FXMod"));
        toolBreakingOverrideKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fxmod.mod.notoolbreak.keybind", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_ALT, "FXMod"));
        zoomKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fxmod.mod.betterzoom.name", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "FXMod"));

        ClientTickEvents.END_WORLD_TICK.register(client ->
        {
            while(openSettingsMenuKeybind.wasPressed()) {
                FXMod.MC.setScreen(new FXSettingsScreen(FXMod.MC.currentScreen));
            }

            handleFeatureKeybindPress(fullbrightKeybind, FXMod.OPTIONS.fullbright, "fxmod.mod.fullbright.name");
            handleFeatureKeybindPress(beeespKeybind, FXMod.OPTIONS.beeESP, "fxmod.mod.beeesp.name");
            handleFeatureKeybindPress(freecamKeybind, FXMod.OPTIONS.freecam, "fxmod.mod.freecam.name");
            handleFeatureKeybindPress(stepKeybind, FXMod.OPTIONS.step, "fxmod.mod.step.name");
        });
    }

    private void handleFeatureKeybindPress(KeyBinding keybind, SimpleOption<Boolean> option, String key)
    {
        while(keybind.wasPressed()) {
            option.setValue(!option.getValue());

            if(option.getValue()) {
                FXMod.MC.inGameHud.getChatHud().addMessage(Text.translatable("fxmod.chat.prefix", Text.translatable("fxmod.mod.onEnable", Text.translatable(key))));
            }
            else {
                FXMod.MC.inGameHud.getChatHud().addMessage(Text.translatable("fxmod.chat.prefix", Text.translatable("fxmod.mod.onDisable", Text.translatable(key))));
            }
        }
    }

    public boolean toolIsNotOverriden() {
        return !toolBreakingOverrideKeybind.isPressed();
    }

    public boolean isZooming() {
        return zoomKeyBind.isPressed();
    }

    public void onReady() {
        ZoomUtils.unbindConflictingKey(MC);
    }

    private void registerCallbacks()
    {
        ClientTickEvents.END_WORLD_TICK.register(clientWorld ->
        {
            precisePlacing.clientTick(MC);

            if (okZoom.setZoom(zoomKeyBind.isPressed())) {
                VARS.wasZooming = true;
            } else if (!zoomKeyBind.isPressed() && VARS.wasZooming) {
                okZoom.resetZoomDivisor();
                ZoomUtils.zoomStep = 0;
            }

            if(FXMod.OPTIONS.toolWarning.getValue() && MC.player != null) {
                ItemStack mainHandItem = FXMod.MC.player.getStackInHand(Hand.MAIN_HAND);
                ItemStack offHandItem = FXMod.MC.player.getStackInHand(Hand.OFF_HAND);

                int mainHandDurability = mainHandItem.getMaxDamage() - mainHandItem.getDamage();
                int offHandDurability = offHandItem.getMaxDamage() - offHandItem.getDamage();

                if(mainHandItem.isDamaged() && mainHandItem != FXMod.VARS.mainHandToolItemStack) {
                    if(MathHelper.floor(mainHandItem.getMaxDamage() * 0.9f) < mainHandItem.getDamage() + 1 && mainHandDurability < 13) {
                        FXMod.VARS.toolDurability = mainHandDurability;
                        FXMod.VARS.toolHand = Hand.MAIN_HAND;
                        FXMod.VARS.resetToolWarningTicks();
                    }
                }

                if(offHandItem.isDamaged() && offHandItem != FXMod.VARS.offHandToolItemStack) {
                    if(MathHelper.floor(offHandItem.getMaxDamage() * 0.9f) < offHandItem.getDamage() + 1 && offHandDurability < 13) {
                        if(mainHandDurability == 0 || offHandDurability < mainHandDurability) {
                            FXMod.VARS.toolDurability = offHandDurability;
                            FXMod.VARS.toolHand = Hand.OFF_HAND;
                            FXMod.VARS.resetToolWarningTicks();
                        }
                    }
                }

                FXMod.VARS.mainHandToolItemStack = mainHandItem;
                FXMod.VARS.offHandToolItemStack = offHandItem;
            }

            if (FXMod.OPTIONS.step.getValue() && MC.player != null) {
                if(MC.player.isSneaking()) {
                    MC.player.setStepHeight(0.6f);
                } else if (MC.player.getStepHeight() < 1.0f) {
                    MC.player.setStepHeight(1.25f);
                }
            } else {
                if (MC.player != null && MC.player.getStepHeight() > 0.6f) {
                    MC.player.setStepHeight(0.6f);
                }
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (FXMod.MC.player == null && FXMod.OPTIONS.freecam.getValue()) {
                // Disable freecam if we dont have a player (leaving world n stuff)
                FXMod.OPTIONS.freecam.setValue(false);
            }
        });

        WorldRenderEvents.START.register(context -> {
            if (VARS.renderedFakeNametags.size() > 0) {
                for (ArmorStandEntity ent: VARS.renderedFakeNametags) {
                    ent.discard();
                }
            }

            VARS.renderedFakeNametags.clear();
        });

        WorldRenderEvents.AFTER_ENTITIES.register((context) ->
            OptifineHooks.doOptifineAwareRender(context, (context1, simple) -> {
                if (OPTIONS.beeESP.getValue()) {
                    Render3d.enable(context);

                    WorldUtils.getBlocksInRadius(2, BeehiveBlockEntity.class, ChunkStatus.SURFACE).forEach((pos) ->
                        Render3d.drawBox(context, pos, new Color(255,255,0))
                    );

                    Render3d.disable(context);
                }
                if (OPTIONS.spawnerESP.getValue()) {
                    WorldUtils.getBlocksInRadius(8, MobSpawnerBlockEntity.class, ChunkStatus.FULL).forEach((pos) -> {
                        String mobSpawnerType;
                        if (MC.world != null && (mobSpawnerType = MobSpawnerUtils.getMobSpawnerType(pos)) != null) {
                            Render3d.enable(context);
                            Render3d.drawBox(context, pos, new Color(25, 25, 25));
                            Render3d.disable(context);
                            if (OPTIONS.spawnerESPTags.getValue()) {
                                Render3d.drawTag(pos, mobSpawnerType + " Spawner");
                            }
                        }
                    });

                }
            })
        );

    }
}
