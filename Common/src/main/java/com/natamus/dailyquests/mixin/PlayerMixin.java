package com.natamus.dailyquests.mixin;

import com.natamus.dailyquests.events.DailyQuestTrackEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Player.class, priority = 1001)
public class PlayerMixin {
	@Inject(method = "giveExperienceLevels(I)V", at = @At(value = "HEAD"))
	public void giveExperienceLevels(int levelCount, CallbackInfo ci) {
		Player player = (Player)(Object)this;
		if (player == null) {
			return;
		}

		DailyQuestTrackEvents.onGainExperienceLevel(player.level(), player, levelCount);
	}

	@Inject(method = "hurtCurrentlyUsedShield(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/stats/Stat;)V"))
	protected void hurtCurrentlyUsedShield(float f, CallbackInfo ci) {
		Player player = (Player)(Object)this;
		if (player == null) {
			return;
		}

		DailyQuestTrackEvents.onItemUseFinished(player.level(), player, new ItemStack(Items.SHIELD));
	}
}
