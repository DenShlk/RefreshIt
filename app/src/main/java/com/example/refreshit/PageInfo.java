package com.example.refreshit;

import android.app.Activity;
import android.util.Log;

import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

public class PageInfo{
	String name, path;
	int delayTime, delayUnit;
	int dataHash = 0; // why not?

	public PageInfo(String name, String path, int delayTime, int delayUnit, boolean runWork) {
		this.name = name;
		this.path = path;
		this.delayTime = delayTime;
		this.delayUnit = delayUnit;

		if(runWork){
			runWorker();
		}
	}

	public PageInfo(String fileName, Activity active){
		readFromStorage(fileName, active);
	}

	public void runWorker(){
		PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(PageRefresher.class,
				15, TimeUnit.MINUTES)
				.setInputData(packData())
				.addTag(path)
				.build();

		WorkManager.getInstance().enqueueUniquePeriodicWork("Send Data",  ExistingPeriodicWorkPolicy.KEEP
				,workRequest);
	}

	public void stopWorker(){
		WorkManager.getInstance().cancelAllWorkByTag("path");
	}

	public PageInfo(Data data){
		unpackData(data);
	}

	Data packData(){
		Data data = new Data.Builder()
				.putString("Name", name)
				.putString("Path", path)
				.putInt("DelayTime", delayTime)
				.putInt("DelayUnit", delayUnit)
				.build();

		return data;
	}

	void unpackData(Data data){
		name = data.getString("Name", "undefined");
		path = data.getString("Path", "undefined");
		delayTime = data.getInt("DelayTime", -1);
		delayUnit = data.getInt("DelayUnit", -1);
	}

	String getFileName(){
		return path.replaceAll("/", "-");
	}

	/*
	save format:
	<name>
	<path>
	<delayTime>
	<delayUnit>
	<dataHash>
	 */
	String saveToStorage(Activity active){
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					active.openFileOutput(getFileName(), active.MODE_PRIVATE)));
			bw.write(name);
			bw.newLine();
			bw.write(path);
			bw.newLine();
			bw.write(String.valueOf(delayTime));
			bw.newLine();
			bw.write(String.valueOf(delayUnit));
			bw.newLine();
			bw.write(String.valueOf(dataHash));
			bw.newLine();
			bw.close();
			Log.d("PageInfo", "Файл записан\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return getFileName();
	}

	void readFromStorage(String fileName, Activity active){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					active.openFileInput(fileName)));

			name = br.readLine();
			path = br.readLine();
			delayTime = Integer.parseInt(br.readLine());
			delayUnit = Integer.parseInt(br.readLine());
			dataHash = Integer.parseInt(br.readLine());

			br.close();
			Log.d("PageInfo", "Файл считан\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void clearStorage(Activity active){
		active.deleteFile(getFileName());
		Log.d("PageInfo", "Файл удален :(\n");
	}
}
