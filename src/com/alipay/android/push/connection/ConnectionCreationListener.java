package com.alipay.android.push.connection;

/**
 * Implementors of this interface will be notified when a new {@link Connection}
 * has been created. The newly created connection will not be actually connected to
 * the server. Use {@link Connection#addConnectionCreationListener(ConnectionCreationListener)}
 * to add new listeners.
 */
public interface ConnectionCreationListener {

    /**
     * Notification that a new connection has been created. The new connection
     * will not yet be connected to the server.
     * 
     * @param connection the newly created connection.
     */
    public void connectionCreated(Connection connection);

}
