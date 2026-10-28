package com.serilum.dailyquests.util;

import com.mojang.datafixers.util.Pair;
import com.natamus.collective.functions.ScreenFunctions;
import com.natamus.collective.implementations.networking.api.Dispatcher;
import com.serilum.dailyquests.config.ConfigHandler;
import com.serilum.dailyquests.data.ConstantsClient;
import com.serilum.dailyquests.data.VariablesClient;
import com.serilum.dailyquests.networking.packets.ToServerAttemptReRollQuest;
import com.serilum.dailyquests.quests.object.QuestObject;
import com.serilum.dailyquests.quests.types.main.AbstractQuest;
import com.serilum.dailyquests.quests.types.main.QuestWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class UtilClient {
	public static void showDailyQuestsIntroduction(int questCount) {
		VariablesClient.questTitles = getIntroductionTitles();
		VariablesClient.questDescriptions = getIntroductionDescriptions(questCount);
		VariablesClient.questProgress = new ArrayList<>();

		VariablesClient.waitingForNewQuest = false;
	}

	public static List<String> getIntroductionTitles() {
		return Arrays.asList(
			Reference.NAME,
			Component.translatableWithFallback("collective.dailyquests.introduction.title.collapselist", "Collapse List").getString(),
			Component.translatableWithFallback("collective.dailyquests.introduction.title.gettingstarted", "Getting Started").getString()
		);
	}

	public static List<String> getIntroductionDescriptions(int questCount) {
		String collapseKey = ".";
		if (ConstantsClient.toggleQuestListKey != null) {
			collapseKey = ConstantsClient.toggleQuestListKey.getTranslatedKeyMessage().getString();
		}

		return Arrays.asList(
			Component.translatableWithFallback("collective.dailyquests.introduction.description.introduction", "Introduction.").getString(),
			Component.translatableWithFallback("collective.dailyquests.introduction.description.collapselist", "Press the %s key to collapse this list.", collapseKey).getString(),
			Component.translatableWithFallback("collective.dailyquests.introduction.description.gettingstarted", "Receive your first %s quests next in-game morning!", questCount).getString()
		);
	}

	public static List<String> getLocalizedQuestTitles(List<String> serverTitles, List<String> questTypes, List<String> questIdentifiers) {
		Level level = Minecraft.getInstance().level;
		if (level == null) {
			return serverTitles;
		}

		List<String> questTitles = new ArrayList<>();
		for (int n = 0; n < serverTitles.size(); n++) {
			String questTitle = serverTitles.get(n);

			QuestObject quest = getClientQuestObject(questTypes, questIdentifiers, n);
			if (quest != null) {
				try {
					String translationKey = quest.getQuestTitleTranslationKey(level);
					if (!translationKey.isEmpty()) {
						questTitle = Component.translatableWithFallback(translationKey, questTitle).getString();
					}
				}
				catch (Exception ignored) { }
			}

			questTitles.add(questTitle);
		}

		return questTitles;
	}

	public static List<String> getLocalizedQuestDescriptions(List<String> serverDescriptions, List<String> questTypes, List<String> questIdentifiers) {
		Level level = Minecraft.getInstance().level;
		if (level == null) {
			return serverDescriptions;
		}

		List<String> questDescriptions = new ArrayList<>();
		for (int n = 0; n < serverDescriptions.size(); n++) {
			String questDescription = serverDescriptions.get(n);

			QuestObject quest = getClientQuestObject(questTypes, questIdentifiers, n);
			if (quest != null) {
				try {
					questDescription = quest.getQuestDescription(level);
				}
				catch (Exception ignored) { }
			}

			questDescriptions.add(questDescription);
		}

		return questDescriptions;
	}

	private static @Nullable QuestObject getClientQuestObject(List<String> questTypes, List<String> questIdentifiers, int questIndex) {
		if (questIndex >= questTypes.size() || questIndex >= questIdentifiers.size()) {
			return null;
		}

		AbstractQuest questType = QuestWrapper.getQuestTypeFromName(questTypes.get(questIndex));
		Identifier identifier = Identifier.tryParse(questIdentifiers.get(questIndex));
		if (questType == null || identifier == null) {
			return null;
		}

		return new QuestObject(questType, identifier, 0, 0);
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
			LocalPlayer localPlayer = Minecraft.getInstance().player;
			if (localPlayer == null) {
				return 0;
			}

			Collection<MobEffectInstance> activeeffects = localPlayer.getActiveEffects();
			if (!activeeffects.isEmpty()) {
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
