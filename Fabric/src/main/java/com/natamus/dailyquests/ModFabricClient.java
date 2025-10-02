package com.natamus.dailyquests;

import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.dailyquests.data.ConstantsClient;
import com.natamus.dailyquests.events.DailyQuestsClientEvents;
import com.natamus.dailyquests.util.Reference;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class ModFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() { 
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		ModCommon.registerPackets();

		ModCommon.registerHotkeys();

		registerEvents();
	}
	
	private void registerEvents() {
		ClientTickEvents.START_CLIENT_TICK.register((Minecraft mc) -> {
			DailyQuestsClientEvents.onClientTick();

			while (ConstantsClient.toggleQuestListKey.isDown()) {
				DailyQuestsClientEvents.toggleQuestListCollapse();
				ConstantsClient.toggleQuestListKey.setDown(false);
			}
		});
	}
}
