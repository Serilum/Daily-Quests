package com.natamus.dailyquests.mixin;

import com.natamus.dailyquests.events.DailyQuestTrackEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Ghast.class, priority = 1001)
public class GhastMixin {
	@Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/FlyingMob;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 0))
	public void hurt(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
		if (!(damageSource.getEntity() instanceof Player player)) {
			return;
		}

		DailyQuestTrackEvents.onGhastReflect(player.level(), player, (Ghast)(Object)this);
	}
}
