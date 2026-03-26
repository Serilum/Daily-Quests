package com.natamus.dailyquests.quests.types;

import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.Constants;
import com.natamus.dailyquests.quests.object.QuestObject;
import com.natamus.dailyquests.quests.types.main.AbstractQuest;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import org.jetbrains.annotations.Nullable;

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
		return level.registryAccess().lookupOrThrow(this.getRegistryResourceKey());
	}

	@Override
	public boolean isAllowedIdentifier(Level level, Identifier identifier) {
		return true;
	}

	@Override @Nullable
	public Identifier getRandomQuestIdentifier(Level level) {
		return Constants.defaultIdentifier;
	}

	@Override
	public int getRandomQuestProgressGoal(Level level, Identifier identifier) {
		return 1;
	}

	@Override
	public Identifier getIdentifierFromObject(Level level, Object object) {
		return Constants.defaultIdentifier;
	}

	@Override
	public String getLocalizedIdentifierName(Level level, Identifier identifier) {
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

	public static Identifier staticGetIdentifierFromObject(Level level, Object object) {
		return Constants.defaultIdentifier;
	}
}