package com.natamus.dailyquests.quests.types;

import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.Constants;
import com.natamus.dailyquests.data.Variables;
import com.natamus.dailyquests.quests.object.QuestObject;
import com.natamus.dailyquests.quests.types.main.AbstractQuest;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SmeltItem extends AbstractQuest {
	private final String name;

	public SmeltItem() {
		this.name = this.getClass().getSimpleName();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isEnabled() {
		return ConfigHandler.enableSmeltItemQuests;
	}

	@Override
	public Registry<Item> getRegistry(Level level) {
		return level.registryAccess().registryOrThrow(this.getRegistryResourceKey());
	}

	@Override
	public boolean isAllowedIdentifier(Level level, ResourceLocation identifier) {
		for (List<ResourceLocation> ingredientIdentifiers : Variables.smeltableItems.values()) {
			if (ingredientIdentifiers.contains(identifier)) {
				return true;
			}
		}

		return false;
	}

	@Override @Nullable
	public ResourceLocation getRandomQuestIdentifier(Level level) {
		Set<ResourceLocation> possibleIdentifiers = new HashSet<>();

		for (List<ResourceLocation> ingredientIdentifiers : Variables.smeltableItems.values()) {
			possibleIdentifiers.addAll(ingredientIdentifiers);
		}

		List<ResourceLocation> identifierList = new ArrayList<>(possibleIdentifiers);
		return identifierList.get(Constants.random.nextInt(identifierList.size()));
	}

	@Override
	public int getRandomQuestProgressGoal(Level level, ResourceLocation identifier) {
		return Constants.random.nextInt(4, 16) + 1;
	}

	@Override
	public ResourceLocation getResourceLocationFromObject(Level level, Object object) {
		return this.getRegistry(level).getKey(this.objectCast(object));
	}

	@Override
	public String getLocalizedIdentifierName(Level level, ResourceLocation identifier) {
		Registry<Item> registry = this.getRegistry(level);
        if (registry.containsKey(identifier)) {
            ItemStack itemStack = new ItemStack(registry.get(identifier));
            return itemStack.getDisplayName().getString().replaceAll("[\\[\\]]", "");
        }
		return identifier.toString();
	}

	@Override
	public int getFinishExperience(QuestObject quest) {
		return quest.getGoalProgress() * 6;
	}

	@Override
	public void onQuestFinished(Player player) {

	}

	private ResourceKey<Registry<Item>> getRegistryResourceKey() {
		return Registries.ITEM;
	}

	private Item objectCast(Object object) {
		return (Item)object;
	}

	public static ResourceLocation staticGetResourceLocationFromObject(Level level, Object object) {
		return level.registryAccess().registryOrThrow(Registries.ITEM).getKey((Item)(object));
	}
}