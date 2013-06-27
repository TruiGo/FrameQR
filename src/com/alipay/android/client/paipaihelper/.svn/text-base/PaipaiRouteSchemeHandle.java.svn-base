package com.alipay.android.client.paipaihelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.content.Intent;
import android.net.Uri;

import com.alipay.android.client.RootActivity;
import com.alipay.android.client.SchemeHandler;
import com.alipay.android.core.expapp.ExpAppRuntime;
import com.alipay.android.core.expapp.Page;

public class PaipaiRouteSchemeHandle {

	private RootActivity activity;
	private Page page;
	private Uri uri;
	private ExpAppRuntime engine;

	private String host;
	private Set<String> mParamNames;

	private String saId;
	private String path;
	private String routeBizType;

	private static HashMap<String, String> paipaiExpMap;
	// forwardOtherOperation 以及对应的表达式push界面表达式
	static {
		paipaiExpMap = new HashMap<String, String>();
		paipaiExpMap.put("0", "route0");// 捐赠
		paipaiExpMap.put("1", "route1");// 跳到我要付款
		paipaiExpMap.put("2", "route2");// 弹出网址对话框
		paipaiExpMap.put("3", "route3");// 调起安全支付
		paipaiExpMap.put("4", "route4");// 正常订购
		paipaiExpMap.put("5", "route5");// wap界面
		paipaiExpMap.put("6", "route6");// 线下业务
		paipaiExpMap.put("7", "route7");// 外部webView
		paipaiExpMap.put("8", "route8");// 个人码路游
	}

	// =================================================
	// 捐赠uri格式
	// [
	// {
	// "method" : "native",
	// "uri" :
	// "alipayqr : //platformapi/startapp?saId=10000007&routeBizType=0&qrcode=dn82z2mgx3nhr1b460&accountName=siyi&needAddress=F&qrCodeMoney=1.12&qrCodeName=siyi_公益--不限时不限额&tradeType=捐赠交易"
	// }
	// ]

	// 淘宝uri格式
	// [{
	// "method" : "native",
	// "uri" : "action=com.taobao.tao.DetailActivity?item_id=1212"
	// "intent://androidIntent/startapptaobao?path=com.taobao.tao.DetailActivity&item_id=1212"
	// }
	// {
	// "method" : "webview",
	// "uri"
	// :"http://a.waptest.taobao.com/i1212.htm?sid=57abf2a9207ebd93cf6b6e5de45df657&ttid=600351@AlipayPaiPai_barcodeprod_1.0.0"
	// }]

	public static final String SCHEME_ALIPAY = "alipay";
	public static final String SCHEME_ALIPAYQR = "alipayqr";
	public static final String SCHEME_INTENT = "intent";

