package com.alipay.android.viewflow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants.BehaviourID;
import com.eg.android.AlipayGphone.R;

public class NumberFlowIndicator extends LinearLayout implements View.OnClickListener {
	private int  leftIndicatorId;
	private int  rightIndicatorId;
	private float  leftTextSize;
	private float  rightTextSize;
	private int  leftTextColor;
	private int  rightTextColor;
	
	private ImageView leftIndicatorImageView;
	private ImageView rightIndicatorImageView;
	private TextView leftIndicatorTextView;
	private TextView rightIndicatorTextView;
//	private ViewFlow viewFlow;
	private ViewPager viewPager;
	
	private int currentPage = 0;
	private int totalPage;
	private Context mContext;
	
	public NumberFlowIndicator(Context context) {
		super(context);
		mContext = context;
	}
	
	public NumberFlowIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.number_flow_indicator, this, true);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberIndicator);
		
		rightIndicatorId = a.getInt(R.styleable.NumberIndicator_rightIndicator, 0);
		leftIndicatorId = a.getInt(R.styleable.NumberIndicator_leftIndicator, 0);
		leftTextSize = a.getDimension(R.styleable.NumberIndicator_leftTextSize, 16.0f);
		rightTextSize = a.getDimension(R.styleable.NumberIndicator_rightTextSize, 16.0f);
		leftTextColor = a.getColor(R.styleable.NumberIndicator_leftTextColor, Color.parseColor("#388243"));
		rightTextColor = a.getColor(R.styleable.NumberIndicator_rightTextColor, Color.parseColor("#666666"));  
		a.recycle();
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		leftIndicatorImageView = (ImageView) findViewById(R.id.indicator_left);
		rightIndicatorImageView = (ImageView) findViewById(R.id.indicator_right);
		leftIndicatorTextView = (TextView) findViewById(R.id.currentPage);
		rightIndicatorTextView = (TextView) findViewById(R.id.totalPage);

		setIndicatorImage(leftIndicatorImageView, leftIndicatorId);
		setIndicatorImage(rightIndicatorImageView, rightIndicatorId);
		setTextAttr(leftIndicatorTextView, leftTextSize, leftTextColor);
		setTextAttr(rightIndicatorTextView, rightTextSize, rightTextColor);

		leftIndicatorImageView.setOnClickListener(this);
		rightIndicatorImageView.setOnClickListener(this);
	}

	private void setIndicatorImage(ImageView imageView, int indicatorId) {
		if(indicatorId != 0)
			imageView.setBackgroundResource(indicatorId);
	}

	private void setTextAttr(TextView textView,float textSize,int textColor) {
		textView.setTextSize(textSize);
		textView.setTextColor(textColor);
	}

	public void onSwitched(View view, int position) {
		currentPage = position;
		refreshPageCount();
		
		AlipayLogAgent.writeLog(mContext, BehaviourID.CLICKED, "walletTicket", "myTicketDetails", "myTicketDetails", "changeMyTicketIcon");
	}

	public void refreshPageCount() {
		totalPage = viewPager.getAdapter().getCount();
		if(totalPage > 1)
			setVisibility(View.VISIBLE);
		else
			setVisibility(View.GONE);
		
		leftIndicatorTextView.setText((currentPage <= 0 ? 1 : currentPage + 1) +"");
		rightIndicatorTextView.setText((totalPage <= 0 ? 1 : totalPage) + "");
		
		if(currentPage == 0 && totalPage > 1){
			leftIndicatorImageView.setVisibility(View.INVISIBLE);
			rightIndicatorImageView.setVisibility(View.VISIBLE);
		} else if(currentPage > 0 && currentPage == totalPage - 1){
			leftIndicatorImageView.setVisibility(View.VISIBLE);
			rightIndicatorImageView.setVisibility(View.INVISIBLE);
		} else if(currentPage == 0 && totalPage == 1){
			leftIndicatorImageView.setVisibility(View.INVISIBLE);
			rightIndicatorImageView.setVisibility(View.INVISIBLE);
		}	else{
			leftIndicatorImageView.setVisibility(View.VISIBLE);
			rightIndicatorImageView.setVisibility(View.VISIBLE);
		}
	}

	public void setViewPager(ViewPager viewPager) {
		this.viewPager = viewPager;
		refreshPageCount();
		invalidate();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.indicator_left:
//			viewFlow.moveToPrevious();
//			viewPager.arrowScroll(arg0);
			viewPager.arrowScroll(currentPage ++);
			break;
		case R.id.indicator_right:
//			viewFlow.moveToNext();
			viewPager.arrowScroll(currentPage --);
			break;
		default:
			break;
		}
	}
}
