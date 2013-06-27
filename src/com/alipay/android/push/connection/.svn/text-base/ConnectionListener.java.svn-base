package com.alipay.android.push.connection;

/**
 * Interface that allows for implementing classes to listen for connection closing
 * and reconnection events. Listeners are registered with Connection objects.
 *
 * @see Connection#addConnectionListener
 * @see Connection#removeConnectionListener
 * 
 * @author Matt Tucker
 */
public interface ConnectionListener {

    /**
     * Notification that the connection was closed normally or that the reconnection
     * process has been aborted.
     */
    public void connectionClosed();

    /**
     * Notification that the connection was closed due to an exception. When
     * abruptly disconnected it is possible for the connection to try reconnecting
     * to the server.
     *
     * @param e the exception.
     */
    public void connectionClosedOnError(Exception e);
    
    /**
     * The connection will retry to reconnect in the specified number of seconds.
     * 
     * @param seconds remaining seconds before attempting a reconnection.
     */
    public void reconnectingIn(int seconds);
    
    /**
     * The connection has reconnected successfully to the server. Connections will
     * reconnect to the server when the previous socket connection was abruptly closed.
     */
    public void reconnectionSuccessful();
    
    /**
     * An attempt to connect to the server has failed. The connection will keep trying
     * reconnecting to the server in a moment.
     *
     * @param e the exception that caused the reconnection to fail.
     */
    public void reconnectionFailed(Exception e);
}