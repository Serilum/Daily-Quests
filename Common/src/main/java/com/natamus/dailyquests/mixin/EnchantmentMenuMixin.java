package com.natamus.dailyquests.mixin;

import com.natamus.dailyquests.events.DailyQuestTrackEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EnchantmentMenu.class, priority = 1001)
public class EnchantmentMenuMixin {
	@Shadow private @Final Container enchantSlots;

	@Inject(method = "clickMenuButton(Lnet/minecraft/world/entity/player/Player;I)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ContainerLevelAccess;execute(Ljava/util/function/BiConsumer;)V"))
	public void clickMenuButton(Player player, int i, CallbackInfoReturnable<Boolean> cir) {
		if (player == null) {
			return;
		}

		DailyQuestTrackEvents.onEnchantItem(player.level(), player, this.enchantSlots.getItem(0));
	}
}
