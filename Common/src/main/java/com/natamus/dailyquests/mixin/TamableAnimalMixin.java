package com.natamus.dailyquests.mixin;

import com.natamus.collective.functions.TaskFunctions;
import com.natamus.dailyquests.events.DailyQuestTrackEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TamableAnimal.class, priority = 1001)
public class TamableAnimalMixin {
	@Inject(method = "setTame(ZZ)V", at = @At(value = "TAIL"))
	public void setTame(boolean setTamed, boolean setEffects, CallbackInfo ci) {
		if (setTamed) {
			TamableAnimal tamableAnimal = (TamableAnimal)(Object)this;
			Level level = tamableAnimal.level();
			if (level.isClientSide) {
				return;
			}

			if (tamableAnimal.getOwner() != null) {
				return;
			}

			TaskFunctions.enqueueCollectiveTask(level.getServer(), () -> {
				LivingEntity livingEntity = tamableAnimal.getOwner();
				if (!(livingEntity instanceof Player)) {
					return;
				}

				DailyQuestTrackEvents.onTameAnimal(level, (Player)livingEntity, tamableAnimal);
			}, 0);
		}
	}
}