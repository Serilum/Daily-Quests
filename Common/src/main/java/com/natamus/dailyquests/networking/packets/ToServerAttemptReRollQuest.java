package com.natamus.dailyquests.networking.packets;

import com.natamus.collective.implementations.networking.data.PacketContext;
import com.natamus.collective.implementations.networking.data.Side;
import com.natamus.dailyquests.config.ConfigHandler;
import com.natamus.dailyquests.data.Variables;
import com.natamus.dailyquests.quests.functions.GenerateQuests;
import com.natamus.dailyquests.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.UUID;

public class ToServerAttemptReRollQuest {
    public static final ResourceLocation CHANNEL = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "to_server_attempt_re_roll_quest");

    private static int questNumber;

    public ToServerAttemptReRollQuest(int questNumberIn) {
        questNumber = questNumberIn;
    }

    public static ToServerAttemptReRollQuest decode(FriendlyByteBuf buf) {
        int questNumberIn = buf.readInt();

        return new ToServerAttemptReRollQuest(questNumberIn);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(questNumber);
    }

    public static void handle(PacketContext<ToServerAttemptReRollQuest> ctx) {
        if (ctx.side().equals(Side.SERVER)) {
            Player player = ctx.sender();
            Level level = player.level();
            if (level.isClientSide) {
                return;
            }

            UUID playerUUID = player.getUUID();
            if (!Variables.playerDataMap.containsKey(playerUUID) || !Variables.playerQuestDataMap.containsKey(playerUUID)) {
                return;
            }

            if (!ConfigHandler.allowReRollingCompletedQuests) {
                if (Variables.playerQuestDataMap.get(playerUUID).values().stream().toList().get(questNumber-1).isCompleted()) {
                    return;
                }
            }

            if (Variables.playerDataMap.get(playerUUID).getReRollsLeft() > 0) {
                Variables.playerDataMap.get(playerUUID).decrementReRolls();

                GenerateQuests.replaceSpecificPlayerQuest((ServerLevel) level, (ServerPlayer) player, Arrays.asList(questNumber));
            }
        }
    }
}
