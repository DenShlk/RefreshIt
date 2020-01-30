package com.example.refreshit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

// Активность для выбора страницы, за которой нужно следить
public class BrowserActivity extends AppCompatActivity {

	WebView webView;
	Button confirm_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);

		confirm_button = findViewById(R.id.confirm_button);
		webView = findViewById(R.id.webView);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("http://google.com");
		webView.setWebViewClient(new MyWebViewClient());
	}

	@Override
	public void onBackPressed() {
		if(webView.canGoBack()) {
			webView.goBack();
		} else {
			super.onBackPressed();
		}
	}

	public void confirm_click(View view) {
		Intent to_params = new Intent(BrowserActivity.this, ParamsActivity.class);
		to_params.putExtra("Url", webView.getUrl());
		startActivity(to_params);
	}
}