	public boolean paipaiRouteNativeExecute(RootActivity activity, Page page,
			Uri uri) {
		this.activity = activity;
		this.page = page;
		this.uri = uri;

		boolean ret = false;

		engine = page.getEngine();

		if (uri.getScheme() != null && !"".equals(uri.getScheme())
				&& uri.getScheme().equals(SCHEME_ALIPAY)) {
			new SchemeHandler(activity).execute(uri);
			engine.exit();
			ret = true;
		} else if (uri.getScheme() != null && !"".equals(uri.getScheme())
				&& uri.getScheme().equals(SCHEME_INTENT)) {
			host = uri.getHost();
			path = uri.getPath().substring(1);
			if ("androidIntent".equals(host) && "startapp".equals(path)) {
				try {
					tryGotoTaobaoApp(uri);
					engine.exit();
					ret = true;
				} catch (Exception e) {
					e.printStackTrace();
					ret = false;
				}
			}
		} else if (uri.getScheme() != null && !"".equals(uri.getScheme())
				&& uri.getScheme().equals(SCHEME_ALIPAYQR)) {
			try {
				path = uri.getPath().substring(1);
				int index = uri.toString().indexOf("?");
				if (index < 0) {
					return false;
				}
				String keyValueData = uri.toString().substring(index + 1);

				String[] keyValueArray = keyValueData.split("&");
				mParamNames = new HashSet<String>(keyValueArray.length);
				for (int i = 0; i < keyValueArray.length; i++) {
					mParamNames.add(((keyValueArray[i]).split("="))[0]);
				}

				saId = uri.getQueryParameter("saId");
				routeBizType = uri.getQueryParameter("routeBizType");

				if ("startapp".equals(path) && "10000007".equals(saId)) {
					paipaiParamsHandle();
				}
				ret = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	private void paipaiParamsHandle() {
		for (Iterator<String> iterator = mParamNames.iterator(); iterator
				.hasNext();) {
			String paramName = (String) iterator.next();
			String paramValue = uri.getQueryParameter(paramName);
			if (engine != null) {
				engine.setRequestCache(paramName, paramValue);
			}
		}

		String parserOverExp = null;
		parserOverExp = paipaiExpMap.get(routeBizType);

		if (parserOverExp != null && !"".equals(parserOverExp)) {
			page.interpreter(page.getPageName() + "::" + parserOverExp,
					engine.getExp(parserOverExp));
		}
	}

	// [{
	// "method" : "native",
	// "intent://androidIntent/startapp?path=com.taobao.tao.DetailActivity&item_id=1212"
	// }

	private void tryGotoTaobaoApp(Uri taobaoUri) throws Exception {
		int index = uri.toString().indexOf("?");
		if (index < 0) {
			return;
		}
		String keyValueData = uri.toString().substring(index + 1);

		String[] keyValueArray = keyValueData.split("&");
		mParamNames = new HashSet<String>(keyValueArray.length);
		for (int i = 0; i < keyValueArray.length; i++) {
			mParamNames.add(((keyValueArray[i]).split("="))[0]);
		}

		String mPath = uri.getQueryParameter("path");

		try {
			Intent intent = new Intent(mPath);

			for (Iterator<String> iterator = mParamNames.iterator(); iterator
					.hasNext();) {
				String paramName = (String) iterator.next();
				if (!"path".equals(paramName)) {
					String paramValue = uri.getQueryParameter(paramName);
					intent.putExtra(paramName, paramValue);
				}
			}

			activity.startActivity(intent);
		} catch (Exception e) {
			throw new Exception("open taobaoapp exception ");
		}
	}

	public boolean paipaiRouteWebViewExecute(RootActivity activity, Page page,
			Uri uri) {
		this.activity = activity;
		this.page = page;
		this.uri = uri;

		boolean ret = false;

		engine = page.getEngine();
		engine.setRequestCache("routeurl", this.uri.toString());

		page.interpreter(page.getPageName() + "::routePushWap",
				engine.getExp("routePushWap"));
		ret = true;
		return ret;
	}
	// 商品码串（待拓展）
	// "alipayqr://platformapi/startapp?saId=10000007&routeBizType=4&memo=12312&accountName=1500027088922&qrCodeCountList={\"accountName\":\"小代\",\"bizType\":\"2\",\"needAddress\":\"T\",\"qrCodeCountList\":{\"defaultValue\":\"1\",\"itemExps\":[\"$ctrl_value_set(NormalOrderAmount,$compute(@JOIN,$value_format(%.2f,$compute(@MUL,$cache_get(qrCodeMoney),$ctrl_value_get(NormalOrderCountComboBox))),@1220))\"],\"values\":[{\"expIndex\":\"0\",\"showValue\":\"1\",\"value\":\"1\"},{\"expIndex\":\"0\",\"showValue\":\"2\",\"value\":\"2\"},{\"expIndex\":\"0\",\"showValue\":\"3\",\"value\":\"3\"},{\"expIndex\":\"0\",\"showValue\":\"4\",\"value\":\"4\"},{\"expIndex\":\"0\",\"showValue\":\"5\",\"value\":\"5\"},{\"expIndex\":\"0\",\"showValue\":\"6\",\"value\":\"6\"},{\"expIndex\":\"0\",\"showValue\":\"7\",\"value\":\"7\"},{\"expIndex\":\"0\",\"showValue\":\"8\",\"value\":\"8\"},{\"expIndex\":\"0\",\"showValue\":\"9\",\"value\":\"9\"},{\"expIndex\":\"0\",\"showValue\":\"10\",\"value\":\"10\"}]},\"qrCodeMoney\":\"1.00\",\"qrCodeName\":\"0319-1\",\"sms\":\"00\",\"tradeType\":\"商户交易\"}\"";
}
