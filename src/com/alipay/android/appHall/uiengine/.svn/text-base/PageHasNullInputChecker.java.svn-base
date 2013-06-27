package com.alipay.android.appHall.uiengine;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.alipay.android.appHall.component.UIAccountBox;
import com.alipay.android.appHall.component.UIContactBox;
import com.alipay.android.appHall.component.UIInputBox;
import com.alipay.android.appHall.component.UIMultiEditBox;

public class PageHasNullInputChecker implements TextWatcher{
	private List<UIInterface> mNeedCheckViews = new CopyOnWriteArrayList<UIInterface>();
	private List<UIInterface> mNeedEnableButtons = new CopyOnWriteArrayList<UIInterface>();
	
	public void addNeedCheckView(UIInterface needCheckView){
		mNeedCheckViews.add(needCheckView);
	}
	
	public boolean hasWaitInputView(){
		for(UIInterface needCheckedView : mNeedCheckViews){
			if(!needCheckedView.getVisible())
				mNeedCheckViews.remove(needCheckedView);
		}
		return mNeedCheckViews.size() != 0 ;
	}
	
	public void addNeedEnabledButton(UIInterface needEnableButton){
		needEnableButton.setEnable(false);
		mNeedEnableButtons.add(needEnableButton);
	}
	
	public void clearNeedEnableButton(){
		mNeedEnableButtons.clear();
	}
	
	public void setEnable(){
		for(UIInterface needEnableButton : mNeedEnableButtons){
			needEnableButton.setEnable(true);
		}
	}
	
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		//enable the buttons need enabled
		boolean hasNull = checkHasNull();
		
		for(UIInterface needEnableButton : mNeedEnableButtons){
			needEnableButton.setEnable(!hasNull);
		}
	}

	private boolean checkHasNull() {
		for(final UIInterface needCheckView : mNeedCheckViews){
			if(!needCheckView.getVisible())
				continue;
			
			if(needCheckView instanceof UIInputBox){
				if(!((UIInputBox)needCheckView).Inputed()){
					((UIInputBox) needCheckView).setButtonVisiable(false);
					return true;
				}else {
					((UIInputBox) needCheckView).setButtonVisiable(true);
					((UIInputBox) needCheckView).getClearButton().setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
						   ((UIInputBox) needCheckView).getEditText().setText("");
						   ((UIInputBox) needCheckView).setButtonVisiable(false);
						}
					});
				}
			}else if(needCheckView instanceof UIMultiEditBox){
				if(!((UIMultiEditBox)needCheckView).Inputed()){
					return true;
				}
			}else if(needCheckView instanceof UIAccountBox){
				if(!((UIAccountBox)needCheckView).Inputed()){
					return true;
				}
			}else if(needCheckView instanceof UIContactBox){
				if(!((UIContactBox)needCheckView).Inputed()){
					return true;
				}
			}
		}
		return false;
	}
}
