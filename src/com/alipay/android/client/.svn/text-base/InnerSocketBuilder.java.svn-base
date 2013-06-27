package com.alipay.android.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.util.LogUtil;

public class InnerSocketBuilder {
	
	// Tag for logcat.
	private static final String LOG_TAG = "InnerSocketBuilder";

	private String proxyHost = "10.0.0.172";
	private int proxyPort = 80;
	private String target = "";

	private Socket innerSocket = null;

	private boolean isConnected = false;

	private long starTime = System.currentTimeMillis();
	private final String TAG = "InnerSocketBuilder";
	private final String UA = "biAji's wap channel";

	public InnerSocketBuilder(String target) {
		this("10.0.0.172", 80, target);
	}

	/**
	 * 建立经由代理服务器至目标服务器的连接
	 * 
	 * @param proxyHost
	 *            代理服务器地址
	 * @param proxyPort
	 *            代理服务器端口
	 * @param target
	 *            目标服务器
	 */
	public InnerSocketBuilder(String proxyHost, int proxyPort, String target) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.target = target;
		connect();
	}

	private void connect() {

		// starTime = System.currentTimeMillis();
		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "connect - create channel start!");
		BufferedReader din = null;
		BufferedWriter dout = null;

		try {
			innerSocket = new Socket(proxyHost, proxyPort);
			innerSocket.setKeepAlive(true);
			innerSocket.setSoTimeout(300 * 1000);

			din = new BufferedReader(new InputStreamReader(
					innerSocket.getInputStream()));
			dout = new BufferedWriter(new OutputStreamWriter(
					innerSocket.getOutputStream()));

//			String connectStr = "CONNECT " + target
//			+ " HTTP/1.1\r\n"+"Host: 115.124.16.89" + "\r\n\r\n";

			String connectStr = "CONNECT " + target
			+ " HTTP/1.1\r\n"+"Host: mobilepmgw.alipay.com" + "\r\n\r\n";
			
			
			dout.write(connectStr);
			dout.flush();
			LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "connect - connectStr="+connectStr);

			String result = din.readLine();
			LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "connect - result="+result);
			
			String line = "";
			while ((line = din.readLine()) != null) {
				if (line.trim().equals(""))
					break;
				LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "connect - line="+line);
			}

			if (result != null && result.contains("200")) {
				LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, 
						"connect - create channel done. And consumed time："
						+ (System.currentTimeMillis() - starTime) / 1000 +" seconds.");
				isConnected = true;
			} else {
				LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, 
						"connect - create channel failed.");
			}

		} catch (IOException e) {
			LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "connect - create channel failed."+ e.getLocalizedMessage());
		}
	}

	public Socket getSocket() {
		return innerSocket;
	}

	public boolean isConnected() {
		return this.isConnected;
	}

}
