package com.serilum.dailyquests.events;

import com.natamus.collective.functions.WorldFunctions;
import com.serilum.dailyquests.config.ConfigHandler;
import com.serilum.dailyquests.data.Variables;
import com.serilum.dailyquests.quests.functions.GenerateQuests;
import com.serilum.dailyquests.quests.functions.ReRollFunctions;
import com.serilum.dailyquests.util.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DailyQuestServerEvents {
	public static void onWorldLoad(Level level) {
		Variables.lastWorldTime = -1;

		if (!Variables.generatedIdentifierLists) {
			Util.generateIdentifierLists(level);
		}
	}

	public static void onServerTick(MinecraftServer minecraftServer) {
		ServerLevel serverLevel = minecraftServer.getLevel(Level.OVERWORLD);
		if (serverLevel == null) {
			return;
		}

		int worldTime = WorldFunctions.getWorldTime(serverLevel);
		int previousWorldTime = Variables.lastWorldTime;
		Variables.lastWorldTime = worldTime;

		if (previousWorldTime < 0 || previousWorldTime == worldTime) {
			return;
		}

		if (passedQuestGenerateTime(previousWorldTime, worldTime)) {
			ReRollFunctions.refreshAllPlayerReRolls(serverLevel);
			GenerateQuests.replaceFinishedPlayerQuests(serverLevel);
		}
	}

	private static boolean passedQuestGenerateTime(int previousWorldTime, int worldTime) {
		int questGenerateTime = ConfigHandler.newQuestGenerateTimeInTicks + 1;
		if (worldTime > previousWorldTime) {
			return previousWorldTime < questGenerateTime && questGenerateTime <= worldTime;
		}

		return questGenerateTime > previousWorldTime || questGenerateTime <= worldTime;
	}

	public static void onEntityJoinLevel(Level level, Entity entity) {
		if (level.isClientSide()) {
			return;
		}

		if (!(entity instanceof Player)) {
			return;
		}

		ServerPlayer serverPlayer = (ServerPlayer)entity;
		if (!Util.hasQuestData(serverPlayer)) {
			GenerateQuests.setInitialQuestScreen(serverPlayer.level(), serverPlayer);
			return;
		}

		Util.loadQuestDataPlayer(serverPlayer);
	}
}
