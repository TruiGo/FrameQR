/**
 * 
 */
package com.alipay.android.core.expapp.api.impl;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import com.alipay.android.core.expapp.AppUiElementType;
import com.alipay.android.core.expapp.ExpAppRuntime;
import com.alipay.android.util.BigDecimalHelper;
import com.alipay.android.util.JsonConvert;

/**
 * @author sanping.li
 *
 */
public class Operate {
    public static final String[] OPERATORS = { "@ADD", "@SUB", "@MUL", "@DIV",//加，减，乘，除
                                              "@JOIN",//字符串连接,
                                              "@UNION","@LEN","@VECTOR","@EACH",//集合联合,集合长度,向量运算,逐个运算
                                              "@COMB"//组合n!/((n-m)!m!)
    };
    public static final int AAD = 0;
    public static final int SUB = AAD + 1;
    public static final int MUL = SUB + 1;
    public static final int DIV = MUL + 1;
    public static final int JOIN = DIV + 1;
    public static final int UNION = JOIN + 1;
    public static final int LENGTH = UNION + 1;
    public static final int VECTOR = LENGTH + 1;
    public static final int EACH = VECTOR + 1;
    public static final int COMB = EACH + 1;

    public Object compute(ExpAppRuntime engine, String operator, ArrayList<Object> params) throws JSONException {
        Object ret = null;
        int index = -1;
        for (int i = 0; i < OPERATORS.length; ++i) {
            if (OPERATORS[i].equals(operator)) {
                index = i;
                break;
            }
        }
        switch (index) {
            case AAD://+
                if (params.size() < 1)
                    break;
                String value = "0";
                for(Object o:params){
                    value = BigDecimalHelper.add(value, o.toString());
                }
                ret = value;    
                break;
            case SUB://-
                if (params.size() < 2)
                    break;
                String vs = params.get(0).toString();
                for(int i =1;i<params.size();++i){
                    vs =BigDecimalHelper.sub(vs, params.get(i).toString());
                }
                ret = vs;    
                break;
            case MUL://*
                if (params.size() < 2)
                    break;
                String vm = "1";
                for(Object o:params){
                    vm = BigDecimalHelper.mul(vm, o.toString());
                }
                ret = vm;    
                break;
            case DIV:// / 
                if (params.size() < 2)
                    break;
                String vd = params.get(0).toString();
                for(int i =1;i<params.size();++i){
                    vd = BigDecimalHelper.div(vd, params.get(i).toString());
                }
                ret = vd;    
                break;
            case JOIN://..
                if (params.size() < 2)
                    break;
                StringBuffer sb = new StringBuffer();
                for(int i =0;i<params.size();++i){
                    String str = params.get(i).toString();
                    if(str.startsWith(AppUiElementType.Reference))
                        str = engine.getValue(str.replace(AppUiElementType.Reference, ""));
                    sb.append(str);
                }
                ret = sb.toString();    
                break;
            case UNION://集合联合
                if (params.size() < 2)
                    break;
                String adapter = params.get(0).toString();
                if(adapter.length()>0){
                    if(adapter.startsWith(AppUiElementType.Reference))
                        adapter = engine.getValue(adapter.replace(
                            AppUiElementType.Reference, ""));
                        JSONArray jsonArray = new JSONArray(adapter);
                        ArrayList<Object> dList = JsonConvert.Json2Array(jsonArray);//适配
                        ArrayList<HashMap<String, Object>> tmp = new ArrayList<HashMap<String,Object>>();
                        HashMap<String, Object> item = null;
                        for(int i =0;i<dList.size();++i){
                            Object object = params.get(i+1);
                            if(object instanceof String){
                                String str = (String) object;
                                if(str.startsWith(AppUiElementType.Reference)){
                                    str = engine.getValue(str.replace(AppUiElementType.Reference, ""));
                                    JSONArray jsonData = new JSONArray(str);
                                    object = JsonConvert.Json2Array(jsonData);
                                }
                            }
                            ArrayList<?> list = (ArrayList<?>)object ;
                            for(int j =0;j<list.size();++j){
                                if(tmp.size()<=j){
                                    item = new HashMap<String, Object>();
                                    tmp.add(item);
                                }else
                                    item = tmp.get(j);
                                item.put(dList.get(i).toString(), list.get(j));
                            }
                        }
                        ret = tmp;  
                }else{
                    ArrayList<Object> arrayList = new ArrayList<Object>();
                    ArrayList<?> list = null;
                    for(int i =1;i<params.size();++i){
                        Object object = params.get(i);
                        if(object instanceof String){
                            String str = (String) object;
                            if(str.startsWith(AppUiElementType.Reference)){
                                str = engine.getValue(str.replace(AppUiElementType.Reference, ""));
                                JSONArray jsonData = new JSONArray(str);
                                object = JsonConvert.Json2Array(jsonData);
                            }
                        }
                        list = (ArrayList<?>) object;
                        arrayList.addAll(list);
                    }
                    ret = arrayList;  
                }  
                break;
            case LENGTH://集合长度
                if (params.size() < 1)
                    break;
                Object data = params.get(0);
                if(data instanceof String){
                    String str = (String) data;
                    if(str.startsWith(AppUiElementType.Reference)){
                        str = engine.getValue(str.replace(AppUiElementType.Reference, ""));
                        JSONArray jsonData = new JSONArray(str);
                        data = JsonConvert.Json2Array(jsonData);
                    }
                }
                ArrayList<?> arrayList = (ArrayList<?>) data;
                ret = arrayList.size();    
                break;
            case VECTOR://向量运算，一维的，每项长度一样或者为一数字
                if (params.size() < 2)
                    break;
                String op = params.get(0).toString();
                ArrayList<Object> operands = new ArrayList<Object>();
                Object object = null;
                int len = 0;
                for(int i =1;i<params.size();++i){
                    object =  params.get(i);
                    if(object instanceof String){
                        String str = (String) object;
                        if(str.startsWith(AppUiElementType.Reference)){
                            str = engine.getValue(str.replace(AppUiElementType.Reference, ""));
                            JSONArray jsonData = new JSONArray(str);
                            object = JsonConvert.Json2Array(jsonData);
                        }
                    }
                    if(object instanceof ArrayList<?>)
                        len = ((ArrayList<?>)object).size();
                    operands.add(object);
                }
                ArrayList<Object> val = new ArrayList<Object>();
                ArrayList<Object> param = null;
                for(int i =0;i<len;++i){
                    param = new ArrayList<Object>();
                    for(int j=0;j<operands.size();++j){
                        Object o = operands.get(j);
                        if(o instanceof ArrayList<?>){
                            param.add(((ArrayList<?>)o).get(i));
                        }else
                            param.add(o);
                    }
                    val.add(compute(engine,op, param));
                }
                ret = val;    
                break;
            case EACH:
                if (params.size() < 2)
                    break;
                String ope = (String) params.get(0);
                Object dat = params.get(1);
                if(dat instanceof String){
                    String str = (String) dat;
                    if(str.startsWith(AppUiElementType.Reference)){
                        str = engine.getValue(str.replace(AppUiElementType.Reference, ""));
                        JSONArray jsonData = new JSONArray(str);
                        dat = JsonConvert.Json2Array(jsonData);
                    }
                }
                @SuppressWarnings("unchecked")
                ArrayList<Object> arr = (ArrayList<Object>) dat;
                return compute(engine,ope, arr);
            case COMB://组合
                if (params.size() < 2)
                    break;
                long n = Long.parseLong(params.get(0).toString());
                long m = Long.parseLong(params.get(1).toString());
                int vv = (int) (factorial(n)/(factorial(n-m)*factorial(m)));
                ret = vv;    
                break;

            default:
                break;
        }
        return ret;
    }
    
    //阶乘
    private long factorial(long n){
        long ret = 0;
        if(n<=1)
            ret = 1;
        else
            ret =  (long)n*factorial(n-1);
        return ret;
    }
}


