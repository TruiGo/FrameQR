package com.alipay.android.appHall.appManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipHelper {
    
    static boolean unZip(InputStream isUnZip, String destPath) {
        boolean unZipState = true;
        
        FileOutputStream fileOut = null;
        ZipInputStream zipIn = null;
        ZipEntry zipEntry = null;

        File file = null;
        int readedBytes;
        byte buf[] = new byte[8*1024];

        try {
            zipIn = new ZipInputStream(new BufferedInputStream(isUnZip));
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                file = new File(destPath + zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                } else {
                    // 如果指定文件的目录不存在,则创建之.
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    
                    fileOut = new FileOutputStream(file);
                    while ((readedBytes = zipIn.read(buf)) > 0) {
                        fileOut.write(buf, 0, readedBytes);
                    }
                    fileOut.close();
                }

                zipIn.closeEntry();
            }
        } catch (Exception e) {
            e.printStackTrace();
            unZipState = false;
        } finally {
            try {
                if (zipIn != null)
                    zipIn.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return unZipState;
    }
    
    public static boolean unZip(String unZipfileName, String destPath) {
        boolean unZipState = false;
        
        try {
            unZipState = unZip(new FileInputStream(unZipfileName), destPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        return unZipState;
    }

}
