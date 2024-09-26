package com.natamus.dailyquests.quests.types;

import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.Constants;
import com.natamus.dailyquests.quests.object.QuestObject;
import com.natamus.dailyquests.quests.types.main.AbstractQuest;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;

public class CompleteRaid extends AbstractQuest {
	private final String name;

	public CompleteRaid() {
		this.name = this.getClass().getSimpleName();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isEnabled() {
		return ConfigHandler.enableCompleteRaidQuests;
	}

	@Override
	public Registry<GameEvent> getRegistry(Level level) {
		return level.registryAccess().registryOrThrow(this.getRegistryResourceKey());
	}

	@Override
	public boolean isAllowedIdentifier(Level level, ResourceLocation identifier) {
		return true;
	}

	@Override @Nullable
	public ResourceLocation getRandomQuestIdentifier(Level level) {
		return Constants.defaultResourceLocation;
	}

	@Override
	public int getRandomQuestProgressGoal(Level level, ResourceLocation identifier) {
		return 1;
	}

	@Override
	public ResourceLocation getResourceLocationFromObject(Level level, Object object) {
		return Constants.defaultResourceLocation;
	}

	@Override
	public String getLocalizedIdentifierName(Level level, ResourceLocation identifier) {
		return Component.translatable("event.minecraft.raid.victory.full").getString();
	}

	@Override
	public int getFinishExperience(QuestObject quest) {
		return 250;
	}

	@Override
	public void onQuestFinished(Player player) {

	}

	private ResourceKey<Registry<GameEvent>> getRegistryResourceKey() {
		return Registries.GAME_EVENT;
	}

	private Object objectCast(Object object) {
		return object;
	}

	public static ResourceLocation staticGetResourceLocationFromObject(Level level, Object object) {
		return Constants.defaultResourceLocation;
	}
}