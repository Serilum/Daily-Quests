package com.natamus.dailyquests.mixin;

import com.natamus.dailyquests.events.DailyQuestTrackEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FurnaceResultSlot.class, priority = 1001)
public class FurnaceMixin {
	@Inject(method = "onTake(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "HEAD"))
	public void onTake(Player player, ItemStack itemStack, CallbackInfo ci) {
		if (player == null) {
			return;
		}

		DailyQuestTrackEvents.onSmeltItem(player.level(), player, itemStack);
	}
}
