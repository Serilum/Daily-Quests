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
import java.util.Collection;

public class UtilClient {
	public static void addRerollButton(PauseScreen pauseScreen, int questNumber, int xPosition, int yPosition) {
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

	public static int getExtraEffectHeightOffset() {
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
