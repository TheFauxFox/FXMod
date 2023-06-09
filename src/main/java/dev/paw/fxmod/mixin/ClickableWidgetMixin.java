package dev.paw.fxmod.mixin;

import dev.paw.fxmod.utils.IClickableWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClickableWidget.class)
abstract class ClickableWidgetMixin implements IClickableWidget
{
    @Shadow
    private int x;
    @Shadow
    private int y;

    @Shadow
    protected int width;
    @Shadow
    protected int height;

    @Shadow
    protected boolean hovered;
    @Shadow
    public boolean visible;

    @Shadow
    public abstract void render(DrawContext context, int mouseX, int mouseY, float delta);

    @Override
    public void _renderWithoutTooltip(DrawContext context, int mouseX, int mouseY, float delta)
    {
        if(!this.visible) {
            return;
        }

        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        this.render(context, mouseX, mouseY, delta);
    }
}

