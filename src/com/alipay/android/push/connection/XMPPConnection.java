package com.alipay.android.push.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.security.auth.callback.CallbackHandler;

import android.net.SSLCertificateSocketFactory;

import com.alipay.android.push.callback.TaskListener;
import com.alipay.android.push.connection.ConnectionConfiguration.SecurityMode;
import com.alipay.android.push.connection.proxy.ProxyInfo;
import com.alipay.android.push.packet.Packet;
import com.alipay.android.push.packet.PacketConstants;
import com.alipay.android.push.packetListener.PacketListener;
import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.StringUtils;
import com.alipay.android.push.util.record.RecordtoFile;

/**
 * Creates a socket connection to a XMPP server. This is the default connection
 * to a Jabber server and is specified in the XMPP Core (RFC 3920).
 * 
 * @see Connection
 * @author Matt Tucker
 */
public class XMPPConnection extends Connection {
	private static final String LOGTAG = LogUtil.makeLogTag(XMPPConnection.class);

    /**
     * The socket which is used for this connection.
     */
    protected Socket socket;

    String connectionID = null;
    private String user = null;
    private boolean connected = false;
    private int msgVersion = PacketConstants.PACKET_VERSION_2;
    private int retryTimes = 0;
    private int mTryCount = 0;
    /**
     * Flag that indicates if the user is currently authenticated with the server.
     */
    private boolean authenticated = false;
    /**
     * Flag that indicates if the user was authenticated with the server when the connection
     * to the server was closed (abruptly or not).
     */
    private boolean wasAuthenticated = false;
    private boolean anonymous = false;
    private boolean usingTLS = false;

    PacketWriter packetWriter;
    PacketReader packetReader;
    
    private Timer mTimer = null;
    private int lastMsgId = -1;
    private long lastActive = System.currentTimeMillis();


    /**
     * Collection of available stream compression methods offered by the server.
     */
    private Collection<String> compressionMethods;
    /**
     * Flag that indicates if stream compression is actually in use.
     */
    private boolean usingCompression;



    /**
     * Creates a new XMPP conection in the same way {@link #XMPPConnection(ConnectionConfiguration,CallbackHandler)} does, but
     * with no callback handler for password prompting of the keystore.  This will work
     * in most cases, provided the client is not required to provide a certificate to 
     * the server.
     *
     *
     * @param config the connection configuration.
     */
    public XMPPConnection(ConnectionConfiguration config) {
        super(config);
    }

    public String getConnectionID() {
        if (!isConnected()) {
            return null;
        }
        return connectionID;
    }

    public String getUser() {
        if (!isAuthenticated()) {
            return null;
        }
        return user;
    }

    /**
     * Logs in to the server using the strongest authentication mode supported by
     * the server. If the server supports SASL authentication then the user will be
     * authenticated using SASL if not Non-SASL authentication will be tried. If more than
     * five seconds (default timeout) elapses in each step of the authentication process
     * without a response from the server, or if an error occurs, a XMPPException will be
     * thrown.<p>
     * 
     * Before logging in (i.e. authenticate) to the server the connection must be connected.
     * For compatibility and easiness of use the connection will automatically connect to the
     * server if not already connected.<p>
     *
     * It is possible to log in without sending an initial available presence by using
     * {@link ConnectionConfiguration#setSendPresence(boolean)}. If this connection is
     * not interested in loading its roster upon login then use
     * {@link ConnectionConfiguration#setRosterLoadedAtLogin(boolean)}.
     * Finally, if you want to not pass a password and instead use a more advanced mechanism
     * while using SASL then you may be interested in using
     * {@link ConnectionConfiguration#setCallbackHandler(javax.security.auth.callback.CallbackHandler)}.
     * For more advanced login settings see {@link ConnectionConfiguration}.
     *
     * @param username the username.
     * @param password the password or <tt>null</tt> if using a CallbackHandler.
     * @param resource the resource.
     * @throws XMPPException if an error occurs.
     * @throws IllegalStateException if not connected to the server, or already logged in
     *      to the serrver.
     */
    public synchronized void login(String username, String password, String resource) throws XMPPException {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected to server.");
        }
        if (authenticated) {
            throw new IllegalStateException("Already logged in to server.");
        }
        // Do partial version of nameprep on the username.
        username = username.toLowerCase().trim();

