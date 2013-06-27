/**
 * 
 */
package com.alipay.android.appHall.component.accountbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alipay.android.client.MessageFilter;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.comon.component.ProgressDiv;
import com.eg.android.AlipayGphone.R;

/**
 * @author chengkuang
 * 支付宝联系人
 */
public class AlipayContact extends RootActivity implements  OnItemClickListener,OnItemSelectedListener
{
	/**
	 * Tag for logcat
	 */
    public static final String LOG_TAG = "AlipyGetContact";

	/*
	 * do file
	 */
	private final String FILE_PATH = Constant.FILE_PATH;
	private final String FILE_NAME = "contact";
//	private CheckedTextView mLastCheck = null;//记录上一次的CheckedTextView的对象
	private MessageFilter mMessageFilter = new MessageFilter(this);
	private ProgressDiv mProgress=null;
	HashMap<String, String> myResponse = new HashMap<String, String>();
	JSONObject myJsonObject = null;
	private Intent intent;
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			showResult(msg);
			switch(msg.what)
			{
			case AlipayHandlerMessageIDs.QUERY_CONTACT:				
				if(getDataHelper().isCanceled())
				{
					jumpToHome();
				}	
				break;
			}
			super.handleMessage(msg);
		}
	};
	private class Contact
	{
//		String groupName = null;
		String name = null;
		String account = null;
		String refresh = null;
	}
	
//	private class ContactList
//	{
//		String groupName =null;		
//	}
	
//	private class ContactLists{
//		int selectedContactId = 0;
//		int groupCount = 0;
//		ArrayList<Contact> Contacts= new ArrayList<Contact>();
//	}
	private ArrayList<Contact> mContacts= new ArrayList<Contact>();
	private TextView mTitleName ;
//	private TextView mContantInfoText;
//	private TextView mRefresh;
	private ListView mContactListView;
//	private ContactLists mContactLists = new ContactLists();
//	private RelativeLayout mInfoCanvas=null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alipay_contact_320_480);
		loadAllVariables();
		if(isFile().exists())
		{
			myJsonObject = this.readContactToFile();
			if(myJsonObject == null)
			{
				getContactInfo();
			}
			else
			{
				loading();
				
				getContactInfoSuccess(myJsonObject);
			}
		}
		else
		{
			getContactInfo();
		}
		
	}
	
	private void loadAllVariables()
	{
		intent = this.getIntent();		
		mTitleName = (TextView) findViewById(R.id.title_text);
		mTitleName.setText(R.string.ContactInfoText);
//		mContantInfoText = (TextView)findViewById(R.id.ContactInfoText);
//		mContantInfoText.setText(R.string.ContactInfoText);
//		mInfoCanvas = (RelativeLayout) findViewById(R.id.ContactInfoDisplayArea);
//		mRefresh = (TextView)findViewById(R.id.ContactRefreshText);
//		mRefresh.setText(Html.fromHtml("<u>"+getResources().getString(R.string.Refresh)+"</u>"));
	    	mContactListView = (ListView) findViewById(R.id.ContactListViewCanvas);
//	    	mContactListView.setDivider(getResources().getDrawable(R.drawable.line3));
//	    	mContactListView.setAdapter(new ContactListAdapter(this,this.mContacts));
	    	mContactListView.setOnItemClickListener(this);
//		mRefresh.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
////				mInfoCanvas.removeView(mContactListView);
//				mContacts.clear();
//				getContactInfo();
//			}
//		}
//		);
	}
	
	private void getContactInfo()
	{
	    getDataHelper().sendQueryContactLists(mHandler, AlipayHandlerMessageIDs.QUERY_CONTACT);	
		loading();
	}
	
	private void loading()
	{
		if(mProgress == null){
			mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null, 
					getResources().getString(R.string.GetContactLoading), 
					false, true, 
					getDataHelper().cancelListener,
					getDataHelper().cancelBtnListener);
		}
	}
	
	private void errorInfo(String info)
	{
//		myHelper.showErrorDialog(this, 
//				R.drawable.infoicon, 
//				getResources().getString(R.string.WarngingString), info,
//				getResources().getString(R.string.Ensure), 
//				new DialogInterface.OnClickListener(){
//							@Override
//							public void onClick(DialogInterface arg0, int arg1)
//							{
//								finish();
//							}
//							
//						},
//				null, null,
//				null, null);
		TextView mContactListView = (TextView) findViewById(R.id.ContactInfoText);
		mContactListView.setVisibility(View.VISIBLE);
		mContactListView.setText(info);
	}
	private void showResult(Message msg)
	{
		Responsor responsor = (Responsor)msg.obj;
		boolean tResultOK = false;

		tResultOK = mMessageFilter.process(msg);
	    	if ((tResultOK) && (!getDataHelper().isCanceled()))
	    	{
	                getContactInfoSuccess(responsor.obj);	
//	                mInfoCanvas.addView(this.mContactListView);
	    	}
	    	
		if (mProgress != null){
        		mProgress.dismiss();
        		mProgress=null;
        	}
	}
	
	private void getContactInfoSuccess(JSONObject myJsonObject)
	{
		mContacts.clear();
		JSONArray array = myJsonObject.optJSONArray(Constant.RPF_CONTACTLISTS);
//		String groupCount = myJsonObject.optString(Constant.PRF_GROUPCOUNT);
		//得到有多少个用户组		
		int tGroupNum = 0;
		if (array == null)
		{
			errorInfo(getResources().getString(R.string.NoContact));
			if (mProgress != null){
	        		mProgress.dismiss();
	        		mProgress=null;
	        	}
			return;
		}
		for(int i = 0; i<array.length();i++)
		{			
			JSONObject itemObj = array.optJSONObject(i);
			if(itemObj != null)
			{
//				String mGroup = itemObj.optString(Constant.PRF_CONTACTGROUPNAME);
				JSONArray arrayContact = itemObj.optJSONArray(Constant.PRF_CONTACTS);
				if(arrayContact != null)
				{
					for(int j = 0;j<arrayContact.length()+1;j++)
					{
						if(j==arrayContact.length()&&j>0){
							Contact con = new Contact();							
							con.refresh = getResources().getString(R.string.Refresh_contact);
							mContacts.add(con);
						}else{
							JSONObject contactObj = arrayContact.optJSONObject(j);
							if(contactObj != null)
							{
								Contact con = new Contact();
//								con.groupName = mGroup;
								con.name = contactObj.optString(Constant.PRF_CONTACTNAME);
								con.account = contactObj.optString(Constant.PRF_CONTACTACCOUNT);
								mContacts.add(con);
							}
						}
					}
					writeContactToFile(myJsonObject);
					mContactListView.setAdapter(new ContactListAdapter(this,this.mContacts));
					tGroupNum++;
				}
			}
		}
		if(tGroupNum==0)
		{
			errorInfo(getResources().getString(R.string.NoContact));
		}
		if (mProgress != null){
        		mProgress.dismiss();
        		mProgress=null;
        	}
		
	}
	
	private void jumpToHome()
	{
		this.finish();
	}
	
	private class ContactListAdapter extends BaseAdapter
	{
		private ArrayList<Contact> tContacts;
		private Context mContext;
		int mListlength;
		public ContactListAdapter(Context c,ArrayList<Contact> list){
			tContacts = list;
    		mContext = c;
    		mListlength = tContacts.size();
    	}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mListlength;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;//mContactLists.Contacts.get(position);
		}

