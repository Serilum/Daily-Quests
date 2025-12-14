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
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;
import java.util.*;

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
		return level.registryAccess().lookupOrThrow(this.getRegistryResourceKey());
	}

	@Override
	public boolean isAllowedIdentifier(Level level, Identifier identifier) {
		for (List<Identifier> ingredientIdentifiers : Variables.smeltableItems.values()) {
			if (ingredientIdentifiers.contains(identifier)) {
				return true;
			}
		}

		return false;
	}

	@Override @Nullable
	public Identifier getRandomQuestIdentifier(Level level) {
		Set<Identifier> possibleIdentifiers = new HashSet<>();

		for (List<Identifier> ingredientIdentifiers : Variables.smeltableItems.values()) {
			possibleIdentifiers.addAll(ingredientIdentifiers);
		}

		List<Identifier> identifierList = new ArrayList<>(possibleIdentifiers);
		return identifierList.get(Constants.random.nextInt(identifierList.size()));
	}

	@Override
	public int getRandomQuestProgressGoal(Level level, Identifier identifier) {
		return Constants.random.nextInt(4, 16) + 1;
	}

	@Override
	public Identifier getIdentifierFromObject(Level level, Object object) {
		return this.getRegistry(level).getKey(this.objectCast(object));
	}

	@Override
	public String getLocalizedIdentifierName(Level level, Identifier identifier) {
		Registry<Item> registry = this.getRegistry(level);
        if (registry.containsKey(identifier)) {
			Optional<Holder.Reference<Item>> itemOptionalReference = registry.get(identifier);
			if (itemOptionalReference.isPresent()) {
				ItemStack itemStack = new ItemStack(itemOptionalReference.get().value());
				return itemStack.getDisplayName().getString().replaceAll("[\\[\\]]", "");
			}
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

	public static Identifier staticGetIdentifierFromObject(Level level, Object object) {
		return level.registryAccess().lookupOrThrow(Registries.ITEM).getKey((Item)(object));
	}
}