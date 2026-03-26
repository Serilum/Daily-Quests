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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BrewPotion extends AbstractQuest {
	private final String name;

	public BrewPotion() {
		this.name = this.getClass().getSimpleName();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isEnabled() {
		return ConfigHandler.enableBrewPotionQuests;
	}

	@Override
	public Registry<Potion> getRegistry(Level level) {
		return level.registryAccess().lookupOrThrow(this.getRegistryResourceKey());
	}

	@Override
	public boolean isAllowedIdentifier(Level level, Identifier identifier) {
		String stringIdentifier = identifier.toString();
		return !stringIdentifier.contains("strong_") && !stringIdentifier.contains("long_") && !stringIdentifier.endsWith("luck");
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
		return 3;
	}

	@Override
	public Identifier getIdentifierFromObject(Level level, Object object) {
		return this.getRegistry(level).getKey(this.objectCast(object));
	}

	@Override
	public String getLocalizedIdentifierName(Level level, Identifier identifier) {
		Registry<Potion> registry = this.getRegistry(level);
        if (registry.containsKey(identifier)) {
			Optional<Holder.Reference<Potion>> holderOptional = registry.get(identifier);
			if (holderOptional.isPresent()) {
				ItemStack potionStack = PotionContents.createItemStack(Items.POTION, holderOptional.get());
				return potionStack.getDisplayName().getString().replaceAll("[\\[\\]]", "");
			}
        }
		return identifier.toString();
	}

	@Override
	public int getFinishExperience(QuestObject quest) {
		return quest.getGoalProgress() * 30;
	}

	@Override
	public void onQuestFinished(Player player) {

	}

	private ResourceKey<Registry<Potion>> getRegistryResourceKey() {
		return Registries.POTION;
	}

	private Potion objectCast(Object object) {
		return (Potion)object;
	}

	public static Identifier staticGetIdentifierFromObject(Level level, Object object) {
		return level.registryAccess().lookupOrThrow(Registries.POTION).getKey((Potion)(object));
	}
}