package com.natamus.dailyquests.neoforge.events;

import com.natamus.dailyquests.data.ConstantsClient;
import com.natamus.dailyquests.events.DailyQuestsClientEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;

@EventBusSubscriber(Dist.CLIENT)
public class NeoForgeDailyQuestClientEvents {
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Pre e) {
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