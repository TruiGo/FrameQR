package com.alipay.android.ui.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class TransferReceiver implements Parcelable{
	public String userId;
	public String recvName;
	public String recvRealName;
	public String recvMobile;
	public String bizType;
	public String recvAccount;
	public String transferType;
	
	public TransferReceiver(){}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public TransferReceiver(Parcel in){
		readFormParcel(in);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((recvAccount == null) ? 0 : recvAccount.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransferReceiver other = (TransferReceiver) obj;
		if (recvAccount == null) {
			if (other.recvAccount != null)
				return false;
		} else if (!recvAccount.equals(other.recvAccount))
			return false;
		return true;
	}

	@Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(recvName);
        dest.writeString(recvRealName);
        dest.writeString(recvMobile);
        dest.writeString(bizType);
        dest.writeString(recvAccount);
    }

    public static final Parcelable.Creator<TransferReceiver> CREATOR = new Parcelable.Creator<TransferReceiver>() {
        public TransferReceiver createFromParcel(Parcel in) {
        	return new TransferReceiver(in);
        }

		public TransferReceiver[] newArray(int size) {
            return new TransferReceiver[size];
        }
    };
    
    private void readFormParcel(Parcel in) {
    	userId = in.readString();
    	recvName = in.readString().trim();
    	recvRealName = in.readString();
    	recvMobile = in.readString();
    	bizType = in.readString();
    	recvAccount = in.readString();
	}
}
