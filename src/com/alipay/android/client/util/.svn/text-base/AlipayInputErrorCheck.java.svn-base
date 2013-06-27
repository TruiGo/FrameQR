package com.alipay.android.client.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

import com.eg.android.AlipayGphone.R;

public class AlipayInputErrorCheck
{

	public static final int NO_ERROR = -1;
	public static final int ERROR_INVALID_FORMAT = -2;
	public static final int ERROR_OUTOF_RANGE = -3;
	public static final int ERROR_NULL_INPUT = -4;
	public static final int ERROR_OUT_LIMIT = -5;
	public static final int ERROR_TOO_LITTLE = -6;

	public static int CheckUserID(String accountString)
	{

		String str_accountString = accountString.trim();
		
		//取圆括号中间的内容
		int startIndex = str_accountString.indexOf("(");
		if (-1 != startIndex) {
			int endIndex = str_accountString.indexOf(")");
			if (-1 != endIndex) {
				str_accountString = str_accountString.substring(startIndex + 1, endIndex);
			}
		}

		if ((str_accountString.length() <= 0))
			return ERROR_NULL_INPUT;

		if (str_accountString.contains("@")) // email possible
		{
			if (isEmail(str_accountString) == false)
				return ERROR_INVALID_FORMAT;

		}
		else
		// phone number possible or invalid string
		{
			if (isMobileNO(str_accountString) == false)
				return ERROR_INVALID_FORMAT;
		}

		return NO_ERROR;
	}

	public static int checkLoginPassword(String passwordString)
	{
		if (passwordString.length() == 0)
			return ERROR_NULL_INPUT;
//		if (isPassword(passwordString) == false)
//			return ERROR_INVALID_FORMAT;

		return NO_ERROR;
	}

	public static int checkMoneyOut(String moneyString)
	{
		if (moneyString.length() <= 0)
			return ERROR_INVALID_FORMAT;

		return NO_ERROR;
	}

	public static int checkPayingPassword(String payingPasswordString)
	{
		if (payingPasswordString.length() == 0)
			return ERROR_NULL_INPUT;

//		if (isPassword(payingPasswordString) == false)
//			return ERROR_INVALID_FORMAT;

		return NO_ERROR;
	}

	/*
	 * 新的密码规则，登录密码由6-20个英文字母、数字或符号组成
	 */
	public static int checkPayingPasswordSet(String payingPasswordString)
	{
		if (payingPasswordString.length() == 0)
			return ERROR_NULL_INPUT;
		
//		if (isNumeric(payingPasswordString) == true)
//			return ERROR_INVALID_FORMAT;
//		
//		if (isPassword(payingPasswordString) == false)
//			return ERROR_INVALID_FORMAT;
//		
//		String s = "abcdefghijklmnopqrstuvwxyz";
//		if (s.indexOf(payingPasswordString.toLowerCase()) != -1)
//			return ERROR_INVALID_FORMAT;
		
		return NO_ERROR;
	}

	public static int checkPhoneNumber(String phoneNoString)
	{
		if (phoneNoString.length() == 0)
			return ERROR_NULL_INPUT;

		if (isMobileNO(phoneNoString) == false)
			return ERROR_INVALID_FORMAT;

		return NO_ERROR;
	}

	public static int CheckCheckcode(String strCheckcode)
	{
		if (strCheckcode.length() == 0)
			return ERROR_NULL_INPUT;
		if (strCheckcode.length() != 4)
			return ERROR_OUTOF_RANGE;

		if (isVerifyCode(strCheckcode) == false)
			return ERROR_INVALID_FORMAT;

		return NO_ERROR;
	}
	
	public static int checkDigital(String checkCode,int maxLength){
		if (checkCode.length() == 0)
			return ERROR_NULL_INPUT;
		if (checkCode.length() != maxLength)
			return ERROR_OUTOF_RANGE;
		if (isVerifyDigital(checkCode) == false)
			return ERROR_INVALID_FORMAT;
		return NO_ERROR;
	}
	
