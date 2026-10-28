package com.serilum.dailyquests.quests.object;

import com.mojang.datafixers.util.Pair;
import com.serilum.dailyquests.data.Constants;
import com.serilum.dailyquests.quests.types.main.AbstractQuest;
import com.serilum.dailyquests.quests.types.main.QuestWrapper;
import com.serilum.dailyquests.util.Reference;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import oshi.util.tuples.Triplet;

import org.jetbrains.annotations.Nullable;
import java.util.Optional;

public class QuestObject {
	private AbstractQuest type;
	private Identifier identifier;
	private int currentProgress;
	private int goalProgress;
	private boolean isCreated = false;

	public QuestObject(Object typeInput, Identifier identifier, int currentProgress, int goalProgress) {
		this.type = (typeInput instanceof String) ? QuestWrapper.getQuestTypeFromName((String)typeInput) : (AbstractQuest)typeInput;
		this.identifier = identifier;
		this.currentProgress = currentProgress;
		this.goalProgress = goalProgress;
		this.isCreated = true;
	}

	public QuestObject(String rawDataTag) {
		try {
			String[] rqSpl = rawDataTag.replace("+", ":").split(Constants.tagSubSplitDelimiter);
			String[] prSpl = rqSpl[3].split("o");

			this.type = QuestWrapper.getQuestTypeFromName(rqSpl[1]);
			this.identifier = Identifier.parse(rqSpl[2]);
			this.currentProgress = Integer.parseInt(prSpl[0]);
			this.goalProgress = Integer.parseInt(prSpl[1]);
			this.isCreated = true;
		}
		catch (Exception ex) {
			Constants.logger.warn("[" + Reference.NAME + "] Unable to parse quest from raw data tag: {}", rawDataTag);
			ex.printStackTrace();
		}
	}

	public String getRawDataTag() {
		return "Q" + Constants.tagSubDelimiter + this.type.getName() + Constants.tagSubDelimiter + this.identifier + Constants.tagSubDelimiter + this.currentProgress + "o" + this.goalProgress;
	}

	@Nullable public Triplet<String, String, Pair<Integer, Integer>> getClientQuestData(Level level) {
		if (this.type == null) {
			return null;
		}

		String questTitle = getQuestTitle(level);
		String questDescription = getQuestDescription(level);
		return new Triplet<>(questTitle, questDescription, new Pair<>(this.currentProgress, this.goalProgress));
	}

	public String getQuestTitle(Level level) {
		if (this.type.equals(QuestWrapper.SMELT_ITEM.getQuestInstance()) || this.type.equals(QuestWrapper.USE_ITEM.getQuestInstance())) {
			Optional<Holder.Reference<Item>> itemOptionalReference = level.registryAccess().lookupOrThrow(Registries.ITEM).get(this.identifier);

			if (itemOptionalReference.isPresent()) {
				boolean isFood = new ItemStack(itemOptionalReference.get().value()).getComponents().has(DataComponents.FOOD);
				if (isFood) {
					if (this.type.equals(QuestWrapper.SMELT_ITEM.getQuestInstance())) {
						return "Cook Item";
					} else if (this.type.equals(QuestWrapper.USE_ITEM.getQuestInstance())) {
						return "Consume Item";
					}
				}
			}
		}

		return this.type.getName().replaceAll("([a-z])([A-Z])", "$1 $2");
	}

	public String getQuestTitleTranslationKey(Level level) {
		return switch (this.getQuestTitle(level)) {
			case "Block Arrow" -> "collective.dailyquests.questtitle.blockarrow";
			case "Breed Mob" -> "collective.dailyquests.questtitle.breedmob";
			case "Brew Potion" -> "collective.dailyquests.questtitle.brewpotion";
			case "Catch Fish" -> "collective.dailyquests.questtitle.catchfish";
			case "Collect Honey" -> "collective.dailyquests.questtitle.collecthoney";
			case "Complete Raid" -> "collective.dailyquests.questtitle.completeraid";
			case "Consume Item" -> "collective.dailyquests.questtitle.consumeitem";
			case "Cook Item" -> "collective.dailyquests.questtitle.cookitem";
			case "Craft Item" -> "collective.dailyquests.questtitle.craftitem";
			case "Enchant Item" -> "collective.dailyquests.questtitle.enchantitem";
			case "Explore Biome" -> "collective.dailyquests.questtitle.explorebiome";
			case "Gain Level" -> "collective.dailyquests.questtitle.gainlevel";
			case "Harvest Block" -> "collective.dailyquests.questtitle.harvestblock";
			case "Harvest Crop" -> "collective.dailyquests.questtitle.harvestcrop";
			case "Ignite Creeper" -> "collective.dailyquests.questtitle.ignitecreeper";
			case "Reflect Ghast" -> "collective.dailyquests.questtitle.reflectghast";
			case "Shear Sheep" -> "collective.dailyquests.questtitle.shearsheep";
			case "Slay Mob" -> "collective.dailyquests.questtitle.slaymob";
			case "Smelt Item" -> "collective.dailyquests.questtitle.smeltitem";
			case "Tame Animal" -> "collective.dailyquests.questtitle.tameanimal";
			case "Trade Villager" -> "collective.dailyquests.questtitle.tradevillager";
			case "Travel Blocks" -> "collective.dailyquests.questtitle.travelblocks";
			case "Use Item" -> "collective.dailyquests.questtitle.useitem";
			default -> "";
		};
	}

	public String getQuestDescription(Level level) {
		return this.type.getLocalizedIdentifierName(level, this.identifier);
	}

	public MutableComponent getBroadcastContent(Level level) {
		String goalString = "";
		if (this.goalProgress > 1) {
			goalString = " " + this.goalProgress + "×";
		}

		String questDescription = this.getQuestDescription(level);
		if (!questDescription.isBlank()) {
			questDescription = " " + questDescription;
		}

		return Component.literal(this.getQuestTitle(level) + ":" + goalString + questDescription);
	}

	public void setProgress(int currentProgress) {
		this.currentProgress = currentProgress;
	}

	public boolean incrementCompletionCheck(int count) {
		this.currentProgress += count;
		if (this.currentProgress > this.goalProgress) {
			this.currentProgress = this.goalProgress;
		}

		return this.currentProgress == this.goalProgress;
	}

	public boolean isCompleted() {
		return this.currentProgress == this.goalProgress;
	}

	public AbstractQuest getType() {
		return this.type;
	}

	public Identifier getIdentifier() {
		return this.identifier;
	}

	public int getCurrentProgress() {
		return this.currentProgress;
	}

	public int getGoalProgress() {
		return this.goalProgress;
	}

	public boolean wasCorrectlyCreated() {
		return this.isCreated;
	}
}
