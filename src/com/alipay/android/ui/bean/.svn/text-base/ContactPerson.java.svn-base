package com.alipay.android.ui.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;


public class ContactPerson implements Parcelable{
	public String displayName;
	public List<String> phoneNumber = new ArrayList<String>();
	public List<String[]> matchedPinYin;
	public boolean isNumberMatch = false;
	public String matchedNum;
	
	@Override
	public String toString() {
		return displayName + phoneNumber;
	}
	
	public ContactPerson(){}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public ContactPerson(Parcel in){
		readFormParcel(in);
	}
	
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(displayName);
        dest.writeList(phoneNumber);
        dest.writeList(matchedPinYin);
        dest.writeString(isNumberMatch + "");
        dest.writeString(matchedNum);
    }

    public static final Parcelable.Creator<ContactPerson> CREATOR = new Parcelable.Creator<ContactPerson>() {
        public ContactPerson createFromParcel(Parcel in) {
        	return new ContactPerson(in);
        }

		public ContactPerson[] newArray(int size) {
            return new ContactPerson[size];
        }
    };
    
    private void readFormParcel(Parcel in) {
    	displayName = in.readString();
    	in.readList(phoneNumber,getClass().getClassLoader());
    	in.readList(matchedPinYin, getClass().getClassLoader());
    	isNumberMatch = Boolean.parseBoolean(in.readString());
    	matchedNum = in.readString();
	}
    
	@Override
	public boolean equals(Object o) {
		if(o == null)
			return false;
		ContactPerson contactPerson = (ContactPerson) o;
		if (this.displayName.equals(contactPerson.displayName)) {
			for (String phoneNumber : contactPerson.phoneNumber) {
				if (!this.phoneNumber.contains(phoneNumber))
					return false;
			}
			return true;
		}
		return super.equals(o);
	}
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
		return result;
	}
}
