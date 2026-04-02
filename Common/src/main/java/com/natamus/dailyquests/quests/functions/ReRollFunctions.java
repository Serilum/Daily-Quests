package com.natamus.dailyquests.quests.functions;

import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.Variables;
import com.natamus.dailyquests.util.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class ReRollFunctions {
	public static void refreshAllPlayerReRolls(ServerLevel serverLevel) {
		for (ServerPlayer serverPlayer : serverLevel.players()) {
			UUID playerUUID = serverPlayer.getUUID();
			if (!Variables.playerDataMap.containsKey(playerUUID)) {
				continue;
			}

			if (Variables.playerDataMap.get(playerUUID).getReRollsLeft() != ConfigHandler.maximumQuestReRollsPerDay) {
				Variables.playerDataMap.get(playerUUID).resetReRolls();

				Util.saveQuestDataPlayer(serverPlayer);
				Util.sendQuestDataToClient(serverPlayer);
			}
		}
	}
}
