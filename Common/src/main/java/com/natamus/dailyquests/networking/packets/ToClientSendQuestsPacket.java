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
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class ToClientSendQuestsPacket {
    public static final ResourceLocation CHANNEL = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "to_client_send_quests_packet");

    private static List<Integer> dataEntries;
    private static List<String> questTitles;
    private static List<String> questDescriptions;
    private static List<Pair<Integer, Integer>> questProgress;

    public ToClientSendQuestsPacket(List<Integer> dataEntriesIn, List<String> rawQuestsIn, List<String> questDescriptionsIn, List<Pair<Integer, Integer>> questProgressIn) {
        dataEntries = dataEntriesIn;
        questTitles = rawQuestsIn;
        questDescriptions = questDescriptionsIn;
        questProgress = questProgressIn;
    }

    public static ToClientSendQuestsPacket decode(FriendlyByteBuf buf) {
        List<Integer> dataEntriesIn = buf.readList(bufIn -> bufIn.readInt());
        List<String> questTitlesIn = buf.readList(bufIn -> bufIn.readUtf(32767));
        List<String> questDescriptionsIn = buf.readList(bufIn -> bufIn.readUtf(32767));
        List<Pair<Integer, Integer>> questProgressIn = buf.readList(buffer -> {
            return Pair.of(buffer.readInt(), buffer.readInt());
        });

        return new ToClientSendQuestsPacket(dataEntriesIn, questTitlesIn, questDescriptionsIn, questProgressIn);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeCollection(dataEntries, (bufOut, i) -> bufOut.writeInt(i));
        buf.writeCollection(questTitles, (bufOut, s) -> bufOut.writeUtf(s));
        buf.writeCollection(questDescriptions, (bufOut, s) -> bufOut.writeUtf(s));
        buf.writeCollection(questProgress, (buffer, pair) -> {
            buffer.writeInt(pair.getFirst());
            buffer.writeInt(pair.getSecond());
        });
    }

    public static void handle(PacketContext<ToClientSendQuestsPacket> ctx) {
        if (ctx.side().equals(Side.CLIENT)) {
            PlayerDataObject previousPlayerDataObject = VariablesClient.playerDataObject;

            VariablesClient.playerDataObject = new PlayerDataObject(UUID.randomUUID(), dataEntries);
            if (VariablesClient.playerDataObject.isShowingIntroduction()) {
                UtilClient.showDailyQuestsIntroduction(questTitles.size());
                return;
            }

            VariablesClient.questTitles = questTitles;
            VariablesClient.questDescriptions = questDescriptions;
            VariablesClient.questProgress = questProgress;

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
