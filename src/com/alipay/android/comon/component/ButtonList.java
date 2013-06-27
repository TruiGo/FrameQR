package com.alipay.android.comon.component;

import java.util.ArrayList;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.Utilz;
import com.alipay.android.ui.bean.ButtonItem;
import com.eg.android.AlipayGphone.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ButtonList extends LinearLayout {
	private ArrayList<ButtonItem> mArrayList;
	public ButtonList(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}
    public ButtonList(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        init();
    }
    private void init() {
    	this.setOrientation(VERTICAL);
    }
    
    OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
    
    public void setData(ArrayList<ButtonItem> arrayList) {
    	mArrayList = arrayList;
    	int count = arrayList.size();
    	if (1 == count) {
    		View childView = LayoutInflater.from(this.getContext()).inflate(R.layout.buttonlistitem,null);
    		childView.setBackgroundResource(R.drawable.list_btn_single);
    		
    		ButtonItem item = (ButtonItem)arrayList.get(0);
    		TextView buttonTextView = (TextView)childView.findViewById(R.id.itemText);
    		buttonTextView.setText(item.getText());
    		
    		String mobileNum = item.getTextExtra();
    		TextView buttonTextExtraView = null;
    		if (null != mobileNum) {
        		buttonTextExtraView = (TextView)childView.findViewById(R.id.itemTextExtra);
        		buttonTextExtraView.setText(Utilz.getMobileNum4Show(mobileNum));
    		}
    		if(buttonTextExtraView != null && item.getmTextExtraColor() != -1)
    		{
    			buttonTextExtraView.setTextColor(item.getmTextExtraColor());
    		}

    		childView.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					if (null != mOnItemClickListener) {
						int index = indexOfChild(v);
						mOnItemClickListener.onItemClick(v, index, getItemAction(index));
					}
				}
    			
    		});
    		
    		addView(childView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    	} else {
        	for (int i = 0; i < count; i++) {
        		View childView = LayoutInflater.from(this.getContext()).inflate(R.layout.buttonlistitem,null);

        		ButtonItem item = getItem(i);
        		if (0 == i) {
        			childView.setBackgroundResource(R.drawable.list_btn_top);
        		} else if (count - 1 == i) {
        			childView.setBackgroundResource(R.drawable.list_btn_f);
        		} else {
        			childView.setBackgroundResource(R.drawable.list_btn_m);
        		}
        		
        		TextView buttonTextView = (TextView)childView.findViewById(R.id.itemText);
        		buttonTextView.setText(item.getText());
        		
        		String mobileNum = item.getTextExtra();
        		TextView buttonTextExtraView = null;
        		if (null != mobileNum) {
            		buttonTextExtraView = (TextView)childView.findViewById(R.id.itemTextExtra);
            		buttonTextExtraView.setText(Utilz.getMobileNum4Show(mobileNum));
        		}
        		if(buttonTextExtraView != null && item.getmTextExtraColor() != -1)
        		{
        			buttonTextExtraView.setTextColor(item.getmTextExtraColor());
        		}
        		
        		childView.setOnClickListener(new View.OnClickListener(){
    				@Override
    				public void onClick(View v) {
    					if (null != mOnItemClickListener) {
    						int index = indexOfChild(v);
    						mOnItemClickListener.onItemClick(v, index, getItemAction(index));
    					}
    				}
        			
        		});
        		addView(childView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        	}
    	}

    }

	public ButtonItem getItem(int position) {
		// TODO Auto-generated method stub
		return mArrayList.get(position);
	}
	
	private int getItemAction(int position) {
		ButtonItem item = getItem(position);
		return null != item ? item.getAction() : Constant.ACTION_NONE;
	}

    public interface OnItemClickListener {
        void onItemClick(View view, int position, int id);
    }
}
