package com.alipay.android.ui.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants.BehaviourID;
import com.alipay.android.longlink.BarcodeHelper;
import com.alipay.android.longlink.ISocketResultNotifer;
import com.alipay.android.longlink.LongLinkServiceManager;
import com.alipay.android.longlink.ScreenScaleParam;
import com.alipay.android.ui.bean.VerifyCode;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.ui.widget.NumberFlowIndicator;
import com.alipay.android.util.HeadsetPlug;
import com.alipay.android.util.LogUtil;
import com.alipay.sonicwavenfc.SonicWaveNFC;
import com.alipay.sonicwavenfc.SonicWaveNFCHandler;
import com.eg.android.AlipayGphone.R;

public class AvailableVoucherAdapter extends PagerAdapter implements HeadsetPlug.HeadsetCallBack{
	private BaseViewController mContext;
	private List<VerifyCode> mVerifyCodeList;
	private boolean mSupportBarcode;
	private HashMap<Integer, View> mHashMap; 
	private LayoutInflater mLayoutInflater;
	private SonicWaveNFC mSonicWaveNFC;
	private LongLinkServiceManager mLongLinkServiceManager;
	private NumberFlowIndicator mNumberFlowIndicator;
	private HeadsetPlug headsetPlug;
	private boolean supportSound;
	private boolean isYimaCode;
	private Integer mCodeTextColor;//二维码字体颜色
	
	public AvailableVoucherAdapter(BaseViewController context, List<VerifyCode> verifyCodeList,LongLinkServiceManager longLinkServiceManager
			,NumberFlowIndicator numberFlowIndicator,Integer codeTextColor) {
		mContext = context;
		mVerifyCodeList = verifyCodeList;
		mLayoutInflater = LayoutInflater.from(context.getRootController());
		mHashMap = new HashMap<Integer, View>();
		mSonicWaveNFC = SonicWaveNFC.getInstance(); 
		mSonicWaveNFC.initSonicWaveNFC(context.getRootController());// 实例构造后，需要检测耳机是否 插入
		mLongLinkServiceManager = longLinkServiceManager;
		mNumberFlowIndicator = numberFlowIndicator;
		headsetPlug = new HeadsetPlug();
		headsetPlug.registerHeadsetPlugReceiver(mContext.getRootController());
		mCodeTextColor = codeTextColor;
	}
	
	/**
	 * 是否支持二维码显示
	 */
	public void setSupportBarcode(boolean supportBarcode){
		mSupportBarcode = supportBarcode;
	}
	
	public void isYimaCode(boolean isYimaCode){
		this.isYimaCode = isYimaCode;
	}
	
	@Override  
    public void destroyItem(View container, int position, Object object) {  
		
    }
	
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
	
	public void stopsenddata() {
		if(mSonicWaveNFC != null)
			mSonicWaveNFC.stopSendData();
		
		headsetPlug.unRegisterHeadsetPlugReceiver();
	}
	
	private void hideBarcodeAndSound(ImageView soundIcon,ImageButton soundUseImage,ImageView barcodeImage,
			ImageView barcodeIcon,TextView soundUseTip) {
		barcodeImage.setVisibility(View.INVISIBLE);
		barcodeIcon.setVisibility(View.INVISIBLE);
		soundIcon.setVisibility(View.INVISIBLE);
		soundUseImage.setVisibility(View.INVISIBLE);
		soundUseTip.setVisibility(View.INVISIBLE);
	}
	
