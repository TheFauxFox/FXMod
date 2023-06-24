package dev.paw.fxmod.utils;

import dev.paw.fxmod.FXMod;
import net.minecraft.text.Text;

public class ChatUtils {
    public static void sendChatMessage(String key, Object... arg) {
        FXMod.MC.inGameHud.getChatHud().addMessage(i18n("fxmod.chat.prefix", i18n(key, arg)));
    }

    public static Text i18n(String key, Object... arg) {
        return Text.translatable(key, arg);
    }
}
