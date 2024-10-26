package com.natamus.dailyquests.neoforge.events;

import com.natamus.dailyquests.data.ConstantsClient;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class NeoForgeDailyQuestHotkeyEvents {
	@SubscribeEvent
	public static void registerKeyBinding(RegisterKeyMappingsEvent e) {
    	ConstantsClient.toggleQuestListKey = new KeyMapping("dailyquests.key.togglequestlistcollapse", 46, "key.categories.misc");
    	e.register(ConstantsClient.toggleQuestListKey);
	}
}