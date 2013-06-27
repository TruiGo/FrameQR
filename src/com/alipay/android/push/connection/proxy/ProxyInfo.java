package com.alipay.android.push.connection.proxy;

import javax.net.SocketFactory;

/**
 * Class which stores proxy information such as proxy type, host, port, 
 * authentication etc.
 * 
 * @author Atul Aggarwal
 */

public class ProxyInfo
{
    public static enum ProxyType
    {
        NONE,
        HTTP,
        SOCKS
    }
    
    private String proxyAddress;
    private int proxyPort;
    private String proxyUsername;
    private String proxyPassword;
    private ProxyType proxyType;
    
    public ProxyInfo(ProxyType pType, String pHost, int pPort, String pUser, String pPass)
    {
        this.proxyType = pType;
        this.proxyAddress = pHost;
        this.proxyPort = pPort;
        
        this.proxyUsername = pUser;
        this.proxyPassword = pPass;
    }
    
    
    public static ProxyInfo forNoProxy()
    {
        return new ProxyInfo(ProxyType.NONE, null, 0, null, null);
    }
    
    public static ProxyInfo forDefaultProxy()
    {
        return new ProxyInfo(ProxyType.NONE, null, 0, null, null);
    }
    
    public ProxyType getProxyType()
    {
        return proxyType;
    }
    
    public String getProxyAddress()
    {
        return proxyAddress;
    }
    
    public int getProxyPort()
    {
        return proxyPort;
    }
    
    public SocketFactory getSocketFactory()
    {
        if(proxyType == ProxyType.NONE)
        {
            return new DirectSocketFactory();
        }
//        else if(proxyType == ProxyType.HTTP)
//        {
//            return new HTTPProxySocketFactory(this);
//        }
//        else if(proxyType == ProxyType.SOCKS4)
//        {
//            return new Socks4ProxySocketFactory(this);
//        }
        else if(proxyType == ProxyType.SOCKS)
        {
            return new SocksProxySocketFactory(this);
        }
        else
        {
            return null;
        }
    }
}
