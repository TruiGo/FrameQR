package com.alipay.android.ui.transfer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;

import com.alipay.android.client.SchemeHandler;
import com.alipay.android.client.baseFunction.TabItemCallback;
import com.alipay.android.core.ParamString;
import com.alipay.android.log.StorageStateInfo;
import com.alipay.android.ui.bean.TransferReceiver;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.ui.framework.RootController;

public class TransferRootControllerActivity extends RootController implements TabItemCallback{
	static TransferRootControllerActivity sRef; 
    static Intent sTempIntent;
    SchemeHandler mSchemeHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		needLogin = true;
		loginBackParam = TransferViewController.class.getName();
		super.onCreate(savedInstanceState);
		if (sRef == null)
            sRef = this;

        if ((Integer.parseInt(VERSION.SDK) > 13)
            && getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // Check to see that the Activity started due to an Android Beam
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
                sTempIntent = (Intent) this.getIntent().clone();
                this.finish();
                return;
            }
        }
        
    	String params = getIntent().getStringExtra("param");
    	if(params != null)
    		navigate2NextPage(params);
    	else
    		navigateTo("TransferView");
	}

	private void navigate2NextPage(String params) {
		String decodeParam = Uri.decode(params);
		if (decodeParam != null) {
			ParamString paramStr = new ParamString(decodeParam);
			String viewId = paramStr.getValue("viewId");
			if (viewId != null && "form".equals(viewId)) {
				TransferReceiver receiver = new TransferReceiver();
				receiver.recvMobile = paramStr.getValue("mobileNo");
				receiver.recvName = paramStr.getValue("userName");
				receiver.transferType = TransferType.FROMTOUCHPAL;
				navigateTo("ConfirmView", receiver);
			}
		}else{
			navigateTo("TransferView");
		}
	}

	@Override
	protected String getControllerClassPath() {
		 return TransferRootControllerActivity.class.getPackage().getName();
	}
	
	public BaseViewController getTopController(){
		if(isNotEmptyStack()){
			return mControllerStack.peek();
		}
		return null;
	}
	
	/*@Override
	protected void onPause() {
		if(isNotEmptyStack()){
			mControllerStack.peek().onPause();
		}
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		if(isNotEmptyStack()){
			mControllerStack.peek().onStop();
		}
		super.onStop();
	}*/
	
	/*@Override
	protected void onDestroy() {
		if(isNotEmptyStack()){
			mControllerStack.peek().onDestroy();
		}
		super.onDestroy();
	}*/
	
	/*@Override
	protected void onStart() {
		if(isNotEmptyStack()){
			mControllerStack.peek().onStart();
		}
		super.onStart();
	}*/
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
		storageStateInfo.clearValue();
	}
	
	void processIntent(Intent intent) {
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		// only one message sent during the beam
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		// record 0 contains the MIME type, record 1 is the AAR, if present
		if(isNotEmptyStack()){
			mControllerStack.peek().intentCallBack(new String(msg.getRecords()[0].getPayload()));
		}
	}

	private boolean isNotEmptyStack() {
		return !mControllerStack.isEmpty() && mControllerStack.peek() != null;
	}

	@Override
	public void onResume() {
		if ((Integer.parseInt(VERSION.SDK) > 13) && getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
			// Check to see that the Activity started due to an Android Beam
			if (sTempIntent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(sTempIntent.getAction())) {
				processIntent(sTempIntent);
				sTempIntent = null;
			}
		}
		if(mControllerStack.size() == 0){
			navigateTo("TransferView");
		}
		
		super.onResume();
	}

	@Override
	public void newUri(String params) {
		clearStack();
		navigate2NextPage(params);
	}
}