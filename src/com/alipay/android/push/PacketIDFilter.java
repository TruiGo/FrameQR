package com.alipay.android.push;

import com.alipay.android.push.connection.PacketFilter;
import com.alipay.android.push.packet.Packet;

/**
 * Filters for packets with a particular packet ID.
 */
public class PacketIDFilter implements PacketFilter {

    private int msgID = -1;

    /**
     * Creates a new packet ID filter using the specified packet ID.
     *
     * @param packetID the packet ID to filter for.
     */
    public PacketIDFilter(int msgID) {
        if (msgID < 0) {
            throw new IllegalArgumentException("Msg ID is invalid.");
        }
        this.msgID = msgID;
    }

    public boolean accept(Packet packet) {
    	if (packet != null && packet.getMsgId() == this.msgID) {
    		return true;
    	} else {
    		return false;
    	}
    }

    public String toString() {
        return "PacketIDFilter by id: " + this.msgID;
    }
}
