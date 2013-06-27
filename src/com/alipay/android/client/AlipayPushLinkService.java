package com.alipay.android.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.util.LogUtil;


public class AlipayPushLinkService extends Service {
    private static final String TAG = "AlipayPushLinkService";  
    
    //AlipayPushLink Service state
    public static final int SERVICE_NEW  = -1;  
    public static final int SERVICE_CREATED = 0;
    public static final int SERVICE_INITED  = 1 ;
    public static final int SERVICE_READY   = 1 ; 
    
    private static boolean mIsRetry = false;
    private static boolean mIsConnected = false;
    private boolean mIsBind = false;
    private boolean mIsServiceQuit = false;
    private boolean mIsWapAPN = false;
    private int mCurNetType = -1;
    private boolean mIsReInit = false;
    private String mClientDealID = null;
    
    public static boolean isScreenOn = true;
    
    boolean mNetSataus = false;
    ConnectivityManager connManager = null;

    
    private static Socket mSocket = null;
    private DataInputStream mDataIns = null;
    private DataOutputStream mDataOuts = null;
  
    public static final int mConnectWaitTime  = 30; 

	private boolean mbRunning = false;
	private long mLastRecvTime = 0;
	private boolean mbWaitKeeplive = false;
	private int mKeepliveTryCount = 0;
	private int mKeepliveCanTry = 3;
	
	private int mNetStateTryCount = 0;
	private int mNetStateCanTry = 1;
    public static final int mCheckNetStateTime  = 10; 
	
	public static int mMaxKeepliveTime  = -1; 
	public static int mReConnectTime  = -1; 
	
	// 消息缓冲队列
	Queue<AlipayPushMsgData> msgQueue = new LinkedList<AlipayPushMsgData>();
	
	private boolean hasMoreAcquire()
	{
	  return !msgQueue.isEmpty();
//	  msgQueue.offer(e);
//	  msgQueue.poll();
	}


	private HashMap<String, String> mAppTypeMap = new HashMap<String, String>();
	private static final HashMap<String, String> mNetTypeMap = new HashMap<String, String>();
	
	public  static final String TYPE_WIFI = "wifi";
	public  static final String TYPE_CMWAP = "cmwap";
	public  static final String TYPE_CMNET = "cmnet";
	public  static final String TYPE_UNIWAP = "uniwap";
	public  static final String TYPE_UNINET = "uninet";
	public  static final String TYPE_CTNET = "ctnet";
	public  static final String TYPE_CTWAP = "ctwap";
	public  static final String TYPE_3GNET = "3gnet";
	public  static final String TYPE_3GWAP = "3gwap";
	
	static
	{
		mNetTypeMap.clear();
		mNetTypeMap.put(TYPE_WIFI, "0");
		
		mNetTypeMap.put(TYPE_CMWAP, "1");
		mNetTypeMap.put(TYPE_CMNET, "2");
		
		mNetTypeMap.put(TYPE_UNIWAP, "3");
		mNetTypeMap.put(TYPE_UNINET, "4");
		
		mNetTypeMap.put(TYPE_CTNET, "5");
		mNetTypeMap.put(TYPE_CTWAP, "6");
		
		mNetTypeMap.put(TYPE_3GNET, "7");
		mNetTypeMap.put(TYPE_3GWAP, "8");
	}
	  
    
    private int mPushLinkServiceState = SERVICE_NEW;   //service state machine
    
    
    //broadcast intent to registerd client app
    public static final String PUSH_LINK_FAILED = "com.alipay.pushlink.failed";
    public static final String PUSH_LINK_INITIALIZED = "com.alipay.pushlink.initialized";
    public static final String PUSH_LINK_RECONNECT = "com.alipay.pushlink.reconnect";
    public static final String PUSH_LINK_CONTENT = "com.alipay.pushlink.content";
    
    private BroadcastReceiver mReceiver = null;

