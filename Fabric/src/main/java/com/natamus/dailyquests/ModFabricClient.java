package com.natamus.dailyquests;

import com.mojang.blaze3d.platform.InputConstants;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.dailyquests.data.ConstantsClient;
import com.natamus.dailyquests.events.DailyQuestsClientEvents;
import com.natamus.dailyquests.util.Reference;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class ModFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() { 
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		ModCommon.registerPackets();

		ConstantsClient.toggleQuestListKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("dailyquests.key.togglequestlistcollapse", InputConstants.Type.KEYSYM, 46, "key.categories.misc"));

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
