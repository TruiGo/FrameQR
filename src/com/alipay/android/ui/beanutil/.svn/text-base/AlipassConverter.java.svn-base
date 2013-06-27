package com.alipay.android.ui.beanutil;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.alipay.android.ui.bean.AlipassInfo;
import com.alipay.android.ui.bean.AlipassInfo.Barcode;
import com.alipay.android.ui.bean.AlipassInfo.Einfo;
import com.alipay.android.ui.bean.AlipassInfo.EvoucherInfo;
import com.alipay.android.ui.bean.AlipassInfo.FileInfo;
import com.alipay.android.ui.bean.AlipassInfo.Locations;
import com.alipay.android.ui.bean.AlipassInfo.Merchant;
import com.alipay.android.ui.bean.AlipassInfo.Platform;
import com.alipay.android.ui.bean.AlipassInfo.Style;
import com.alipay.android.ui.bean.VerifyCode;
import com.alipay.android.ui.bean.Voucher;
import com.alipay.android.ui.bean.Voucher.Mode;
import com.alipay.android.util.JsonConvert;

public class AlipassConverter {
	/**
	 * Alipass文件转换为AlipassInfo对象
	 * @param alipassJson
	 * @param alipassInfo
	 * @return
	 */
	public static AlipassInfo json2Alipass(JSONObject alipassJson,AlipassInfo alipassInfo){
		JsonConvert.json2Bean(alipassJson, alipassInfo);
		
		JSONObject platformJson = alipassJson.optJSONObject("platform");
		if(platformJson != null){
			alipassInfo.setPlatform(JsonConvert.json2Bean(platformJson, new Platform()));
		}
		
		JSONObject evoucherInfoJson = alipassJson.optJSONObject("evoucherInfo");
		if(evoucherInfoJson != null){
			EvoucherInfo evoucherInfo = new EvoucherInfo();
			alipassInfo.setEvoucherInfo(JsonConvert.json2Bean(evoucherInfoJson, evoucherInfo));
			
			JSONObject einfoInfoJson = evoucherInfoJson.optJSONObject("einfo");
			if(einfoInfoJson != null){
				Einfo einfo = new Einfo();
				evoucherInfo.seteInfo(JsonConvert.json2Bean(einfoInfoJson, einfo));
				
				JSONArray barcodeJson = einfoInfoJson.optJSONArray("barcode");
				if(barcodeJson != null){
					einfo.setBarcodeList(JsonConvert.jArry2BeanList(barcodeJson, new Barcode()));
				}
			}
			
			JSONArray locationsJson = evoucherInfoJson.optJSONArray("locations");
			if(locationsJson != null){
				evoucherInfo.setLocations(JsonConvert.jArry2BeanList(locationsJson, new Locations()));
			}
		}
		
		JSONObject styleJson = alipassJson.optJSONObject("style");
		if(styleJson != null){
			Style style = new Style();
			alipassInfo.setStyle(JsonConvert.json2Bean(styleJson, style));
		}
		
		JSONObject merchantJson = alipassJson.optJSONObject("merchant");
		if(merchantJson != null){
			Merchant style = new Merchant();
			alipassInfo.setMerchant(JsonConvert.json2Bean(merchantJson, style));
		}
		JSONObject fileInfoJson = alipassJson.optJSONObject("fileInfo");
		if(fileInfoJson != null){
			FileInfo fileInfo = new FileInfo();
			alipassInfo.setFileInfo(JsonConvert.json2Bean(fileInfoJson, fileInfo));
		}
		
//		String expiredEndDate = alipassInfo.getEvoucherInfo().getEndDate();
//		String voucherStatus = getLocalVoucherStatus(expiredEndDate);
		alipassInfo.setStatus(Voucher.Status.CAN_USE);	
		
    	return alipassInfo;
    }

	public static List<Voucher> aliapssInfo2VoucherInfo(List<AlipassInfo> alipassInfos){
		List<Voucher> resultVoucherList = new ArrayList<Voucher>();
		for(AlipassInfo alipassInfo : alipassInfos){
//			if(alipassInfo.getStatus() != null && alipassInfo.getStatus().equals(Voucher.Status.CAN_USE)){
				Voucher voucher  = new Voucher();
				voucher.setExpiredBeginDate(alipassInfo.getEvoucherInfo().getStartDate());
				voucher.setExpiredEndDate(alipassInfo.getEvoucherInfo().getEndDate());
				voucher.setMerchantName(alipassInfo.getMerchant().getMname());
				voucher.setStatus(alipassInfo.getStatus());
				
				voucher.setVoucherId(alipassInfo.getEvoucherInfo().getGoodsId());
				voucher.setVoucherName(alipassInfo.getEvoucherInfo().getTitle());
				voucher.setVoucherType(alipassInfo.getEvoucherInfo().getType());
				voucher.setGmtCreate(alipassInfo.getLastModified());
				voucher.setPassFileName(alipassInfo.getPassFileName());
				voucher.setMode(Mode.OFFLINE);
				voucher.setBackGroundColor(alipassInfo.getStyle() == null ? 0:alipassInfo.getStyle().getBackgroundColor());
				
				resultVoucherList.add(voucher);
//			}
		}
		return resultVoucherList;
	}
	
	
	public static List<VerifyCode> barcodes2VerifyCode(List<Barcode> barcodes){
		List<VerifyCode> verifyCodes = new ArrayList<VerifyCode>();
		if(barcodes != null){
			for(Barcode barcode : barcodes){
				String altText = barcode.getAltText();
				if(altText != null && !"".equals(altText)){
					VerifyCode verifyCode = new VerifyCode();
					verifyCode.setDynamicId("");//声波动态ID
					verifyCode.setQrCodeStr("");//二维码字符串 
					verifyCode.setVerifyCode(altText);
					verifyCode.setMessage(barcode.getMessage());
					verifyCode.setFormat(barcode.getFormat());
					verifyCode.setMessageEncoding(barcode.getMessageEncoding());
					
					verifyCode.setVerifyStatus("can_use");
					verifyCodes.add(verifyCode);
				}
			}
		}
		return verifyCodes;
	}
}