    private void registerBroadcastListener() {
        if (mReceiver == null){
            mReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "Received intent: "+ action);
                    if (action != null) {
                    	//处理各个类型的intent
                    	if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                    		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "CONNECTIVITY_CHANGE is received.");
                    		if (checkConnStatus()) {
//                    			mHandler.sendEmptyMessageDelayed(MSG_CONNECTION_CHANGE, mCheckNetStateTime*1000);
                    		}
//                    		mHandler.sendEmptyMessageDelayed(MSG_CONNECTION_CHANGE, 30*1000);
                      } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                          // AND DO WHATEVER YOU NEED TO DO HERE
                          isScreenOn = true;
                      } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                      	  // DO WHATEVER YOU NEED TO DO HERE
                      	  isScreenOn = false;
                      }

                    }
                }
            };
                    
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            iFilter.addAction(Intent.ACTION_SCREEN_ON);
            iFilter.addAction(Intent.ACTION_SCREEN_OFF);
            //
            registerReceiver(mReceiver,iFilter);
        }
    }
    
    
    //Broadcast some interesting intents to client
    private void notifyCmdResults(String what, String value, boolean isAppData){
//        Intent i = new Intent(what);
    	Intent i = new Intent();
        i.setAction(AlipayPushLinkService.PUSH_LINK_CONTENT);
        
        if (isAppData) {
        	Bundle bundle=new Bundle();
					bundle.putString("appdata", value.toString());
					i.putExtras(bundle);
        	//i.putExtra("appData", Integer.parseInt(value.toString()));
        }
        sendBroadcast(i);
    }
    
    //用于通知所有注册的业务应用
    private void notifyPushResults(String what) {
    		Intent i = new Intent(what);
    		sendBroadcast(i);
    }
  
  /******************************** Message Handler ****************************/
	
	  
	private Timer mTimer = null;
  	private static final int mPollingInterval = 30*1000;
  	private static final int mExpRespInterval = 5*1000;
  	private static final int mExceptionInterval = 3*1000;
  	
  	protected void stopTimer() {
      if(null != mTimer) {
      	mTimer.cancel(); //ֹͣtimer
      	mTimer = null;
      }
	}
		
	protected void startTimer(int interval) {
	  	if(null != mTimer) {
	  		stopTimer();
		}
	  	mTimer = new Timer(true);
	  //每隔30s检查是否收发包情况
	  	mTimer.schedule(new timerTask(), (long)interval, (long)interval);
	}
	  
	class timerTask extends TimerTask {
		public void run() {
			Message message = new Message(); 
			message.what = MSG_CHECKING_KEEP_LIVE;
			mHandler.sendMessage(message);
		}	
	}
	
    private static final int MSG_CREATE_SOCKET_SUCCESS = 100;
    private static final int MSG_CREATE_SOCKET_FAILURE = 101;
    private static final int MSG_PUSH_SOCKET_LOST = 102;
    
    private static final int MSG_INITIALIZE_SOCKET = 110;
    private static final int MSG_CHECKING_KEEP_LIVE = 111;
	private static final int MSG_GETTING_DATA_FROM_SERVER = 112;
	private static final int MSG_CONNECTION_CHANGE = 113;

  
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
        	LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "Receive message in mHandler handleMessage(), msg = "+msg.what
            		+ ", mPushLinkServiceState="+mPushLinkServiceState);
            
            if (mIsServiceQuit)
                return; 
                
            switch (msg.what) {
	            case MSG_INITIALIZE_SOCKET:
                    if (mIsConnected && checkConnStatus()) {
                    	switch (mPushLinkServiceState) {
                    		case SERVICE_CREATED:
                    			//初始化push机制
            	      		  	//compose initialized data for server
            	      		  	String initData = String.valueOf(mCurNetType);
            	      		    LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, 
            	      		    		"handleMessage MSG_INITIALIZE_SOCKET-SERVICE_CREATED barcodeToken:"+Constant.STR_BARCODETOKEN);
            	      		  	initData = initData + Constant.STR_BARCODETOKEN;
//            	      		    initData = initData + mClientDealID;
            	      		    LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "handleMessage mClient initData:"+initData);
            	      		  	AlipayPushMsgData initMsg = new AlipayPushMsgData(AlipayPushMsgData.MSG_PUSH_INITIALIZE, 
            	      				AlipayPushMsgData.MSG_PUSH_TYPE_REQUEST, initData);
            	
            	      		  	try {
            	      		  		sendPushMsg(initMsg);
            	      		  	} catch (IOException e) {
            		    	        e.printStackTrace();
            		    	    }
            	      		    LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "handleMessage mClient has sent initialization packet!");
            		              
            		          	//发送出第一条消息后，启动timer
            		            startTimer(10*1000);
                    			break;
                    		case SERVICE_INITED:
                    			//原来的连接仍然有效，则不做特殊处理
                    			break;
                    		default:
                    			break;
                    	}
                    } else {
                    	mHandler.sendEmptyMessageDelayed(MSG_CONNECTION_CHANGE, mCheckNetStateTime*1000);
                    }
		        break; 
          
            	case MSG_CHECKING_KEEP_LIVE:
					//已经超时没有收到来自服务端的数据
					//考虑到心跳包和业务数据消息夹杂的情况，不能依赖收发时间来判定
            		if (mbWaitKeeplive && mPushLinkServiceState == SERVICE_INITED) {  //心跳包响应超时
            			//停止原来的timer
            			stopTimer();
    	      	
            			//是否超过尝试发送限制
            			if (mKeepliveCanTry > mKeepliveTryCount) {
            				//可以继续尝试KeepLive
    	      		
            				//判断tcp socket是否还连接着
            				if (mIsConnected) {
								//compose initialized data for server
            					AlipayPushMsgData keepMsg = new AlipayPushMsgData(AlipayPushMsgData.MSG_PUSH_KEEPLIVE, 
            							AlipayPushMsgData.MSG_PUSH_TYPE_REQUEST, "0");
								try {
									sendPushMsg(keepMsg);
		            			} catch (IOException e) {
		            				e.printStackTrace();
		            			}
		            			LogUtil.logMsg(Constant.LOG_LEVEL_WARNING, TAG, "handleMessage mClient has sent Exception keepLive packet!");
		            			mbWaitKeeplive = true;
		            			mKeepliveTryCount++;
    	        
								//启动数据收发异常的timer——缩小发送keepLive包的间隔
								startTimer(mExceptionInterval);
							} else {
								//直接能判知socket已经中断
								//msg通知服务本身，长连接失效
								LogUtil.logMsg(Constant.LOG_LEVEL_ERROR, TAG, "handleMessage mClient MSG_PUSH_SOCKET_LOST -- connect lost!");
								mHandler.sendEmptyMessage(MSG_PUSH_SOCKET_LOST);
							}
						} else {
							//已经超过尝试限制
							//msg通知服务本身，长连接失效
							LogUtil.logMsg(Constant.LOG_LEVEL_ERROR, TAG, "handleMessage mClient MSG_PUSH_SOCKET_LOST -- reach to try limit!");
							mHandler.sendEmptyMessage(MSG_PUSH_SOCKET_LOST);
						}
					} else {
						if (mPushLinkServiceState != SERVICE_INITED){
							// 初始化失败，服务空转
							// 业务应用启动时，查询push服务状态；不符合则退出应用，或者再尝试一次
							
							//停止原来的timer
	            			stopTimer();
	            			
	            			if(checkConnStatus() && mIsConnected) {
	            				closeSocket();
	            				//需要重新建立和初始化长连接
	            				mHandler.sendEmptyMessageDelayed(MSG_CONNECTION_CHANGE, mCheckNetStateTime*1000);
	            			} else {
	            				if (mIsReInit == true) {
	            					//通知应用初始化push失败
		            				notifyPushResults(PUSH_LINK_FAILED);
									mAppTypeMap.clear();

		            				mIsReInit = false;
	            				}
	            			}
						} else {
							//正常
							//compose initialized data for server
							AlipayPushMsgData keepMsg = new AlipayPushMsgData(AlipayPushMsgData.MSG_PUSH_KEEPLIVE, 
	        						AlipayPushMsgData.MSG_PUSH_TYPE_REQUEST, "0");
							try {
								sendPushMsg(keepMsg);
	            			} catch (IOException e) {
	            				e.printStackTrace();
	            			}
	            			LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "handleMessage mClient has sent normal keepLive packet!");
	            			mbWaitKeeplive = true;
	    	        
	            			//原timer继续有效
						}
					}
				break;
							
				case MSG_GETTING_DATA_FROM_SERVER:
					AlipayPushMsgData curData = (AlipayPushMsgData)msg.obj;
            
					//push机制消息处理
					switch (curData.getMsgId()) {
						case AlipayPushMsgData.MSG_PUSH_INITIALIZE:
							//检查消息类型
							if (curData.getMsgType() == AlipayPushMsgData.MSG_PUSH_TYPE_RESPONSE) {
								//应答消息
								String response = curData.getData();
								LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "handleMessage mClient has gotten INITIALIZE response! response:"+response);
                  	
								//判断响应结果
								if (true) {
									//初始化请求成功
									mPushLinkServiceState = SERVICE_INITED;
									//仅当初始化成功后，才复位设置为0
									mNetStateTryCount = 0;
									//通知应用
									notifyPushResults(PUSH_LINK_INITIALIZED);
									
									mAppTypeMap.clear();
									
									//提取初始化返回的时间信息
									if (response != null && !response.equals("")) {
										char[] timeArray = {0, 0};
										timeArray = response.toCharArray();
										
										LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "handleMessage INITIALIZE info: mMaxKeepliveTime="+timeArray[0] +", mReConnectTime="+timeArray[1]);
										mMaxKeepliveTime  = timeArray[0] * 60; 
										mReConnectTime  = Integer.valueOf(String.valueOf(timeArray[1])).intValue(); 
										LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "handleMessage INITIALIZE info: mMaxKeepliveTime="+mMaxKeepliveTime +", mReConnectTime="+mReConnectTime);
									}
									
								} else {
									//初始化请求失败
								}
                  	
								//通知应用
								//notifyCmdResults(null, null, false);
								//将Message从Message Queue中删除
								mHandler.removeMessages(MSG_INITIALIZE_SOCKET);
							}
						break;
                
		                case AlipayPushMsgData.MSG_PUSH_RECONNECT:
		                  if (curData.getMsgType() == AlipayPushMsgData.MSG_PUSH_TYPE_REQUEST) {
		                  	String reason = curData.getData();
		                  	
		                  	//通知应用
		                  	notifyPushResults(PUSH_LINK_RECONNECT);
		                  	
		                  	reconnectSocket();
		                  }
		                break;
		                
		                case AlipayPushMsgData.MSG_PUSH_KEEPLIVE:
		                  if (curData.getMsgType() == AlipayPushMsgData.MSG_PUSH_TYPE_RESPONSE) {
		                  	//收到应答消息
		                  	mbWaitKeeplive = false;
		                  	
		                  	LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "handleMessage mClient has gotten KEEPLIVE response!");
		                  	
		                  	//计数复位
		                  	mKeepliveTryCount = 0;
		                  	
		                  	//timer已经更新，继续等待
		                  }
		                break;
		                
		                case AlipayPushMsgData.MSG_PUSH_DATA:
		                case AlipayPushMsgData.MSG_PUSH_DATA_RESULT:
		                  //先检查是否有业务应用注册；没有则直接丢弃
		                  int appID = (int)msg.arg1;
		                  LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "handleMessage mClient got app("+appID+") packet!");
		                  
		                  if (AlipayPushLinkService.this.hasRegApp(appID)) {
		                      //通知应用
		                      notifyCmdResults(getRegAppAction(appID), curData.getData(), true);
		                      
		                      if (appID > 0) {
			                      //发送业务数据响应给push server
				                  //compose response msg for server data
			                      AlipayPushMsgData dataRespMsg = new AlipayPushMsgData(AlipayPushMsgData.MSG_PUSH_DATA, 
			                    		  AlipayPushMsgData.MSG_PUSH_TYPE_RESPONSE, "0");
			                      try {
			                    	  sendPushMsg(dataRespMsg);
			                      } catch (IOException e) {
			                    	  e.printStackTrace();
			                      }
			                      LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "handleMessage mClient has sent response msg for data packet!");
		                      }
		                  }
		                  break;
		            }
			    break;
				
            	case MSG_CREATE_SOCKET_SUCCESS:
            		mPushLinkServiceState = SERVICE_CREATED;
            		mIsRetry = false;
            	      
            	    //开始初始化push长连接
            	    mHandler.sendEmptyMessage(MSG_INITIALIZE_SOCKET);
                break;
                
            	case MSG_CREATE_SOCKET_FAILURE:
            	    mPushLinkServiceState = SERVICE_NEW;
            	    
            	    LogUtil.logMsg(Constant.LOG_LEVEL_ERROR, TAG, "handleMessage MSG_CREATE_SOCKET_FAILURE!");
            	    //通知应用，长连接中断，应退出应用
                	notifyPushResults(PUSH_LINK_FAILED);
                	
            	    if (mCurNetType == 1 || mCurNetType == 3 || mCurNetType == 6 || mCurNetType == 8 ) {
            	    	//目前暂不支持wap网络情况
//            	    	mHandler.sendEmptyMessageDelayed(MSG_CONNECTION_CHANGE, 2*1000);
            	    } else {
	            	    //等待mCheckNetStateTime时间后再继续尝试
//	                	mHandler.sendEmptyMessageDelayed(MSG_CONNECTION_CHANGE, mCheckNetStateTime*1000);
            	    }
                break;
                
            	case MSG_CONNECTION_CHANGE:
            		int newNetType = checkConnectType();
            		//原来的连接仍然有效，则不做特殊处理
                    if (mIsConnected && checkConnStatus() && 
                    		mPushLinkServiceState != SERVICE_INITED) {
                    	LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "MSG_CONNECTION_CHANGE send MSG_INITIALIZE_SOCKET!");
                    	mHandler.sendEmptyMessage(MSG_INITIALIZE_SOCKET);
                    } else {
                    	mNetStateTryCount++;
                    	LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "handleMessage MSG_CONNECTION_CHANGE mNetStateTryCount="+mNetStateTryCount);

                    	if (mNetStateTryCount > mNetStateCanTry) {
                    		//关闭原来的长连接socket
                        	closeSocket();
                        	mPushLinkServiceState = SERVICE_NEW;
                        	mAppTypeMap.clear();
                        	
                        	LogUtil.logMsg(Constant.LOG_LEVEL_ERROR, TAG, "handleMessage MSG_CONNECTION_CHANGE exceed acceptable times!");
                            //通知应用，长连接中断，无法恢复，应退出应用
                        	notifyPushResults(PUSH_LINK_FAILED);
                    	} else {
	                        if (checkConnStatus()) {
	                      	    //网络连接类型变化，原来的连接已经中断
	                            //需要额外判断当前网络是否可以连接
	                        	mPushLinkServiceState = SERVICE_NEW;
	                        	reconnectSocket();
	                        } else {
	                        	//等待mCheckNetStateTime时间后再继续尝试
	                        	mHandler.sendEmptyMessageDelayed(MSG_CONNECTION_CHANGE, mCheckNetStateTime*1000);
	                        }
                    	}
                    }
                break;
                
                case MSG_PUSH_SOCKET_LOST:
                	//原来的连接已经中断
            		closeSocket();
                	mPushLinkServiceState = SERVICE_NEW;
                	mAppTypeMap.clear();

    				if (mIsRetry) {
	                	//不论是否当前网络状况如何，都进行重新建立和初始化长连接的尝试
	            		mHandler.sendEmptyMessageDelayed(MSG_CONNECTION_CHANGE, mCheckNetStateTime*1000);
    				} else {
    					//通知应用，长连接中断，应退出应用
                    	notifyPushResults(PUSH_LINK_FAILED);
    				}

                  break;
            }
        }
    };
    
    public void sendPushMsg(AlipayPushMsgData data) throws IOException {
      String rawData = data.getData();
      LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "1 sendPushMsg mClient rawData:"+rawData);
      
      Runnable task = new pushSendRunnable(data);
      new Thread(task).start();
   
      LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "1 sendPushMsg done!");
	}

    public class pushSendRunnable implements Runnable {
	    private AlipayPushMsgData msg;

	    public pushSendRunnable() {
	      super();
	    }
	
	    public pushSendRunnable(AlipayPushMsgData msg){
	      this.msg = msg;
	    }
	
	    public void run() {
	    	if (!mIsConnected) {
	    		//连接中断，直接return
	    		return;
	    	}
	    	
	    	try {
	    		String rawData = msg.getData();
	    		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "2 sendPushMsg mClient rawData:"+rawData);
		        
		  	  if (mSocket != null && mSocket.isOutputShutdown()) {
		  		  //暂时的处理是：发现中断后不进行重连接尝试，后续可以完善
		  		  LogUtil.logMsg(Constant.LOG_LEVEL_ERROR, TAG, "sendPushMsg mSocket OutputStream is Shutdown!");
		  		  //关闭原来的长连接socket
		  		  closeSocket();
		  		  mPushLinkServiceState = SERVICE_NEW;
		      	
		  		  //通知应用，长连接中断，无法恢复，应退出应用
		  		  notifyPushResults(PUSH_LINK_FAILED);
		  	  } else {
		  		  byte[] bufferData = getHdrbufforWrite(msg);
		  		  byte[] buffer = new byte[AlipayPushMsgData.mHeaderLen + rawData.length()];
		  		  
		  		  System.arraycopy(bufferData, 0, buffer, 0, AlipayPushMsgData.mHeaderLen);
		  		  System.arraycopy(rawData.getBytes("utf8"), 0, buffer, AlipayPushMsgData.mHeaderLen, rawData.length());
		  		  mDataOuts.write(buffer);
		  		  
		  		  if (msg.getMsgId() == AlipayPushMsgData.MSG_PUSH_CLOSE) {
		  			  //发送给Handler去处理关闭消息
		  			  mHandler.sendEmptyMessage(MSG_PUSH_SOCKET_LOST);
		  		  }
		  		  
//		  		  mDataOuts.writeByte(msg.getMsgVersion());
//		  		  mDataOuts.writeByte(msg.getMsgId());
//		  		  mDataOuts.writeByte(msg.getMsgType());
//		  		  mDataOuts.writeInt(rawData.length());
//		  		  Log.d(TAG, "sendPushMsg mClient rawDatalength:"+rawData.length());
//		  	
//		  		  long tExdData = 1234567890;
//		  		  mDataOuts.writeLong(tExdData);
//		  		  
//		  		  mDataOuts.write(rawData.getBytes("utf8"));
//		  		  Log.d(TAG, "sendPushMsg will call flush!");
		  	 
		  		  mDataOuts.flush();
		  	  }	  
		  	  LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "2 sendPushMsg done!");
		  	  
            } catch (IOException e) {
          	  e.printStackTrace();
          	  
          	  mIsConnected = false;
          	  mHandler.sendEmptyMessage(MSG_PUSH_SOCKET_LOST);
            }
	    }
    }
    
    /**
     * 将int类型的数据转换为byte数组
     * 原理：将int数据中的四个byte取出，分别存储
     * @param n int数据
     * @return 生成的byte数组
     */
    public static byte[] intToBytes2(int n){
     byte[] b = new byte[4];
     for(int i = 0;i < 4;i++){
      b[i] = (byte)(n >> (24 - i * 8)); 
     }
     return b;
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
    	
    	//8个字节的扩展域
    	
    	return buffer;
    	
//    	ByteBuffer buf = ByteBuffer.allocate(AlipayPushMsgData.mHeaderLen);
//    	
//    	Integer ver = new Integer(msg.getMsgVersion());
//    	buf.put(ver.byteValue());
//    	buf.flip(); 
//
//    	Integer id = new Integer(msg.getMsgId());
//    	buf.put(id.byteValue());
//    	buf.flip(); 
//
//    	Integer type = new Integer(msg.getMsgType());
//    	buf.put(type.byteValue());
//    	buf.flip(); 
//
//    	int bufferLen =  data.length();
//    	buf.putInt(bufferLen);
//    	buf.flip(); 
//    	
//    	long tExdData = 1234567890;
//    	buf.putLong(tExdData);
//    	buf.flip();
//
//    	return buf.array();
    }
    
    private Object Integer(int msgVersion) {
		// TODO Auto-generated method stub
		return null;
	}


	private static String wap_proxy_ip = "";
	private static int wap_proxy_port = -1;
    
	private void openStreamOnSocket() throws IOException {
		if (mSocket != null && mIsConnected) {
			LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "pushReceiveWatchDog: will get DataOutputStream!");
	        mDataOuts = new DataOutputStream(mSocket.getOutputStream());
	        if (mDataOuts == null) {
	        	LogUtil.logMsg(Constant.LOG_LEVEL_ERROR, TAG, "pushReceiveWatchDog: fail to get DataOutputStream!");
	        }
	      
	        LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "pushReceiveWatchDog: will get DataInputStream!");
		    mDataIns = new DataInputStream(mSocket.getInputStream());
		    if (mDataIns == null) {
		    	LogUtil.logMsg(Constant.LOG_LEVEL_ERROR, TAG, "pushReceiveWatchDog: fail to get DataInputStream!");
		    }
		}
		
		LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "openStreamOnSocket done! mIsConnected="+mIsConnected);
	}
	
	private void newSocket() {
		try {
			LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "pushReceiveWatchDog: will create Socket!");
	        //建立长连接Socket
			if (mIsWapAPN == true) {	//cmwap方式
				InnerSocketBuilder builder = new InnerSocketBuilder(wap_proxy_ip,
						wap_proxy_port, Constant.getPushURL(this)+":"+String.valueOf(Constant.getPushPort(this)));
//				mSocket = builder.getSocket();
				
				warpperSSLSocket(builder.getSocket());
				
//				mIsConnected = builder.isConnected();
			} else {
				LogUtil.logOnlyDebuggable("xxxx", Constant.getPushURL(this) + ":"+ Constant.getPushPort(this)+ ";ssl "+ Constant.getPushSsl(this));
				if (Constant.getPushSsl(this)) {
					InnerSSLSocket innerSSLSocket = new InnerSSLSocket(Constant.getPushURL(this), Constant.getPushPort(this));
					innerSSLSocket.init();
					mSocket = innerSSLSocket.getSocket();
					mIsConnected = innerSSLSocket.isConnected();
					LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "pushReceiveWatchDog: InnerSSLSocket mIsConnected="+mIsConnected);
				} else {
					mSocket = new Socket();
					mSocket.connect(new InetSocketAddress(Constant.getPushURL(this), Constant.getPushPort(this)), mConnectWaitTime*1000);
					mIsConnected = true;
				}

//				mNetStateTryCount = 0;
				openStreamOnSocket();
				
				LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "pushReceiveWatchDog: create Socket ok! mIsConnected="+mIsConnected);
				
				if (mIsConnected) {
		        	mHandler.sendEmptyMessage(MSG_CREATE_SOCKET_SUCCESS);
		        } else {
		        	mHandler.sendEmptyMessage(MSG_CREATE_SOCKET_FAILURE);
		        }
			}
	        
