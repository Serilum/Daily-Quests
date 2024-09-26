package com.natamus.dailyquests.cmds;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

public class CommandDailyQuests {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		for (String commandPrefix : Constants.commandPrefixes) {
			dispatcher.register(Commands.literal(commandPrefix).requires((iCommandSender) -> iCommandSender.hasPermission(2))
				.then(Commands.literal("debug")
				.then(Commands.argument("target", EntityArgument.player())
				.then(Commands.literal("generate")
				.then(Commands.argument("count", IntegerArgumentType.integer(1, 5))
				.executes((command) -> {
					ServerPlayer targetPlayer = EntityArgument.getPlayer(command, "target");
					int count = IntegerArgumentType.getInteger(command, "count");

					GenerateQuests.replaceAllPlayerQuests(targetPlayer.serverLevel(), targetPlayer, count);

					MessageFunctions.sendMessage(command.getSource(), "All active player quests have been replaced.", ChatFormatting.GRAY, true);
					return 1;
				})))))

				.then(Commands.literal("debug")
				.then(Commands.argument("target", EntityArgument.player())
				.then(Commands.literal("complete")
				.then(Commands.argument("number", IntegerArgumentType.integer(1, 5))
				.executes((command) -> {
					CommandSourceStack source = command.getSource();
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
					ServerPlayer targetPlayer = EntityArgument.getPlayer(command, "target");
					UUID playerUUID = targetPlayer.getUUID();

					Variables.playerDataMap.get(playerUUID).resetReRolls();

					Util.saveQuestDataPlayer(targetPlayer);
					Util.sendQuestDataToClient(targetPlayer);

					MessageFunctions.sendMessage(command.getSource(), targetPlayer.getName().getString() + "'s re-rolls have been reset.", ChatFormatting.GRAY, true);
					return 1;
				}))))

				.then(Commands.literal("debug")
				.then(Commands.argument("target", EntityArgument.player())
				.then(Commands.literal("questscompleted")
				.then(Commands.argument("amount", IntegerArgumentType.integer(0, 1000))
				.executes((command) -> {
					ServerPlayer targetPlayer = EntityArgument.getPlayer(command, "target");
					int amount = IntegerArgumentType.getInteger(command, "amount");

					UUID playerUUID = targetPlayer.getUUID();
					if (!Variables.playerDataMap.containsKey(playerUUID)) {
						Variables.playerDataMap.put(playerUUID, new PlayerDataObject(playerUUID, ConfigHandler.maximumQuestReRollsPerDay, amount));
					}

					Variables.playerDataMap.get(playerUUID).setQuestsCompleted(amount);

					Util.saveQuestDataPlayer(targetPlayer);
					Util.sendQuestDataToClient(targetPlayer);

					MessageFunctions.sendMessage(command.getSource(), targetPlayer.getName().getString() + " now has " + amount + " completed quests!", ChatFormatting.GRAY, true);
					return 1;
				})))))
			);
		}
	}
}
