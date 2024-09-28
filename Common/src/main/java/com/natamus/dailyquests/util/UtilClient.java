package com.natamus.dailyquests.util;

import com.mojang.datafixers.util.Pair;
import com.natamus.collective.functions.ScreenFunctions;
import com.natamus.collective.implementations.networking.api.Dispatcher;
import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.ConstantsClient;
import com.natamus.dailyquests.data.VariablesClient;
import com.natamus.dailyquests.networking.packets.ToServerAttemptReRollQuest;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;

public class UtilClient {
	public static void showDailyQuestsIntroduction(int questCount) {
		VariablesClient.questTitles = Arrays.asList(ConfigHandler.introductionQuestScreenTitles.split("\\|"));
		VariablesClient.questDescriptions = Arrays.asList(ConfigHandler.introductionQuestScreenDescriptions.replace("%questcount%", questCount + "").split("\\|"));
		VariablesClient.questProgress = new ArrayList<>();

		VariablesClient.waitingForNewQuest = false;
	}

	public static void addRerollButton(PauseScreen pauseScreen, int questNumber, int xPosition, int yPosition) {
		if (VariablesClient.playerDataObject.isShowingIntroduction()) {
			return;
		}

		if (VariablesClient.addedRerollButtons) {
			return;
		}

		if (VariablesClient.playerDataObject.getReRollsLeft() > 0) {
			boolean addButton = true;
			if (!ConfigHandler.allowReRollingCompletedQuests) {
				Pair<Integer, Integer> progressPair = VariablesClient.questProgress.get(questNumber-1);
				addButton = !progressPair.getFirst().equals(progressPair.getSecond());
			}

			if (addButton) {
				Button reRollButton = Button.builder(Component.literal("⟳"), ($$0) -> { // ⟲
					Dispatcher.sendToServer(new ToServerAttemptReRollQuest(questNumber));

					VariablesClient.waitingForNewQuest = true;
				}).bounds(xPosition - 24, yPosition - 2, 14, 14).build();

				VariablesClient.reRollButtons.put(questNumber, reRollButton);
				ScreenFunctions.addRenderableWidget(pauseScreen, reRollButton);

			}
		}

		if (questNumber == VariablesClient.questDescriptions.size()) {
			VariablesClient.addedRerollButtons = true;
		}
	}

	public static void resetReRollButtons() {
		VariablesClient.reRollButtons.values().forEach(button -> button.visible = false);

		VariablesClient.reRollButtons = new LinkedHashMap<>();
		VariablesClient.addedRerollButtons = false;
	}

	public static int getExtraEffectHeightOffset() {
		if (VariablesClient.lowerForBedrockifySaving) {
			return 50;
		}

		if (ConfigHandler.lowerQuestListWhenPlayerHasEffects) {
			Collection<MobEffectInstance> activeeffects = ConstantsClient.mc.player.getActiveEffects();
			if (activeeffects.size() > 0) {
				boolean haspositive = false;
				boolean hasnegative = false;
				for (MobEffectInstance effect : activeeffects) {
					if (effect.isVisible()) {
						if (effect.getEffect().value().getCategory().equals(MobEffectCategory.BENEFICIAL)) {
							haspositive = true;
						}
						else {
							hasnegative = true;
						}

						if (haspositive && hasnegative) {
							break;
						}
					}
				}

				if (hasnegative) {
					return 50;
				}
				else if (haspositive) {
					return 25;
				}
			}
		}

		return 0;
	}

	public static void resetQuestDataClient() {
		VariablesClient.playerDataObject = null;
		VariablesClient.questTitles = new ArrayList<>();
		VariablesClient.questDescriptions = new ArrayList<>();
		VariablesClient.questProgress = new ArrayList<>();
	}
}
