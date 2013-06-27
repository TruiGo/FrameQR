package com.alipay.android.appHall.appManager;

public class FileHelper {
    static boolean delete(String path) {
        boolean deleteState = true;
        try {
            String command = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();

            @SuppressWarnings("unused")
            Process proc = runtime.exec(command);

            //
            // A delay wait for the command operation to completely finished.
            // Note: This is important,otherwise some unexpected things will appear.
            Thread.sleep(1*1000);
        } catch (Exception e) {
            e.printStackTrace();
            deleteState = false;
        }
        return deleteState;
    }
}
