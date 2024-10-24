package com.natamus.dailyquests.quests.object;

import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.Constants;
import com.natamus.dailyquests.util.Reference;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerDataObject {
	private final UUID playerUUID;
	private int reRollsLeft;
	private int questsCompleted;
	private boolean showingIntroduction;

	public PlayerDataObject(UUID playerUUID, boolean showingIntroduction) {
		this.playerUUID = playerUUID;
		this.reRollsLeft = ConfigHandler.maximumQuestReRollsPerDay;
		this.questsCompleted = 0;
		this.showingIntroduction = showingIntroduction;
	}

	public PlayerDataObject(UUID playerUUID, int reRollsLeft, int questsCompleted, boolean showingIntroduction) {
		this.playerUUID = playerUUID;
		this.reRollsLeft = reRollsLeft;
		this.questsCompleted = questsCompleted;
		this.showingIntroduction = showingIntroduction;
	}

	public PlayerDataObject(UUID playerUUID, List<Integer> dataEntries) {
		this.playerUUID = playerUUID;
		this.reRollsLeft = dataEntries.get(0);
		this.questsCompleted = dataEntries.get(1);
		this.showingIntroduction = (dataEntries.size() > 2) && (dataEntries.get(2) == 1);
	}

	public PlayerDataObject(UUID playerUUID, String rawDataTag) {
		this.playerUUID = playerUUID;

		try {
			String[] rqSpl = rawDataTag.split(Constants.tagSubSplitDelimiter);

			this.reRollsLeft = Integer.parseInt(rqSpl[1]);
			this.questsCompleted = Integer.parseInt(rqSpl[2]);

			this.showingIntroduction = (rqSpl.length > 3) && (Integer.parseInt(rqSpl[3]) == 1);
		}
		catch (Exception ex) {
			Constants.logger.warn("[" + Reference.NAME + "] Unable to parse player data from raw data tag: " + rawDataTag);
			ex.printStackTrace();
		}
	}

	public String getRawDataTag() {
		return "D" + Constants.tagSubDelimiter + this.reRollsLeft + Constants.tagSubDelimiter + this.questsCompleted + Constants.tagSubDelimiter + (this.showingIntroduction ? 1 : 0);
	}

	public List<Integer> getDataEntries() {
		return Arrays.asList(this.reRollsLeft, this.questsCompleted, this.showingIntroduction ? 1 : 0);
	}

	public int getReRollsLeft() {
		return this.reRollsLeft;
	}

	public void decrementReRolls() {
		this.reRollsLeft -= 1;
	}

	public void resetReRolls() {
		this.reRollsLeft = ConfigHandler.maximumQuestReRollsPerDay;
	}

	public int getQuestsCompleted() {
		return this.questsCompleted;
	}

	public void incrementQuestsCompleted() {
		this.questsCompleted += 1;
	}

	public void setQuestsCompleted(int amount) {
		this.questsCompleted = amount;
	}

	public boolean isShowingIntroduction() {
		return this.showingIntroduction;
	}

	public void setShowingIntroduction(boolean showingIntroduction) {
		this.showingIntroduction = showingIntroduction;
	}
}
