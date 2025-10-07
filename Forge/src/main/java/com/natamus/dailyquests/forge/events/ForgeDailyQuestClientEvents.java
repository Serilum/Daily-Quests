package com.natamus.dailyquests.forge.events;

import com.natamus.dailyquests.data.ConstantsClient;
import com.natamus.dailyquests.events.DailyQuestsClientEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

import java.lang.invoke.MethodHandles;

public class ForgeDailyQuestClientEvents {
	public static void registerEventsInBus() {
		BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgeDailyQuestClientEvents.class);
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent.Pre e) {
		DailyQuestsClientEvents.onClientTick();
	}

	@SubscribeEvent
	public static void onKey(InputEvent.Key e) {
		if (e.getAction() != 1) {
			return;
		}

		if (ConstantsClient.toggleQuestListKey == null) {
			return;
		}

		if (e.getKey() == ConstantsClient.toggleQuestListKey.getKey().getValue()) {
			DailyQuestsClientEvents.toggleQuestListCollapse();
		}
	}
}