package com.example.refreshit;

import android.content.Context;
import android.util.Log;

import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

class PageInfo{
	String name;
	private String path;
	private List<String> content;
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

	PageInfo(Data data, Context context){
		path = data.getString("Path");
		readFromStorage(getFileName(), context);
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
				.putString("Path", path)
				.build();
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
	<content>
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
			bw.write(content2String());
			bw.newLine();
			bw.close();
			Log.d("PageInfo", "Файл записан\n");
		} catch (Exception e) {
			e.printStackTrace();
		}

		getFileName();
	}

	private String content2String(){
		String result = content.get(0);
		for (int i = 1; i < content.size(); i++) {
			result += "<" + content.get(i);
			//use '<' because it shouldn't contains in content
		}
		return result;
	}
	private void string2Content(String input){
		content = Arrays.asList(input.split("<"));
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
			string2Content(br.readLine());
			Log.d("Content read", String.valueOf(content.size()));
			br.close();
			Log.d("PageInfo", "Файл считан\n");
		} catch (Exception e) {
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
			//int newHash = newHtml.hashCode();
			//Log.d("Hashes", contentHash + " " + newHash);

			List<String> newContent = HtmlComparer.getContentFromHtml(newHtml);
			Log.d("Content", String.valueOf(newContent.size()));
			Log.d("Html now", newHtml.length() + " " + newHtml);

			Log.d("Compare", String.valueOf(HtmlComparer.tableCompare(content, newContent));
			//return newHash != contentHash;
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
					content = HtmlComparer.getContentFromHtml(html);

					if(context!=null)
						saveToStorage(context);
					//we need to refresh data, which push to worker
					if(runWork)
						runWorker();

					Log.d("Hashes", "contentHash= " + contentHash);
					Log.d("Content now", content.length() + " " + content);
					Log.d("Html now", html.length() + " " + html);
				} catch (Exception e) {
					e.printStackTrace();
					Log.d("Hashes", "Error");
				}
			}
		}).start();
	}

}
