package com.alipay.android.ui.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.partner.PartnerInfo;
import com.alipay.android.core.ParamString;

/**
 * 银行卡列表详细信息
 * @author caidie.wang
 *
 */
/**
 * @author caidie.wang
 * 
 */
public class BankCardInfo {
	/**
	 * 银行ID 提现卡时为卡ID 信用卡时为银行mark
	 */
	private String bankId = null;
	/**
	 * 银行名称
	 */
	private String bankName = null;
	/**
	 * 银行标志
	 */
	private String bankMark = null;
	/**
	 * 银行尾号
	 */
	private String cardTailNumber = null;

	/**
	 * cardType卡类型为CC--贷记卡时，此值为信用卡索引卡号 cardType卡类型为DC--借记卡时，此值为实名卡号
	 */
	private String cardNo = null;

	/**
	 * 申请时间 目前只有卡通与快捷卡有申请时间 卡通为：卡通签约时间
	 */
	private String applyTime = null;
	/**
	 * 业务类型 对应服务端actiontype字段
	 */

	private int bizType;
	public static final int BIZ_DEFAULT = 0;
	public static final int BIZ_CREDITCARDFUND_NEW = 1; // 新信用卡还款
	public static final int BIZ_CREDITCARDFUND_OLD = 1 << 1; // 老信用卡还款
	public static final int BIZ_WIDTHDRAW_INSTANT = 1 << 2; // 实时提现
	public static final int BIZ_WIDTHDRAW_NORMAL = 1 << 3; // 普通提现
	public static final int BIZ_EXPRESS_DELETEKUAIJIE = 1 << 4; // 撤销签约
	public static final int BIZ_EXPRESS_MOBILE_EMPTY = 1 << 5; // 添加签约手机号码
	public static final int BIZ_EXPRESS_MOBILE_EDITABLE = 1 << 6; // 修改签约手机号码
	public static final int BIZ_EXPRESS_CARDLIMIT_ACCESSABLE = 1 << 7; // 查询限额
	public static final int BIZ_EXPRESS_DELETEKATONG = 1 << 8; // 撤销签约提示。用于卡通撤销签约
	public static final int BIZ_EXPRESS_DELETEFROMCCFUNDLIST = 1 << 9; // 从信用卡还款已存列表删除
	public static final int BIZ_EXPRESS_DELETETIXIAN = 1 << 10; // 从CIF提现设置卡列表删除
	public static final int BIZ_EXPRESS_SIGNABLE = 1 << 11; // 签约为快捷卡
	public static final int BIZ_EXPRESS_DETAILS_ACCESSABLE = 1 << 20; // 查看详情

	/**
	 * 银行卡类型 DC--借记卡，CC--贷记卡
	 */
	private String cardType;
	public static final String CREDIT = "CC";
	public static final String DEBIT = "DC";
	/**
	 * 信用卡自有属性 持卡人姓名
	 */
	private String userName = null;

	/**
	 * unknown：未知 cifWithDraw：CIF提现设置 signKatong：卡通签约 signExpress：快捷签约 ccr：信用卡还款
	 */
	private int mSourceChannel;
	public static final int CARDSOURCE_UNKNOW = 1;
	public static final int CARDSOURCE_WITHDRAW = 1 << 1;
	public static final int CARDSOURCE_KATONG = 1 << 2;
	public static final int CARDSOURCE_EXPRESS = 1 << 3;

	private static final String CARDSOURCE_UNKNOW_STRING = "unknown";
	private static final String CARDSOURCE_WITHDRAW_STRING = "cifWithDraw";
	private static final String CARDSOURCE_KATONG_STRING = "signKatong";
	private static final String CARDSOURCE_EXPRESS_STRING = "signExpress";
	/**
	 * 是否本人卡 true 本人卡 false 非本人卡
	 */
	private boolean isOwner;

