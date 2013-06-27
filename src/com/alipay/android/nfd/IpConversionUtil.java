package com.alipay.android.nfd;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpConversionUtil {

    /**
          * IP地址转为整数型
     * convert ipaddr like 1.1.1.1 to int
     * @param str
     * @return
     */
    public static int convertString2Ipaddr(String str){
        if(str==null||str.length()<=0){
            return -1;
        }
        InetAddress addr = null;
        
        try {
            addr = InetAddress.getByName(str);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return -1;
        }
    
        return convertBytes2Int(addr.getAddress());
    }

    /**
     * convert byte[4] to int，高位在前
     * @param addr
     * @return
     */
    public static int convertBytes2Int(byte[] addr){
        
        int address = 0;
        if (addr != null) {
            if (addr.length == 4) {
            address  = addr[3] & 0xFF;
            address |= ((addr[2] << 8) & 0xFF00);
            address |= ((addr[1] << 16) & 0xFF0000);
            address |= ((addr[0] << 24) & 0xFF000000);
            } 
        } 
        
        return address;
    }


 
    /**
          * 整数型转为IP地址
     * convert ipaddr like 172903153 to String 10.78.74.241
     * @param ipaddr
     * @return
     */
    public static String convertIpaddr2String(int ipaddr){
        
        byte[] bytes = convertInt2Bytes(ipaddr);
        
        InetAddress addr = null;
        
        try {
            addr = InetAddress.getByAddress(bytes);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
        
        return addr.getHostAddress();
    }

    /**
     * convert int to byte[4]
     * @param value
     * @return
     */
    public static byte[] convertInt2Bytes(int value){
        
        byte[] addr = new byte[4];

        addr[0] = (byte) ((value >>> 24) & 0xFF);
        addr[1] = (byte) ((value >>> 16) & 0xFF);
        addr[2] = (byte) ((value >>> 8) & 0xFF);
        addr[3] = (byte) (value & 0xFF);
        return addr;
    
    }
}
