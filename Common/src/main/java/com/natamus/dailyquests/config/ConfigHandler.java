package com.natamus.dailyquests.config;

import com.natamus.collective.config.DuskConfig;
import com.natamus.dailyquests.util.Reference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigHandler extends DuskConfig {
	public static HashMap<String, List<String>> configMetaData = new HashMap<String, List<String>>();

	@Entry(min = 0, max = 24000) public static int newQuestGenerateTimeInTicks = 1000;
	@Entry(min = 1, max = 5) public static int defaultTotalQuestCount = 3;
	@Entry(min = 0, max = 1000) public static int maximumQuestReRollsPerDay = 1;
	@Entry public static boolean allowReRollingCompletedQuests = false;

	@Entry public static boolean useQuestCompletionItemLootTables = true;
	@Entry public static boolean giveExperienceOnQuestCompletion = true;
	@Entry(min = 0, max = 100.0) public static double questCompletionExperienceModifier = 1.0;
	@Entry public static boolean sendSummaryToPlayerOnQuestCompletion = true;
	@Entry public static boolean broadcastMessageOnCompletion = true;
	@Entry(min = 0, max = 15) public static int broadcastMessageColourIndex = 6;
	@Entry public static boolean spawnFireworksOnQuestCompletion = true;

	@Entry public static boolean questListDrawTextShadow = true;
	@Entry(min = 0, max = 3000) public static int questListVerticalOffset = 5;
	@Entry(min = -3000, max = 3000) public static int questListHorizontalOffset = 0;
	@Entry public static boolean lowerQuestListWhenPlayerHasEffects = true;

	@Entry public static boolean enableBlockArrowQuests = true;
	@Entry public static boolean enableBreedMobQuests = true;
	@Entry public static boolean enableBrewPotionQuests = true;
	@Entry public static boolean enableCatchFishQuests = true;
	@Entry public static boolean enableCollectHoneyQuests = true;
	@Entry public static boolean enableCompleteRaidQuests = true;
	@Entry public static boolean enableCraftItemQuests = true;
	@Entry public static boolean enableEnchantItemQuests = true;
	@Entry public static boolean enableExploreBiomeQuests = true;
	@Entry public static boolean enableGainLevelQuests = true;
	@Entry public static boolean enableHarvestBlockQuests = true;
	@Entry public static boolean enableHarvestCropQuests = true;
	@Entry public static boolean enableIgniteCreeperQuests = true;
	@Entry public static boolean enableReflectGhastQuests = true;
	@Entry public static boolean enableShearSheepQuests = true;
	@Entry public static boolean enableSlayMobQuests = true;
	@Entry public static boolean enableSmeltItemQuests = true;
	@Entry public static boolean enableTameAnimalQuests = true;
	@Entry public static boolean enableTradeVillagerQuests = true;
	@Entry public static boolean enableTravelDistanceQuests = true;
	@Entry public static boolean enableUseItemQuests = true;

	public static void initConfig() {
		configMetaData.put("newQuestGenerateTimeInTicks", Arrays.asList(
			"At what time new quests should generate. Default is day, at 1000 ticks."
		));
		configMetaData.put("defaultTotalQuestCount", Arrays.asList(
			"How many daily quests should be generated for a player."
		));
		configMetaData.put("maximumQuestReRollsPerDay", Arrays.asList(
			"The maximum amount of quest re-rolls a player has each day. Resets during 'newQuestGenerateTimeInTicks'."
		));
		configMetaData.put("allowReRollingCompletedQuests", Arrays.asList(
			"If completed quests should be able to be re-rolled."
		));

		configMetaData.put("useQuestCompletionItemLootTables", Arrays.asList(
			"If the " + Reference.MOD_ID + " loot tables '1th_quest', '10th_quest', '50th_quest' and '100th_quest' should be used."
		));
		configMetaData.put("giveExperienceOnQuestCompletion", Arrays.asList(
			"Whether experience should be given to the player upon completing a quest, based on the task difficulty."
		));
		configMetaData.put("questCompletionExperienceModifier", Arrays.asList(
			"Can be used to decrease/increase the amount of experience given when completing quests."
		));
		configMetaData.put("sendSummaryToPlayerOnQuestCompletion", Arrays.asList(
			"If a summary should be sent to the player upon quest completion, containing information about the reward and how many quests completed."
		));
		configMetaData.put("broadcastMessageOnCompletion", Arrays.asList(
			"Whether a message should be broadcasted whenever a player completes a quest."
		));
		configMetaData.put("broadcastMessageColourIndex", Arrays.asList(
			"0: black, 1: dark_blue, 2: dark_green, 3: dark_aqua, 4: dark_red, 5: dark_purple, 6: gold, 7: gray, 8: dark_gray, 9: blue, 10: green, 11: aqua, 12: red, 13: light_purple, 14: yellow, 15: white"
		));
		configMetaData.put("spawnFireworksOnQuestCompletion", Arrays.asList(
			"Whether fireworks should be set off at a players location when a quest is completed."
		));

		configMetaData.put("moveQuestListToLeft", Arrays.asList(
			"Whether the quest list should be moved from the right side of the screen to the left."
		));
		configMetaData.put("questListDrawTextShadow", Arrays.asList(
			"If the quest list text displayed should have a shadow drawn below it."
		));
		configMetaData.put("questListVerticalOffset", Arrays.asList(
			"The vertical (y) offset of the quest list."
		));
		configMetaData.put("questListHorizontalOffset", Arrays.asList(
			"The horizontal (x) offset of the quest list."
		));
		configMetaData.put("lowerQuestListWhenPlayerHasEffects", Arrays.asList(
			"Whether the quest list in the GUI should be lowered when the player has potion effects to prevent overlap."
		));

		configMetaData.put("enableBlockArrowQuests", Arrays.asList(
			"Whether the quests with the type BlockArrow should be randomly chosen."
		));
		configMetaData.put("enableBreedMobQuests", Arrays.asList(
			"Whether the quests with the type BreedMob should be randomly chosen."
		));
		configMetaData.put("enableBrewPotionQuests", Arrays.asList(
			"Whether the quests with the type BrewPotion should be randomly chosen."
		));
		configMetaData.put("enableCatchFishQuests", Arrays.asList(
			"Whether the quests with the type CatchFish should be randomly chosen."
		));
		configMetaData.put("enableCollectHoneyQuests", Arrays.asList(
			"Whether the quests with the type CollectHoney should be randomly chosen."
		));
		configMetaData.put("enableCompleteRaidQuests", Arrays.asList(
			"Whether the quests with the type CompleteRaid should be randomly chosen."
		));
		configMetaData.put("enableCraftItemQuests", Arrays.asList(
			"Whether the quests with the type CraftItem should be randomly chosen."
		));
		configMetaData.put("enableEnchantItemQuests", Arrays.asList(
			"Whether the quests with the type EnchantItem should be randomly chosen."
		));
		configMetaData.put("enableExploreBiomeQuests", Arrays.asList(
			"Whether the quests with the type ExploreBiome should be randomly chosen."
		));
		configMetaData.put("enableGainLevelQuests", Arrays.asList(
			"Whether the quests with the type GainLevel should be randomly chosen."
		));
		configMetaData.put("enableHarvestBlockQuests", Arrays.asList(
			"Whether the quests with the type HarvestBlock should be randomly chosen."
		));
		configMetaData.put("enableHarvestCropQuests", Arrays.asList(
			"Whether the quests with the type HarvestCrop should be randomly chosen."
		));
		configMetaData.put("enableIgniteCreeperQuests", Arrays.asList(
			"Whether the quests with the type IgniteCreeper should be randomly chosen."
		));
		configMetaData.put("enableReflectGhastQuests", Arrays.asList(
			"Whether the quests with the type ReflectGhast should be randomly chosen."
		));
		configMetaData.put("enableShearSheepQuests", Arrays.asList(
			"Whether the quests with the type ShearSheep should be randomly chosen."
		));
		configMetaData.put("enableSlayMobQuests", Arrays.asList(
			"Whether the quests with the type SlayMob should be randomly chosen."
		));
		configMetaData.put("enableSmeltItemQuests", Arrays.asList(
			"Whether the quests with the type SmeltItem should be randomly chosen."
		));
		configMetaData.put("enableTameAnimalQuests", Arrays.asList(
			"Whether the quests with the type TameAnimal should be randomly chosen."
		));
		configMetaData.put("enableTradeVillagerQuests", Arrays.asList(
			"Whether the quests with the type TradeVillager should be randomly chosen."
		));
		configMetaData.put("enableTravelDistanceQuests", Arrays.asList(
			"Whether the quests with the type TravelDistance should be randomly chosen."
		));
		configMetaData.put("enableUseItemQuests", Arrays.asList(
			"Whether the quests with the type UseItem should be randomly chosen."
		));

		DuskConfig.init(Reference.NAME, Reference.MOD_ID, ConfigHandler.class);
	}
}