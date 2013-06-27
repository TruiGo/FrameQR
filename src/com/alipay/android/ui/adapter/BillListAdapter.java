package com.alipay.android.ui.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.android.client.baseFunction.AlipayDetailInfo;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.comon.component.ScrollMoreListAdapter;
import com.alipay.android.ui.bean.BillListInfo;
import com.eg.android.AlipayGphone.R;

public class BillListAdapter extends ScrollMoreListAdapter{
	
	private BillListInfo billListInfo = null;
	private Context context;
	
    public BillListAdapter(ListView listView, Activity context, ArrayList<?> data,BillListInfo billListInfo) {
        super(listView, context, data);
        this.billListInfo = billListInfo;
        this.context = context;
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup arg2) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                R.layout.alipay_trading_remind_item_320_480, null);
        }
        AlipayDetailInfo item = (AlipayDetailInfo) mListDatas.get(position);
        
        TextView mCommodityTitle = (TextView) convertView.findViewById(R.id.TradingRemindTitle);
        TextView mMoney = (TextView) convertView.findViewById(R.id.TradingRemindMoney);
        TextView mDate = (TextView) convertView.findViewById(R.id.TradingRemindDate);
        TextView mState = (TextView) convertView.findViewById(R.id.TradingRemindState);
        TextView mAct = (TextView) convertView.findViewById(R.id.TradingAct);
        ImageView listDivide = (ImageView)convertView.findViewById(R.id.BillListDivide);
        LinearLayout seperator = (LinearLayout) convertView.findViewById(R.id.divider_for_lifepay);
        View listEnd = convertView.findViewById(R.id.ListEnd);

		removeDivide(convertView);
		
		if(seperator != null && listDivide != null)
		{
			if(position == (billListInfo.getLifePayCount()-1) && item.getBillType() == 1)
			{
				listDivide.setVisibility(View.GONE);
				seperator.setVisibility(View.VISIBLE);
			}
			else
			{
				listDivide.setVisibility(View.VISIBLE);
				seperator.setVisibility(View.GONE);
			}
		}
		
		// if (position == 0)
		// {
		// // addDivideAtStart(convertView);
		// if (item.getBillType() == 1)
		// {
		// layoutTop.setBackgroundResource(R.color.TextColorWhite);
		// layoutBottom.setBackgroundResource(R.color.TextColorWhite);
		// }
		// else if(item.getBillType() == 2)
		// {
		// layoutTop.setBackgroundResource(R.color.TradeBackGroundColor);
		// layoutBottom.setBackgroundResource(R.color.TextColorWhite);
		// }
		// }
		// // else if(position == billListInfo.getWaitPayBillCount()){
		// // addDivideAtStart(convertView);
		// // }
		// else
		// {
		// if (item.getBillType() == 1)
		// {
		// layoutTop.setBackgroundResource(R.color.TextColorWhite);
		// layoutBottom.setBackgroundResource(R.color.TextColorWhite);
		// }
		// else if(item.getBillType() == 2)
		// {
		// layoutTop.setBackgroundResource(R.color.TradeBackGroundColor);
		// layoutBottom.setBackgroundResource(R.color.TextColorWhite);
		// }
		// removeDivide(convertView);
		// }
        
        if(listDivide != null){
        	BaseHelper.fixBackgroundRepeat(listDivide);
        }
        
        listEnd.setVisibility(View.GONE);
        
		// if(position == getCount()-1){
		// listEnd.setVisibility(View.VISIBLE);
		// BaseHelper.fixBackgroundRepeat(listEnd);
		// }else{
		// listEnd.setVisibility(View.GONE);
		// }
        
        mCommodityTitle.setText(Html.fromHtml(item.resultGoodsName).toString());   
        if(item.resultTradeMoney != null && context != null && item.getBillType() == 1)
		{
        	mMoney.setText(item.resultTradeMoney + context.getResources().getString(R.string.Yuan));
		}
        else
        {
        	mMoney.setText(item.resultTradeMoney);
        }
        mDate.setText(item.resultTradeTime);

        if ((item.resultAct != null && item.resultAct.indexOf("买") != -1) 
        		|| item.getBillType() == 1) {
            mAct.setText("-");
        }
        if (item.resultAct != null && item.resultAct.indexOf("卖") != -1) {
            mAct.setText("+");
        }
        
        item.setStatusColor(mState);
        return convertView;
    }


    @Override
	public boolean hasMore() {
        return billListInfo.hasMore();
    }

	@Override
	protected void itemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		
	}

}
