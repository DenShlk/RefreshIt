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
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

	static final private int GET_URL = 1;
	static final private int GET_PARAMS = 2;
	static final private String PAGE_LIST_FILE = "_PAGE_LIST_";

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

	private void setInitialData() {

		printFile(PAGE_LIST_FILE);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					openFileInput(PAGE_LIST_FILE)));


			int active_count = Integer.parseInt(br.readLine());
			for (int i = 0; i < active_count; i++) {
				//printFile(br.readLine());
				active_pages.add(new PageInfo(br.readLine(), MainActivity.this));
			}
			int arhive_count = Integer.parseInt(br.readLine());
			for (int i = 0; i < arhive_count; i++) {
				arhive_pages.add(new PageInfo(br.readLine(), MainActivity.this));
			}

			br.close();
			Log.d("MainActivity", "Файл считан\n");
		} catch (Exception e) {
			try {
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						openFileOutput(PAGE_LIST_FILE, MODE_PRIVATE)));
				bw.write("0\n0\n");

				bw.close();
			}catch (IOException e2) {
				e.printStackTrace();
				e2.printStackTrace();
			}
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		savePageList();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		savePageList();
	}

	void savePageList(){
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					openFileOutput(PAGE_LIST_FILE, MODE_PRIVATE)));

			bw.write(String.valueOf(active_pages.size()));
			bw.newLine();
			for (int i = 0; i < active_pages.size(); i++) {
				bw.write(active_pages.get(i).getFileName());
				bw.newLine();
			}
			bw.write(String.valueOf(arhive_pages.size()));
			bw.newLine();
			for (int i = 0; i < arhive_pages.size(); i++) {
				bw.write(arhive_pages.get(i).getFileName());
				bw.newLine();
			}

			bw.close();
			Log.d("MainActivity", "Все сохранено\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

		printFile(PAGE_LIST_FILE);
	}

	void printFile(String file) {
		String s = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					openFileInput(file)));

			s = br.readLine();
			while (s != null) {
				Log.d("MainActivity", s);
				s = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void add_click(View view) {
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

		if (requestCode == GET_PARAMS) {
			if (resultCode == RESULT_OK) {
				name = data.getStringExtra("Name");
				delayTime = data.getIntExtra("DelayTime", 1);
				delayUnit = data.getIntExtra("DelayUnit", TimeUnit.HOURS.ordinal());

				add_page();
			}
		}
	}

	void add_page() {
		PageInfo page = new PageInfo(name, path, delayTime, delayUnit, true);

		page.saveToStorage(MainActivity.this);
		savePageList();

		active_pages.add(page);
		active_adapter.notifyItemRangeInserted(active_pages.size() - 1, 1);
	}
}
