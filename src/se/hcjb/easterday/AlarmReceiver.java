package se.hcjb.easterday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;


public class AlarmReceiver extends BroadcastReceiver {
	private static final String TAG = "EasterLog:AlarmReceiver";
	SharedPreferences prefs;
	int notifyIndex = -1;


	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "AlarmReceiver.onReceive");

		//From EasterService
	    EasterApplication easterApp = ((EasterApplication) context.getApplicationContext());
	    
	    easterApp.notification(context, intent); 		

		// Initiate next event
		easterApp.initTimer();
	}





}
