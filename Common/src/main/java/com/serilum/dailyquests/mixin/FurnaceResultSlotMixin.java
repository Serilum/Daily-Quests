package com.serilum.dailyquests.mixin;

import com.serilum.dailyquests.events.DailyQuestTrackEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FurnaceResultSlot.class, priority = 1001)
public class FurnaceResultSlotMixin {
	@Shadow private @Final Player player;
	@Shadow private int removeCount;

	@Inject(method = "checkTakeAchievements(Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "HEAD"))
	protected void checkTakeAchievements(ItemStack itemStack, CallbackInfo ci) {
		if (player == null || removeCount <= 0 || itemStack.isEmpty()) {
			return;
		}

		DailyQuestTrackEvents.onSmeltItem(player.level(), player, itemStack.copyWithCount(removeCount));
	}
}
