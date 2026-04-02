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
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ExploreBiome extends AbstractQuest {
	private final String name;

	public ExploreBiome() {
		this.name = this.getClass().getSimpleName();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isEnabled() {
		return ConfigHandler.enableExploreBiomeQuests;
	}

	@Override
	public Registry<Biome> getRegistry(Level level) {
		return level.registryAccess().lookupOrThrow(this.getRegistryResourceKey());
	}

	@Override
	public boolean isAllowedIdentifier(Level level, Identifier identifier) {
		return true;
	}

	@Override @Nullable
	public Identifier getRandomQuestIdentifier(Level level) {
		List<Identifier> resourceLocations = new ArrayList<>(this.getRegistry(level).keySet());

		Identifier identifier = resourceLocations.get(Constants.random.nextInt(resourceLocations.size()));
		while (!this.isAllowedIdentifier(level, identifier)) {
			identifier = resourceLocations.get(Constants.random.nextInt(resourceLocations.size()));
		}

		return identifier;
	}

	@Override
	public int getRandomQuestProgressGoal(Level level, Identifier identifier) {
		return 1;
	}

	@Override
	public Identifier getIdentifierFromObject(Level level, Object object) {
		return this.getRegistry(level).getKey(this.objectCast(object));
	}

	@Override
	public String getLocalizedIdentifierName(Level level, Identifier identifier) {
		Registry<Biome> registry = this.getRegistry(level);
        if (registry.containsKey(identifier)) {
			String translationKey = "biome." + identifier.getNamespace() + "." + identifier.getPath();
            return Component.translatable(translationKey).getString();
        }
		return identifier.toString();
	}

	@Override
	public int getFinishExperience(QuestObject quest) {
		return 200;
	}

	@Override
	public void onQuestFinished(Player player) {

	}

	private ResourceKey<Registry<Biome>> getRegistryResourceKey() {
		return Registries.BIOME;
	}

	private Biome objectCast(Object object) {
		return (Biome)object;
	}

	public static Identifier staticGetIdentifierFromObject(Level level, Object object) {
		return level.registryAccess().lookupOrThrow(Registries.BIOME).getKey((Biome)(object));
	}
}