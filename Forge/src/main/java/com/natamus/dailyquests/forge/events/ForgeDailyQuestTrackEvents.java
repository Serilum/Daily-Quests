package com.natamus.dailyquests.forge.events;

import com.natamus.collective.functions.WorldFunctions;
import com.natamus.dailyquests.events.DailyQuestTrackEvents;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ForgeDailyQuestTrackEvents {
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent e) {
		if (e.phase.equals(TickEvent.Phase.START)) {
			return;
		}

		Player player = e.player;
		DailyQuestTrackEvents.onPlayerTick(player.level(), player);
	}

	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent e) {
		Level level = WorldFunctions.getWorldIfInstanceOfAndNotRemote(e.getLevel());
		if (level == null) {
			return;
		}

		DailyQuestTrackEvents.onBlockBreak(level, e.getPlayer(), e.getPos(), e.getState(), null);
	}

	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent e) {
		LivingEntity livingEntity = e.getEntity();
		DailyQuestTrackEvents.onLivingDeath(livingEntity.level(), livingEntity, e.getSource());
	}

	@SubscribeEvent
	public void onBaby(BabyEntitySpawnEvent e) {
		AgeableMob child = e.getChild();
		DailyQuestTrackEvents.onBreeding(child.level(), e.getCausedByPlayer(), e.getParentA(), e.getParentB(), child);
	}

	@SubscribeEvent
	public void onDrink(LivingEntityUseItemEvent.Finish e) {
		LivingEntity livingEntity = e.getEntity();
		if (!(livingEntity instanceof Player)) {
			return;
		}

		DailyQuestTrackEvents.onItemUseFinished(livingEntity.level(), (Player)livingEntity, e.getItem());
	}
}