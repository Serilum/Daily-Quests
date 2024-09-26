package com.natamus.dailyquests;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.dailyquests.neoforge.config.IntegrateNeoForgeConfig;
import com.natamus.dailyquests.neoforge.events.NeoForgeDailyQuestClientEvents;
import com.natamus.dailyquests.neoforge.events.NeoForgeDailyQuestHotkeyEvents;
import com.natamus.dailyquests.neoforge.events.NeoForgeDailyQuestServerEvents;
import com.natamus.dailyquests.neoforge.events.NeoForgeDailyQuestTrackEvents;
import com.natamus.dailyquests.util.Reference;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Reference.MOD_ID)
public class ModNeoForge {
	
	public ModNeoForge(IEventBus modEventBus) {
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		modEventBus.addListener(this::loadComplete);
		modEventBus.register(NeoForgeDailyQuestHotkeyEvents.class);

		setGlobalConstants();
		ModCommon.init();

		IntegrateNeoForgeConfig.registerScreen(ModLoadingContext.get());

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		if (FMLEnvironment.dist.equals(Dist.CLIENT)) {
			NeoForge.EVENT_BUS.register(NeoForgeDailyQuestClientEvents.class);
		}

		NeoForge.EVENT_BUS.register(NeoForgeDailyQuestServerEvents.class);
		NeoForge.EVENT_BUS.register(NeoForgeDailyQuestTrackEvents.class);
	}

	private static void setGlobalConstants() {

	}
}