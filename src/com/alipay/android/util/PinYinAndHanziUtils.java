package com.alipay.android.util;

import java.util.ArrayList;
import java.util.List;

import com.alipay.android.client.lifePayment.ChineseToPy;

public class PinYinAndHanziUtils {
	
	public static List<String[]> convertChinese2Pinyin(String name){
		if(name == null || "".equals(name.trim())){
			return null;
		}
		
		String[] splitedDisplayName = splitDisplayNameWithHanzi(name);
		String[] displayNamePinyin = new String[splitedDisplayName.length];
		List<String[]> pinyinResult = new ArrayList<String[]>();
		
		for(int i = 0;i < splitedDisplayName.length;i++){
			if(isHanzi(splitedDisplayName[i])){
				displayNamePinyin[i] = ChineseToPy.getFullPy(splitedDisplayName[i]).toLowerCase();
			}else{
				displayNamePinyin[i] = splitedDisplayName[i];
			}
		}
		pinyinResult.add(displayNamePinyin);
		
		if(splitedDisplayName.length > 0 && hasHanzi(name)){
			pinyinResult.add(splitedDisplayName);
		}
		return pinyinResult;
	}
	
	
	public static List<String[]> processResult(String[][] pinyinResult) {
		List<String[]> result = new ArrayList<String[]>();
		String[] allCombination = doExchange(pinyinResult)[0];
		
		for(String pinyinContent : allCombination){
			String[] pinyinForHanzi = pinyinContent.split("##");
			result.add(pinyinForHanzi);
		}
		return result;
	}

	private static String[][] doExchange(String[][] pinyinForHanzi) {
		int len = pinyinForHanzi.length;
		if (len >= 2) {
			int len1 = pinyinForHanzi[0].length;
			int len2 = pinyinForHanzi[1].length;
			int newlen = len1 * len2;
			String[] temp = new String[newlen];
			
			int Index = 0;
			for (int i = 0; i < len1; i++) {
				for (int j = 0; j < len2; j++) {
					temp[Index++] =  pinyinForHanzi[0][i] + "##" + pinyinForHanzi[1][j];
				}
			}
			
			String[][] newArray = new String[len - 1][];
			for (int i = 2; i < len; i++) {
				newArray[i - 1] = pinyinForHanzi[i];
			}
			newArray[0] = temp;
			return doExchange(newArray);
		} else {
			return pinyinForHanzi;
		}
	}
	
	private static boolean hasHanzi(String name){
		return name.matches(".*[\\u4e00-\\u9fa5]+.*");
	}
	
	public static boolean isHanzi(String name){
		return name.matches("[\\u4e00-\\u9fa5]");
	}
	
	public static boolean isAlphabet(String name){
		return name.matches("[a-zA-Z]+");
	}
	
	public static boolean isHanziAndAlphabet(String name){
		return name.matches("[\\u4e00-\\u9fa5[a-zA-Z ]]+");
	}
	
	public static String[] splitDisplayNameWithHanzi(String name){
		name = name.replaceAll("([\\u4e00-\\u9fa5 ])", "###$1###");
		String[] splitedResult = name.split("#{3}");
		List<String> splitedList = new ArrayList<String>(); 
		for(String curStr : splitedResult){
			if(!"".equals(curStr)){
				splitedList.add(curStr);
			}
		}
		return splitedList.toArray(new String[splitedList.size()]);
	}
	
	public static boolean isContainHanzi(String name) {
		return name.matches(".*[\\u4e00-\\u9fa5]+.*");
	}


	public static boolean isStartWithAlphabet(String firstHanzi) {
		return firstHanzi.matches("[a-zA-Z]+.*");
	}
}
