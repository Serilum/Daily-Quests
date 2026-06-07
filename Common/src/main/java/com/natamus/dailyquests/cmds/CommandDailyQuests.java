package com.natamus.dailyquests.cmds;
import com.natamus.dailyquests.util.Reference;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.natamus.collective.functions.MessageFunctions;
import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.Constants;
import com.natamus.dailyquests.data.Variables;
import com.natamus.dailyquests.quests.functions.CompleteQuests;
import com.natamus.dailyquests.quests.functions.GenerateQuests;
import com.natamus.dailyquests.quests.object.PlayerDataObject;
import com.natamus.dailyquests.quests.object.QuestObject;
import com.natamus.dailyquests.quests.types.main.AbstractQuest;
import com.natamus.dailyquests.quests.types.main.QuestWrapper;
import com.natamus.dailyquests.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

public class CommandDailyQuests {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		for (String commandPrefix : Constants.commandPrefixes) {
			dispatcher.register(Commands.literal(commandPrefix)
				.then(Commands.literal("info")
				.executes((command) -> {
					CommandSourceStack source = command.getSource();
					ServerPlayer serverPlayer = source.getPlayer();
					if (serverPlayer == null) {
						MessageFunctions.sendTranslatableMessage(source, "collective.shared.message.playeronly", ChatFormatting.RED);
						return 0;
					}

					UUID playerUUID = serverPlayer.getUUID();

					int questsCompleted = 0;
					int reRollsRemaining = ConfigHandler.maximumQuestReRollsPerDay;
					if (Variables.playerDataMap.containsKey(playerUUID)) {
						PlayerDataObject playerDataObject = Variables.playerDataMap.get(playerUUID);

						questsCompleted = playerDataObject.getQuestsCompleted();
						reRollsRemaining = playerDataObject.getReRollsLeft();
					}

					MessageFunctions.sendMessage(serverPlayer, Component.translatable("collective.dailyquests.message.stats", Reference.NAME).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GRAY), true);
					MessageFunctions.sendTranslatableMessage(serverPlayer, " ", "collective.dailyquests.message.questscompleted", ChatFormatting.GRAY, questsCompleted);
					MessageFunctions.sendTranslatableMessage(serverPlayer, " ", "collective.dailyquests.message.rerollsremaining", ChatFormatting.GRAY, reRollsRemaining);

					MessageFunctions.sendTranslatableMessage(serverPlayer, "collective.dailyquests.message.seequestsuse", true, ChatFormatting.DARK_GREEN);
					return 1;
				}))

				.then(Commands.literal("quests")
				.executes((command) -> {
					CommandSourceStack source = command.getSource();
					ServerPlayer serverPlayer = source.getPlayer();
					if (serverPlayer == null) {
						MessageFunctions.sendTranslatableMessage(source, "collective.shared.message.playeronly", ChatFormatting.RED);
						return 0;
					}

					UUID playerUUID = serverPlayer.getUUID();
					if (!Variables.playerQuestDataMap.containsKey(playerUUID)) {
						MessageFunctions.sendTranslatableMessage(serverPlayer, "collective.dailyquests.message.unablefindquestdata", ChatFormatting.RED);
						return 0;
					}

					ServerLevel serverLevel = serverPlayer.serverLevel();

					MessageFunctions.sendMessage(serverPlayer, Component.translatable("collective.dailyquests.message.quests", serverPlayer.getName().getString()).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GRAY), true);

					LinkedHashMap<AbstractQuest, QuestObject> quests = Variables.playerQuestDataMap.get(playerUUID);
					for (QuestObject quest : quests.values()) {
						if (quest == null) {
							continue;
						}

						String questDescription = quest.getQuestDescription(serverLevel);
						if (!questDescription.isBlank()) {
							questDescription = " " + questDescription + ",";
						}

						String questProgress = quest.getCurrentProgress() + " / " + quest.getGoalProgress();

						MessageFunctions.sendMessage(serverPlayer, " > " + quest.getQuestTitle(serverLevel) + ":" + questDescription + " " + questProgress , ChatFormatting.GRAY);
					}

					MessageFunctions.sendTranslatableMessage(serverPlayer, "collective.dailyquests.message.seequeststats", true, ChatFormatting.DARK_GREEN);
					return 1;
				}))

