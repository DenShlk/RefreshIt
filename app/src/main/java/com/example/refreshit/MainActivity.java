package com.example.refreshit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.content.PeriodicSync;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

	static final private int GET_URL = 1;
	static final private int GET_PARAMS = 2;

	List<PageInfo> active_pages = new ArrayList<>(), arhive_pages = new ArrayList<>();
	Button add_button, clear_button;
	RecyclerView actives, arhived;
	LayoutManager layoutManager;
	SiteAdapter active_adapter, arhive_adapter;

	String path, name;
	int delayTime, delayUnit;

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
		active_adapter = new SiteAdapter(this, active_pages);
		actives.setAdapter(active_adapter);

		layoutManager = new LinearLayoutManager(this);
		arhived.setLayoutManager(layoutManager);
		arhive_adapter = new SiteAdapter(this, arhive_pages);
		arhived.setAdapter(arhive_adapter);

	}

	private void setInitialData(){
		active_pages.add(new PageInfo ("www.google.com", "google", 15, TimeUnit.MINUTES.ordinal(), true));
		active_pages.add(new PageInfo ("www.cats.net", "caaats!", 1, TimeUnit.HOURS.ordinal(), true));

		arhive_pages.add(new PageInfo ("http://www.php.net", "php", 92, TimeUnit.DAYS.ordinal(), false));
	}

	public void add_click(View view){
		path = name = "";
		delayTime = delayUnit = -1;

		Intent to_browser = new Intent(MainActivity.this, BrowserActivity.class);
		startActivityForResult(to_browser, GET_URL);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == GET_URL) {
			if (resultCode == RESULT_OK) {
				path = data.getStringExtra("Url");

				Intent to_params = new Intent(MainActivity.this, ParamsActivity.class);
				to_params.putExtra("Url", path);
				startActivityForResult(to_params, GET_PARAMS);
			}
		}

		if(requestCode == GET_PARAMS){
			if(resultCode == RESULT_OK){
				name = data.getStringExtra("Name");
				delayTime = data.getIntExtra("DelayTime", 1);
				delayUnit = data.getIntExtra("DelayUnit", TimeUnit.HOURS.ordinal());

				add_page();
			}
		}
	}

	void add_page(){
		PageInfo page = new PageInfo(name, path, delayTime, delayUnit, true);
		active_pages.add(page);
		active_adapter.notifyItemRangeInserted(active_pages.size() - 1, 1);
	}
}
