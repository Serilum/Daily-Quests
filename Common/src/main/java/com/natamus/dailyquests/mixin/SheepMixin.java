package com.natamus.dailyquests.mixin;

import com.natamus.dailyquests.events.DailyQuestTrackEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Sheep.class, priority = 1001)
public class SheepMixin {
	@Inject(method = "mobInteract", at = @At("HEAD"))
	private void onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
	    Sheep sheep = (Sheep)(Object)this;

	    if (player.getItemInHand(hand).is(Items.SHEARS) && sheep.readyForShearing()) {
	        DailyQuestTrackEvents.onSheepShear(player.level(), player, sheep);
	    }
	}
}