        String response = "";
//        if (config.isSASLAuthenticationEnabled() &&
//                saslAuthentication.hasNonAnonymousAuthentication()) {
//            // Authenticate using SASL
//            if (password != null) {
//                response = saslAuthentication.authenticate(username, password, resource);
//            }
//            else {
//                response = saslAuthentication
//                        .authenticate(username, resource, config.getCallbackHandler());
//            }
//        }
//        else {
//            // Authenticate using Non-SASL
//            response = new NonSASLAuthentication(this).authenticate(username, password, resource);
//        }

        // Set the user.
        if (response != null) {
            this.user = response;
            // Update the serviceName with the one returned by the server
            config.setServiceName(StringUtils.parseServer(response));
        }
       /* else {
            this.user = username + "@" + getServiceName();
            if (resource != null) {
                this.user += "/" + resource;
            }
        }*/

        // If compression is enabled then request the server to use stream compression
        if (config.isCompressionEnabled()) {
            useCompression();
        }

        // Set presence to online.
        if (config.isSendPresence()) {
//            packetWriter.sendPacket(new Presence(Presence.Type.available));
        }

        // Indicate that we're now authenticated.
        authenticated = true;
        anonymous = false;

        // Stores the autentication for future reconnection
        config.setLoginInfo(username, password, resource);

        // If debugging is enabled, change the the debug window title to include the
        // name we are now logged-in as.
        // If DEBUG_ENABLED was set to true AFTER the connection was created the debugger
        // will be null
//        if (config.isDebuggerEnabled() && debugger != null) {
//            debugger.userHasLogged(user);
//        }
    }

    /**
     * Logs in to the server anonymously. Very few servers are configured to support anonymous
     * authentication, so it's fairly likely logging in anonymously will fail. If anonymous login
     * does succeed, your XMPP address will likely be in the form "server/123ABC" (where "123ABC"
     * is a random value generated by the server).
     *
     * @throws XMPPException if an error occurs or anonymous logins are not supported by the server.
     * @throws IllegalStateException if not connected to the server, or already logged in
     *      to the serrver.
     */
    public synchronized void loginAnonymously() throws XMPPException {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected to server.");
        }
        if (authenticated) {
            throw new IllegalStateException("Already logged in to server.");
        }

        String response = "";
//        if (config.isSASLAuthenticationEnabled() &&
//                saslAuthentication.hasAnonymousAuthentication()) {
//            response = saslAuthentication.authenticateAnonymously();
//        }
//        else {
//            // Authenticate using Non-SASL
//            response = new NonSASLAuthentication(this).authenticateAnonymously();
//        }

        // Set the user value.
        this.user = response;
        // Update the serviceName with the one returned by the server
        config.setServiceName(StringUtils.parseServer(response));

        // If compression is enabled then request the server to use stream compression
        if (config.isCompressionEnabled()) {
            useCompression();
        }

        // Anonymous users can't have a roster.
//        roster = null;

        // Set presence to online.
//        packetWriter.sendPacket(new Presence(Presence.Type.available));

        // Indicate that we're now authenticated.
        authenticated = true;
        anonymous = true;

        // If debugging is enabled, change the the debug window title to include the
        // name we are now logged-in as.
        // If DEBUG_ENABLED was set to true AFTER the connection was created the debugger
        // will be null
