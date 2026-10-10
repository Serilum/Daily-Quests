package com.serilum.dailyquests.quests.types;

import com.serilum.dailyquests.config.ConfigHandler;
import com.serilum.dailyquests.data.Constants;
import com.serilum.dailyquests.data.Variables;
import com.serilum.dailyquests.quests.object.QuestObject;
import com.serilum.dailyquests.quests.types.main.AbstractQuest;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class TameAnimal extends AbstractQuest {
	private final String name;

	public TameAnimal() {
		this.name = this.getClass().getSimpleName();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isEnabled() {
		return ConfigHandler.enableTameAnimalQuests;
	}

	@Override
	public Registry<EntityType<?>> getRegistry(Level level) {
		return level.registryAccess().registryOrThrow(this.getRegistryResourceKey());
	}

	@Override
	public boolean isAllowedIdentifier(Level level, ResourceLocation identifier) {
		return Variables.tamableMobs.contains(identifier);
	}

	@Override @Nullable
	public ResourceLocation getRandomQuestIdentifier(Level level) {
		if (Variables.tamableMobs.isEmpty()) {
			return null;
		}

		return Variables.tamableMobs.get(Constants.random.nextInt(Variables.tamableMobs.size()));
	}

	@Override
	public int getRandomQuestProgressGoal(Level level, ResourceLocation identifier) {
		return 1;
	}

	@Override
	public ResourceLocation getResourceLocationFromObject(Level level, Object object) {
		return this.getRegistry(level).getKey(this.objectCast(object));
	}

	@Override
	public String getLocalizedIdentifierName(Level level, ResourceLocation identifier) {
		Registry<EntityType<?>> registry = this.getRegistry(level);
		if (registry.containsKey(identifier)) {
			return registry.get(identifier).getDescription().getString().replaceAll("[\\[\\]]", "");
		}
		return identifier.toString();
	}

	@Override
	public int getFinishExperience(QuestObject quest) {
		return 150;
	}

	@Override
	public void onQuestFinished(Player player) {

	}

	private ResourceKey<Registry<EntityType<?>>> getRegistryResourceKey() {
		return Registries.ENTITY_TYPE;
	}

	private EntityType<?> objectCast(Object object) {
		return (EntityType<?>)object;
	}

	public static ResourceLocation staticGetResourceLocationFromObject(Level level, Object object) {
		return level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE).getKey((EntityType<?>)(object));
	}
}