package com.natamus.dailyquests;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.dailyquests.forge.config.IntegrateForgeConfig;
import com.natamus.dailyquests.forge.events.ForgeDailyQuestClientEvents;
import com.natamus.dailyquests.forge.events.ForgeDailyQuestHotkeyEvents;
import com.natamus.dailyquests.forge.events.ForgeDailyQuestServerEvents;
import com.natamus.dailyquests.forge.events.ForgeDailyQuestTrackEvents;
import com.natamus.dailyquests.util.Reference;
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
		modEventBus.register(new ForgeDailyQuestHotkeyEvents());

		setGlobalConstants();
		ModCommon.init();

		IntegrateForgeConfig.registerScreen(ModLoadingContext.get());

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		if (FMLEnvironment.dist.equals(Dist.CLIENT)) {
			MinecraftForge.EVENT_BUS.register(new ForgeDailyQuestClientEvents());
		}

    	MinecraftForge.EVENT_BUS.register(new ForgeDailyQuestServerEvents());
		MinecraftForge.EVENT_BUS.register(new ForgeDailyQuestTrackEvents());
	}

	private static void setGlobalConstants() {

	}
}