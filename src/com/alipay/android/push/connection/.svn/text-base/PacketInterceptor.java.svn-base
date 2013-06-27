package com.alipay.android.push.connection;

import com.alipay.android.push.packet.Packet;

/**
 * Provides a mechanism to intercept and modify packets that are going to be
 * sent to the server. PacketInterceptors are added to the {@link Connection}
 * together with a {@link org.jivesoftware.smack.filter.PacketFilter} so that only
 * certain packets are intercepted and processed by the interceptor.<p>
 *
 * This allows event-style programming -- every time a new packet is found,
 * the {@link #interceptPacket(Packet)} method will be called.
 *
 * @see Connection#addPacketInterceptor(PacketInterceptor, org.jivesoftware.smack.filter.PacketFilter)
 */
public interface PacketInterceptor {

    /**
     * Process the packet that is about to be sent to the server. The intercepted
     * packet can be modified by the interceptor.<p>
     *
     * Interceptors are invoked using the same thread that requested the packet
     * to be sent, so it's very important that implementations of this method
     * not block for any extended period of time.
     *
     * @param packet the packet to is going to be sent to the server.
     */
    public void interceptPacket(Packet packet);
}
