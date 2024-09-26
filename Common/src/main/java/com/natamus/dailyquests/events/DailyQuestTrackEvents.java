package com.natamus.dailyquests.events;

import com.natamus.collective.functions.TaskFunctions;
import com.natamus.dailyquests.data.Variables;
import com.natamus.dailyquests.quests.functions.CompleteQuests;
import com.natamus.dailyquests.quests.types.*;
import com.natamus.dailyquests.quests.types.main.QuestWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class DailyQuestTrackEvents {
	public static void onPlayerTick(Level level, Player player) {
		if (player.tickCount % 20 != 0) {
			return;
		}

		if (CompleteQuests.hasQuestType(QuestWrapper.EXPLORE_BIOME, player)) {
			CompleteQuests.updateQuestProgression(QuestWrapper.EXPLORE_BIOME, level, (ServerPlayer)player, ExploreBiome.staticGetResourceLocationFromObject(level, level.getBiome(player.blockPosition()).value()), 1);
		}

		if (CompleteQuests.hasQuestType(QuestWrapper.TRAVEL_BLOCKS, player)) {
			Vec3 playerPosition = player.position();
			UUID playerUUID = player.getUUID();

			if (Variables.lastPlayerLocation.containsKey(playerUUID)) {
				Vec3 lastPlayerPosition = Variables.lastPlayerLocation.get(playerUUID);

				int distance = (int)Math.floor(lastPlayerPosition.distanceTo(playerPosition));

				if (distance > 0) {
					CompleteQuests.updateQuestProgression(QuestWrapper.TRAVEL_BLOCKS, level, (ServerPlayer) player, TravelBlocks.staticGetResourceLocationFromObject(level, null), distance);
				}
			}

			Variables.lastPlayerLocation.put(playerUUID, playerPosition);
		}
	}

	public static void onBlockBreak(Level level, Player player, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
		if (level.isClientSide) {
			return;
		}

		if (!CompleteQuests.hasQuestType(QuestWrapper.HARVEST_BLOCK, player) && !CompleteQuests.hasQuestType(QuestWrapper.HARVEST_CROP, player)) {
			return;
		}

		ResourceLocation blockIdentifier = level.registryAccess().registryOrThrow(Registries.BLOCK).getKey(blockState.getBlock());
		if (CompleteQuests.hasQuestType(QuestWrapper.HARVEST_BLOCK, player)) {
			CompleteQuests.updateQuestProgression(QuestWrapper.HARVEST_BLOCK, level, (ServerPlayer)player, blockIdentifier, 1);
		}

		if (CompleteQuests.hasQuestType(QuestWrapper.HARVEST_CROP, player)) {
			CompleteQuests.updateQuestProgression(QuestWrapper.HARVEST_CROP, level, (ServerPlayer)player, blockIdentifier, 1);
		}
	}

	public static void onLivingDeath(Level level, LivingEntity livingEntity, DamageSource damageSource) {
		if (level.isClientSide) {
			return;
		}

		Entity sourceEntity = damageSource.getDirectEntity();
		if (!(sourceEntity instanceof ServerPlayer serverPlayer)) {
			return;
		}

		if (!CompleteQuests.hasQuestType(QuestWrapper.SLAY_MOB, serverPlayer)) {
			return;
		}

		CompleteQuests.updateQuestProgression(QuestWrapper.SLAY_MOB, level, serverPlayer, SlayMob.staticGetResourceLocationFromObject(level, livingEntity.getType()), 1);
	}

	public static void onSheepShear(Level level, Player player, Sheep sheep) {
		if (level.isClientSide) {
			return;
		}

		if (!CompleteQuests.hasQuestType(QuestWrapper.SHEAR_SHEEP, player)) {
			return;
		}

		CompleteQuests.updateQuestProgression(QuestWrapper.SHEAR_SHEEP, level, (ServerPlayer)player, ShearSheep.staticGetResourceLocationFromObject(level, sheep.getType()), 1);
	}

	public static void onBreeding(Level level, @Nullable Player causedByPlayerIn, Mob parentA, Mob parentB, AgeableMob offspring) {
		if (level.isClientSide) {
			return;
		}

		TaskFunctions.enqueueCollectiveTask(level.getServer(), () -> {
			Player causedByPlayer = causedByPlayerIn;
			if (causedByPlayer == null) {
				causedByPlayer = level.getNearestPlayer(offspring, 10.0);
				if (causedByPlayer == null) {
					return;
				}
			}

			if (!CompleteQuests.hasQuestType(QuestWrapper.BREED_MOB, causedByPlayer)) {
				return;
			}

			CompleteQuests.updateQuestProgression(QuestWrapper.BREED_MOB, level, (ServerPlayer)causedByPlayer, BreedMob.staticGetResourceLocationFromObject(level, offspring.getType()), 1);
		}, 0);
	}

	public static void onItemCraft(Level level, Player player, ItemStack itemStack) {
		if (level.isClientSide) {
			return;
		}

		if (!CompleteQuests.hasQuestType(QuestWrapper.CRAFT_ITEM, player)) {
			return;
		}

		int count = itemStack.getCount();
		if (count == 0) {
			return;
		}

		CompleteQuests.updateQuestProgression(QuestWrapper.CRAFT_ITEM, level, (ServerPlayer)player, CraftItem.staticGetResourceLocationFromObject(level, itemStack.getItem()), count);
	}

	public static void onItemUseFinished(Level level, Player player, ItemStack usedItem) {
		if (level.isClientSide) {
			return;
		}

		if (!CompleteQuests.hasQuestType(QuestWrapper.USE_ITEM, player)) {
			return;
		}

		CompleteQuests.updateQuestProgression(QuestWrapper.USE_ITEM, level, (ServerPlayer)player, UseItem.staticGetResourceLocationFromObject(level, usedItem.getItem()), 1);
	}

	public static void onBrewPotion(Level level, Player player, ItemStack itemStack) {
		if (level.isClientSide) {
			return;
		}

		if (!CompleteQuests.hasQuestType(QuestWrapper.BREW_POTION, player)) {
			return;
		}

		Optional<Holder<Potion>> optionalPotionHolder = (itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).potion();
		optionalPotionHolder.ifPresent(potionHolder -> CompleteQuests.updateQuestProgression(QuestWrapper.BREW_POTION, level, (ServerPlayer)player, BrewPotion.staticGetResourceLocationFromObject(level, potionHolder.value()), 1));
	}

	public static void onEnchantItem(Level level, Player player, ItemStack itemStack) {
		if (level.isClientSide) {
			return;
		}

		if (!CompleteQuests.hasQuestType(QuestWrapper.ENCHANT_ITEM, player)) {
			return;
		}

		CompleteQuests.updateQuestProgression(QuestWrapper.ENCHANT_ITEM, level, (ServerPlayer)player, EnchantItem.staticGetResourceLocationFromObject(level, itemStack.getItem()), 1);
	}

	public static void onGainExperienceLevel(Level level, Player player, int count) {
		if (level.isClientSide) {
			return;
		}

		if (!CompleteQuests.hasQuestType(QuestWrapper.GAIN_LEVEL, player)) {
			return;
		}

		CompleteQuests.updateQuestProgression(QuestWrapper.GAIN_LEVEL, level, (ServerPlayer)player, GainLevel.staticGetResourceLocationFromObject(level, null), count);
	}

	public static void onSmeltItem(Level level, Player player, ItemStack itemStack) {
		if (level.isClientSide) {
			return;
		}

		if (!CompleteQuests.hasQuestType(QuestWrapper.SMELT_ITEM, player)) {
			return;
		}

		int count = itemStack.getCount();
		if (count == 0) {
			return;
		}

		ResourceLocation resultIdentifier = level.registryAccess().registryOrThrow(Registries.ITEM).getKey(itemStack.getItem());
		if (resultIdentifier == null) {
			return;
		}

		for (ResourceLocation ingredientIdentifier : Variables.smeltableItems.get(resultIdentifier)) {
			CompleteQuests.updateQuestProgression(QuestWrapper.SMELT_ITEM, level, (ServerPlayer)player, ingredientIdentifier, count);
		}
	}

	public static void onTameAnimal(Level level, Player player, TamableAnimal tamableAnimal) {
		if (level.isClientSide) {
			return;
		}

		if (!CompleteQuests.hasQuestType(QuestWrapper.TAME_ANIMAL, player)) {
			return;
		}

		CompleteQuests.updateQuestProgression(QuestWrapper.TAME_ANIMAL, level, (ServerPlayer)player, TameAnimal.staticGetResourceLocationFromObject(level, tamableAnimal.getType()), 1);
	}

	public static void onVillagerTrade(Level level, Player player, Villager villager) {
		if (level.isClientSide) {
			return;
		}

		if (!CompleteQuests.hasQuestType(QuestWrapper.TRADE_VILLAGER, player)) {
			return;
		}

		CompleteQuests.updateQuestProgression(QuestWrapper.TRADE_VILLAGER, level, (ServerPlayer)player, TradeVillager.staticGetResourceLocationFromObject(level, villager.getVillagerData().getProfession()), 1);
	}

	public static void onCreeperIgnite(Level level, Player player, Creeper creeper) {
		if (level.isClientSide) {
			return;
		}

		if (!CompleteQuests.hasQuestType(QuestWrapper.IGNITE_CREEPER, player)) {
			return;
		}

		CompleteQuests.updateQuestProgression(QuestWrapper.IGNITE_CREEPER, level, (ServerPlayer)player, IgniteCreeper.staticGetResourceLocationFromObject(level, null), 1);
	}

	public static void onArrowBlock(Level level, Player player) {
		if (level.isClientSide) {
			return;
		}

		if (!CompleteQuests.hasQuestType(QuestWrapper.BLOCK_ARROW, player)) {
			return;
		}

		CompleteQuests.updateQuestProgression(QuestWrapper.BLOCK_ARROW, level, (ServerPlayer)player, BlockArrow.staticGetResourceLocationFromObject(level, null), 1);
	}

	public static void onGhastReflect(Level level, Player player, Ghast ghast) {
		if (level.isClientSide) {
			return;
		}

		if (!CompleteQuests.hasQuestType(QuestWrapper.REFLECT_GHAST, player)) {
			return;
		}

		CompleteQuests.updateQuestProgression(QuestWrapper.REFLECT_GHAST, level, (ServerPlayer)player, ReflectGhast.staticGetResourceLocationFromObject(level, null), 1);
	}

	public static void onRaidComplete(Level level, Player player, Raid raid) {
		if (level.isClientSide) {
			return;
		}

		if (!CompleteQuests.hasQuestType(QuestWrapper.COMPLETE_RAID, player)) {
			return;
		}

		CompleteQuests.updateQuestProgression(QuestWrapper.COMPLETE_RAID, level, (ServerPlayer)player, CompleteRaid.staticGetResourceLocationFromObject(level, null), 1);
	}

	public static void onHoneyCollect(Level level, Player player) {
		if (level.isClientSide) {
			return;
		}

		if (!CompleteQuests.hasQuestType(QuestWrapper.COLLECT_HONEY, player)) {
			return;
		}

		CompleteQuests.updateQuestProgression(QuestWrapper.COLLECT_HONEY, level, (ServerPlayer)player, CollectHoney.staticGetResourceLocationFromObject(level, null), 1);
	}

	public static void onFishCatch(Level level, Player player) {
		if (level.isClientSide) {
			return;
		}

		if (!CompleteQuests.hasQuestType(QuestWrapper.CATCH_FISH, player)) {
			return;
		}

		CompleteQuests.updateQuestProgression(QuestWrapper.CATCH_FISH, level, (ServerPlayer)player, CatchFish.staticGetResourceLocationFromObject(level, null), 1);
	}
}