package dev.paw.fxmod.settings;

import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.paw.fxmod.FXMod;
import dev.paw.fxmod.utils.ISimpleOption;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.SimpleOption.TooltipFactory;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.TranslatableOption;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FXOptions
{
	private final File file;
	private final Map<String, SimpleOption<?>> features;

	// all the FEATURES
	public final SimpleOption<Boolean> fullbright;

	public FXOptions()
	{
		this.file = new File(FXMod.MC.runDirectory, "config/fxmod.properties");
		this.features = new HashMap<>();

		fullbright = SimpleOption.ofBoolean(
			"fxmod.mod.fullbright.name",
			tooltip("fxmod.mod.fullbright.tooltip", false),
			false
		);
		features.put("fullbright", fullbright);

		init();
	}

	private static <T> TooltipFactory<T> tooltip(String key, T defaultValue)
	{
		return value -> {
			List<Text> lines = new ArrayList<>();
			lines.add(Text.translatable(key));

			if(defaultValue instanceof Double) {
				// double is mostly used with percent so should be fine, just leaving this in case I forgot and rage why it shows percent even tho it should not
				lines.add(Text.translatable("fxmod.options.mod_default", (int)((double)defaultValue * 100.0)).append("%").formatted(Formatting.GRAY));
			}
			else if(defaultValue instanceof Boolean) {
				lines.add(Text.translatable("fxmod.options.mod_default", (boolean)defaultValue ? ScreenTexts.ON : ScreenTexts.OFF).formatted(Formatting.GRAY));
			}
			else if(defaultValue instanceof TranslatableOption) {
				lines.add(Text.translatable("fxmod.options.mod_default", ((TranslatableOption) defaultValue).getText()).formatted(Formatting.GRAY));
			}
			else {
				lines.add(Text.translatable("fxmod.options.mod_default", defaultValue).formatted(Formatting.GRAY));
			}

			return Tooltip.of(Texts.join(lines, Text.of("\n")));
		};
	}

	private static <T> TooltipFactory<T> tooltipHT(String key, T defaultValue)
	{
		return value -> {
			List<Text> lines = new ArrayList<>();
			lines.add(Text.translatable(key));
			lines.add(Text.translatable("fxmod.options.mod_default", (Integer)defaultValue / 10.0D).formatted(Formatting.GRAY));
			return Tooltip.of(Texts.join(lines, Text.of("\n")));
		};
	}

	public static Text getHTText(Text prefix, int value)
	{
		return Text.translatable("options.generic_value", prefix, value / 10.0D);
	}

	public static Text getValueText(Text prefix, int value)
	{
		return Text.translatable("options.generic_value", prefix, value);
	}

	public static Text getValueText(Text prefix, double value)
	{
		return Text.translatable("options.generic_value", prefix, value);
	}

	private static Text getPercentValueText(Text prefix, double value)
	{
		if(value == 0.0) {
			return ScreenTexts.composeToggleText(prefix, false);
		}
		
		return Text.translatable("options.percent_value", prefix, (int)(value * 100.0));
	}

	private void init()
	{
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			write();
		}
		else {
			read();
		}
	}

	@SuppressWarnings("unchecked")
	private <T> void resetFeature(SimpleOption<T> feature)
	{
		((ISimpleOption<T>)(Object) feature)._setValueToDefault();
	}

	public void reset()
	{
		for(SimpleOption<?> feature : features.values()) {
			resetFeature(feature);
		}
	}

	public void write()
	{
		try(PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
			printWriter.println("# fxmod configuration. Do not edit here unless you know what you're doing!");
			printWriter.println("# Last save: " + DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy").format(LocalDateTime.now()));

			for(Entry<String, SimpleOption<?>> feature : features.entrySet()) {
				printWriter.println(feature.getKey() + "=" + feature.getValue().getValue());
			}
		}
		catch(Exception e) {
			FXMod.LOGGER.error("Failed to write to 'fxmod.properties': {}", e.toString());
		}
	}

	private <T> void parseFeatureLine(SimpleOption<T> option, String value)
	{
		DataResult<T> dataResult = option.getCodec().parse(JsonOps.INSTANCE, JsonParser.parseString(value));
		dataResult.error().ifPresent(partialResult -> FXMod.LOGGER.warn("Skipping bad config option (" + value + "): " + partialResult.message()));
		dataResult.result().ifPresent(option::setValue);
	}

	private void read()
	{
		try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
			bufferedReader.lines().forEach((line) -> {
				if(line.startsWith("#")) {
					// skips comments
					return;
				}

				String[] v = line.split("=");

				if(v.length != 2) {
					FXMod.LOGGER.warn("Skipping bad config option line!");
					return;
				}

				String key = v[0];
				String value = v[1];

				SimpleOption<?> option = features.get(key);

				if(option == null || value.isEmpty()) {
					FXMod.LOGGER.warn("Skipping bad config option (" + value + ")" + " for " + key);
				}
				else {
					parseFeatureLine(option, value);
				}
			});
		}
		catch(IOException e) {
			FXMod.LOGGER.error("Failed to read from 'fxmod.properties': {}", e.toString());
		}
	}
}