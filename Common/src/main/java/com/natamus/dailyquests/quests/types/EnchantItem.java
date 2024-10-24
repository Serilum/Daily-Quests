package com.natamus.dailyquests.quests.types;

import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.Constants;
import com.natamus.dailyquests.quests.object.QuestObject;
import com.natamus.dailyquests.quests.types.main.AbstractQuest;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnchantItem extends AbstractQuest {
	private final String name;

	public EnchantItem() {
		this.name = this.getClass().getSimpleName();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isEnabled() {
		return ConfigHandler.enableEnchantItemQuests;
	}

	@Override
	public Registry<Item> getRegistry(Level level) {
		return level.registryAccess().lookupOrThrow(this.getRegistryResourceKey());
	}

	@Override
	public boolean isAllowedIdentifier(Level level, ResourceLocation identifier) {
		Registry<Item> itemRegistry = this.getRegistry(level);

		Optional<Holder.Reference<Item>> itemOptionalReference = itemRegistry.get(identifier);
		if (itemOptionalReference.isEmpty()) {
			return false;
		}

		ItemStack itemStack = new ItemStack(itemOptionalReference.get().value());

		Optional<HolderSet.Named<Enchantment>> namedOptional = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.IN_ENCHANTING_TABLE);
		return namedOptional.filter(holders -> EnchantmentHelper.selectEnchantment(Constants.randomSource, itemStack, 0, (holders).stream()).size() > 0).isPresent();
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
		return 1;
	}

	@Override
	public ResourceLocation getResourceLocationFromObject(Level level, Object object) {
		return this.getRegistry(level).getKey(this.objectCast(object));
	}

	@Override
	public String getLocalizedIdentifierName(Level level, ResourceLocation identifier) {
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
		return 100;
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
		return level.registryAccess().lookupOrThrow(Registries.ITEM).getKey((Item)(object));
	}
}