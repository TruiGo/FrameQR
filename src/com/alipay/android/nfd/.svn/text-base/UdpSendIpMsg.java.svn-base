package com.alipay.android.nfd;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import android.content.Context;
import android.util.Log;

import com.alipay.android.util.LogUtil;

public class UdpSendIpMsg extends Thread {
    private final static String TAG = "UdpSendIpMsg";
    private String registMsg;
    private final String PROTOCOL_VERSION = "XTTP/1.0";
    DatagramSocket socket;
    private boolean shouldExit;
    Context mContext;

    public void setThreadExit() {
        shouldExit = true;
        if (socket != null) {
            socket.close();
            socket = null;
        }
    }

    public UdpSendIpMsg(String msg, Context context) {
        registMsg = msg;
        shouldExit = false;
        mContext = context;
    }

    @Override
    public void run() {
        super.run();
        try {
            socket = new DatagramSocket(NFDUtils.UDP_BROADCAST_PORT);
        } catch (SocketException e1) {
        }
        while (true) {
            if (shouldExit) {
                break;
            }
            try {
                // 接收
                byte[] instr = new byte[200];
                DatagramPacket receive = new DatagramPacket(instr, instr.length);
                socket.receive(receive); // 接收客户机发过来的数据
                String str = new String(receive.getData()); // 提取数据
                str = str.substring(0, str.lastIndexOf("#"));
                String[] ipStr = str.split("\\.");
                String ip = (ipStr[3] + "." + ipStr[2] + "." + ipStr[1] + "." + ipStr[0]);
                Log.i("str：", str.trim()); // 打印客户机消息
                InetAddress dstAddress = InetAddress.getByName(ip);

                Socket clientSocket = new Socket(dstAddress, NFDUtils.SERVER_PORT);
                DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(
                    clientSocket.getOutputStream()));
                dataOutputStream.writeUTF(PROTOCOL_VERSION);
                dataOutputStream.writeInt(registMsg.length());
                dataOutputStream.write(registMsg.getBytes());
                dataOutputStream.flush();
                clientSocket.close();
                clientSocket = null;
                LogUtil.logAnyTime("UdpSendIpMsg", registMsg + " to ip = " + str);
            } catch (Exception e) {
                LogUtil.logOnlyDebuggable(TAG, e.toString());
            }
        }
    }
}
