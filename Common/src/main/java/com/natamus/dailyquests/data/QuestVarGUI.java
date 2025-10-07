package com.natamus.dailyquests.data;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class QuestVarGUI {
	public final Component textComponent;
	public final FormattedCharSequence textSequence;
	public final Pair<Integer, Integer> questProgressPair;
	public final int xPosition;
	public final int yPosition;
	public final int rgb;
	public final float scale;

	public QuestVarGUI(Component textComponent, FormattedCharSequence textSequence, Pair<Integer, Integer> questProgressPair, int xPosition, int yPosition, int rgb, float scale) {
		this.textComponent = textComponent;
		this.textSequence = textSequence;
		this.questProgressPair = questProgressPair;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.rgb = rgb;
		this.scale = scale;
	}
}