	private static boolean isVerifyDigital(String checkCode) {
		Pattern pattern = Pattern.compile("^[0-9]*$");
		Matcher matcher = pattern.matcher(checkCode);
		if (matcher.matches())
		{
			return true;
		}
		return false;
	}

	public static int CheckBindingcode(String strCheckcode)
	{
		if (strCheckcode.length() == 0)
			return ERROR_NULL_INPUT;
		if (strCheckcode.length() != 6)
			return ERROR_OUTOF_RANGE;

		if (isVerifyBindingCode(strCheckcode) == false)
			return ERROR_INVALID_FORMAT;

		return NO_ERROR;
	}

	public static int CheckUsername(String strUsername)
	{
		String str_Username = strUsername.trim();

		if (str_Username.length() == 0)
			return ERROR_NULL_INPUT;
		if (str_Username.length() == 1)
			return ERROR_OUTOF_RANGE;

		if (isRealName(str_Username) == false)
			return ERROR_INVALID_FORMAT;

		return NO_ERROR;
	}

	public static int CheckMoney(String strMoney)
	{
		String str_Money = strMoney.trim();

		if (str_Money.length() == 0)
			return ERROR_NULL_INPUT;

		if (isMoney(str_Money) == false)
			return ERROR_INVALID_FORMAT;

		return NO_ERROR;
	}

	public static int CheckDraftNO(String strNO)
	{
		String str_No = strNO.trim();

		if (str_No.length() == 0)
			return ERROR_NULL_INPUT;

		if (isDraftNO(str_No) == false)
			return ERROR_INVALID_FORMAT;

		return NO_ERROR;
	}

	public static int CheckDraftPassword(String strPassword)
	{

		if (strPassword.length() == 0)
			return ERROR_NULL_INPUT;

		if (isDraftPassword(strPassword) == false)
			return ERROR_INVALID_FORMAT;

		return NO_ERROR;
	}

	public static int CheckRealName(String strName)
	{
		String str_Name = strName.trim();

		if (str_Name.length() == 0)
			return ERROR_NULL_INPUT;

		if ((str_Name.length() < 2) || (str_Name.length() > 32))
			return ERROR_OUTOF_RANGE;

		if (isRealName(str_Name) == false)
			return ERROR_INVALID_FORMAT;

		return NO_ERROR;
	}
	/**
	 * 信用卡持卡人姓名规则检�
	 * 只能包含汉字、大写字母�?、空�
	 * 长度�?-20位间
	 * @param strName
	 * @return
	 */
	public static int CheckCCRAccountName(String strName)
	{
		String str_Name = strName.trim();

		if (str_Name.length() == 0)
			return ERROR_NULL_INPUT;

		if ((str_Name.length() < 2) || (str_Name.length() > 32))
			return ERROR_OUTOF_RANGE;
		
		boolean isRealName = false;
		Pattern pattern = Pattern.compile("^[\\u4E00-\\u9FA5.\\sA-Z]{2,32}$");
		Matcher matcher = pattern.matcher(str_Name);
		if (matcher.matches())
		{
			isRealName =  true;
		}
		if (isRealName == false)
			return ERROR_INVALID_FORMAT;

		return NO_ERROR;
	}

	public static int CheckIdentityCard(String strNO)
	{
		String str_No = strNO.trim();

		if (str_No.length() == 0)
			return ERROR_NULL_INPUT;

		if (isIdentityCard(str_No) == false)
			return ERROR_INVALID_FORMAT;

		return NO_ERROR;
	}

	public static int CheckBankNO(String strNO)
	{
		String str_No = strNO.trim();

		if (str_No.length() == 0)
			return ERROR_NULL_INPUT;

		if (isBankNO(str_No) == false)
			return ERROR_INVALID_FORMAT;

		return NO_ERROR;
	}

