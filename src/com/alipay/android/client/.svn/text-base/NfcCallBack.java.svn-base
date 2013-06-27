package com.alipay.android.client;

import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;

public class NfcCallBack implements CreateNdefMessageCallback {
	Main    app;
    private NfcAdapter mNfcAdapter;
    Context            mcontext;

    public NfcCallBack(Main activty, Context context) {
        mcontext = context;
        app = activty;
        mNfcAdapter = NfcAdapter.getDefaultAdapter(mcontext);
        if (mNfcAdapter != null) {
            // Register callback to set NDEF message
            mNfcAdapter.setNdefPushMessageCallback(this, activty);
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        return app.createNdefMessage(event);
    }

}
