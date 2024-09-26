package com.natamus.dailyquests.mixin;

import com.natamus.dailyquests.events.DailyQuestTrackEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$PotionSlot", priority = 1001)
public class BrewingStandMenuMixin {
	@Inject(method = "onTake(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "HEAD"))
	private void onTake(Player player, ItemStack itemStack, CallbackInfo ci) {
		if (player == null) {
			return;
		}

		DailyQuestTrackEvents.onBrewPotion(player.level(), player, itemStack);
	}
}