	public static int CheckCardNO(String strNO)
	{
		String str_No = strNO.trim();

		if (str_No.length() == 0)
			return ERROR_NULL_INPUT;

		if (isCardNO(str_No) == false)
			return ERROR_INVALID_FORMAT;

		return NO_ERROR;
	}
	public static int CheckCreditCardNO(String strNO)
	{
		String str_No = strNO.trim();

		if (str_No.length() == 0)
			return ERROR_NULL_INPUT;

		if (isCreditCardNO(str_No) == false)
			return ERROR_INVALID_FORMAT;

		return NO_ERROR;
	}

	public String getErrorMessage(int errorType)
	{
		if (errorType == NO_ERROR)
		{
			return "";
		}

		final String tErrorMsg = "";

		return tErrorMsg;
	}
	public static int CheckNumber(String str)
	{
		String str_No = str.trim();

		if (str_No.length() == 0)
			return ERROR_NULL_INPUT;

		if (isNumeric(str_No) == false)
			return ERROR_INVALID_FORMAT;

		return NO_ERROR;
	}
	private static boolean isNumeric(String str)
	{
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches())
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	// �����ַ��Ƿ�Ϊ�Ϸ������ַ��ʽ���Ϸ�����?���Ƿ�����0��
	private static boolean isEmail(String value)
	{
		// ��ȷ�������ַ���£�?
		// youmeng@alipay.com
		// ^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$
		// ����ĸ��ϸ񣬵��ͻ��˷ſ�����?
		// ^[a-zA-Z0-9]+(([\\.]?[0-9a-zA-Z]+)|([_]*[0-9a-zA-Z]+))*@([0-9a-zA-Z]+[\\-]?[0-9a-zA-Z]+[\\.])+[a-zA-Z]{2,4}$

		// Pattern pattern =
		// Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
		Pattern pattern = Pattern
				.compile("^[\\w\\.-]+(\\.[\\w\\.-]+)*@[\\w-]+(\\.[\\w-]+)+$");
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches())
		{
			return true;
		}
		return false;
	}

	// ���������ַ��Ƿ�Ϊ�ֻ���롣�й��ֻ������11λ������ɣ���?3* ��18* �� 15*
	// ��ʼ�����Ϸ�������1�����򷵻�0��
	private static boolean isMobileNO(String value)
	{
		Pattern pattern = Pattern.compile("1\\d{10}");
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches())
		{
			return true;
		}
		return false;
	}

	// ��֤�Ƿ�Ϊ���֤���룬�й����֤������15����18λ������ɡ�?
	private static boolean isIdentityCard(String value)
	{
		Pattern pattern = Pattern.compile("\\d{15}$|\\d{17}[0-9xX]{1}");
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches())
		{
			return true;
		}
		return false;
	}

	// �����飺�����ַ��ʽ��λ��֧��ȫ���ַ����?-16λ��
	// ������������֡���ĸ��?2�����ַ�`~!@#$%^&*()-_+={[}]|\:;"'<,>.?/�����ɡ�
