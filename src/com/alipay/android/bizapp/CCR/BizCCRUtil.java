package com.alipay.android.bizapp.CCR;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.alipay.android.appHall.CacheManager;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.partner.PartnerInfo;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.log.Constants;
import com.alipay.android.security.Des;
import com.alipay.android.ui.bean.BankCardInfo;
import com.eg.android.AlipayGphone.R;

public class BizCCRUtil {

    /**
     * 创建新用户还款请求数�?
     * @param accountStr 
     * @param currentBankCardInfo 
     * @param context
     * @param bankStr
     * @return
     */
    public static HashMap<String, String> prepareNewUData(String creditCardNumber, String holderName, String bankMark, String depositAmmount,String cardNumberType, CCRBankCardInfo currentCCRBankCardInfo, String accountStr){
        HashMap<String, String> dataMap = new HashMap<String, String>();
        dataMap.put(Constant.RQF_CCR_CCNOMBER, creditCardNumber);
        dataMap.put(Constant.RQF_CCR_DEPOSITAMOUNT, depositAmmount);
        dataMap.put(Constant.RQF_CCR_HOLDERNAME, holderName);
        dataMap.put(Constant.RQF_CCR_BANKMARK, bankMark);
        dataMap.put(Constant.RQF_CCR_CARDNUMBERTYPE, cardNumberType);
        buildOutsideParam(dataMap,creditCardNumber,holderName,bankMark,depositAmmount,currentCCRBankCardInfo,accountStr);
        return dataMap;
    }
    /**
     * 银行卡列表请求数�?
     * @param context
     * @param bankStr
     * @return
     */
    public static HashMap<String, String> prepareGetBankData(String type){
    	HashMap<String, String> dataMap = new HashMap<String, String>();
        dataMap.put(Constant.RQF_CCR_QUERYTYPE, type);
        return dataMap;
    }
    /**
     * 获取某个银行信息
     * @param type
     * @param bankMark
     * @return
     */
    public static HashMap<String, String> prepareGetBankInfo(String type,String bankMark){
    	HashMap<String, String> dataMap = new HashMap<String, String>();
        dataMap.put(Constant.RQF_CCR_QUERYTYPE, type);
        dataMap.put(Constant.RQF_CCR_BANKMARK, bankMark);
        return dataMap;
    }
    /**
     * 创建还款订单请求数据
     * @param cardIndexNumber
     * @param holderName
     * @param bankMark
     * @param depositAmount
     * @param partnerInfo 
     * @return
     */
    public static HashMap<String,String>prepareCreateCreditCardTrade(String cardIndexNumber,String holderName,String bankMark,String depositAmount, CCRBankCardInfo currentCCRBankCardInfo){
    	HashMap<String,String> dataMap = new HashMap<String,String>();
    	dataMap.put(Constant.RQF_CCR_CARDINDEXTNUMBER, cardIndexNumber);
    	dataMap.put(Constant.RQF_CCR_HOLDERNAME, holderName);
    	dataMap.put(Constant.RQF_CCR_BANKMARK, bankMark);
    	dataMap.put(Constant.RQF_CCR_DEPOSITAMOUNT, depositAmount);
    	buildOutsideParam(dataMap,cardIndexNumber,holderName,bankMark,depositAmount,currentCCRBankCardInfo,null);
    	return dataMap;
    }
    /**
     * 组装外部订单参数
     * @param accountStr 
     * @param partnerInfo
     */
    private static void buildOutsideParam(HashMap<String,String> dataMap,String cardIndexNumber,String holderName,String bankMark,String depositAmount,CCRBankCardInfo currentCCRBankCardInfo, String accountStr) {
		if(currentCCRBankCardInfo != null && currentCCRBankCardInfo.getPartnerInfo() != null && dataMap != null){
			PartnerInfo partnerInfo = currentCCRBankCardInfo.getPartnerInfo();
			if(partnerInfo.getSourceId() != null){
				dataMap.put(Constant.ORDER_SOURCE, partnerInfo.getSourceId() + outOrderChange(cardIndexNumber,holderName,bankMark,depositAmount,currentCCRBankCardInfo,accountStr));
			}
			if(partnerInfo.getOutTradeNo() != null){
				dataMap.put(Constant.OUT_ORDER_NO, partnerInfo.getOutTradeNo());
				
			}
//			if(partnerInfo.getOutOrderChange() != null){
//				dataMap.put(Constant.OUT_ORDER_CHANGE, partnerInfo.getOutOrderChange());
//			}
		}
		
	}
    
