package com.natamus.dailyquests.neoforge.events;

import com.natamus.collective.functions.WorldFunctions;
import com.natamus.dailyquests.events.DailyQuestTrackEvents;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class NeoForgeDailyQuestTrackEvents {
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post e) {
		Player player = e.getEntity();
		DailyQuestTrackEvents.onPlayerTick(player.level(), player);
	}

	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent e) {
		Level level = WorldFunctions.getWorldIfInstanceOfAndNotRemote(e.getLevel());
		if (level == null) {
			return;
		}

		DailyQuestTrackEvents.onBlockBreak(level, e.getPlayer(), e.getPos(), e.getState(), null);
	}

	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent e) {
		LivingEntity livingEntity = e.getEntity();
		DailyQuestTrackEvents.onLivingDeath(livingEntity.level(), livingEntity, e.getSource());
	}

	@SubscribeEvent
	public static void onBaby(BabyEntitySpawnEvent e) {
		AgeableMob child = e.getChild();
		DailyQuestTrackEvents.onBreeding(child.level(), e.getCausedByPlayer(), e.getParentA(), e.getParentB(), child);
	}

	@SubscribeEvent
	public static void onDrink(LivingEntityUseItemEvent.Finish e) {
		LivingEntity livingEntity = e.getEntity();
		if (!(livingEntity instanceof Player)) {
			return;
		}

		DailyQuestTrackEvents.onItemUseFinished(livingEntity.level(), (Player)livingEntity, e.getItem());
	}
}