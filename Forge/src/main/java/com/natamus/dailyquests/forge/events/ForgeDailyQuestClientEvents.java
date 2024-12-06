package com.natamus.dailyquests.forge.events;

import com.natamus.dailyquests.data.ConstantsClient;
import com.natamus.dailyquests.events.DailyQuestsClientEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(Dist.CLIENT)
public class ForgeDailyQuestClientEvents {
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent e) {
		if (!e.phase.equals(TickEvent.Phase.START)) {
			return;
		}

		DailyQuestsClientEvents.onClientTick();
	}

	@SubscribeEvent
	public void onKey(InputEvent.Key e) {
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