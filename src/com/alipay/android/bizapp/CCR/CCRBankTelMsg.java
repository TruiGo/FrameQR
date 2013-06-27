package com.alipay.android.bizapp.CCR;

import android.text.format.Time;

/**
 * “@”替换新卡用户的信用卡的完整卡号，“$”替换信用卡卡号后四位
 * @author daping.gp
 *
 */
public enum CCRBankTelMsg {
    /** 银行信息  */
	CMB("CMB","ZD","1065795555","1065502010095555",""),
	CCB("CCB","CCZD#$#","95533","95533",""),
	ICBC("ICBC","CXZD#@#短信密码或密码器动态密码","95588","95588","95588"),
	COMM("COMM","cc账单#$","95559","95559","95559"),
	BOC("BOC","XYK#SJZDKT#$","95566","95566",""),
	GDB("GDB","XZD$","95508","95508",""),
	CITIC("CITIC","ZD$","106980095558","106980095558","106980095558"),
	CIB("CIB","30$","95561","95561",""),
	SPDB("SPDB","ZDCX $","95528","95528","95528"),
	CMBC("CMBC","ZD$","10657109009556800","1065502195568","1065902195568"),
	CEBBANK("CEBBANK","账单#$","95595","95595","95595"),
	NXBANK("NXBANK","ZD$","10657109095577000","106592010095577",""),
	NBBANK("NBBANK","ZD$","1065752574896528","1065505716096528","106590571100096528"),
	BJBANK("BJBANK","ZD$","106575257489601169","106550571609601169","10659057110009601169"),
	JSBANK("JSBANK","ZD$","106575257489280888","106550571609280888","10659057110009280888"),
	SHRCB("SHRCB","ZD$","10657525748962999","10655057160962999","1065905711000962999"),
	HZCB("HZCB","ZD$","10657525748996523","10655057160996523","1065905711000996523"),
	CQRCB("CQRCB","ZD$","106575257489966866","106550571609966866","10659057110009966866"),
	SJBANK("SJBANK","ZD$","10657525748996666","10655057160996666","1065905711000996666");
	
   /** 银行字母缩写 */
   private String bankId;
   //短信内容
   private String msg;
   //短信运营商类型（移动，联通，电信）
   private String ydTel;
   //联通短信号码
   private String ltTel;
   //电信短信号码
   private String dxTel;
   //运营商类型
   public String telType[] = new String[] {"中国移动","中国联通","中国电信"};
   //运营商类型对应mnc
   public String telTypeId[] = new String[] {"00","01","03"};


private CCRBankTelMsg(String bid,String m,String ydt,String ltt,String dxt) {
	   if(bid.equalsIgnoreCase("GDB")){
		   Time time =new Time("GMT+8");
		   time.setToNow();
		   m+=String.valueOf(time.year)+(time.month+1);
	   }
	   this.bankId = bid;
	   this.msg = m;
	   this.ydTel = ydt;
	   this.ltTel = ltt;
	   this.dxTel = dxt;
   }

   public String getBankId() {
		return bankId;
	}

	public String getMsg() {
		return msg;
	}

	public String getYdTel() {
		return ydTel;
	}

	public String getLtTel() {
		return ltTel;
	}

	public String getDxTel() {
		return dxTel;
	}
	
	public String[] getTelType() {
		return telType;
	}
	
	public String[] getTelTypeId() {
		return telTypeId;
	}
   /** 
    * 根据代码获取枚举
    * @param code
    * @return
    */
   public static CCRBankTelMsg getEnumByCode(String bankId) {
       for (CCRBankTelMsg obj : CCRBankTelMsg.values()) {
           if (obj.getBankId().equalsIgnoreCase(bankId)) {
               return obj;
           }
       }
       return null;
   }
}
