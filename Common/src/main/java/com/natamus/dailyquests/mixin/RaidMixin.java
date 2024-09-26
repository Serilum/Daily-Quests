package com.natamus.dailyquests.mixin;

import com.natamus.dailyquests.events.DailyQuestTrackEvents;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Raid.class, priority = 1001)
public class RaidMixin {
	@Shadow private @Final ServerBossEvent raidEvent;

	@Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;isVictory()Z"))
	public void tick(CallbackInfo ci) {
		Raid raid = (Raid)(Object)this;
		if (raid.isVictory()) {
			for (Player player : this.raidEvent.getPlayers()) {
				if (player == null) {
					continue;
				}

				DailyQuestTrackEvents.onRaidComplete(player.level(), player, raid);
			}
		}
	}
}
