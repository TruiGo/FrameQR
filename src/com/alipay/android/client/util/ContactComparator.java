package com.alipay.android.client.util;

import java.util.Comparator;

import com.alipay.android.client.lifePayment.ChineseToPy;
import com.alipay.android.ui.bean.ContactPerson;
import com.alipay.android.util.PinYinAndHanziUtils;

public class ContactComparator implements Comparator<ContactPerson> {
	public int compare(ContactPerson lhs, ContactPerson rhs) {
		String[] firstNameArray = PinYinAndHanziUtils.splitDisplayNameWithHanzi(lhs.displayName);
		String[] secondNameArray = PinYinAndHanziUtils.splitDisplayNameWithHanzi(rhs.displayName);

		return getFirstHanzi(firstNameArray).compareTo(getFirstHanzi(secondNameArray));
	}

	private String getFirstHanzi(String[] nameArray) {
		String nameFirst = "~";
		if (nameArray.length > 0) {
			nameFirst = nameArray[0].toLowerCase();
			
			if (PinYinAndHanziUtils.isHanzi(nameFirst)){
				nameFirst = ChineseToPy.getFullPy(nameFirst);
			}else if(PinYinAndHanziUtils.isStartWithAlphabet(nameFirst)){
				nameFirst = nameFirst.substring(0,1);
			}else{
				nameFirst = "~";
			}
		}
		return nameFirst;
	}
}