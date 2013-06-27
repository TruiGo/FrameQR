package com.alipay.android.appHall.component.incrementitem;

import java.util.HashMap;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.appHall.component.UIAccountBox;
import com.alipay.android.appHall.component.UIIncrementBox;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.core.expapp.Page;
import com.eg.android.AlipayGphone.R;

public class UtilGetMoneyIncreamItem extends AbstractIncreamUtil {

    public static final String ACCOUNT = "account";
    public static final String MONEYCOUNT = "moneyCount";

    private HashMap<String, MSubItem> mSubItem = new HashMap<String, MSubItem>();
    private boolean isContainSelf;
    private UIIncrementBox uiIncreamBox;
    private LinearLayout fakeRootLinearLayout;
    private TextView resultText;

    private double mTotalMoney;
    private boolean mCanDelete;
    private boolean mCanAdd;

    public UtilGetMoneyIncreamItem(Page page, UIIncrementBox uiIncreamBox,
                                   LinearLayout fakeRootLinearLayout,
                                   GetMoneyDataStructure dataStructure) {
        mPage = page;
        this.uiIncreamBox = uiIncreamBox;
        this.fakeRootLinearLayout = fakeRootLinearLayout;
        cacheLayoutHashMap = new HashMap<String, RelativeLayout>();
        //        mTotalMoney = dataStructure.getMoneyTotalCount;

        isContainSelf = ((GetMoneyDataStructure) dataStructure).isContainSelf;
        DataStructureFactory factory = new DataStructureFactory();
        inputContentArrayList = factory.generateAllItemInputContent(
            UIIncrementBox.ITEMTYPE_GETMONEY, dataStructure, (RootActivity) mPage.getEngine()
                .getContext());
        
        mCanDelete = true;
        mCanAdd = true;
    }