				.then(Commands.literal("debug")
				.then(Commands.argument("target", EntityArgument.player())
				.then(Commands.literal("generate")
				.then(Commands.argument("count", IntegerArgumentType.integer(1, 5))
				.executes((command) -> {
					CommandSourceStack source = command.getSource();
					if (!source.hasPermission(2)) {
						MessageFunctions.sendTranslatableMessage(source, "collective.shared.message.nopermission", ChatFormatting.RED);
						return 0;
					}

					ServerPlayer targetPlayer = EntityArgument.getPlayer(command, "target");
					int count = IntegerArgumentType.getInteger(command, "count");

					GenerateQuests.replaceAllPlayerQuests(targetPlayer.serverLevel(), targetPlayer, count);

					MessageFunctions.sendTranslatableMessage(source, "collective.dailyquests.message.activeplayerquests", true, ChatFormatting.GRAY);
					return 1;
				})))))

				.then(Commands.literal("debug")
				.then(Commands.argument("target", EntityArgument.player())
				.then(Commands.literal("complete")
				.then(Commands.argument("number", IntegerArgumentType.integer(1, 5))
				.executes((command) -> {
					CommandSourceStack source = command.getSource();
					if (!source.hasPermission(2)) {
						MessageFunctions.sendTranslatableMessage(source, "collective.shared.message.nopermission", ChatFormatting.RED);
						return 0;
					}

					ServerPlayer targetPlayer = EntityArgument.getPlayer(command, "target");
					UUID playerUUID = targetPlayer.getUUID();

					if (!Variables.playerQuestDataMap.containsKey(playerUUID)) {
						MessageFunctions.sendTranslatableMessage(source, "collective.dailyquests.message.playeractivequests", ChatFormatting.RED);
						return 0;
					}

					int number = IntegerArgumentType.getInteger(command, "number");

					LinkedHashMap<AbstractQuest, QuestObject> quests = Variables.playerQuestDataMap.get(playerUUID);
					if (number-1 >= quests.size()) {
						MessageFunctions.sendTranslatableMessage(source, "collective.dailyquests.message.questnumberexist", ChatFormatting.RED);
						return 0;
					}

					Level level = targetPlayer.level();

					QuestObject questObject = new ArrayList<>(quests.entrySet()).get(number-1).getValue();
					QuestWrapper<?> wrappedQuestType = QuestWrapper.getWrappedQuestType(questObject.getType());

					CompleteQuests.updateQuestProgression(wrappedQuestType, level, targetPlayer, Constants.defaultResourceLocation, 1000000, true);

					MessageFunctions.sendTranslatableMessage(source, "collective.dailyquests.message.forcecompletedquest", true, ChatFormatting.GRAY, questObject.getQuestTitle(level), targetPlayer.getName().getString());
					return 1;
				})))))

				.then(Commands.literal("debug")
				.then(Commands.argument("target", EntityArgument.player())
				.then(Commands.literal("resetrerolls")
				.executes((command) -> {
					CommandSourceStack source = command.getSource();
					if (!source.hasPermission(2)) {
						MessageFunctions.sendTranslatableMessage(source, "collective.shared.message.nopermission", ChatFormatting.RED);
						return 0;
					}

					ServerPlayer targetPlayer = EntityArgument.getPlayer(command, "target");
					UUID playerUUID = targetPlayer.getUUID();

					Variables.playerDataMap.get(playerUUID).resetReRolls();

					Util.saveQuestDataPlayer(targetPlayer);
					Util.sendQuestDataToClient(targetPlayer);

					MessageFunctions.sendTranslatableMessage(source, "collective.dailyquests.message.rerollsreset", true, ChatFormatting.GRAY, targetPlayer.getName().getString());
					return 1;
				}))))

