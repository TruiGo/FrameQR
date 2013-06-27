package com.alipay.android.push;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Future;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;

import com.alipay.android.appHall.DeviceHelper;
import com.alipay.android.appHall.common.CacheSet;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.TelephoneInfoHelper;
import com.alipay.android.push.callback.ConnectInitListener;
import com.alipay.android.push.callback.TaskListener;
import com.alipay.android.push.connection.ConnectionConfiguration;
import com.alipay.android.push.connection.ConnectionConfiguration.SecurityMode;
import com.alipay.android.push.connection.ConnectionListener;
import com.alipay.android.push.connection.SmackConfiguration;
import com.alipay.android.push.connection.XMPPConnection;
import com.alipay.android.push.connection.proxy.ProxyInfo;
import com.alipay.android.push.packet.Packet;
import com.alipay.android.push.packet.PacketConstants;
import com.alipay.android.push.packet.PacketFactory;
import com.alipay.android.push.packetListener.HeartBeatPacketListener;
import com.alipay.android.push.packetListener.NotificationPacketListener;
import com.alipay.android.push.packetListener.PacketListener;
import com.alipay.android.push.packetListener.ReconnectPacketListener;
import com.alipay.android.push.packetListener.RegisterPacketListener;
import com.alipay.android.push.util.ConnectParamConstant;
import com.alipay.android.push.util.Constants;
import com.alipay.android.push.util.DataHelper;
import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.StringUtils;
import com.alipay.android.push.util.record.RecordtoFile;
import com.alipay.android.push.widget.TestWidgetProvider;

/**
 * This class is to manage the XMPP connection between client and server.
 */
public class XmppManager {

	private static final String LOGTAG = LogUtil.makeLogTag(XmppManager.class);

	private Context context;

	private NotificationService.TaskSubmitter taskSubmitter;
	private NotificationService.TaskTracker taskTracker;

	private SharedPreferences sharedPrefs;
	private String sslUsed;
	private String zipFlag;
	private String xmppHost;
	private int xmppPort;
	private String proxyHost;
	private int proxyPort;
	private int retryTimes = 0;

	private int protocolVersion = PacketConstants.PACKET_VERSION_2;

	private XMPPConnection connection;

	private String username = "";
	private String initParam = "";

	private ConnectionListener connectionListener;
	private RegisterPacketListener registerListener;
	private HeartBeatPacketListener heartBeatListener;
	private NotificationPacketListener notificationPacketListener;
	private ReconnectPacketListener reconnectListener;

	private Handler mHandler;

	private List<Runnable> taskList;
	private boolean running = false;
	private Future<?> futureTask;
	Timer mHeartTimer = null;
	Timer reconnectedTimer = null;
	ReconnectionTask reconnectionTask;
	
	private AlarmManager am = null;
	private PendingIntent mConnSender = null;
	private PendingIntent mHeartSender = null;
	
	private PowerManager.WakeLock mWakeLock = null;
	
	private int mPackageUid = -1;

	public XmppManager(NotificationService notificationService) {
		context = notificationService;
		taskSubmitter = notificationService.getTaskSubmitter();
		taskTracker = notificationService.getTaskTracker();
		
		sharedPrefs = notificationService.getSharedPreferences();

		username = sharedPrefs.getString(Constants.XMPP_USERNAME, "");
		initParam = sharedPrefs.getString(Constants.XMPP_TOKEN, "");

		connectionListener = new PersistentConnectionListener(this);
		registerListener = new RegisterPacketListener(this);
		heartBeatListener = new HeartBeatPacketListener(this);
		notificationPacketListener = new NotificationPacketListener(this);
		reconnectListener = new ReconnectPacketListener(this);

		mHandler = new Handler();
		taskList = new ArrayList<Runnable>();
		
		this.am = (AlarmManager)this.context.getSystemService(Context.ALARM_SERVICE);
		
		try {
    		PackageManager pm = context.getPackageManager();
        	ApplicationInfo info = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        	
            if (info != null) {
            	mPackageUid = info.uid;
            } else {
                //
            }
        } catch (Exception e) {
        	//
        }

		LogUtil.LogOut(5, LOGTAG, "xmppManager=" + this.hashCode());
	}

