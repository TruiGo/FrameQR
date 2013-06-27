package com.alipay.android.appHall;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.alipay.android.util.LogUtil;

public class DeviceHelper {
    static final String TAG = "DeviceHelper";

    public static String getUserAgent(Context context) {
        // device info
        String device = getDevice();
        
        // os info
        String fv = getOsInfo(context);

        // kernel version
        String kv = getKernelVersion(context);

        // locale
        String locale = getDefaultLocale(context);

        // protocol
        String protocol = getProtocol(context);

        // default font size
        String fontsize = getDefaultFontSize(context);

        // Apn
        String strApn = getApnInUse(context);

        // cell info
        String cellinfo = getCellInfo(context);

        return " (" + device + ";" + fv + ";" + kv + ";" + locale + ";" + protocol + ";" + fontsize + ";" + strApn + ";" + cellinfo + ")";
    }

    public static String getDevice() {
        //String device = "Milestone";
        return Build.MODEL;
    }

    public static String getOsInfo(Context context) {
        return "Android " + Build.VERSION.RELEASE;
    }

    public static String getKernelVersion(Context context) {
        String fkv = getFormattedKernelVersion();
        int i = fkv.indexOf("-");
        if (i != -1)
            fkv = fkv.substring(0, i);

        i = fkv.indexOf("\n");
        if (i != -1)
            fkv = fkv.substring(0, i);

        return "Linux " + fkv;
    }

    public static String getFormattedKernelVersion() {
        String procVersionStr;

        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/version"), 256);
            try {
                procVersionStr = reader.readLine();
            } finally {
                reader.close();
            }

            final String PROC_VERSION_REGEX = "\\w+\\s+" + /* ignore: Linux */
            "\\w+\\s+" + /* ignore: version */
            "([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
            "\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /*
                                                        * group 2:
                                                        * (xxxxxx@xxxxx
                                                        * .constant)
                                                        */
            "\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+" + /* ignore: (gcc ..) */
            "([^\\s]+)\\s+" + /* group 3: #26 */
            "(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
            "(.+)"; /* group 4: date */

            Pattern p = Pattern.compile(PROC_VERSION_REGEX);
            Matcher m = p.matcher(procVersionStr);

            if (!m.matches()) {
                LogUtil.logAnyTime(TAG, "Regex did not match on /proc/version: " + procVersionStr);
                return "Unavailable";
            } else if (m.groupCount() < 4) {
            	LogUtil.logAnyTime(TAG, "Regex match on /proc/version only returned " + m.groupCount()
                           + " groups");
                return "Unavailable";
            } else {
                return (new StringBuilder(m.group(1)).append("\n").append(m.group(2)).append(" ")
                    .append(m.group(3)).append("\n").append(m.group(4))).toString();
            }
        } catch (IOException e) {
            LogUtil.logAnyTime(TAG, "IO Exception when getting kernel version for Device Info screen", e);

            return "Unavailable";
        }
    }

    public static String getDefaultLocale(Context context) {
        Resources res = context.getResources();
        Configuration cfg = res.getConfiguration();
        Locale locale = cfg.locale;
        return locale.toString();
    }

    public static String getProtocol(Context context) {
        String protocol = "https";
        return protocol;
    }

    public static String getScreenResolution(Context context) {
        DisplayMetrics displayMetrics = Helper.getDisplayMetrics(context);
        StringBuilder resolution = new StringBuilder();
        resolution.append(displayMetrics.widthPixels);
        resolution.append("*");
        resolution.append(displayMetrics.heightPixels);

        return resolution.toString();
    }

    public static String getDefaultFontSize(Context context) {
        float size = new TextView(context).getTextSize();
        return Float.toString(size);
    }

    public static String getApnInUse(Context context) {
        String apn = "wifi";

        try {
            ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();

            String extra = ni.getExtraInfo();
            if (extra == null || extra.indexOf("none") != -1)
                apn = ni.getTypeName();
            else
                apn = extra;
        } catch (Exception e) {
            e.printStackTrace();
        }

        apn = apn.replace("internet", "wifi");
        apn = apn.replace("\"", "");
        return apn;
    }

    public static String getCellInfo(Context context) {
        String cellInfo = "-1;-1";

        try {
            TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
            GsmCellLocation gsmcl = (GsmCellLocation) tm.getCellLocation();
            int cellid = gsmcl.getCid();
            int lac = gsmcl.getLac();

            StringBuilder sbcellInfo = new StringBuilder();
            sbcellInfo.append(lac);
            sbcellInfo.append(";");
            sbcellInfo.append(cellid);

            cellInfo = sbcellInfo.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cellInfo;
    }

}
