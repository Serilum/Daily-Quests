package com.serilum.dailyquests.data;

import com.serilum.dailyquests.quests.object.PlayerDataObject;
import com.serilum.dailyquests.quests.object.QuestObject;
import com.serilum.dailyquests.quests.types.main.AbstractQuest;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class Variables {
	public static HashMap<UUID, LinkedHashMap<AbstractQuest, QuestObject>> playerQuestDataMap = new HashMap<>();
	public static HashMap<UUID, PlayerDataObject> playerDataMap = new HashMap<>();
	public static HashMap<UUID, Vec3> lastPlayerLocation = new HashMap<>();

	public static boolean generatedIdentifierLists = false;
	public static List<Identifier> breedableMobs = new ArrayList<>();
	public static List<Identifier> craftableItems = new ArrayList<>();
	public static List<Identifier> cropBlocks = new ArrayList<>();
	public static List<Identifier> naturallyGeneratedOres = new ArrayList<>();
	public static HashMap<Identifier, List<Identifier>> smeltableItems = new HashMap<>();
	public static List<Identifier> usableItems = new ArrayList<>();
}
