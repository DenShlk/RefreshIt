package com.example.refreshit;

import android.graphics.pdf.PdfDocument;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;

// 1) Not Black, back!!!
// 2) Класс для проверки изменений через промежуток времени
public class BackWorker extends Worker {
	@NonNull
	@Override
	public WorkerResult doWork() {

		PageInfo page = new PageInfo(getInputData());

		Log.d("BackWorker", page.name);

		return WorkerResult.SUCCESS;
	}
}
