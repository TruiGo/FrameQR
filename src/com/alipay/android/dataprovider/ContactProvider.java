package com.alipay.android.dataprovider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import com.alipay.android.client.util.ContactComparator;
import com.alipay.android.ui.bean.ContactPerson;

/**
 * 保存联系人信息供多个界面使用
 */
public class ContactProvider extends Observable{
	private static ContactProvider instance = null;
	private ContactProvider(){}
	private List<ContactPerson> contactList = new ArrayList<ContactPerson>(); 
	private boolean loadingFinish;
	private boolean isLoading;
	
	public static synchronized ContactProvider getInstance(){
		if(instance == null){
			instance = new ContactProvider();
		}
		return instance;
	}
	
	//将load出的数据添加到集合
	public void setContactData(List<ContactPerson> loadedContactList){
		contactList.clear();
		contactList.addAll(loadedContactList);
		Collections.sort(contactList, new ContactComparator());
		
		loadingFinish = true;
		isLoading = false;
		setChanged();
		notifyObservers(contactList);
	}
	
	public List<ContactPerson> getContactData(){
		return contactList;
	}
	
	public boolean loadingFinish(){
		return loadingFinish;
	}
	
	public void setLoadingFinish(boolean loadingFinish){
		this.loadingFinish = loadingFinish;
	}

	public boolean isLoading() {
		return isLoading;
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}
}
