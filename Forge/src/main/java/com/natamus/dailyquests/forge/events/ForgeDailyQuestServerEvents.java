package com.natamus.dailyquests.forge.events;

import com.natamus.collective.functions.WorldFunctions;
import com.natamus.dailyquests.cmds.CommandDailyQuests;
import com.natamus.dailyquests.events.DailyQuestServerEvents;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

import java.lang.invoke.MethodHandles;

public class ForgeDailyQuestServerEvents {
	public static void registerEventsInBus() {
		BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgeDailyQuestServerEvents.class);
	}

	@SubscribeEvent
	public static void onWorldLoad(LevelEvent.Load e) {
		Level level = WorldFunctions.getWorldIfInstanceOfAndNotRemote(e.getLevel());
		if (level == null) {
			return;
		}

		DailyQuestServerEvents.onWorldLoad(level);
	}

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent.Post e) {
        DailyQuestServerEvents.onServerTick(e.server());
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
