package com.natamus.dailyquests.mixin;

import com.natamus.dailyquests.events.DailyQuestTrackEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Ghast.class, priority = 1001)
public class GhastMixin {
	@Inject(method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/FlyingMob;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 0))
	public void hurt(ServerLevel serverLevel, DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
		if (!(damageSource.getEntity() instanceof Player player)) {
			return;
		}

		DailyQuestTrackEvents.onGhastReflect(player.level(), player, (Ghast)(Object)this);
	}
}
