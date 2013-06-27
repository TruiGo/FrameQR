package com.alipay.android.core.webapp;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.http.HttpHost;

import android.content.Context;
import android.os.Build;
import android.util.Log;

public class ProxyUtil {
    public static boolean setProxy(Context ctx, String host, int port) throws Exception {
        setSystemProperties(host, port);

        boolean worked = false;

        if (Build.VERSION.SDK_INT < 14) {
            worked = setWebkitProxyGingerbread(ctx, host, port);
        } else {
            worked = setWebkitProxyICS(ctx, host, port);
        }

        return worked;
    }

    private static void setSystemProperties(String host, int port) {
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", port + "");
    }

    /**
    * Override WebKit Proxy settings
    *
    * @param ctx Android ApplicationContext
    * @param host
    * @param port
    * @return  true if Proxy was successfully set
    */
    private static boolean setWebkitProxyGingerbread(Context ctx, String host, int port)
                                                                                        throws Exception {
        Object requestQueueObject = getRequestQueue(ctx);
        if (requestQueueObject != null) {
            //Create Proxy config object and set it into request Q
            HttpHost httpHost =null;
            if(host!=null&&host.length()>0&&port>0)
                httpHost = new HttpHost(host, port, "http");
            setDeclaredField(requestQueueObject, "mProxyHost", httpHost);
            return true;
        }
        return false;

    }

    private static boolean setWebkitProxyICS(Context ctx, String host, int port) throws Exception {

        // PSIPHON: added support for Android 4.x WebView proxy
        try {
            Class<?> webViewCoreClass = Class.forName("android.webkit.WebViewCore");

            Class<?> proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
            if (webViewCoreClass != null && proxyPropertiesClass != null) {
                Method m = webViewCoreClass.getDeclaredMethod("sendStaticMessage", Integer.TYPE,
                    Object.class);
                Constructor<?> c = proxyPropertiesClass.getConstructor(String.class, Integer.TYPE,
                    String.class);

                if (m != null && c != null) {
                    m.setAccessible(true);
                    c.setAccessible(true);
                    Object properties = c.newInstance(host, port, null);

                    // android.webkit.WebViewCore.EventHub.PROXY_CHANGED = 193;
                    m.invoke(null, 193, properties);
                    return true;
                } else
                    return false;
            }
        } catch (Exception e) {
            Log.e(
                "ProxySettings",
                "Exception setting WebKit proxy through android.net.ProxyProperties: "
                        + e.toString());
        } catch (Error e) {
            Log.e("ProxySettings",
                "Exception setting WebKit proxy through android.webkit.Network: " + e.toString());
        }

        return false;

    }

    public static void resetProxy(Context ctx) throws Exception {
        Object requestQueueObject = getRequestQueue(ctx);
        if (requestQueueObject != null) {
            setDeclaredField(requestQueueObject, "mProxyHost", null);
        }
    }

    public static Object getRequestQueue(Context ctx) throws Exception {
        Object ret = null;
        Class<?> networkClass = Class.forName("android.webkit.Network");
        if (networkClass != null) {
            Object networkObj = invokeMethod(networkClass, "getInstance", new Object[] { ctx },
                Context.class);
            if (networkObj != null) {
                ret = getDeclaredField(networkObj, "mRequestQueue");
            }
        }
        return ret;
    }

    public static Object getDeclaredField(Object obj, String name) throws SecurityException,
                                                                   NoSuchFieldException,
                                                                   IllegalArgumentException,
                                                                   IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }

    public static void setDeclaredField(Object obj, String name, Object value)
                                                                               throws SecurityException,
                                                                               NoSuchFieldException,
                                                                               IllegalArgumentException,
                                                                               IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(obj, value);
    }

    @SuppressWarnings("rawtypes")
    private static Object invokeMethod(Object object, String methodName, Object[] params,
                                       Class... types) throws Exception {
        Object out = null;
        Class c = object instanceof Class ? (Class) object : object.getClass();
        if (types != null) {
            Method method = c.getMethod(methodName, types);
            out = method.invoke(object, params);
        } else {
            Method method = c.getMethod(methodName);
            out = method.invoke(object);
        }
        return out;
    }

}
