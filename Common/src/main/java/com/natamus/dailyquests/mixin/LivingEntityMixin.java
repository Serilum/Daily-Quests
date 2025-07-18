package com.natamus.dailyquests.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = LivingEntity.class, priority = 1001)
public class LivingEntityMixin {
	/*@Inject(method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V"))
	public void hurt(ServerLevel serverLevel, DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
		if (!(((LivingEntity)(Object)this) instanceof Player player)) {
			return;
		}

		if (!damageSource.is(DamageTypeTags.IS_PROJECTILE)) {
			return;
		}

		DailyQuestTrackEvents.onArrowBlock(player.level(), player);
	}*/
}
