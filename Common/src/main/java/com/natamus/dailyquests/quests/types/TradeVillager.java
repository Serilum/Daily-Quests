package com.natamus.dailyquests.quests.types;

import com.natamus.collective.functions.StringFunctions;
import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.Constants;
import com.natamus.dailyquests.quests.object.QuestObject;
import com.natamus.dailyquests.quests.types.main.AbstractQuest;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TradeVillager extends AbstractQuest {
	private final String name;

	public TradeVillager() {
		this.name = this.getClass().getSimpleName();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isEnabled() {
		return ConfigHandler.enableTradeVillagerQuests;
	}

	@Override
	public Registry<VillagerProfession> getRegistry(Level level) {
		return level.registryAccess().lookupOrThrow(this.getRegistryResourceKey());
	}

	@Override
	public boolean isAllowedIdentifier(Level level, Identifier identifier) {
		Optional<Holder.Reference<VillagerProfession>> villagerProfessionOptionalReference = this.getRegistry(level).get(identifier);
		if (villagerProfessionOptionalReference.isEmpty()) {
			return false;
		}

		VillagerProfession villagerProfession = villagerProfessionOptionalReference.get().value();
		return !villagerProfession.equals(VillagerProfession.NITWIT) && !villagerProfession.equals(VillagerProfession.NONE);
	}

	@Override @Nullable
	public Identifier getRandomQuestIdentifier(Level level) {
		List<Identifier> resourceLocations = new ArrayList<Identifier>(this.getRegistry(level).keySet());

		Identifier identifier = resourceLocations.get(Constants.random.nextInt(resourceLocations.size()));
		while (!this.isAllowedIdentifier(level, identifier)) {
			identifier = resourceLocations.get(Constants.random.nextInt(resourceLocations.size()));
		}

		return identifier;
	}

	@Override
	public int getRandomQuestProgressGoal(Level level, Identifier identifier) {
		return Constants.random.nextInt(2, 8) + 1;
	}

	@Override
	public Identifier getIdentifierFromObject(Level level, Object object) {
		return this.getRegistry(level).getKey(this.objectCast(object));
	}

	@Override
	public String getLocalizedIdentifierName(Level level, Identifier identifier) {
		Registry<VillagerProfession> registry = this.getRegistry(level);
        if (registry.containsKey(identifier)) {
			Optional<Holder.Reference<VillagerProfession>> villagerProfessionOptionalReference = registry.get(identifier);
			if (villagerProfessionOptionalReference.isPresent()) {
				return StringFunctions.capitalizeEveryWord(villagerProfessionOptionalReference.get().value().name().getString());
			}
        }
		return identifier.toString();
	}

	@Override
	public int getFinishExperience(QuestObject quest) {
		return quest.getGoalProgress() * 20;
	}

	@Override
	public void onQuestFinished(Player player) {

	}

	private ResourceKey<Registry<VillagerProfession>> getRegistryResourceKey() {
		return Registries.VILLAGER_PROFESSION;
	}

	private VillagerProfession objectCast(Object object) {
		return (VillagerProfession)object;
	}

	public static Identifier staticGetIdentifierFromObject(Level level, Object object) {
		return level.registryAccess().lookupOrThrow(Registries.VILLAGER_PROFESSION).getKey((VillagerProfession)(object));
	}
}