package com.alipay.android.push;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.push.connection.SmackConfiguration;
import com.alipay.android.push.util.Constants;
import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.Notifier;
import com.alipay.android.push.util.StringUtils;
import com.alipay.android.push.util.record.RecordtoFile;

/**
 * Service that continues to run in background and respond to the push
 * notification events from the server. This should be registered as service in
 * AndroidManifest.xml.
 */
public class NotificationService extends Service {

	private static final String LOGTAG = LogUtil.makeLogTag(NotificationService.class);
	public static final String SERVICE_NAME = "com.alipay.android.push.NotificationService";
	
	public static final String NOTICE_CHECK_ALARM_TIMER = "com.alipay.android.push.check";
	
	private AlarmManager am = null;
	private PendingIntent sender = null;

	private TelephonyManager telephonyManager;
	// private ConnectivityManager connectivityManager;

	private BroadcastReceiver notificationReceiver;
//	private BroadcastReceiver connectivityReceiver;
	private BroadcastReceiver clientActionReceiver;

	private ExecutorService executorService;
	private TaskSubmitter taskSubmitter;
	private TaskTracker taskTracker;
	
	private boolean mIsServiceQuit = false;

	public static XmppManager xmppManager = null;
	private SharedPreferences sharedPrefs;
	private String deviceId;
	private String mUserId = "";


	public NotificationService() {
		notificationReceiver = new NotificationReceiver(Constants.ACTION_SHOW_NOTIFICATION);
//		connectivityReceiver = new ConnectivityReceiver(this);
		clientActionReceiver = new ClientActionReceiver(this);

		executorService = Executors.newSingleThreadExecutor();
		taskSubmitter = new TaskSubmitter();
		taskTracker = new TaskTracker();
	}
	
	private final Handler mHandler = new Handler() {
	    public void handleMessage(Message msg) {
	    	LogUtil.LogOut(4, LOGTAG, "mHandler handleMessage msg:"+ msg.what);
	    	
	    	if (mIsServiceQuit)
	            return; 
	    }
	};

	@Override
	public void onCreate() {
		LogUtil.LogOut(4, LOGTAG, "onCreate()...");
		// Get deviceId
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = telephonyManager.getDeviceId();
		
		//获取当前用户信息
        AlipayApplication application = (AlipayApplication) this.getApplicationContext();
        UserInfo tCurUserInfo = application.getDataHelper().getLatestLoginedUser(this);
        if (tCurUserInfo != null && tCurUserInfo.userId != null
        		&& tCurUserInfo.userId.length() > 0) {
        	mUserId = tCurUserInfo.userId;
        }
		
		mIsServiceQuit = false;
		
		this.am = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent();
		intent.setAction(NOTICE_CHECK_ALARM_TIMER);
		this.sender = PendingIntent.getBroadcast(this, 0, intent, 0);

		sharedPrefs = getSharedPreferences(Constants.PUSH_PREFERENCE_NAME,Context.MODE_PRIVATE);
		Editor editor = sharedPrefs.edit();
		editor.putString(Constants.DEVICE_ID, deviceId);
		editor.commit();

		// If running on an emulator
		if (deviceId == null || deviceId.trim().length() == 0 || deviceId.matches("0+")) {
			if (sharedPrefs.contains("EMULATOR_DEVICE_ID")) {
				deviceId = sharedPrefs.getString(Constants.EMULATOR_DEVICE_ID,"");
			} else {
				deviceId = (new StringBuilder("EMU")).append(
						(new Random(System.currentTimeMillis())).nextLong())
						.toString();
				editor.putString(Constants.EMULATOR_DEVICE_ID, deviceId);
				editor.commit();
			}
		}
		LogUtil.LogOut(5, LOGTAG, "deviceId=" + deviceId);
		
		xmppManager = new XmppManager(this);
		LogUtil.LogOut(5, LOGTAG, "xmppManager=" + xmppManager.hashCode());
		
		//通过xmppManager来传递user信息
		xmppManager.setUsername(mUserId);
		LogUtil.LogOut(4, LOGTAG, "xmppManager setUsername=" + mUserId);
		
		RecordtoFile.recordPushInfo(RecordtoFile.REASON_SERVICE_START, RecordtoFile.ACTION_SERVICE_START,
				System.currentTimeMillis(), 
				RecordtoFile.ACTION_SERVICE_START, 
				System.currentTimeMillis()+1*10*1000,
				"NotificationService_onCreate:mUserId="+mUserId,
				1);

		registerNotificationReceiver();
		registerClientActionReceiver();

		LogUtil.LogOut(5, LOGTAG, "onCreate() executorService isShutdown="+executorService.isShutdown());
		if (executorService.isShutdown()) {
			executorService = Executors.newSingleThreadExecutor();
		}
		
		ReconnectionTask.resetWaitingTime();
//		taskSubmitter.submit(new Runnable() {
//			public void run() {
//				start();
//			}
//		});
	}
	