//	        mSocket.setSoTimeout(60*1000);
			
			if (mIsWapAPN == true) {	//cmwap方式
//				String test = new String(rawData, "utf8");
//				mDataOuts.write(getHttpConnectReq().getBytes("utf8"));
//				mDataOuts.write(getHttpConnectReq().getBytes());
//				mDataOuts.flush();
			}
	        
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	LogUtil.logMsg(Constant.LOG_LEVEL_ERROR, TAG, "pushReceiveWatchDog: create Socket failed!");
	    	mIsConnected = false;
	    	mHandler.sendEmptyMessage(MSG_CREATE_SOCKET_FAILURE);
        }
	}
	
    private class pushReceiveWatchDog implements Runnable{
    	public void run() {
    		//如果以下部分的工作，如果比较花费时间，可以放到thread pushReceiveWatchDog的循环外区做。
    		newSocket();

	        boolean isException = false;
    		byte[] netData = new byte[1024];
	        LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "pushReceiveWatchDog: will run recv while loop!");
	        
	        while(!mIsServiceQuit && mIsConnected){
	        	try {
	        		if (mSocket != null && !mSocket.isInputShutdown()) {
	        			//if (mCurNetType == 1) {	//cmwap方式
	        				//无效数据，读出丢弃，以免被阻塞
//	            			mDataIns.read(netData);
//	            			String test = new String(rawData, "utf8");
//	            			Log.d(TAG, "pushReceiveWatchDog: recv rawData："+netData.toString());
//	            			String test = new String(netData, "utf8");
//	            			Log.d(TAG, "pushReceiveWatchDog: recv rawData："+test);
	        			//} else {
	        			{
	        				int count = mDataIns.read(netData);
	        				if (count > 0) {
	        					byte[] rawData = new byte[count];
	        					System.arraycopy(netData, 0, rawData, 0, count);
        					
	        					LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "pushReceiveWatchDog: in while loop!  count="+count);
	        					stopTimer();
	        					
	        					if(count >= AlipayPushMsgData.mHeaderLen) {	//有效数据
			        				
			        				handleRecvMsg(rawData);
					    			  
				            		//因为收到数据，重新startTimer；接收数据线程，已经停止了timer
								    startTimer(mPollingInterval);
	        					} else {
	        						LogUtil.logMsg(Constant.LOG_LEVEL_WARNING, TAG, "pushReceiveWatchDog: drop rawData!");
	        					}
	        				}
	        			
/*		        			int count = mDataIns.available();
		        			if (count > 0) {
		        				LogUtil.logOnlyDebuggable(TAG, "pushReceiveWatchDog: will run recv while loop!  count="+count);
	
		        				if(count >= AlipayPushMsgData.mHeaderLen) {	//有效数据
			        				stopTimer();
		
			        				handleRecvMsg();
				    			  
				            		//因为收到数据，重新startTimer；接收数据线程，已经停止了timer
								    startTimer(mPollingInterval);
		        				} else {
		        					//无效数据，读出丢弃，以免被阻塞
		        					byte[] rawData = new byte[1024];
			            			mDataIns.read(rawData);
			            			
			            			String test = new String(rawData, "utf8");
			            			Log.d(TAG, "pushReceiveWatchDog: recv rawData："+test);
		        				}
			            	 } else {
			            		 //睡眠一下
			            		 Thread.sleep(500);
			            	 }
*/
	        			}/////
	            	 } else {
	            		 //暂时的处理是：发现中断后不进行重连接尝试，后续可以完善
	            		 LogUtil.logMsg(Constant.LOG_LEVEL_ERROR, TAG, "pushReceiveWatchDog mSocket InputStream is shutdown!");
		           		 //关闭原来的长连接socket
		           		 closeSocket();
		           		 mPushLinkServiceState = SERVICE_NEW;
		           		 stopTimer();

//		           		 //通知应用，长连接中断，无法恢复，应退出应用
//		           		 notifyPushResults(PUSH_LINK_FAILED);
	            	 }

	              } catch (SocketTimeoutException e) {
	            	  e.printStackTrace();
	            	  LogUtil.logMsg(Constant.LOG_LEVEL_ERROR, TAG, "pushReceiveWatchDog: SocketTimeoutException!");
	            	  isException = true;
	              } catch (SocketException e) {
	            	  e.printStackTrace();
	            	  LogUtil.logMsg(Constant.LOG_LEVEL_ERROR, TAG, "pushReceiveWatchDog: SocketException!");
	            	  isException = true;
	              } catch (Exception e) {
	                  e.printStackTrace();
	                  LogUtil.logMsg(Constant.LOG_LEVEL_ERROR, TAG, 
	                		  "pushReceiveWatchDog: read data from Socket Exception!");
	                  isException = true;
	              } finally {
	            	  if (mIsConnected && isException) {
	                	  AlipayPushLinkService.this.closeSocket();
	                	  stopTimer();
	                	  
	                	  mHandler.sendEmptyMessage(MSG_PUSH_SOCKET_LOST);
	                  }
	              }
	        }
	        
