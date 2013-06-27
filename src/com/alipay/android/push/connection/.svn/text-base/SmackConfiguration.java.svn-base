package com.alipay.android.push.connection;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 * Represents the configuration of Smack. The configuration is used for:
 * <ul>
 *      <li> Initializing classes by loading them at start-up.
 *      <li> Getting the current Smack version.
 *      <li> Getting and setting global library behavior, such as the period of time
 *          to wait for replies to packets from the server. Note: setting these values
 *          via the API will override settings in the configuration file.
 * </ul>
 *
 * Configuration settings are stored in META-INF/smack-config.xml (typically inside the
 * smack.jar file).
 */
public final class SmackConfiguration {

    private static final String SMACK_VERSION = "0.1.0";

    //默认的时间属性
    private static int packetReplyTimeout = 15 * 1000;	//单位：毫秒milliseconds
    private static int keepAliveInterval = 30;			//单位：秒
    private static int reconnectInterval = 37 * 60;		//单位：秒
    
    private static boolean updateLBSInfo = false;
    
    //上次连接后初始化成功的时间
    private static long lastConnectedTime = 0;
    
    private static Vector<String> defaultMechs = new Vector<String>();

    private SmackConfiguration() {
    }

    /**
     * Loads the configuration from the smack-config.xml file.<p>
     * 
     * So far this means that:
     * 1) a set of classes will be loaded in order to execute their static init block
     * 2) retrieve and set the current Smack release
     */
    static {
        try {
            // Get an array of class loaders to try loading the providers files from.
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the Smack version information, eg "1.3.0".
     * 
     * @return the Smack version information.
     */
    public static String getVersion() {
        return SMACK_VERSION;
    }

    /**
     * Returns the number of milliseconds to wait for a response from
     * the server. The default value is 5000 ms.
     * 
     * @return the milliseconds to wait for a response from the server
     */
    public static int getPacketReplyTimeout() {
        // The timeout value must be greater than 0 otherwise we will answer the default value
        if (packetReplyTimeout <= 0) {
            packetReplyTimeout = 15*1000;
        }
        return packetReplyTimeout;
    }
    
    public static void setReconnectInterval(int reconInterval){
    	//转化到秒
    	reconnectInterval = reconInterval * 60;
    }
    
    public static int getReconnectInterval(){
    	return reconnectInterval;
    }
    
    public static void setLastConnectedTime(long lastTime) {
		lastConnectedTime = lastTime;
	}
    
    public static long getLastConnectedTime(){
    	return lastConnectedTime;
    }
    
    public static void setLBSFlag(boolean flag) {
    	updateLBSInfo = flag;
	}
    
    public static boolean getLBSFlag(){
    	return updateLBSInfo;
    }

    /**
     * Sets the number of milliseconds to wait for a response from
     * the server.
     * 
     * @param timeout the milliseconds to wait for a response from the server
     */
    public static void setPacketReplyTimeout(int timeout) {
        if (timeout <= 0) {
            throw new IllegalArgumentException();
        }
        packetReplyTimeout = timeout;
    }

    /**
     * Returns the number of milleseconds delay between sending keep-alive
     * requests to the server. The default value is 30000 ms. A value of -1
     * mean no keep-alive requests will be sent to the server.
     *
     * @return the milliseconds to wait between keep-alive requests, or -1 if
     *      no keep-alive should be sent.
     */
    public static int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    /**
     * Sets the number of milleseconds delay between sending keep-alive
     * requests to the server. The default value is 30000 ms. A value of -1
     * mean no keep-alive requests will be sent to the server.
     *
     * @param interval the milliseconds to wait between keep-alive requests,
     *      or -1 if no keep-alive should be sent.
     */
    public static void setKeepAliveInterval(int interval) {
        keepAliveInterval = interval;
    }

    /**
     * Add a SASL mechanism to the list to be used.
     *
     * @param mech the SASL mechanism to be added
     */
    public static void addSaslMech(String mech) {
        if(! defaultMechs.contains(mech) ) {
            defaultMechs.add(mech);
        }
    }

   /**
     * Add a Collection of SASL mechanisms to the list to be used.
     *
     * @param mechs the Collection of SASL mechanisms to be added
     */
    public static void addSaslMechs(Collection<String> mechs) {
        for(String mech : mechs) {
            addSaslMech(mech);
        }
    }

    /**
     * Remove a SASL mechanism from the list to be used.
     *
     * @param mech the SASL mechanism to be removed
     */
    public static void removeSaslMech(String mech) {
        if( defaultMechs.contains(mech) ) {
            defaultMechs.remove(mech);
        }
    }

   /**
     * Remove a Collection of SASL mechanisms to the list to be used.
     *
     * @param mechs the Collection of SASL mechanisms to be removed
     */
    public static void removeSaslMechs(Collection<String> mechs) {
        for(String mech : mechs) {
            removeSaslMech(mech);
        }
    }

    /**
     * Returns the list of SASL mechanisms to be used. If a SASL mechanism is
     * listed here it does not guarantee it will be used. The server may not
     * support it, or it may not be implemented.
     *
     * @return the list of SASL mechanisms to be used.
     */
    public static List<String> getSaslMechs() {
        return defaultMechs;
    }
}
