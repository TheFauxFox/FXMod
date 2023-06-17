package dev.paw.fxmod.settings;

import dev.paw.fxmod.FXMod;
import dev.paw.fxmod.utils.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class FXSettingsScreen extends Screen
{
	private final Screen parent;
	private NewButtonListWidget list;

	public static Screen getNewScreen(Screen parent)
	{
		return new FXSettingsScreen(parent);
	}

	public FXSettingsScreen(Screen parent)
	{
		super(Text.translatable("fxmod.options.title"));
		this.parent = parent;
	}

	protected void init()
	{
		this.list = new NewButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
		this.list.addCategoryEntry("fxmod.options.category.render");
		this.list.addOptionEntry(FXMod.OPTIONS.fullbright, FXMod.OPTIONS.beeESP);
		this.list.addOptionEntry(FXMod.OPTIONS.noPotionParticles, FXMod.OPTIONS.fpsdisplay);
		this.list.addOptionEntry(FXMod.OPTIONS.spawnerESP);
		this.list.addOptionEntry(FXMod.OPTIONS.noFog, FXMod.OPTIONS.lowFire);
		this.list.addCategoryEntry("fxmod.options.category.tools");
		this.list.addOptionEntry(FXMod.OPTIONS.toolWarning, FXMod.OPTIONS.noToolBreaking);
		this.list.addOptionEntry(FXMod.OPTIONS.preciseBlockPlace);
		this.list.addCategoryEntry("fxmod.options.category.misc");
		this.list.addOptionEntry(FXMod.OPTIONS.freecam, FXMod.OPTIONS.freecamOutline);
		this.list.addOptionEntry(FXMod.OPTIONS.step);
		this.addSelectableChild(this.list);
		
		// DEFAULTS button at the top left corner
		this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("fxmod.options.mod_default.button"), (buttonWidget) -> {
				FXMod.OPTIONS.reset();
				this.client.setScreen(getNewScreen(parent));
			})
			.dimensions(6, 6, 55, 20)
			.tooltip(Tooltip.of(Text.translatable("fxmod.options.mod_default.button.tooltip").formatted(Formatting.YELLOW)))
			.build()
		);

		// DONE button at the bottom
		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.DONE, (buttonWidget) -> {
				FXMod.OPTIONS.write();
				this.client.setScreen(parent);
			})
			.dimensions(this.width / 2 - 100, this.list.getBottom() + ((this.height - this.list.getBottom() - 20) / 2), 200, 20)
			.build()
		);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(context);
		this.list.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12, Color.WHITE.getPacked());

		super.render(context, mouseX, mouseY, delta);
	}

	@Override
	public void removed()
	{
		FXMod.OPTIONS.write();
	}

	@Override
	public void close()
	{
		this.client.setScreen(parent);
	}
}