//          try {
//	          if (mSocket != null) {
//              AlipayPushLinkService.this.closeSocket();
//	          }
//          } catch (Exception e) {
//              e.printStackTrace();
//          } 
          
        }
    }

    private void handleRecvMsg(byte[] netData) {
    	int msgVersion = netData[0];	//消息协议
    	
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
					LogUtil.logMsg(Constant.LOG_LEVEL_WARNING, TAG, "handleRecvMsg handle rawdata error.");
		        } 

    			int appId = -1;
    			if (msgId == AlipayPushMsgData.MSG_PUSH_DATA) {
//    				String tId = rawData.substring(0, 1);
//    				
//    	    		appId = Integer.valueOf(tId).intValue();
//    	    		rawData = rawData.substring(1, msgLen);
    				appId = 1;
    			}
    			
    			LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "handleRecvMsg got msgLen:"+msgLen +", msgId:"+msgId  +", msgType:"+msgType);  
    			
    			AlipayPushMsgData recvMsg = new AlipayPushMsgData(msgId, msgType, rawData);
  			  
    			Message msg = new Message();
    			msg.what = MSG_GETTING_DATA_FROM_SERVER;
    			msg.arg1 = appId;
    			msg.obj = recvMsg;
    			mHandler.sendMessage(msg);
    			
    			LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "pushReceiveWatchDog sendMessage for received data!");
    			
				break;
			default:
				break;
    	}
    	
    }
    
    private void handleRecvMsg() {
    	try {
	    	int msgVersion = mDataIns.readByte(); 	//消息协议
	    	switch (msgVersion) {
	    		case 1:
	    			int msgId = mDataIns.readByte();  		//消息ID
	    			int msgType = mDataIns.readByte();   	//消息类型
	    			int msgLen = mDataIns.readInt(); 		//长度 
	    			long msgExtend = mDataIns.readLong();	//头部扩展域
	    			
	    			byte[] msgData = null;
	    			String rawData = null;
	    			LogUtil.logAnyTime(TAG, "pushReceiveWatchDog 33");
	    			if (msgLen > 0) {
	    				msgData = new byte[msgLen];
	    				mDataIns.read(msgData);
	    	
	    	    		rawData = new String(msgData, "utf8");	//得到此业务应用的JSON数据
	    	    		
	    	    		LogUtil.logOnlyDebuggable(TAG, "pushReceiveWatchDog rawData:"+rawData);
	    			}
	    			
	    			int appId = -1;
	    			if (msgId == AlipayPushMsgData.MSG_PUSH_DATA) {
//	    				String tId = rawData.substring(0, 1);
//	    				
//	    	    		appId = Integer.valueOf(tId).intValue();
//	    	    		rawData = rawData.substring(1, msgLen);
	    				appId = 1;
	    			}
	    			LogUtil.logOnlyDebuggable(TAG, "pushReceiveWatchDog got msgLen:"+msgLen +", msgId:"+msgId 
	    					+", msgType:"+msgType +", appId:"+appId);  
	    			LogUtil.logOnlyDebuggable(TAG, "pushReceiveWatchDog rawData:"+rawData);
	    	
	    			
	    			AlipayPushMsgData recvMsg = new AlipayPushMsgData(msgId, msgType, rawData);
	    			  
	    			Message msg = new Message();
	    			msg.what = MSG_GETTING_DATA_FROM_SERVER;
	    			msg.arg1 = appId;
	    			msg.obj = recvMsg;
	    			mHandler.sendMessage(msg);
	    			
	    			LogUtil.logAnyTime(TAG, "pushReceiveWatchDog sendMessage for received data!");
	    		break;
	    		
	    		default:
	    		break;
	    	}
    	} catch (SocketTimeoutException e) {
      	  e.printStackTrace();
      	  LogUtil.logAnyTime(TAG, "pushReceiveWatchDog: SocketTimeoutException!");
      	  
        } catch (SocketException e) {
      	  e.printStackTrace();
      	  LogUtil.logAnyTime(TAG, "pushReceiveWatchDog: SocketException!");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.logAnyTime(TAG, "pushReceiveWatchDog: read data from Socket Exception!");
            AlipayPushLinkService.this.closeSocket();
        } 
    }
    
    private boolean checkConnStatus() {
    	connManager.getActiveNetworkInfo();

    	mNetSataus = false;
    	if (connManager.getActiveNetworkInfo() != null) {
        	mNetSataus = connManager.getActiveNetworkInfo().isAvailable();
        }
        
    	LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "checkConnStatus: mNetSataus="+mNetSataus);
        return mNetSataus;
    }
    
    private void warpperSSLSocket(Socket st) {
    	try {
	    	SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
	    	// create the wrapper over connected socket
	    	SSLSocket sslSocket = (SSLSocket) ssf.createSocket(st,
	    			wap_proxy_ip, wap_proxy_port, true);
	        sslSocket.setUseClientMode(true);
	        sslSocket.startHandshake();
//	        sslSocket.addHandshakeCompletedListener(new HandshakeCB());
	        
	        mSocket = sslSocket;
	        mIsConnected = true;
        	
        	if (mIsConnected) {
	        	mHandler.sendEmptyMessage(MSG_CREATE_SOCKET_SUCCESS);

	        	try {
	        		openStreamOnSocket();
	        	} catch (Exception e) {
	    	    	e.printStackTrace();
	    	    	LogUtil.logMsg(Constant.LOG_LEVEL_ERROR, TAG, "HandshakeCB - handshakeCompleted error.");
	    	    	mIsConnected = false;
	    	    	mHandler.sendEmptyMessage(MSG_CREATE_SOCKET_FAILURE);
	            }
        	}

    	} catch (IOException e) {
    		LogUtil.logMsg(Constant.LOG_LEVEL_ERROR, TAG, 
    				"warpperSSLSocket - create the wrapper over connected socket failed.");
    		e.printStackTrace();
        }
    }
    
    public class HandshakeCB implements HandshakeCompletedListener {
    	HandshakeCB() {
            super();
        }
    	
        public void handshakeCompleted(HandshakeCompletedEvent event) {
        	LogUtil.logMsg(Constant.LOG_LEVEL_WARNING, TAG, 
        			"HandshakeCB - handshakeCompleted.");
        	mSocket = event.getSocket();
//        	mIsConnected = builder.isConnected();
        	mIsConnected = true;
        	
        	if (mIsConnected) {
	        	mHandler.sendEmptyMessage(MSG_CREATE_SOCKET_SUCCESS);

	        	try {
	        		openStreamOnSocket();
	        	} catch (Exception e) {
	    	    	e.printStackTrace();
	    	    	LogUtil.logMsg(Constant.LOG_LEVEL_ERROR, TAG, "HandshakeCB - handshakeCompleted error.");
	    	    	mIsConnected = false;
	    	    	mHandler.sendEmptyMessage(MSG_CREATE_SOCKET_FAILURE);
	            }
        	}
        }
    }
  
    
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG,"onCreate() called");
        
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        
        mIsRetry = true;
        mIsConnected = false;
        mIsServiceQuit = false;
        createSocket();
        
        registerBroadcastListener();
    }
    
    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
    	LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG,"onStart() called");
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
    	LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "onBind() called");
        mIsBind = true;

        return mBinder;
    }
    
    @Override
    public void onRebind(Intent intent) {
        // TODO Auto-generated method stub
    	LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG,"onRebind() called");
        mIsBind = true;
        
        super.onRebind(intent);
    }
    
    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
    	LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG,"onUnbind() called");
        mIsBind = false;
        
        super.onUnbind(intent);
        return true;
    }
    
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
    	LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG,"onDestroy() called");
        
        if (mReceiver != null){
        	LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG," unregister Receiver.");
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        
        stopTimer();

        mHandler.removeMessages(MSG_CHECKING_KEEP_LIVE);
        mHandler.removeMessages(MSG_GETTING_DATA_FROM_SERVER);
        mHandler.removeMessages(MSG_CREATE_SOCKET_SUCCESS);
        mHandler.removeMessages(MSG_CREATE_SOCKET_FAILURE);
        mHandler.removeMessages(MSG_INITIALIZE_SOCKET);
        mHandler.removeMessages(MSG_PUSH_SOCKET_LOST);
        
        closeSocket();
        mIsConnected = false;
        mPushLinkServiceState = SERVICE_NEW;
        mIsServiceQuit = true;
        
        super.onDestroy();
    }
    
    private static Socket getSocketInstance(){
    	if (mSocket == null) {
    		synchronized(AlipayPushLinkService.class) {
    			if (mSocket == null) {
    				mSocket = new Socket();
    			}
    		}
    	}
        return mSocket;
    }
    
    public int getState(){
        return mPushLinkServiceState;
    }
    
    private int checkConnectType() {
    	LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "checkConnectType(): which type net is used now!");
        int netType = 0;
        try {  
	        ConnectivityManager cm = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	        if (cm != null) {
		        NetworkInfo info = cm.getActiveNetworkInfo();
		        if (info != null && info.isAvailable()) {
			        String typeName = info.getTypeName(); //cmwap/cmnet/wifi/uniwap/uninet
			        int apnType = info.getType();
			        LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "checkConnectType(): typeName="+typeName +", apnType="+apnType);
			        
			        if (typeName.equalsIgnoreCase("WIFI")) {
			        	netType = 0; 
			        } else if(typeName.equalsIgnoreCase("MOBILE")) { 	// 如果是使用的运营商网络
						typeName = info.getExtraInfo().toLowerCase();  
						//3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap
//						if(typeName.contains("wap")){
//							mIsWapAPN = true; 
//						}
						
						// 获取默认代理主机ip  
						wap_proxy_ip = android.net.Proxy.getDefaultHost();  
                        // 获取端口  
						wap_proxy_port = android.net.Proxy.getDefaultPort(); 
                        if (wap_proxy_ip != null && !wap_proxy_ip.equals("") && wap_proxy_port != -1) {
                        	mIsWapAPN = true; 
                        	LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "checkConnectType() wap_proxy_ip:"+wap_proxy_ip +", wap_proxy_port="+wap_proxy_port);
                        }
                        
    			        if (netType == -1 && typeName.contains("777")) {	//可能是电信网络——华为是如此
    			        	if(mIsWapAPN) {
    			        		netType = 6;
    			        	} else {
    			        		netType = 5;
    			        	}
    			        } else {
    			        	//转换String到int，此处需要和服务端匹配
        			        netType = Integer.valueOf(mNetTypeMap.get(typeName)).intValue(); 
    			        }
    			        
    			        LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "checkConnectType() typeName:"+typeName +", netType="+netType); 
					} else {
						netType = -1;
					}
				} else {
					netType = 0;
				}
			}
		} catch (Exception e) {   
			netType = 0;   
		}   
        
        return netType;
    }
    
    private boolean hasRegApp(int appId) {
    	boolean bRegistreApp = true;
    	
    	if (mAppTypeMap.containsKey(appId)) {
    		bRegistreApp = true;
    	}
    	return bRegistreApp;
    }
 
    private String getRegAppAction(int appId) {
    	return mAppTypeMap.get(String.valueOf(appId));
    }

    private boolean createSocket() {
    	LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "createSocket(): create long link socket!");
        
        mCurNetType = checkConnectType();
	    new Thread(new pushReceiveWatchDog()).start();

	    mbWaitKeeplive = false;
	    LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "createSocket(): mCurNetType:"+mCurNetType);
        return true;
    }
    
    private boolean reconnectSocket() {
    	LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "reconnectSocket(): re-connect long link socket!");
        
        // 服务端命令要求，或者客户端发现网络中断
        //关闭原有的长连接
        closeSocket();
        
        //重新建立新的长连接socket
        createSocket();
        return true;
    }
	      
    private void closeSocket() {
    	  LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "closeSocket(): try close long link socket!");
			try {
//				if (mIsConnected) {
//					//如果连接存在，则发送关闭连接的指令
//					AlipayPushMsgData closeMsg = new AlipayPushMsgData(AlipayPushMsgData.MSG_PUSH_CLOSE, 
//							AlipayPushMsgData.MSG_PUSH_TYPE_REQUEST, "");
//					
//					try {
//	      		  		sendPushMsg(closeMsg);
//	      		  	} catch (IOException e) {
//		    	        e.printStackTrace();
//		    	    }
//		          	Log.d(TAG, "handleMessage mClient has sent close packet!");
//				}
				mIsConnected = false;
				
				if (mSocket != null && !mSocket.isClosed()) {
					mSocket.close();
					mSocket = null;
				}
				if (mDataOuts != null) {
					mDataOuts.close();
					mDataOuts = null;
				}
				if (mDataIns != null) {
					mDataIns.close();
					mDataIns = null;
				}
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				LogUtil.logMsg(Constant.LOG_LEVEL_ERROR, TAG, e.toString());
				e.printStackTrace();
			}
			LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "closeSocket(): done close long link socket!");
		}

    public boolean initialize(int netType, String clientDealID) {
    	LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "PushLinkStateCreated:initialize(): initialize push link!");
        
        if (mPushLinkServiceState != SERVICE_INITED) {
        	mIsReInit = true;
        	mClientDealID = clientDealID;
        	mHandler.sendEmptyMessage(MSG_INITIALIZE_SOCKET);
        } else {
        	//已经初始化完成，直接通知业务应用
        	notifyPushResults(PUSH_LINK_INITIALIZED);
        }
        
        return true;
    }
        
    public boolean register(int appId, String actionName) {
    	LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "PushLinkStateReady:register(): register application= "+appId);
        //注册业务应用
        mAppTypeMap.put(String.valueOf(appId), actionName);
        //mHandler.sendEmptyMessage(ACTION_REGISTER_APP);
        return true;
    }
        
    public boolean unregister(int appId) {
    	LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "PushLinkStateReady:unregisterAppId(): unregisterAppId application= "+appId);
        //注销业务应用
        mAppTypeMap.remove(String.valueOf(appId));
        //mHandler.sendEmptyMessage(ACTION_UNREGISTER_APP);
        return true;
    }

    /*
    public boolean sendAppData(JSON request) {
        Log.d(TAG, "AlipayPushLinkService:sendAppData(): JSON request! ");
        return true;
    }
    */
    
    public boolean close() {
    	LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "AlipayPushLinkService:close()!");
        
        AlipayPushMsgData closeMsg = new AlipayPushMsgData(AlipayPushMsgData.MSG_PUSH_CLOSE, 
  				AlipayPushMsgData.MSG_PUSH_TYPE_REQUEST, "");

	  	try {
	  		sendPushMsg(closeMsg);
	  	} catch (IOException e) {
	        e.printStackTrace();
	    }
	  	LogUtil.logMsg(Constant.LOG_LEVEL_INFO, TAG, "close() mClient has sent close packet!");
        
      	//通过消息机制累出发关闭操作，此处仅仅发送关闭消息
