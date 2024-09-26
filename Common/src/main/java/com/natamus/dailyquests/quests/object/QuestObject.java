package com.natamus.dailyquests.quests.object;

import com.mojang.datafixers.util.Pair;
import com.natamus.dailyquests.data.Constants;
import com.natamus.dailyquests.quests.types.main.AbstractQuest;
import com.natamus.dailyquests.quests.types.main.QuestWrapper;
import com.natamus.dailyquests.util.Reference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import oshi.util.tuples.Triplet;

import javax.annotation.Nullable;

public class QuestObject {
	private AbstractQuest type;
	private ResourceLocation identifier;
	private int currentProgress;
	private int goalProgress;
	private boolean isCreated = false;

	public QuestObject(Object typeInput, ResourceLocation identifier, int currentProgress, int goalProgress) {
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
			this.identifier = ResourceLocation.parse(rqSpl[2]);
			this.currentProgress = Integer.parseInt(prSpl[0]);
			this.goalProgress = Integer.parseInt(prSpl[1]);
			this.isCreated = true;
		}
		catch (Exception ex) {
			Constants.logger.warn("[" + Reference.NAME + "] Unable to parse quest from raw data tag: " + rawDataTag);
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
		return new Triplet<>(questTitle, questDescription, new Pair<Integer, Integer>(this.currentProgress, this.goalProgress));
	}

	public String getQuestTitle(Level level) {
		if (this.type.equals(QuestWrapper.SMELT_ITEM.getQuestInstance()) || this.type.equals(QuestWrapper.USE_ITEM.getQuestInstance())) {
			boolean isFood = new ItemStack(level.registryAccess().registryOrThrow(Registries.ITEM).get(this.identifier)).getComponents().has(DataComponents.FOOD);
			if (isFood) {
				if (this.type.equals(QuestWrapper.SMELT_ITEM.getQuestInstance())) {
					return "Cook Item";
				}
				else if (this.type.equals(QuestWrapper.USE_ITEM.getQuestInstance())) {
					return "Consume Item";
				}
			}
		}

		return this.type.getName().replaceAll("([a-z])([A-Z])", "$1 $2");
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

	public ResourceLocation getIdentifier() {
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
