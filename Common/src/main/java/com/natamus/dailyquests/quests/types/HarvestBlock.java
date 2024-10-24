package com.natamus.dailyquests.quests.types;

import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.Constants;
import com.natamus.dailyquests.data.Variables;
import com.natamus.dailyquests.quests.object.QuestObject;
import com.natamus.dailyquests.quests.types.main.AbstractQuest;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.Optional;

public class HarvestBlock extends AbstractQuest {
	private final String name;

	public HarvestBlock() {
		this.name = this.getClass().getSimpleName();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isEnabled() {
		return ConfigHandler.enableHarvestBlockQuests;
	}

	@Override
	public Registry<Block> getRegistry(Level level) {
		return level.registryAccess().lookupOrThrow(this.getRegistryResourceKey());
	}

	@Override
	public boolean isAllowedIdentifier(Level level, ResourceLocation identifier) {
		return Variables.naturallyGeneratedOres.contains(identifier);
	}

	@Override @Nullable
	public ResourceLocation getRandomQuestIdentifier(Level level) {
		return Variables.naturallyGeneratedOres.get(Constants.random.nextInt(Variables.naturallyGeneratedOres.size()));
	}

	@Override
	public int getRandomQuestProgressGoal(Level level, ResourceLocation identifier) {
		return (Constants.random.nextInt(16) + 1) * 4;
	}

	@Override
	public ResourceLocation getResourceLocationFromObject(Level level, Object object) {
		return this.getRegistry(level).getKey(this.objectCast(object));
	}

	@Override
	public String getLocalizedIdentifierName(Level level, ResourceLocation identifier) {
		Registry<Block> registry = this.getRegistry(level);
        if (registry.containsKey(identifier)) {
			Optional<Holder.Reference<Block>> blockOptionalReference = registry.get(identifier);
			if (blockOptionalReference.isPresent()) {
				return blockOptionalReference.get().value().getName().getString().replaceAll("[\\[\\]]", "");
			}
        }
		return identifier.toString();
	}

	@Override
	public int getFinishExperience(QuestObject quest) {
		return quest.getGoalProgress() * 10;
	}

	@Override
	public void onQuestFinished(Player player) {

	}

	private ResourceKey<Registry<Block>> getRegistryResourceKey() {
		return Registries.BLOCK;
	}

	private Block objectCast(Object object) {
		return (Block)object;
	}

	public static ResourceLocation staticGetResourceLocationFromObject(Level level, Object object) {
		return level.registryAccess().lookupOrThrow(Registries.BLOCK).getKey((Block)(object));
	}
}