package com.example.refreshit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class PageInfo{
	String name, path, requestTag;
	int delayTime, delayUnit;

	public PageInfo(String name, String path, int delayTime, int delayUnit, boolean runWork) {
		this.name = name;
		this.path = path;
		this.delayTime = delayTime;
		this.delayUnit = delayUnit;

		if(runWork){
			runWorker();
		}
	}

	public void runWorker(){
		PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(PageRefresher.class, 15, TimeUnit.MINUTES)
				.setInputData(packData())
				.addTag(path)
				.build();

		WorkManager.getInstance().enqueue(workRequest);
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
}
