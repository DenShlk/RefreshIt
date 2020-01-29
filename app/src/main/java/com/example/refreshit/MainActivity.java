package com.example.refreshit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	List<RefreshPage> active_pages = new ArrayList<>(), arhive_pages = new ArrayList<>();
	Button add_button, clear_button;
	RecyclerView actives, arhived;
	LayoutManager layoutManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		add_button = findViewById(R.id.add_button);
		clear_button = findViewById(R.id.clear_button);
		actives = findViewById(R.id.actives);
		arhived = findViewById(R.id.arhived);

		setInitialData();

		layoutManager = new LinearLayoutManager(this);
		actives.setLayoutManager(layoutManager);
		ActiveAdapter adapter1 = new ActiveAdapter(this, active_pages);
		actives.setAdapter(adapter1);

		layoutManager = new LinearLayoutManager(this);
		arhived.setLayoutManager(layoutManager);
		ActiveAdapter adapter2 = new ActiveAdapter(this, arhive_pages);
		arhived.setAdapter(adapter2);
	}

	private void setInitialData(){
		active_pages.add(new RefreshPage ("www.google.com", "google", new Date()));
		active_pages.add(new RefreshPage ("www.cats.net", "caaats!", new Date()));

		arhive_pages.add(new RefreshPage ("http://www.php.net", "php", new Date()));
	}

	public void add_click(View view){

	}
}
