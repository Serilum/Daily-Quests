package com.natamus.dailyquests.quests.types;

import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.Constants;
import com.natamus.dailyquests.data.Variables;
import com.natamus.dailyquests.quests.object.QuestObject;
import com.natamus.dailyquests.quests.types.main.AbstractQuest;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class TravelBlocks extends AbstractQuest {
	private final String name;

	public TravelBlocks() {
		this.name = this.getClass().getSimpleName();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isEnabled() {
		return ConfigHandler.enableTravelDistanceQuests;
	}

	@Override
	public Registry<?> getRegistry(Level level) {
		return null;
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
		return Constants.random.nextInt(500, 2000) + 1;
	}

	@Override
	public ResourceLocation getResourceLocationFromObject(Level level, Object object) {
		return Constants.defaultResourceLocation;
	}

	@Override
	public String getLocalizedIdentifierName(Level level, ResourceLocation identifier) {
		return "";
	}

	@Override
	public int getFinishExperience(QuestObject quest) {
		return quest.getGoalProgress() / 10;
	}

	@Override
	public void onQuestFinished(Player player) {
		Variables.lastPlayerLocation.remove(player.getUUID());
	}

	private ResourceKey<Registry<?>> getRegistryResourceKey() {
		return null;
	}

	private Object objectCast(Object object) {
		return object;
	}

	public static ResourceLocation staticGetResourceLocationFromObject(Level level, Object object) {
		return Constants.defaultResourceLocation;
	}
}