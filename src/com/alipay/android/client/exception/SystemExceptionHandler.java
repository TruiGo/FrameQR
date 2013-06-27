package com.alipay.android.client.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
public class SystemExceptionHandler implements UncaughtExceptionHandler {   
   private Thread.UncaughtExceptionHandler mDefaultHandler;   
   private static SystemExceptionHandler INSTANCE;   
   private Context mContext;   
   
   private SystemExceptionHandler() {}   
   public static SystemExceptionHandler getInstance() {   
       if (INSTANCE == null) {   
           INSTANCE = new SystemExceptionHandler();   
       }   
       return INSTANCE;   
   }   
   
   public void init(Context ctx) {   
       mContext = ctx;   
       mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();   
       Thread.setDefaultUncaughtExceptionHandler(this);  
   }   
 
   @Override  
   public void uncaughtException(Thread thread, Throwable ex) {  
	   handleException(ex);
	   if(mDefaultHandler != null){
		   mDefaultHandler.uncaughtException(thread, ex);  
		   
	   }
   }   
 
   private void handleException(Throwable ex) {   
       if (ex == null) {   
           return;   
       }   
       saveErrorInfoToFile(ex,Constants.MONITORPOINT_EXCEPTION);   
    } 
   
    private void saveErrorInfoToFile(Throwable ex,String exceptionType){
    	 String result = getExceptionMsg(ex);
         if(result != null){
	    	StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
	    	AlipayLogAgent.onError(mContext, 
	    			result, 
	    			storageStateInfo.getValue(Constants.STORAGE_CURRENTVIEWID),
	    			exceptionType);
         }
    }
  
    public void saveConnInfoToFile(Throwable ex,String exceptionType) {
        String result = getExceptionMsg(ex);
        if(result != null && result.equals(previousException)){
        	return;
        }
        
        previousException = result;
        if(result != null){
	        StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
	    	/*AlipayLogAgent.onEvent(mContext,
	    			exceptionType,
		 			"operationType=" + storageStateInfo.getValue(Constants.STORAGE_REQUESTTYPE) +"|" + result, 
		 			storageStateInfo.getValue(Constants.STORAGE_CURRENTVIEWID),
	    			storageStateInfo.getValue(Constants.STORAGE_APPID),
	    			storageStateInfo.getValue(Constants.STORAGE_APPVERSION),
	    			storageStateInfo.getValue(Constants.STORAGE_PRODUCTID),
	    			application.getUserData()==null?"":application.getUserData().getUserId(),
		 			storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
		 			storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/
	    	AlipayLogAgent.onError(mContext, 
	    			"operationType=" + storageStateInfo.getValue(Constants.STORAGE_REQUESTTYPE) +"|" + result, 
	    			storageStateInfo.getValue(Constants.STORAGE_CURRENTVIEWID),
	    			exceptionType);
        }
    }
    
    private String previousException = "";
	public String getExceptionMsg(Throwable ex) {
		Writer info = new StringWriter();   
        PrintWriter printWriter = new PrintWriter(info);   

        Throwable cause = ex.getCause();   
        if(cause == null){
        	ex.printStackTrace(printWriter);
        }
        
        while (cause != null) {   
            cause.printStackTrace(printWriter);   
            cause = cause.getCause();   
        }  
        printWriter.close(); 
        return info.toString();
	}
}  
