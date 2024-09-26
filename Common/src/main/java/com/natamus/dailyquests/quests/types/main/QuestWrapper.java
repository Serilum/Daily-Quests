package com.natamus.dailyquests.quests.types.main;

import com.natamus.dailyquests.quests.types.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class QuestWrapper<T extends AbstractQuest> {
	private final T questInstance;

	private static final List<QuestWrapper<?>> ALL_WRAPPED_QUESTS = new ArrayList<>();
	private static final List<AbstractQuest> ALL_QUESTS = new ArrayList<>();

	public static final QuestWrapper<BlockArrow> BLOCK_ARROW = new QuestWrapper<>(new BlockArrow());
	public static final QuestWrapper<BreedMob> BREED_MOB = new QuestWrapper<>(new BreedMob());
	public static final QuestWrapper<BrewPotion> BREW_POTION = new QuestWrapper<>(new BrewPotion());
	public static final QuestWrapper<CatchFish> CATCH_FISH = new QuestWrapper<>(new CatchFish());
	public static final QuestWrapper<CollectHoney> COLLECT_HONEY = new QuestWrapper<>(new CollectHoney());
	public static final QuestWrapper<CompleteRaid> COMPLETE_RAID = new QuestWrapper<>(new CompleteRaid());
	public static final QuestWrapper<CraftItem> CRAFT_ITEM = new QuestWrapper<>(new CraftItem());
	public static final QuestWrapper<EnchantItem> ENCHANT_ITEM = new QuestWrapper<>(new EnchantItem());
	public static final QuestWrapper<ExploreBiome> EXPLORE_BIOME = new QuestWrapper<>(new ExploreBiome());
	public static final QuestWrapper<GainLevel> GAIN_LEVEL = new QuestWrapper<>(new GainLevel());
	public static final QuestWrapper<HarvestBlock> HARVEST_BLOCK = new QuestWrapper<>(new HarvestBlock());
	public static final QuestWrapper<HarvestCrop> HARVEST_CROP = new QuestWrapper<>(new HarvestCrop());
	public static final QuestWrapper<IgniteCreeper> IGNITE_CREEPER = new QuestWrapper<>(new IgniteCreeper());
	public static final QuestWrapper<ReflectGhast> REFLECT_GHAST = new QuestWrapper<>(new ReflectGhast());
	public static final QuestWrapper<ShearSheep> SHEAR_SHEEP = new QuestWrapper<>(new ShearSheep());
	public static final QuestWrapper<SlayMob> SLAY_MOB = new QuestWrapper<>(new SlayMob());
	public static final QuestWrapper<SmeltItem> SMELT_ITEM = new QuestWrapper<>(new SmeltItem());
	public static final QuestWrapper<TameAnimal> TAME_ANIMAL = new QuestWrapper<>(new TameAnimal());
	public static final QuestWrapper<TradeVillager> TRADE_VILLAGER = new QuestWrapper<>(new TradeVillager());
	public static final QuestWrapper<TravelBlocks> TRAVEL_BLOCKS = new QuestWrapper<>(new TravelBlocks());
	public static final QuestWrapper<UseItem> USE_ITEM = new QuestWrapper<>(new UseItem());

	public QuestWrapper(T questInstance) {
		this.questInstance = questInstance;
		ALL_WRAPPED_QUESTS.add(this);
		ALL_QUESTS.add(this.questInstance);
	}

	public T getQuestInstance() {
		return questInstance;
	}

	public static List<AbstractQuest> getAllQuests() {
		return new ArrayList<>(ALL_QUESTS);
	}

	@Nullable
	public static AbstractQuest getQuestTypeFromName(String name) {
		for (AbstractQuest abstractQuest : getAllQuests()) {
			if (abstractQuest.getName().equals(name)) {
				return abstractQuest;
			}
		}
		return null;
	}

	@Nullable
	public static QuestWrapper<?> getWrappedQuestType(AbstractQuest abstractQuest) {
		for (QuestWrapper<?> wrappedQuestType : ALL_WRAPPED_QUESTS) {
			if (wrappedQuestType.getQuestInstance().equals(abstractQuest)) {
				return wrappedQuestType;
			}
		}
		return null;
	}
}