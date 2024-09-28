package com.natamus.dailyquests.util;

import com.mojang.datafixers.util.Pair;
import com.natamus.collective.implementations.networking.api.Dispatcher;
import com.natamus.dailyquests.data.Constants;
import com.natamus.dailyquests.data.Variables;
import com.natamus.dailyquests.networking.packets.ToClientSendQuestsPacket;
import com.natamus.dailyquests.quests.object.PlayerDataObject;
import com.natamus.dailyquests.quests.object.QuestObject;
import com.natamus.dailyquests.quests.types.main.AbstractQuest;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import oshi.util.tuples.Triplet;

import java.util.*;
import java.util.stream.Collectors;

public class Util {
	public static void generateIdentifierLists(Level level) {
		RegistryAccess registryAccess = level.registryAccess();

		RecipeManager recipeManager = level.getRecipeManager();

		Registry<Item> itemRegistry = registryAccess.registryOrThrow(Registries.ITEM);
		Registry<Block> blockRegistry = registryAccess.registryOrThrow(Registries.BLOCK);
		Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
		Registry<EntityType<?>> entityTypeRegistry = registryAccess.registryOrThrow(Registries.ENTITY_TYPE);
		Registry<Potion> potionRegistry = registryAccess.registryOrThrow(Registries.POTION);


		for (RecipeHolder<CraftingRecipe> craftingRecipeHolder : recipeManager.getAllRecipesFor(RecipeType.CRAFTING)) {
			ResourceLocation identifier = itemRegistry.getKey(craftingRecipeHolder.value().getResultItem(level.registryAccess()).getItem());
			if (!Variables.craftableItems.contains(identifier)) {
				Variables.craftableItems.add(identifier);
			}
		}


		for (RecipeHolder<SmeltingRecipe> smeltingRecipeHolder : recipeManager.getAllRecipesFor(RecipeType.SMELTING)) {
			SmeltingRecipe smeltingRecipe = smeltingRecipeHolder.value();
			ItemStack resultStack = smeltingRecipe.getResultItem(registryAccess);
			ResourceLocation resultIdentifier = itemRegistry.getKey(resultStack.getItem());

			for (Ingredient ingredient : smeltingRecipe.getIngredients()) {
				for (ItemStack itemStack : ingredient.getItems()) {
					ResourceLocation ingredientIdentifier = itemRegistry.getKey(itemStack.getItem());
					if (ingredientIdentifier.toString().contains("terracotta")) {
						continue;
					}

					if (!Variables.smeltableItems.containsKey(resultIdentifier)) {
						Variables.smeltableItems.put(resultIdentifier, new ArrayList<ResourceLocation>());
					}

					Variables.smeltableItems.get(resultIdentifier).add(ingredientIdentifier);
				}
			}
		}


		for (Biome biome : biomeRegistry.stream().toList()) {
			BiomeGenerationSettings biomeGenerationSettings =  biome.getGenerationSettings();
			for (HolderSet<PlacedFeature> placedFeatureHolderSet : biomeGenerationSettings.features()) {
				for (Holder<PlacedFeature> placedFeatureHolder : placedFeatureHolderSet.stream().toList()) {
					PlacedFeature placedFeature = placedFeatureHolder.value();
					for (ConfiguredFeature<?, ?> configuredFeature : placedFeature.getFeatures().toList()) {
						FeatureConfiguration config = configuredFeature.config();
						if (config instanceof OreConfiguration) {
							OreConfiguration oreConfig = (OreConfiguration) config;
							for (OreConfiguration.TargetBlockState targetBlockState: oreConfig.targetStates) {
								ResourceLocation targetBlockResourceLocation = blockRegistry.getKey(targetBlockState.state.getBlock());
								if (targetBlockResourceLocation.toString().contains("infested_")) {
									continue;
								}

								if (!Variables.naturallyGeneratedOres.contains(targetBlockResourceLocation)) {
									Variables.naturallyGeneratedOres.add(targetBlockResourceLocation);
								}
							}
						}
					}
				}
			}
		}


		for (Item item : itemRegistry.stream().toList()) {
			try {
				int useDuration = item.getUseDuration(new ItemStack(item), null);
				if (useDuration > 0) {
					Variables.usableItems.add(itemRegistry.getKey(item));
				}
			}
			catch (NullPointerException ignored) { }
		}


		for (Block block : blockRegistry.stream().toList()) {
			if (block instanceof CropBlock) {
				Variables.cropBlocks.add(blockRegistry.getKey(block));
			}
		}


		for (EntityType<?> entityType : entityTypeRegistry) {
			Entity entity = entityType.create(level);
			if (entity instanceof Animal) {
				Variables.breedableMobs.add(entityTypeRegistry.getKey(entityType));
			}
		}


		Variables.generatedIdentifierLists = true;
	}

	public static void saveQuestDataPlayer(Player player) {
		saveQuestDataPlayer(player, Variables.playerQuestDataMap.get(player.getUUID()));
	}
	public static void saveQuestDataPlayer(Player player, LinkedHashMap<AbstractQuest, QuestObject> quests) {
		UUID playerUUID = player.getUUID();
		if (!Variables.playerDataMap.containsKey(playerUUID)) {
			Variables.playerDataMap.put(playerUUID, new PlayerDataObject(playerUUID));
		}

		player.getTags().removeIf(currentTag -> currentTag.startsWith(Constants.tagPrefix));

		StringBuilder tag = new StringBuilder(Constants.tagPrefix);

		tag.append(Constants.tagMainDelimiter).append(Variables.playerDataMap.get(playerUUID).getRawDataTag());

		for (QuestObject quest : quests.values()) {
			tag.append(Constants.tagMainDelimiter).append(quest.getRawDataTag());
		}

		player.addTag(tag.toString().replace(":", "+"));
	}

