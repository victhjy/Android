package com.example.zhbit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class WebShowActivity extends Activity {

	Button btn_change;
	WebView wv_view;
	String str_url;
	String str_name, str_pwd;
	String search_url;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webshowlayout);

		wv_view = (WebView) findViewById(R.id.WebShowWebView);

		str_url = "http://mylib.zhbit.com:8080/reader/redr_verify.php" + "?"
				+ "number=" + str_name + "&" + "passwd=" + str_pwd
				+ "&select=cert_no&returnUrl=";

		search_url = "http://mylib.zhbit.com:8080/opac/search.php";

		wv_view.getSettings().setJavaScriptEnabled(true); // 支持js
		wv_view.getSettings().setSupportZoom(true); // 可以缩放
		wv_view.setWebViewClient(new WebViewClient() { // 在webView中打开用户点击的连接
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
		
		wv_view.getSettings().setBuiltInZoomControls(true);	 // 支持缩放
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		str_url = bundle.getString("URL");

		wv_view.loadUrl(str_url);
		wv_view.loadUrl(search_url);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { // 屏蔽后退键
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && wv_view.canGoBack()) {
			wv_view.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
