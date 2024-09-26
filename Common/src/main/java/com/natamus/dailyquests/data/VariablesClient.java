package com.natamus.dailyquests.data;

import com.mojang.datafixers.util.Pair;
import com.natamus.dailyquests.quests.object.PlayerDataObject;
import net.minecraft.client.gui.components.Button;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class VariablesClient {
	public static PlayerDataObject playerDataObject;
	public static List<String> questTitles = new ArrayList<>();
	public static List<String> questDescriptions = new ArrayList<>();
	public static List<Pair<Integer, Integer>> questProgress = new ArrayList<>();

	public static boolean questListCollapsed = false; // TODO: config

	public static LinkedHashMap<Integer, Button> reRollButtons = new LinkedHashMap<>();
	public static boolean addedRerollButtons = false;
	public static boolean waitingForNewQuest = false;
}
