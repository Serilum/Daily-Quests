package com.natamus.dailyquests.mixin;

import com.natamus.dailyquests.events.DailyQuestTrackEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ResultSlot.class, priority = 1001)
public class ResultSlotMixin {
	@Shadow private @Final Player player;

	@Inject(method = "checkTakeAchievements(Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "TAIL"))
	protected void checkTakeAchievements(ItemStack itemStack, CallbackInfo ci) {
		if (player == null) {
			return;
		}

		DailyQuestTrackEvents.onItemCraft(player.level(), player, itemStack);
	}
}
