package com.alipay.android.longlink;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.AlipayPushMsgData;
import com.alipay.android.client.InnerSSLSocket;
import com.alipay.android.client.InnerSocketBuilder;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.common.data.UserData;
import com.alipay.android.nfd.NFDUtils;
import com.alipay.android.util.LogUtil;

public class VerifyLinkService extends Service{
	private final String TAG = "VerifyLinkService";
	private Socket mSocket;
	public static final int mConnectWaitTime  = 30;
	private DataInputStream mDataIns;
    private DataOutputStream mDataOuts;
	public int mMaxKeepliveTime  = -1; 
	public int mReConnectTime  = -1; 
  	private static final int mPollingInterval = 30*1000;
  	private int mKeepliveTryCount = 0;
  	private int mKeepliveCanTry = 4;
  	public static final int mCheckNetStateTime  = 10; 
  	private IVerifyClientCallback mClientCallback;
    
    private static final int MSG_CREATE_SOCKET_SUCCESS = 100;
    private static final int MSG_CREATE_SOCKET_FAILURE = 101;
    private static final int MSG_PUSH_SOCKET_LOST = 102;
    private static final int MSG_PUSH_SOCKET_CLOSE = 103;
    
    private static final int MSG_INITIALIZE_SOCKET = 110;
    private static final int MSG_CHECKING_KEEP_LIVE = 111;
	private static final int MSG_GETTING_DATA_FROM_SERVER = 112;
//	private static final int MSG_CONNECTION_CHANGE = 113;
    
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_CREATE_SOCKET_SUCCESS:
				String initData = String.valueOf(ConnectionUtils
						.getConnType(VerifyLinkService.this));
				UserData userData = ((AlipayApplication)getApplication()).getUserData(); 
				if(userData != null){
					initData = initData + userData.getUserId();
					AlipayPushMsgData initMsg = new AlipayPushMsgData(AlipayPushMsgData.MSG_PUSH_INITIALIZE, AlipayPushMsgData.MSG_PUSH_TYPE_REQUEST,
							initData);
					
					try {
						sendPushMsg(initMsg);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					sendHeartBeatPaket(10*1000);
					mKeepliveTryCount = 0;
					LogUtil.logOnlyDebuggable(TAG, "===============>create socket success!!!");
					
					if(mSocketNotifer != null){
						try {
							mSocketNotifer.onSocketCreateSuccess();
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
				break;
			case MSG_CHECKING_KEEP_LIVE:
				if(mSocket != null && mSocket.isConnected()){
					AlipayPushMsgData keepMsg = new AlipayPushMsgData(AlipayPushMsgData.MSG_PUSH_KEEPLIVE, 
    						AlipayPushMsgData.MSG_PUSH_TYPE_REQUEST, "0");
					try {
						sendPushMsg(keepMsg);
        			} catch (IOException e) {
        				e.printStackTrace();
        			}
					LogUtil.logOnlyDebuggable(TAG, "===============>send keeplive heartbeat packet!!!");
				}
				break;
			case MSG_CREATE_SOCKET_FAILURE:
				if(mSocketNotifer != null){
					try {
						mSocketNotifer.onSocketCreateFail();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				
				reconnectToServer();
				break;
			case MSG_INITIALIZE_SOCKET:
				break;
			case MSG_PUSH_SOCKET_CLOSE:
				closeSocket();
				break;
			case MSG_GETTING_DATA_FROM_SERVER:
				AlipayPushMsgData curData = (AlipayPushMsgData) msg.obj;
				// push机制消息处理
				switch (curData.getMsgId()) {
				case AlipayPushMsgData.MSG_PUSH_INITIALIZE:
					// 检查消息类型
					if (curData.getMsgType() == AlipayPushMsgData.MSG_PUSH_TYPE_RESPONSE) {
						// 应答消息
						String response = curData.getData();
						LogUtil.logOnlyDebuggable(TAG, "===============>long link init finished");

						// 提取初始化返回的时间信息
						if (response != null && !response.equals("")) {
							char[] timeArray = { 0, 0 };
							timeArray = response.toCharArray();
							mMaxKeepliveTime = timeArray[0] * 60;
							mReConnectTime = Integer.valueOf(String.valueOf(timeArray[1])).intValue();
							LogUtil.logOnlyDebuggable(TAG, "===============>init socket response,mMaxKeepliveTime = " + mMaxKeepliveTime + ";mReConnectTime = "
									+ mReConnectTime);
						}
					}
					break;
				case AlipayPushMsgData.MSG_PUSH_RECONNECT:
					if (curData.getMsgType() == AlipayPushMsgData.MSG_PUSH_TYPE_REQUEST) {
						LogUtil.logOnlyDebuggable(TAG, "==========> command from server to reconnect socket!!!");
						reconnectSocket();
					}
					break;
				case AlipayPushMsgData.MSG_PUSH_KEEPLIVE:
					if (curData.getMsgType() == AlipayPushMsgData.MSG_PUSH_TYPE_RESPONSE) {
						// 计数复位
						mKeepliveTryCount = 0;
						LogUtil.logOnlyDebuggable(TAG, "==========> keeplive heartbeat response");
					}
					break;
				case AlipayPushMsgData.MSG_PUSH_DATA:
                case AlipayPushMsgData.MSG_PUSH_DATA_RESULT:
                	//先检查是否有业务应用注册；没有则直接丢弃
					int appID = (int) msg.arg1;
					
					try {
						if(mClientCallback != null){
							Bundle bundle=new Bundle();
							bundle.putString("appdata", curData.getData().toString());
							mClientCallback.processPacket(bundle);//回调客户端
						}
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}

					if (appID > 0) {
						// 发送业务数据响应给push server
						// compose response msg for server data
						AlipayPushMsgData dataRespMsg = new AlipayPushMsgData(AlipayPushMsgData.MSG_PUSH_DATA,
								AlipayPushMsgData.MSG_PUSH_TYPE_RESPONSE, "0");
						try {
							sendPushMsg(dataRespMsg);
						} catch (IOException e) {
							e.printStackTrace();
						}
						LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "handleMessage mClient has sent response msg for data packet!");
					}
                  break;
				}
				break;
			case MSG_PUSH_SOCKET_LOST:
				if(mSocket != null && mKeepliveTryCount < mKeepliveCanTry)
					reconnectSocket();
				else
					closeSocket();
              break;
			}
			super.handleMessage(msg);
		}

		private void reconnectToServer() {
			mKeepliveTryCount++;
			if(mKeepliveTryCount < mKeepliveCanTry){
				reconnectSocket();
			}else {
				closeSocket();
			}
		}
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		link2RemoteServer();
		LogUtil.logOnlyDebuggable(TAG, "VerifyLinkService ==========>onCreate");
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		LogUtil.logOnlyDebuggable(TAG, "VerifyLinkService ==========>onBind");
		return new LongLinkBinder();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		LogUtil.logOnlyDebuggable(TAG, "VerifyLinkService ==========>onUnbind");
		return super.onUnbind(intent);
	}
	
	@Override
	public void onDestroy() {
		LogUtil.logOnlyDebuggable(TAG, "VerifyLinkService ==========>onDestroy");
		super.onDestroy();
	}
	
	Runnable heartBeatRunnable;
	/**
	 * 发送心跳包
	 */
	private void sendHeartBeatPaket(final long interval) {
		stopHeartBeatSender();
		
		heartBeatRunnable = new Runnable() {
			@Override
			public void run() {
				Message message = mHandler.obtainMessage(); 
				message.what = MSG_CHECKING_KEEP_LIVE;
				mHandler.sendMessage(message);
				
				mHandler.postDelayed(this,interval);
			}
		};
		
		mHandler.postDelayed(heartBeatRunnable, interval);
	}
	
	private void stopHeartBeatSender(){
		if(heartBeatRunnable != null)
			mHandler.removeCallbacks(heartBeatRunnable);
	}
	
	private void link2RemoteServer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				newSocket();
				try {
					byte[] netData = new byte[2048];
					while (mSocket != null && mSocket.isConnected()) {
						if (mSocket != null && !mSocket.isInputShutdown()) {
							int count;
							count = mDataIns.read(netData);
							if (count > 0) {
								byte[] rawData = new byte[count];
								System.arraycopy(netData, 0, rawData, 0, count);
								if (count >= AlipayPushMsgData.mHeaderLen) { // 有效数据
									handleRecvMsg(rawData);
									// 因为收到数据，重新startTimer；接收数据线程，已经停止了timer
									sendHeartBeatPaket(/*mMaxKeepliveTime > 0 ? mMaxKeepliveTime * 1000 : */mPollingInterval);
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					//close socket error occur when read data 
					closeSocket();
				}
			}
		}).start();
	}
	
	public void sendPushMsg(final AlipayPushMsgData data) throws IOException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(mSocket == null || mDataOuts == null || mDataOuts == null){//还未建立连接发送消息直接返回
					return;
				}else if (mSocket != null && !mSocket.isConnected()) {//未建立连接需要重连
					mHandler.sendEmptyMessage(MSG_PUSH_SOCKET_LOST);
					return;
				} 

				try {
					String rawData = data.getData();
					if (mSocket != null && mSocket.isOutputShutdown()) {
						// 暂时的处理是：发现中断后不进行重连接尝试，后续可以完善
						// 关闭原来的长连接socket
						closeSocket();
					} else {
						byte[] bufferData = getHdrbufforWrite(data);
						byte[] buffer = new byte[AlipayPushMsgData.mHeaderLen + rawData.length()];

						System.arraycopy(bufferData, 0, buffer, 0, AlipayPushMsgData.mHeaderLen);
						System.arraycopy(rawData.getBytes("utf8"), 0, buffer, AlipayPushMsgData.mHeaderLen, rawData.length());
						mDataOuts.write(buffer);
						mDataOuts.flush();
						
						if (data.getMsgId() == AlipayPushMsgData.MSG_PUSH_CLOSE) {
							// 发送给Handler去处理关闭消息
							mHandler.sendEmptyMessage(MSG_PUSH_SOCKET_CLOSE);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					stopHeartBeatSender();
//					mHandler.sendEmptyMessage(MSG_PUSH_SOCKET_LOST);
				}
			}
		}).start();
	}
	
