package com.alipay.android.net.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.common.Defines;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.exception.SystemExceptionHandler;
import com.alipay.android.log.Constants;
import com.alipay.android.util.BaseHelper;
import com.alipay.android.util.LogUtil;

public class HttpClient {
	static final String TAG = "HttpClient";
	
    public int errorType = 0;
    public static int SSL_ERROR = 1;
    public static int IO_ERROR = 0;
    
	private final static String CHARSET = "UTF-8";
	private String mUrl;

	Context mContext;

	public HttpClient(Context context) {
		mUrl = null;
		this.mContext = context;
		setDefaultHostnameVerifier();
	}

	public HttpClient(String url, Context context) {
		this.mUrl = url;
		this.mContext = context;
		setDefaultHostnameVerifier();
	}

	public void setUrl(String url) {
		this.mUrl = url;
	}

	public String getUrl() {
		return mUrl;
	}

	public URL getURL() {
		URL url = null;

		try {
			url = new URL(this.mUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return url;
	}

	private void setDefaultHostnameVerifier() {
		//
		HostnameVerifier hv = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}

	public void outputRequestInfo(String strReqData) {
		URL url = getURL();

		LogUtil.logOnlyDebuggable(TAG, "Request" + strReqData);
		LogUtil.logOnlyDebuggable(TAG, "Dest url:  " + url.toString());
	}

	public HttpResponse sendSynchronousRequestAsHttpResponse(String strReqData,
			ArrayList<BasicHeader> headers) {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("requestData", strReqData));
		outputRequestInfo(strReqData);

		headers = prepareCommonHeaders(headers);
		return sendSynchronousRequestAsHttpResponse(pairs, headers);
	}

	public RespData sendSynchronousGetRequest(
			ArrayList<BasicHeader> headers) {
		RespData respData = null;
		try {
			HttpResponse httpResponse = sendSynchronousRequestAsHttpResponse((ArrayList<BasicNameValuePair>)null, headers);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = httpResponse.getEntity();
				String strResponse = EntityUtils.toString(entity);
				LogUtil.logOnlyDebuggable(TAG, "Response " + strResponse);

				String contentType = entity.getContentType().getValue();
				String charset = BaseHelper.getCharset(contentType);
				contentType = BaseHelper.getContentType(contentType);
				respData = new RespData(strResponse, contentType, charset);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return respData;
	}

	private ArrayList<BasicHeader> prepareCommonHeaders(ArrayList<BasicHeader> headers){
		if(headers == null){
			headers = new ArrayList<BasicHeader>();
		}
		headers.add(new BasicHeader("Accept-Encoding", "gzip"));
		return headers;
	}

	public static AndroidHttpClient sAndroidHttpClient = null;
	
	public HttpResponse sendSynchronousRequestAsHttpResponse(
			ArrayList<BasicNameValuePair> pairs, ArrayList<BasicHeader> headers) {
		HttpResponse httpResponse = null;

		URL url = getURL();
		UrlEncodedFormEntity p_entity = null;
		long start = System.currentTimeMillis();

		HttpRequest httpRequest = null;
		HttpHost target = null;
		try {
			if (sAndroidHttpClient == null)
				sAndroidHttpClient = AndroidHttpClient.newInstance(mContext,
						"alipay");

			// configure the proxy.
			HttpParams httpParams = sAndroidHttpClient.getParams();
			// 设置网络超时参数
//			HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);
//			HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);

			HttpHost proxy = getProxy();
			httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			String protocol = url.getProtocol();
			int port = 80;
			if (protocol.equalsIgnoreCase("https"))
				port = 443;

			target = new HttpHost(url.getHost(), port, protocol);

			if (pairs != null) {
				httpRequest = new HttpPost(mUrl);
				p_entity = new UrlEncodedFormEntity(pairs, "utf-8");
				((HttpPost) httpRequest).setEntity(p_entity);
			} else {
				httpRequest = new HttpGet(mUrl);
			}
			httpRequest.addHeader("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
			httpRequest.addHeader("Accept", "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
			httpRequest.addHeader("Connection", "Keep-Alive");

			if (headers != null) {
				for (Header header : headers)
					httpRequest.addHeader(header);
			}

			// Execute the request.
			httpResponse = sAndroidHttpClient.execute(target, httpRequest);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}catch(SSLException e){
		    errorType = SSL_ERROR;
            SystemExceptionHandler.getInstance().saveConnInfoToFile(e,
                    Constants.MONITORPOINT_CONNECTERR);
            e.printStackTrace();
		} catch (Exception e) {
            errorType = IO_ERROR;
			try {
				// Execute the request again.
				if (e instanceof NullPointerException)
					httpResponse = sAndroidHttpClient.execute(target,
							httpRequest);
				else
					throw e;
			} catch (final Exception e1) {
				// network Exception to be log
				SystemExceptionHandler.getInstance().saveConnInfoToFile(e1,
						Constants.MONITORPOINT_CONNECTERR);
				e1.printStackTrace();
			}
		} finally {
			// logout the cost.
			long lend = System.currentTimeMillis();
			LogUtil.logOnlyDebuggable("start at: ", String.valueOf(start)
					+ "(ms)");
			LogUtil.logOnlyDebuggable("finished at: ", String.valueOf(lend)
					+ "(ms)");

			float lEalpse = (float) ((lend - start) / 1000.0);
			String Ealpse = String.valueOf(lEalpse);
			LogUtil.logOnlyDebuggable(" cost : ", Ealpse + "(s)");
		}

		return httpResponse;
	}

	public HttpResponse sendGZipSynchronousRequest(String strReqData) {
		HttpResponse httpResponse = null;

		HttpRequest httpRequest = null;
		HttpHost target = null;

		long start = System.currentTimeMillis();
		try {
			URL url = getURL();
			if (sAndroidHttpClient == null)
				sAndroidHttpClient = AndroidHttpClient.newInstance(mContext,
						"alipay");

			// configure the proxy.
			HttpParams httpParams = sAndroidHttpClient.getParams();
			HttpHost proxy = getProxy();
			httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			String protocol = url.getProtocol();
			int port = 80;
			if (protocol.equalsIgnoreCase("https"))
				port = 443;

			target = new HttpHost(url.getHost(), port, protocol);

			if (strReqData != null) {
				httpRequest = new HttpPost(Constant.getStatisticsUrl(mContext));
				byte[] zipData = GZipData(strReqData);

				ByteArrayEntity zipEntity = new ByteArrayEntity(zipData);
				((HttpPost) httpRequest).setEntity(zipEntity);
			} else {
				httpRequest = new HttpGet(Constant.getStatisticsUrl(mContext));
			}
			httpRequest.addHeader("Content-type", "text/xml");

			// Execute the request.
			httpResponse = sAndroidHttpClient.execute(target, httpRequest);
		} catch(SSLException e){
            errorType = SSL_ERROR;
            SystemExceptionHandler.getInstance().saveConnInfoToFile(e,
                    Constants.MONITORPOINT_CONNECTERR);
            e.printStackTrace();
        } catch (Exception e) {
            errorType = IO_ERROR;
			try {
				// Execute the request again.
				if (e instanceof NullPointerException)
					httpResponse = sAndroidHttpClient.execute(target,
							httpRequest);
				else
					throw e;
			} catch (Exception e1) {
				e1.printStackTrace();

			}
		} finally {
			// logout the cost.
			long lend = System.currentTimeMillis();
			Log.i(TAG, "start at: " + String.valueOf(start) + "(ms)");
			Log.i(TAG, "finished at: " + String.valueOf(lend) + "(ms)");

			float lEalpse = (float) ((lend - start) / 1000.0);
			String Ealpse = String.valueOf(lEalpse);
			Log.i(TAG, " cost : " + Ealpse + "(s)");
		}

		return httpResponse;

	}

	public HttpHost getProxy() {
		HttpHost proxy = null;
		NetworkInfo ni = this.getActiveNetworkInfo();
		if (ni != null && ni.isAvailable()
				&& ni.getType() == ConnectivityManager.TYPE_MOBILE) {
			String proxyHost = android.net.Proxy.getDefaultHost();
			int port = android.net.Proxy.getDefaultPort();
			if (proxyHost != null)
				proxy = new HttpHost(proxyHost, port);
		}

		return proxy;
	}

	public NetworkInfo getActiveNetworkInfo() {
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			return connectivityManager.getActiveNetworkInfo();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean urlDownloadToFile(String strUrl, String path,
			boolean append, DownloadListener downloadListener, Context context) {
		boolean bRet = false;

		HttpClient httpClient = null;
		InputStream inputStream = null;
		try {
			httpClient = new HttpClient(strUrl, context);

			ArrayList<BasicHeader> headers = null;
			File file = new File(path);

			// Check to see if any new modified since last time we requested.
			Date date = new Date(file.lastModified());
			String strlastModified = Helper.dayFromInt(date.getDay()) + ", "
					+ date.toGMTString();
			if (httpClient.isModifiedSince(strUrl, strlastModified, context)) {
				file.delete();
				file.createNewFile();
			}

			FileOutputStream fos = null;
			fos = new FileOutputStream(file, append);
			long length = file.length();
			if (append && length > 0) {
				// Use for resume broken downloads.
				headers = new ArrayList<BasicHeader>();
				headers.add(new BasicHeader("Range", "bytes=" + length + "-"));
			}

			ArrayList<BasicNameValuePair> pairs = null;
			HttpResponse httpResponse = httpClient.sendSynchronousRequestAsHttpResponse(pairs, headers);

			if (httpResponse != null)
				LogUtil.logAnyTime("NetWork", "response:"
						+ httpResponse.getStatusLine().getStatusCode());
			if (httpResponse != null
					&& httpResponse.getStatusLine().getStatusCode() < HttpStatus.SC_BAD_REQUEST) {
				HttpEntity entity = httpResponse.getEntity();

				inputStream = entity.getContent();

				long contentLength = entity.getContentLength();
				int bytesPerDownload = (int) (contentLength * 0.01);
				if (bytesPerDownload < 1024)
					bytesPerDownload = 1024;

				byte buf[] = new byte[bytesPerDownload];
				int iCurRead = 0;
				float iAllHaveRead = 0.0f + length;
				while ((iCurRead = inputStream.read(buf)) > 0) {
					// try {
					// Thread.sleep(500);
					// } catch (InterruptedException e) {
					// e.printStackTrace();
					// }

					fos.write(buf, 0, iCurRead);
					iAllHaveRead += iCurRead;

					if (downloadListener != null) {
						downloadListener.updatePercent(iAllHaveRead
								/ (contentLength + length));
						if (downloadListener.getStatus() == Defines.PAUSED) {
							break;
						}
					}
				}

				fos.close();

				if (append) {
					// try{
					Header header = httpResponse
							.getFirstHeader("Last-Modified");
					if (header != null) {
						long lastModified = Date.parse(header.getValue());
						file.setLastModified(lastModified);
					}
					// }catch(Exception e){
					// e.printStackTrace();
					// }
				}

				if (downloadListener == null
						|| downloadListener.getStatus() != Defines.PAUSED) {
					bRet = true;
				}
			} else {
				if (downloadListener != null
						&& downloadListener.getStatus() != Defines.PAUSED) {
					downloadListener.updatePercent(-1);
				}
			}
		} catch (IOException e) {
			if (downloadListener != null
					&& downloadListener.getStatus() != Defines.PAUSED) {
				downloadListener.updatePercent(-1);
			}
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return bRet;
	}

	private boolean isModifiedSince(String strUrl, String lastModified,
			Context context) {
		ArrayList<BasicHeader> headers = new ArrayList<BasicHeader>();
		headers.add(new BasicHeader("If-Modified-Since", lastModified));
		ArrayList<BasicNameValuePair> pairs = null;
		HttpResponse httpResponse = this.sendSynchronousRequestAsHttpResponse(
				pairs, headers);
		if (httpResponse != null)
			return httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_MODIFIED ? false
					: true;
		return true;
	}

	public InputStream inputStreamFromUrl() {
		InputStream inputStream = null;

		try {
			ArrayList<BasicNameValuePair> pairs = null;
			HttpResponse httpResponse = sendSynchronousRequestAsHttpResponse(pairs, null);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = httpResponse.getEntity();
				inputStream = entity.getContent();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return inputStream;
	}

	/*
	 * Gzip压缩
	 */
	@SuppressWarnings("finally")
	public static byte[] GZipData(String inData) {
		byte[] result = null;

		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		GZIPOutputStream out = null;
		try {

			out = new GZIPOutputStream(bytesOut);

			byte[] postbyte = inData.getBytes(CHARSET);
			out.write(postbyte, 0, postbyte.length);
			out.finish();
			out.flush();
			result = bytesOut.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
				bytesOut.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}
	}

	public String getStrFromResponse(HttpEntity httpEntity) {
	    InputStream rawIs = getInputStreamFromRespone(httpEntity);
	    String response = null;
        if (rawIs != null)
            response = BaseHelper.convertStreamToString(rawIs);
	    return response;
	}

	private InputStream getInputStreamFromRespone(HttpEntity httpEntity) {
		InputStream is = null;
		try {
			is = httpEntity.getContent();
			if (httpEntity.getContentEncoding() != null
					&& httpEntity.getContentEncoding().getValue() != null
					&& httpEntity.getContentEncoding().getValue()
							.contains("gzip")) {
			    is = new GZIPInputStream(is);
				Log.i(TAG + "getInputStreamFromRespone", "isGZIP:" + "true");
			}else{
				Log.i(TAG + "getInputStreamFromRespone", "isGZIP:" + "false");
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return is;
	}

}