	@Override  
    public Object instantiateItem(View container, int position) {
        View itemView; 
        final TextView codeText ;
    	final ImageView soundIcon ;
    	final ImageButton soundUseImage ;
    	final ImageView barcodeImage ;
    	final ImageView barcodeIcon ;
    	final TextView soundUseTip ;
        if(mHashMap.containsKey(position)){
            itemView = mHashMap.get(position);
        }else{  
        	itemView = mLayoutInflater.inflate(R.layout.voucher_detail_item, null);
            mHashMap.put(position, itemView);
            ((ViewPager) container).addView(itemView);  
        }
        
        codeText = (TextView) itemView.findViewById(R.id.voucherCode);
    	soundIcon = (ImageView) itemView.findViewById(R.id.use_sound_icon);
    	soundUseImage = (ImageButton) itemView.findViewById(R.id.soundWave);
    	barcodeImage = (ImageView) itemView.findViewById(R.id.voucherBarcode);
    	barcodeIcon = (ImageView) itemView.findViewById(R.id.voucher_barcode_icon);
    	soundUseTip = (TextView) itemView.findViewById(R.id.soundWaveTip);
    	
    	if(mCodeTextColor != null){
    		codeText.setTextColor(mCodeTextColor);
    	}
        VerifyCode curVerifyCode = mVerifyCodeList.get(position);
        final String dynamicId = curVerifyCode.getDynamicId();
        final String qrCodeStr =  curVerifyCode.getQrCodeStr();
        final String message = curVerifyCode.getMessage();
		
		final String verifyCode = curVerifyCode.getVerifyCode();
		
		if(verifyCode != null && verifyCode.length() >= 16){
			codeText.setTextSize(20);
		}else{
			codeText.setTextSize(26);
		}
		
		String formatedCode = "";
		char[] codeChars = verifyCode.toCharArray();
		int length = codeChars.length;
		int blankCharLength = (length - 1) / 4;
		
		if(blankCharLength > 0){
			char[] newChars = new char[length + blankCharLength];
			int newCharIndex = 0; 
			for(int i = 0;i < length;i++){
				if(i > 0 && i  % 4 == 0){
					newChars[newCharIndex++] = ' ';
					newChars[newCharIndex++] = codeChars[i];
				}else
					newChars[newCharIndex++] = codeChars[i];
			}
			
			formatedCode = new String(newChars);
		}else
			formatedCode = verifyCode;
		
		codeText.setText(formatedCode);
		hideBarcodeAndSound(soundIcon,soundUseImage,barcodeImage,
				barcodeIcon,soundUseTip);
		
		soundUseImage.setOnClickListener(new SoundButtonClickListener(dynamicId));
		if(mSupportBarcode && !isSoundSupported(dynamicId)){
			barcodeImage.setVisibility(View.VISIBLE);
			setBarcodeImage(barcodeImage, qrCodeStr, (message != null && !"".equals(message)) ? message : verifyCode);
		}else if(!mSupportBarcode && isSoundSupported(dynamicId)){
			soundUseImage.setVisibility(View.VISIBLE);
			soundUseTip.setVisibility(View.VISIBLE);
			supportSound = true;
		}else if(mSupportBarcode && isSoundSupported(dynamicId)){
			soundUseImage.setVisibility(View.VISIBLE);
			barcodeIcon.setVisibility(View.VISIBLE);
			soundUseTip.setVisibility(View.VISIBLE);
			supportSound = true;
			barcodeIcon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					barcodeIcon.setVisibility(View.INVISIBLE);
					soundIcon.setVisibility(View.VISIBLE);
					soundUseImage.setVisibility(View.INVISIBLE);
					barcodeImage.setVisibility(View.VISIBLE);
					soundUseTip.setVisibility(View.INVISIBLE);
					soundIcon.setClickable(true);
					
					setBarcodeImage(barcodeImage, qrCodeStr, (message != null && !"".equals(message)) ? message : verifyCode);
					AlipayLogAgent.writeLog(mContext.getRootController(), BehaviourID.CLICKED, "walletTicket", "myTicketDetails", "myTicketDetails", "changeQRCodeIcon");
				}
			});
			
			soundIcon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					barcodeIcon.setVisibility(View.VISIBLE);
					soundIcon.setVisibility(View.INVISIBLE);
					soundUseImage.setVisibility(View.VISIBLE);
					barcodeImage.setVisibility(View.INVISIBLE);
					soundUseTip.setVisibility(View.VISIBLE);
					barcodeIcon.setClickable(true);
					
					AlipayLogAgent.writeLog(mContext.getRootController(), BehaviourID.CLICKED, "walletTicket", "myTicketDetails", "myTicketDetails", "changeSoundWaveIcon");
				}
			});
		}
		
		if(supportSound && !headsetPlug.hadRegisted()){
			headsetPlug.registerHeadsetCallback(this);
			if(headsetPlug.isHeadsetOn(mContext.getRootController()))
				popuOutTip();
		}
        return itemView;  
    }

	private void setBarcodeImage(final ImageView barcodeImage, final String qrCodeStr, final String verifyCode) {
		if(isYimaCode){
			if(qrCodeStr != null && !"".equals(qrCodeStr)){
				byte[] codeByte = getBitmapArray(qrCodeStr);
				if (null != codeByte) {
					Bitmap source = BitmapFactory.decodeByteArray(codeByte, 0, codeByte.length);
					Rect rect = BarcodeHelper.getBarcodeSize(mContext.getRootController());
					ScreenScaleParam param = BarcodeHelper.detectScreenLevel(mContext.getRootController());
					int curScale = (int) (40 * param.getCurScale());
					Bitmap barcodeBitmap = Bitmap.createScaledBitmap(source, rect.width() - curScale, rect.height() - curScale, false);
					if (null != barcodeBitmap) {
						barcodeImage.setImageBitmap(barcodeBitmap);
					}
				}
			}else if(verifyCode != null && !"".equals(verifyCode)){
				requestBarcode(barcodeImage, verifyCode);
			}
		}else{
			requestBarcode(barcodeImage, verifyCode);
		}
	}

	private byte[] getBitmapArray(String bitmapArray) {
		try {
			byte[] bitArray = com.alipay.android.util.Base64.decode(bitmapArray);
			return bitArray;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean isSoundSupported(String dynamicId){
		return dynamicId != null && !"".equals(dynamicId);
	}
	
	private void requestBarcode(final ImageView barcodeImage, String verifyCode) {
		BarcodeHelper.getBarcodeImg(false,verifyCode,new Handler(){
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case R.id.encode_succeeded:
					Bitmap resultBit = (Bitmap) msg.obj;
					if(resultBit !=null){
						Bitmap barcodeBitmap = getBarcode(resultBit);
						LogUtil.logOnlyDebuggable("Barcode", "encode result==> width="+ resultBit.getWidth()+";" + "height="+ resultBit.getHeight());
						
						if (null != barcodeBitmap) {
							barcodeImage.setImageBitmap(barcodeBitmap);
						}
					}
		            break;
				}
				super.handleMessage(msg);
		}},mContext.getRootController());
	}
	
	private Bitmap getBarcode(Bitmap bitmap) {
		if (null == bitmap) {
			return null;
		}
		
		ScreenScaleParam param = BarcodeHelper.detectScreenLevel(mContext.getRootController());

		Bitmap barcodeBitmap = Bitmap.createBitmap(bitmap, (int)(20 * param.getCurScale()), (int)(20 * param.getCurScale()), 
				bitmap.getWidth() - (int)(40 * param.getCurScale()), bitmap.getHeight() - (int)(40 * param.getCurScale()));
		bitmap.recycle();
		
		return barcodeBitmap;
	}
  
    @Override  
    public boolean isViewFromObject(View view, Object object) {  
        return view == object;  
    }

	@Override
	public int getCount() {
		if(mVerifyCodeList == null)
			return 0;
		return mVerifyCodeList.size();
	}
	
	private final class SoundButtonClickListener implements View.OnClickListener{
		private String dynamicId;
		public SoundButtonClickListener(String dynamicId){
			this.dynamicId = dynamicId;
		}
		
		@Override
		public void onClick(final View v) {
			final ImageButton soundUseImage = (ImageButton) v.findViewById(R.id.soundWave);
			//设置混音模式时的声音文件
			mSonicWaveNFC.setBkSoundWave4MixFromAsset(mContext.getRootController(), "SendingData.wav");
			//设置混音模式（0，超声；1，增强有声；2，混音）
			int iSoundType = SonicWaveNFC.NFC_SENDDATA_SOUNDTYPE_MIX;
			int iTimeoutSeconds = 5;
			int iVolume = 40; // 40%音量
			AlipayLogAgent.writeLog(mContext.getRootController(), BehaviourID.SUBMITED, null, null, "walletTicket", null, "myTicketDetails", "myTicketDetails", "soundWaveButton");
			// 发送数据
			try {
				mSonicWaveNFC.startSendData(dynamicId, iTimeoutSeconds, iSoundType, iVolume, mContext.getRootController(), new SonicWaveNFCHandler(){
					@Override
					public void onDataReceived(String arg0) {
					}

					@Override
					public void onReceiveDataFailed(int arg0) {
					}

					@Override
					public void onReceiveDataInfo(String arg0) {
					}

					@Override
					public void onReceiveDataStarted() {
					}

					@Override
					public void onReceiveDataTimeout() {
					}

					@Override
					public void onSendDataFailed(int arg0) {
					}

					@Override
					public void onSendDataInfo(String arg0) {
					}

					@Override
					public void onSendDataStarted() {
						if(mLongLinkServiceManager != null && !mLongLinkServiceManager.isSocketConnected()){
							//设置Socket连接监听
							mLongLinkServiceManager.setSocketListener(new ISocketResultNotifer.Stub() {
								//socket建立成功
								@Override
								public void onSocketCreateSuccess() throws RemoteException {
									closeProgressAndSendWave();
								}
								
								//socket建立失败
								@Override
								public void onSocketCreateFail() throws RemoteException {
									closeProgressAndSendWave();
								}
								
								private void closeProgressAndSendWave() {
									mContext.closeProgress();
									mLongLinkServiceManager.unregistSocketListener();
								}
							});
							
							mContext.showProgress();
							mLongLinkServiceManager.reConnect();
						}
						
						soundUseImage.setSelected(true);
						setAllEnable();
					}

					@Override
					public void onSendDataTimeout() {
						setAllAble();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void popuOutTip() {
		if(supportSound){
			View toastView = mLayoutInflater.inflate(R.layout.alipay_toast_view, null);
			TextView toastTextView = (TextView) toastView.findViewById(R.id.toastText);
			toastTextView.setText(R.string.headsetTip);
			Toast toast = new Toast(mContext.getRootController());
			toast.setView(toastView);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER,0,0);
			toast.show();
		}
	}
	
	private void setAllEnable(){
		for(Map.Entry<Integer, View> viewEntry : mHashMap.entrySet()){
			View curView = viewEntry.getValue();
			View soundButton = curView.findViewById(R.id.soundWave);
			soundButton.setEnabled(false);
		}
	}
	
	private void setAllAble(){
		for(Map.Entry<Integer, View> viewEntry : mHashMap.entrySet()){
			View curView = viewEntry.getValue();
			View soundButton = curView.findViewById(R.id.soundWave);
			soundButton.setEnabled(true);
			soundButton.setSelected(false);
		}
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		if(getCount() > 1)
			mNumberFlowIndicator.refreshPageCount();
		else
			mNumberFlowIndicator.setVisibility(View.GONE);
	}

	@Override
	public void onHeadsetOn() {
		popuOutTip();
	}

	@Override
	public void onHeadsetOff() {
	}
}