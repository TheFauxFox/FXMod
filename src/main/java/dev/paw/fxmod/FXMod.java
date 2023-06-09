package dev.paw.fxmod;

import dev.paw.fxmod.settings.FXOptions;
import dev.paw.fxmod.settings.FXSettingsScreen;
import dev.paw.fxmod.utils.FXModVars;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FXMod implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("fxmod");
    public static FXMod INSTANCE;
    public static MinecraftClient MC;
    public static FXOptions OPTIONS;
    public static final FXModVars VARS = new FXModVars();

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
        KeyBinding openSettingsMenuKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fxmod.options.keybind.name", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "fxmod"));
        KeyBinding fullbrightKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fxmod.mod.fullbright.name", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "fxmod"));

        ClientTickEvents.END_WORLD_TICK.register(client ->
        {
            while(openSettingsMenuKeybind.wasPressed()) {
                FXMod.MC.setScreen(new FXSettingsScreen(FXMod.MC.currentScreen));
            }

            handleFeatureKeybindPress(fullbrightKeybind, FXMod.OPTIONS.fullbright, "fxmod.mod.fullbright.name");
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

    private void registerCallbacks() {

    }
}
