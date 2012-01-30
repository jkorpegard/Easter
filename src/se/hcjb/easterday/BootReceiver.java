package se.hcjb.easterday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
	
//	private static final String TAG = "EasterLog:BootReceiver";

	@Override
	public void onReceive(Context context, Intent callingIntent) {

		EasterApplication easterApp = ((EasterApplication) context.getApplicationContext());
		
	    // Set up timer to go off at next event time
		easterApp.initTimer();

	    // Send notification (if any unread)
	    easterApp.notification(context, callingIntent);

	    
	}

}
