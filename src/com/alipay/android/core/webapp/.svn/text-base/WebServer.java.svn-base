package com.alipay.android.core.webapp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.util.HashMap;

import org.apache.cordova.api.LOG;

import android.net.Uri;

public class WebServer implements Runnable {
    //    private static String mJnject_lib ;

    private boolean isActive;
    private ServerSocketChannel mSocketChannel;
    private int mPort;
    private Thread serverThread;
    private WebAppRunTime mRunTime;

    private HashMap<String, byte[]> mCache;

    //    private ArrayList<String> mInjectUrls;

    public WebServer(WebAppRunTime runTime) {
        mRunTime = runTime;
        mCache = new HashMap<String, byte[]>();
        //        mInjectUrls = new ArrayList<String>();
    }

    @Override
    public void run() {
        try {
            String request;
            mSocketChannel = ServerSocketChannel.open();
            mSocketChannel.configureBlocking(true);
            ServerSocket waitSocket = mSocketChannel.socket();
            waitSocket.bind(new InetSocketAddress("127.0.0.1", 8080));

            waitSocket.setReuseAddress(true);
            mPort = waitSocket.getLocalPort();
            //            mJnject_lib = "<script type=\"text/javascript\" src=\"http://127.0.0.1:"+mPort+"/alipay.js\"></script>";

            while (isActive) {
                Socket connection = waitSocket.accept();
                BufferedReader xhrReader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()), 40);
                DataOutputStream output = new DataOutputStream(connection.getOutputStream());
                request = xhrReader.readLine();
                String response = "";
                if (isActive && (request != null)) {
                    if (request.contains("GET")) {
                        String[] requestParts = request.split(" ");
                        if (requestParts.length == 3) {
                            LOG.d("WebAppLog",
                                "load " + requestParts[1] + " start:" + System.currentTimeMillis());
                            byte[] content = null;
                            if (mCache.containsKey(requestParts[1])) {
                                content = mCache.get(requestParts[1]);
                            } else {
                                if (requestParts[1].equalsIgnoreCase("/AWebApp.js")) {
                                    content = getAsset("/cordova-1.5.0.js");
                                } else {
                                    content = getFile(requestParts[1]);
                                }
//                                if (content != null && content.length > 0) {
//                                    mCache.put(requestParts[1], content);
//                                }
                            }

                            if (content != null && content.length > 0) {
                                response = "HTTP/1.1 200 OK\r\n\r\n";
                                output.writeBytes(response);
                                output.write(content);
                            } else {
                                response = "HTTP/1.1 403 Forbidden\r\n\r\n ";
                                output.writeBytes(response);
                            }
                            LOG.d("WebAppLog",
                                "load " + requestParts[1] + " end  :" + System.currentTimeMillis());
                        }
                    } else {
                        response = "HTTP/1.1 400 Bad Request\r\n\r\n ";
                        output.writeBytes(response);
                    }
                    output.flush();
                }
                output.close();
                xhrReader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    public void addInjectUrl(String url){
    //        mInjectUrls.add(url);
    //    }

    public void start() {
        isActive = true;
        serverThread = new Thread(this);
        serverThread.start();
    }

    public void stop() {
        isActive = false;
        try {
            if (mSocketChannel != null) {
                mSocketChannel.close();
                mSocketChannel = null;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mCache.clear();
    }

    public int getPort() {
        return mPort;
    }

    private byte[] getAsset(String path) {
        InputStream inputStream = null;
        byte[] bs = null;
        try {
            inputStream = mRunTime.getAssets().open("webapp" + path);
            bs = new byte[inputStream.available()];
            inputStream.read(bs);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bs;
    }

    private byte[] getFile(String path) {
        Uri uri = Uri.parse(path);
        FileInputStream fileInputStream = null;
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            fileInputStream = new FileInputStream(mRunTime.getPath() + uri.getPath());
            byte[] bs = new byte[1024];
            int l = 0;
            while ((l = fileInputStream.read(bs)) > 0) {
                arrayOutputStream.write(bs, 0, l);
            }
            //            for(String url:mInjectUrls){
            //                if(url.endsWith(path)){//需要注入库
            //                    mInjectUrls.remove(url);
            //                    arrayOutputStream.write(mJnject_lib.getBytes());
            //                }
            //            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
                arrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return arrayOutputStream.toByteArray();
    }
}
