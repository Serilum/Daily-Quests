package com.natamus.dailyquests.mixin;

import com.natamus.collective.services.Services;
import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.VariablesClient;
import com.natamus.dailyquests.util.UtilClient;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(value = MinecraftServer.class, priority = 1001)
public abstract class MinecraftServerMixin {
	@Shadow public abstract boolean isDedicatedServer();

	@Unique UUID timerUUID = UUID.randomUUID();

	@Inject(method = "saveAllChunks", at= @At("HEAD"))
	private void startSaving(CallbackInfoReturnable<Boolean> info) {
		if (!Services.MODLOADER.isModLoaded("bedrockify")) {
			return;
		}

		if (!this.isDedicatedServer() && ConfigHandler.lowerQuestListWhenBedrockifyModSaves) {
			timerUUID = UUID.randomUUID();

			VariablesClient.lowerForBedrockifySaving = true;
			UtilClient.resetReRollButtons();
		}
	}

	@Inject(method = "saveAllChunks", at= @At("RETURN"))
	private void stopSaving(CallbackInfoReturnable<Boolean> info) {
		if (!Services.MODLOADER.isModLoaded("bedrockify")) {
			return;
		}

		if (!this.isDedicatedServer() && ConfigHandler.lowerQuestListWhenBedrockifyModSaves) {
			final UUID currentUUID = timerUUID;
			new Thread(() -> {
				try  { Thread.sleep( 3000 ); }
				catch (InterruptedException ignored)  {}

				if (currentUUID.equals(timerUUID)) {
					VariablesClient.lowerForBedrockifySaving = false;
					UtilClient.resetReRollButtons();
				}
			}).start();
		}
	}
}
