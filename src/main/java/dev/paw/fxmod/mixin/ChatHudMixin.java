package dev.paw.fxmod.mixin;

import dev.paw.fxmod.FXMod;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Inject(method = "clear(Z)V", at = @At(value = "HEAD"), cancellable = true)
    public void dontClear(boolean clearHistory, CallbackInfo ci) {
        if (clearHistory && FXMod.OPTIONS.dontClearChat.getValue()) {
            ci.cancel();
        }
    }
}
