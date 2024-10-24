package com.natamus.dailyquests;

import com.natamus.collective.globalcallbacks.CollectiveGuiCallback;
import com.natamus.collective.services.Services;
import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.events.DailyQuestsClientEvents;
import com.natamus.dailyquests.networking.PacketRegistration;

public class ModCommon {

	public static void init() {
		registerPackets();

		ConfigHandler.initConfig();
		load();
	}

	private static void load() {
		if (Services.MODLOADER.isClientSide()) {
			CollectiveGuiCallback.ON_GUI_RENDER.register(((guiGraphics, deltaTracker) -> {
				DailyQuestsClientEvents.renderOverlay(guiGraphics, deltaTracker, null);
			}));
		}
	}

	public static void registerPackets() {
		new PacketRegistration().init();
	}
}