//        closeSocket();
        mPushLinkServiceState = SERVICE_NEW;
        return true;
    }
    
  /******************************* IBinder Service Interface ****************************/ 

    private final IAlipayPushLinkService.Stub mBinder = new IAlipayPushLinkService.Stub(){
        public int getPushLinkStatus(){
//            Log.d(TAG, "IAlipayPushLinkService.Stub : getPushLinkStatus.");
            return AlipayPushLinkService.this.getState();
        }
        
        public boolean initializePushLink(int netType, String clientDealDesc){
//            Log.d(TAG, "IAlipayPushLinkService.Stub : initializePushLink");
            return AlipayPushLinkService.this.initialize(netType, clientDealDesc);
        }
        
        public boolean registerAppId(int appId, String actionName){
//            Log.d(TAG, "IAlipayPushLinkService.Stub : registerAppId");
            return AlipayPushLinkService.this.register(appId, actionName);
        }

        public boolean unregisterAppId(int appId){
//            Log.d(TAG, "IAlipayPushLinkService.Stub : unregisterAppId");
            return AlipayPushLinkService.this.unregister(appId);
        }
        
        /*
        public boolean sendAppData(JSON request){
            Log.d(TAG, "IAlipayPushLinkService.Stub : sendAppData");
            return AlipayPushLinkService.sendData(request);
        }
        */
        
        public boolean closePushLink(){
//            Log.d(TAG, "IAlipayPushLinkService.Stub : closePushLink");
            return AlipayPushLinkService.this.close();
        }

    };
}