/**
 * 
 */
package com.alipay.android.comon.component;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li
 *
 */
public class ProgressDiv extends AlertDialog {
    private ProgressBar mProgress;
    private TextView mMessageView;
    private CharSequence mMessage;
    private boolean mIndeterminate;

    public ProgressDiv(Context context) {
        this(context,R.style.Float);
    }

    public ProgressDiv(Context context, int theme) {
        super(context, R.style.Float);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_dialog);
        mProgress = (ProgressBar) findViewById(android.R.id.progress);
        mMessageView = (TextView) findViewById(R.id.message);

        if (mMessage != null) {
            setMessage(mMessage);
        }
        setIndeterminate(mIndeterminate);
    }

    @Override
    public void setMessage(CharSequence message) {
        if (mProgress != null) {
            mMessageView.setText(message);
        } else {
            mMessage = message;
        }
    }
    

    public void setIndeterminate(boolean indeterminate) {
        if (mProgress != null) {
            mProgress.setIndeterminate(indeterminate);
        } else {
            mIndeterminate = indeterminate;
        }
    }
}
