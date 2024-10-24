package com.natamus.dailyquests.mixin;

import com.natamus.dailyquests.events.DailyQuestTrackEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 1001)
public class LivingEntityMixin {
	@Inject(method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V"))
	public void hurt(ServerLevel serverLevel, DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
		if (!(((LivingEntity)(Object)this) instanceof Player player)) {
			return;
		}

		if (!damageSource.is(DamageTypeTags.IS_PROJECTILE)) {
			return;
		}

		DailyQuestTrackEvents.onArrowBlock(player.level(), player);
	}
}
