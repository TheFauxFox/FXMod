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
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FXMod implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("fxmod");
    public static FXMod INSTANCE;
    public static MinecraftClient MC;
    public static FXOptions OPTIONS;
    public static final FXModVars VARS = new FXModVars();

    private KeyBinding toolBreakingOverrideKeybind;

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
        toolBreakingOverrideKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fxmod.mod.notoolbreak.keybind", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_ALT, "FXMod"));

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

    public boolean isToolBreakingOverriden() {
        return toolBreakingOverrideKeybind.isPressed();
    }

    private void registerCallbacks()
    {

        ClientTickEvents.END_WORLD_TICK.register(clientWorld ->
        {
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

        });
    }
}
