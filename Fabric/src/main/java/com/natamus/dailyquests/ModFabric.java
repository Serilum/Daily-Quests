package com.natamus.dailyquests;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.collective.fabric.callbacks.CollectiveAnimalEvents;
import com.natamus.collective.fabric.callbacks.CollectiveItemEvents;
import com.natamus.collective.fabric.callbacks.CollectivePlayerEvents;
import com.natamus.dailyquests.cmds.CommandDailyQuests;
import com.natamus.dailyquests.events.DailyQuestServerEvents;
import com.natamus.dailyquests.events.DailyQuestTrackEvents;
import com.natamus.dailyquests.util.Reference;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

public class ModFabric implements ModInitializer {
	
	@Override
	public void onInitialize() {
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		setGlobalConstants();
		ModCommon.init();

		loadEvents();

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadEvents() {
		ServerWorldEvents.LOAD.register((server, level) -> {
			DailyQuestServerEvents.onWorldLoad(level);
		});

		ServerTickEvents.END_SERVER_TICK.register((minecraftServer) -> {
			DailyQuestServerEvents.onServerTick(minecraftServer);
		});

		ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
			DailyQuestServerEvents.onEntityJoinLevel(level, entity);
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			CommandDailyQuests.register(dispatcher);
		});

		// Tracking
		CollectivePlayerEvents.PLAYER_TICK.register((serverLevel, serverPlayer) -> {
			DailyQuestTrackEvents.onPlayerTick(serverLevel, serverPlayer);
		});

		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
			DailyQuestTrackEvents.onBlockBreak(world, player, pos, state, entity);
		});

		ServerLivingEntityEvents.ALLOW_DEATH.register((livingEntity, damageSource, damageAmount) -> {
			DailyQuestTrackEvents.onLivingDeath(livingEntity.level(), livingEntity, damageSource);
			return true;
		});

		CollectiveAnimalEvents.PRE_BABY_SPAWN.register((serverLevel, parentA, parentB, offspring) -> {
			DailyQuestTrackEvents.onBreeding(serverLevel, null, parentA, parentB, offspring);
			return true;
		});

		CollectiveItemEvents.ON_ITEM_USE_FINISHED.register((player, usedItem, newItem, hand) -> {
			DailyQuestTrackEvents.onItemUseFinished(player.level(), player, usedItem);
			return null;
		});
	}

	private static void setGlobalConstants() {

	}
}
