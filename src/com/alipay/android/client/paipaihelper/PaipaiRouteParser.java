package com.alipay.android.client.paipaihelper;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

import com.alipay.android.client.RootActivity;
import com.alipay.android.core.expapp.Page;

public class PaipaiRouteParser {

	public static final String METHOD = "method";
	public static final String URI = "uri";

	private RootActivity activity;
	private Page page;
	private String forwardOtherOperation;
	private String memo;
	private String routeInfoList;

	private PaipaiRouteSchemeHandle paipaiRouteSchemeHandle;

	public PaipaiRouteParser(RootActivity activity, Page page,
			String forwardOtherOperation, String memo, String routeInfoList) {
		this.activity = activity;
		this.page = page;
		this.forwardOtherOperation = forwardOtherOperation;
		this.memo = memo;
		this.routeInfoList = routeInfoList;
		paipaiRouteSchemeHandle = new PaipaiRouteSchemeHandle();
	}

	private ArrayList<RouteInfoItem> routeList = new ArrayList<RouteInfoItem>();

	public void routing() {
		PRParser();
		doPR();
	}

	private void PRParser() {
		JSONArray jsonArrayData = null;
		try {
			jsonArrayData = new JSONArray(routeInfoList);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JSONObject tempObj = null;
		if (jsonArrayData != null) {
			for (int i = 0; i < jsonArrayData.length(); i++) {
				try {
					tempObj = jsonArrayData.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (tempObj != null) {
					try {
						routeList.add(new RouteInfoItem(tempObj
								.getString(METHOD), tempObj.getString(URI)));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void doPR() {

		for (int i = 0; i < routeList.size(); i++) {
			if (isItemCanRoute(routeList.get(i))) {
				return;
			}
		}
	}

	private boolean isItemCanRoute(RouteInfoItem item) {
		boolean ret = false;
		// TODO
		if (item == null) {
			return false;
		}

		if (item.method.equals("native")) {
			ret = nativeHandle(item.routeUri);
		} else if (item.method.equals("webview")) {
			webViewHandle(item.routeUri);
		}

		return ret;
	}

	private boolean nativeHandle(String uriStr) {
		Uri uri = Uri.parse(uriStr);
		return paipaiRouteSchemeHandle.paipaiRouteNativeExecute(activity, page,
				uri);
	}

	// routeurl
	// {
	// "method" : "webview",
	// "uri"
	// :"http://a.waptest.taobao.com/i1212.htm?sid=57abf2a9207ebd93cf6b6e5de45df657&ttid=600351@AlipayPaiPai_barcodeprod_1.0.0"
	// }
	private boolean webViewHandle(String uriStr) {
		Uri uri = Uri.parse(uriStr);
		return paipaiRouteSchemeHandle.paipaiRouteWebViewExecute(activity,
				page, uri);
	}

	class RouteInfoItem {

		public RouteInfoItem(String method, String routeUri) {
			this.method = method;
			this.routeUri = routeUri;
		}

		public String method;
		public String routeUri;
	}

}