//        if (config.isDebuggerEnabled() && debugger != null) {
//            debugger.userHasLogged(user);
//        }
    }

    public boolean isConnected() {
    	LogUtil.LogOut(4, LOGTAG, "isConnected()...called="+connected);
        return connected;
    }

    public void setConnected(boolean isConnected){
    	LogUtil.LogOut(4, LOGTAG, "setConnected()...isConnected="+isConnected);
    	connected = isConnected;
    }
    
    public void setMsgVersion(int protoVer){
    	msgVersion = protoVer;
    }
    
    public int getMsgVersion(){
    	return this.msgVersion;
    }
    
    public void setRetryTimes(int times){
    	retryTimes = times;
    }
    
    public boolean isSecureConnection() {
        return isUsingTLS();
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    /**
     * Closes the connection by setting presence to unavailable then closing the stream to
     * the XMPP server. The shutdown logic will be used during a planned disconnection or when
     * dealing with an unexpected disconnection. Unlike {@link #disconnect()} the connection's
     * packet reader, packet writer, and {@link Roster} will not be removed; thus
     * connection's state is kept.
     *
     * @param unavailablePresence the presence packet to send during shutdown.
     */
    protected void shutdown() {
    	LogUtil.LogOut(2, LOGTAG, "shutdown() called...");
    	
        // Set presence to offline.
    	if(packetWriter!=null){
//    		packetWriter.sendPacket(unavailablePresence);
    	}

        this.setWasAuthenticated(authenticated);
        authenticated = false;
        connected = false;
        retryTimes = 0;
        mTryCount = 0;
        
        if(packetReader!=null){
        	packetReader.shutdown();
        }
        if(packetWriter!=null){
        	packetWriter.shutdown();
        }
        // Wait 150 ms for processes to clean-up, then shutdown.
        try {
            Thread.sleep(150);
        }
        catch (Exception e) {
            // Ignore.
        }

        // Close down the readers and writers.
        if (reader != null) {
            try {
                reader.close();
            }
            catch (Throwable ignore) { /* ignore */ }
            reader = null;
        }
        if (writer != null) {
            try {
                writer.close();
            }
            catch (Throwable ignore) { /* ignore */ }
            writer = null;
        }

        try {
            socket.close();
        }
        catch (Exception e) {
            // Ignore.
        }
        LogUtil.LogOut(3, LOGTAG, "shutdown()... Done!");
    }

    public void disconnect() {
    	LogUtil.LogOut(3, LOGTAG, "disconnect()... called!");
    	
    	RecordtoFile.recordPushInfo(RecordtoFile.REASON_UNKNOWN, RecordtoFile.ACTION_DISCONN,
				System.currentTimeMillis(), 
				RecordtoFile.ACTIONT_UNKNOWN, 
				System.currentTimeMillis()+1*0*1000,
				"XMPPConnection_disconnect",
				1);
    	
        // If not connected, ignore this request.
        if (packetReader == null || packetWriter == null) {
            return;
        }
        shutdown();
        wasAuthenticated = false;

        packetWriter.cleanup();
        packetWriter = null;
        packetReader.cleanup();
        packetReader = null;
        
        LogUtil.LogOut(3, LOGTAG, "disconnect()... done!");
    }

    public void sendPacket(Packet packet) {
    	LogUtil.LogOut(4, LOGTAG, "sendPacket()... isConnected="+isConnected());
 try {
			if (!isConnected()) {
			    throw new IllegalStateException("Have not connected to server.");
			}
			if (packet == null) {
			    throw new NullPointerException("Packet is null.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	
        LogUtil.LogOut(3, LOGTAG, "sendPacket()... packet.id="+packet.getMsgId());
        packetWriter.sendPacket(packet);
    }

    /**
     * Registers a packet interceptor with this connection. The interceptor will be
     * invoked every time a packet is about to be sent by this connection. Interceptors
     * may modify the packet to be sent. A packet filter determines which packets
     * will be delivered to the interceptor.
     *
     * @param packetInterceptor the packet interceptor to notify of packets about to be sent.
     * @param packetFilter      the packet filter to use.
     * @deprecated replaced by {@link Connection#addPacketInterceptor(PacketInterceptor, PacketFilter)}.
     */
    public void addPacketWriterInterceptor(PacketInterceptor packetInterceptor,
            PacketFilter packetFilter) {
//        addPacketInterceptor(packetInterceptor, packetFilter);
    }

    /**
     * Removes a packet interceptor.
     *
     * @param packetInterceptor the packet interceptor to remove.
     * @deprecated replaced by {@link Connection#removePacketInterceptor(PacketInterceptor)}.
     */
    public void removePacketWriterInterceptor(PacketInterceptor packetInterceptor) {
        removePacketInterceptor(packetInterceptor);
    }

    /**
     * Registers a packet listener with this connection. The listener will be
     * notified of every packet that this connection sends. A packet filter determines
     * which packets will be delivered to the listener. Note that the thread
     * that writes packets will be used to invoke the listeners. Therefore, each
     * packet listener should complete all operations quickly or use a different
     * thread for processing.
     *
     * @param packetListener the packet listener to notify of sent packets.
     * @param packetFilter   the packet filter to use.
     * @deprecated replaced by {@link #addPacketSendingListener(PacketListener, PacketFilter)}.
     */
    public void addPacketWriterListener(PacketListener packetListener, PacketFilter packetFilter) {
//        addPacketSendingListener(packetListener, packetFilter);
    }

    /**
     * Removes a packet listener for sending packets from this connection.
     *
     * @param packetListener the packet listener to remove.
     * @deprecated replaced by {@link #removePacketSendingListener(PacketListener)}.
     */
    public void removePacketWriterListener(PacketListener packetListener) {
        removePacketSendingListener(packetListener);
    }

    private void connectUsingConfiguration(ConnectionConfiguration config, TaskListener taskListener) throws XMPPException {
    	SecurityMode securityMode = config.getSecurityMode();
    	boolean sslUsed = SecurityMode.required == securityMode ? true : false;

        String host = config.getHost();
        int port = config.getPort();
        ProxyInfo proxyInfo = config.getProxyInfo();
        
        RecordtoFile.recordPushInfo(RecordtoFile.REASON_UNKNOWN, RecordtoFile.ACTION_CONN,
				System.currentTimeMillis(), 
				RecordtoFile.ACTIONT_REGISTER, 
				System.currentTimeMillis()+1*10*1000,
				"XMPPConnection_connectUsingConfiguration:host="+host +" port="+port,
				1);
        
        try {
            if (config.getSocketFactory() == null) {
                this.socket = new Socket(host, port);
            }
            else {
                if (sslUsed) {
                	LogUtil.LogOut(3, LOGTAG, "connectUsingConfiguration ssl is needed!");
                	
                	//根据接入点类型
                	if (ProxyInfo.ProxyType.SOCKS == proxyInfo.getProxyType()) {
                		LogUtil.LogOut(4, LOGTAG, "ProxyType.SOCKS ProxyAddress:"+proxyInfo.getProxyAddress() 
                				+", ProxyPort:"+proxyInfo.getProxyPort());
                		
                		//接入点为wap
                		Socket st = config.getSocketFactory().createSocket(host, port);
                		SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
                		
            	    	// create the wrapper over connected socket
            	    	SSLSocket sslSocket = (SSLSocket) ssf.createSocket(st,
            	    			proxyInfo.getProxyAddress(), proxyInfo.getProxyPort(), true);
            	        sslSocket.setUseClientMode(true);
            	        
            	        //SSL握手
            	        sslSocket.startHandshake();
            	        
            	        this.socket = sslSocket;
                	} else {
                		//接入点为net
                		SSLSocket sslSocket = (SSLSocket)SSLCertificateSocketFactory.getDefault().createSocket(host, port);
                		//因为心跳间隔由服务端指定，故此处不设置读等待时间
//                		sslSocket.setSoTimeout(60 * 1000);
                		
                		this.socket = sslSocket;
                	}
                } else {
                	this.socket = config.getSocketFactory().createSocket(host, port);
                }
            }

            //初始化writer和reader
            if (this.socket != null) {
            	LogUtil.LogOut(4, LOGTAG, "connectUsingConfiguration socket is ready!");
            	initConnection(taskListener);
            } else {
            	LogUtil.LogOut(2, LOGTAG, "connectUsingConfiguration socket is failed!");
            }
        }
        catch (UnknownHostException uhe) {
            String errorMessage = "Could not connect to " + host + ":" + port + ".";
            throw new XMPPException(errorMessage, uhe);
        }
        catch (IOException ioe) {
            String errorMessage = "XMPPError connecting to " + host + ":"+ port + ".";
            throw new XMPPException(errorMessage, ioe);
        }
    }

    /**
     * Initializes the connection by creating a packet reader and writer and opening a
     * XMPP stream to the server.
     * @param taskListener 
     *
     * @throws XMPPException if establishing a connection to the server fails.
     */
    private void initConnection(TaskListener taskListener) throws XMPPException {
        boolean isFirstInitialization = packetReader == null || packetWriter == null;
        if (!isFirstInitialization) {
            usingCompression = false;
        }

        // Set the reader and writer instance variables
        initReaderAndWriter();
        LogUtil.LogOut(4, LOGTAG, "initConnection Reader and Writer are created!");

        try {
            if (isFirstInitialization) {
                packetWriter = new PacketWriter(this);
                packetReader = new PacketReader(this);
            }else {
                packetWriter.init();
                packetReader.init();
            }

            // Start the packet writer. This will open a XMPP stream to the server
            packetWriter.startup();
            // Start the packet reader. The startup() method will block until we
            // get an opening stream packet back from server.
            packetReader.startup();
            
            LogUtil.LogOut(3, LOGTAG, "initConnection Reader and Writer are ready!");
            
            taskListener.onSuccess(this);

            /*//提示建立了连接???
            if (isFirstInitialization) {
                // Notify listeners that a new connection has been established
                for (ConnectionCreationListener listener : getConnectionCreationListeners()) {
                    listener.connectionCreated(this);
                }
            } else if (!wasAuthenticated) {
            	//目前不支持授权控制???
//                packetReader.notifyReconnection();
            }*/
        }
        catch (XMPPException ex) {
            throw ex;        // Everything stoppped. Now throw the exception.
        }
    }

	public void resetConnection() {
		LogUtil.LogOut(3, LOGTAG, "resetConnection()...");
		if(null != mTimer) {
  	  		stopTimer();
  		}
		
		// An exception occurred in setting up the connection. Make sure we shut down the
		// readers and writers and close the socket.
		if (packetWriter != null) {
		    try {
		        packetWriter.shutdown();
		    }
		    catch (Throwable ignore) { /* ignore */ }
		    packetWriter = null;
		}
		if (packetReader != null) {
		    try {
		        packetReader.shutdown();
		    }
		    catch (Throwable ignore) { /* ignore */ }
		    packetReader = null;
		}
		if (reader != null) {
		    try {
		        reader.close();
		    }
		    catch (Throwable ignore) { /* ignore */ }
		    reader = null;
		}
		if (writer != null) {
		    try {
		        writer.close();
		    }
		    catch (Throwable ignore) {  /* ignore */}
		    writer = null;
		}
		if (socket != null) {
		    try {
		        socket.close();
		    }
		    catch (Exception e) { /* ignore */ }
		    socket = null;
		}
		this.setWasAuthenticated(authenticated);
		authenticated = false;
		connected = false;
	}

    private void initReaderAndWriter() throws XMPPException {
        try {
            if (!usingCompression) {
            	reader = new DataInputStream(socket.getInputStream());
            	writer = new DataOutputStream(socket.getOutputStream());
            }
            else {
                try {
                    Class<?> zoClass = Class.forName("com.jcraft.jzlib.ZOutputStream");
                    Constructor<?> constructor =
                            zoClass.getConstructor(OutputStream.class, Integer.TYPE);
                    Object out = constructor.newInstance(socket.getOutputStream(), 9);
                    Method method = zoClass.getMethod("setFlushMode", Integer.TYPE);
                    method.invoke(out, 2);
                    writer = new DataOutputStream(socket.getOutputStream());

                    Class<?> ziClass = Class.forName("com.jcraft.jzlib.ZInputStream");
                    constructor = ziClass.getConstructor(InputStream.class);
                    Object in = constructor.newInstance(socket.getInputStream());
                    method = ziClass.getMethod("setFlushMode", Integer.TYPE);
                    method.invoke(in, 2);
                    reader = new DataInputStream(socket.getInputStream());
                }
                catch (Exception e) {
                    e.printStackTrace();
                    reader = new DataInputStream(socket.getInputStream());
                    writer = new DataOutputStream(socket.getOutputStream());
                }
            }
        }catch (IOException ioe) {
            throw new XMPPException("XMPPError establishing connection with server.",ioe);
        }

        // If debugging is enabled, we open a window and write out all network traffic.
//        initDebugger();
    }

    /***********************************************
     * TLS code below
     **********************************************/

    /**
     * Returns true if the connection to the server has successfully negotiated TLS. Once TLS
     * has been negotiatied the connection has been secured.
     *
     * @return true if the connection to the server has successfully negotiated TLS.
     */
    public boolean isUsingTLS() {
        return usingTLS;
    }

    /**
     * Notification message saying that the server supports TLS so confirm the server that we
     * want to secure the connection.
     *
     * @param required true when the server indicates that TLS is required.
     */
    void startTLSReceived(boolean required) {
        if (required && config.getSecurityMode() ==
                ConnectionConfiguration.SecurityMode.disabled) {
            packetReader.notifyConnectionError(new IllegalStateException(
                    "TLS required by server but not allowed by connection configuration"));
            return;
        }

        if (config.getSecurityMode() == ConnectionConfiguration.SecurityMode.disabled) {
            // Do not secure the connection using TLS since TLS was disabled
            return;
        }
        try {
//            writer.write("<starttls xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\"/>");
            writer.flush();
        }
        catch (IOException e) {
            packetReader.notifyConnectionError(e);
        }
    }

    /**
     * Sets the available stream compression methods offered by the server.
     *
     * @param methods compression methods offered by the server.
     */
    void setAvailableCompressionMethods(Collection<String> methods) {
        compressionMethods = methods;
    }

    /**
     * Returns true if the specified compression method was offered by the server.
     *
     * @param method the method to check.
     * @return true if the specified compression method was offered by the server.
     */
    private boolean hasAvailableCompressionMethod(String method) {
        return compressionMethods != null && compressionMethods.contains(method);
    }

    public boolean isUsingCompression() {
        return usingCompression;
    }

    /**
     * Starts using stream compression that will compress network traffic. Traffic can be
     * reduced up to 90%. Therefore, stream compression is ideal when using a slow speed network
     * connection. However, the server and the client will need to use more CPU time in order to
     * un/compress network data so under high load the server performance might be affected.<p>
     * <p/>
     * Stream compression has to have been previously offered by the server. Currently only the
     * zlib method is supported by the client. Stream compression negotiation has to be done
     * before authentication took place.<p>
     * <p/>
     * Note: to use stream compression the smackx.jar file has to be present in the classpath.
     *
     * @return true if stream compression negotiation was successful.
     */
    private boolean useCompression() {
        // If stream compression was offered by the server and we want to use
        // compression then send compression request to the server
        if (authenticated) {
            throw new IllegalStateException("Compression should be negotiated before authentication.");
        }
        try {
            Class.forName("com.jcraft.jzlib.ZOutputStream");
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot use compression. Add smackx.jar to the classpath");
        }
        if (hasAvailableCompressionMethod("zlib")) {
            requestStreamCompression();
            // Wait until compression is being used or a timeout happened
            synchronized (this) {
                try {
                    this.wait(SmackConfiguration.getPacketReplyTimeout() * 5);
                }
                catch (InterruptedException e) {
                    // Ignore.
                }
            }
            return usingCompression;
        }
        return false;
    }

    /**
     * Request the server that we want to start using stream compression. When using TLS
     * then negotiation of stream compression can only happen after TLS was negotiated. If TLS
     * compression is being used the stream compression should not be used.
     */
    private void requestStreamCompression() {
        try {
//            writer.write("<compress xmlns='http://jabber.org/protocol/compress'>");
//            writer.write("<method>zlib</method></compress>");
            writer.flush();
        }
        catch (IOException e) {
            packetReader.notifyConnectionError(e);
        }
    }

    /**
     * Start using stream compression since the server has acknowledged stream compression.
     *
     * @throws Exception if there is an exception starting stream compression.
     */
    void startStreamCompression() throws Exception {
        // Secure the plain connection
        usingCompression = true;
        // Initialize the reader and writer with the new secured version
        initReaderAndWriter();

        // Set the new  writer to use
        packetWriter.setWriter(writer);
        // Send a new opening stream to the server
//        packetWriter.openStream();
        // Notify that compression is being used
        synchronized (this) {
            this.notify();
        }
    }

    /**
     * Notifies the XMPP connection that stream compression was denied so that
     * the connection process can proceed.
     */
    void streamCompressionDenied() {
        synchronized (this) {
            this.notify();
        }
    }

    /**
     * Establishes a connection to the XMPP server and performs an automatic login
     * only if the previous connection state was logged (authenticated). It basically
     * creates and maintains a socket connection to the server.<p>
     * <p/>
     * Listeners will be preserved from a previous connection if the reconnection
     * occurs after an abrupt termination.
     * @param taskListener 
     *
     * @throws XMPPException if an error occurs while trying to establish the connection.
     *      Two possible errors can occur which will be wrapped by an XMPPException --
     *      UnknownHostException (XMPP error code 504), and IOException (XMPP error code
     *      502). The error codes and wrapped exceptions can be used to present more
     *      appropiate error messages to end-users.
     */
    public void connect(TaskListener taskListener){
    	try {
    		// Establish the connection, readers and writers
            connectUsingConfiguration(config, taskListener);
            LogUtil.LogOut(3, LOGTAG, "XMPP connected successfully");
        } catch (XMPPException e) {
        	taskListener.onFail();
        	e.printStackTrace();
        }
        //此功能暂时不需要
        // Automatically makes the login if the user was previouslly connected successfully
        // to the server and the connection was terminated abruptly
//        if (connected && wasAuthenticated) {
//            // Make the login
//            try {
//                if (isAnonymous()) {
//                    // Make the anonymous login
//                    loginAnonymously();
//                }
//                else {
//                    login(config.getUsername(), config.getPassword(),
//                            config.getResource());
//                }
//                packetReader.notifyReconnection();
//            }
//            catch (XMPPException e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * Sets whether the connection has already logged in the server.
     *
     * @param wasAuthenticated true if the connection has already been authenticated.
     */
    private void setWasAuthenticated(boolean wasAuthenticated) {
        if (!this.wasAuthenticated) {
            this.wasAuthenticated = wasAuthenticated;
        }
    }
    
  //=========================================================================
    
    protected void resetTryCount() {
    	mTryCount = 0;
    }
    
    private Object mLock = new Object();	
    protected void stopTimer() {
    	synchronized (mLock) {
    		if(null != mTimer) {
				mTimer.cancel(); //ֹͣtimer
				mTimer = null;
			}
		}
  	}
  	
  	protected void startTimer(long lastSend, int msgId) {
  	  	if(null != mTimer) {
  	  		stopTimer();
  		}
  	  	lastMsgId = msgId;
  	    lastActive = lastSend;
  	    
  	    //更新连接交互的最新时间
  	    SmackConfiguration.setLastConnectedTime(lastActive);
  	    
  	  	synchronized(mLock){
  	  		mTimer = new Timer(true);
	  	  	//每隔-PacketReplyTimeout-检查是否收发包情况
	  	  	mTimer.schedule(new reConnTask(), (long)SmackConfiguration.getPacketReplyTimeout());
  	  	}
  	}
  	  
  	class reConnTask extends TimerTask {
  		public void run() {
  			LogUtil.LogOut(3, LOGTAG, "reConnTask() curMsgId="+lastMsgId);
  			
  			RecordtoFile.recordPushInfo(RecordtoFile.REASON_CONN_TIMEOUT, RecordtoFile.ACTIONT_STATUS_CHECK,
	      				System.currentTimeMillis(), 
	      				RecordtoFile.ACTIONT_UNKNOWN, 1*0*1000,
	      				"XMPPConnection_reConnTask:TimerTask timeout."+" lastMsgId="+lastMsgId,
	      				1);

			mTryCount = 0;
  			for (ConnectionListener listener : getConnectionListeners()) {
  	            try {
  	            	
  	            	RecordtoFile.recordPushInfo(RecordtoFile.REASON_CONN_TIMEOUT, RecordtoFile.ACTION_DISCONN,
  	      				System.currentTimeMillis(), 
  	      				RecordtoFile.ACTIONT_UNKNOWN, 1*0*1000,
  	      				"XMPPConnection_reConnTask:TimerTask timeout and connectionClosedOnError."+
  	      				" lastMsgId="+lastMsgId,
  	      				1);
  	            	
  	            	//通知连接listener：需要重连
  	                listener.connectionClosedOnError(null);
  	            }
  	            catch (Exception e) {
  	                // Catch and print any exception so we can recover from a faulty listener
  	                e.printStackTrace();
  	            }
  	        }
  			LogUtil.LogOut(2, LOGTAG, "reConnTask() connectionClosedOnError will be notify!");
  		}	
  	}
}
