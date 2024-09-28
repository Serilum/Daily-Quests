package com.natamus.dailyquests.quests.types;

import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.Constants;
import com.natamus.dailyquests.quests.object.QuestObject;
import com.natamus.dailyquests.quests.types.main.AbstractQuest;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
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
		return level.registryAccess().registryOrThrow(this.getRegistryResourceKey());
	}

	@Override
	public boolean isAllowedIdentifier(Level level, ResourceLocation identifier) {
		String stringIdentifier = identifier.toString();
		return !stringIdentifier.contains("strong_") && !stringIdentifier.contains("long_") && !stringIdentifier.endsWith("luck");
	}

	@Override @Nullable
	public ResourceLocation getRandomQuestIdentifier(Level level) {
		List<ResourceLocation> resourceLocations = new ArrayList<ResourceLocation>(this.getRegistry(level).keySet());

		ResourceLocation identifier = resourceLocations.get(Constants.random.nextInt(resourceLocations.size()));
		while (!this.isAllowedIdentifier(level, identifier)) {
			identifier = resourceLocations.get(Constants.random.nextInt(resourceLocations.size()));
		}

		return identifier;
	}

	@Override
	public int getRandomQuestProgressGoal(Level level, ResourceLocation identifier) {
		return 3;
	}

	@Override
	public ResourceLocation getResourceLocationFromObject(Level level, Object object) {
		return this.getRegistry(level).getKey(this.objectCast(object));
	}

	@Override
	public String getLocalizedIdentifierName(Level level, ResourceLocation identifier) {
		Registry<Potion> registry = this.getRegistry(level);
        if (registry.containsKey(identifier)) {
			Optional<Holder.Reference<Potion>> holderOptional = registry.getHolder(identifier);
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

	public static ResourceLocation staticGetResourceLocationFromObject(Level level, Object object) {
		return level.registryAccess().registryOrThrow(Registries.POTION).getKey((Potion)(object));
	}
}