package com.natamus.dailyquests.quests.types;

import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.Constants;
import com.natamus.dailyquests.quests.object.QuestObject;
import com.natamus.dailyquests.quests.types.main.AbstractQuest;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class CatchFish extends AbstractQuest {
	private final String name;

	public CatchFish() {
		this.name = this.getClass().getSimpleName();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isEnabled() {
		return ConfigHandler.enableCatchFishQuests;
	}

	@Override
	public Registry<Item> getRegistry(Level level) {
		return level.registryAccess().lookupOrThrow(this.getRegistryResourceKey());
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
		return Constants.random.nextInt(8, 16) + 1;
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
		return quest.getGoalProgress() * 6;
	}

	@Override
	public void onQuestFinished(Player player) {

	}

	private ResourceKey<Registry<Item>> getRegistryResourceKey() {
		return Registries.ITEM;
	}

	private Item objectCast(Object object) {
		return (Item)object;
	}

	public static ResourceLocation staticGetResourceLocationFromObject(Level level, Object object) {
		return Constants.defaultResourceLocation;
	}
}