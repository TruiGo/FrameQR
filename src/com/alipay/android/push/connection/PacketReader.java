package com.alipay.android.push.connection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.alipay.android.push.connection.Connection.ListenerWrapper;
import com.alipay.android.push.packet.Packet;
import com.alipay.android.push.packet.PacketConstants;
import com.alipay.android.push.packet.PacketFactory;
import com.alipay.android.push.util.LogUtil;

/**
 * Listens for XML traffic from the XMPP server and parses it into packet objects.
 * The packet reader also invokes all packet listeners and collectors.<p>
 *
 * @see Connection#createPacketCollector
 * @see Connection#addPacketListener
 */
class PacketReader {
	private static final String LOGTAG = LogUtil.makeLogTag(PacketReader.class);

    private Thread readerThread;
    private ExecutorService listenerExecutor;

    private XMPPConnection connection;
    private XmlPullParser parser;
    private boolean done;

    private String connectionID = null;
    private Semaphore connectionSemaphore;

    protected PacketReader(final XMPPConnection connection) {
        this.connection = connection;
        
        this.init();
    }

    /**
     * Initializes the reader in order to be used. The reader is initialized during the
     * first connection and when reconnecting due to an abruptly disconnection.
     */
    protected void init() {
        done = false;
        connectionID = null;

        readerThread = new Thread() {
            public void run() {
                parsePackets(this);
            }
        };
        readerThread.setName("Smack Packet Reader (" + connection.connectionCounterValue + ")");
        readerThread.setDaemon(true);
        
        // Create an executor to deliver incoming packets to listeners. We'll use a single
        // thread with an unbounded queue.
        listenerExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable,"Smack Listener Processor (" + connection.connectionCounterValue + ")");
                thread.setDaemon(true);
                return thread;
            }
        });
        
