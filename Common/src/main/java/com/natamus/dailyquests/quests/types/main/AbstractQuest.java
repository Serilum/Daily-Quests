package com.natamus.dailyquests.quests.types.main;

import com.natamus.dailyquests.quests.object.QuestObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class AbstractQuest {
	public abstract String getName();
	public abstract boolean isEnabled();
	public abstract Registry<?> getRegistry(Level level);
	public abstract String getLocalizedIdentifierName(Level level, Identifier identifier);
	public abstract Identifier getIdentifierFromObject(Level level, Object object);
	public abstract boolean isAllowedIdentifier(Level level, Identifier identifier);
	public abstract Identifier getRandomQuestIdentifier(Level level);
	public abstract int getRandomQuestProgressGoal(Level level, Identifier identifier);
	public abstract int getFinishExperience(QuestObject quest);
	public abstract void onQuestFinished(Player player);
}