	// 在2.0以后的版本如果重写了onStartCommand，那onStart将不会被调用，注：在2.0以前是没有onStartCommand方法
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	LogUtil.LogOut(3, LOGTAG, "onStartCommand Received start id " + startId + ", intent: " + intent);
    	
    	// 如果服务进程在它启动后(从onStartCommand()返回后)被kill掉, 那么让他呆在启动状态但不取传给它的intent.
		// 随后系统会重写创建service，因为在启动时，会在创建新的service时保证运行onStartCommand
		// 如果没有任何开始指令发送给service，那将得到null的intent，因此必须检查它.
		// 该方式可用在开始和在运行中任意时刻停止的情况，例如一个service执行音乐后台的重放

    	String cur_triger = Constants.SERVICE_INIT_TRIGER;
    	if (intent != null) {
    		cur_triger = intent.getStringExtra(Constants.SERVICE_CUR_TRIGER);
    		
    		if (cur_triger != null) {
    			RecordtoFile.recordPushInfo(RecordtoFile.REASON_SERVICE_START, RecordtoFile.ACTION_SERVICE_START,
    					System.currentTimeMillis(), 
    					RecordtoFile.ACTIONT_UNKNOWN, 
    					System.currentTimeMillis()+1*10*1000,
    					"NotificationService_onStartCommand:cur_triger="+cur_triger,
    					1);
    			
    			int triger = Integer.parseInt(cur_triger);
    			if (triger < Integer.parseInt(Constants.SERVICE_BOOT_TRIGER)) {
    				//触发来自客户端（启动、登录和设置），应该立即启动后台服务和建立连接
    				//清理之前保存的最后重连时间、建立连接时间等信息
    				clearLastConnInfo();
    			}
    			
    			//获取之前记录的、最近一次和服务端进行数据交互的时间
    			//主要是因为：Android系统或者别的管理软件，是可以停止本服务的；
    			//如果自动重启后，不能准确获取上次连接时间，将会立即发起connect；
    			//这种修改，将会减少建联连接的次数，减少流量消耗
    			long lastConnectedTime = sharedPrefs.getLong(Constants.LAST_CONNECTED_TIME, 0);
    			SmackConfiguration.setLastConnectedTime(lastConnectedTime);
    			
    			//在取完上次时间后，再处理网络事件
    			if (cur_triger.equals(Constants.SERVICE_CONN_TRIGER)) {
    				//需要对网络事件特殊处理，其他事件仅用来启动服务
            		handleConnIntent();
    			} else {
    				long lostedTime = System.currentTimeMillis() - lastConnectedTime;
    				LogUtil.LogOut(3, LOGTAG, "onStartCommand() lastConnectedTime="+StringUtils.timeLong2Date(lastConnectedTime)
    						+", lostedTime="+lostedTime
    						+", reconnectInterval="+SmackConfiguration.getReconnectInterval()*1000);
    				
    				RecordtoFile.recordPushInfo(RecordtoFile.REASON_PUSH_CHECKED, RecordtoFile.ACTION_CONN,
    						System.currentTimeMillis(), 
    						RecordtoFile.ACTIONT_REGISTER, 
    						System.currentTimeMillis()+1*SmackConfiguration.getReconnectInterval()*1000,
    						"NotificationService_onStartCommand:lastConnectedTime="+StringUtils.timeLong2Date(lastConnectedTime)
    						+" lostedTime="+lostedTime
    						+" reconnectInterval="+SmackConfiguration.getReconnectInterval(),
    						1);
    				
    				//修改了记录上次连接时间的逻辑——保存到本地，因此，此部分需要调整
    				if (lastConnectedTime == 0) {
    					//这两种情况都是可以立即发起连接的:之前没有连接过，或者用户进入客户端操作触发的
    					//因为之前没有连接过，则不进行更多的条件限制
    					NotificationService.xmppManager.connect();
    				} else {
    					if (lostedTime >= SmackConfiguration.getReconnectInterval()*1000) {
    						Notifier notifier = new Notifier(this);
        					if (notifier.isNotificationEnabled() && notifier.isNotificationTime()
        							&& checkNetState()) {
        						//是否在可以接收push消息的时间段内
        						//考虑到网络不ok时，可能导致的频繁尝试连接，也需要检查网络状态
        						NotificationService.xmppManager.connect();
        					} else {
        						//不在有效时间段内，或者网络部ok，不立即启动连接
        						//等待client_check这个时钟事件去检查（前面已经启动了对应的检查时钟）
        					}
    					} else {
    						//事件已经触发，但是还未到允许的时间间隔
        					if(this.getXmppManager() != null) {
        						if(this.getXmppManager().getConnection() != null
                            			&& this.getXmppManager().getConnection().isConnected()) {
                        			//原有连接有效，不做新的处理
                            	} else {
        	    					//可能导致的频繁时钟设置，此处不再执行下面的语句
//                            		NotificationService.xmppManager.startReconnectionThread();
                            	}
        					} else {
        						//it's shouldn't be reached!
        					}
    					}
    				}
    			}
        	}
    	} else {
    		//获取之前记录的、最近一次和服务端进行数据交互的时间
    		long lastConnectedTime = sharedPrefs.getLong(Constants.LAST_CONNECTED_TIME, 0);
			SmackConfiguration.setLastConnectedTime(lastConnectedTime);
			
			//如果service进程被kill掉，保留service的状态为开始状态，但不保留递送的intent对象
			//如果在此期间没有任何启动命令被传递到service，那么参数Intent将为null
			if(this.getXmppManager() != null) {
				NotificationService.xmppManager.startReconnectionThread();
			}
    	}
    	
