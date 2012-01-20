package se.hcjb.easterday;

import java.io.IOException;
import java.util.Random;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

public class EasterApplication extends Application {
	public BibleTexts bibleTexts=null;
	///// "Global variable" for data transfer to ViewBibleText TODO: Replace with intent data! 
	int id = -1;

	private static final String TAG = "EasterLog:"+EasterApplication.class.getSimpleName();
	public static final int ALARM_INTENT_ID = 214725233; // A unique ID

	public static final int maxCommercialId = 5;
	public static final int APP_MODE_NOT_STARTED = 0;
	public static final int APP_MODE_STARTED = 1;
	public static final int APP_MODE_RUNNING = 2;
	public static final int APP_MODE_ALL_READ= 3;
	public static final int APP_MODE_RESTARTED = 4;

	@Override public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
		
		bibleTexts = new BibleTexts(this.getApplicationContext());
		
		try {
			initTimer();
		} catch (Exception e) {
			Log.e(TAG, "Could not initiate timer: " + e.toString()); 
		}
		
	}
	
	public static final int getCommercialRef(int id) {
		if (id==1) return R.string.reklmatext1;
		else if (id==2) return R.string.reklmatext2;
		else if (id==3) return R.string.reklmatext3;
		else if (id==4) return R.string.reklmatext4;
		else return R.string.reklmatext5;
	}
	
	
	public void notification(Context context, Intent intent) {
				
		Cursor cursor = null;
		Log.d(TAG, " ----------> Alarm Updater running, with intent " + intent);
		try {
			if (bibleTexts.isAllReadNow())
				return; // If we got an event when all bible texts are read - just ignore!
			if (bibleTexts.getEasterDate()!=null) {
				Log.d(TAG, "Alarm Updater ran");
				cursor = bibleTexts.getTexts();
				if (cursor.getCount() > 0) {
					cursor.moveToLast();
					int cId = createNotification(context, cursor);
					Editor ed = PreferenceManager.getDefaultSharedPreferences(this).edit(); 
					ed.putInt("maxId", cursor.getInt(cId));
					ed.commit();
//					Log.d(TAG, "New Max-id: " + cursor.getInt(cId));
				}
				cursor.close();
			}
		} catch (Exception e) {  
			Log.d(TAG, "!!!!!!!!!!!!! Caught exception: " + e.toString());
			if (cursor!=null)
				cursor.close();
		}
		return;
	}

	

	public void initTimer() { 
		Intent alarmIntent = new Intent(this, AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, EasterApplication.ALARM_INTENT_ID, alarmIntent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE); 
		long nextEventTime = -1;
		try {
			nextEventTime = bibleTexts.getNextEventTime(System.currentTimeMillis()+1000)+2000;
		} catch (Exception e) {
			Log.e(TAG, "Could not read database; try again in 10 seconds... ");
			try {
				bibleTexts.createDatabase(getBaseContext());
			} catch (IOException e1) {
				Log.e(TAG, "Could not create database..." + e.toString());
				e1.printStackTrace();
			}
			nextEventTime = System.currentTimeMillis()+10000;
		}
		alarmManager.set(AlarmManager.RTC_WAKEUP, nextEventTime, pendingIntent);
	}

	public void activateCommercial(Activity act, int vId) {
		int commercialId = (new Random()).nextInt(EasterApplication.maxCommercialId);
        Log.d(TAG,"Commercial ID = " + commercialId);
        TextView tView3 = (TextView) act.findViewById(vId);
	    tView3.setText(EasterApplication.getCommercialRef(commercialId));
	    tView3.setMovementMethod(LinkMovementMethod.getInstance());
	}
	
	public int createNotification(Context context, Cursor cursor) {
		
		if (cursor==null) return -1;
		
		int cText = cursor.getColumnIndex(BibleTexts.C_TEXT);
		int cBookName= cursor.getColumnIndex(BibleTexts.C_BOOK_NAME);
		int cChapVerse = cursor.getColumnIndex(BibleTexts.C_CHAP_VERSE);
		int cDay= cursor.getColumnIndex(BibleTexts.C_DAY);
		int cHour = cursor.getColumnIndex(BibleTexts.C_HOUR);
		int cMin = cursor.getColumnIndex(BibleTexts.C_MINUTE);
		int cPlace= cursor.getColumnIndex(BibleTexts.C_LOCATION_TEXT);
		int cId = cursor.getColumnIndex(BibleTexts.C_ID);
		String place = cursor.getString(cPlace);
		String datestring = BibleTexts.makeDateString(context, cursor.getInt(cDay), cursor.getInt(cHour), cursor.getInt(cMin));
		String text = cursor.getString(cText);
		String chapVerse = cursor.getString(cBookName) + " " + cursor.getString(cChapVerse);
		sendNotification(context, place, datestring, text, chapVerse);
		return cId;
	} 
	
	private NotificationManager mManager;
	private static final int APP_ID = 0;
	  
	
	private void sendNotification(Context context, String place, String datestring, String text, String chapVerse) {
		mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, -1,
				new Intent(context, Easter.class), PendingIntent.FLAG_UPDATE_CURRENT);
		Notification notification = new Notification(R.drawable.eastericon, place +", "+ datestring, System.currentTimeMillis());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context); 
//		if (prefs.getBoolean("enableAlert", false)) {
			notification.defaults = Notification.DEFAULT_ALL;
			long[] vibrate = {100,100,200,300};
			notification.vibrate = vibrate;
//		}
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		if (prefs.getBoolean("enableInsistent", false))
			notification.flags |= Notification.FLAG_INSISTENT;
		notification.setLatestEventInfo(context, datestring + ", " + place, text, pendingIntent);
		mManager.notify(APP_ID, notification);

//		Log.d(TAG, "Sent notification: " + chapVerse);
		  
	  }
}
