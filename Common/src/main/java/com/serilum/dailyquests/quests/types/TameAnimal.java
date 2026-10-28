package com.serilum.dailyquests.quests.types;

import com.serilum.dailyquests.config.ConfigHandler;
import com.serilum.dailyquests.data.Constants;
import com.serilum.dailyquests.data.Variables;
import com.serilum.dailyquests.quests.object.QuestObject;
import com.serilum.dailyquests.quests.types.main.AbstractQuest;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;
import java.util.Optional;

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
		return level.registryAccess().lookupOrThrow(this.getRegistryResourceKey());
	}

	@Override
	public boolean isAllowedIdentifier(Level level, Identifier identifier) {
		return Variables.tamableMobs.contains(identifier);
	}

	@Override @Nullable
	public Identifier getRandomQuestIdentifier(Level level) {
		if (Variables.tamableMobs.isEmpty()) {
			return null;
		}

		return Variables.tamableMobs.get(Constants.random.nextInt(Variables.tamableMobs.size()));
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
		Registry<EntityType<?>> registry = this.getRegistry(level);
		if (registry.containsKey(identifier)) {
			Optional<Holder.Reference<EntityType<?>>> entityTypeOptionalReference = registry.get(identifier);
			if (entityTypeOptionalReference.isPresent()) {
				return entityTypeOptionalReference.get().value().getDescription().getString().replaceAll("[\\[\\]]", "");
			}
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

	public static Identifier staticGetIdentifierFromObject(Level level, Object object) {
		return level.registryAccess().lookupOrThrow(Registries.ENTITY_TYPE).getKey((EntityType<?>)(object));
	}
}