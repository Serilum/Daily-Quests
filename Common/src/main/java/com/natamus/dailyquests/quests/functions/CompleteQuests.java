package com.natamus.dailyquests.quests.functions;

import com.natamus.collective.functions.*;
import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.Constants;
import com.natamus.dailyquests.data.Variables;
import com.natamus.dailyquests.quests.object.PlayerDataObject;
import com.natamus.dailyquests.quests.object.QuestObject;
import com.natamus.dailyquests.quests.types.main.AbstractQuest;
import com.natamus.dailyquests.quests.types.main.QuestWrapper;
import com.natamus.dailyquests.util.Reference;
import com.natamus.dailyquests.util.Util;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CompleteQuests {
	public static boolean hasQuestType(QuestWrapper<?> wrappedQuestType, Player player) {
		UUID playerUUID = player.getUUID();
		if (!Variables.playerQuestDataMap.containsKey(playerUUID)) {
			return false;
		}

		AbstractQuest abstractQuest = wrappedQuestType.getQuestInstance();
		if (!Variables.playerQuestDataMap.get(playerUUID).containsKey(abstractQuest)) {
			return false;
		}

		QuestObject quest = Variables.playerQuestDataMap.get(playerUUID).get(abstractQuest);
		if (quest == null) {
			return false;
		}

		return !quest.isCompleted();
	}
	public static void updateQuestProgression(QuestWrapper<?> wrappedQuestType, Level level, ServerPlayer serverPlayer, ResourceLocation identifier, int count) {
		updateQuestProgression(wrappedQuestType, level, serverPlayer, identifier, count, false);
	}
	public static void updateQuestProgression(QuestWrapper<?> wrappedQuestType, Level level, ServerPlayer serverPlayer, ResourceLocation identifier, int count, boolean forceEqual) {
		UUID playerUUID = serverPlayer.getUUID();

		AbstractQuest abstractQuest = wrappedQuestType.getQuestInstance();
		QuestObject quest = Variables.playerQuestDataMap.get(playerUUID).get(abstractQuest);
		if (quest.isCompleted()) {
			return;
		}

		if (quest.getIdentifier().equals(identifier) || forceEqual) {
			if (quest.incrementCompletionCheck(count)) {
				CompleteQuests.completeQuest((ServerLevel)level, serverPlayer, abstractQuest, quest);
			}

			Variables.playerQuestDataMap.get(playerUUID).put(abstractQuest, quest);

			Util.saveQuestDataPlayer(serverPlayer);
			Util.sendQuestDataToClient(serverPlayer);
		}
	}

	public static void completeQuest(ServerLevel serverLevel, ServerPlayer serverPlayer, AbstractQuest abstractQuest, QuestObject quest) {
		UUID playerUUID = serverPlayer.getUUID();

		quest.getType().onQuestFinished(serverPlayer);

		int questsCompleted;
		if (!Variables.playerDataMap.containsKey(playerUUID)) {
			Variables.playerDataMap.put(playerUUID, new PlayerDataObject(playerUUID, ConfigHandler.maximumQuestReRollsPerDay, 1, false));

			questsCompleted = 1;
		}
		else {
			Variables.playerDataMap.get(playerUUID).incrementQuestsCompleted();

			questsCompleted = Variables.playerDataMap.get(playerUUID).getQuestsCompleted();
		}

		TaskFunctions.enqueueCollectiveTask(serverLevel.getServer(), () -> {
			int experience = giveExperienceToPlayer(serverLevel, serverPlayer, abstractQuest, quest);

			List<ItemStack> lootTableItemStacks = giveLootTableItems(serverLevel, serverPlayer, questsCompleted);

			spawnFireworks(serverLevel, serverPlayer, questsCompleted);

			broadcastCompletionMessage(serverLevel, serverPlayer, quest, questsCompleted);

			sendQuestSummary(serverLevel, serverPlayer, abstractQuest, quest, questsCompleted, experience, lootTableItemStacks);
		}, 10);
	}

	private static int giveExperienceToPlayer(ServerLevel serverLevel, ServerPlayer serverPlayer, AbstractQuest abstractQuest, QuestObject quest) {
		if (!ConfigHandler.giveExperienceOnQuestCompletion) {
			return 0;
		}

		int experience = (int)(abstractQuest.getFinishExperience(quest) * ConfigHandler.questCompletionExperienceModifier);

		ExperienceFunctions.addPlayerXP(serverPlayer, experience);

		return experience;
	}

	private static List<ItemStack> giveLootTableItems(ServerLevel serverLevel, ServerPlayer serverPlayer, int questsCompleted) {
		List<ItemStack> lootTableItemStacks = new ArrayList<>();

		if (!ConfigHandler.useQuestCompletionItemLootTables) {
			return lootTableItemStacks;
		}

		LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(getLootTableResourceKey(questsCompleted));
		if (lootTable == null) {
			return lootTableItemStacks;
		}

		LootParams lootParams = (new LootParams.Builder(serverLevel))
				.withParameter(LootContextParams.THIS_ENTITY, serverPlayer)
				.withParameter(LootContextParams.ORIGIN, serverPlayer.position())
				.create(LootContextParamSets.ADVANCEMENT_REWARD);

		List<ItemStack> randomItems = Util.mergeItemStacks(lootTable.getRandomItems(lootParams));

		for (ItemStack itemStack : randomItems) {
			lootTableItemStacks.add(itemStack.copy());

			ItemFunctions.giveOrDropItemStack(serverPlayer, itemStack);
		}

		return lootTableItemStacks;
	}

	private static void spawnFireworks(Level level, Player player, int questsCompleted) {
		if (!ConfigHandler.spawnFireworksOnQuestCompletion) {
			return;
		}

		Vec3 playerVec = player.position();

		if (!BlockPosFunctions.isOnSurface(level, playerVec)) {
			return;
		}

		List<Vec3> fireworkPositions = new ArrayList<Vec3>(Arrays.asList(playerVec));
		if (isSpecialQuestCompletedCount(questsCompleted)) {
			for (int i = 0; i < 6; i++) {
				double offsetX = -2 + Constants.random.nextDouble() * 4;
				double offsetZ = -2 + Constants.random.nextDouble() * 4;

				Vec3 offsetVec = playerVec.add(offsetX, 0, offsetZ);
				if (BlockPosFunctions.isOnSurface(level, offsetVec)) {
					fireworkPositions.add(offsetVec);
				}
			}
		}

		for (Vec3 fireworkPosition : fireworkPositions) {
			ItemStack fireworkItemStack = new ItemStack(Items.FIREWORK_ROCKET, 1);

			List<FireworkExplosion> explosionsList = new ArrayList<FireworkExplosion>();

			boolean hasTrail = true;
			boolean hasTwinkle = true;
			IntList colours = IntList.of(16711680, 16753920, 16776960, 65280, 255, 4915330, 9055202);
			IntList fadeColours = IntList.of(255, 16777215, 16777152, 8355711, 11141120, 11796480, 16711935);

			FireworkExplosion fireworkExplosion = new FireworkExplosion(FireworkExplosion.Shape.LARGE_BALL, colours, fadeColours, hasTrail, hasTwinkle);

			explosionsList.add(fireworkExplosion);

			Fireworks fireworks = new Fireworks(2, explosionsList);

			fireworkItemStack.set(DataComponents.FIREWORKS, fireworks);

			level.addFreshEntity(new FireworkRocketEntity(level, fireworkPosition.x, fireworkPosition.y, fireworkPosition.z, fireworkItemStack));
		}
	}

	private static void broadcastCompletionMessage(ServerLevel serverLevel, ServerPlayer serverPlayer, QuestObject quest, int questsCompleted) {
		if (!ConfigHandler.broadcastMessageOnCompletion) {
			return;
		}

		ChatFormatting broadcastColour = ChatFormatting.getById(ConfigHandler.broadcastMessageColourIndex);
		if (broadcastColour == null) {
			Constants.logger.warn("[" + Reference.NAME + "] Unable to find text formatting colour for message one with index '" + ConfigHandler.broadcastMessageColourIndex + "'.");
			return;
		}

		MutableComponent broadcastComponent = Component.literal(serverPlayer.getName().getString() + " completed ").withStyle(ChatFormatting.DARK_GREEN).append(quest.getBroadcastContent(serverLevel).withStyle(broadcastColour));
		if (isSpecialQuestCompletedCount(questsCompleted)) {
			broadcastComponent.append(Component.literal(", which is their " + questsCompleted + "th completed quest!").withStyle(ChatFormatting.DARK_GREEN));
		}

		MessageFunctions.broadcastMessage(serverLevel, broadcastComponent);
	}

	private static void sendQuestSummary(ServerLevel serverLevel, ServerPlayer serverPlayer, AbstractQuest abstractQuest, QuestObject quest, int questsCompleted, int experience, List<ItemStack> lootTableItemStacks) {
		if (!ConfigHandler.sendSummaryToPlayerOnQuestCompletion) {
			return;
		}

		UUID playerUUID = serverPlayer.getUUID();

		List<String> summaryContent = new ArrayList<>();

		summaryContent.add(" > Quests completed: " + questsCompleted);

		if (experience > 0) {
			summaryContent.add(" > Experience received: " + experience);
		}

		if (!lootTableItemStacks.isEmpty()) {
			summaryContent.add(" > Items received: " + Util.formatItemStacks(lootTableItemStacks));
		}

		MessageFunctions.sendMessage(serverPlayer, Component.literal("Quest Summary").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GRAY), true);
		for (String content : summaryContent) {
			MessageFunctions.sendMessage(serverPlayer, content, ChatFormatting.GRAY);
		}
	}

	private static boolean isSpecialQuestCompletedCount(int questsCompleted){
		return questsCompleted % 10 == 0;
	}

	private static ResourceKey<LootTable> getLootTableResourceKey(int questsCompleted) {
		if (questsCompleted % 100 == 0) {
			return Constants.LOOT_TABLE_100NTH_QUEST;
		}

		if (questsCompleted % 50 == 0) {
			return Constants.LOOT_TABLE_50NTH_QUEST;
		}

		if (questsCompleted % 10 == 0) {
			return Constants.LOOT_TABLE_10NTH_QUEST;
		}

		return Constants.LOOT_TABLE_1NTH_QUEST;
	}
}
