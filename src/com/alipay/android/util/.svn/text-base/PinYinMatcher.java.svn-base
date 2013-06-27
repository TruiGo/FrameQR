package com.alipay.android.util;

import java.util.ArrayList;
import java.util.List;

public class PinYinMatcher {

	public List<String[]> getMatchedHanZi(String ziMu, String[] pinYinForHanzi) {
		List<String[]> matchedHanziList = new ArrayList<String[]>();
		int zimuLength = ziMu.length();
		String curWaitMatchHanzi;
		for (int index = 0; index < pinYinForHanzi.length; index++) {
			curWaitMatchHanzi = pinYinForHanzi[index];

			char[] inputChar = ziMu.toCharArray();
			char[] curHanziChar = curWaitMatchHanzi.toLowerCase().toCharArray();

			String curZimu = ziMu;

			for (int i = 0; i < curHanziChar.length && ziMu.length() > 0; i++) {
				if (curHanziChar[i] != inputChar[i])
					break;
				else
					ziMu = ziMu.substring(1);
			}

			if (ziMu.length() < curZimu.length()) {
				String[] matchedStr = new String[2];
				matchedStr[0] = String.valueOf(index);
				matchedStr[1] = curZimu.substring(0, curZimu.length() - ziMu.length());
				matchedHanziList.add(matchedStr);
			} else if (ziMu.length() != zimuLength)
				break;
		}

		if (ziMu.length() > 0) {
			matchedHanziList.clear();
		}
		return matchedHanziList;
	}
}
