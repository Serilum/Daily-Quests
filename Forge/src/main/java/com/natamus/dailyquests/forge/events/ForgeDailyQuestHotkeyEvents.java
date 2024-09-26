package com.natamus.dailyquests.forge.events;

import com.natamus.dailyquests.data.ConstantsClient;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(Dist.CLIENT)
public class ForgeDailyQuestHotkeyEvents {
    @SubscribeEvent
	public void registerKeyBinding(RegisterKeyMappingsEvent e) {
    	ConstantsClient.toggleQuestListKey = new KeyMapping("dailyquests.key.togglequestlistcollapse", 46, "key.categories.misc");
    	e.register(ConstantsClient.toggleQuestListKey);
    }
}