	public Context getContext() {
		return context;
	}

	public void connect() {
		LogUtil.LogOut(3, LOGTAG, "connect()...");
		// stopReconnTimer();
		// stopHeartTimer();

		this.stopReconnAlarmTimer();
		addTask(new ConnectTask(new ConnectInitListener()));
	}

	public void disconnect() {
		LogUtil.LogOut(3, LOGTAG, "disconnect()...");

		this.stopReconnAlarmTimer();
//		stopReconnTimer();
//		stopHeartTimer();
		this.stopHeartAlarmTimer();

		terminatePersistentConnection();
		//清理资源后，再次检查电源锁是否已经释放
		this.releaseWakeLock();

		if (Constants.PUSH_TEST) {
			Intent intent = new Intent();
			intent.setClass(this.getContext(), TestWidgetProvider.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			intent.putExtra("PushStatus", -1);

			this.getContext().sendBroadcast(intent);
		}
	}

	public void terminatePersistentConnection() {
		LogUtil.LogOut(4, LOGTAG, "terminatePersistentConnection()...");
		// 添加断开连接任务
		addTask(new Runnable() {
			final XmppManager xmppManager = XmppManager.this;

			@Override
			public void run() {
				LogUtil.LogOut(5, LOGTAG,
						"terminatePersistentConnection()... called. connection:"
								+ connection.hashCode());
				if (isConnected()) {
					LogUtil.LogOut(4, LOGTAG,
							"terminatePersistentConnection()... run()");

					// 取消之前注册所有的数据listener
					xmppManager.getConnection().removePacketListener(
							xmppManager.getRegisterPacketListener());
					xmppManager.getConnection().removePacketListener(
							xmppManager.getHeartBeatPacketListener());
					xmppManager.getConnection().removePacketListener(
							xmppManager.getHeartBeatPacketListener());
					xmppManager.getConnection().removePacketListener(
							xmppManager.getReconnectPacketListener());

					getConnection().disconnect();
					LogUtil.LogOut(4, LOGTAG,
							"terminatePersistentConnection()...Done!");
				}
			}
		});
	}

	public XMPPConnection getConnection() {
		// LogUtil.LogOut(5, LOGTAG,
		// "getConnection()... called. connection:"+connection.hashCode());
		return connection;
	}

	public void setConnection(XMPPConnection connection) {
		LogUtil.LogOut(5, LOGTAG, "setConnection()... called. connection:"
				+ connection.hashCode());
		this.connection = connection;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getInitParam() {
		return initParam;
	}

	public void setInitParam(String initParam) {
		this.initParam = initParam;
	}
	
	public void saveLastConnectedTime(long lastTime) {
		synchronized(this) {
			Editor editor = this.sharedPrefs.edit();
			editor.putLong(Constants.LAST_CONNECTED_TIME, lastTime);
	        editor.commit();
		}
	}
	
	public void saveCreateConnectTime(long lastTime) {
		synchronized(this) {
			Editor editor = this.sharedPrefs.edit();
			editor.putLong(Constants.CREATE_CONNECT_TIME, lastTime);
	        editor.commit();
		}
	}
	
	public int getProtoVer() {
		return this.protocolVersion;
	}
	
	private long getCreateConnectTime() {
		long lastCreateConnectTime = 0;
		synchronized(this) {
			lastCreateConnectTime = sharedPrefs.getLong(Constants.CREATE_CONNECT_TIME, 0);
			LogUtil.LogOut(4, LOGTAG, "XmppManager() lastCreateConnectTime:" + lastCreateConnectTime);
		}
		return lastCreateConnectTime;
	}
	
	private void loadPushConfig() {
		xmppHost = sharedPrefs.getString(Constants.XMPP_HOST, "localhost");
		xmppPort = sharedPrefs.getInt(Constants.XMPP_PORT, 5222);
		protocolVersion = sharedPrefs.getInt(Constants.PROTOCOL_VERSION, PacketConstants.PACKET_VERSION_2);
		
		sslUsed = sharedPrefs.getString(Constants.SSL_USED, "1");
		zipFlag = sharedPrefs.getString(Constants.ZIP_FLAG, "0");
		retryTimes = sharedPrefs.getInt(Constants.RETRY_TIMES, 0);
		
		RecordtoFile.recordPushInfo(RecordtoFile.REASON_UNKNOWN, RecordtoFile.ACTION_CONN,
				System.currentTimeMillis(), 
				RecordtoFile.ACTION_CONN, 
				System.currentTimeMillis()+1*0*1000,
				"NotificationService_loadPushConfig: xmppHost:"+xmppHost 
				+", xmppPort:"+xmppPort +", protocolVersion:"+protocolVersion,
				1);
			
		LogUtil.LogOut(4, LOGTAG, "loadPushConfig() xmppHost:" + xmppHost
				+ ", xmppPort:" + xmppPort + ", protocolVersion:" + protocolVersion);
	}

	private void checkConnectType() {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm != null) {
				NetworkInfo info = cm.getActiveNetworkInfo();
				if (info != null && info.isAvailable()) {
					String typeName = info.getTypeName(); // cmwap/cmnet/wifi/uniwap/uninet

					if (typeName.equalsIgnoreCase("MOBILE")) { // 如果是使用的运营商网络
						typeName = info.getExtraInfo();
						LogUtil.LogOut(4, LOGTAG,
								"ActiveNetworkInfo() typeName:" + typeName);
						// 3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap

						// 获取默认代理主机ip
						proxyHost = android.net.Proxy.getDefaultHost();
						// 获取端口
						proxyPort = android.net.Proxy.getDefaultPort();
					} else {
						proxyHost = null;
						proxyPort = 0;
					}
				} else {
					proxyHost = null;
					proxyPort = 0;
				}
			}
		} catch (Exception e) {
			proxyHost = null;
			proxyPort = 0;
		}

		LogUtil.LogOut(4, LOGTAG, "checkConnectType() proxyHost:" + proxyHost
				+ ", proxyPort=" + proxyPort);
	}

