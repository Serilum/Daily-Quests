package com.natamus.dailyquests.neoforge.events;

import com.natamus.collective.functions.WorldFunctions;
import com.natamus.dailyquests.cmds.CommandDailyQuests;
import com.natamus.dailyquests.events.DailyQuestServerEvents;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber
public class NeoForgeDailyQuestServerEvents {
	@SubscribeEvent
	public static void onWorldLoad(LevelEvent.Load e) {
		Level level = WorldFunctions.getWorldIfInstanceOfAndNotRemote(e.getLevel());
		if (level == null) {
			return;
		}

		DailyQuestServerEvents.onWorldLoad(level);
	}

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent.Post e) {
		DailyQuestServerEvents.onServerTick(e.getServer());
	}

	@SubscribeEvent
	public static void onScaffoldingItem(EntityJoinLevelEvent e) {
		DailyQuestServerEvents.onEntityJoinLevel(e.getLevel(), e.getEntity());
	}

	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent e) {
		CommandDailyQuests.register(e.getDispatcher());
	}
}