	private void newSocket() {
		try {
	        //建立长连接Socket
			if (BaseHelper.isWapApn(this)) {	//cmwap方式
				InnerSocketBuilder builder = new InnerSocketBuilder(ConnectionUtils.getWapIP(),
						ConnectionUtils.getWapPort(), Constant.getPushURL(this)+":"+String.valueOf(Constant.getPushPort(this)));
				wrapSSLSocket(builder.getSocket());
			} else {
				if (Constant.getPushSsl(this)) {
					InnerSSLSocket innerSSLSocket = new InnerSSLSocket(Constant.getPushURL(this), Constant.getPushPort(this));
					innerSSLSocket.init();
					mSocket = innerSSLSocket.getSocket();
				} else {
					mSocket = new Socket();
					mSocket.connect(new InetSocketAddress(Constant.getPushURL(this), Constant.getPushPort(this)), mConnectWaitTime*1000);
				}
			}
			
			openStreamOnSocket();
			if (mSocket.isConnected()) {
				mHandler.sendEmptyMessage(MSG_CREATE_SOCKET_SUCCESS);
			} else {
				closeSocket();
				mHandler.sendEmptyMessage(MSG_CREATE_SOCKET_FAILURE);
			}
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	closeSocket();
	    	mHandler.sendEmptyMessage(MSG_CREATE_SOCKET_FAILURE);
        }
	}
	
