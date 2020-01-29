package com.example.refreshit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;

public class Browser extends AppCompatActivity {

	WebView webView;
	Button confirm_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);

		confirm_button = findViewById(R.id.confirm_button);
		
	}
}
