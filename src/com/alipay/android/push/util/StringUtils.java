package com.alipay.android.push.util;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * A collection of utility methods for String objects.
 */
public class StringUtils {

    private static final char[] QUOTE_ENCODE = "&quot;".toCharArray();
    private static final char[] APOS_ENCODE = "&apos;".toCharArray();
    private static final char[] AMP_ENCODE = "&amp;".toCharArray();
    private static final char[] LT_ENCODE = "&lt;".toCharArray();
    private static final char[] GT_ENCODE = "&gt;".toCharArray();

    /**
     * Returns the name portion of a XMPP address. For example, for the
     * address "matt@jivesoftware.com/Smack", "matt" would be returned. If no
     * username is present in the address, the empty string will be returned.
     *
     * @param XMPPAddress the XMPP address.
     * @return the name portion of the XMPP address.
     */
    public static String parseName(String XMPPAddress) {
        if (XMPPAddress == null) {
            return null;
        }
        int atIndex = XMPPAddress.lastIndexOf("@");
        if (atIndex <= 0) {
            return "";
        }
        else {
            return XMPPAddress.substring(0, atIndex);
        }
    }

    /**
     * Returns the server portion of a XMPP address. For example, for the
     * address "matt@jivesoftware.com/Smack", "jivesoftware.com" would be returned.
     * If no server is present in the address, the empty string will be returned.
     *
     * @param XMPPAddress the XMPP address.
     * @return the server portion of the XMPP address.
     */
    public static String parseServer(String XMPPAddress) {
        if (XMPPAddress == null) {
            return null;
        }
        int atIndex = XMPPAddress.lastIndexOf("@");
        // If the String ends with '@', return the empty string.
        if (atIndex + 1 > XMPPAddress.length()) {
            return "";
        }
        int slashIndex = XMPPAddress.indexOf("/");
        if (slashIndex > 0 && slashIndex > atIndex) {
            return XMPPAddress.substring(atIndex + 1, slashIndex);
        }
        else {
            return XMPPAddress.substring(atIndex + 1);
        }
    }

    /**
     * Returns the resource portion of a XMPP address. For example, for the
     * address "matt@jivesoftware.com/Smack", "Smack" would be returned. If no
     * resource is present in the address, the empty string will be returned.
     *
     * @param XMPPAddress the XMPP address.
     * @return the resource portion of the XMPP address.
     */
    public static String parseResource(String XMPPAddress) {
        if (XMPPAddress == null) {
            return null;
        }
        int slashIndex = XMPPAddress.indexOf("/");
        if (slashIndex + 1 > XMPPAddress.length() || slashIndex < 0) {
            return "";
        }
        else {
            return XMPPAddress.substring(slashIndex + 1);
        }
    }

    /**
     * Returns the XMPP address with any resource information removed. For example,
     * for the address "matt@jivesoftware.com/Smack", "matt@jivesoftware.com" would
     * be returned.
     *
     * @param XMPPAddress the XMPP address.
     * @return the bare XMPP address without resource information.
     */
    public static String parseBareAddress(String XMPPAddress) {
        if (XMPPAddress == null) {
            return null;
        }
        int slashIndex = XMPPAddress.indexOf("/");
        if (slashIndex < 0) {
            return XMPPAddress;
        }
        else if (slashIndex == 0) {
            return "";
        }
        else {
            return XMPPAddress.substring(0, slashIndex);
        }
    }

