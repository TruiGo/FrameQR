/**
 * 
 */
package com.alipay.android.appHall.security;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import android.content.DialogInterface;

import com.alipay.android.client.exception.SystemExceptionHandler;
import com.alipay.android.comon.component.StyleAlertDialog;
import com.alipay.android.core.Engine;
import com.alipay.android.log.Constants;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li
 *
 */
public class CertHandler {
    public static final String TAG = "CertHandler";

    private static final String PREFIX_MD5_DIGEST = "Md5-Digest:";
    private static final String PREFIX_NAME = "Name:";
    private static final String CERT_NAME = "CERT";

    private Engine mEngine;
    private String mAppPkg;

    private HashMap<String, String> mCertData;

    public CertHandler(Engine mEngine, String mAppPkg) {
        this.mEngine = mEngine;
        this.mAppPkg = mAppPkg;
        mCertData = new HashMap<String, String>();
    }

    public boolean init() {
        Scanner scanner = null;
        try {
            File file = new File(mAppPkg + CERT_NAME);
            scanner = new Scanner(file);
            String name = null;
            String digest = null;
            while (scanner.hasNextLine()) {
                String string = scanner.nextLine().trim();
                if (string.indexOf(PREFIX_NAME) == 0) {
                    name = string.substring(PREFIX_NAME.length()).trim();
                    string = scanner.nextLine().trim();
                    if (string.indexOf(PREFIX_MD5_DIGEST) == 0) {
                        digest = string.substring(PREFIX_MD5_DIGEST.length()).trim();
                    } else {
                        throw new Exception("certify error!");
                    }

                    if (name.length() > 0 && digest.length() > 0) {
                        mCertData.put(name, digest);
                    } else {
                        throw new Exception("certify error!");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            LogUtil.logContainerDebuggable(TAG, "CERT file can't find");
            return false;
        } catch (Exception e) {
            //verify Exception to be log
            SystemExceptionHandler.getInstance().saveConnInfoToFile(e, Constants.MONITORPOINT_CONNECTERR);
            LogUtil.logContainerDebuggable(TAG, "CERT file parser error: " + e.getMessage());
            error();
            return false;
        }
        return true;
    }

    private void error() {
        StyleAlertDialog alertDialog = new StyleAlertDialog(mEngine.getContext(),
            R.drawable.erroricon, mEngine.getContext().getString(R.string.Error), mEngine
                .getContext().getString(R.string.package_verify_error), mEngine.getContext()
                .getString(R.string.Ensure), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mEngine.exit();
                }
            }, null, null, null);
        alertDialog.show();
    }

    public File verify(String path) {
        File file = null;
        try {
            file = new File(path);
            String digest = mCertData.get(path.substring(mAppPkg.length()));
            if (!VerifyHelper.verify(file, digest))//验证没通过
                throw new Exception("certify error!");
        } catch (FileNotFoundException e) {
            LogUtil.logContainerDebuggable(TAG, "File: " + path + " can't find");
        } catch (Exception e) {
            //verify Exception to be log
            SystemExceptionHandler.getInstance().saveConnInfoToFile(e, Constants.MONITORPOINT_CONNECTERR);
            LogUtil.logContainerDebuggable(TAG, "certify file:  " + path + " fail");
            error();
        }
        return file;
    }

    /**
     * @param mAppPkg
     * @param path
     * @return
     * 验证单个文件
     */
    public static File verify(String mAppPkg, String path) {
        File file = null;
        Scanner scanner = null;
        try {
            File f = new File(mAppPkg + File.separator + CERT_NAME);
            scanner = new Scanner(f);
            String name = null;
            String digest = null;

            while (scanner.hasNextLine()) {
                String string = scanner.nextLine().trim();
                if (string.matches(PREFIX_NAME + "(\\s)*" + path)) {
                    name = string.substring(PREFIX_NAME.length()).trim();
                    string = scanner.nextLine().trim();
                    if (string.indexOf(PREFIX_MD5_DIGEST) == 0) {
                        digest = string.substring(PREFIX_MD5_DIGEST.length()).trim();
                    } else {
                        throw new Exception("certify error!");
                    }

                    if (name.length() > 0 && digest.length() > 0) {
                        file = new File(mAppPkg + File.separator + path);
                        if (!VerifyHelper.verify(file, digest)) {//验证没通过
                            throw new Exception("certify error!");
                        } else {
                            break;
                        }
                    } else {
                        throw new Exception("certify error!");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            LogUtil.logContainerDebuggable(TAG, "File: " + mAppPkg + "/" + path + " can't find");
        } catch (Exception e) {
            LogUtil
                .logContainerDebuggable(TAG, "certify file:  " + mAppPkg + "/" + path + "  fail");
        }

        return file;
    }
}
