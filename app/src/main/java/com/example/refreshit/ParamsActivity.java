package com.example.refreshit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

// Активность для финальной настройки добавляемой страницы
public class ParamsActivity extends AppCompatActivity {

	EditText nameInput;
	TextView urlText;
	Button applyButton;
	List<RadioButton> delays = new ArrayList<>();
	RadioGroup radioGroup;
	RadioButton selectedButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_params);

		nameInput = findViewById(R.id.name_input);
		applyButton = findViewById(R.id.apply_button);
		urlText = findViewById(R.id.url_text);
		radioGroup = findViewById(R.id.radioGroup);
		for (int i = 0; i < radioGroup.getChildCount(); i++) {
			if(radioGroup.getChildAt(i) instanceof RadioButton)
				delays.add((RadioButton) radioGroup.getChildAt(i));
		}

		selectedButton = findViewById(R.id.rbutton_1_hour);

		String url = getIntent().getStringExtra("Url");
		urlText.setText(url);
	}

	public void reselectDelay(View view){
		if(view instanceof RadioButton)
			selectedButton = (RadioButton) view;
	}

	public void applyClick(View view){

		Intent answerIntent = new Intent();

		answerIntent.putExtra("Name", String.valueOf(nameInput.getText()));

		if(selectedButton == null)
			return;

		switch (String.valueOf(selectedButton.getText())){
			case("15 min"):
				answerIntent.putExtra("DelayTime", 15);
				answerIntent.putExtra("DelayUnit", TimeUnit.MINUTES.ordinal());
				break;
			case("30 min"):
				answerIntent.putExtra("DelayTime", 30);
				answerIntent.putExtra("DelayUnit", TimeUnit.MINUTES.ordinal());
				break;
			case("1 hour"):
				answerIntent.putExtra("DelayTime", 1);
				answerIntent.putExtra("DelayUnit", TimeUnit.HOURS.ordinal());
				break;
			case("3 hours"):
				answerIntent.putExtra("DelayTime", 3);
				answerIntent.putExtra("DelayUnit", TimeUnit.HOURS.ordinal());
				break;
			case("8 hours"):
				answerIntent.putExtra("DelayTime", 8);
				answerIntent.putExtra("DelayUnit", TimeUnit.HOURS.ordinal());
				break;
			case("1 day"):
				answerIntent.putExtra("DelayTime", 1);
				answerIntent.putExtra("DelayUnit", TimeUnit.DAYS.ordinal());
				break;
		}

		setResult(RESULT_OK, answerIntent);
		finish();
	}
}
