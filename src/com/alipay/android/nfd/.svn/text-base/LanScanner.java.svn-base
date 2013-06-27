package com.alipay.android.nfd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alipay.android.util.LogUtil;

/**
 * 扫描同一局域网扫描类
 * @author daping.gp
 * @version $Id: LanScannerPeer.java, v 0.1 2012-6-20 下午1:53:07 daping.gp Exp $
 */
public class LanScanner {
    private final static String TAG = "LanScannerPeer";

    /** 本地计算的ip地址段 */
    private String startIp;
    /**自定义服务版本和协议号  */
    public final String PROTOCOL_VERSION = "XTTP/1.0";
    private static int produceTaskMaxNumber = 10;
    LinkedBlockingQueue<String> linkRetList = new LinkedBlockingQueue<String>();
    private Object mlockNotify = new Object();
    private int finishNum;
    private String mNetmask;
    private int miLocalIp;
    private String mLocalIp;
    private boolean shouldExit;

    private void notifyFinishState() {
        synchronized (mlockNotify) {
            finishNum++;
            if (finishNum >= produceTaskMaxNumber) {
                mlockNotify.notify();
            }
        }
    }

    public void setThreadExit() throws IOException {
        shouldExit = true;
    }

    public LanScanner(String ip, String netmask, int localIp) {
        shouldExit = false;
        this.startIp = ip;
        this.mNetmask = netmask;
        this.miLocalIp = localIp;
        this.mLocalIp = IpConversionUtil.convertIpaddr2String(localIp) + "#";
        finishNum = 0;
    }

    public int getUnsignedLongFromIpToSubIp(String ip) {
        String[] a = ip.split("\\.");
        return (Integer.parseInt(a[0]) * 0 + Integer.parseInt(a[1]) * 65536
                + Integer.parseInt(a[2]) * 256 + Integer.parseInt(a[3]));
    }

    public void doScanTask() throws InterruptedException {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(produceTaskMaxNumber - 2,
            produceTaskMaxNumber + 2, produceTaskMaxNumber, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(5));
        int ipNum = (getUnsignedLongFromIpToSubIp("255.255.255.255"))
                    - (getUnsignedLongFromIpToSubIp(mNetmask));
        if (ipNum > 600) {
            ipNum = 600;
        }

        int tempip = IpConversionUtil.convertString2Ipaddr(startIp);
        synchronized (mlockNotify) {
        for (int i = 1; i <= produceTaskMaxNumber; i++) {
            // 产生一个任务，并将其加入到线程池 
            threadPool.execute(new SannerRunnable(IpConversionUtil
                .convertIpaddr2String(tempip + (i - 1) * ipNum / produceTaskMaxNumber),
                (tempip + i * ipNum / produceTaskMaxNumber)));
        }
            mlockNotify.wait();
        }
        threadPool.shutdownNow();
    }

    /**
         * 扫描端口方法类
         * @author daping.gp
         * @version $Id: LanScannerPeer.java, v 0.1 2012-6-20 下午2:45:31 daping.gp Exp $
         */
    private class SannerRunnable implements Runnable {

        private String mStartip;
        private Long mEndip;

        private final String TAG = "SannerRunnable";

        private SannerRunnable(String startip, long lastip) {
            this.mStartip = startip;
            mEndip = lastip;
        }

        private void detectReachability(String ip) {
            DatagramSocket server = null;
            try {
                server = new DatagramSocket();
            } catch (SocketException e1) {
                e1.printStackTrace();
            }
            try {
                if (server == null) {
                    return;
                }
                //LogUtil.logAnyTime("ip", ip);
                InetAddress dstAddress = InetAddress.getByName(ip);
                DatagramPacket dPacket = new DatagramPacket(mLocalIp.getBytes(), mLocalIp.length(),
                    dstAddress, NFDUtils.UDP_BROADCAST_PORT);
                server.send(dPacket);
                server.close();
                server = null;
            } catch (IOException e) {
                if (server != null) {
                    server.close();
                    //LogUtil.logAnyTime(TAG, "Exception close");
                    server = null;
                }
                LogUtil.logOnlyDebuggable(TAG, e.toString());
            }
        }

        public void run() {
            for (int ip = IpConversionUtil.convertString2Ipaddr(mStartip); ip <= mEndip; ip++) {
                if (shouldExit) {
                    return;
                }
                if (ip == miLocalIp) {
                    continue;
                }
                detectReachability(IpConversionUtil.convertIpaddr2String(ip));
            }
            notifyFinishState();
        }
    }
}
