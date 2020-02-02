package com.example.refreshit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

// Активность для выбора страницы, за которой нужно следить
public class BrowserActivity extends AppCompatActivity {

	static final private int ADD_PAGE = 1;

	WebView webView;
	Button confirmButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);

		confirmButton = findViewById(R.id.confirm_button);
		webView = findViewById(R.id.webView);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("http://google.com");
		webView.setWebViewClient(new MyWebViewClient());
	}

	@Override
	public void onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			super.onBackPressed();
		}
	}

	public void confirm_click(View view) {
		Intent answerIntent = new Intent();

		answerIntent.putExtra("Url", webView.getUrl());

		setResult(RESULT_OK, answerIntent);
		finish();
	}
}
