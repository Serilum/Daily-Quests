package com.natamus.dailyquests.quests.types.main;

import com.natamus.dailyquests.quests.object.QuestObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class AbstractQuest {
	public abstract String getName();
	public abstract boolean isEnabled();
	public abstract Registry<?> getRegistry(Level level);
	public abstract String getLocalizedIdentifierName(Level level, ResourceLocation identifier);
	public abstract ResourceLocation getResourceLocationFromObject(Level level, Object object);
	public abstract boolean isAllowedIdentifier(Level level, ResourceLocation identifier);
	public abstract ResourceLocation getRandomQuestIdentifier(Level level);
	public abstract int getRandomQuestProgressGoal(Level level, ResourceLocation identifier);
	public abstract int getFinishExperience(QuestObject quest);
	public abstract void onQuestFinished(Player player);
}