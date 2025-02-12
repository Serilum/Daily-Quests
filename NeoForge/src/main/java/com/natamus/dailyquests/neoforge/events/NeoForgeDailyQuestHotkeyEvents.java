package com.natamus.dailyquests.neoforge.events;

import com.natamus.dailyquests.data.ConstantsClient;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class NeoForgeDailyQuestHotkeyEvents {
	@SubscribeEvent
	public static void registerKeyBinding(RegisterKeyMappingsEvent e) {
    	ConstantsClient.toggleQuestListKey = new KeyMapping("dailyquests.key.togglequestlistcollapse", 46, "key.categories.misc");
    	e.register(ConstantsClient.toggleQuestListKey);
	}
}