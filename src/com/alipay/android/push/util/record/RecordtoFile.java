package com.alipay.android.push.util.record;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alipay.android.push.util.LogUtil;

import android.os.Environment;
import android.util.Log;

/** 
 * Utility class for LogCat.
 */
public class RecordtoFile {
	
	public static String RECORD_TIME = "time";
	public static String RECORD_ACTION = "action";
	public static String RECORD_REASON = "triger";
	public static String RECORD_NEXT_TIME = "expected_time";
	public static String RECORD_NEXT_ACTION = "expected_action";
	
	//
	public static String REASON_SERVICE_START = "service_start";
	public static String REASON_SERVICE_STOP = "service_stop";
	
	public static String REASON_NET_FIND = "net_find";
	public static String REASON_NET_LOST = "net_lost";
	
	public static String REASON_CONN_EXCEPTION = "conn_exception";
	public static String REASON_CONN_TIMEOUT = "conn_timeout";
	
	public static String REASON_CLIENT_CHECK = "client_check";
	public static String REASON_CLIENT_RECONN = "client_reconn";
	
	public static String REASON_PHONE_BOOTED = "phone_booted";
	public static String REASON_SCREEN_ON = "screen_on";
	public static String REASON_USER_CHECKED = "user_checked";
	public static String REASON_PUSH_CHECKED = "push_checked";
	
	public static String REASON_XMPP_CONNED = "xmpp_conned";
	public static String REASON_XMPP_REGISTERD = "xmpp_registerd";
	public static String REASON_XMPP_SEND = "xmpp_send";
	public static String REASON_XMPP_RECV = "xmpp_recv";
	
	public static String REASON_UNKNOWN = "reason_unknown";
	
	//
	public static String ACTION_CONN = "connect";
	public static String ACTIONT_REGISTER = "register";
	public static String ACTIONT_KEEPLIVE = "keeplive";
	public static String ACTION_DISCONN = "disconnect";
	
	public static String ACTIONT_TIMER = "timer";
	public static String ACTIONT_STATUS_CHECK = "status_check";
	
	public static String ACTION_SERVICE_START = "service_start";
	public static String ACTION_SERVICE_STOP = "service_stop";
	
	public static String ACTIONT_UNKNOWN = "actioin_unknown";
	
	//
	private static String pathName="/sdcard/alipay/";   
	private static String fileName="push";  

	
    private static void writeFileToSD(File file, String info, int partern) {   

        try {
            if( file == null) {   
                Log.w("RecordtoFile", "the file is null.");   
                return;   
            } 
            
            String   nextLine   =   System.getProperty( "line.separator "); 
            if (nextLine != null) {
            	 Log.w("RecordtoFile", "writeFileToSD nextLine separator is null.");
            	 info = info + nextLine;
            }
            
            
            FileOutputStream stream = new FileOutputStream(file, true);
            byte[] buf = info.getBytes();
            stream.write(buf);             
            stream.close();   
               
        } catch(Exception e) {   
            Log.e("RecordtoFile", "Error on writeFilToSD.");   
            e.printStackTrace();   
        }   
    }
    
    private static File checkFileOfSD(int partern) {
    	File file = null;
    			
    	String sdStatus = Environment.getExternalStorageState();   
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {   
            Log.w("RecordtoFile", "SD card is not avaiable/writeable right now.");   
            return null;   
        }
        
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    	LogUtil.LogOut(3, "RecordtoFile", "checkFileOfSD date="+date);
        
        try { 
            File path = new File(pathName);
            if( !path.exists()) {   
                Log.d("RecordtoFile", "Create the path:" + pathName);   
                path.mkdir();   
            } 
            
            file = new File(pathName + fileName + "_" +date +".log"); 
            if( !file.exists()) {   
                Log.d("RecordtoFile", "Create the file:" + fileName);   
                file.createNewFile();  
                
                switch (partern) {
	                case 1:
	                	FileOutputStream stream = new FileOutputStream(file);
	                	
	                	StringBuffer info = new StringBuffer();
	                	info.append("Time\t\t\t")
	                	.append("Reason\t\t\t").append("Action\t\t\t")
	                	.append("NextTime\t\t\t").append("NextAction\t\t\t")
	                	.append("Note\t\t\t")
	                	.append("\n");
	                	
	                	byte[] buf = info.toString().getBytes(); 
	                	stream.write(buf);
	                	
	                	StringBuffer info2 = new StringBuffer();
	                	info.append("====================================================================================================")
	                	.append("\n");
	                	
	                	byte[] buf2 = info.toString().getBytes(); 
	                	stream.write(buf2);
	                	
	                	stream.close(); 
	                	break;
	                default:
	                	break;
                }
               
            }
            
            return file;
               
        } catch(Exception e) {   
            Log.w("RecordtoFile", "Error on writeFilToSD.");   
            e.printStackTrace(); 
            return null;
        }  
    }
    
    private static String formatString(String reason, String action, String time, 
    		String nextAction, String nextTime, String notes, int partern) { 
    	String info = null;
    	
    	switch (partern) {
        case 1:
        	StringBuffer infoBuffer = new StringBuffer();
        	infoBuffer.append(time).append(",    ")
        	.append(reason).append(",    \t\t")
        	.append(action).append(",    \t\t")
        	.append(nextTime).append(",    ")
        	.append(nextAction).append(",    \t\t")
        	.append(notes)
        	.append("\n");
        	
        	info = infoBuffer.toString();
        	break;
        default:
        	break;
    	}
    	return info;
    }
    
    public static void recordPushInfo(String reason, String action, long time, 
    		String nextAction, long nextTime, String notes, int partern) {
    	if (LogUtil.LOG_LEVEL > 3) {
    		String curTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(time);
        	String expectTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(nextTime);
        	
        	Log.d("RecordtoFile", "curTime="+curTime);  
        	Log.d("RecordtoFile", "expectTime="+expectTime);  
        	
        	File recordFile = checkFileOfSD(partern);
        	if (recordFile != null) {
        		String record = formatString(reason, action, curTime, nextAction, expectTime, notes, partern);
        		Log.d("RecordtoFile", "current record="+record); 
        		
        		writeFileToSD(recordFile, record, partern);
        	}
    	}
    	
    	return;
    }

    public static void recordPushInfo(int partern) {
    	if (LogUtil.LOG_LEVEL > 3) {
    		String curTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(System.currentTimeMillis());
        	
        	Log.d("RecordtoFile", "curTime="+curTime); 
        	
        	File recordFile = checkFileOfSD(partern);
        	if (recordFile != null) {
        		StringBuffer line = new StringBuffer();
        		line.append("====================================================================================================")
            	.append("\n");
            	
            	writeFileToSD(recordFile, line.toString(), partern);
        	}
    	}
    	
    	return;
    }




}
