package com.natamus.dailyquests.networking;

import com.natamus.collective.implementations.networking.api.Network;
import com.natamus.dailyquests.networking.packets.ToClientSendQuestsPacket;
import com.natamus.dailyquests.networking.packets.ToServerAttemptReRollQuest;

public class PacketRegistration {

    public void init() {
        initClientPackets();
        initServerPackets();
    }

    private void initClientPackets() {
        Network.registerPacket(ToClientSendQuestsPacket.CHANNEL, ToClientSendQuestsPacket.class, ToClientSendQuestsPacket::encode, ToClientSendQuestsPacket::decode, ToClientSendQuestsPacket::handle);
    }

    private void initServerPackets() {
        Network.registerPacket(ToServerAttemptReRollQuest.CHANNEL, ToServerAttemptReRollQuest.class, ToServerAttemptReRollQuest::encode, ToServerAttemptReRollQuest::decode, ToServerAttemptReRollQuest::handle);
    }
}
