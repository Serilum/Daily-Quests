package com.natamus.dailyquests.forge.events;

import com.natamus.collective.functions.WorldFunctions;
import com.natamus.dailyquests.cmds.CommandDailyQuests;
import com.natamus.dailyquests.events.DailyQuestServerEvents;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ForgeDailyQuestServerEvents {
	@SubscribeEvent
	public void onWorldLoad(LevelEvent.Load e) {
		Level level = WorldFunctions.getWorldIfInstanceOfAndNotRemote(e.getLevel());
		if (level == null) {
			return;
		}

		DailyQuestServerEvents.onWorldLoad(level);
	}

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent e) {
        if (!e.phase.equals(TickEvent.Phase.END)) {
            return;
        }

        DailyQuestServerEvents.onServerTick(e.getServer());
    }

	@SubscribeEvent
	public void onScaffoldingItem(EntityJoinLevelEvent e) {
		DailyQuestServerEvents.onEntityJoinLevel(e.getLevel(), e.getEntity());
	}

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent e) {
    	CommandDailyQuests.register(e.getDispatcher());
    }
}
