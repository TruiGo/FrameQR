package com.alipay.android.comon.component;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.alipay.android.client.lifePayment.ChineseToPy;
import com.alipay.android.ui.bean.ContactPerson;

public class SelectionBar extends ImageView {
	private int height;
	private int singleHeight = 0;
	private LetterSelectedListener letterSelectedListener;
	private char[] selectedChar= {'?','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','#'};
	
	public SelectionBar(Context context) {
		super(context);
	}
	
	public SelectionBar(Context context,AttributeSet set) {
		super(context,set);
	}
	
	public void initAlphaIndex(List<ContactPerson> contactPersons, Map<Character, Integer> alphaIndex){
		alphaIndex.clear();
		alphaIndex.put('?', 0);
		char preChar = ' ';
		ContactPerson  contactPerson;
		for(int i = 0;i < contactPersons.size();i++){
			contactPerson = contactPersons.get(i);
			char curChar = ChineseToPy.getSinglePy(contactPerson.displayName.substring(0,1)).charAt(0);
			
			if(preChar != curChar){
				alphaIndex.put(curChar, i + 1);
				preChar = curChar;
			}
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		height = getHeight();
		singleHeight = this.height / 28;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
	    final float y = event.getY();
	    int index = (int) (y / singleHeight) - 1;
	    
		switch (action) {
			case MotionEvent.ACTION_MOVE:
				if(index == -1){
					letterSelectedListener.onLetterSelected('?');
				}else if(index > -1 && index < selectedChar.length){
					letterSelectedListener.onLetterSelected(selectedChar[index]);
				}
			break;
		}
		return true;
	}
	

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		this.height = h;
		this.singleHeight = this.height / 28;
	}
	
	public void setOnLetterSelectedListener(LetterSelectedListener letterSelectedListener){
		this.letterSelectedListener = letterSelectedListener;
	}
	
	public interface LetterSelectedListener{
		void onLetterSelected(char selectedChar);
	}
}
