package com.example.refreshit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import android.content.Intent;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

	static final private int GET_URL = 1;
	static final private int GET_PARAMS = 2;

	static final private String PAGE_LIST_FILE = "_PAGE_LIST_";

	static final private String TAG = "DEBUG_MAIN_ACTIVE";

	List<PageInfo> activePages = new ArrayList<>(), archivePages = new ArrayList<>();
	Button addButton, clearButton;
	RecyclerView actives, archived;

	PageAdapter activeAdapter, archiveAdapter;

	String path, name;
	int delayTime, delayUnit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		addButton = findViewById(R.id.add_button);
		clearButton = findViewById(R.id.clear_button);
		actives = findViewById(R.id.actives);
		archived = findViewById(R.id.archived);

		setInitialData();

		LayoutManager layoutManager = new LinearLayoutManager(this);
		actives.setLayoutManager(layoutManager);
		activeAdapter = new PageAdapter(this, activePages, true);
		actives.setAdapter(activeAdapter);

		layoutManager = new LinearLayoutManager(this);
		archived.setLayoutManager(layoutManager);
		archiveAdapter = new PageAdapter(this, archivePages, false);
		archived.setAdapter(archiveAdapter);

	}

	void activeItemClick(View view, int position){
		if(view instanceof Button){

			Button button = (Button) view;
			switch (String.valueOf(button.getText())){
				case("C"):
					//TODO: item changing
					break;
				case("A"):
					addArchive(activePages.get(position));
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
					addActive(archivePages.get(position));
					deleteArchive(position);
					break;
				case("A"):
					addActive(archivePages.get(position));
					deleteArchive(position);
					break;
				case("D"):
					deleteArchive(position);
					break;
			}
		}
	}

	private void setInitialData() {

		printPageListFile();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					openFileInput(PAGE_LIST_FILE)));


			int activeCount = Integer.parseInt(br.readLine());
			for (int i = 0; i < activeCount; i++) {
				//printPageListFile(br.readLine());
				activePages.add(new PageInfo(br.readLine(), MainActivity.this, true));
			}
			int archiveCount = Integer.parseInt(br.readLine());
			for (int i = 0; i < archiveCount; i++) {
				archivePages.add(new PageInfo(br.readLine(), MainActivity.this, false));
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

			bw.write(String.valueOf(activePages.size()));
			bw.newLine();
			for (int i = 0; i < activePages.size(); i++) {
				bw.write(activePages.get(i).getFileName());
				bw.newLine();
			}
			bw.write(String.valueOf(archivePages.size()));
			bw.newLine();
			for (int i = 0; i < archivePages.size(); i++) {
				bw.write(archivePages.get(i).getFileName());
				bw.newLine();
			}

			bw.close();
			Log.d("MainActivity", "Все сохранено\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

		printPageListFile();
	}

	void printPageListFile() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					openFileInput(MainActivity.PAGE_LIST_FILE)));

			String s = br.readLine();
			while (s != null) {
				Log.d("MainActivity", s);
				s = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addClick(View view) {
		path = name = "";
		delayTime = delayUnit = -1;

		Intent toBrowser = new Intent(MainActivity.this, BrowserActivity.class);
		startActivityForResult(toBrowser, GET_URL);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == GET_URL) {
			if (resultCode == RESULT_OK) {
				assert data != null;

				path = data.getStringExtra("Url");

				Intent toParams = new Intent(MainActivity.this, ParamsActivity.class);
				toParams.putExtra("Url", path);
				startActivityForResult(toParams, GET_PARAMS);
			}
		}

		if (requestCode == GET_PARAMS) {
			if (resultCode == RESULT_OK) {
				assert data != null;

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
		activePages.add(page);
		savePageList();
		page.runWorker();

		activeAdapter.notifyItemRangeInserted(activePages.size() - 1, 1);
	}

	void deleteActive(int position) {
		activePages.get(position).clearStorage(MainActivity.this);
		activePages.get(position).stopWorker();
		activePages.remove(position);
		savePageList();

		activeAdapter.notifyItemRangeRemoved(position, 1);
	}

	void addArchive(PageInfo page) {
		archivePages.add(page);
		savePageList();

		archiveAdapter.notifyItemRangeInserted(archivePages.size() - 1, 1);
	}

	void deleteArchive(int position) {
		archivePages.get(position).clearStorage(MainActivity.this);
		archivePages.remove(position);
		archiveAdapter.notifyItemRangeRemoved(position, 1);
		savePageList();
	}

	public void clearClick(View view){
		while(!archivePages.isEmpty())
			deleteArchive(0);
	}
}
