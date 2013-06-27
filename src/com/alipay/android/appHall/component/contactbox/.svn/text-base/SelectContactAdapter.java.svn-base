package com.alipay.android.appHall.component.contactbox;

import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.android.appHall.uiengine.Contact;
import com.eg.android.AlipayGphone.R;

public class SelectContactAdapter extends BaseAdapter {

    private Context           context;

    private ArrayList<Contact> contactArray;

    public SelectContactAdapter(Context mContext, ArrayList<Contact> contactArray) {
        this.context = mContext;
        this.contactArray = contactArray;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return contactArray.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ContactItem item;
        if (convertView == null) {
            item = new ContactItem();
            convertView = LayoutInflater.from(context).inflate(
                R.layout.local_contact_view_320_480, null);
            item.contactIcon = (ImageView) convertView.findViewById(R.id.LocalContactIcon);
            item.contactName = (TextView) convertView.findViewById(R.id.ContactInfoItemAccount);
            convertView.setTag(item);
        } else {
            item = (ContactItem) convertView.getTag();
        }

        Contact contact = contactArray.get(position);
        item.contactIcon.setVisibility(View.VISIBLE);
        item.contactName.setVisibility(View.VISIBLE);
        item.contactName.setText(contact.name);
        return convertView;
    }
    
    private final DataSetObservable mObservable = new DataSetObservable();
    
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mObservable.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mObservable.unregisterObserver(observer);
    }
    
    public void dataChanged(){
        mObservable.notifyChanged();
    }
//=======================================================
    
    
    

    class ContactItem {
        ImageView contactIcon;
        TextView  contactNum;
        TextView contactName;
    }

}
