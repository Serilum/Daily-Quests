package com.natamus.dailyquests.mixin;

import com.natamus.dailyquests.events.DailyQuestTrackEvents;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractVillager.class, priority = 1001)
public class AbstractVillagerMixin {
	@Shadow private Player tradingPlayer;;

	@Inject(method = "notifyTrade(Lnet/minecraft/world/item/trading/MerchantOffer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/TradeTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/npc/AbstractVillager;Lnet/minecraft/world/item/ItemStack;)V"))
	public void notifyTrade(MerchantOffer merchantOffer, CallbackInfo ci) {
		if (this.tradingPlayer == null) {
			return;
		}

		AbstractVillager abstractVillager = (AbstractVillager)(Object)this;
		if (abstractVillager instanceof Villager villager) {
			DailyQuestTrackEvents.onVillagerTrade(abstractVillager.level(), this.tradingPlayer, villager);
		}
	}
}
