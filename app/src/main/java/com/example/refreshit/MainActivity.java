package com.example.refreshit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.work.WorkManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

	static final private int GET_URL = 1;
	static final private int GET_PARAMS = 2;

	static final private String PAGE_LIST_FILE = "_PAGE_LIST_";

	static final private String TAG = "DEBUG_MAIN_ACTIVE";

	List<PageInfo> active_pages = new ArrayList<>(), archive_pages = new ArrayList<>();
	Button add_button, clear_button;
	RecyclerView actives, archived;

	PageAdapter active_adapter, archive_adapter;

	String path, name;
	int delayTime, delayUnit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		add_button = findViewById(R.id.add_button);
		clear_button = findViewById(R.id.clear_button);
		actives = findViewById(R.id.actives);
		archived = findViewById(R.id.archived);

		setInitialData();

		LayoutManager layoutManager = new LinearLayoutManager(this);
		actives.setLayoutManager(layoutManager);
		active_adapter = new PageAdapter(this, active_pages, true);
		actives.setAdapter(active_adapter);

		layoutManager = new LinearLayoutManager(this);
		archived.setLayoutManager(layoutManager);
		archive_adapter = new PageAdapter(this, archive_pages, false);
		archived.setAdapter(archive_adapter);

	}

	void activeItemClick(View view, int position){
		if(view instanceof Button){

			Button button = (Button) view;
			switch (String.valueOf(button.getText())){
				case("C"):
					//TODO: item changing
					break;
				case("A"):
					addArchive(active_pages.get(position));
					deleteActive(position);
					break;
				case("D"):
					deleteActive(position);
					break;
			}
		}
	}

	void archiveItemClick(View view, int position){
		if(view instanceof Button){
			Button button = (Button) view;
			switch (String.valueOf(button.getText())){
				case("C"):
					//TODO: item changing
					addActive(archive_pages.get(position));
					deleteArchive(position);
					break;
				case("A"):
					addActive(archive_pages.get(position));
					deleteArchive(position);
					break;
				case("D"):
					deleteArchive(position);
					break;
			}
		}
	}

	private void setInitialData() {

		printFile(PAGE_LIST_FILE);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					openFileInput(PAGE_LIST_FILE)));


			int active_count = Integer.parseInt(br.readLine());
			for (int i = 0; i < active_count; i++) {
				//printFile(br.readLine());
				active_pages.add(new PageInfo(br.readLine(), MainActivity.this, true));
			}
			int archive_count = Integer.parseInt(br.readLine());
			for (int i = 0; i < archive_count; i++) {
				archive_pages.add(new PageInfo(br.readLine(), MainActivity.this, false));
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
			bw.write(String.valueOf(archive_pages.size()));
			bw.newLine();
			for (int i = 0; i < archive_pages.size(); i++) {
				bw.write(archive_pages.get(i).getFileName());
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

				PageInfo page = new PageInfo(name, path, delayTime, delayUnit, true);

				addActive(page);
				page.saveToStorage(MainActivity.this);
			}
		}
	}

	void addActive(PageInfo page) {
		active_pages.add(page);
		savePageList();
		page.runWorker();

		active_adapter.notifyItemRangeInserted(active_pages.size() - 1, 1);
	}

	void deleteActive(int position) {
		active_pages.get(position).clearStorage(MainActivity.this);
		active_pages.get(position).stopWorker();
		active_pages.remove(position);
		savePageList();

		active_adapter.notifyItemRangeRemoved(position, 1);
	}

	void addArchive(PageInfo page) {
		archive_pages.add(page);
		savePageList();

		archive_adapter.notifyItemRangeInserted(archive_pages.size() - 1, 1);
	}

	void deleteArchive(int position) {
		archive_pages.get(position).clearStorage(MainActivity.this);
		archive_pages.remove(position);
		archive_adapter.notifyItemRangeRemoved(position, 1);
		savePageList();
	}

	public void clearClick(View view){
		while(!archive_pages.isEmpty())
			deleteArchive(0);
	}
}