	/**
	 * 解析返回的银行卡列表数据
	 * 
	 * @param rootController
	 * @param currentBankListItem
	 * @return
	 */
	public BankCardInfo processBankCardInfoJson(JSONObject currentBankListItem) {
		try {
			// 公共属性
			if (currentBankListItem.has(Constant.BANK_MARK)) {
				setBankMark(currentBankListItem.getString(Constant.BANK_MARK));
			}
			if (currentBankListItem.has(Constant.BANK_NAME)) {
				setBankName(currentBankListItem.getString(Constant.BANK_NAME));
			}
			if (currentBankListItem.has(Constant.APPLY_TIME)) {
				setApplyTime(currentBankListItem.getString(Constant.APPLY_TIME));
			}

			// 卡业务类型
			if (currentBankListItem.has(Constant.ACTIONTYPES)) {
				JSONArray actionTypes = currentBankListItem
						.getJSONArray(Constant.ACTIONTYPES);
				if (null != actionTypes) {
					int valueTemp = -1;
					for (int i = 0; i < actionTypes.length(); i++) {
						valueTemp = actionTypes.optInt(i, -1);
						if (-1 != valueTemp) {
							setBizType(translaterActionType(valueTemp));
						}
					}
				}
			}

			// 卡类型
			if (currentBankListItem.has(Constant.CARDTYPE)) {
				setCardType(currentBankListItem.optString(Constant.CARDTYPE));
			}
			// 信用卡自有部分
			if (currentBankListItem.has(Constant.HOLDER_NAME)) {
				setUserName(currentBankListItem.getString(Constant.HOLDER_NAME));
			}

			// 银行卡来源类型
			if (currentBankListItem.has(Constant.SOURCECHANNELS)) {
				Object object = currentBankListItem
						.opt(Constant.SOURCECHANNELS);
				if (object instanceof JSONArray) {
					JSONArray sourceChannelArray = (JSONArray) object;
					if (null != sourceChannelArray) {
						for (int i = 0; i < sourceChannelArray.length(); i++) {
							setSourceChannel(translaterSourceChannel(sourceChannelArray
									.optString(i)));
						}
					}
				}
			}

			// 绑定手机号码
			if (currentBankListItem.has(Constant.BINDINGMOBILE)) {
				setBindMobile(currentBankListItem
						.optString(Constant.BINDINGMOBILE));
			}

			// 卡ID
			if (currentBankListItem.has(Constant.CARDID)) {
				setCardId(currentBankListItem.optString(Constant.CARDID));
			}

			// 借记卡自有部分
			if (currentBankListItem.has(Constant.BANK_ID)) {
				setBankId(currentBankListItem.getString(Constant.BANK_ID));
			}
			if (currentBankListItem.has(Constant.CARD_NUMBER)) {
				setCardTailNumber(currentBankListItem
						.getString(Constant.CARD_NUMBER));
			}

			if (currentBankListItem.has(Constant.CARDNO)) {
				setCardNo(currentBankListItem.getString(Constant.CARDNO));
			}

			if (currentBankListItem.has(Constant.IS_OWNER)) {
				isOwner = Boolean.parseBoolean(currentBankListItem
						.getString(Constant.IS_OWNER));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return this;
	}

	/*
	 * 101：新信用卡还款 102：老信用卡还款 201：实时提现 202：普通提现 301 ：撤销签约 302 ：添加签约手机号码 303
	 * ：修改签约手机号码 304 ：查询限额 305 ：撤销签约提示。用于卡通撤销签约 306 ：从信用卡还款已存列表删除 307
	 * ：从CIF提现设置卡列表删除 308 ：签约为快捷卡 401 ：查看详情
	 */
	private int translaterActionType(int originActionType) {
		switch (originActionType) {
		case 101:
			return BIZ_CREDITCARDFUND_NEW;
		case 102:
			return BIZ_CREDITCARDFUND_OLD;
		case 201:
			return BIZ_WIDTHDRAW_INSTANT;
		case 202:
			return BIZ_WIDTHDRAW_NORMAL;
		case 301:
			return BIZ_EXPRESS_DELETEKUAIJIE;
		case 302:
			return BIZ_EXPRESS_MOBILE_EMPTY;
		case 303:
			return BIZ_EXPRESS_MOBILE_EDITABLE;
		case 304:
			return BIZ_EXPRESS_CARDLIMIT_ACCESSABLE;
		case 305:
			return BIZ_EXPRESS_DELETEKATONG;
		case 306:
			return BIZ_EXPRESS_DELETEFROMCCFUNDLIST;
		case 307:
			return BIZ_EXPRESS_DELETETIXIAN;
		case 308:
			return BIZ_EXPRESS_SIGNABLE;
		case 401:
			return BIZ_EXPRESS_DETAILS_ACCESSABLE;
		default:
			return -1;
		}
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public int getBizType() {
		return bizType;
	}

	public void setBizType(int bizType) {
		this.bizType |= bizType;
	}

	public String getBankMark() {
		return bankMark;
	}

	public void setBankMark(String bankMark) {
		this.bankMark = bankMark;
	}

	public String getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String type) {
		this.cardType = type;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String holderName) {
		this.userName = holderName;
	}

	public String getCardTailNumber() {
		return cardTailNumber;
	}

	public void setCardTailNumber(String cardNumber) {
		this.cardTailNumber = cardNumber;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public int getSourceChannel() {
		return mSourceChannel;
	}

	public void setSourceChannel(int sourceChannel) {
		this.mSourceChannel |= sourceChannel;
	}

	private int translaterSourceChannel(String sourceChannel) {
		if (CARDSOURCE_UNKNOW_STRING.equals(sourceChannel)) {
			return CARDSOURCE_UNKNOW;
		} else if (CARDSOURCE_WITHDRAW_STRING.equals(sourceChannel)) {
			return CARDSOURCE_WITHDRAW;
		} else if (CARDSOURCE_KATONG_STRING.equals(sourceChannel)) {
			return CARDSOURCE_KATONG;
		} else if (CARDSOURCE_EXPRESS_STRING.equals(sourceChannel)) {
			return CARDSOURCE_EXPRESS;
		}

		return CARDSOURCE_UNKNOW;
	}

	private String mBindMobile;

	public String getBindMobile() {
		return mBindMobile;
	}

	public void setBindMobile(String bingMobile) {
		this.mBindMobile = bingMobile;
	}

	private String mCardId;

	public String getCardId() {
		return mCardId;
	}

	public void setCardId(String cardId) {
		this.mCardId = cardId;
	}

	public boolean isOwner() {
		return isOwner;
	}

	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}

	/**
	 * 序列化BankCardInfo 属性之间使用&分割
	 * 
	 * @return
	 */
	public String serialization() {

		StringBuffer stringBuffer = new StringBuffer();
		if (getBankId() != null) {
			stringBuffer.append(Constant.BANK_ID + "=" + getBankId() + "&");
		}

		if (getBankName() != null) {
			stringBuffer.append(Constant.BANK_NAME + "=" + getBankName() + "&");
		}

		if (getBankMark() != null) {
			stringBuffer.append(Constant.BANK_MARK + "=" + getBankMark() + "&");
		}

		if (getCardTailNumber() != null) {
			stringBuffer.append(Constant.CARD_TAIL_NUMBER + "="
					+ getCardTailNumber() + "&");
		}

		if (getUserName() != null) {
			stringBuffer.append(Constant.USER_NAME + "=" + getUserName() + "&");
		}

		if (getCardNo() != null) {
			stringBuffer.append(Constant.CARDNO + "=" + getCardNo() + "&");
		}

		stringBuffer.append(Constant.BIZ_TYPE + "=" + getBizType());

		return stringBuffer.toString();
	}

	public BankCardInfo deserialization(String serialization) {
		ParamString paramString = new ParamString(serialization);
		if (paramString.getValue(Constant.BANK_ID) != null) {
			setBankId(paramString.getValue(Constant.BANK_ID));
		}
		
		if (paramString.getValue(Constant.BANK_NAME) != null) {
			setBankName(paramString.getValue(Constant.BANK_NAME));
		}
		
		if (paramString.getValue(Constant.BANK_MARK) != null) {
			setBankMark(paramString.getValue(Constant.BANK_MARK));
		}
		
		if (paramString.getValue(Constant.CARD_TAIL_NUMBER) != null) {
			setCardTailNumber(paramString.getValue(Constant.CARD_TAIL_NUMBER));
		}
		
		if (paramString.getValue(Constant.USER_NAME) != null) {
			setUserName(paramString.getValue(Constant.USER_NAME));
		}
		
		if (paramString.getValue(Constant.CARDNO) != null) {
			setCardNo(paramString.getValue(Constant.CARDNO));
		}
		
		if (paramString.getValue(Constant.BIZ_TYPE) != null) {
			setBizType(Integer
					.parseInt(paramString.getValue(Constant.BIZ_TYPE)));
		}
		return this;
	}

}
