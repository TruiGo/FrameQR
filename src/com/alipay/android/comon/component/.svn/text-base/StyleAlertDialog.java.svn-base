package com.alipay.android.comon.component;

import java.lang.ref.WeakReference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alipay.android.client.AlipayApplication;
import com.eg.android.AlipayGphone.R;

public class StyleAlertDialog implements View.OnClickListener {
    /**
     * android4.0需要左右按钮翻转
     */
    public static boolean mReverseButton;

    private AlertDialog dialog;
    private View view;
    private Button button1, button2;
    private ButtonHandler mHandler;
    private DialogInterface.OnClickListener btn1, btn2;

    public StyleAlertDialog(Context context, int iconRes, String titleText, String MessageText,
                            String btn1Msg, DialogInterface.OnClickListener btn1, String btn2Msg,
                            DialogInterface.OnClickListener btn2,
                            DialogInterface.OnCancelListener cancelListener) {
        if (Build.VERSION.RELEASE.compareTo("4.0") > 0 && !mReverseButton) {
            this.btn1 = btn2;
            this.btn2 = btn1;
            String str = btn1Msg;
            btn1Msg = btn2Msg;
            btn2Msg = str;
        } else {
            this.btn1 = btn1;
            this.btn2 = btn2;
        }
        AlertDialog.Builder tDialog = new AlertDialog.Builder(context);
        dialog = tDialog.create();
        mHandler = new ButtonHandler(dialog);
        dialog.setOnCancelListener(cancelListener);

        LayoutInflater inflater = dialog.getLayoutInflater();
        view = inflater.inflate(R.layout.alert_dialog, null);

        TextView message = (TextView) view.findViewById(R.id.message);
        if (MessageText == null || MessageText.length() <= 0) {
            message.setText(titleText);
        } else {
            message.setText(Html.fromHtml(MessageText));
        }

        button1 = (Button) view.findViewById(R.id.button1);
        if (btn1Msg != null) {
            button1.setOnClickListener(this);
            button1.setText(btn1Msg);
        } else {
            button1.setVisibility(View.GONE);
        }
        button2 = (Button) view.findViewById(R.id.button2);
        if (btn2Msg != null) {
            button2.setOnClickListener(this);
            button2.setText(btn2Msg);
        } else {
            button2.setVisibility(View.GONE);
        }

        if (Build.VERSION.RELEASE.compareTo("4.0") > 0 && !mReverseButton) {
            button1.setBackgroundResource(R.drawable.popwindow_btn_right);
            button1
                .setTextColor(context.getResources().getColorStateList(R.drawable.sub_btn_color));
            button2.setBackgroundResource(R.drawable.popwindow_btn_left);
            button2.setTextColor(context.getResources()
                .getColorStateList(R.drawable.main_btn_color));
        } else {
            button2.setBackgroundResource(R.drawable.popwindow_btn_right);
            button2
                .setTextColor(context.getResources().getColorStateList(R.drawable.sub_btn_color));
            button1.setBackgroundResource(R.drawable.popwindow_btn_left);
            button1.setTextColor(context.getResources()
                .getColorStateList(R.drawable.main_btn_color));
        }
    }

    public void show() {
        if (!dialog.isShowing()) {
            try {
                dialog.show();
                AlipayApplication application = (AlipayApplication) dialog.getContext()
                    .getApplicationContext();

                int screenWidth =  Integer.parseInt(application.getConfigData().getScreenHeight()) > Integer.parseInt(application.getConfigData().getScreenWidth())?Integer.parseInt(application.getConfigData().getScreenWidth()):Integer.parseInt(application.getConfigData().getScreenHeight());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                		screenWidth - 60,
                    LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                dialog.setContentView(view, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        Message msg = null;
        if (v == button1 && btn1 != null) {
            dialog.dismiss();
            msg = mHandler.obtainMessage(DialogInterface.BUTTON_POSITIVE, btn1);
        } else if (v == button2 && btn2 != null) {
            dialog.dismiss();
            msg = mHandler.obtainMessage(DialogInterface.BUTTON_NEGATIVE, btn2);
        } else {
            msg = mHandler.obtainMessage(ButtonHandler.MSG_DISMISS_DIALOG, dialog);
        }

        msg.sendToTarget();
    }

    private static final class ButtonHandler extends Handler {
        // Button clicks have Message.what as the BUTTON{1,2,3} constant
        private static final int MSG_DISMISS_DIALOG = 1;

        private WeakReference<DialogInterface> mDialog;

        public ButtonHandler(DialogInterface dialog) {
            mDialog = new WeakReference<DialogInterface>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case DialogInterface.BUTTON_POSITIVE:
                case DialogInterface.BUTTON_NEGATIVE:
                case DialogInterface.BUTTON_NEUTRAL:
                    ((DialogInterface.OnClickListener) msg.obj).onClick(mDialog.get(), msg.what);
                    break;

                case MSG_DISMISS_DIALOG:
                    ((DialogInterface) msg.obj).dismiss();
            }
        }
    }

    public AlertDialog getDialog() {
        return dialog;
    }

}
