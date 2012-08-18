package com.pTricKg.UnForgetter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import com.pTricKg.UnForgetter.R;

public class UnForgetterService extends WakeUnForgetterIntentService {
	public UnForgetterService() {
		super("UnForgetterService");
			}

	@SuppressWarnings("deprecation")
	@Override
	void doTaskerWork(Intent intent) {
		Log.d("UnForgetterService", "Doing work.");
		Long rowId = intent.getExtras().getLong(UnForgetterDbAdapter.KEY_ROWID);

		//set up notification bar alerts
		NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		Intent notificationIntent = new Intent(this, UnForgetterEditActivity.class); 
		notificationIntent.putExtra(UnForgetterDbAdapter.KEY_ROWID, rowId); 

		PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT); 

		Notification note=new Notification(android.R.drawable.stat_sys_warning, getString(R.string.notify_new_task_message), System.currentTimeMillis());
		note.setLatestEventInfo(this, getString(R.string.notify_new_task_title), getString(R.string.notify_new_task_message), pi);
		note.defaults |= Notification.DEFAULT_SOUND; 
		note.flags |= Notification.FLAG_AUTO_CANCEL; 

		// An issue could occur if user ever enters over 2,147,483,647 tasks. (Max int value). 
		// I highly doubt this will ever happen. But is good to note. 
		int id = (int)((long)rowId);
		mgr.notify(id, note); 
		mgr.cancel(id);
		mgr.cancelAll();


	}
}