//        resetParser();
    }

    /**
     * Starts the packet reader thread and returns once a connection to the server
     * has been established. A connection will be attempted for a maximum of five
     * seconds. An XMPPException will be thrown if the connection fails.
     *
     * @throws XMPPException if the server fails to send an opening stream back
     *      for more than five seconds.
     */
    public void startup() throws XMPPException {
        connectionSemaphore = new Semaphore(1);

        readerThread.start();
        // Wait for stream tag before returning. We'll wait a couple of seconds before
        // giving up and throwing an error.
        try {
            connectionSemaphore.acquire();

            // A waiting thread may be woken up before the wait time or a notify
            // (although this is a rare thing). Therefore, we continue waiting
            // until either a connectionID has been set (and hence a notify was
            // made) or the total wait time has elapsed.
            int waitTime = SmackConfiguration.getPacketReplyTimeout();
//            connectionSemaphore.tryAcquire(1 * waitTime, TimeUnit.MILLISECONDS);
            connectionSemaphore.tryAcquire(2 * 1000, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException ie) {
            // Ignore.
        }
//        if (connectionID == null) {
//            throw new XMPPException("Connection failed. No response from server.");
//        }
//        else {
//            connection.connectionID = connectionID;
//        }
    }

    /**
     * Shuts the packet reader down.
     */
    public void shutdown() {
        // Notify connection listeners of the connection closing if done hasn't already been set.
        if (!done) {
            for (ConnectionListener listener : connection.getConnectionListeners()) {
                try {
                    listener.connectionClosed();
                }
                catch (Exception e) {
                    // Cath and print any exception so we can recover
                    // from a faulty listener and finish the shutdown process
                    e.printStackTrace();
                }
            }
        }
        done = true;

        // Shut down the listener executor.
        listenerExecutor.shutdown();
        
        LogUtil.LogOut(3, LOGTAG, "shutdown()...listenerExecutor.shutdown!");
    }

    /**
     * Cleans up all resources used by the packet reader.
     */
    void cleanup() {
        connection.recvListeners.clear();
    }

    /**
     * Sends out a notification that there was an error with the connection
     * and closes the connection.
     *
     * @param e the exception that causes the connection close event.
     */
    void notifyConnectionError(Exception e) {
    	// Print the stack trace to help catch the problem
    	e.printStackTrace();
    	LogUtil.LogOut(2, LOGTAG, "notifyConnectionError()...Exception!");
        // Notify connection listeners of the error.
        for (ConnectionListener listener : connection.getConnectionListeners()) {
            try {
                listener.connectionClosedOnError(e);
            }
            catch (Exception e2) {
                // Catch and print any exception so we can recover from a faulty listener
                e2.printStackTrace();
            }
        }
    }

    /**
     * Sends a notification indicating that the connection was reconnected successfully.
     */
    protected void notifyReconnection() {
        // Notify connection listeners of the reconnection.
        for (ConnectionListener listener : connection.getConnectionListeners()) {
            try {
                listener.reconnectionSuccessful();
            }
            catch (Exception e) {
                // Catch and print any exception so we can recover from a faulty listener
                e.printStackTrace();
            }
        }
    }

    /**
     * Resets the parser using the latest connection's reader. Reseting the parser is necessary
     * when the plain connection has been secured or when a new opening stream element is going
     * to be sent by the server.
     */
    private void resetParser() {
        try {
            parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
//            parser.setInput(connection.reader);
        }
        catch (XmlPullParserException xppe) {
            xppe.printStackTrace();
        }
    }

    /**
     * Parse top-level packets in order to process them further.
     *
     * @param thread the thread that is being used by the reader to parse incoming packets.
     */
    private void parsePackets(Thread thread) {
        try {
//        	boolean isException = false;
    		byte[] netData = new byte[2048];
    		LogUtil.LogOut(4, LOGTAG, "parsePackets()...");
            do {
            	int count = connection.reader.read(netData);
				if (count > 0) {
					LogUtil.LogOut(4, LOGTAG, "reader() count="+count);

					//简单判断一下是否为有效数据
					if(count >= 2) {	
						byte[] rawData = new byte[count];
						System.arraycopy(netData, 0, rawData, 0, count);
        				handleRecvMsg(rawData, count);
					} else {
						// 
					}
				}
            } while (!done && thread == readerThread);
        }
        catch (Exception e) {
            if (!done) {
            	LogUtil.LogOut(2, LOGTAG, "parsePackets() encounter Exception!");
                // Close the connection and notify connection listeners of the error.
                notifyConnectionError(e);
            }
        }
    }
    
	private void handleRecvMsg(byte[] netData, int bufferLen) {
		int leftLen = bufferLen;
		int thisLen = 0;

		int msgVersion = this.connection.getMsgVersion();  
		InputStream in = new ByteArrayInputStream(netData); 

		while (leftLen >= 2) { 
			LogUtil.LogOut(4, LOGTAG,"handleRecvMsg() got valid packet msgVersion:"+msgVersion
					+", msgByte1st: "+ Integer.toBinaryString(netData[0]));

			Packet recvMsg;
			try  {
				recvMsg = PacketFactory.getPacket(msgVersion);

				//必须先处理前两个字节
				int hdrLen = 2;
				byte[] baseHdrBuf = new byte[hdrLen];
				int readLen = in.read(baseHdrBuf, 0, 2);
				LogUtil.LogOut(4, LOGTAG,"handleRecvMsg() readLen="+readLen);
				if (readLen == hdrLen) {
					recvMsg.initBaseHdrfromRead(baseHdrBuf);
				} else {
					//不在继续处理，跳出
					break;
				}
				
				int leftHdrLen = recvMsg.getPacketHdrLen() - hdrLen;
				LogUtil.LogOut(4, LOGTAG,"handleRecvMsg() leftHdrLen="+leftHdrLen);
				byte[] hdrBuf = new byte[leftHdrLen];
				if (msgVersion == 3 && recvMsg.getMsgId() == PacketConstants.MSG_PUSH_KEEPLIVE) {
					//当前为心跳数据包，且读取完毕
					processPacket(recvMsg);
					
					thisLen = recvMsg.getPacketHdrLen();
				} else {
					if (leftHdrLen > (leftLen-2)) {
						LogUtil.LogOut(2, LOGTAG,"handleRecvMsg() got error header!");
						//不在继续处理，跳出
						break;
					}
					
					//后续头部处理
					readLen = in.read(hdrBuf, 0, leftHdrLen);
					if (readLen == leftHdrLen) {
						recvMsg.initHdrfromRead(hdrBuf);
						
						//继续处理，把当前不支持的packet处理完再继续
						int bodyLen = recvMsg.getDataLength();
						if (bodyLen <= (leftLen-recvMsg.getPacketHdrLen()) && bodyLen >= 0) {
							byte[] bodyBuf = new byte[bodyLen];
							in.read(bodyBuf, 0, bodyLen);
							recvMsg.setData(bodyBuf);
							LogUtil.LogOut(4, LOGTAG,"handleRecvMsg() got valid packet! rawData=" + recvMsg.getData());

							if (!Packet.isSupport(recvMsg)) {
								LogUtil.LogOut(2, LOGTAG,"handleRecvMsg() it's unsupported packet!");
							} else {
								processPacket(recvMsg);
							}
						} else {
							//数据包有问题，不做处理
						}
						
						thisLen = (recvMsg.getDataLength() + recvMsg.getPacketHdrLen());
					} else {
						LogUtil.LogOut(2, LOGTAG,"handleRecvMsg() got error packet!");
						
						if (readLen == -1) {
							break;
						}
						thisLen = readLen;
					}
				}
					
				leftLen = leftLen - thisLen;
				LogUtil.LogOut(4, LOGTAG,"handleRecvMsg() current thisLen="+thisLen +", leftLen="+leftLen);
				
			} catch (Exception e)  {
				// Catch and print any exception so we can recover
				e.printStackTrace();
				return;
			} 
		}

		LogUtil.LogOut(5, LOGTAG,"handleRecvMsg() done! leftLen=" + leftLen);
	}
	

    /**
     * Processes a packet after it's been fully parsed by looping through the installed
     * packet collectors and listeners and letting them examine the packet to see if
     * they are a match with the filter.
     *
     * @param packet the packet to process.
     */
    private void processPacket(Packet packet) {
        if (packet == null) {
            return;
        }
        LogUtil.LogOut(5, LOGTAG, "processPacket() are processing one valid packet!");
        
        //stop发送等待定时器
		connection.stopTimer();
		connection.resetTryCount();

        // Deliver the incoming packet to listeners.
        listenerExecutor.submit(new ListenerNotification(packet));
//        listenerExecutor.submit(new Callback(packet));
    }
  
    /**
     * A runnable to notify all listeners of a packet.
     */
    private class ListenerNotification implements Runnable {

        private Packet packet;

        public ListenerNotification(Packet packet) {
            this.packet = packet;
        }

        public void run() {
            for (ListenerWrapper listenerWrapper : connection.recvListeners.values()) {
                listenerWrapper.notifyListener(packet);
            }
        }
    }
}
