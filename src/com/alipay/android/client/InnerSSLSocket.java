package com.alipay.android.client;


import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocket;

import android.net.SSLCertificateSocketFactory;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.util.LogUtil;


public class InnerSSLSocket {
	private String TAG = "InnerSSLSocket";
	
//	private static final String CLIENT_AGREEMENT = "TLS";//使用协议
//	private static final String CLIENT_KEY_MANAGER = "X509";//密钥管理器
//	private static final String CLIENT_TRUST_MANAGER = "X509";//
//	private static final String CLIENT_KEY_KEYSTORE = "BKS";//密库，这里用的是BouncyCastle密库
//	private static final String CLIENT_TRUST_KEYSTORE = "BKS";//
//	private static final String ENCONDING = "utf-8";//字符集
	
	private int SERVER_PORT = 443;//端口号
	private String SERVER_IP = "110.75.128.214";//连接IP
	private SSLSocket Client_sslSocket;
	private boolean mIsConnected = false;

	
	public InnerSSLSocket(String targetHost, int targetPort) {
		this.SERVER_IP = targetHost;
		this.SERVER_PORT = targetPort;
		
//		init();
	}

    public void init() throws Exception {
    	LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "connect - create channel start!");
    	
//    	try {
    		Client_sslSocket = (SSLSocket) SSLCertificateSocketFactory.getDefault().createSocket();
    		Client_sslSocket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT), 20 * 1000);
//    		Client_sslSocket = (Socket) new Socket("mobilepay.d132.alipay.net", 80);
    		
	        if (Client_sslSocket != null) {
	        	Client_sslSocket.setSoTimeout(60 * 1000);
	        	
	        	LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "init ok!");
	        	mIsConnected = true;
	        	
	        	LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "init done!");
	        }
//    	} catch (Exception e) {
//			Log.e(TAG, e.getMessage());
//		}
    }
    
    public Socket getSocket() {
		return Client_sslSocket;
	}

	public boolean isConnected() {
		return this.mIsConnected;
	}
 
}
 