//	private static boolean isPassword(String value)
//	{
//		Pattern pattern = Pattern
//				.compile("^[0-9a-zA-Z`~!@#%&:;\"'_=<,>\\.\\$\\?\\^\\/\\*\\(\\)\\-\\+\\{\\}\\[\\]\\|\\\\]{6,20}$");
//		Matcher matcher = pattern.matcher(value);
//		if (matcher.matches())
//		{
//			return true;
//		}
//		return false;
//	}
//
//	private static boolean isPassword(String value, int minLen, int maxLen)
//	{
//		String patternStr = "^[0-9a-zA-Z`~!@#%&:;\"'_=<,>\\.\\$\\?\\^\\/\\*\\(\\)\\-\\+\\{\\}\\[\\]\\|\\\\]{";
//
//		patternStr = patternStr + Integer.toString(minLen) + ","
//				+ Integer.toString(maxLen) + "}$";
//
//		Pattern pattern = Pattern.compile(patternStr);
//		Matcher matcher = pattern.matcher(value);
//		if (matcher.matches())
//		{
//			return true;
//		}
//		return false;
//	}

	// ���������ַ��Ƿ�Ϊ�Ϸ��Ľ�ֻ��������С�����ܶ�����λ��?
	private static boolean isMoney(String value)
	{
		Pattern pattern = Pattern
				.compile("^[1-9]\\d*(([\\.]?[0-9]{1,2})?)$|^[0][\\.][1-9]$|^[0][\\.]([0-9][1-9])$");
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches())
		{
			return true;
		}
		return false;
	}

	// ��Ʊ�����飺ֻ����14λ����18λ�Ĵ������ַ���֧��ȫ���ַ�
	private static boolean isDraftNO(String value)
	{
		Pattern pattern = Pattern.compile("\\d{14}|\\d{18}");
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches())
		{
			return true;
		}
		return false;
	}

	// ��Ʊ�����飺ֻ����6λ�������ַ���֧��ȫ���ַ�
	private static boolean isDraftPassword(String value)
	{
		Pattern pattern = Pattern.compile("\\d{6}");
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches())
		{
			return true;
		}
		return false;
	}

	// ���п��ż��?6��19λ����
	private static boolean isBankNO(String value)
	{
		Pattern pattern = Pattern.compile("\\d{16,21}");
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches())
		{
			return true;
		}
		return false;
	}
	private static boolean isCardNO(String value)
	{
		Pattern pattern = Pattern.compile("\\d{15,21}");
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches())
		{
			return true;
		}
		return false;
	}
	private static boolean isCreditCardNO(String value)
	{
		Pattern pattern = Pattern.compile("\\d{15,21}");
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches())
		{
			int cardBin = CardBinStringConvert(value);
			if(cardBin%10==0){
				return true;
			}
		}
		return false;
	}
	private static int CardBinStringConvert(String str){
		String cardRevers = new StringBuilder(str).reverse().toString();
		char[] cardChar = cardRevers.toCharArray();
		int[] cardBinNumbers = new int[cardChar.length];
		int cardBinluhnValue = 0;
		int index = 0;
		for(int cardBinNumber : cardBinNumbers){
			cardBinNumber = Utilz.parseInt(String.valueOf(cardChar[index]));
			int luhnValue;
			if(index%2>0){
				luhnValue = cardBinNumber*2;
				if(luhnValue>9){
					luhnValue = luhnValue - 9;
				}
			}else{
				luhnValue = cardBinNumber;
			}
			cardBinluhnValue = cardBinluhnValue +luhnValue;
			index++;
		}
		return cardBinluhnValue;
	}
	
	// ??????
	// ��ʵ�����飺�����Ǻ��ֻ��дӢ����ĸ��֧��ȫ���ַ�λ��?
	private static boolean isRealName(String value)
	{
		Pattern pattern = Pattern.compile("^[\\u4E00-\\u9FA5A-Z]{2,32}$");
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches())
		{
			return true;
		}
		return false;
	}

	// ��֤���飺���������ֻ���ĸ����֧��ȫ���ַ���Ϊ4λ
	private static boolean isVerifyCode(String value)
	{
		Pattern pattern = Pattern.compile("[0-9a-zA-Z]{4}");
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches())
		{
			return true;
		}
		return false;
	}
	
	private static boolean isVerifyBindingCode(String value)
	{
		Pattern pattern = Pattern.compile("[0-9a-zA-Z]{6}");
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches())
		{
			return true;
		}
		return false;
	}

	// 神州行充值卡号检查：17位数�?
	private static boolean isEasyOwnCardNO(String value)
	{
		Pattern pattern = Pattern.compile("\\d{14,21}");
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches())
		{
			return true;
		}

		return false;
	}

	// 神州行充值卡密码检查：18位数�?
	private static boolean isEasyOwnCardPassword(String value)
	{
		Pattern pattern = Pattern.compile("\\d{14,21}");
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches())
		{
			return true;
		}

		return false;
	}
	
	// 合法url检�?
	public static boolean isUrl(String value)
	{
		Pattern pattern = Pattern.compile("^((https|http)?://){1}"
				+ "(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?"	//ftp的user@ 
				+ "(([0-9]{1,3}\\.){3}[0-9]{1,3}" 	// IP形式的URL- 199.194.52.184
				+ "|" 	// 允许IP和DOMAIN（域名）
				+ "([0-9a-z_!~*'()-]+\\.)*"		// 域名- www.  
				+ "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\."	// 二级域名  
				+ "[a-z]{2,6})" 	// first level domain- .com or .museum  
				+ "(:[0-9]{1,4})?" 	// 端口- :80  
				+ "((/?)|"		// a slash isn't required if there is no file name  
				+ "(/[0-9A-Za-z_!~*'().;?:@&=+$,%#-]+)+/?)$");
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches())
		{
			return true;
		}

		return false;
	}
	
	public static CheckState checkAmount(Context context,String amount,String head){
		CheckState cs = new CheckState(context);
		amount = amount.trim();
		if (amount.length() == 0){
			cs.bRet = false;
			cs.strErr = head+context.getString(R.string.PayAmountEmpty);
			return cs;
		}else if (isMoney(amount) == false){
			cs.bRet = false;
			cs.strErr = head+context.getString(R.string.AmountFormatError);
			return cs;
		}else{
			cs.bRet = true;
		}
		return cs;
	}
	
	public static CheckState checkPaymentPassword(Context context,String paymentPassword,String head){
		CheckState cs = new CheckState(context);
		paymentPassword = paymentPassword.trim();
		if (paymentPassword.length() == 0){
			cs.bRet = false;
			cs.strErr = head+context.getString(R.string.PayPasswordEmpty);
			return cs;
		}else{
			cs.bRet = true;
		}
		return cs;
	}
	
	public static CheckState checkDigital(Context context,String checkCode,int maxLength){
		CheckState cs = new CheckState(context);
		checkCode = checkCode.trim();
		if (checkCode.length() == 0){
			cs.bRet = false;
			cs.strErr = context.getString(R.string.WarningCheckCodeEmpty);
			return cs;
		}else if (checkCode.length() != maxLength){
			cs.bRet = false;
			cs.strErr = context.getString(R.string.WarningCheckCodeOutRange);
			return cs;
		}else if(isVerifyDigital(checkCode) == false){
			cs.bRet = false;
			cs.strErr = context.getString(R.string.WarningCheckCodeFormatError);
			return cs;
		}else{
			cs.bRet = true;
		}
		return cs;
	}
	
	
	
	public static CheckState CheckEasyOwnCardNO(Context context,
			String strCardNO)
	{
		CheckState cs = new CheckState(context);

		strCardNO = strCardNO.trim();
		if (strCardNO.length() <= 0)
		{
			cs.strErr = context.getString(R.string.StrCardNoIsEmpty);
		}
		else
		{
			if (isEasyOwnCardNO(strCardNO))
			{
				cs.bRet = true;
			}
			else
			{
				cs.strErr = context.getString(R.string.StrCardNoError);
			}
		}

		return cs;
	}

	public static CheckState CheckEasyOwnCardPassword(Context context,
			String strCardPassword)
	{
		CheckState cs = new CheckState(context);

		strCardPassword = strCardPassword.trim();
		if (strCardPassword.length() <= 0)
		{
			cs.strErr = context.getString(R.string.StrCardPassIsEmpty);
		}
		else
		{
			if (isEasyOwnCardPassword(strCardPassword))
			{
				cs.bRet = true;
			}
			else
			{
				cs.strErr = context.getString(R.string.StrCardPassError);
			}
		}

		return cs;
	}

	public static CheckState CheckChargeAmount(Context context,
			String strChargeAmount)
	{
		CheckState cs = new CheckState(context);

		strChargeAmount = strChargeAmount.trim();
		if (strChargeAmount.length() <= 0)
		{
			cs.strErr = context.getString(R.string.AmountEmpty);
		}
		else
		{
			if (isMoney(strChargeAmount))
			{
				cs.bRet = true;
			}
			else
			{
				cs.strErr = context.getString(R.string.StrMoneyFormatError);
			}
		}

		return cs;
	}
}
