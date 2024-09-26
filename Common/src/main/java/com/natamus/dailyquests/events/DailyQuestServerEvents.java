package com.natamus.dailyquests.events;

import com.natamus.collective.functions.WorldFunctions;
import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.Variables;
import com.natamus.dailyquests.quests.functions.GenerateQuests;
import com.natamus.dailyquests.quests.functions.ReRollFunctions;
import com.natamus.dailyquests.util.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DailyQuestServerEvents {
	public static void onWorldLoad(Level level) {
		if (!Variables.generatedIdentifierLists) {
			Util.generateIdentifierLists(level);
		}
	}

	public static void onServerTick(MinecraftServer minecraftServer) {
		ServerLevel serverLevel = minecraftServer.getLevel(Level.OVERWORLD);
		if (serverLevel == null) {
			return;
		}

		if (WorldFunctions.getWorldTime(serverLevel) - 1 == ConfigHandler.newQuestGenerateTimeInTicks) {
			ReRollFunctions.refreshAllPlayerReRolls(serverLevel);
			GenerateQuests.replaceFinishedPlayerQuests(serverLevel);
		}
	}

	public static void onEntityJoinLevel(Level level, Entity entity) {
		if (level.isClientSide) {
			return;
		}

		if (!(entity instanceof Player)) {
			return;
		}

		Util.loadQuestDataPlayer((ServerPlayer)entity);
	}
}
