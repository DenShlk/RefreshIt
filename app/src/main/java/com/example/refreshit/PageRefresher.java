package com.example.refreshit;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;

public class PageRefresher  extends Worker {

	static private final String CHANNEL_ID = "Refresh It";

	public PageRefresher(@NonNull Context context, @NonNull WorkerParameters workerParams) {
		super(context, workerParams);
	}

	@NonNull
	@Override
	public Result doWork() {

		PageInfo page = new PageInfo(getInputData());

		createNotificationChannel();

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

		if(page.checkContentUpdates()) {
			mBuilder.setContentTitle(page.name)
					.setContentText(Calendar.getInstance().getTime().toString() + " was changed!")
					.setSmallIcon(R.drawable.ic_launcher_background);
		}else{
			mBuilder.setContentTitle(page.name)
					.setContentText(Calendar.getInstance().getTime().toString() + " checked")
					.setSmallIcon(R.drawable.ic_launcher_background);
		}
		notificationManager.notify(page.name.hashCode(), mBuilder.build());
		Log.d("PageRefresher", String.valueOf(page.contentHash));

		return Result.success();
	}


	private void createNotificationChannel() {
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = "Refresh It";
			String description = "Refresh It notification channel";
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
			channel.setDescription(description);
			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}
	}
}
