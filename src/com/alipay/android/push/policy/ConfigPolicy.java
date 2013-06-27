package com.alipay.android.push.policy;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.alipay.android.push.NotificationService;
import com.alipay.android.push.config.ConfigService;
import com.alipay.android.push.connection.SmackConfiguration;
import com.alipay.android.push.util.Constants;
import com.alipay.android.push.util.DataHelper;
import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.Notifier;
import com.alipay.android.push.util.StringUtils;
import com.alipay.android.push.util.record.RecordtoFile;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class ConfigPolicy
{
	private static final String LOGTAG = LogUtil.makeLogTag(ConfigPolicy.class);
	
	private final int DELAY_STEP_TIME = 15;	//minutes
	
	private Context mContext;
	private DataHelper dataHelper;
	
	private Notifier notifier;
	
    public String cfgId;
    public int minTime;
    public int curCount;
    public int maxCount;
    public long lastRunTime;
    public boolean netState;
    
    public ConfigPolicy(Context ctx) 
    {
        super();

        this.cfgId = "";
        this.minTime = 5*60;		// minutes
        this.curCount = 0;
        this.maxCount = 1;
        this.netState = true;
        
        mContext = ctx;
        dataHelper = new DataHelper(mContext);
        notifier = new Notifier(mContext);
        
        init();
    }
    
    private void init() {
    	//load the latest data
    	int tCurCount = (int)dataHelper.getCfgPolicy(Constants.RPF_CONSUME_COUNT);
    	//当获取的消耗值为-1时，表明之前没有进行过配置请求
    	if (tCurCount > 0) {
    		this.curCount = tCurCount;
    	}
    	
    	int tMaxCount = (int)dataHelper.getCfgPolicy(Constants.RPF_SUCCESS_COUNT);
    	if (tMaxCount > 0) {
    		this.maxCount = tMaxCount;
    	}
    	
    	int tMinTime = (int)dataHelper.getCfgPolicy(Constants.RPF_INTERVAL_TIME);
    	if (tMinTime > 2*60) {
    		this.minTime = tMinTime;
    	}
    	
    	long tLastTime = dataHelper.getCfgPolicy(Constants.LAST_CONFIG_TIME);
    	if (tLastTime > 0) {
    		this.lastRunTime = tLastTime;
    	}
    	
    	RecordtoFile.recordPushInfo(RecordtoFile.REASON_PHONE_BOOTED, RecordtoFile.ACTION_SERVICE_START,
				System.currentTimeMillis(), 
				RecordtoFile.ACTION_SERVICE_START, 
				System.currentTimeMillis()+1*10*1000,
				"ConfigPolicy_init: lastRunTime="+StringUtils.timeLong2Date(lastRunTime) +", minTime="+minTime,
				1);
    	
    	LogUtil.LogOut(3, LOGTAG, "init() lastRunTime="+StringUtils.timeLong2Date(lastRunTime)
    			+ ", curCount="+curCount + ", maxCount="+maxCount + ", minTime="+minTime);
    }
    
    public void toStart(int trigger) {
    	LogUtil.LogOut(3, LOGTAG, "toStart() trigger="+trigger);
    	
    	Notifier notifier = new Notifier(mContext);
    	boolean settingStatus = notifier.isConfigEnabled() && notifier.isNotificationTime();
    	if (settingStatus == false || checkNetState() == false) {
    		//用户关闭push开关，或者当前网络状态处于非连接状态，则直接返回
    		return;
    	}
    	
    	//check whether to run based on trigger event
    	if (trigger == Integer.parseInt(Constants.SERVICE_BOOT_TRIGER)) {
    		// run now directly
    		// even then user always restart his phone, still do it
    		runService();
    	} else if (trigger == Integer.parseInt(Constants.SERVICE_INIT_TRIGER)
    			|| trigger == Integer.parseInt(Constants.SERVICE_USER_TRIGER)
    			|| trigger == Integer.parseInt(Constants.SERVICE_CONN_TRIGER)) {
    		// will to check interval time 
    		if(getPolicy() == true) {
    			runService();
    		}
    	} else {
    		// do nothing
    	}
    }
    
    private void runService() {
    	Intent config = ConfigService.getIntent();
    	mContext.startService(config);
        LogUtil.LogOut(3, LOGTAG, "==> runService ");
    }
    
    private boolean getPolicy() {
    	boolean ret = true;
    	
    	//检查当前是否可能为初始访问高峰
    	checkDelayTime();

    	//2. 时间间隔大于最小要求
    	long curTime = System.currentTimeMillis();
    	long delayPollTime = dataHelper.getCfgPolicy(Constants.DELAY_CONFIG_TIME);
    	long lastPollTime = dataHelper.getCfgPolicy(Constants.LAST_CONFIG_TIME);
    	long waitTime = (curTime - lastPollTime)/1000;
    	
    	if (curTime < delayPollTime) {
    		//回避访问高峰
    		ret = false;
    	} else {
        	if (waitTime/60 < this.minTime) {
        		//不能频繁尝试
        		ret = false;
        	}
    	}

    	RecordtoFile.recordPushInfo(RecordtoFile.REASON_NET_FIND, RecordtoFile.ACTION_SERVICE_START,
				System.currentTimeMillis(), 
				RecordtoFile.ACTION_SERVICE_START, 
				System.currentTimeMillis()+1*10*1000,
				"ConfigPolicy_getPolicy: delayPollTime="+StringUtils.timeLong2Date(delayPollTime) +
				"curTime="+StringUtils.timeLong2Date(curTime) +"waitedTime="+waitTime +"s, ret="+ret,
				1);

    	return ret;
    }
    
    // avoid to pulse request pressure on server
    private void checkDelayTime() {
    	long delay = this.maxCount * DELAY_STEP_TIME;
    	
    	Calendar now = Calendar.getInstance();
    	int hour = now.get(Calendar.HOUR);
    	int min = now.get(Calendar.MINUTE);
    	LogUtil.LogOut(3, LOGTAG, "getDelayTime delay="+delay +"; hour="+hour + " min="+min);
    	
    	long rand = 0;
    	if ((hour >= notifier.getNotificationStartTime()) &&
    			(hour*60 + min) < (notifier.getNotificationStartTime()*60 + delay)) {
    		//需要进行处理，获取一个延迟时间(毫秒)
        	rand = (long)(Math.random()*delay*60)*1000;	//产生0-dalay范围内的双精度随机数
        	
        	long expectedTime = rand + System.currentTimeMillis();
        	dataHelper.saveCfgPolicy(Constants.DELAY_CONFIG_TIME, expectedTime);
        	
        	LogUtil.LogOut(3, LOGTAG, "getDelayTime rand="+rand 
        			+", expectedTime="+StringUtils.timeLong2Date(expectedTime));
    	}
    	
    	LogUtil.LogOut(3, LOGTAG, "getDelayTime delay="+delay + ", rand="+rand);
    }
    
    private boolean checkNetState() {
    	boolean ret = false;
    	
        NetworkInfo networkInfo = ((ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.isConnected()) {
            	ret = true;
            }
            
            RecordtoFile.recordPushInfo(RecordtoFile.REASON_NET_FIND, RecordtoFile.ACTIONT_UNKNOWN,
    				System.currentTimeMillis(), 
    				RecordtoFile.ACTION_SERVICE_START, 
    				System.currentTimeMillis()+1*10*1000,
    				"ConfigPolicy_checkNetState: Network_Type="+networkInfo.getTypeName() 
    				+", Network_State="+networkInfo.getState(),
    				1);
            
            LogUtil.LogOut(3, LOGTAG, "Network_Type="+networkInfo.getTypeName()
        			+ ", Network_State = "+networkInfo.getState() +", net="+ret);
        }
        
        return ret;
    }
}