    /**
     * Escapes the node portion of a JID according to "JID Escaping" (JEP-0106).
     * Escaping replaces characters prohibited by node-prep with escape sequences,
     * as follows:<p>
     *
     * <table border="1">
     * <tr><td><b>Unescaped Character</b></td><td><b>Encoded Sequence</b></td></tr>
     * <tr><td>&lt;space&gt;</td><td>\20</td></tr>
     * <tr><td>"</td><td>\22</td></tr>
     * <tr><td>&</td><td>\26</td></tr>
     * <tr><td>'</td><td>\27</td></tr>
     * <tr><td>/</td><td>\2f</td></tr>
     * <tr><td>:</td><td>\3a</td></tr>
     * <tr><td>&lt;</td><td>\3c</td></tr>
     * <tr><td>&gt;</td><td>\3e</td></tr>
     * <tr><td>@</td><td>\40</td></tr>
     * <tr><td>\</td><td>\5c</td></tr>
     * </table><p>
     *
     * This process is useful when the node comes from an external source that doesn't
     * conform to nodeprep. For example, a username in LDAP may be "Joe Smith". Because
     * the &lt;space&gt; character isn't a valid part of a node, the username should
     * be escaped to "Joe\20Smith" before being made into a JID (e.g. "joe\20smith@example.com"
     * after case-folding, etc. has been applied).<p>
     *
     * All node escaping and un-escaping must be performed manually at the appropriate
     * time; the JID class will not escape or un-escape automatically.
     *
     * @param node the node.
     * @return the escaped version of the node.
     */
    public static String escapeNode(String node) {
        if (node == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder(node.length() + 8);
        for (int i=0, n=node.length(); i<n; i++) {
            char c = node.charAt(i);
            switch (c) {
                case '"': buf.append("\\22"); break;
                case '&': buf.append("\\26"); break;
                case '\'': buf.append("\\27"); break;
                case '/': buf.append("\\2f"); break;
                case ':': buf.append("\\3a"); break;
                case '<': buf.append("\\3c"); break;
                case '>': buf.append("\\3e"); break;
                case '@': buf.append("\\40"); break;
                case '\\': buf.append("\\5c"); break;
                default: {
                    if (Character.isWhitespace(c)) {
                        buf.append("\\20");
                    }
                    else {
                        buf.append(c);
                    }
                }
            }
        }
        return buf.toString();
    }

    /**
     * Un-escapes the node portion of a JID according to "JID Escaping" (JEP-0106).<p>
     * Escaping replaces characters prohibited by node-prep with escape sequences,
     * as follows:<p>
     *
     * <table border="1">
     * <tr><td><b>Unescaped Character</b></td><td><b>Encoded Sequence</b></td></tr>
     * <tr><td>&lt;space&gt;</td><td>\20</td></tr>
     * <tr><td>"</td><td>\22</td></tr>
     * <tr><td>&</td><td>\26</td></tr>
     * <tr><td>'</td><td>\27</td></tr>
     * <tr><td>/</td><td>\2f</td></tr>
     * <tr><td>:</td><td>\3a</td></tr>
     * <tr><td>&lt;</td><td>\3c</td></tr>
     * <tr><td>&gt;</td><td>\3e</td></tr>
     * <tr><td>@</td><td>\40</td></tr>
     * <tr><td>\</td><td>\5c</td></tr>
     * </table><p>
     *
     * This process is useful when the node comes from an external source that doesn't
     * conform to nodeprep. For example, a username in LDAP may be "Joe Smith". Because
     * the &lt;space&gt; character isn't a valid part of a node, the username should
     * be escaped to "Joe\20Smith" before being made into a JID (e.g. "joe\20smith@example.com"
     * after case-folding, etc. has been applied).<p>
     *
     * All node escaping and un-escaping must be performed manually at the appropriate
     * time; the JID class will not escape or un-escape automatically.
     *
     * @param node the escaped version of the node.
     * @return the un-escaped version of the node.
     */
    public static String unescapeNode(String node) {
        if (node == null) {
            return null;
        }
        char [] nodeChars = node.toCharArray();
        StringBuilder buf = new StringBuilder(nodeChars.length);
        for (int i=0, n=nodeChars.length; i<n; i++) {
            compare: {
                char c = node.charAt(i);
                if (c == '\\' && i+2<n) {
                    char c2 = nodeChars[i+1];
                    char c3 = nodeChars[i+2];
                    if (c2 == '2') {
                        switch (c3) {
                            case '0': buf.append(' '); i+=2; break compare;
                            case '2': buf.append('"'); i+=2; break compare;
                            case '6': buf.append('&'); i+=2; break compare;
                            case '7': buf.append('\''); i+=2; break compare;
                            case 'f': buf.append('/'); i+=2; break compare;
                        }
                    }
                    else if (c2 == '3') {
                        switch (c3) {
                            case 'a': buf.append(':'); i+=2; break compare;
                            case 'c': buf.append('<'); i+=2; break compare;
                            case 'e': buf.append('>'); i+=2; break compare;
                        }
                    }
                    else if (c2 == '4') {
                        if (c3 == '0') {
                            buf.append("@");
                            i+=2;
                            break compare;
                        }
                    }
                    else if (c2 == '5') {
                        if (c3 == 'c') {
                            buf.append("\\");
                            i+=2;
                            break compare;
                        }
                    }
                }
                buf.append(c);
            }
        }
        return buf.toString();
    }

    /**
     * Escapes all necessary characters in the String so that it can be used
     * in an XML doc.
     *
     * @param string the string to escape.
     * @return the string with appropriate characters escaped.
     */
    public static String escapeForXML(String string) {
        if (string == null) {
            return null;
        }
        char ch;
        int i=0;
        int last=0;
        char[] input = string.toCharArray();
        int len = input.length;
        StringBuilder out = new StringBuilder((int)(len*1.3));
        for (; i < len; i++) {
            ch = input[i];
            if (ch > '>') {
            }
            else if (ch == '<') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(LT_ENCODE);
            }
            else if (ch == '>') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(GT_ENCODE);
            }

            else if (ch == '&') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                // Do nothing if the string is of the form &#235; (unicode value)
                if (!(len > i + 5
                    && input[i + 1] == '#'
                    && Character.isDigit(input[i + 2])
                    && Character.isDigit(input[i + 3])
                    && Character.isDigit(input[i + 4])
                    && input[i + 5] == ';')) {
                        last = i + 1;
                        out.append(AMP_ENCODE);
                    }
            }
            else if (ch == '"') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(QUOTE_ENCODE);
            }
            else if (ch == '\'') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(APOS_ENCODE);
            }
        }
        if (last == 0) {
            return string;
        }
        if (i > last) {
            out.append(input, last, i - last);
        }
        return out.toString();
    }

    /**
     * Used by the hash method.
     */
    private static MessageDigest digest = null;

    /**
     * Hashes a String using the SHA-1 algorithm and returns the result as a
     * String of hexadecimal numbers. This method is synchronized to avoid
     * excessive MessageDigest object creation. If calling this method becomes
     * a bottleneck in your code, you may wish to maintain a pool of
     * MessageDigest objects instead of using this method.
     * <p>
     * A hash is a one-way function -- that is, given an
     * input, an output is easily computed. However, given the output, the
     * input is almost impossible to compute. This is useful for passwords
     * since we can store the hash and a hacker will then have a very hard time
     * determining the original password.
     *
     * @param data the String to compute the hash of.
     * @return a hashed version of the passed-in String
     */
    public synchronized static String hash(String data) {
        if (digest == null) {
            try {
                digest = MessageDigest.getInstance("SHA-1");
            }
            catch (NoSuchAlgorithmException nsae) {
                System.err.println("Failed to load the SHA-1 MessageDigest. " +
                "Jive will be unable to function normally.");
            }
        }
        // Now, compute hash.
        try {
            digest.update(data.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            System.err.println(e);
        }
        return encodeHex(digest.digest());
    }

    /**
     * Encodes an array of bytes as String representation of hexadecimal.
     *
     * @param bytes an array of bytes to convert to a hex string.
     * @return generated hex string.
     */
    public static String encodeHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);

        for (byte aByte : bytes) {
            if (((int) aByte & 0xff) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toString((int) aByte & 0xff, 16));
        }

        return hex.toString();
    }

    /**
     * Pseudo-random number generator object for use with randomString().
     * The Random class is not considered to be cryptographically secure, so
     * only use these random Strings for low to medium security applications.
     */
    private static Random randGen = new Random();

    /**
     * Array of numbers and letters of mixed case. Numbers appear in the list
     * twice so that there is a more equal chance that a number will be picked.
     * We can use the array to get a random number or letter by picking a random
     * array index.
     */
    private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" +
                    "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

    /**
     * Returns a random String of numbers and letters (lower and upper case)
     * of the specified length. The method uses the Random class that is
     * built-in to Java which is suitable for low to medium grade security uses.
     * This means that the output is only pseudo random, i.e., each number is
     * mathematically generated so is not truly random.<p>
     *
     * The specified length must be at least one. If not, the method will return
     * null.
     *
     * @param length the desired length of the random String to return.
     * @return a random String of numbers and letters of the specified length.
     */
    public static String randomString(int length) {
        if (length < 1) {
            return null;
        }
        // Create a char buffer to put random letters and numbers in.
        char [] randBuffer = new char[length];
        for (int i=0; i<randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
        }
        return new String(randBuffer);
    }
    
    public static String timeLong2Date(long datelong) {
    	String curTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(datelong);
    	return curTime;
    }
    
    /** 
     * 拆分以逗号分隔的字符串,并存入String数组中 
     * @param sSource 源字符串 
     * @return String[] 
     */ 
    public static String[] strToArray(String sSource) { 
        String aReturn[] = null; 

        StringTokenizer st = null; 
        st = new StringTokenizer(sSource, ","); 
        aReturn = new String[st.countTokens()]; 
        int i = 0; 
        while (st.hasMoreTokens()) { 
            aReturn[i] = st.nextToken(); 
            i++; 
        } 

        return aReturn; 
    }
    
    /** 
     * 将数组中的元素连成一个以逗号分隔的字符串 
     * @param aSource 源数组 
     * @return 字符串 
     */ 
    public static String arrayToString(String[] aSource) { 
        String sReturn = ""; 

        for (int i = 0; i < aSource.length; i++) { 
            if (i > 0) { 
                sReturn += ","; 
            } 
            sReturn += aSource[i]; 
        } 

        return sReturn; 
    }
    
    /** 
     * 将两个字符串的所有元素连结为一个字符串数组 
     * @param source 源字符串数组1 
     * @param item 源字符串数组2 
     * @return String[] 
     */ 
    public static String[] arrayAppend(String[] source, String item) { 
        int iLen = 0; 
        int delta = 1;
        String aReturn[] = null; 

        if (source == null) { 
        	source = new String[0]; 
        } 
        if (item == null) { 
        	delta = 0; 
        } 
        iLen = source.length; 
        aReturn = new String[iLen + delta]; 

        /** 
         * 将第一个字符串数组的元素加到结果数组中 
         */ 
        for (int i = 0; i < iLen; i++) { 
            aReturn[i] = source[i]; 
        }
        
        if (delta == 1) {
        	aReturn[iLen] = item; 
        }

        return aReturn; 
    } 

    /** 
     * 查找源字符串数组中是否包含给定字符串 
     * @param aSource :源字符串数组 
     * @param sItem :子串 
     * @return 是否包含 
     */ 
    public static boolean isContain(String[] aSource, String sItem) { 
        boolean isReturn = false; 

        for (int i = 0; i < aSource.length; i++) { 
            if (sItem.equals(aSource[i])) { 
                isReturn = true; 
                break; 
            } 
        } 
        return isReturn; 
    }
    
    public static String getLocalIpAddress() {
    	try {
    		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
    			NetworkInterface intf = en.nextElement();
    			for (Enumeration<InetAddress>  enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
    				InetAddress inetAddress = enumIpAddr.nextElement();
    				if (!inetAddress.isLoopbackAddress()) {
    					return inetAddress.getHostAddress().toString();
    				}
    			}
    		}
    	} catch (SocketException ex) {
    		ex.printStackTrace();
    	}
    	
    	return null;
    }

}