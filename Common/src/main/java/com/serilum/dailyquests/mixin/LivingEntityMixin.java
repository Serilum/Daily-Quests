package com.serilum.dailyquests.mixin;

import com.serilum.dailyquests.events.DailyQuestTrackEvents;
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
	@Inject(method = "applyItemBlocking(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)F", at = @At(value = "RETURN"))
	public void applyItemBlocking(ServerLevel serverLevel, DamageSource damageSource, float damage, CallbackInfoReturnable<Float> cir) {
		if (cir.getReturnValue() <= 0.0F) {
			return;
		}

		if (!(((LivingEntity)(Object)this) instanceof Player player)) {
			return;
		}

		if (!damageSource.is(DamageTypeTags.IS_PROJECTILE)) {
			return;
		}

		DailyQuestTrackEvents.onArrowBlock(player.level(), player);
	}
}
