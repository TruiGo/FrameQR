package com.alipay.android.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.alipay.android.dataprovider.ContactProvider;
import com.alipay.android.ui.bean.ContactPerson;
import com.alipay.android.util.PinYinAndHanziUtils;
import com.alipay.android.util.PinYinMatcher;

public class ContactPersonDAO {
	public static String OP_CONTACT_LOAD = ContactPersonDAO.class.getName();
	private PinYinMatcher pinYinMatcher = new PinYinMatcher();
	private ContactProvider contactProvider = ContactProvider.getInstance();
	private Cursor phoneCursor,contactCursor = null;
	private ContactPerson preContactPerson;
	
	public synchronized void loadAllContacts(Context context) {
		contactProvider.setLoading(true);
		List<ContactPerson> loadedContactList = new ArrayList<ContactPerson>();
		try {
			contactCursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[] { ContactsContract.Contacts._ID,
					ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER }, null, null,
					ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");

			while (contactCursor != null && contactCursor.moveToNext()) {
				if ((contactCursor.getInt(contactCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					String contactID = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));

					phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID, null, null);

					ContactPerson contactPerson = new ContactPerson();
					contactPerson.displayName = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					if(contactPerson.displayName == null){
						contactPerson.displayName = "";
					}

					List<String> phoneNumberList = new ArrayList<String>();
					while (phoneCursor != null && phoneCursor.moveToNext()) {
						String phoneNum = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[- ]", "");
						if (phoneNum.length() >= 11) {
							phoneNum = phoneNum.substring(phoneNum.length() - 11);
							if (phoneNum.matches("1\\d{10}")) {
								phoneNumberList.add(phoneNum);
							}
						}
					}
					closeCursor(phoneCursor);

					if (phoneNumberList.size() > 0) {
						contactPerson.phoneNumber = phoneNumberList;
						if(contactPerson.equals(preContactPerson))//去重
							continue;
						preContactPerson = contactPerson;
						loadedContactList.add(contactPerson);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(contactCursor);
		}
		
		contactProvider.setContactData(loadedContactList);
	}

	/**
	 * close Cursor
	 * @param cursor
	 */
	private void closeCursor(Cursor cursor) {
		if (cursor != null) {
			try {
				cursor.close();
				cursor = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据用户输入搜索联系人
	 * 
	 * @param inputedStr
	 *            用户输入
	 * @param contactList
	 *            需要搜索的数据源
	 * @return 符合条件的数据
	 */
	public List<ContactPerson> getMatchedContact(String inputedStr, List<ContactPerson> contactList) {
		if (contactList == null)
			return null;
		List<ContactPerson> contacterSearched = new ArrayList<ContactPerson>();
		if (!TextUtils.isDigitsOnly(inputedStr)) {
			List<String[]> pinYinName;
			List<String[]> pinyinResult;
			for (ContactPerson curContacter : contactList) {
				pinYinName = PinYinAndHanziUtils.convertChinese2Pinyin(curContacter.displayName);
				for (int i = 0; pinYinName != null && i < pinYinName.size(); i++) {
					String[] pinyinNameArray = pinYinName.get(i);
					pinyinResult = pinYinMatcher.getMatchedHanZi(inputedStr.toLowerCase(), pinyinNameArray);

					if (pinyinResult.size() > 0) {
						curContacter.matchedPinYin = pinyinResult;
						contacterSearched.add(curContacter);
						break;
					}
				}
			}
		} else {
			for (ContactPerson curContacter : contactList) {
				if (curContacter.phoneNumber.get(0).contains(inputedStr)) {
					curContacter.isNumberMatch = true;
					curContacter.matchedNum = inputedStr;
					contacterSearched.add(curContacter);
				}
			}
		}
		return contacterSearched;
	}

	/**
	 * reset 集合中的联系人对象的搜索字段
	 */
	public void resetContactPerson(List<ContactPerson> contactList) {
		for (ContactPerson curContacter : contactList) {
			curContacter.matchedPinYin = null;
			curContacter.matchedNum = null;
			curContacter.isNumberMatch = false;
		}
	}
}

