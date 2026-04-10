package com.natamus.dailyquests.mixin;

import com.natamus.dailyquests.events.DailyQuestTrackEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Creeper.class, priority = 1001)
public class CreeperMixin {
	@Inject(method = "mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
	protected void mobInteract(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
		if (player == null) {
			return;
		}

		DailyQuestTrackEvents.onCreeperIgnite(player.level(), player, (Creeper)(Object)this);
	}
}
