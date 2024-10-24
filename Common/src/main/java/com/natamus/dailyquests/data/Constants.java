package com.natamus.dailyquests.data;

import com.mojang.logging.LogUtils;
import com.natamus.dailyquests.util.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootTable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Constants {
	public static final Logger logger = LogUtils.getLogger();
	public static final Random random = new Random();
	public static final RandomSource randomSource = RandomSource.create();

	public static final TagKey<EntityType<?>> BOSSES = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("c", "bosses"));

	public static final String tagPrefix = Reference.NAME.replace(" ", "");
	public static final String tagMainDelimiter = "--";
	public static final String tagSubDelimiter = "..";
	public static final String tagSubSplitDelimiter = "\\.\\.";

	public static List<String> commandPrefixes = new ArrayList<String>(Arrays.asList("dailyquests", "dq"));

	public static final ResourceLocation defaultResourceLocation = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "none");

	public static final ResourceKey<LootTable> LOOT_TABLE_1NTH_QUEST = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "1nth_quest"));
	public static final ResourceKey<LootTable> LOOT_TABLE_10NTH_QUEST = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "10nth_quest"));
	public static final ResourceKey<LootTable> LOOT_TABLE_50NTH_QUEST = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "50nth_quest"));
	public static final ResourceKey<LootTable> LOOT_TABLE_100NTH_QUEST = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "100nth_quest"));
}
