package com.example.refreshit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

// Активность для финальной настройки добавляемой страницы
public class ParamsActivity extends AppCompatActivity {

	EditText name_input;
	TextView url_text;
	Button apply_button;
	List<RadioButton> delays = new ArrayList<>();
	RadioGroup radioGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_params);

		name_input = findViewById(R.id.name_input);
		apply_button = findViewById(R.id.apply_button);
		url_text = findViewById(R.id.url_text);
		radioGroup = findViewById(R.id.radioGroup);
		for (int i = 0; i < radioGroup.getChildCount(); i++) {
			if(radioGroup.getChildAt(i) instanceof RadioButton)
				delays.add((RadioButton) radioGroup.getChildAt(i));
		}

		String url = getIntent().getStringExtra("Url");
		url_text.setText(url);
	}

	public void apply_click(View view){

	}
}
