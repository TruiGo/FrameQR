package com.alipay.android.push.connection.proxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

import com.alipay.android.push.util.LogUtil;

/**
 * Socket factory for socks proxy 
 */
public class SocksProxySocketFactory extends SocketFactory
{
	private static final String LOGTAG = LogUtil.makeLogTag(SocksProxySocketFactory.class);
	
    private ProxyInfo proxy;
    
    public SocksProxySocketFactory(ProxyInfo proxy)
    {
        this.proxy = proxy;
    }

    public Socket createSocket(String host, int port) 
        throws IOException, UnknownHostException
    {
        return socksProxifiedSocket(host,port);
        
    }

    public Socket createSocket(String host ,int port, 
    		InetAddress localHost, int localPort)
        throws IOException, UnknownHostException
    {
        return socksProxifiedSocket(host,port);
    }

    public Socket createSocket(InetAddress host, int port)
        throws IOException
    {
        return socksProxifiedSocket(host.getHostAddress(),port);
    }

    public Socket createSocket( InetAddress address, int port, 
                                InetAddress localAddress, int localPort) 
        throws IOException
    {
        return socksProxifiedSocket(address.getHostAddress(),port);
        
    }
    
    private Socket socksProxifiedSocket(String target, int port) 
        throws IOException
    {
        Socket innerSocket = null;
        BufferedReader din = null;
		BufferedWriter dout = null;
		
		LogUtil.LogOut(4, LOGTAG, "socksProxifiedSocket - target="+target +", port="+port);
		
        String proxy_host = proxy.getProxyAddress();
        int proxy_port = proxy.getProxyPort();
        
        LogUtil.LogOut(4, LOGTAG, "socksProxifiedSocket - proxy_host="+proxy_host +", proxy_port="+proxy_port);
        
        long starTime = System.currentTimeMillis();
        
        try
        {
        	innerSocket=new Socket(proxy_host, proxy_port);
//        	innerSocket.setSoTimeout(120 * 1000);
        	innerSocket.setKeepAlive(true);
        	innerSocket.setTcpNoDelay(true);
            
            din = new BufferedReader(new InputStreamReader(
					innerSocket.getInputStream()));
			dout = new BufferedWriter(new OutputStreamWriter(
					innerSocket.getOutputStream()));
            
			String connectStr = "CONNECT " + target+":"+port+ " HTTP/1.1\r\n"
			+"Host: "+target + "\r\n\r\n";

			LogUtil.LogOut(3, LOGTAG, "connect - write="+connectStr);
			
			dout.write(connectStr);
			dout.flush();
			
			String result = din.readLine();
			LogUtil.LogOut(3, LOGTAG, "connect - result="+result);
			
			String line = "";
			while ((line = din.readLine()) != null) {
				if (line.trim().equals(""))
					break;
				LogUtil.LogOut(5, LOGTAG, "connect - line="+line);
			}

			if (result != null && result.contains("200")) {
				LogUtil.LogOut(4, LOGTAG, 
						"connect - create channel done. And consumed time："
						+ (System.currentTimeMillis() - starTime) / 1000 +" seconds.");
			} else {
				LogUtil.LogOut(2, LOGTAG, "connect - create channel failed.");
				if(innerSocket!=null) {
                	innerSocket.close(); 
                }
				innerSocket = null;
				
				String error = "connect - create channel failed.";
				throw new ProxyException(ProxyInfo.ProxyType.SOCKS, error.toString());
			}

            return innerSocket;
        }
        catch(RuntimeException e)
        {
            throw e;
        }
        catch(Exception e)
        {
            try
            {
                if(innerSocket!=null) {
                	innerSocket.close(); 
                }
            }
            catch(Exception eee)
            {
            }
            throw new ProxyException(ProxyInfo.ProxyType.SOCKS, e.toString());
        }
    }
}
