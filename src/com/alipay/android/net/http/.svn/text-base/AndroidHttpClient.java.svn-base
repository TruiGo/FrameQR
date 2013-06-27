/*
 * Copyright (C) 2008
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alipay.android.net.http;

import java.io.IOException;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;

import com.alipay.android.client.constant.Constant;

/**
 * <p>
 * Subclass of the Apache {@link DefaultHttpClient} that is configured with
 * reasonable default settings and registered schemes for Android, and also lets
 * the user add {@link HttpRequestInterceptor} classes. Don't create this
 * directly, use the {@link #newInstance} factory method.
 * </p>
 * <p/>
 * <p>
 * This client processes cookies but does not retain them by default. To retain
 * cookies, simply add a cookie store to the HttpContext:
 * 
 * <pre>
 * context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
 * </pre>
 * 
 * </p>
 */
public final class AndroidHttpClient implements HttpClient
{
	/**
	 * Create a new HttpClient with reasonable defaults (which you can update).
	 * 
	 * @param userAgent
	 *            to report in your HTTP requests.
	 * @return AndroidHttpClient for you to use for all your requests.
	 */
	public static AndroidHttpClient newInstance(Context context,String userAgent)
	{
		HttpParams params = new BasicHttpParams();

		// Turn off stale checking. Our connections break all the time anyway,
		// and it's not worth it to pay the penalty of checking every time.
		HttpConnectionParams.setStaleCheckingEnabled(params, true);

		// Default connection and socket timeout of 20 seconds. Tweak to taste.
		HttpConnectionParams.setConnectionTimeout(params, 100 * 1000);
		HttpConnectionParams.setSoTimeout(params, 45 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 2*8192);

		// Don't handle redirects -- return them to the caller. Our code
		// often wants to re-POST after a redirect, which we must do ourselves.
		HttpClientParams.setRedirecting(params, false);
		HttpClientParams.setAuthenticating(params, false);
		
		// Set the specified user agent and register standard protocols.
		if (userAgent != null) {
            HttpProtocolParams.setUserAgent(params, userAgent);
        }
		
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		
		if(!Constant.isDebug(context)){
			SSLSocketFactory sslsf = SSLSocketFactory.getSocketFactory();
			HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			sslsf.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
			schemeRegistry.register(new Scheme("https", sslsf, 443));
		}else{
			try {
				KeyStore trustKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
				trustKeyStore.load(null, null);
				EasySSLSocketFactory sslsf = new EasySSLSocketFactory(trustKeyStore);
				sslsf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				schemeRegistry.register(new Scheme("https", sslsf, 443));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
        ConnPerRoute connPerRoute = new ConnPerRouteBean(10);
        ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
        ConnManagerParams.setMaxTotalConnections(params, 50);
		ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
		
		// We use a factory method to modify superclass initialization
		// parameters without the funny call-a-static-method dance.
		return new AndroidHttpClient(manager, params);
	}

	private final DefaultHttpClient delegate;

	private AndroidHttpClient(ClientConnectionManager ccm, HttpParams params) {
		this.delegate = new DelegateHttpClient(ccm, params);
	}

	/**
	 * Release resources associated with this client. You must call this, or
	 * significant resources (sockets and memory) may be leaked.
	 */
	public void close()
	{
		getConnectionManager().shutdown();
	}

	public HttpParams getParams()
	{
		return delegate.getParams();
	}

	public ClientConnectionManager getConnectionManager()
	{
		return delegate.getConnectionManager();
	}

	public HttpResponse execute(HttpUriRequest request) throws IOException
	{
		return delegate.execute(request);
	}

	public HttpResponse execute(HttpUriRequest request, HttpContext context)
			throws IOException
	{
		return delegate.execute(request, context);
	}

	public HttpResponse execute(HttpHost target, HttpRequest request)
			throws IOException
	{
		HttpResponse httpResponse = delegate.execute(target, request);
	    return httpResponse;
	}

	public HttpResponse execute(HttpHost target, HttpRequest request,
			HttpContext context) throws IOException
	{
		return delegate.execute(target, request, context);
	}

	public <T> T execute(HttpUriRequest request,
			ResponseHandler<? extends T> responseHandler) throws IOException
	{
		return delegate.execute(request, responseHandler);
	}

	public <T> T execute(HttpUriRequest request,
			ResponseHandler<? extends T> responseHandler, HttpContext context)
			throws IOException
	{
		return delegate.execute(request, responseHandler, context);
	}

	public <T> T execute(HttpHost target, HttpRequest request,
			ResponseHandler<? extends T> responseHandler) throws IOException
	{
		return delegate.execute(target, request, responseHandler);
	}

	public <T> T execute(HttpHost target, HttpRequest request,
			ResponseHandler<? extends T> responseHandler, HttpContext context)
			throws IOException
	{
		return delegate.execute(target, request, responseHandler, context);
	}

	private static class DelegateHttpClient extends DefaultHttpClient
	{

		private DelegateHttpClient(ClientConnectionManager ccm,
				HttpParams params) {
			super(ccm, params);
		}

		@Override protected HttpContext createHttpContext()
		{
			// Same as DefaultHttpClient.createHttpContext() minus the
			// cookie store.
			HttpContext context = new BasicHttpContext();
			context.setAttribute(ClientContext.AUTHSCHEME_REGISTRY,
					getAuthSchemes());
			context.setAttribute(ClientContext.COOKIESPEC_REGISTRY,
					getCookieSpecs());
			context.setAttribute(ClientContext.CREDS_PROVIDER,
					getCredentialsProvider());
			return context;
		}
	}
}