/**
 * 
 */
package com.alipay.android.net.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.alipay.android.net.NetworkManager;
import com.alipay.android.net.alipayclient2.APHttpClient;
import com.alipay.platform.core.AlipayApp;
import com.alipay.platform.core.Command;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li@alipay.com
 * 网络线程池
 *
 */
public class HttpManager implements NetworkManager {
    public static final String TAG = "NetworkManager";

    public static final int POOL_SIZE = 4;
    public static final int KEEP_ALIVE_TIME = 5;
    public static final int QUEUE_SIZE = 128;

    private Context mContext;
    private ThreadPoolExecutor mPool;

    public HttpManager(Context context) {
        mContext = context;
        setDefaultHostnameVerifier();
        //Thread pool
        mPool = new ThreadPoolExecutor(3, POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(QUEUE_SIZE));
    }

    @Override
    public void connect(final Command command) {
        mPool.execute(new Runnable() {
            public void run() {
                Object resp = null;
                try {
                    APHttpClient nm = new APHttpClient(command.getRequestUrl(),
                        HttpManager.this.mContext);
                    if (command.getMimeType() == DataParser.TYPE_NORMAL) {
                        StorageStateInfo.getInstance().putValue(
                            Constants.STORAGE_REQUESTTYPE,
                            new JSONObject(command.getRequestData().toString())
                                .optString(Constant.RQF_OPERATION_TYPE));

                        resp = nm.sendSynchronousRequest(command.getRequestData().toString());
                        //				            Log.i("Ap_Request",command.getRequestData().toString());
                    } else {
                        InputStream is = nm.inputStreamFromUrl();
                        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                        int len = -1;
                        byte[] buffer = new byte[1024];
                        while ((len = is.read(buffer)) != -1) {
                            arrayOutputStream.write(buffer, 0, len);
                        } 
                        resp = arrayOutputStream.toByteArray();
                        is.close();
                    }

                    if (resp != null && ((String) resp).length() > 0) {
                        AlipayApp app = (AlipayApp) mContext.getApplicationContext();
                        Object data = app.getController().getDataPaser()
                            .parseStream(resp, command.getMimeType());
                        command.setResponseData(data);
                        command.setResponseRawData(resp);
                        //							command.setResponseMessage(resp);
                        command.setState(HttpManager.this, Command.STATE_COMPLETED);
                    } else {
                        if (nm.errorType == HttpClient.SSL_ERROR) {
                            command.setResponseMessage(mContext.getResources().getString(
                                R.string.WarningDataCheck));
                        } else if (nm.errorType == HttpClient.IO_ERROR) {
                            command.setResponseMessage(mContext.getResources().getString(
                                R.string.CheckNetwork));
                        } else {
                            command.setResponseMessage(mContext.getString(R.string.CheckNetwork));
                        }
                        command.setState(HttpManager.this, Command.STATE_FAILED);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    command.setResponseMessage(mContext.getString(R.string.CheckNetwork));
                    command.setState(HttpManager.this, Command.STATE_FAILED);
                }
            }
        });
    }

    private void setDefaultHostnameVerifier() {
        HostnameVerifier hv = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    public boolean isNetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
            .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        return ni != null && ni.isAvailable();
    }

    @Override
    public int getType() {
        return CTR_NET;
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public Context getContext() {
        return mContext;
    }
}