    private static String outOrderChange(String cardIndexNumber,String holderName,String bankMark,String depositAmount,CCRBankCardInfo currentCCRBankCardInfo, String accountStr){
    	if(currentCCRBankCardInfo != null && currentCCRBankCardInfo.getBankCardInfo() != null && currentCCRBankCardInfo.getPartnerInfo() != null && currentCCRBankCardInfo.getPartnerInfo().getSourceId() != null){
    		BankCardInfo bankCardInfo = currentCCRBankCardInfo.getBankCardInfo();
//    		PartnerInfo partnerInfo = currentCCRBankCardInfo.getPartnerInfo();
//    		String outOrderChange = null;
    		String modify = "";
    		if(cardIndexNumber != null 
    				&& currentCCRBankCardInfo.getCreditCardTailNumber(cardIndexNumber)!= null 
    				&& bankCardInfo.getCardTailNumber() != null
    				&&!bankCardInfo.getCardTailNumber().equalsIgnoreCase(currentCCRBankCardInfo.getCreditCardTailNumber(cardIndexNumber))){
//    			outOrderChange = Constant.CARDNO;
    			modify =  "_" + Constant.MODIFY;
    		}
    		if(accountStr != null && accountStr.length() >= 4 && bankCardInfo.getCardTailNumber() != null 
    				&& accountStr.substring(accountStr.length() - 4, accountStr.length()).equals(bankCardInfo.getCardTailNumber())){
    			modify =  "";
    		}else if(accountStr == null){
    			modify =  "";
    		}else{
    			modify =  "_" + Constant.MODIFY;
    		}
    		if(bankMark != null
    				&& bankCardInfo.getBankMark() != null
    				&& !bankCardInfo.getBankMark().equalsIgnoreCase(bankMark)){
//    			outOrderChange += "|" + Constant.BANK_MARK;
    			modify = "_" + Constant.MODIFY;
    		}
    		if(holderName != null 
    				&& bankCardInfo.getUserName() != null
    				&& !bankCardInfo.getUserName().equalsIgnoreCase(holderName)){
//    			outOrderChange += "|" + Constant.HOLDER_NAME;
    			modify = "_" + Constant.MODIFY;
    		}
    		if(depositAmount != null
    				&& currentCCRBankCardInfo.getAmount() != null
    				&& !depositAmount.equalsIgnoreCase(currentCCRBankCardInfo.getAmount())){
//    			outOrderChange += "|" + Constant.AMOUNT;
    		}
//    		partnerInfo.setOutOrderChange(outOrderChange);
    		return modify;
    	}
    	return null;
    }
    
