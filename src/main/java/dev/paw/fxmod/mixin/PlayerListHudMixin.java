package dev.paw.fxmod.mixin;

import dev.paw.fxmod.FXMod;
import dev.paw.fxmod.utils.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    @ModifyVariable(method = "render", at = @At(value = "STORE"), ordinal = 7)
    private int modifyN(int n) {
        // Fix rendering issue, set width of "icon" to width of max ping
        return n + MinecraftClient.getInstance().textRenderer.getWidth("9999ms");
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;renderLatencyIcon(Lnet/minecraft/client/gui/DrawContext;IIILnet/minecraft/client/network/PlayerListEntry;)V"))
    private void renderLatencyIcon(PlayerListHud playerListHud, DrawContext drawContext, int width, int x, int y, PlayerListEntry playerListEntry) {
        if (FXMod.OPTIONS.pingDisplay.getValue()) {
            MinecraftClient client = MinecraftClient.getInstance();
            int latency = playerListEntry.getLatency();
            int color = new Color(102, 255, 136).getPacked(); // Green = Good, <=150 ping
            if(latency > 300) {
                color = new Color(255, 82, 82).getPacked(); // Red = Dogshit, > 300 ping
            } else if (latency > 150) {
                color = new Color(255, 82, 82).getPacked(); // Orange = Mid, > 150, <= 300 ping
            }
            String strLatency = latency + "ms";
            int strOffset = client.textRenderer.getWidth(strLatency);
            drawContext.drawTextWithShadow(client.textRenderer, strLatency, x + width - strOffset, y, color);
        }
    }
}

