package com.natamus.dailyquests.data;

import com.natamus.dailyquests.quests.object.PlayerDataObject;
import com.natamus.dailyquests.quests.object.QuestObject;
import com.natamus.dailyquests.quests.types.main.AbstractQuest;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class Variables {
	public static HashMap<UUID, LinkedHashMap<AbstractQuest, QuestObject>> playerQuestDataMap = new HashMap<>();
	public static HashMap<UUID, PlayerDataObject> playerDataMap = new HashMap<>();
	public static HashMap<UUID, Vec3> lastPlayerLocation = new HashMap<>();

	public static boolean generatedIdentifierLists = false;
	public static List<ResourceLocation> breedableMobs = new ArrayList<>();
	public static List<ResourceLocation> craftableItems = new ArrayList<>();
	public static List<ResourceLocation> cropBlocks = new ArrayList<>();
	public static List<ResourceLocation> naturallyGeneratedOres = new ArrayList<>();
	public static HashMap<ResourceLocation, List<ResourceLocation>> smeltableItems = new HashMap<>();
	public static List<ResourceLocation> usableItems = new ArrayList<>();
}
