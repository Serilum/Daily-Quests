package com.natamus.dailyquests.cmds;

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
						MessageFunctions.sendMessage(source, "Only in-game players can use this command.", ChatFormatting.RED);
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

					MessageFunctions.sendMessage(serverPlayer, Component.literal("Daily Quests Stats").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GRAY), true);
					MessageFunctions.sendMessage(serverPlayer, " > Quests completed: " + questsCompleted, ChatFormatting.GRAY);
					MessageFunctions.sendMessage(serverPlayer, " > Re-rolls remaining: " + reRollsRemaining, ChatFormatting.GRAY);
					return 1;
				}))

				.then(Commands.literal("debug")
				.then(Commands.argument("target", EntityArgument.player())
				.then(Commands.literal("generate")
				.then(Commands.argument("count", IntegerArgumentType.integer(1, 5))
				.executes((command) -> {
					CommandSourceStack source = command.getSource();
					if (!source.hasPermission(2)) {
						MessageFunctions.sendMessage(source, "You do not have the permissions to use that command.", ChatFormatting.RED);
						return 0;
					}

					ServerPlayer targetPlayer = EntityArgument.getPlayer(command, "target");
					int count = IntegerArgumentType.getInteger(command, "count");

					GenerateQuests.replaceAllPlayerQuests(targetPlayer.serverLevel(), targetPlayer, count);

					MessageFunctions.sendMessage(source, "All active player quests have been replaced.", ChatFormatting.GRAY, true);
					return 1;
				})))))

				.then(Commands.literal("debug")
				.then(Commands.argument("target", EntityArgument.player())
				.then(Commands.literal("complete")
				.then(Commands.argument("number", IntegerArgumentType.integer(1, 5))
				.executes((command) -> {
					CommandSourceStack source = command.getSource();
					if (!source.hasPermission(2)) {
						MessageFunctions.sendMessage(source, "You do not have the permissions to use that command.", ChatFormatting.RED);
						return 0;
					}

					ServerPlayer targetPlayer = EntityArgument.getPlayer(command, "target");
					UUID playerUUID = targetPlayer.getUUID();

					if (!Variables.playerQuestDataMap.containsKey(playerUUID)) {
						MessageFunctions.sendMessage(source, "That player does not have any active quests.", ChatFormatting.RED);
						return 0;
					}

					int number = IntegerArgumentType.getInteger(command, "number");

					LinkedHashMap<AbstractQuest, QuestObject> quests = Variables.playerQuestDataMap.get(playerUUID);
					if (number-1 >= quests.size()) {
						MessageFunctions.sendMessage(source, "That quest number does not exist.", ChatFormatting.RED);
						return 0;
					}

					Level level = targetPlayer.level();

					QuestObject questObject = new ArrayList<>(quests.entrySet()).get(number-1).getValue();
					QuestWrapper<?> wrappedQuestType = QuestWrapper.getWrappedQuestType(questObject.getType());

					CompleteQuests.updateQuestProgression(wrappedQuestType, level, targetPlayer, Constants.defaultResourceLocation, 1000000, true);

					MessageFunctions.sendMessage(source, "Force completed the quest " + questObject.getQuestTitle(level) + " for " + targetPlayer.getName().getString() + ".", ChatFormatting.GRAY, true);
					return 1;
				})))))

				.then(Commands.literal("debug")
				.then(Commands.argument("target", EntityArgument.player())
				.then(Commands.literal("resetrerolls")
				.executes((command) -> {
					CommandSourceStack source = command.getSource();
					if (!source.hasPermission(2)) {
						MessageFunctions.sendMessage(source, "You do not have the permissions to use that command.", ChatFormatting.RED);
						return 0;
					}

					ServerPlayer targetPlayer = EntityArgument.getPlayer(command, "target");
					UUID playerUUID = targetPlayer.getUUID();

					Variables.playerDataMap.get(playerUUID).resetReRolls();

					Util.saveQuestDataPlayer(targetPlayer);
					Util.sendQuestDataToClient(targetPlayer);

					MessageFunctions.sendMessage(source, targetPlayer.getName().getString() + "'s re-rolls have been reset.", ChatFormatting.GRAY, true);
					return 1;
				}))))

				.then(Commands.literal("debug")
				.then(Commands.argument("target", EntityArgument.player())
				.then(Commands.literal("questscompleted")
				.then(Commands.argument("amount", IntegerArgumentType.integer(0, 1000))
				.executes((command) -> {
					CommandSourceStack source = command.getSource();
					if (!source.hasPermission(2)) {
						MessageFunctions.sendMessage(source, "You do not have the permissions to use that command.", ChatFormatting.RED);
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

					MessageFunctions.sendMessage(source, targetPlayer.getName().getString() + " now has " + amount + " completed quests!", ChatFormatting.GRAY, true);
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
						MessageFunctions.sendMessage(source, "You do not have the permissions to use that command.", ChatFormatting.RED);
						return 0;
					}

					ServerPlayer targetPlayer = EntityArgument.getPlayer(command, "target");
					ServerLevel serverLevel = targetPlayer.serverLevel();

					int questNumber = IntegerArgumentType.getInteger(command, "number");
					String questTypeString = StringArgumentType.getString(command, "type");
					String questIdentifierString = StringArgumentType.getString(command, "identifier").replace("-", ":");

					AbstractQuest questType = QuestWrapper.getQuestTypeFromName(questTypeString);
					if (questType == null) {
						MessageFunctions.sendMessage(source, "Unable to find quest type from string: " + questTypeString, ChatFormatting.RED);
						return 0;
					}

					ResourceLocation identifier = ResourceLocation.parse(questIdentifierString);
					if (identifier == null) {
						MessageFunctions.sendMessage(source, "Unable to find quest identifier from string: " + questIdentifierString, ChatFormatting.RED);
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

					MessageFunctions.sendMessage(source, "Added the " + quest.getQuestTitle(serverLevel) + " quest type to " + targetPlayer.getName().getString() + "'s quest list.", ChatFormatting.GRAY, true);
					return 1;
				})))))))
			);
		}
	}
}