	/**
     * 生成设置还款提醒请求数据
     * @return
     */
    public static HashMap<String, String> prepareSetCreditCardDepositAlert(String cardIndexNumber,String alertDate){
    	HashMap<String, String> dataMap = new HashMap<String, String>();
    	dataMap.put(Constant.RQF_CCR_CARDINDEXTNUMBER, cardIndexNumber);
    	dataMap.put(Constant.RQF_CCR_ALERT_DATE, alertDate);
    	return dataMap;
    }
    /**
     * 得到支持信用卡列�?
     * @param context
     * @param bankStr
     * @return
     */
    public static ArrayList<CCRBankCardInfo> getSupportBank(Activity context, String bankStr ,String saveKey){
	    	CacheManager cm = CacheManager.getInstance(context);
			cm.putObject(saveKey, getBankItemInfoList(bankStr));
    	
    	return null;
    }
    /**
     * 将str信用卡列表数据转换为ArrayList
     * @param bankListStr
     * @return
     */
	private static ArrayList<CCRBankCardInfo> getBankItemInfoList(String bankListStr) {
		if(bankListStr != null && bankListStr.length() != 0){
			ArrayList<CCRBankCardInfo> bankInfo = new ArrayList<CCRBankCardInfo>();
			CCRBankCardInfo bankItemInfo = null;
			JSONArray banklist;
			try {
				banklist = new JSONArray(bankListStr);
				if (banklist != null) {
					for (int i = 0; i < banklist.length(); i++) {
						JSONObject responsor = banklist.getJSONObject(i);
						bankItemInfo = new CCRBankCardInfo();
						bankItemInfo.processCCRBankInfoJson(responsor);
						bankInfo.add(bankItemInfo);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return bankInfo;
		}else{
			return null;
		}
	}

    /**
     * 保存已存信用卡列�?
     * @param context
     * @param bankStr
     * @return
     */
    public static void setSavedBank(RootActivity context, String bankStr){
    	String key = Constant.ALIPAY_INFO;
    	String desStr = Des.encrypt(bankStr, key);
    	AlipayDataStore alipayDataStore = new AlipayDataStore(context);
    	alipayDataStore.putString(context.getUserId() + AlipayDataStore.BANK_CCR_SAVED_BANKLIST, desStr);
    }
    /**
     * 删除已存信用卡
     * @param context
     */
    public static void deleteSavedBank(RootActivity context){
    	AlipayDataStore alipayDataStore = new AlipayDataStore(context);
    	alipayDataStore.romve(context.getUserId() + AlipayDataStore.BANK_CCR_SAVED_BANKLIST);
    }
    /**
     * 得到已存信用卡列�?
     * @param context
     * @param bankStr
     * @return
     */
    public static ArrayList<CCRBankCardInfo> getSavedBank(String bankStr){
    	return getBankItemInfoList(bankStr);
    }
    /**
     * 得到已存信用卡列�?
     * @param context
     * @param bankStr
     * @return
     */
    public static ArrayList<CCRBankCardInfo> getSavedBank(RootActivity context){
    	AlipayDataStore alipayDataStore = new AlipayDataStore(context);
    	String bankstr = alipayDataStore.getString(context.getUserId() + AlipayDataStore.BANK_CCR_SAVED_BANKLIST);
    	if(bankstr.length()>0){
	    	String key = Constant.ALIPAY_INFO;
	    	String desStr = Des.decrypt(bankstr, key);
	    	return getSavedBank(desStr);    
    	}
    	return null;
    }
    
    /**
     * 检验信用卡账户
     * @param context
     * @param account
     * @return
     */
    public static boolean checkAccount(CCRActivity context, String account){
    	int tResult = 0;
    	tResult = AlipayInputErrorCheck.CheckCreditCardNO(account);
		if (tResult != AlipayInputErrorCheck.NO_ERROR) {
            // check error.
            String warningMsg;

            if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT){
				 warningMsg = context.getResources().getString(R.string.WarningInvalidBankId);
			 }else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT){
				 warningMsg = context.getResources().getString(R.string.WarningBankIdEmpty);
			 }else{
				 warningMsg = "UNKNOWN_ERROR TYPE = "+ tResult;
			 }
			 
			 BaseHelper.recordWarningMsg(context, warningMsg,Constants.CCRNEWUSERVIEW);
			 
            if (!warningMsg.equals("")) {
            	
   			 context.getDataHelper().showDialog(context, R.drawable.infoicon, 
				        context.getResources().getString(R.string.WarngingString),warningMsg, 
				        context.getResources().getString(R.string.Ensure), null, null, null, null, null);
            }	 
            return false;
        }	
		return true;
    	
    }
    /**
     * 检验用户实�?
     * @param context
     * @param account
     * @return
     */
    public static boolean checkName(CCRActivity context, String name){
    	int tResult = 0;
    	tResult = AlipayInputErrorCheck.CheckCCRAccountName(name);
		 if (tResult != AlipayInputErrorCheck.NO_ERROR){
			 // check error.
			 String warningMsg;
			 if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT)
				{
					warningMsg = context.getResources().getString(R.string.WarningCCRInvalidRealName);
				}
				else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT)
				{
					warningMsg = context.getResources().getString(R.string.WarningRealNameEmpty);
				}
				else if (tResult == AlipayInputErrorCheck.ERROR_OUTOF_RANGE)
				{
					warningMsg = context.getResources().getString(R.string.WarningRealNameOutRange);
				}else{
				 warningMsg = "UNKNOWN_ERROR TYPE = "+ tResult;
			 }
			 
			 BaseHelper.recordWarningMsg(context, warningMsg,Constants.CCRNEWUSERVIEW);
			 
   			 context.getDataHelper().showDialog(context, R.drawable.infoicon, 
				        context.getResources().getString(R.string.WarngingString),warningMsg, 
				        context.getResources().getString(R.string.Ensure), null, null, null, null, null);
			 return false;
		 }
		return true;
    
    }
    /**
     * 检验金额输�?
     * @param context
     * @param account
     * @return
     */
    public static boolean checkMoney(CCRActivity context, String inMoney){
    	int tResult = 0;
    	tResult = AlipayInputErrorCheck.CheckMoney(inMoney);
		 String errMsg="";
		 if (tResult != AlipayInputErrorCheck.NO_ERROR){
				 // check error.				 
				 if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT){
					 errMsg = context.getResources().getString(R.string.MoneyFormatError);
		    		}else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT){
		    			errMsg = context.getResources().getString(R.string.MoneyEmpty);
		    		}else{
		    			errMsg = "UNKNOWN_ERROR TYPE = "+ tResult;
		    		}
				 
		 }else{
			 try{
				 float money = new Float(inMoney);
				 if(money>10000){
					 errMsg =  context.getResources().getString(R.string.MoneyOver);
				 }
			 }catch(Exception e){
				 e.printStackTrace();
			 }
		 }
		 if(errMsg.length()>0){
			 BaseHelper.recordWarningMsg(context, errMsg,Constants.CCRNEWUSERVIEW);
			 
			 context.getDataHelper().showDialog(context, R.drawable.infoicon, 
					        context.getResources().getString(R.string.WarngingString),errMsg, 
					        context.getResources().getString(R.string.Ensure), null, null, null, null, null);
			 return false;
		 }
		 return true;
    }
	public static String prepareCCDCUrl(String url, String requestData) {
		String requestUrl = url+"?"+requestData;
		return requestUrl;
	}
}