    public void loadIncreamItemsData() {
        LayoutInflater inflater = (LayoutInflater) mPage.getRawContext().getSystemService(
            Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.incream_items_layout, fakeRootLinearLayout);
        itemsLayout = (LinearLayout) uiIncreamBox.findViewById(R.id.ItemsLayout);
        itemsLayout.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            
            @Override
            public void onChildViewRemoved(View parent, View child) {
                mCanDelete = true;
            }
            
            @Override
            public void onChildViewAdded(View parent, View child) {
                mCanAdd = true;
            }
        });

        for (int i = 0; i < inputContentArrayList.size(); i++) {
            HashMap<Object, Object> hashMap = inputContentArrayList.get(i);
            String account = (String) hashMap.get(UtilGetMoneyIncreamItem.ACCOUNT);
            double moneyCount = ((Double) hashMap.get(UtilGetMoneyIncreamItem.MONEYCOUNT))
                .doubleValue();
            mTotalMoney += moneyCount;
            addIncreamItem(account, moneyCount, true);
        }
    }

    public void loadIncreamControlData() {
        ((LayoutInflater) (mPage.getRawContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)))
            .inflate(R.layout.incream_control_layout, fakeRootLinearLayout);
        controlLayout = (LinearLayout) uiIncreamBox.findViewById(R.id.controlLayout);
        LinearLayout layout = (LinearLayout) controlLayout.findViewById(R.id.control);
        ImageView addMoreImg = (ImageView) controlLayout.findViewById(R.id.addMoreImg);
        TextView addMoreTextview = (TextView) controlLayout.findViewById(R.id.addMoreTextview);
        resultText = (TextView) controlLayout.findViewById(R.id.resulttext);
        resultText.setText(Html.fromHtml(mPage.getRawContext().getString(R.string.totalmoney)
                                         + "：<font color=red>" + String.format("%.2f", mTotalMoney)
                                         + "</font>"
                                         + mPage.getRawContext().getString(R.string.Yuan)));

        OnClickListener addListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                addIncreamItem("", 0, false);
            }
        };
        layout.setOnClickListener(addListener);
        addMoreImg.setOnClickListener(addListener);
        addMoreTextview.setOnClickListener(addListener);

        LinearLayout controlLayout = (LinearLayout) uiIncreamBox.findViewById(R.id.control);
        if (mAddCount == 20 && controlLayout.getVisibility() == View.VISIBLE && layout != null) {
            controlLayout.setVisibility(View.GONE);
        }

    }

    private void addIncreamItem(String account, double money, boolean isInit) {
        if(!mCanAdd)return;
        LinearLayout layout = (LinearLayout) uiIncreamBox.findViewById(R.id.control);
        if (!isInit && mAddCount == 20 && layout != null) {
            layout.setVisibility(View.GONE);
            return;
        }

        if (!isInit && mAddCount < 20 && layout != null) {
            layout.setVisibility(View.VISIBLE);
        }

        RelativeLayout tItem = (RelativeLayout) LayoutInflater.from(mPage.getRawContext()).inflate(
            R.layout.incream_getmoney_item, null);

        LinearLayout accountBoxLayout = (LinearLayout) tItem.findViewById(R.id.accountbox);
        UIAccountBox accountbox = new UIAccountBox(mPage);
        accountbox.init(accountBoxLayout);

        mCanAdd = false;
        accountBoxLayout.addView(accountbox);
        mAddCount++;

        cacheLayoutHashMap.put(String.valueOf(mAddCount), tItem);
        itemsLayout.addView(tItem);
        MSubItem tSubItem = new MSubItem();
        tSubItem.mAccount = (EditText) tItem.findViewById(R.id.PayAccountEditText);
        tSubItem.mMoney = (EditText) tItem.findViewById(R.id.increamGetMoneyCountEditText);
        tSubItem.mMoney.setTag(String.valueOf(mAddCount));
        if (mAddCount == 1 && isContainSelf) {
            accountbox.setEnable(false);
        }
        if (money > 0)
            tSubItem.mMoney.setText(String.valueOf(money));
        if (account != null && account.length() > 0)
            tSubItem.mAccount.setText(account);
        final EditText moneyview = tSubItem.mMoney;
        tSubItem.mMoney.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                try {
                    if (s.length() > 0) {
                        String tString = s.toString().trim();
                        if ((tString.length() > 0)) {
                            int dotPos = tString.indexOf('.');
                            if (dotPos != -1) {
                                if (tString.length() > dotPos + 3) {
                                    tString = tString.substring(0, dotPos + 3);
                                    moneyview.setText(tString);
                                    moneyview.setSelection(tString.length());
                                }
                                if (dotPos == 0) {
                                    tString = "0" + tString;
                                    moneyview.setText(tString);
                                    moneyview.setSelection(tString.length());
                                }

                            }
                        }
                    }
                    //--
                    double mo = new Double(s.toString()).doubleValue();
                    mTotalMoney += mo;
                    String fm = String.format(
                        mPage.getRawContext().getResources().getString(R.string.totalcount),
                        mTotalMoney);
                    resultText.setText(Html.fromHtml(mPage.getRawContext().getString(
                        R.string.totalmoney)
                                                     + "：<font color=red>"
                                                     + fm
                                                     + "</font>"
                                                     + mPage.getRawContext().getString(
                                                         R.string.Yuan)));
                    //--

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
                try {
                    double mo = new Double(s.toString()).doubleValue();
                    mTotalMoney -= mo;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //        tSubItem.mContacts = (ImageView) accountBoxLayout.findViewById(R.id.PayAccountSearch);
        //        tSubItem.mContacts.setTag(String.valueOf(mAddCount));
        mSubItem.put(String.valueOf(mAddCount), tSubItem);

        tSubItem.mDelContactView = (TextView) tItem.findViewById(R.id.DelContactTextview);
        tSubItem.mDelContactView.setTag(String.valueOf(mAddCount));
        tSubItem.mDelContact = (RelativeLayout) tItem.findViewById(R.id.DelContactView);
        tSubItem.mDelContact.setTag(String.valueOf(mAddCount));

        tSubItem.mDelContactImg = (ImageView) tItem.findViewById(R.id.DelContactimg);
        tSubItem.mDelContactImg.setTag(String.valueOf(mAddCount));
        OnClickListener listener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String tag = (String) v.getTag();
                removeIncreamItem(tag);
            }

        };
        tSubItem.mDelContactView.setOnClickListener(listener);
        tSubItem.mDelContactImg.setOnClickListener(listener);
    }

    protected void removeIncreamItem(String tag) {
        RootActivity activity = (RootActivity) mPage.getEngine().getContext();
        int tagint = new Integer(tag).intValue();
        if ((isContainSelf && mAddCount > 2) || (!isContainSelf && mAddCount > 1)
            || (isContainSelf && mAddCount == 2 && tagint == 1)) {
            if(!mCanDelete)return;
            if (isContainSelf && tagint == 1) {
                isContainSelf = false;
            }
            View v = (View) cacheLayoutHashMap.get(tag);
            mCanDelete = false;
            itemsLayout.removeView(v);
            cacheLayoutHashMap.remove(tag);
            mAddCount--;
            Toast.makeText(mPage.getRawContext(),
                mPage.getRawContext().getString(R.string.delsuccess), 1000).show();

            //在这里要添加一个回调接口来改变总金额，尚未添加
            try {
                double mo = new Double(mSubItem.get(tag).mMoney.getText().toString()).doubleValue();
                mTotalMoney -= mo;
                String fm = String.format(
                    (mPage.getRawContext().getResources()).getString(R.string.totalcount),
                    mTotalMoney);
                resultText.setText(Html.fromHtml(mPage.getRawContext().getString(
                    R.string.totalmoney)
                                                 + "：<font color=red>"
                                                 + fm
                                                 + "</font>"
                                                 + mPage.getRawContext().getString(R.string.Yuan)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSubItem.remove(tag);
        } else if (isContainSelf && cacheLayoutHashMap.size() == 2 && tagint != 1) {
            String warningMsg = uiIncreamBox.getResources().getString(R.string.includeErr);
            activity.getDataHelper().showDialog((Activity) mPage.getRawContext(),
                R.drawable.infoicon,
                mPage.getRawContext().getResources().getString(R.string.WarngingString),
                warningMsg, mPage.getRawContext().getResources().getString(R.string.Ensure), null,
                null, null, null, null);
        } else {
            String warningMsg = uiIncreamBox.getResources().getString(R.string.lessthanone);
            activity.getDataHelper().showDialog((Activity) mPage.getRawContext(),
                R.drawable.infoicon,
                mPage.getRawContext().getResources().getString(R.string.WarngingString),
                warningMsg, mPage.getRawContext().getResources().getString(R.string.Ensure), null,
                null, null, null, null);
        }

        LinearLayout layout = (LinearLayout) uiIncreamBox.findViewById(R.id.control);
        if (layout.getVisibility() == View.GONE && layout != null) {
            layout.setVisibility(View.VISIBLE);
        }

    }

    class MSubItem {
        EditText mAccount;
        EditText mMoney;
        //        ImageView            mContacts;
        RelativeLayout mDelContact = null;
        TextView mDelContactView = null;
        ImageView mDelContactImg = null;
    }

    @Override
    public Object getValue() {
        Set<String> set = mSubItem.keySet();
        MSubItem item = null;
        JSONArray ret = new JSONArray();
        JSONObject o = null;
        int index = 0;
        for (String str : set) {
            try {
                o = new JSONObject();
                item = mSubItem.get(str);
                o.put("tag", "" + index++);
                String account = item.mAccount.getText().toString();
                if (account != null && account != "") {
                    if (account.indexOf("(") != -1) {
                        account = account.trim();
                        int startIndex = account.indexOf("(");
                        account = account.substring(startIndex + 1, account.length() - 1);
                    }
                }
                o.put("account", account);
                String amount = item.mMoney.getText().toString();
                o.put("amount", amount);
                ret.put(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret.toString();
    }

    @Override
    public boolean validateCheck() {
        RootActivity activity = (RootActivity) mPage.getEngine().getContext();
        Set<String> set = mSubItem.keySet();
        MSubItem item = null;
        boolean ret = true;
        String warningMsg = "";
        for (String str : set) {
            try {
                item = mSubItem.get(str);
                String account = item.mAccount.getText().toString();
                if (account == null || account.length() <= 0) {
                    warningMsg = mPage.getRawContext().getString(R.string.get_money_no_account);
                    activity.getDataHelper().showDialog((Activity) mPage.getRawContext(),
                        R.drawable.infoicon,
                        mPage.getRawContext().getResources().getString(R.string.WarngingString),
                        mPage.getRawContext().getString(R.string.get_money_no_account),
                        mPage.getRawContext().getResources().getString(R.string.Ensure), null,
                        null, null, null, null);
                    ret = false;
                    break;
                }

                if (mSubItem.size() == 1 && account.equals(activity.getAccountName(true))) {
                    warningMsg = mPage.getRawContext().getString(R.string.includeErr);
                    activity.getDataHelper().showDialog((Activity) mPage.getRawContext(),
                        R.drawable.infoicon,
                        mPage.getRawContext().getResources().getString(R.string.WarngingString),
                        mPage.getRawContext().getString(R.string.includeErr),
                        mPage.getRawContext().getResources().getString(R.string.Ensure), null,
                        null, null, null, null);
                    ret = false;
                    break;
                }
                String amount = item.mMoney.getText().toString();
                if (amount == null || amount.length() <= 0 ) {
                    warningMsg = mPage.getRawContext().getString(R.string.get_money_no_money);
                    activity.getDataHelper().showDialog((Activity) mPage.getRawContext(),
                        R.drawable.infoicon,
                        mPage.getRawContext().getResources().getString(R.string.WarngingString),
                        mPage.getRawContext().getString(R.string.get_money_no_money),
                        mPage.getRawContext().getResources().getString(R.string.Ensure), null,
                        null, null, null, null);
                    ret = false;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (ret == false) {
            BaseHelper.recordWarningMsg(activity, warningMsg);
        }
        return ret;
    }

    @Override
    public boolean inputed() {
        Set<String> set = mSubItem.keySet();
        MSubItem item = null;
        boolean ret = false;
        for (String str : set) {
            try {
                item = mSubItem.get(str);
                String account = item.mAccount.getText().toString();
                if (account != null && account.length() > 0) {
                    ret = true;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
}
