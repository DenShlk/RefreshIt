package com.example.refreshit;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/*
	Этот класс позволяет переходить по ссылкам внутри приложения, стандартный WebViewClient выкидывает в браузер по умолчанию
 */
public class MyWebViewClient extends WebViewClient {
	@TargetApi(Build.VERSION_CODES.N)
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
		view.loadUrl(request.getUrl().toString());
		return true;
	}

	// Для старых устройств
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		view.loadUrl(url);
		return true;
	}
}
