package com.natamus.dailyquests.events;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.ConstantsClient;
import com.natamus.dailyquests.data.QuestVarGUI;
import com.natamus.dailyquests.data.VariablesClient;
import com.natamus.dailyquests.util.UtilClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DailyQuestsClientEvents {
	private static boolean toggling = false;
	private static int cachedInitialY = -1;
	private static int cachedcalculatedY = -1;
	private static int cachedToggleY = -1;

	public static void onClientTick() {
		if (ConstantsClient.mc.player == null) {
			UtilClient.resetQuestDataClient();
		}
	}

	public static void toggleQuestListCollapse() {
		if (!VariablesClient.questListCollapsed) {
			cachedToggleY = cachedcalculatedY;
		}

		VariablesClient.questListCollapsed = !VariablesClient.questListCollapsed;
		toggling = true;
	}

	public static void renderOverlay(GuiGraphics guiGraphics, @Nullable DeltaTracker deltaTracker, @Nullable PauseScreen pauseScreen) {
		if (pauseScreen != null && deltaTracker != null) {
			return;
		}

		if (ConstantsClient.mc.gui.getDebugOverlay().showDebugScreen()) {
			return;
		}

		if (VariablesClient.questTitles.size() == 0) {
			return;
		}

		PoseStack poseStack = guiGraphics.pose();
		poseStack.pushPose();

		Font font = ConstantsClient.mc.font;
		Window scaled = ConstantsClient.mc.getWindow();
		int width = scaled.getGuiScaledWidth();

		int xPosition = width - 90 + ConfigHandler.questListHorizontalOffset;

		int heightOffset = ConfigHandler.questListVerticalOffset + UtilClient.getExtraEffectHeightOffset();
		int yPosition = 5 + heightOffset, initialY = 5 + heightOffset;

		if (toggling) {
			if ((cachedToggleY <= 20 && VariablesClient.questListCollapsed) || (cachedToggleY >= cachedcalculatedY && !VariablesClient.questListCollapsed)) {
				toggling = false;
			}
			else {
				guiGraphics.fill(xPosition - 5, cachedInitialY - 5, width - 5, cachedToggleY + 5, ConstantsClient.questBackgroundRGB);

				guiGraphics.fill(xPosition - 5 - ConstantsClient.questBackgroundBorderThickness, cachedInitialY - 5 - ConstantsClient.questBackgroundBorderThickness, width - 5 + ConstantsClient.questBackgroundBorderThickness, cachedInitialY - 5, ConstantsClient.questBackgroundBorderRGB);
				guiGraphics.fill(xPosition - 5 - ConstantsClient.questBackgroundBorderThickness, cachedToggleY + 5, width - 5 + ConstantsClient.questBackgroundBorderThickness, cachedToggleY + 5 + ConstantsClient.questBackgroundBorderThickness, ConstantsClient.questBackgroundBorderRGB);
				guiGraphics.fill(xPosition - 5 - ConstantsClient.questBackgroundBorderThickness, cachedInitialY - 5, xPosition - 5, cachedToggleY + 5, ConstantsClient.questBackgroundBorderRGB);
				guiGraphics.fill(width - 5, cachedInitialY - 5, width - 5 + ConstantsClient.questBackgroundBorderThickness, cachedToggleY + 5, ConstantsClient.questBackgroundBorderRGB);

				if (VariablesClient.questListCollapsed) {
					cachedToggleY -= 3;
				} else {
					cachedToggleY += 3;
				}

				poseStack.popPose();
				return;
			}
		}

		int questCount = Math.max(VariablesClient.questTitles.size(), VariablesClient.questDescriptions.size());;
		List<QuestVarGUI> elementsToDraw = new ArrayList<>();
		for (int n = 0; n < questCount; n++) {
			Pair<Integer, Integer> questProgressPair = (VariablesClient.questProgress.size() > n)
				? VariablesClient.questProgress.get(n)
				: Pair.of(0, 0);

			String questTitle = (n < VariablesClient.questTitles.size()) ? VariablesClient.questTitles.get(n) : "";
			String questDescription = (n < VariablesClient.questDescriptions.size()) ? VariablesClient.questDescriptions.get(n) : "";

			if (!VariablesClient.questListCollapsed) {
				elementsToDraw.add(new QuestVarGUI(Component.literal(questTitle), null, questProgressPair, xPosition, yPosition, ConstantsClient.questTitleRGB, 0.8F));
				yPosition += 11;

				if (!questDescription.isEmpty()) {
					List<FormattedCharSequence> descriptionLines = ComponentRenderUtils.wrapComponents(Component.literal(questDescription), 95, font);
					for (FormattedCharSequence line : descriptionLines) {
						elementsToDraw.add(new QuestVarGUI(null, line, null, (int) (xPosition * 1.25) + 5, (int) (yPosition * 1.25), ConstantsClient.questDescriptionRGB, 0F));
						yPosition += 9;
					}
				}
				else {
					yPosition += 1;
				}

				if (!questProgressPair.getFirst().equals(questProgressPair.getSecond()) && questProgressPair.getSecond() != 1) {
					elementsToDraw.add(new QuestVarGUI(null, null, questProgressPair, (int) (xPosition * 1.25) + 5, (int) (yPosition * 1.25), ConstantsClient.questProgressBarGreenRGB, 1.25F));

					yPosition += 20;
				} else {
					elementsToDraw.add(new QuestVarGUI(null, null, null, (int) (xPosition * 1.25) + 5, (int) (yPosition * 1.25), ConstantsClient.questProgressBarGreenRGB, 1.25F));

					yPosition += 7;
				}
			}
			else {
				int totalWidth = 90;
				int collapsedWidth = totalWidth / questCount;
				int remainingWidth = totalWidth % questCount;

				int xOffset = (n * collapsedWidth) + Math.min(n, remainingWidth);

				int progress = questProgressPair.getFirst();
				int goal = questProgressPair.getSecond();

				guiGraphics.fill(xPosition + xOffset - 5 - ConstantsClient.questBackgroundBorderThickness, initialY - 5 - ConstantsClient.questBackgroundBorderThickness, xPosition + xOffset + collapsedWidth - 5 + (n < remainingWidth ? 1 : 0) + ConstantsClient.questBackgroundBorderThickness, yPosition + 15 + ConstantsClient.questBackgroundBorderThickness, ConstantsClient.questBackgroundBorderRGB);

				guiGraphics.fill(xPosition + xOffset - 5, initialY - 5, xPosition + xOffset + collapsedWidth - 5 + (n < remainingWidth ? 1 : 0), yPosition + 15, ConstantsClient.questProgressBarCollapsedRedRGB);

				int progressWidth = (int) ((double) progress / goal * (collapsedWidth + (n < remainingWidth ? 1 : 0)));

				guiGraphics.fill(xPosition + xOffset - 5, initialY - 5, xPosition + xOffset - 5 + progressWidth, yPosition + 15, ConstantsClient.questProgressBarGreenRGB);

				int progressTextYOffset = 0;
				int progressTextXOffset = 0;
				String progressText;
				if (VariablesClient.playerDataObject.isShowingIntroduction()) {
					progressText = "~";
					progressTextYOffset = 3;
				}
				else {
					if (goal > 1) {
						progressText = (int) Math.floor(((double) progress / (double) goal) * 100) + "%";
					} else {
						if (progress != goal) {
							progressText = "x";
						} else {
							progressText = "✔";
							progressTextXOffset = 1;
						}
					}
				}

				int textXPosition = xPosition + xOffset - progressTextXOffset - 5 + (collapsedWidth / 2) - (font.width(progressText) / 2);
				guiGraphics.drawString(font, Component.literal(progressText), textXPosition, initialY + 1 + progressTextYOffset, 0xFFFFFFFF, ConfigHandler.questListDrawTextShadow);
			}
		}

		if (!VariablesClient.questListCollapsed) {
			yPosition -= 10;

			cachedcalculatedY = yPosition;
		}
		else {
			yPosition += 10;
		}

		cachedInitialY = initialY;

		if (!VariablesClient.questListCollapsed) {
			guiGraphics.fill(xPosition - 5, initialY - 5, width - 5, yPosition + 5, ConstantsClient.questBackgroundRGB);
		}

		guiGraphics.fill(xPosition - 5 - ConstantsClient.questBackgroundBorderThickness, initialY - 5 - ConstantsClient.questBackgroundBorderThickness, width - 5 + ConstantsClient.questBackgroundBorderThickness, initialY - 5, ConstantsClient.questBackgroundBorderRGB);
		guiGraphics.fill(xPosition - 5 - ConstantsClient.questBackgroundBorderThickness, yPosition + 5, width - 5 + ConstantsClient.questBackgroundBorderThickness, yPosition + 5 + ConstantsClient.questBackgroundBorderThickness, ConstantsClient.questBackgroundBorderRGB);
		guiGraphics.fill(xPosition - 5 - ConstantsClient.questBackgroundBorderThickness, initialY - 5, xPosition - 5, yPosition + 5, ConstantsClient.questBackgroundBorderRGB);
		guiGraphics.fill(width - 5, initialY - 5, width - 5 + ConstantsClient.questBackgroundBorderThickness, yPosition + 5, ConstantsClient.questBackgroundBorderRGB);

		int questNum = 1;
		for (QuestVarGUI questText : elementsToDraw) {
			if (questText.textComponent != null) {
				guiGraphics.drawString(font, questText.textComponent, questText.xPosition, questText.yPosition, questText.rgb, ConfigHandler.questListDrawTextShadow);

				if (!VariablesClient.playerDataObject.isShowingIntroduction()) {
					if (!questText.questProgressPair.getFirst().equals(questText.questProgressPair.getSecond())) {
						guiGraphics.drawString(font, "x", width - 15, questText.yPosition - 1, ConstantsClient.questProgressBarRedRGB, ConfigHandler.questListDrawTextShadow);
					} else {
						guiGraphics.drawString(font, "✔", width - 15, questText.yPosition - 1, ConstantsClient.questTitleRGB, ConfigHandler.questListDrawTextShadow);
					}

					if (pauseScreen != null) {
						UtilClient.addRerollButton(pauseScreen, questNum, questText.xPosition, questText.yPosition);
					}
				}

				questNum += 1;
			} else if (questText.textSequence != null) {
				guiGraphics.drawString(font, questText.textSequence, questText.xPosition, questText.yPosition, questText.rgb, ConfigHandler.questListDrawTextShadow);
			} else if (questText.questProgressPair != null) {
				int progress = questText.questProgressPair.getFirst();
				int goal = questText.questProgressPair.getSecond();
				int barWidth = 90;
				int progressWidth = (int) ((double) progress / goal * barWidth);

				// Draw the red background
				guiGraphics.fill(questText.xPosition, questText.yPosition-1, questText.xPosition + barWidth, questText.yPosition + 10, ConstantsClient.questProgressBarRedRGB);

				// Draw the green foreground
				guiGraphics.fill(questText.xPosition, questText.yPosition-1, questText.xPosition + progressWidth, questText.yPosition + 10, ConstantsClient.questProgressBarGreenRGB);

				// Draw the progress text
				String progressText = progress + " / " + goal;

				guiGraphics.drawString(font, Component.literal(progressText), questText.xPosition + (barWidth / 2) - (font.width(progressText) / 2), questText.yPosition+1, 0xFFFFFFFF, ConfigHandler.questListDrawTextShadow);
			}

			if (questText.scale != 0F) {
				poseStack.scale(questText.scale, questText.scale, questText.scale);
			}
		}

		poseStack.popPose();
	}
}