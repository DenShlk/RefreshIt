package com.example.refreshit;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

// Активность для выбора страницы, за которой нужно следить
public class BrowserActivity extends AppCompatActivity {

	static final private int ADD_PAGE = 1;
	static final private String INIT_URL = "http://www.google.com";

	WebView webView;
	Button confirmButton;
	EditText urlInput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);

		confirmButton = findViewById(R.id.confirm_button);

		webView = findViewById(R.id.webView);

		urlInput = findViewById(R.id.url_input);
		urlInput.setText(INIT_URL);
		urlInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }

			@Override
			public void afterTextChanged(Editable s) {
				if(urlInput.hasFocus())
					if(webView.getUrl().compareTo(String.valueOf(s))!=0)
						webView.loadUrl(String.valueOf(s));
			}
		});


		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(INIT_URL);
		webView.setWebViewClient(new MyWebViewClient(urlInput));
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
