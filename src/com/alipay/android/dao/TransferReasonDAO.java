package com.alipay.android.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.eg.android.AlipayGphone.R;

public class TransferReasonDAO {
	
	/**
	 * 保存转账理由
	 */
	public void saveTransferReason(Context context,String transferReason) {
		List<String> reasonList = new ArrayList<String>();
		SharedPreferences preferences = context.getSharedPreferences("transferReason", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		
		String[] reasonArray = new String[]{context.getText(R.string.transfer) + "",
											context.getText(R.string.goodsPay) + "",
											context.getText(R.string.luanch) + "",
											context.getText(R.string.songForWeekDay) + ""};
		
		reasonList.addAll(Arrays.asList(reasonArray));
		String reasonStr = preferences.getString("reason", "");
		if ("".equals(reasonStr)) {
			editor.putString("reason", getReasonStr(reasonList));
			editor.commit();
		}else{
			reasonList.clear();
			String[] arrayInFile = reasonStr.split("\\$#\\$");
			for(int i = 0;i < arrayInFile.length;i++){
				reasonList.add(arrayInFile[i]);
			}
		}
		
		//符合条件才保存
		if (!reasonList.contains(transferReason) && reasonList.size() <= 5) {
			if(reasonList.size() == 5){//删除最后一条
				reasonList.set(0, transferReason);
			}else{
				reasonList.add(0, transferReason);
			}
			editor.putString("reason", getReasonStr(reasonList));
			editor.commit();
		} 
	}
	
	private String getReasonStr(List<String> reasonList){
		StringBuilder sb = new StringBuilder();
		for(String reasonStr : reasonList){
			sb.append(reasonStr + "$#$");
		}
		return sb.toString().substring(0 , sb.length() - 3);
	}
}
