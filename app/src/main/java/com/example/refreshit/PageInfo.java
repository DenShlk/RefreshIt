package com.example.refreshit;

import android.content.Context;
import android.util.Log;

import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

class PageInfo{
	String name;
	private String path;
	int delayTime;
	int delayUnit;
	int contentHash = 0; // why not?
	private Context context;

	PageInfo(String name, String path, int delayTime, int delayUnit, boolean runWork) {
		this.name = name;
		this.path = path;
		this.delayTime = delayTime;
		this.delayUnit = delayUnit;
		setContent(runWork);

		if(runWork){
			runWorker();
		}

	}

	PageInfo(String fileName, Context context, boolean runWork){
		readFromStorage(fileName, context);
		if(runWork){
			runWorker();
		}
	}

	PageInfo(Data data){
		unpackData(data);
	}


	void runWorker(){
		PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(PageRefresher.class,
				delayTime, TimeUnit.values()[delayUnit])
				.setInitialDelay(1, TimeUnit.MINUTES)
				.setInputData(packData())
				.addTag(path)
				.build();

		WorkManager.getInstance().enqueueUniquePeriodicWork("Send Data",  ExistingPeriodicWorkPolicy.REPLACE
				,workRequest);
	}

	void stopWorker(){
		WorkManager.getInstance().cancelAllWorkByTag(path);
	}

	private Data packData(){
		return new Data.Builder()
				.putString("Name", name)
				.putString("Path", path)
				.putInt("DelayTime", delayTime)
				.putInt("DelayUnit", delayUnit)
				.putInt("ContentHash", contentHash)
				.build();
	}

	private void unpackData(Data data){
		name = data.getString("Name");
		path = data.getString("Path");
		delayTime = data.getInt("DelayTime", -1);
		delayUnit = data.getInt("DelayUnit", -1);
		contentHash = data.getInt("ContentHash", -1);
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
	<contentHash>
	 */
	void saveToStorage(Context context){
		this.context = context;
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					context.openFileOutput(getFileName(), Context.MODE_PRIVATE)));
			bw.write(name);
			bw.newLine();
			bw.write(path);
			bw.newLine();
			bw.write(String.valueOf(delayTime));
			bw.newLine();
			bw.write(String.valueOf(delayUnit));
			bw.newLine();
			bw.write(String.valueOf(contentHash));
			bw.newLine();
			bw.close();
			Log.d("PageInfo", "Файл записан\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

		getFileName();
	}

	private void readFromStorage(String fileName, Context context){
		this.context = context;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					context.openFileInput(fileName)));

			name = br.readLine();
			path = br.readLine();
			delayTime = Integer.parseInt(br.readLine());
			delayUnit = Integer.parseInt(br.readLine());
			contentHash = Integer.parseInt(br.readLine());

			br.close();
			Log.d("PageInfo", "Файл считан\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void clearStorage(Context context){
		context.deleteFile(getFileName());
		Log.d("PageInfo", "Файл удален :(\n");
	}

	private static String getHtml(String url) throws Exception {
		// Build and set timeout values for the request.
		URLConnection connection = (new URL(url)).openConnection();
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(5000);
		connection.connect();

		// Read and store the result line by line then return the entire string.
		InputStream in = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder html = new StringBuilder();
		for (String line; (line = reader.readLine()) != null; ) {
			html.append(line);
		}
		in.close();
		Log.d("Html", "loaded");
		return html.toString();
	}

	boolean checkContentUpdates(){
		try {
			String newHtml = getHtml(path);
			int newHash = newHtml.hashCode();
			Log.d("Hashes", contentHash + " " + newHash);
			return newHash != contentHash;
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("Hashes", "Error");
		}

		return true;
	}

	private void setContent(final boolean runWork){
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					String html = getHtml(path);
					contentHash = html.hashCode();

					if(context!=null)
						saveToStorage(context);
					//we need to refresh data, which push to worker
					if(runWork)
						runWorker();

					Log.d("Hashes", "contentHash= " + contentHash);
				} catch (Exception e) {
					e.printStackTrace();
					Log.d("Hashes", "Error");
				}
			}
		}).start();
	}

}