	private void reconnectSocket() {
		closeSocket();
		if(NFDUtils.getInstance(this).hasNetWork()){
			LogUtil.logOnlyDebuggable(TAG, "===============>reconnect to server!!!");
			//重新建立新的长连接socket
			link2RemoteServer();
		}else if(mSocketNotifer != null){
			try {
				mSocketNotifer.onSocketCreateFail();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void handleRecvMsg(byte[] netData) {
    	int msgVersion = netData[0];	//消息协议
    	
    	LogUtil.logOnlyDebuggable(TAG, "netData" + netData != null ? new String(netData):"");
    	switch (msgVersion) {
			case 1:
				int msgId = netData[1];		//消息ID
    			int msgType = netData[2]; 	//消息类型
    			
    			ByteBuffer buf1 = ByteBuffer.allocate(4);
				buf1.put(netData, 3, 4);	//数据域长度
				buf1.flip(); 
				int msgLen = buf1.getInt();
    			
				byte[] data = new byte[msgLen];
				String rawData = null;
				System.arraycopy(netData, AlipayPushMsgData.mHeaderLen, data, 0, msgLen);
				try {
					rawData = new String(data, "utf8");
				} catch (Exception e) {
					e.printStackTrace();
		        } 

    			int appId = -1;
    			if (msgId == AlipayPushMsgData.MSG_PUSH_DATA) {
    				appId = 1;
    			}
    			AlipayPushMsgData recvMsg = new AlipayPushMsgData(msgId, msgType, rawData);
    			
    			LogUtil.logOnlyDebuggable(TAG, "msgId="+msgId +";msgType="+msgType+";rawData="+rawData);
  			  
    			Message msg = new Message();
    			msg.what = MSG_GETTING_DATA_FROM_SERVER;
    			msg.arg1 = appId;
    			msg.obj = recvMsg;
    			mHandler.sendMessage(msg);
				break;
    	}
	}
	
	byte[] getHdrbufforWrite(AlipayPushMsgData msg) {
    	String data = msg.getData();
    	byte[] buffer = new byte[AlipayPushMsgData.mHeaderLen];
    	
    	Integer ver = new Integer(msg.getMsgVersion());
    	buffer[0] = ver.byteValue();
    	
    	Integer id = new Integer(msg.getMsgId());
    	buffer[1] = id.byteValue();
    	
    	Integer type = new Integer(msg.getMsgType());
    	buffer[2] = type.byteValue();
    	
    	byte[] len = intToBytes2(data.length());
    	System.arraycopy(len, 0, buffer, 3, 4);
    	return buffer;
    }
	
	public static byte[] intToBytes2(int n) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (n >> (24 - i * 8));
		}
		return b;
	}
	
	private void wrapSSLSocket(Socket socket) throws IOException  {
    	SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
    	// create the wrapper over connected socket
    	SSLSocket sslSocket = (SSLSocket) ssf.createSocket(socket,
    			ConnectionUtils.getWapIP(), ConnectionUtils.getWapPort(), true);
        sslSocket.setUseClientMode(true);
        sslSocket.startHandshake();
        if(Build.VERSION.SDK_INT < 9){
        	System.setProperty("http.keepAlive", "false");
        }
        
        mSocket = sslSocket;
	}
	
	private void openStreamOnSocket() throws IOException {
		if (mSocket != null && mSocket.isConnected()) {
	        mDataOuts = new DataOutputStream(mSocket.getOutputStream());
		    mDataIns = new DataInputStream(mSocket.getInputStream());
		}
	}
	
	private void sendCloseMsg(){
		LogUtil.logOnlyDebuggable(TAG, "============> sendCloseMsg");
		AlipayPushMsgData closeMsg = new AlipayPushMsgData(AlipayPushMsgData.MSG_PUSH_CLOSE, AlipayPushMsgData.MSG_PUSH_TYPE_REQUEST,"");
		try {
			sendPushMsg(closeMsg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void closeSocket() {
		if (mDataOuts != null) {
			try {
				mDataOuts.close();
				mDataOuts = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (mDataIns != null) {
			try {
				mDataIns.close();
				mDataIns = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (mSocket != null) {
			try {
				if(!mSocket.isClosed())
					mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mSocket = null;
		}
	}
	
	private ISocketResultNotifer mSocketNotifer;
	private class LongLinkBinder extends IVerifyLinkService.Stub{
		@Override
		public boolean initializePushLink(int netType, String clientDealDesc) throws RemoteException {
			return false; 
		}

		@Override
		public boolean closePushLink() throws RemoteException {
			sendCloseMsg();
			return false;
		}

		@Override
		public void registCallback(IVerifyClientCallback callback) throws RemoteException {
			mClientCallback = callback;
		}

		@Override
		public void unRegistCallback(IVerifyClientCallback callback) throws RemoteException {
		}

		/**
		 * 如果socket建立失败，重新建立长连接
		 */
		@Override
		public void reConnect() throws RemoteException {
			if(mSocket == null || !mSocket.isConnected())
				reconnectSocket();
		}
		
		public boolean isSocketConnected(){
			return mSocket != null && mSocket.isConnected();
		}

		@Override
		public void registSocketNotifer(ISocketResultNotifer notifer)
				throws RemoteException {
			mSocketNotifer = notifer;
		}

		@Override
		public void unRegistSocketNofiter() throws RemoteException {
			mSocketNotifer = null;
		}
	}
}