    	return Service.START_STICKY;
    }
    
    private boolean checkNetState() {
    	boolean ret = false;
    	
        NetworkInfo networkInfo = ((ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.isConnected()) {
            	ret = true;
            }
        }
        
        return ret;
    }
    
    private void clearLastConnInfo() {
    	//清除上次连接时保存的信息：最后交互事件
    	if (this.getXmppManager() != null) {
    		this.getXmppManager().saveLastConnectedTime(0);
        	this.getXmppManager().saveCreateConnectTime(0);
        	LogUtil.LogOut(3, LOGTAG, "clearLastConnInfo done!");
    	}
    }
    
    private void handleConnIntent() {
        NetworkInfo networkInfo = ((ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (networkInfo != null) {
        	LogUtil.LogOut(3, LOGTAG, "Network_Type="+networkInfo.getTypeName()
        			+ ", Network_State = "+networkInfo.getState());
        	
            if (networkInfo.isConnected()) {
            	long lastConnectedTime = SmackConfiguration.getLastConnectedTime();
            	long lostedTime = System.currentTimeMillis() - lastConnectedTime;
            	
            	LogUtil.LogOut(3, LOGTAG, "handleConnIntent lostedTime="+lostedTime 
            			+", lastConnectedTime="+lastConnectedTime);
            	
            	if (lastConnectedTime == 0) {
            		//之前没有成功建立过连接，立即重新连接
            		if (this.getXmppManager() != null
            				&& this.getXmppManager().getConnection() != null) {
            			RecordtoFile.recordPushInfo(RecordtoFile.REASON_NET_FIND, RecordtoFile.ACTION_CONN,
                				System.currentTimeMillis(), 
                				RecordtoFile.ACTIONT_UNKNOWN, 
                				System.currentTimeMillis()+1*0*1000,
                				"NotificationService_handleConnIntent:lastConnectedTime="+StringUtils.timeLong2Date(lastConnectedTime) 
                				+" isConnected="+this.getXmppManager().getConnection().isConnected(),
                				1);
            			
            			if(!this.getXmppManager().getConnection().isConnected()) {
            				//原有连接的状态不对，发起重连
            				//实际上，如果连接实体存在，则原有的控制策略应该也在，因此，此处动作可以去掉
            				NotificationService.xmppManager.startReconnectionThread();
                    	}
            		} else {
            			RecordtoFile.recordPushInfo(RecordtoFile.REASON_NET_FIND, RecordtoFile.ACTION_CONN,
                				System.currentTimeMillis(), 
                				RecordtoFile.ACTION_CONN, 
                				System.currentTimeMillis()+1*0*1000,
                				"NotificationService_handleConnIntent:lastConnectedTime="+StringUtils.timeLong2Date(lastConnectedTime),
                				1);
            			//之前没有尝试建立过连接，直接建立连接
            			this.connect();
            		}
            	} else {
            		RecordtoFile.recordPushInfo(RecordtoFile.REASON_NET_FIND, RecordtoFile.ACTION_CONN,
            				System.currentTimeMillis(), 
            				RecordtoFile.ACTIONT_TIMER, 
            				System.currentTimeMillis()+1*0*1000,
            				"NotificationService_handleConnIntent:lastConnectedTime="+StringUtils.timeLong2Date(lastConnectedTime)
            				+" lostedTime="+lostedTime,
            				1);
            		
            		if(this.getXmppManager() != null && this.getXmppManager().getConnection() != null
                			&& this.getXmppManager().getConnection().isConnected()) {
            			//原有连接有效，不做新的处理
                	} else {
                		//虽然之前和服务端有过数据交互，但是现在没有有效连接
                		if (NotificationService.xmppManager != null) {
                			//此处，将上次发起建立连接的时间复位；此处理将使得CreateConnectTime小于LastConnectedTime
                			//原因：此分支肯定之前和服务端有过连接，但那之后尝试过建立连接未能成功
                			//如果网络恢复，我们希望能立即发起建立连接
                			this.xmppManager.saveCreateConnectTime(0);
                			
                			//启动连接控制策略，择机重新建立长连接
                			NotificationService.xmppManager.startReconnectionThread();
                		}
                	}
            	}
            }
        } else {
        	RecordtoFile.recordPushInfo(RecordtoFile.REASON_NET_LOST, RecordtoFile.ACTION_DISCONN,
    				System.currentTimeMillis(), 
    				RecordtoFile.ACTIONT_TIMER, 
    				System.currentTimeMillis()+1*0*1000,
    				"NotificationService_handleConnIntent:Network unavailable",
    				1);
        	
        	LogUtil.LogOut(2, LOGTAG, "Network unavailable");
        	if(this.getXmppManager().getConnection() != null
        			&& this.getXmppManager().getConnection().isConnected()) {
        		this.disconnect();
        	}
        }
    }

	public static Intent getIntent() {
		return new Intent(SERVICE_NAME);
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public TaskSubmitter getTaskSubmitter() {
		return taskSubmitter;
	}

	public TaskTracker getTaskTracker() {
		return taskTracker;
	}

	public XmppManager getXmppManager() {
		return xmppManager;
	}

	public SharedPreferences getSharedPreferences() {
		return sharedPrefs;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void connect() {
		LogUtil.LogOut(3, LOGTAG, "connect()...");
		taskSubmitter.submit(new Runnable() {
			public void run() {
				NotificationService.this.getXmppManager().connect();
			}
		});
	}

	public void disconnect() {
		LogUtil.LogOut(3, LOGTAG, "disconnect()...");
		taskSubmitter.submit(new Runnable() {
			public void run() {
				NotificationService.this.getXmppManager().disconnect();
			}
		});
	}
	
	private void registerClientActionReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_PUSH_CONNECT);
        filter.addAction(Constants.ACTION_KEEPLIVE_TIMER);
//        filter.addAction(Constants.ACTION_PUSH_DISCONNECT);
        filter.addAction(NotificationService.NOTICE_CHECK_ALARM_TIMER);
        registerReceiver(clientActionReceiver,filter);
        
        //但是需要按照一定时间周期来查询，以便及时启动连接
		startAlarmTimer(30*60*1000);
		
		RecordtoFile.recordPushInfo(RecordtoFile.REASON_CLIENT_CHECK, RecordtoFile.ACTIONT_TIMER,
				System.currentTimeMillis(), 
				RecordtoFile.ACTIONT_STATUS_CHECK, System.currentTimeMillis()+30*60*1000,
				"NotificationService_registerClientActionReceiver:30 minutes",
				1);
	}
	
	private void unregisterClientActionReceiver() {
		try {
			unregisterReceiver(clientActionReceiver);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		clientActionReceiver = null;
	}

	private void registerNotificationReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_SHOW_NOTIFICATION);
//		filter.addAction(Constants.ACTION_NOTIFICATION_CLICKED);
//		filter.addAction(Constants.ACTION_NOTIFICATION_CLEARED);
		registerReceiver(notificationReceiver, filter);
	}

	private void unregisterNotificationReceiver() {
		try {
			unregisterReceiver(notificationReceiver);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		notificationReceiver = null;
	}


	private void start() {
		LogUtil.LogOut(3, LOGTAG, "start()...");
		registerNotificationReceiver();
		registerClientActionReceiver();

		LogUtil.LogOut(5, LOGTAG, "start() executorService isShutdown="+executorService.isShutdown());
		if (executorService.isShutdown()) {
			executorService = Executors.newSingleThreadExecutor();
		}
		
		ReconnectionTask.resetWaitingTime();
		
		long lastConnectedTime = SmackConfiguration.getLastConnectedTime();
		long lostedTime = System.currentTimeMillis() - lastConnectedTime;
		LogUtil.LogOut(3, LOGTAG, "start() lastConnectedTime="+StringUtils.timeLong2Date(lastConnectedTime)
				+", lostedTime="+lostedTime
				+", reconnectInterval="+SmackConfiguration.getReconnectInterval()*1000);
		
		RecordtoFile.recordPushInfo(RecordtoFile.REASON_PUSH_CHECKED, RecordtoFile.ACTION_CONN,
				System.currentTimeMillis(), 
				RecordtoFile.ACTIONT_REGISTER, 
				System.currentTimeMillis()+1*SmackConfiguration.getReconnectInterval()*1000,
				"NotificationService_start:lastConnectedTime="+StringUtils.timeLong2Date(lastConnectedTime)
				+" lostedTime="+lostedTime
				+" reconnectInterval="+SmackConfiguration.getReconnectInterval(),
				1);

		
		/*
		if(lastConnectedTime == 0) {
			//服务是重新启动的，则直接建立和服务端的通道
			Notifier notifier = new Notifier(this);
			if (notifier.isNotificationEnabled() && notifier.isNotificationTime()) {
				//是否在可以接收push消息的时间段内
				NotificationService.xmppManager.connect();
			} else {
				//不在有效时间段内，不立即启动连接
			}
		} else {
			//因为修改了记录上次连接时间的逻辑——保存到本地
			//所以，此部分需要调整
			
			//网络重连控制，接受服务端的配置
	    	if(lostedTime >= SmackConfiguration.getReconnectInterval()*1000){
	    		NotificationService.xmppManager.startReconnectionThread();
	    	}
		}
		*/
	}

	private void stop() {
		LogUtil.LogOut(3, LOGTAG, "stop()...");
		
		// 清理之前可能注册的时钟资源
//		this.getXmppManager().stopReconnAlarmTimer();
		stopAlarmTimer();

		unregisterNotificationReceiver();
//		unregisterConnectivityReceiver();
		unregisterClientActionReceiver();
		
		RecordtoFile.recordPushInfo(RecordtoFile.REASON_SERVICE_STOP, RecordtoFile.ACTION_DISCONN,
				System.currentTimeMillis(), 
				RecordtoFile.ACTION_CONN, 
				System.currentTimeMillis()+SmackConfiguration.getReconnectInterval()*1000,
				"NotificationService_stop:reconnectInterval="+SmackConfiguration.getReconnectInterval()
				+" lostedTime="+System.currentTimeMillis(),
				1);

		xmppManager.disconnect();
		LogUtil.LogOut(5, LOGTAG, "stop() executorService will be shutdown!");
		executorService.shutdown();
	}
	
	void shutdownAndAwaitTermination() {
		executorService.shutdown(); // Disable new tasks from being submitted
		
		try {
		     // Wait a while for existing tasks to terminate
		     if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
		    	 executorService.shutdownNow(); // Cancel currently executing tasks
		    	 
		    	 // Wait a while for tasks to respond to being canceled
		    	 if (!executorService.awaitTermination(60, TimeUnit.SECONDS))
		    		 LogUtil.LogOut(5, LOGTAG, "executorService did not terminate!");
			 }
		} catch (InterruptedException ie) {
		     // (Re-)Cancel if current thread also interrupted
			executorService.shutdownNow();
			
		     // Preserve interrupt status
		     Thread.currentThread().interrupt();
		}
	}

	/**
	 * Class for summiting a new runnable task.
	 */
	public class TaskSubmitter {
		@SuppressWarnings("rawtypes")
		public Future submit(Runnable task) {
			Future result = null;
			
			LogUtil.LogOut(5, LOGTAG, "submit(runnable)...getExecutorService:"+getExecutorService().hashCode());
			LogUtil.LogOut(5, LOGTAG, "isTerminated="+getExecutorService().isTerminated());
			LogUtil.LogOut(5, LOGTAG, "isShutdown="+getExecutorService().isShutdown());

			if (!getExecutorService().isTerminated()
					&& !getExecutorService().isShutdown() && task != null) {
				result = getExecutorService().submit(task);
				LogUtil.LogOut(5, LOGTAG, "Future result is "+result.getClass().getName().toString());
				LogUtil.LogOut(5, LOGTAG, "Future result isCancelled="+result.isCancelled()
						+ ", isDone="+result.isDone());
			}
			return result;
		}
	}

	/**
	 * Class for monitoring the running task count.
	 */
	public class TaskTracker {
		public int count;

		public TaskTracker() {
			this.count = 0;
		}

		public void increase() {
			synchronized (getTaskTracker()) {
				getTaskTracker().count++;
				LogUtil.LogOut(4, LOGTAG, "Incremented task count to " + count);
			}
		}

		public void decrease() {
			synchronized (getTaskTracker()) {
				getTaskTracker().count--;
				LogUtil.LogOut(4, LOGTAG, "Decremented task count to " + count);
			}
		}
	}

	@Override
	public void onDestroy() {
		LogUtil.LogOut(4, LOGTAG, "onDestroy()...");
		
		RecordtoFile.recordPushInfo(RecordtoFile.REASON_SERVICE_STOP, RecordtoFile.ACTION_SERVICE_STOP,
				System.currentTimeMillis(), 
				RecordtoFile.ACTION_SERVICE_STOP, 
				System.currentTimeMillis()+1*10*1000,
				"NotificationService_onDestroy:mUserId="+mUserId,
				1);
		
		stop();
		mUserId = "";
		mIsServiceQuit = true;
	}

	@Override
	public IBinder onBind(Intent intent) {
		LogUtil.LogOut(4, LOGTAG, "onBind()...");
		return null;
	}

	@Override
	public void onRebind(Intent intent) {
		LogUtil.LogOut(4, LOGTAG, "onRebind()...");
	}

	@Override
	public boolean onUnbind(Intent intent) {
		LogUtil.LogOut(4, LOGTAG, "onUnbind()...");
		return true;
	}
	
	protected void stopAlarmTimer() {
        if(am != null) {
        	am.cancel(sender);
        }
    }
	
    protected void startAlarmTimer(int nextTime) {
    	if(null != am) {
    		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+nextTime, sender);
    		LogUtil.LogOut(3, LOGTAG, "startAlarmTimer ELAPSED_REALTIME_WAKEUP! nextTime="+nextTime);
		}
    }

}
