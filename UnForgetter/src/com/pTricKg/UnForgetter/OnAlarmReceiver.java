package com.pTricKg.UnForgetter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.util.Log;

public class OnAlarmReceiver extends BroadcastReceiver {
	private static final String TAG = ComponentInfo.class.getCanonicalName(); 


	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Received wake up from alarm manager.");

		long rowid = intent.getExtras().getLong(UnForgetterDbAdapter.KEY_ROWID);

		WakeUnForgetterIntentService.acquireStaticLock(context);

		Intent i = new Intent(context, UnForgetterService.class); 
		i.putExtra(UnForgetterDbAdapter.KEY_ROWID, rowid);  
		context.startService(i);

	}
}
