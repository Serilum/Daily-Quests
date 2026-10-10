package com.serilum.dailyquests.quests.functions;

import com.mojang.datafixers.util.Pair;
import com.serilum.dailyquests.config.ConfigHandler;
import com.serilum.dailyquests.data.Constants;
import com.serilum.dailyquests.data.Variables;
import com.serilum.dailyquests.quests.object.PlayerDataObject;
import com.serilum.dailyquests.quests.object.QuestObject;
import com.serilum.dailyquests.quests.types.main.AbstractQuest;
import com.serilum.dailyquests.quests.types.main.QuestWrapper;
import com.serilum.dailyquests.util.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.stream.Collectors;

public class GenerateQuests {
	public static void setInitialQuestScreen(ServerLevel serverLevel, ServerPlayer serverPlayer) {
		if (!ConfigHandler.showQuestIntroductionScreenFirstLogin) {
			GenerateQuests.replaceAllPlayerQuests(serverLevel, serverPlayer, ConfigHandler.defaultTotalQuestCount);
			return;
		}

		UUID playerUUID = serverPlayer.getUUID();

		PlayerDataObject playerDataObject = new PlayerDataObject(playerUUID, true);

		Variables.playerDataMap.put(playerUUID, playerDataObject);
		Variables.playerQuestDataMap.put(playerUUID, getIntroductionQuests());

		Util.saveQuestDataPlayer(serverPlayer);
		Util.sendQuestDataToClient(serverPlayer);
	}

	public static LinkedHashMap<AbstractQuest, QuestObject> getIntroductionQuests() {
		LinkedHashMap<AbstractQuest, QuestObject> quests = new LinkedHashMap<>();
		for (AbstractQuest abstractQuest : getRandomQuestTypes(Set.of(), ConfigHandler.defaultTotalQuestCount)) {
			quests.put(abstractQuest, null);
		}
		return quests;
	}

	public static void replaceFinishedPlayerQuests(ServerLevel serverLevel) {
		for (ServerPlayer serverPlayer : serverLevel.getServer().getPlayerList().getPlayers()) {
			if (serverPlayer.tickCount < ConfigHandler.newQuestGenerateTimeInTicks + 1) {
				continue;
			}

			UUID playerUUID = serverPlayer.getUUID();
			boolean isShowingIntroduction = Variables.playerDataMap.containsKey(playerUUID) && Variables.playerDataMap.get(playerUUID).isShowingIntroduction();
			if (!Variables.playerQuestDataMap.containsKey(playerUUID) || isShowingIntroduction) {
				replaceAllPlayerQuests(serverLevel, serverPlayer, ConfigHandler.defaultTotalQuestCount);
				continue;
			}

			List<Integer> questsToReplace = new ArrayList<>();

			int questNumber = 0;
			LinkedHashMap<AbstractQuest, QuestObject> quests = Variables.playerQuestDataMap.get(playerUUID);
			for (QuestObject quest : quests.values()) {
				questNumber += 1;

				if (quest != null) {
					if (!quest.isCompleted()) {
						continue;
					}
				}

				questsToReplace.add(questNumber);
			}

			if (!questsToReplace.isEmpty()) {
				replaceSpecificPlayerQuest(serverLevel, serverPlayer, questsToReplace);
			}
		}
	}

	public static void replaceAllPlayerQuests(ServerLevel serverLevel, ServerPlayer serverPlayer, int count) {
		UUID playerUUID = serverPlayer.getUUID();

		LinkedHashMap<AbstractQuest, QuestObject> quests = getNewQuests(serverLevel, Set.of(), count);

		if (Variables.playerDataMap.containsKey(playerUUID)) {
			Variables.playerDataMap.get(playerUUID).setShowingIntroduction(false);
		}

		Variables.playerQuestDataMap.put(playerUUID, quests);

		Util.saveQuestDataPlayer(serverPlayer, quests);
		Util.sendQuestDataToClient(serverPlayer, Pair.of(Variables.playerDataMap.get(playerUUID), quests));
	}

	public static void replaceSpecificPlayerQuest(ServerLevel serverLevel, ServerPlayer serverPlayer, List<Integer> questNumbers) {
		UUID playerUUID = serverPlayer.getUUID();
		if (!Variables.playerQuestDataMap.containsKey(playerUUID)) {
			return;
		}

		LinkedHashMap<AbstractQuest, QuestObject> currentQuests = Variables.playerQuestDataMap.get(playerUUID);

		Set<AbstractQuest> keptQuestTypes = new HashSet<>();
		int questNumber = 0;
		for (AbstractQuest questType : currentQuests.keySet()) {
			questNumber += 1;
			if (!questNumbers.contains(questNumber)) {
				keptQuestTypes.add(questType);
			}
		}

		LinkedHashMap<AbstractQuest, QuestObject> newQuests = getNewQuests(serverLevel, currentQuests.keySet(), questNumbers.size());
		if (newQuests.size() < questNumbers.size()) {
			Set<AbstractQuest> questTypesToSkip = new HashSet<>(keptQuestTypes);
			questTypesToSkip.addAll(newQuests.keySet());

			newQuests.putAll(getNewQuests(serverLevel, questTypesToSkip, questNumbers.size() - newQuests.size()));
		}

		LinkedHashMap<AbstractQuest, QuestObject> quests = new LinkedHashMap<>();
		questNumber = 0;
		for (QuestObject questObject : currentQuests.values()) {
			questNumber += 1;

			if (questNumbers.contains(questNumber)) {
				Map.Entry<AbstractQuest, QuestObject> questEntry = newQuests.pollFirstEntry();
				if (questEntry != null) {
					quests.put(questEntry.getKey(), questEntry.getValue());
				}
				continue;
			}

			if (questObject == null || questObject.getType() == null) {
				continue;
			}

			quests.put(questObject.getType(), questObject);
		}

		if (Variables.playerDataMap.get(playerUUID).isShowingIntroduction()) {
			Variables.playerDataMap.get(playerUUID).setShowingIntroduction(false);
		}

		Variables.playerQuestDataMap.put(playerUUID, quests);

		Util.saveQuestDataPlayer(serverPlayer, quests);
		Util.sendQuestDataToClient(serverPlayer);
	}

	public static LinkedHashMap<AbstractQuest, QuestObject> getNewQuests(Level level, Set<AbstractQuest> questTypesToSkip, int count) {
		LinkedHashMap<AbstractQuest, QuestObject> quests = new LinkedHashMap<>();

		for (AbstractQuest questType : getRandomQuestTypes(questTypesToSkip, count)) {
			ResourceLocation identifier = questType.getRandomQuestIdentifier(level);
			if (identifier == null) {
				continue;
			}

			QuestObject questObject = new QuestObject(questType, identifier, 0, questType.getRandomQuestProgressGoal(level, identifier));

			quests.put(questType, questObject);
		}

		return quests;
	}

	private static List<AbstractQuest> getRandomQuestTypes(Set<AbstractQuest> questTypesToSkip, int count) {
		List<AbstractQuest> possibleQuestTypes = QuestWrapper.getAllQuests().stream()
			.filter(AbstractQuest::isEnabled)
			.filter(questType -> !questTypesToSkip.contains(questType))
			.collect(Collectors.toList());

		Collections.shuffle(possibleQuestTypes, Constants.random);

		List<AbstractQuest> selectedQuestTypes = possibleQuestTypes.subList(0, Math.min(count, possibleQuestTypes.size()));

		selectedQuestTypes.sort(Comparator.comparing(AbstractQuest::getName));

		return selectedQuestTypes;
	}
}
