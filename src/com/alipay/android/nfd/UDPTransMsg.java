package com.alipay.android.nfd;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import com.alipay.android.security.Des;
import com.alipay.android.util.LogUtil;

public class UDPTransMsg extends Thread {
    private final static String TAG = "UDPTransMsg";
    /** 自定义服务版本和协议号 */
    public final String PROTOCOL_VERSION = "XTTP/1.0";
    ServerSocket serverSocket = null;
    LinkedBlockingQueue<String> linkRetList = new LinkedBlockingQueue<String>();
    private boolean shouldExit;
    String mBizType;

    public UDPTransMsg(String bizType) {
        mBizType = bizType;
        shouldExit = false;
    }

    public LinkedBlockingQueue<String> getResult() {
        return linkRetList;
    }

    public void run() {
        getServiceResult();
    }

    public void setThreadExit() throws IOException {
        shouldExit = true;
        if (serverSocket != null) {
            serverSocket.close();
            serverSocket = null;
        }

    }

    public void getServiceResult() {
        try {
            serverSocket = new ServerSocket(NFDUtils.SERVER_PORT);
        } catch (IOException e2) {
            LogUtil.logOnlyDebuggable(TAG, e2.toString());
        }
        while (true) {
            if (shouldExit)
                break;
            try {
                // serverSocket = new ServerSocket(NFDUtils.SERVER_PORT);
                Socket socket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(
                    socket.getInputStream()));
                // XTTP/1.0
                String version = dataInputStream.readUTF();
                if (version != null && version.equalsIgnoreCase(PROTOCOL_VERSION)) {
                    // ContentLength.
                    int contentLength = dataInputStream.readInt();
                    // Data stream.
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] temp = new byte[contentLength];
                    dataInputStream.readFully(temp);
                    baos.write(temp);
                    String retStr = baos.toString();
                    baos.close();
                    try {
                        if (retStr.startsWith("$") && retStr.endsWith("^")) {
                            retStr = retStr.substring(1, retStr.length() - 1);
                            if (retStr.endsWith("#")) {
                                retStr = retStr.substring(0, retStr.length() - 1);
                                retStr = Des.decrypt(retStr, NFDUtils.ALIPAY_INFO);
                            }
                            String nameArray[] = retStr.split("#");
                            if (mBizType.endsWith("*")
                                || (nameArray.length >= 1 && nameArray[nameArray.length - 1]
                                    .equals(mBizType))) {
                                linkRetList.put(retStr.substring(0,
                                    (retStr.length() - mBizType.length() - 1)));
                            }
                        }
                    } catch (InterruptedException e) {
                        LogUtil.logOnlyDebuggable(TAG, e.toString());
                    }
                }
                socket.close();
            } catch (Exception e) {
                LogUtil.logOnlyDebuggable(TAG, e.toString());
            }
        }
    }
}