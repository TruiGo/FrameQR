package com.alipay.android.appHall.component.incrementitem;

import java.util.ArrayList;
import java.util.HashMap;

import com.alipay.android.appHall.component.UIIncrementBox;
import com.alipay.android.client.RootActivity;

public class DataStructureFactory {
    public ArrayList<HashMap<Object, Object>> generateAllItemInputContent(int itemType, Object dataStructure, RootActivity context){
        ArrayList<HashMap<Object, Object>> obj = null;
        switch (itemType) {
            case UIIncrementBox.ITEMTYPE_GETMONEY:
                obj = generateGetMoneyDataStructure(dataStructure,context);
                break;
            case UIIncrementBox.ITEMTYPE_ANOTHER:
                obj = generateAnotherDataStructure(dataStructure);
                break;
            default:
                break;
        }
        return obj;
    }
    
    
    private ArrayList<HashMap<Object, Object>> generateGetMoneyDataStructure(Object dataStructure,RootActivity context){
        
        
        
        ArrayList<HashMap<Object, Object>> arrayList = new  ArrayList<HashMap<Object, Object>>();
        GetMoneyDataStructure tempDataStructure = (GetMoneyDataStructure)dataStructure;
        int count = tempDataStructure.getMoneypeopleCount;
        
        //之前的均分算法
        double tmoney = tempDataStructure.getMoneyTotalCount/(double)count;
        int imoney = (new Double(tmoney*100)).intValue();
        final double fmoney = imoney/100d;
        
        for (int i = 0; i < count; i++) {
            HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
            if (tempDataStructure.isContainSelf&&i==0) {
                //输入收款人自己的账户信息
                hashMap.put(UtilGetMoneyIncreamItem.ACCOUNT, context.getAccountName(true));
            }else{
                hashMap.put(UtilGetMoneyIncreamItem.ACCOUNT, "");
            }
            hashMap.put(UtilGetMoneyIncreamItem.MONEYCOUNT, fmoney);
            arrayList.add(hashMap);
        }
        return arrayList;
    }
    
    private ArrayList<HashMap<Object, Object>> generateAnotherDataStructure(Object dataStructure){
        return null;
    }
}
