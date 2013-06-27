package com.alipay.android.push.packetListener;

import com.alipay.android.push.connection.Connection;
import com.alipay.android.push.packet.Packet;

/**
 * Provides a mechanism to listen for packets that pass a specified filter.
 * This allows event-style programming -- every time a new packet is found,
 * the {@link #processPacket(Packet)} method will be called. This is the
 * opposite approach to the functionality provided by a {@link PacketCollector}
 * which lets you block while waiting for results.
 *
 * @see Connection#addPacketListener(PacketListener, org.jivesoftware.smack.filter.PacketFilter)
 */
public interface PacketListener {
//	public XmppManager xmppManager =  NotificationService.xmppManager;

    /**
     * Process the next packet sent to this packet listener.<p>
     *
     * A single thread is responsible for invoking all listeners, so
     * it's very important that implementations of this method not block
     * for any extended period of time.
     *
     * @param packet the packet to process.
     */
    public void processPacket(Packet packet);

}
