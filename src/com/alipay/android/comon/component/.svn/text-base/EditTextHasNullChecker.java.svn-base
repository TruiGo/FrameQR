package com.alipay.android.comon.component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;

public class EditTextHasNullChecker implements TextWatcher{
	private List<EditText> mNeedCheckViews = new CopyOnWriteArrayList<EditText>();
	private Set<Button> mNeedEnableButtons = new HashSet<Button>();
	
	public void addNeedCheckView(EditText needCheckView){
		mNeedCheckViews.add(needCheckView);
		validate();
	}
	
	/**
	 * 移除需要检测的控件
	 */
	public void removeNeedCheckView(EditText needCheckView){
		mNeedCheckViews.remove(needCheckView);
		validate();
	}
	
	/**
	 * 添加进集合之后立即进行一次验证
	 */
	public void addNeedEnabledButton(Button needEnableButton){
		if(needEnableButton != null){
			needEnableButton.setEnabled(false);
			mNeedEnableButtons.add(needEnableButton);
			validate();
		}
	}
	
	/**
	 * 移除需要置灰的按钮
	 * @param needRemovedButton
	 */
	public void removeButton(Button needRemovedButton){
		mNeedEnableButtons.remove(needRemovedButton);
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		validate();
	}

	private void validate() {
		//enable the buttons need enabled
		boolean hasNull = checkHasNull();
		for(Button needEnableButton : mNeedEnableButtons){
			needEnableButton.setEnabled(!hasNull);
		}
	}

	private boolean checkHasNull() {
		for(EditText needCheckView : mNeedCheckViews){
			if(!getViewVisiable(needCheckView))
				continue;
			
			if("".equals(needCheckView.getText().toString().trim())){
				return true;
			}
		}
		return false;
	}
	
	private boolean getViewVisiable(View view){
		if(view.getVisibility() == View.GONE || view.getVisibility() == View.INVISIBLE){
			return false;
		}else if(view.getParent() instanceof View && !(view.getParent() instanceof TabHost)){
			return getViewVisiable((View)view.getParent());
		}
		return true;
	}
}
