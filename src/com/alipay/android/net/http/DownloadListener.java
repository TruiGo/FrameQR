package com.alipay.android.net.http;

public interface DownloadListener {
    public void updatePercent(double percent);
    public int getStatus();
}
