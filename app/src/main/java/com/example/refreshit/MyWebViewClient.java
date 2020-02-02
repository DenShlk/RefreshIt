package com.example.refreshit;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

/*
	Этот класс позволяет переходить по ссылкам внутри приложения, стандартный WebViewClient выкидывает в браузер по умолчанию
 */
public class MyWebViewClient extends WebViewClient {
	EditText urlPath;

	public MyWebViewClient(EditText urlPath) {
		super();
		this.urlPath = urlPath;
	}

	@TargetApi(Build.VERSION_CODES.N)
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
		view.loadUrl(request.getUrl().toString());
		return true;
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);

		if(!urlPath.hasFocus())
			urlPath.setText(url);
	}

	// Для старых устройств
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		view.loadUrl(url);
		return true;
	}
}