	public static void loadQuestDataPlayer(ServerPlayer serverPlayer) {
		Pair<PlayerDataObject, LinkedHashMap<AbstractQuest, QuestObject>> questData;

		UUID playerUUID = serverPlayer.getUUID();
		if (Variables.playerDataMap.containsKey(playerUUID) && Variables.playerQuestDataMap.containsKey(playerUUID)) {
			questData = Pair.of(Variables.playerDataMap.get(playerUUID), Variables.playerQuestDataMap.get(playerUUID));
		}
		else {
			questData = getQuestDataOfPlayer(serverPlayer);
			if (questData.getFirst() == null || questData.getSecond().isEmpty()) {
				if (hasQuestData(serverPlayer)) {
					Constants.logger.warn("[" + Reference.NAME + "] Unable to load quest data for player " + serverPlayer.getName().getString() + ". (loadQuestDataPlayer)");
				}

				return;
			}

			Variables.playerDataMap.put(playerUUID, questData.getFirst());
			Variables.playerQuestDataMap.put(playerUUID, questData.getSecond());

			Util.saveQuestDataPlayer(serverPlayer);
		}

		Util.sendQuestDataToClient(serverPlayer, questData);
	}

	public static Pair<PlayerDataObject, LinkedHashMap<AbstractQuest, QuestObject>> getQuestDataOfPlayer(Player player) {
		PlayerDataObject playerDataObject = null;
		LinkedHashMap<AbstractQuest, QuestObject> quests = new LinkedHashMap<>();

		UUID playerUUID = player.getUUID();

		for (String tag : player.getTags()) {
			if (tag.startsWith(Constants.tagPrefix)) {
				for (String rawDataTag : tag.split(Constants.tagMainDelimiter)) {
					if (rawDataTag.equals(Constants.tagPrefix)) {
						continue;
					}

					if (rawDataTag.startsWith("Q" + Constants.tagSubDelimiter)) {
						QuestObject quest = new QuestObject(rawDataTag);
						if (!quest.wasCorrectlyCreated()) {
							continue;
						}

						quests.put(quest.getType(), quest);
					}
					else if (rawDataTag.startsWith("D" + Constants.tagSubDelimiter)) {
						playerDataObject = new PlayerDataObject(player.getUUID(), rawDataTag);
					}
				}
			}
		}

		if (playerDataObject == null) {
			playerDataObject = new PlayerDataObject(playerUUID);
		}

		return Pair.of(playerDataObject, quests);
	}

	public static void sendQuestDataToClient(ServerPlayer serverPlayer) {
		UUID playerUUID = serverPlayer.getUUID();
		sendQuestDataToClient(serverPlayer, Pair.of(Variables.playerDataMap.get(playerUUID), Variables.playerQuestDataMap.get(playerUUID)));
	}
	public static void sendQuestDataToClient(ServerPlayer serverPlayer, Pair<PlayerDataObject, LinkedHashMap<AbstractQuest, QuestObject>> questData) {
		ServerLevel serverLevel = serverPlayer.serverLevel();

		List<Integer> dataEntries = questData.getFirst().getDataEntries();
		List<String> questTitles = new ArrayList<>();
		List<String> questDescriptions = new ArrayList<>();
		List<Pair<Integer, Integer>> questProgress = new ArrayList<>();

		for (QuestObject quest : questData.getSecond().values()) {
			Triplet<String, String, Pair<Integer, Integer>> clientQuestData = quest.getClientQuestData(serverLevel);
			if (clientQuestData == null) {
				continue;
			}

			questTitles.add(clientQuestData.getA());
			questDescriptions.add(clientQuestData.getB());
			questProgress.add(clientQuestData.getC());
		}

		Dispatcher.sendToClient(new ToClientSendQuestsPacket(dataEntries, questTitles, questDescriptions, questProgress), serverPlayer);
	}

	public static boolean hasQuestData(Player player) {
		for (String tag : player.getTags()) {
			if (tag.startsWith(Constants.tagPrefix)) {
				return true;
			}
		}
		return false;
	}

    public static List<ItemStack> mergeItemStacks(List<ItemStack> itemStacks) {
        Map<Item, Integer> itemCountMap = new HashMap<>();

        for (ItemStack stack : itemStacks) {
            Item item = stack.getItem();
            int count = stack.getCount();

            itemCountMap.put(item, itemCountMap.getOrDefault(item, 0) + count);
        }

        List<ItemStack> mergedStacks = new ArrayList<>();

        for (Map.Entry<Item, Integer> entry : itemCountMap.entrySet()) {
            Item item = entry.getKey();
            int totalCount = entry.getValue();
            int maxStackSize = item.getDefaultMaxStackSize();

            while (totalCount > 0) {
                int stackSize = Math.min(totalCount, maxStackSize);
                mergedStacks.add(new ItemStack(item, stackSize));
                totalCount -= stackSize;
            }
        }

        return mergedStacks;
    }

    public static String formatItemStacks(List<ItemStack> itemStacks) {
        return itemStacks.stream()
            .filter(stack -> !stack.isEmpty())
            .map(stack -> formatItemStack(stack))
            .collect(Collectors.joining(", "));
    }

    private static String formatItemStack(ItemStack stack) {
        String itemName = stack.getItem().getName(stack).getString();
        int count = stack.getCount();
        return itemName + " " + count + "×";
    }
}
