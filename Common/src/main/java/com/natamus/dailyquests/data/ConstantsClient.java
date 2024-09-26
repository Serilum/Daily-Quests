package com.natamus.dailyquests.data;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class ConstantsClient {
	public static final Minecraft mc = Minecraft.getInstance();

	public static KeyMapping toggleQuestListKey;

	public static final int questTitleRGB = new Color(19, 168, 16, 255).getRGB();
	public static final int questDescriptionRGB = new Color(220, 219, 10, 255).getRGB();
	public static final int questProgressBarRedRGB = new Color(180, 18, 18, 255).getRGB();
	public static final int questProgressBarCollapsedRedRGB = new Color(255, 77, 77, 180).getRGB();
	public static final int questProgressBarGreenRGB = new Color(75, 115, 65, 255).getRGB();

	public static final int questBackgroundRGB = new Color(202, 175, 127, 100).getRGB();
	public static final int questBackgroundBorderRGB = new Color(222, 183, 134, 255).getRGB();
	public static final int questBackgroundBorderThickness = 2;
}
