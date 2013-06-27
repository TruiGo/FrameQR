/**
 * 
 */
package com.alipay.android.client.baseFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.os.Bundle;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.RootActivity;
import com.alipay.android.common.data.MessageData;
import com.alipay.android.common.data.MessageRecord;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li
 *
 */
public class MessageEditor extends RootActivity implements OnItemClickListener, OnClickListener {
    private ListView mListView;
    private Button mDelBtn;

    private BaseAdapter mListAdapter;

    private ArrayList<MessageRecord> mDatas;
    private MessageData mMessageData;
    
    private int mSelectedNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_editor);

        AlipayApplication application = (AlipayApplication) getApplicationContext();
        mMessageData = application.getMessageData();
        mDatas = mMessageData.getAllMsg();
        if (mDatas == null){
        	mDatas = new ArrayList<MessageRecord>();
        }else{
        	Collections.sort(mDatas);
        }
            
        loadAllVariables();
    }

    private void loadAllVariables() {
    	TextView title = (TextView)findViewById(R.id.title_text);
    	title.setText(R.string.message_title);
    	
        mListView = (ListView) findViewById(R.id.content);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setOnItemClickListener(this);
        mListAdapter = new MsgAdapter();
        mListView.setAdapter(mListAdapter);

        mDelBtn = (Button) findViewById(R.id.delete);
        mDelBtn.setOnClickListener(this);
    }

    private class ViewHolder {
    	ImageView mTypeIcon;
        TextView mTitle;
        TextView mSubTitle;
        CheckBox mSelect;
    }

    private class MsgAdapter extends BaseAdapter {
    	
    	HashMap <Integer, Integer> typeIcon = new HashMap <Integer, Integer>();
    	
    	public MsgAdapter(){
    		super();
    		typeIcon.put(MessageRecord.TEMPLATE_TYPE_ACCOUNCEMENT, R.drawable.template_accouncement);
            typeIcon.put(MessageRecord.TEMPLATE_TYPE_BUSINESS, R.drawable.template_business);
            typeIcon.put(MessageRecord.TEMPLATE_TYPE_ACTIVITY, R.drawable.template_activity);
            typeIcon.put(MessageRecord.TEMPLATE_TYPE_OFF, R.drawable.template_off);
    	}

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(MessageEditor.this).inflate(
                    R.layout.message_item, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.mTypeIcon = (ImageView)convertView.findViewById(R.id.type_icon);
                viewHolder.mTitle = (TextView) convertView.findViewById(R.id.title);
                viewHolder.mSubTitle = (TextView) convertView.findViewById(R.id.subtitle);
                viewHolder.mSelect = (CheckBox) convertView.findViewById(R.id.select);
                convertView.setTag(viewHolder);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            MessageRecord msgItem = mDatas.get(position);
            if(msgItem.getTemplateType() != MessageRecord.TEMPLATE_TYPE_UNKNOWN){
            	holder.mTypeIcon.setVisibility(View.VISIBLE);
            	holder.mTypeIcon.setBackgroundResource(typeIcon.get(msgItem.getTemplateType()));
            }else{
            	holder.mTypeIcon.setVisibility(View.GONE);
            }
            if (msgItem.isNew()){
                holder.mTitle.setText(Html.fromHtml("<b>"+msgItem.getTopic()+"</b>"));
                holder.mTitle.setTextColor(getResources().getColor(R.color.text_gray));
                holder.mSubTitle.setTextColor(getResources().getColor(R.color.TextColorGrayTwo));
            }else{
                holder.mTitle.setText(msgItem.getTopic());
                holder.mTitle.setTextColor(getResources().getColor(R.color.TextColorGrayTwo));
                holder.mSubTitle.setTextColor(getResources().getColor(R.color.TextColorGrayTwo));
            }
            holder.mSubTitle.setText(msgItem.getDate());
            holder.mSelect.setVisibility(View.VISIBLE);
            if (mListView.isItemChecked(position)) {
                holder.mSelect.setChecked(true);
            } else {
                holder.mSelect.setChecked(false);
            }
            return convertView;
        }

    }

    @Override
    public void onClick(View view) {
        SparseBooleanArray checks = mListView.getCheckedItemPositions();
        if (checks == null){
            finish();
            return;
        }
        boolean hasDel = false;
        for (int i = 0; i < checks.size(); ++i) {
            int index = checks.keyAt(i);
            if (checks.get(index)) {
                hasDel = true;
                mMessageData.deleteMsg(mDatas.get(index));
            }
        }
        if (hasDel)
            setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> listView, View view, int position, long arg3) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.mSelect.toggle();
        boolean isCheck = holder.mSelect.isChecked();
        if(isCheck)
            mSelectedNum++;
        else
            mSelectedNum--;
        mListView.setItemChecked(position, isCheck);

        if (mSelectedNum > 0) {
            mDelBtn.setEnabled(true);
            mDelBtn.setText(getString(R.string.delete_now)+"("+mSelectedNum+")");
        } else {
            mDelBtn.setEnabled(false);
            mDelBtn.setText(getString(R.string.delete_now));
        }
    }
}
