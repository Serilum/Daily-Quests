package com.natamus.dailyquests.mixin;

import com.natamus.dailyquests.data.VariablesClient;
import com.natamus.dailyquests.events.DailyQuestsClientEvents;
import com.natamus.dailyquests.util.UtilClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PauseScreen.class, priority = 1001)
public abstract class PauseScreenMixin extends Screen {
	protected PauseScreenMixin(Component component) {
		super(component);
	}

	@Inject(method = "createPauseMenu()V", at = @At(value = "TAIL"))
	private void createPauseMenu(CallbackInfo ci) {
		UtilClient.resetReRollButtons();
	}

	@Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At(value = "TAIL"))
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
		if (VariablesClient.questListCollapsed) {
			return;
		}

		DailyQuestsClientEvents.renderOverlay(guiGraphics, null, (PauseScreen)(Object)this);

		if (!VariablesClient.reRollButtons.isEmpty()) {
			for (Button reRollButton : VariablesClient.reRollButtons.values()) {
				if (reRollButton.isHoveredOrFocused()) {
					Component tooltip = Component.literal(VariablesClient.playerDataObject.getReRollsLeft() + " remaining");
					guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
				}
			}
		}
	}
}
