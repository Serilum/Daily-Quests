package com.natamus.dailyquests.networking.packets;

import com.mojang.datafixers.util.Pair;
import com.natamus.collective.implementations.networking.data.PacketContext;
import com.natamus.collective.implementations.networking.data.Side;
import com.natamus.dailyquests.data.VariablesClient;
import com.natamus.dailyquests.quests.object.PlayerDataObject;
import com.natamus.dailyquests.util.Reference;
import com.natamus.dailyquests.util.UtilClient;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class ToClientSendQuestsPacket {
	public static final Identifier CHANNEL = Identifier.fromNamespaceAndPath(Reference.MOD_ID, "to_client_send_quests_packet");

	private final List<Integer> dataEntries;
	private final List<String> questTitles;
	private final List<String> questDescriptions;
	private final List<Pair<Integer, Integer>> questProgress;

	public ToClientSendQuestsPacket(List<Integer> dataEntriesIn, List<String> rawQuestsIn, List<String> questDescriptionsIn, List<Pair<Integer, Integer>> questProgressIn) {
		this.dataEntries = dataEntriesIn;
		this.questTitles = rawQuestsIn;
		this.questDescriptions = questDescriptionsIn;
		this.questProgress = questProgressIn;
	}

	public static ToClientSendQuestsPacket decode(FriendlyByteBuf buf) {
		try {
			List<Integer> dataEntriesIn = ByteBufCodecs.INT.apply(ByteBufCodecs.list()).decode(buf);
			List<String> questTitlesIn = ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).decode(buf);
			List<String> questDescriptionsIn = ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).decode(buf);
			List<Pair<Integer, Integer>> questProgressIn = new ArrayList<>();
			int questProgressCount = buf.readVarInt();
			for (int i = 0; i < questProgressCount; i++) {
				questProgressIn.add(Pair.of(buf.readInt(), buf.readInt()));
			}

			return new ToClientSendQuestsPacket(dataEntriesIn, questTitlesIn, questDescriptionsIn, questProgressIn);
		}
		catch (IndexOutOfBoundsException ex) {
			return new ToClientSendQuestsPacket(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		}
	}

	public void encode(FriendlyByteBuf buf) {
		ByteBufCodecs.INT.apply(ByteBufCodecs.list()).encode(buf, dataEntries);
		ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).encode(buf, questTitles);
		ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).encode(buf, questDescriptions);
		buf.writeVarInt(questProgress.size());
		for (Pair<Integer, Integer> pair : questProgress) {
			buf.writeInt(pair.getFirst());
			buf.writeInt(pair.getSecond());
		}
	}

	public static void handle(PacketContext<ToClientSendQuestsPacket> ctx) {
		if (ctx.side().equals(Side.CLIENT)) {
			ToClientSendQuestsPacket packet = ctx.message();
			PlayerDataObject previousPlayerDataObject = VariablesClient.playerDataObject;

			VariablesClient.playerDataObject = new PlayerDataObject(UUID.randomUUID(), packet.dataEntries);
			if (VariablesClient.playerDataObject.isShowingIntroduction()) {
				UtilClient.showDailyQuestsIntroduction(packet.questTitles.size());
				return;
			}

			VariablesClient.questTitles = packet.questTitles;
			VariablesClient.questDescriptions = packet.questDescriptions;
			VariablesClient.questProgress = packet.questProgress;

			boolean reRollCountChanged = false;
			if (previousPlayerDataObject != null) {
				reRollCountChanged = previousPlayerDataObject.getReRollsLeft() != VariablesClient.playerDataObject.getReRollsLeft();
			}

			if (VariablesClient.waitingForNewQuest || reRollCountChanged) {
				VariablesClient.waitingForNewQuest = false;

				for (Button button : VariablesClient.reRollButtons.values()) {
					button.visible = false;
				}

				VariablesClient.reRollButtons = new LinkedHashMap<>();
				VariablesClient.addedRerollButtons = false;
			}
		}
	}
}
