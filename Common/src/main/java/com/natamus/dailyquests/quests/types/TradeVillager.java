package com.natamus.dailyquests.quests.types;

import com.natamus.collective.functions.StringFunctions;
import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.Constants;
import com.natamus.dailyquests.quests.object.QuestObject;
import com.natamus.dailyquests.quests.types.main.AbstractQuest;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
		return level.registryAccess().registryOrThrow(this.getRegistryResourceKey());
	}

	@Override
	public boolean isAllowedIdentifier(Level level, ResourceLocation identifier) {
		VillagerProfession villagerProfession = this.getRegistry(level).get(identifier);
		return !villagerProfession.equals(VillagerProfession.NITWIT) && !villagerProfession.equals(VillagerProfession.NONE);
	}

	@Override @Nullable
	public ResourceLocation getRandomQuestIdentifier(Level level) {
		List<ResourceLocation> resourceLocations = new ArrayList<ResourceLocation>(this.getRegistry(level).keySet());

		ResourceLocation identifier = resourceLocations.get(Constants.random.nextInt(resourceLocations.size()));
		while (!this.isAllowedIdentifier(level, identifier)) {
			identifier = resourceLocations.get(Constants.random.nextInt(resourceLocations.size()));
		}

		return identifier;
	}

	@Override
	public int getRandomQuestProgressGoal(Level level, ResourceLocation identifier) {
		return Constants.random.nextInt(2, 8) + 1;
	}

	@Override
	public ResourceLocation getResourceLocationFromObject(Level level, Object object) {
		return this.getRegistry(level).getKey(this.objectCast(object));
	}

	@Override
	public String getLocalizedIdentifierName(Level level, ResourceLocation identifier) {
		Registry<VillagerProfession> registry = this.getRegistry(level);
        if (registry.containsKey(identifier)) {
            return StringFunctions.capitalizeEveryWord(registry.get(identifier).name());
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

	public static ResourceLocation staticGetResourceLocationFromObject(Level level, Object object) {
		return level.registryAccess().registryOrThrow(Registries.VILLAGER_PROFESSION).getKey((VillagerProfession)(object));
	}
}