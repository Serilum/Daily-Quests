package com.natamus.dailyquests;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.dailyquests.forge.config.IntegrateForgeConfig;
import com.natamus.dailyquests.forge.events.ForgeDailyQuestClientEvents;
import com.natamus.dailyquests.forge.events.ForgeDailyQuestServerEvents;
import com.natamus.dailyquests.forge.events.ForgeDailyQuestTrackEvents;
import com.natamus.dailyquests.util.Reference;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Reference.MOD_ID)
public class ModForge {
	
	public ModForge(FMLJavaModLoadingContext modLoadingContext) {
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		BusGroup busGroup = modLoadingContext.getModBusGroup();

		FMLLoadCompleteEvent.getBus(busGroup).addListener(this::loadComplete);
		ModCommon.registerHotkeys();

		setGlobalConstants();
		ModCommon.init();

		IntegrateForgeConfig.registerScreen(modLoadingContext);

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		if (FMLEnvironment.dist.equals(Dist.CLIENT)) {
			ForgeDailyQuestClientEvents.registerEventsInBus();
		}

    	ForgeDailyQuestServerEvents.registerEventsInBus();
		ForgeDailyQuestTrackEvents.registerEventsInBus();
	}

	private static void setGlobalConstants() {

	}
}