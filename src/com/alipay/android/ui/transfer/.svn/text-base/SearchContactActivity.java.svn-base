package com.alipay.android.ui.transfer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.client.RootActivity;
import com.alipay.android.comon.component.EditTextWithButton;
import com.alipay.android.dao.ContactPersonDAO;
import com.alipay.android.dataprovider.ContactProvider;
import com.alipay.android.ui.adapter.ContactListAdapter;
import com.alipay.android.ui.bean.ContactPerson;
import com.alipay.android.ui.bean.TransferReceiver;
import com.alipay.android.ui.fragment.MultiPhoneNumPop;
import com.eg.android.AlipayGphone.R;

public class SearchContactActivity extends RootActivity implements TextWatcher,OnTouchListener{
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;
	private EditTextWithButton mSearchInput;
	private ListView contactListView;
	private TextView noMatchedContactView;
	private ContactPersonDAO contactPersonDAO;
	private ContactProvider contactProvider;
	private ContactListAdapter contactListAdapter;
	private ContactPerson contactClicked;
	private TransferReceiver transformReceiver;
	private View alipaySubTitleView;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_transform_contact);
		
		loadAllVariables();
        initListener();
	}

	private void loadAllVariables() {
		mSearchInput = (EditTextWithButton) findViewById(R.id.searchEditText);
		noMatchedContactView = (TextView) findViewById(R.id.noMatchedContactView);
		alipaySubTitleView = findViewById(R.id.AlipaySubTitle);
		TextView titleView = (TextView) alipaySubTitleView.findViewById(R.id.title_text);
		titleView.setText(R.string.select_receiver);
		contactPersonDAO = new ContactPersonDAO();
		contactProvider = ContactProvider.getInstance();
		contactListView = (ListView) findViewById(R.id.contactListView);
		contactListAdapter = new ContactListAdapter(this);
		contactListView.setAdapter(contactListAdapter);
	}
	
	private void initListener() {
		mSearchInput.getEtContent().addTextChangedListener(this);
		contactListView.setOnTouchListener(this);
		noMatchedContactView.setOnTouchListener(this);
		
		mSearchInput.getEtContent().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK){
					finish();
				}
				return false;
			}
		});
		
		
		contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				contactClicked = contactListAdapter.getItem(position);
				
				if(contactClicked.phoneNumber.size() > 1){
					showContactPop(contactClicked);
				}else{
					transformReceiver = new TransferReceiver();
					transformReceiver.recvName = contactClicked.displayName;
					transformReceiver.recvMobile = contactClicked.phoneNumber.get(0);
					backToPrevious();
				}
			}
		});
	}
	
	private void showContactPop(final ContactPerson contactPerson){
		final MultiPhoneNumPop multiPhoneNumPop = new MultiPhoneNumPop(this);
		multiPhoneNumPop.showPop(contactPerson, new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				transformReceiver = new TransferReceiver();
				transformReceiver.recvName = contactPerson.displayName;
				transformReceiver.recvMobile = contactPerson.phoneNumber.get(position);
				multiPhoneNumPop.dismissPop();
				
				backToPrevious();
			}
		});
	}
	
	private void backToPrevious() {
		setResult(Activity.RESULT_OK,getIntent().putExtra("transformReceiver", transformReceiver));
		finish();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(matches.size() > 0){
            	mSearchInput.setText(matches.get(0));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	/**
	 * 输入框搜索
	 */
	public void afterTextChanged(Editable s) {
		String inputedStr = s.toString().trim();
		if (inputedStr.length() == 0) {
			contactPersonDAO.resetContactPerson(contactProvider.getContactData());
			contactListView.setVisibility(View.GONE);
			noMatchedContactView.setText("");
		}else{
			List<ContactPerson> contacterSearched = contactPersonDAO.getMatchedContact(inputedStr,contactProvider.getContactData());
			if(contacterSearched.size() > 0){
				contactListView.setVisibility(View.VISIBLE);
				noMatchedContactView.setText("");
			}else{
				contactListView.setVisibility(View.GONE);
				noMatchedContactView.setText(R.string.noResult);//android:text="@string/noResult"
			}
			contactListAdapter.refreshUI(contacterSearched);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		contactPersonDAO.resetContactPerson(contactProvider.getContactData());
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.contactListView:
			Helper.hideInput(SearchContactActivity.this, mSearchInput);
			return false;
		case R.id.noMatchedContactView:
			Helper.hideInput(SearchContactActivity.this, mSearchInput);
			finish();
			return true;
		default:
			break;
		}
		return false;
	}
}