//		private int totalCount =  tContacts.size();
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
//			if(position <= mListlength){
//				return position;
//			}else{
//				return -1;
//			}
		}

		private class ContactItem{
			TextView contactName;
			TextView contactAccout;
//			ImageView icon;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ContactItem item;
			if (convertView == null){
				item = new ContactItem();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.alipay_contactview_320_480, null);
				item.contactName = (TextView) convertView.findViewById(R.id.ContactInfoItemName);
				item.contactAccout = (TextView) convertView.findViewById(R.id.ContactInfoItemAccount);
//				item.icon = (ImageView) convertView.findViewById(R.id.ContactInfoItemIcon);
				convertView.setTag(item);
			}else{
				item = (ContactItem) convertView.getTag();
			}

			Contact contact = tContacts.get(position);
			if(position == mListlength -1){
				item.contactName.setVisibility(View.GONE);
				item.contactAccout.setVisibility(View.GONE);
				TextView refreshview = (TextView) convertView.findViewById(R.id.RefreshContacts);
				refreshview.setVisibility(View.VISIBLE);
				refreshview.setText(contact.refresh + "");
			}else{
				String mTemp = contact.name;
				item.contactName.setVisibility(View.VISIBLE);
				item.contactAccout.setVisibility(View.VISIBLE);
				TextView refreshview = (TextView) convertView.findViewById(R.id.RefreshContacts);
				refreshview.setVisibility(View.GONE);
				item.contactName.setText(mTemp);
				item.contactAccout.setText(contact.account);	
			}
			return convertView;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(arg0 == mContactListView)
		{	
			if(arg2 == mContactListView.getCount()-1){
				getContactInfo();
			}else{

			String account = mContacts.get(arg2).account;
			intent.setData(Uri.parse(account));
			String name = mContacts.get(arg2).name;
			intent.putExtra(Constant.PRF_CONTACTNAME, name);
			
			this.setResult(Activity.RESULT_OK, intent);			
			finish();
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) 
	{
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0)
	{
		// TODO Auto-generated method stub
	}
	
	/*
	 * Get this file 
	 */
	private File isFile()
	{
		File file = new File(FILE_PATH+this.getPackageName()+"/files/"+getAccountName(true)+"/contact/", FILE_NAME);
		return file;
	}
	/*
	 * write file
	 */
	private void writeContactToFile(JSONObject jObject)
	{
		
		try
		{			
//			String strUserId = myHelper.mDefaultValueMap.get(Constant.RPF_LOGON_ID);
			File file = new File(FILE_PATH+this.getPackageName()+"/files/"+getAccountName(true)+"/contact/");
			file.mkdirs();
			File fileName = new File(FILE_PATH+this.getPackageName()+"/files/"+getAccountName(true)+"/contact/",FILE_NAME);
			fileName.createNewFile();
			FileOutputStream out = new FileOutputStream(fileName);
			out.write(jObject.toString().getBytes());
			out.flush();
			out.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * read file
	 */
	private JSONObject readContactToFile()
	{
		JSONObject jObject = null;
		
		try
		{
			File file = isFile();
			if( file.exists() )
			{
				FileInputStream in = new FileInputStream(file);
				if(in.available()>0)
				{
					jObject = new JSONObject(BaseHelper.convertStreamToString(in));		
				}
				in.close();
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jObject;
	}
	/*
	 * add contact to file
	 */
	public  void addContactToFile(String groupName, String name ,String account)
	{
		try
		{		
			boolean isGroup = false;//判断是否有这个组
			boolean isContact = false;//判断是否已有此联系人
			File file = isFile();
			if( file.exists() )
			{
				FileInputStream in = new FileInputStream(file);
				JSONObject jObject  = new JSONObject(BaseHelper.convertStreamToString(in));	
				JSONArray mArray = jObject.optJSONArray(Constant.RPF_CONTACTLISTS);
				if(mArray != null)
				{
					for(int i = 0;i<mArray.length();i++)
					{
						JSONObject mObject = mArray.optJSONObject(i);
						if(mObject.optString(Constant.PRF_CONTACTGROUPNAME).equals(groupName))//由此组
						{
							isGroup = true;
							JSONArray mContactsArray = mObject.optJSONArray(Constant.PRF_CONTACTS);
							if(mContactsArray !=null)
							{
								for(int j = 0;j<mContactsArray.length();j++)
								{
									JSONObject mContacts = mContactsArray.optJSONObject(j);
									if(mContacts.optString(Constant.PRF_CONTACTACCOUNT).equals(account))//有此联系人
									{
										isContact = true;
										break;
									}
								}
							}
							if(!isContact)
							{	
								JSONArray contacts = new JSONArray();
								JSONObject contact = new JSONObject();
								contact.put(Constant.PRF_CONTACTNAME, name);
								contact.put(Constant.PRF_CONTACTACCOUNT, account);
								contacts.put(contact);
								mObject.optJSONArray(Constant.PRF_CONTACTS).put(contacts);
							}
							break;
						}
					}
				}
				if(!isGroup)//没有这个组的，自己加入此json对象
				{	
					JSONObject group = new JSONObject();
					JSONObject contact = new JSONObject();
					contact.put(Constant.PRF_CONTACTNAME, name);
					contact.put(Constant.PRF_CONTACTACCOUNT, account);
					JSONArray array = new JSONArray();
					array.put(contact);
					group.put(Constant.PRF_CONTACTGROUPNAME, groupName);
					group.put(Constant.PRF_CONTACTS, array);
					mArray.put(group);
				}
				in.close();
				if(isContact)
				{
					errorInfo("已有此联系人");
				}
				else
				{
					FileOutputStream out = new FileOutputStream(file);
					out.write(jObject.toString().getBytes());
					out.flush();
					out.close();
				}
			}
			else
			{
//				String strUserId = myHelper.mDefaultValueMap.get(Constant.RPF_LOGON_ID);
				File dire = new File(FILE_PATH+this.getPackageName()+"/files/"+getAccountName(true)+"/contact/");
				dire.mkdirs();
				File fileName = new File(FILE_PATH+this.getPackageName()+"/files/"+getAccountName(true)+"/contact/",FILE_NAME);
				fileName.createNewFile();
				JSONObject root = new JSONObject();	
				JSONObject group = new JSONObject();
				JSONObject contact = new JSONObject();
				contact.put(Constant.PRF_CONTACTNAME, name);
				contact.put(Constant.PRF_CONTACTACCOUNT, account);
				JSONArray contactArray = new JSONArray();
				contactArray.put(contact);
				group.put(Constant.PRF_CONTACTGROUPNAME, groupName);
				group.put(Constant.PRF_CONTACTS, contactArray);
				JSONArray groupArray = new JSONArray();
				groupArray.put(group);
				root.put(Constant.RPF_CONTACTLISTS, groupArray);
				FileOutputStream out = new FileOutputStream(fileName);
				out.write(root.toString().getBytes());
				out.flush();
				out.close();				
			}

		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if (keyCode == KeyEvent.KEYCODE_BACK) {
	        	jumpToHome();
	            return true;
	        }

	        return false;
	    }
}
