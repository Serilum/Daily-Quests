package com.natamus.dailyquests.quests.types;

import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.Constants;
import com.natamus.dailyquests.quests.object.QuestObject;
import com.natamus.dailyquests.quests.types.main.AbstractQuest;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SlayMob extends AbstractQuest {
	private final String name;

	public SlayMob() {
		this.name = this.getClass().getSimpleName();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isEnabled() {
		return ConfigHandler.enableSlayMobQuests;
	}

	@Override
	public Registry<EntityType<?>> getRegistry(Level level) {
		return level.registryAccess().lookupOrThrow(this.getRegistryResourceKey());
	}

	@Override
	public boolean isAllowedIdentifier(Level level, Identifier identifier) {
		Optional<Holder.Reference<EntityType<?>>> entityTypeOptionalReference = this.getRegistry(level).get(identifier);
		if (entityTypeOptionalReference.isEmpty()) {
			return false;
		}

		EntityType<?> entityType = entityTypeOptionalReference.get().value();
		return entityType.getCategory().equals(MobCategory.MONSTER) && !entityType.equals(EntityType.ILLUSIONER);
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
	    Optional<Holder.Reference<EntityType<?>>> entityTypeOptionalReference = this.getRegistry(level).get(identifier);

	    if (entityTypeOptionalReference.isPresent()) {
	        Holder.Reference<EntityType<?>> entityTypeHolder = entityTypeOptionalReference.get();
			EntityType<?> entityType = entityTypeHolder.value();
	        if (entityTypeHolder.is(Constants.BOSSES) || entityType.equals(EntityType.ENDER_DRAGON) || entityType.equals(EntityType.WARDEN) || entityType.equals(EntityType.WITHER)) {
	            return 1;
	        } else if (entityType.equals(EntityType.ELDER_GUARDIAN)) {
	            return 3;
	        }
	    }

	    return Constants.random.nextInt(6, 16) + 1;
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
		return quest.getGoalProgress() * 15;
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