	public ConnectionListener getConnectionListener() {
		return connectionListener;
	}

	public PacketListener getRegisterPacketListener() {
		return registerListener;
	}

	public PacketListener getHeartBeatPacketListener() {
		return heartBeatListener;
	}

	public PacketListener getNotificationPacketListener() {
		return notificationPacketListener;
	}

	public PacketListener getReconnectPacketListener() {
		return reconnectListener;
	}

	public void startReconnectionThread() {
		LogUtil.LogOut(3, LOGTAG,
				"startReconnectionThread()... ReconnectionTask");
		// 如果有之前的连接存在，则重置连接
		if (this.getConnection() != null) {
			getConnection().resetConnection();
		}
		
		this.stopReconnAlarmTimer();
//		stopReconnTimer();
//		stopHeartTimer();
		this.stopHeartAlarmTimer();

		try {
			Thread.sleep(150);
//			startReconnTimer();
			this.startReconnAlarmTimer();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (Constants.PUSH_TEST) {
			Intent intent = new Intent();
			intent.setClass(this.getContext(), TestWidgetProvider.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			intent.putExtra("PushStatus", -1);

			this.getContext().sendBroadcast(intent);
		}

		// synchronized (reconnection) {
		// LogUtil.LogOut(2, LOGTAG,
		// "startReconnectionThread()... reconnection");
		// if (!reconnection.isAlive()) {
		// reconnection.setName("Xmpp Reconnection Thread");
		// reconnection.start();
		// }
		// }
	}

	public void resetWaitingTime() {
		ReconnectionTask.resetWaitingTime();
	}

	public Handler getHandler() {
		return mHandler;
	}

	/*
	 * public void reregisterAccount() { removeAccount(); submitLoginTask();
	 * runTask(); }
	 */

	public List<Runnable> getTaskList() {
		return taskList;
	}

	public Future<?> getFutureTask() {
		return futureTask;
	}

	public void runTask() {
		LogUtil.LogOut(5, LOGTAG, "runTask()...");

		synchronized (taskList) {
			running = false;
			futureTask = null;

			if (!taskList.isEmpty()) {
				Runnable runnable = (Runnable) taskList.get(0);
				taskList.remove(0);
				running = true;

				LogUtil.LogOut(4, LOGTAG, "runTask() runnable is "
						+ runnable.getClass().getName().toString());
				futureTask = taskSubmitter.submit(runnable);
				if (futureTask == null) {
					taskTracker.decrease();
				}
			} else {
				LogUtil.LogOut(5, LOGTAG, "runTask(),taskList is empty");
			}
		}
		taskTracker.decrease();
		LogUtil.LogOut(5, LOGTAG, "runTask()...done");
	}

	private String newRandomUUID() {
		String uuidRaw = UUID.randomUUID().toString();
		return uuidRaw.replaceAll("-", "");
	}

	private String getIdentifier() {
		// get the latest identifier
		String identifier = sharedPrefs.getString(Constants.CLIENT_IDENTIFIER,
				"");
		if (identifier.length() == 0 && identifier != null) {
			identifier = newRandomUUID();

			setIdentifier(identifier);
		}
		return identifier;
	}

	private void setIdentifier(String identifier) {
		// set the latest identifier
		Editor editor = sharedPrefs.edit();
		editor.putString(Constants.CLIENT_IDENTIFIER, identifier);
		editor.commit();
	}

	private boolean isConnected() {
		boolean ret = false;
		
		if (connection != null && connection.isConnected()) {
			// double check whether this connection is alive
			long lastTime = SmackConfiguration.getLastConnectedTime();
			long del = (System.currentTimeMillis() - lastTime) / 1000;
			//必须在一定时间内有数据往来，才被认为是活着的连接
			if (del < SmackConfiguration.getReconnectInterval()) {
				ret = true;
			}
		}
		return ret;
	}

	/*
	 * private boolean isAuthenticated() { return connection != null &&
	 * connection.isConnected() && connection.isAuthenticated(); }
	 */

	public boolean isRegistered() {
		return sharedPrefs.contains(Constants.XMPP_USERNAME)
				&& sharedPrefs.contains(Constants.XMPP_TOKEN);
	}

	public void submitRegisterTask() {
		LogUtil.LogOut(4, LOGTAG, "submitRegisterTask()...");
		addTask(new RegisterTask());
	}

	public void submitHeartBeatTask() {
		LogUtil.LogOut(4, LOGTAG, "submitHeartBeatTask()...");
		addTask(new HeartBeatTask());
	}

	/*
	 * private void submitLoginTask() { LogUtil.LogOut(3, LOGTAG,
	 * "submitLoginTask()...");
	 * 
	 * //对于我们的push通知，不考虑Login功能 //addTask(new LoginTask()); }
	 */

	private void addTask(Runnable runnable) {
		LogUtil.LogOut(4, LOGTAG, "addTask(runnable)...");
		taskTracker.increase();

		synchronized (taskList) {
			LogUtil.LogOut(4, LOGTAG, "addTask taskList=" + taskList.size());
			if (taskList.isEmpty() && !running) {
				running = true;

				LogUtil.LogOut(3, LOGTAG, "addTask() runnable is "
						+ runnable.getClass().getName().toString());
				LogUtil.LogOut(5, LOGTAG, "addTask(runnable)...taskSubmitter:"
						+ taskSubmitter.hashCode());
				futureTask = taskSubmitter.submit(runnable);
				if (futureTask == null) {
					taskTracker.decrease();
				}
			} else {
				taskList.add(runnable);
				runTask();
			}
		}
		LogUtil.LogOut(4, LOGTAG, "addTask(runnable)... done");
	}

	/*
	 * private void removeAccount() { Editor editor = sharedPrefs.edit();
	 * editor.remove(Constants.XMPP_USERNAME);
	 * editor.remove(Constants.XMPP_TOKEN); editor.commit(); }
	 */
	/**
	 * A runnable task to connect the server.
	 */
	private class ConnectTask implements Runnable {

		final XmppManager xmppManager;

		private TaskListener taskListener;

		private ConnectTask(ConnectInitListener connectInitListener) {
			this.xmppManager = XmppManager.this;
			taskListener = connectInitListener;
		}

		public void run() {
			LogUtil.LogOut(3, LOGTAG, "===== ConnectTask.run()=====");
			
			//为防止连接建立过程中手机休眠，获取设备锁
			this.xmppManager.acquireWakeLock();

			// 设置连接状态
			if (!xmppManager.isConnected()) {
				//记录发起建立连接的时间
				saveCreateConnectTime(System.currentTimeMillis());
				
				//加载push连接的配置参数，需要每次连接前重新加载
				loadPushConfig();
				
				// 获取当前的代理ip和port
				checkConnectType();

				ProxyInfo proxyInfo;
				if (proxyHost != null && proxyHost.length() > 0
						&& proxyPort != 0) {
					proxyInfo = new ProxyInfo(ProxyInfo.ProxyType.SOCKS,
							proxyHost, proxyPort, null, null);
				} else {
					proxyInfo = ProxyInfo.forNoProxy();
				}

				// Create the configuration for this new connection
				ConnectionConfiguration connConfig = new ConnectionConfiguration(
						xmppHost, xmppPort, proxyInfo);

				// 根据属性配置决定是否需要使用SSL
				if (sslUsed != null && sslUsed.equals("1")) {
					connConfig.setSecurityMode(SecurityMode.required);
				} else {
					connConfig.setSecurityMode(SecurityMode.disabled);
				}

				connConfig.setSASLAuthenticationEnabled(false);
				connConfig.setCompressionEnabled(false);

				XMPPConnection connection = new XMPPConnection(connConfig);
				xmppManager.setConnection(connection);

				// 设置本连接的重发次数
				xmppManager.getConnection().setRetryTimes(retryTimes);
				xmppManager.getConnection().setMsgVersion(protocolVersion);

				// Connect to the server
				xmppManager.getConnection().connect(taskListener);
			} else {
				//已经处于连接状态，则直接释放电源锁
				this.xmppManager.releaseWakeLock();
			}
		}
	}

	/**
	 * A runnable task to register a new user onto the server.
	 */
	private class RegisterTask implements Runnable {

		final XmppManager xmppManager;

		private RegisterTask() {
			xmppManager = XmppManager.this;
		}

		public void run() {
			LogUtil.LogOut(3, LOGTAG, "===== RegisterTask.run()=====");

			if (!xmppManager.isRegistered()) {
				Packet registration;
				try  {
					registration = PacketFactory.getPacket(protocolVersion);
					registration.setMsgId(PacketConstants.MSG_PUSH_INITIALIZE);
					registration.setMsgType(PacketConstants.MSG_PUSH_TYPE_REQUEST);
				} catch (Exception e)  {
					e.printStackTrace();
					return;
				}
				
				DataHelper dataHelper = new DataHelper(context);
				JSONObject registerReq = dataHelper.getRequestObj();
				if (registerReq != null) {
					try {
						if (xmppManager.getUsername().length() > 0) {
							registerReq.put(ConnectParamConstant.USERID,
									xmppManager.getUsername());
						} else {
							registerReq.put(ConnectParamConstant.USERID, "");
						}
						registerReq.put(ConnectParamConstant.PUSHVERSION,
								SmackConfiguration.getVersion());
						
						registration.setData(registerReq.toString());
	
						LogUtil.LogOut(3, LOGTAG,
								"RegisterTask() registration will be sent! data:"
										+ registration.getData());
					} catch (JSONException e) {
						e.printStackTrace();
					}

					// 注册所有的数据listener
					connection.addPacketListener(
							xmppManager.getRegisterPacketListener(), null);
					connection.addPacketListener(
							xmppManager.getHeartBeatPacketListener(), null);
					connection.addPacketListener(
							xmppManager.getNotificationPacketListener(), null);
					connection.addPacketListener(
							xmppManager.getReconnectPacketListener(), null);
	
					LogUtil.LogOut(4, LOGTAG,
							"RegisterTask() registration will be sent! length="
									+ registration.getDataLength());
					LogUtil.LogOut(5, LOGTAG, "RegisterTask() xmppManager="
							+ xmppManager.hashCode());
					xmppManager.getConnection().sendPacket(registration);
				} else {
					LogUtil.LogOut(2, LOGTAG, "RegisterTask() getRequestObj is null!");
				}
			} else {
				LogUtil.LogOut(3, LOGTAG, "Account registered already");
				// xmppManager.runTask();
			}
		}
	}

	/**
	 * A runnable task to register a new user onto the server.
	 */
	private class HeartBeatTask implements Runnable {

		final XmppManager xmppManager;
		TelephoneInfoHelper telephoneInfoHelper = TelephoneInfoHelper
				.getTelephoneHelper(context);

		private HeartBeatTask() {
			xmppManager = XmppManager.this;
		}

		public void run() {
			LogUtil.LogOut(3, LOGTAG, "===== HeartBeatTask() Runnable=====");

			Packet heartBeat;
			try  {
				heartBeat = PacketFactory.getPacket(protocolVersion);
				heartBeat.setMsgId(PacketConstants.MSG_PUSH_KEEPLIVE);
				heartBeat.setMsgType(PacketConstants.MSG_PUSH_TYPE_REQUEST);
				heartBeat.setData("");
			} catch (Exception e)  {
				e.printStackTrace();
				return;
			}
			LogUtil.LogOut(4, LOGTAG,
					"HeartBeatTask() heartBeat will be sent! length="
							+ heartBeat.getDataLength());
			
			xmppManager.getConnection().sendPacket(heartBeat);
		}
	}
	
	public void startHeartAlarmTimer() {
		this.stopHeartAlarmTimer();
		
		try {
			Thread.sleep(150);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Intent intent = new Intent();
		intent.setAction(Constants.ACTION_KEEPLIVE_TIMER);
		this.mHeartSender = PendingIntent.getBroadcast(this.context, 0, intent, 0); 
		
		if(this.am != null) {
			this.am.set(AlarmManager.RTC_WAKEUP, 
					SmackConfiguration.getKeepAliveInterval()*1000 + System.currentTimeMillis(), 
					this.mHeartSender);
		}
	}
	
	public void stopHeartAlarmTimer() {
		if(this.am != null) {
			this.am.cancel(this.mHeartSender);
        }
	}

	public void startHeartTimer() {
		stopHeartTimer();

		this.mHeartTimer = new Timer();
		// 每隔-PacketReplyTimeout-检查是否收发包情况
		this.mHeartTimer.schedule(new sendHeartBeatTask(),
				(long) SmackConfiguration.getKeepAliveInterval() * 1000);
	}

	protected void stopHeartTimer() {
		if (null != this.mHeartTimer) {
			this.mHeartTimer.cancel(); // ֹͣtimer
			this.mHeartTimer = null;
		}
	}

	class sendHeartBeatTask extends TimerTask {
		final XmppManager xmppManager = XmppManager.this;

		public void run() {
			LogUtil.LogOut(4, LOGTAG, "===== HeartBeatTask() TimerTask=====");

			// Start keep alive process (after TLS was negotiated - if
			// available)
			XmppManager.this.submitHeartBeatTask();

			/*
			 * Packet heartBeat = new Packet();
			 * heartBeat.setMsgType(Packet.MSG_PUSH_TYPE_REQUEST);
			 * heartBeat.setMsgId(Packet.MSG_PUSH_KEEPLIVE);
			 * 
			 * try { //地理位置信息 if (SmackConfiguration.getLBSFlag()) { JSONObject
			 * lbsReq = new JSONObject();
			 * 
			 * AlipayLocateInfo locateInfo = BaseHelper.getLocation(context,
			 * null); lbsReq.put(ConnectParamConstant.LATITUDE,
			 * locateInfo.latitude);
			 * lbsReq.put(ConnectParamConstant.LONGITITUDE,
			 * locateInfo.longititude);
			 * lbsReq.put(ConnectParamConstant.ACCURACY_RANGE,
			 * locateInfo.accuracyRange);
			 * lbsReq.put(ConnectParamConstant.CELL_ID, locateInfo.curCellId);
			 * lbsReq.put(ConnectParamConstant.CELL_LAC,
			 * locateInfo.curCellIdLac);
			 * lbsReq.put(ConnectParamConstant.CELL_MNC,
			 * locateInfo.curCellIdNet);
			 * lbsReq.put(ConnectParamConstant.CELL_MCC,
			 * locateInfo.curCellIdCountry);
			 * 
			 * heartBeat.setData(lbsReq.toString()); LogUtil.LogOut(3, LOGTAG,
			 * "RegisterTask() heartBeat will be sent! data:"
			 * +heartBeat.getData()); } else { heartBeat.setData(""); } } catch
			 * (JSONException e) {
			 * 
			 * e.printStackTrace(); }
			 * 
			 * LogUtil.LogOut(3, LOGTAG,
			 * "HeartBeatTask() heartBeat will be sent! length="
			 * +heartBeat.getDataLength()); LogUtil.LogOut(3, LOGTAG,
			 * "HeartBeatTask() xmppManager="+xmppManager.hashCode());
			 * xmppManager.getConnection().sendPacket(heartBeat);
			 */
		}
	}
	
	//获取需要等待的时间间隔：绝对时间，单位毫秒
	private long getReconnTimer() {
		long curWaitTime = 0;
		
		long lastConnectedTime = SmackConfiguration.getLastConnectedTime();
		long lastTryConnectTime = this.getCreateConnectTime();
		
		LogUtil.LogOut(3, LOGTAG, "getReconnTimer lastTryConnectTime="+ lastTryConnectTime 
				+ ", lastConnectedTime=" + StringUtils.timeLong2Date(lastConnectedTime));
		
		if (lastConnectedTime > 0 && lastConnectedTime >= lastTryConnectTime) {	
			// 之前连接成功过，则以服务端返回的重连设置为准
			long lostedTime = System.currentTimeMillis() - lastConnectedTime;
			// 距离上次连接的时间间隔： milliseconds
			curWaitTime = SmackConfiguration.getReconnectInterval() * 1000L
					- lostedTime;
			if (curWaitTime <= 0) {
				curWaitTime = 0;
			}

			LogUtil.LogOut(3, LOGTAG, "getReconnTimer lostedTime="+ lostedTime
					+ ", curWaitTime=" + curWaitTime);
		} else {	
			// 之前没有连接成功过，则按照自有的连接机制进行
			curWaitTime = ReconnectionTask.waiting() * 1000L;
			LogUtil.LogOut(3, LOGTAG,
					"getReconnTimer reconnectionTask.waiting=" + curWaitTime);
		}
		
		RecordtoFile.recordPushInfo(RecordtoFile.REASON_CLIENT_RECONN,
				RecordtoFile.ACTIONT_TIMER, System.currentTimeMillis(),
				RecordtoFile.ACTION_CONN,
				System.currentTimeMillis() + curWaitTime,
				"XmppManager_getReconnTimer:curWaitTime=" + curWaitTime
				+ " lastConnectedTime=" + StringUtils.timeLong2Date(lastConnectedTime) 
				+ " lastTryConnectTime=" + StringUtils.timeLong2Date(lastTryConnectTime), 1);
		
		return curWaitTime;
	}
	
	private void startReconnAlarmTimer() {
		Intent intent = new Intent();
		intent.setAction(Constants.ACTION_PUSH_CONNECT);
		this.mConnSender = PendingIntent.getBroadcast(this.context, 0, intent, 0); 
		
		if(this.am != null) {
			this.am.set(AlarmManager.RTC_WAKEUP, 
					getReconnTimer() + System.currentTimeMillis(), 
					this.mConnSender);
		}
	}
	
	private void stopReconnAlarmTimer() {
		if(this.am != null) {
			this.am.cancel(this.mConnSender);
        }
	}
	

	public void startReconnTimer() throws Exception {
		stopReconnTimer();

		reconnectionTask = new ReconnectionTask();
		reconnectedTimer = new Timer();
		reconnectedTimer.schedule(reconnectionTask, getReconnTimer());
	}

	protected void stopReconnTimer() {
		if (null != reconnectedTimer) {
			reconnectedTimer.cancel(); // ֹͣtimer

			if (reconnectionTask != null) {
				reconnectionTask.cancel(); // 将原任务从队列中移除
				reconnectionTask = null;
			}

			reconnectedTimer = null;
		}
	}
	
	/**
     * 申请设备电源锁
     */
	public void acquireWakeLock() {
        if (null == mWakeLock) {
            PowerManager pm = (PowerManager) this.context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, LOGTAG);
            if (null != mWakeLock) {
                mWakeLock.acquire();
            }
        }
    }

    /**
     * 释放设备电源锁
     */
    public void releaseWakeLock() {
        if (null != mWakeLock) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }
    
    public int getAlipayClientInfo() {
    	return mPackageUid;
    }
}
