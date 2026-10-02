package com.serilum.dailyquests;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.serilum.dailyquests.forge.config.IntegrateForgeConfig;
import com.serilum.dailyquests.forge.events.ForgeDailyQuestClientEvents;
import com.serilum.dailyquests.forge.events.ForgeDailyQuestServerEvents;
import com.serilum.dailyquests.forge.events.ForgeDailyQuestTrackEvents;
import com.serilum.dailyquests.util.Reference;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Reference.MOD_ID)
public class ModForge {
	
	public ModForge() {
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(this::loadComplete);
		ModCommon.registerHotkeys();

		setGlobalConstants();
		ModCommon.init();

		IntegrateForgeConfig.registerScreen(ModLoadingContext.get());

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		if (FMLEnvironment.dist.equals(Dist.CLIENT)) {
			MinecraftForge.EVENT_BUS.register(ForgeDailyQuestClientEvents.class);
		}

		MinecraftForge.EVENT_BUS.register(ForgeDailyQuestServerEvents.class);
		MinecraftForge.EVENT_BUS.register(ForgeDailyQuestTrackEvents.class);
	}

	private static void setGlobalConstants() {

	}
}