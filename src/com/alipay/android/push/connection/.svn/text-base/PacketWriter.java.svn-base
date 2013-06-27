
package com.alipay.android.push.connection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.alipay.android.push.packet.Packet;
import com.alipay.android.push.packet.PacketConstants;
import com.alipay.android.push.util.LogUtil;

/**
 * Writes packets to a XMPP server. Packets are sent using a dedicated thread. Packet
 * interceptors can be registered to dynamically modify packets before they're actually
 * sent. Packet listeners can be registered to listen for all outgoing packets.
 *
 * @see Connection#addPacketInterceptor
 * @see Connection#addPacketSendingListener
 */
class PacketWriter {
	private static final String LOGTAG = LogUtil.makeLogTag(PacketWriter.class);

    private Thread writerThread;
    private DataOutputStream writer;
    private XMPPConnection connection;
    private final BlockingQueue<Packet> queue;
    private boolean done;

    /**
     * Timestamp when the last stanza was sent to the server. This information is used
     * by the keep alive process to only send heartbeats when the connection has been idle.
     */
    private long lastActive = System.currentTimeMillis();

    /**
     * Creates a new packet writer with the specified connection.
     *
     * @param connection the connection.
     */
    protected PacketWriter(XMPPConnection connection) {
        this.queue = new ArrayBlockingQueue<Packet>(500, true);
        this.connection = connection;
        init();
    }

    /** 
    * Initializes the writer in order to be used. It is called at the first connection and also 
    * is invoked if the connection is disconnected by an error.
    */ 
    protected void init() {
        this.writer = connection.writer;
        done = false;

        writerThread = new Thread() {
            public void run() {
                writePackets(this);
            }
        };
        writerThread.setName("Smack Packet Writer (" + connection.connectionCounterValue + ")");
        writerThread.setDaemon(true);
    }

    /**
     * Sends the specified packet to the server.
     * 
     * @param packet the packet to send.
     */
    public void sendPacket(Packet packet) {
    	LogUtil.LogOut(4, LOGTAG, "sendPacket() enter... done="+done);
        if (!done) {
            // Invoke interceptors for the new packet that is about to be sent. Interceptors
            // may modify the content of the packet.
//            connection.firePacketInterceptors(packet);

			try {
				queue.put(packet);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
				return;
			}

			synchronized (queue) {
				LogUtil.LogOut(4, LOGTAG, "sendPacket queue len="+queue.size());
				queue.notifyAll();
			}

            // Process packet writer listeners. Note that we're using the sending
            // thread so it's expected that listeners are fast.
//            connection.firePacketSendingListeners(packet);
        }
    }

    /**
     * Starts the packet writer thread and opens a connection to the server. The
     * packet writer will continue writing packets until {@link #shutdown} or an
     * error occurs.
     */
    public void startup() {
        writerThread.start();
    }

    void setWriter(DataOutputStream writer) {
        this.writer = writer;
    }

    /**
     * Shuts down the packet writer. Once this method has been called, no further
     * packets will be written to the server.
     */
    public void shutdown() {
        done = true;
        synchronized (queue) {
            queue.notifyAll();
        }
    }

    /**
     * Cleans up all resources used by the packet writer.
     */
    void cleanup() {
        connection.interceptors.clear();
        connection.sendListeners.clear();
    }

    /**
     * Returns the next available packet from the queue for writing.
     *
     * @return the next packet for writing.
     */
    private Packet nextPacket() {
        Packet packet = null;
        // Wait until there's a packet or we're done.
        while (!done && (packet = queue.poll()) == null) {
            try {
                synchronized (queue) {
                    queue.wait();
                    LogUtil.LogOut(4, LOGTAG, "nextPacket queue len="+queue.size());
                }
            }
            catch (InterruptedException ie) {
                // Do nothing
            }
        }
        return packet;
    }

    private void writePackets(Thread thisThread) {
        try {
            // Open the stream.
            //openStream();
        	
            // Write out packets from the queue.
            while (!done && (writerThread == thisThread)) {
                Packet packet = nextPacket();
                if (packet != null) {
                    synchronized (writer) {
                    	LogUtil.LogOut(3, LOGTAG, "writePackets curMsgId="+packet.getMsgId());
                        writer.write(packet.toByteBuf());
                        writer.flush();
                        
                        // Keep track of the last time a stanza was sent to the server
                        lastActive = System.currentTimeMillis();
                        
                        if (PacketConstants.MSG_PUSH_TYPE_REQUEST == packet.getMsgType()) {
                        	//设置
                            connection.startTimer(lastActive, packet.getMsgId());
                        }
                    }
                }
                
                synchronized (queue) {
                	LogUtil.LogOut(4, LOGTAG, "writePackets queue len="+queue.size());
                }
                
            }
            // Flush out the rest of the queue. If the queue is extremely large, it's possible
            // we won't have time to entirely flush it before the socket is forced closed
            // by the shutdown process.
            // 目前不需要如此处理——展志
            /*
            try {
                synchronized (writer) {
                   while (!queue.isEmpty()) {
                       Packet packet = queue.remove();
                       writer.write(packet.toByteBuf());
                    }
                    writer.flush();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            */

            // Delete the queue contents (hopefully nothing is left).
            queue.clear();

            // Close the stream.
            try {
//                writer.write("</stream:stream>");
                writer.flush();
            }
            catch (Exception e) {
                // Do nothing
            }
            finally {
                try {
                    writer.close();
                }
                catch (Exception e) {
                    // Do nothing
                }
            }
        }
        catch (IOException ioe){
            if (!done) {
                done = true;
                connection.packetReader.notifyConnectionError(ioe);
            }
        }
    }
    
}