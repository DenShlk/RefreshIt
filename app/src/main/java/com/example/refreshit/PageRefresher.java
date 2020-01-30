package com.example.refreshit;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;

import java.util.concurrent.TimeUnit;

public class PageRefresher  extends Worker {
	@NonNull
	@Override
	public WorkerResult doWork() {

		PageInfo page = new PageInfo(getInputData());

		Log.d("BackWorker", page.name);

		return WorkerResult.SUCCESS;
	}
}
