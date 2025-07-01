package com.natamus.dailyquests.mixin;

import com.natamus.dailyquests.events.DailyQuestTrackEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FishingHook.class, priority = 1001)
public class FishingHookMixin {
	@Inject(method = "retrieve(Lnet/minecraft/world/item/ItemStack;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V"))
	public void retrieve(ItemStack itemStack, CallbackInfoReturnable<Integer> cir) {
		FishingHook fishingHook = (FishingHook)(Object)this;
		Player player = fishingHook.getPlayerOwner();
		if (player == null) {
			return;
		}

		DailyQuestTrackEvents.onFishCatch(player.level(), player);
	}
}