				.then(Commands.literal("debug")
				.then(Commands.argument("target", EntityArgument.player())
				.then(Commands.literal("questscompleted")
				.then(Commands.argument("amount", IntegerArgumentType.integer(0, 1000))
				.executes((command) -> {
					CommandSourceStack source = command.getSource();
					if (!source.hasPermission(2)) {
						MessageFunctions.sendTranslatableMessage(source, "collective.shared.message.nopermission", ChatFormatting.RED);
						return 0;
					}

					ServerPlayer targetPlayer = EntityArgument.getPlayer(command, "target");
					int amount = IntegerArgumentType.getInteger(command, "amount");

					UUID playerUUID = targetPlayer.getUUID();
					if (!Variables.playerDataMap.containsKey(playerUUID)) {
						Variables.playerDataMap.put(playerUUID, new PlayerDataObject(playerUUID, ConfigHandler.maximumQuestReRollsPerDay, amount, false));
					}

					Variables.playerDataMap.get(playerUUID).setQuestsCompleted(amount);

					Util.saveQuestDataPlayer(targetPlayer);
					Util.sendQuestDataToClient(targetPlayer);

					MessageFunctions.sendTranslatableMessage(source, "collective.dailyquests.message.completedquests", true, ChatFormatting.GRAY, targetPlayer.getName().getString(), amount);
					return 1;
				})))))

				.then(Commands.literal("debug")
				.then(Commands.argument("target", EntityArgument.player())
				.then(Commands.literal("setquest")
				.then(Commands.argument("number", IntegerArgumentType.integer(1, 5))
				.then(Commands.argument("type", StringArgumentType.string())
				.then(Commands.argument("identifier", StringArgumentType.string())
				.executes((command) -> {
					CommandSourceStack source = command.getSource();
					if (!source.hasPermission(2)) {
						MessageFunctions.sendTranslatableMessage(source, "collective.shared.message.nopermission", ChatFormatting.RED);
						return 0;
					}

					ServerPlayer targetPlayer = EntityArgument.getPlayer(command, "target");
					ServerLevel serverLevel = targetPlayer.serverLevel();

					int questNumber = IntegerArgumentType.getInteger(command, "number");
					String questTypeString = StringArgumentType.getString(command, "type");
					String questIdentifierString = StringArgumentType.getString(command, "identifier").replace("-", ":");

					AbstractQuest questType = QuestWrapper.getQuestTypeFromName(questTypeString);
					if (questType == null) {
						MessageFunctions.sendTranslatableMessage(source, "collective.dailyquests.message.unablefindquesttype", ChatFormatting.RED, questTypeString);
						return 0;
					}

					ResourceLocation identifier = ResourceLocation.parse(questIdentifierString);
					if (identifier == null) {
						MessageFunctions.sendTranslatableMessage(source, "collective.dailyquests.message.unablefindquestidentifier", ChatFormatting.RED, questIdentifierString);
						return 0;
					}

					QuestObject quest = new QuestObject(questType, identifier, 0, questType.getRandomQuestProgressGoal(serverLevel, identifier));

					UUID playerUUID = targetPlayer.getUUID();
					if (!Variables.playerQuestDataMap.containsKey(playerUUID)) {
						GenerateQuests.replaceAllPlayerQuests(targetPlayer.serverLevel(), targetPlayer, ConfigHandler.defaultTotalQuestCount);
					}

					LinkedHashMap<AbstractQuest, QuestObject> quests = new LinkedHashMap<>();
					for (QuestObject questObject : Variables.playerQuestDataMap.get(playerUUID).values()) {
						if (quests.size()+1 == questNumber) {
							quests.put(quest.getType(), quest);
							continue;
						}

						quests.put(questObject.getType(), questObject);
					}


					Variables.playerQuestDataMap.put(playerUUID, quests);

					Util.saveQuestDataPlayer(targetPlayer);
					Util.sendQuestDataToClient(targetPlayer);

					MessageFunctions.sendTranslatableMessage(source, "collective.dailyquests.message.addedquesttype", true, ChatFormatting.GRAY, quest.getQuestTitle(serverLevel), targetPlayer.getName().getString());
					return 1;
				})))))))
			);
		}
	}
}
