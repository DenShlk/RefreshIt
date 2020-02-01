package com.example.refreshit;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;

public class PageRefresher  extends Worker {
	@NonNull
	@Override
	public Result doWork() {

		PageInfo page = new PageInfo(getInputData());

		Log.d("PageRefresher", page.name);

		return Result.SUCCESS;
	}
}
