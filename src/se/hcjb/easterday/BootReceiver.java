package se.hcjb.easterday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	
	private static final String TAG = "EasterLog:BootReceiver";

	@Override
	public void onReceive(Context context, Intent callingIntent) {
	    Log.d(TAG, "onReceived");

		EasterApplication easterApp = ((EasterApplication) context.getApplicationContext());
		
	    // Set up timer to go off at next event time
		easterApp.initTimer();
	    Log.d(TAG, " Easter app initiated!");

	    // Send notification (if any unread)
	    easterApp.notification(context, callingIntent);

	    
	}

}
