package se.hcjb.easterday;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;


public class BibleTexts {
	private static final String TAG = "EasterLog:BibleTexts";
	SharedPreferences prefs;
	Context baseContext;
	private final String DB_DESTINATION = "/data/data/se.hcjb.easterday/databases/BibleTexts.db";
	protected boolean createdNewDatabase = false;
	
	final static public String makeDateString(Context context, int days, int hour, int minute)
	{
		String weekday;
		if (days<=0) {
			if (abs(days % 7) == 0) weekday=context.getResources().getString(R.string.weekday_sunday);
			else if (abs(days % 7) == 1) weekday=context.getResources().getString(R.string.weekday_saturday);
			else if (abs(days % 7) == 2) weekday=context.getResources().getString(R.string.weekday_friday);
			else if (abs(days % 7) == 3) weekday=context.getResources().getString(R.string.weekday_thursday);
			else if (abs(days % 7) == 4) weekday=context.getResources().getString(R.string.weekday_wednesday);
			else if (abs(days % 7) == 5) weekday=context.getResources().getString(R.string.weekday_tuesday);
			else weekday=context.getResources().getString(R.string.weekday_monday);
		}
		else {
			if ((days % 7) == 0) weekday=context.getResources().getString(R.string.weekday_sunday);
			else if ((days % 7) == 6) weekday=context.getResources().getString(R.string.weekday_saturday);
			else if ((days % 7) == 5) weekday=context.getResources().getString(R.string.weekday_friday);
			else if ((days % 7) == 4) weekday=context.getResources().getString(R.string.weekday_thursday);
			else if ((days % 7) == 3) weekday=context.getResources().getString(R.string.weekday_wednesday);
			else if ((days % 7) == 2) weekday=context.getResources().getString(R.string.weekday_tuesday);
			else weekday=context.getResources().getString(R.string.weekday_monday);
		}

		
		return weekday + ", " + String.format("%2d", hour) + ":" + String.format("%02d", minute);

	}
	
	final static private int abs(int in) 
	{
		if (in>0) return in;
		else return -in;
	}
	
	final static public String makeDateString(Context context, Cursor cursor)
	{
		int cDay= cursor.getColumnIndex(BibleTexts.C_DAY);
		int cHour= cursor.getColumnIndex(BibleTexts.C_HOUR);
		int cMinute= cursor.getColumnIndex(BibleTexts.C_MINUTE);
		
		return makeDateString(context, cursor.getInt(cDay), cursor.getInt(cHour), cursor.getInt(cMinute));

	}

	BibleTexts(Context context)
	{
		boolean initialiseDatabase = (new File(DB_DESTINATION)).exists();

		baseContext = context;
		try {
			// TODO Does not work after fresh install; app thinks it has version 1 on DB, 
			// but version is now 2... How to set initial version???
			if (!initialiseDatabase) {
				createDatabase(context);
//				Log.e(TAG, "Re-created database!");

			}
		} catch (Exception e) {
			Log.e(TAG, "Caught Error when creating database: " +e);
			Log.e(TAG, "Stack trace: " + e.getStackTrace().toString());
		}
	    this.dbHelper = new DbHelper(context);
	}
	
	public void createDatabase(Context cntxt) throws IOException {
//		Log.d(TAG, "started createDatabase... ");
		  
		InputStream assetsDB = cntxt.getAssets().open("BibleTexts.db");
//		Log.d(TAG, "Opened source file... "  + assetsDB.toString());
		OutputStream dbOut = new FileOutputStream(DB_DESTINATION);
//		Log.d(TAG, "Opened destination file... " + DB_DESTINATION);
		
		byte[] buffer = new byte[1024];
		int length;
//		Log.d(TAG, "Starting to copy file... ");
		while ((length = assetsDB.read(buffer))>0){
			dbOut.write(buffer, 0, length);
		}
//		Log.d(TAG, "Done copying file... ");
		 
		dbOut.flush();
		dbOut.close();
		assetsDB.close();
//		Log.d(TAG, "Closed and flushed all files... ");
		
		createdNewDatabase = true;
//	    Log.d(TAG, "Set flag createdNewDatabase to true");
	}

	public void setEasterDate(Calendar newEasterDate) {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(baseContext); 
    	Editor prefsEdit = prefs.edit();
		if (newEasterDate == null) {
	    	prefsEdit.putInt("easterDay_DayOfYear_int", -1);
	    	prefsEdit.putInt("easterDay_Year_int", -1);
		}
		else {
	    	prefsEdit.putInt("easterDay_DayOfYear_int", newEasterDate.get(Calendar.DAY_OF_YEAR));
	    	prefsEdit.putInt("easterDay_Year_int", newEasterDate.get(Calendar.YEAR));
		}
    	prefsEdit.commit();        	
	}
	
	public Calendar getEasterDate()
	{
		try {
			prefs = PreferenceManager.getDefaultSharedPreferences(baseContext); 
			int easterDate = prefs.getInt("easterDay_DayOfYear_int", -1);
			
			if (easterDate == -1) return null;
			
			int easterYear = prefs.getInt("easterDay_Year_int", Calendar.getInstance().get(Calendar.YEAR));
			Calendar tempCal = Calendar.getInstance(); 
			tempCal.set(Calendar.YEAR, easterYear);
			tempCal.set(Calendar.DAY_OF_YEAR, easterDate);
			tempCal.set(Calendar.HOUR_OF_DAY, 0);
			tempCal.set(Calendar.MINUTE, 0);
			tempCal.set(Calendar.SECOND, 1);

			return tempCal;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int getTranslation() {
		String strLanguage = PreferenceManager.getDefaultSharedPreferences(baseContext).getString("bibleLanguage", "-1");

		if (strLanguage.equals("1"))
			return EasterApplication.TRANSLATION_SFB;
		else if (strLanguage.equals("2"))
			return EasterApplication.TRANSLATION_NET;
		else {
			String lang=Locale.getDefault().getLanguage();
			if (lang.equals("sv")) 
				return EasterApplication.TRANSLATION_SFB;
		}
		
		return EasterApplication.TRANSLATION_NET;
	}
	
	public void setTranslation(int translation) {
		Editor ed = PreferenceManager.getDefaultSharedPreferences(baseContext).edit(); 
		ed.putString("bibleLanguage", String.format("%d", translation));
		ed.commit();
	}
	
	Cursor getTexts()  
	{
	    Cursor cursor = null;
    	Calendar now = Calendar.getInstance();
    	cursor = this.getAllBibleTexts(now.getTimeInMillis());
        	
		return cursor;
		
	}

	long getNextEventTime(long currentTime)
	{
    	Cursor cursor = getNextEvent(currentTime);  
    	if (cursor.getCount()>0) {
        	cursor.moveToFirst();
    		int cTimeStamp= cursor.getColumnIndex(C_TIMESTAMP);
    		long ret = cursor.getLong(cTimeStamp) + getEasterDayLong();
       		int cHour = cursor.getColumnIndex(C_HOUR);
       		int hourOfDay = cursor.getInt(cHour);
    		cursor.close();
    		Calendar tempCal = Calendar.getInstance(); 
    		tempCal.setTimeInMillis(ret); 
    		tempCal.set(Calendar.HOUR_OF_DAY, hourOfDay); // To compensate for DST!
/*       		Log.d(TAG, "Next Event: " + tempCal.get(Calendar.DAY_OF_YEAR) + " - " +
       				String.format("%02d", tempCal.get(Calendar.HOUR_OF_DAY)) + ":" +
       				String.format("%02d", tempCal.get(Calendar.MINUTE)) + ":" +
       				String.format("%02d", tempCal.get(Calendar.SECOND))); */
    		long ret2 = tempCal.getTimeInMillis();
    		return ret2;
    	}
    	cursor.close();

    	Calendar now = Calendar.getInstance();
    	Calendar nextEasterDay = getNextEasterDay(now.getTime());
    	if ((nextEasterDay.getTimeInMillis() - now.getTimeInMillis()) < (EasterApplication.DAY_IN_MILLIS * 20)) { // three weeks before Easter
    		setEasterDate(nextEasterDay);
    		setAllAsUnRead();
    	}
    	
		return currentTime + (EasterApplication.DAY_IN_MILLIS); // Wait LONG time; until tomorrow!
		
	}

	
	Calendar getNextEasterDay(Date now) {
		
		if (now.after(new Date(114,3,20)))
			return new GregorianCalendar(2015, Calendar.APRIL, 5); 
		else if (now.after(new Date(113,2,31)))
			return new GregorianCalendar(2014, Calendar.APRIL, 20); 
		else if (now.after(new Date(112,3,8)))
			return new GregorianCalendar(2013, Calendar.MARCH, 31); 
		else if (now.after(new Date(111,3,2)))
			return new GregorianCalendar(2012, Calendar.APRIL,8); 

		return new GregorianCalendar(2011, Calendar.APRIL,2); 

	}

	public long getEasterDayLong() {
		Calendar easterDate = getEasterDate();
   		
		if (easterDate != null)
			return easterDate.getTimeInMillis();		
		else 
			return -1;
	}

	
	//////////////////////////////////////////////////////
	//////////// Preparing for database version below...

	static final int VERSION = 1;
	static final String DATABASE = "BibleTexts.db";
	static final String TABLE_NAME= "timedtext";

	public static final String C_ID = "_id";
	public static final String C_DAY = "DAY";
	public static final String C_HOUR = "HOUR";
	public static final String C_MINUTE = "MINUTE";
	public static final String C_BOOK_NAME = "BOOK_NAME"; 
	public static final String C_CHAP_VERSE = "CHAP_VERSE";
	public static final String C_TEXT= "TEXT";
	public static final String C_LOCATION_TEXT = "LOCATION_TEXT";
	public static final String C_READ = "READ";
	public static final String C_TIMESTAMP = "TIMESTAMP";
	public static final String C_BIBLE_LOC= "BIBLE_LOC";

	private static final String[] DB_TEXT_COLUMNS = { C_TEXT, C_READ, C_DAY, C_HOUR, C_MINUTE, C_BOOK_NAME, C_CHAP_VERSE, C_LOCATION_TEXT, C_ID, C_BIBLE_LOC };

	  // DbHelper implementations
	class DbHelper extends SQLiteOpenHelper {
		Context cntxt;
		
		public DbHelper(Context context) {
			super(context, DATABASE, null, VERSION);
			cntxt = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
//			Log.i(TAG, "Creating database: " + DATABASE);
			
			// Set correct version number on database if it was just created
			if (createdNewDatabase == true) {
//				Log.d(TAG, "Setting version to " + VERSION);
				db.setVersion(VERSION);
			}

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			Log.d(TAG, "Old version: " + oldVersion + ", New version: " + newVersion);
			this.onCreate(db);
			try {
				createDatabase(baseContext);
			} catch (IOException e) {
//				Log.d(TAG,"Error when upgrading database! " + e.toString());
				e.printStackTrace();
			}
//			Log.e(TAG, "Re-created database!");

		}
		
	}

	  
	  private final DbHelper dbHelper; 


	  public void close() { 
	    this.dbHelper.close();
	  }

	  private long getCurrentTimeRel_DSTcomp(long currentTime) {
		  long currentTimeRel = currentTime - getEasterDayLong();
		  //Compensate for DST!
		  Calendar cal1=Calendar.getInstance();
		  cal1.setTimeInMillis(currentTime);
		  Calendar cal2=Calendar.getInstance();
		  cal2.setTimeInMillis(getEasterDayLong());
		  int dstOffset1 = cal1.get(Calendar.DST_OFFSET);
		  int dstOffset2 = cal2.get(Calendar.DST_OFFSET);
		  currentTimeRel += (dstOffset1 - dstOffset2);
		  return currentTimeRel;
	  }
	  
	  private Cursor getNextEvent(long currentTime) {
		  long currentTimeRel = getCurrentTimeRel_DSTcomp(currentTime);
		  SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		  String[] selectColumns = { C_TIMESTAMP, C_HOUR };
		  return db.query(true, TABLE_NAME,  selectColumns, "TIMESTAMP > " + currentTimeRel, null, null, null, C_TIMESTAMP, "1");
	  }


		public String getBibleSourceString(Context context) {

			if (getTranslation() == EasterApplication.TRANSLATION_SFB)
				return context.getString(R.string.bibleSource);
			else
				return context.getString(R.string.bibleSourceNET);
		}

		public String getBibleSourceStringShort(Context context) {

			if (getTranslation() == EasterApplication.TRANSLATION_SFB)
				return context.getString(R.string.bibleTranslationShortSFB);
			else
				return context.getString(R.string.bibleTranslationShortNET);
		}



	  private String[] getDbTextColumns() {
		  return DB_TEXT_COLUMNS;
	  }
	  
	  
	/**
	   *
	   * @return Cursor where the columns are _id, created_at, user, txt
	   */
	  public Cursor getBibleTextsById(int id) {  
	    return db_query(getDbTextColumns() , C_ID + " = " + id); 
	  } 

	private Cursor db_query(String[] columns, String select) {
	    SQLiteDatabase db = this.dbHelper.getReadableDatabase();
	    String sql_string;
	    if (getTranslation() == EasterApplication.TRANSLATION_SFB) {
		    sql_string = "select _id, DAY, HOUR, MINUTE, biblebooks.BOOK_NAME, CHAP_VERSE, TEXT, locations.LOCATION_TEXT, READ, TIMESTAMP, BIBLE_LOC " + 
		    			"from timedtext left join locations on timedtext.LOCATION_ID = locations.LOCATION_ID left join biblebooks on timedtext.BOOK_ID = biblebooks.BOOK_ID " +
		    			"WHERE " + select + " ORDER BY " + C_TIMESTAMP; 
	    }
	    else {
		    sql_string = "select _id, DAY, HOUR, MINUTE, biblebooks.BOOK_NAME_EN AS BOOK_NAME, CHAP_VERSE, TEXT_EN AS TEXT, locations.LOCATION_TEXT_EN AS LOCATION_TEXT, READ, TIMESTAMP, BIBLE_LOC " +
			"from timedtext left join locations on timedtext.LOCATION_ID = locations.LOCATION_ID left join biblebooks on timedtext.BOOK_ID = biblebooks.BOOK_ID " +
			"WHERE " + select + " ORDER BY " + C_TIMESTAMP;
	    }
	    return db.rawQuery(sql_string, null);
//	    return db.query(getTableViewName(),  columns , select, null, null, null, C_TIMESTAMP); 
	}

	public Cursor getPrevBibleTextsById(int id) {
		  Cursor tmpCursor = db_query(getDbTextColumns(), C_ID + " < " + id); 
		  if (tmpCursor != null && tmpCursor.getCount() > 0) {
			  return tmpCursor;
		  }
		  return null;
	  }

	  public Cursor getNextBibleTextsById(int id) {
		  Cursor tmpCursor = db_query(getDbTextColumns(), C_ID + " > " + id); 
		  if (tmpCursor != null && tmpCursor.getCount() > 0) {
			  return tmpCursor;
		  }
		  return null;
	  }
		  

	  /**
	   *
	   * @return Cursor where the columns are _id, created_at, user, txt
	   */
	  public Cursor getAllBibleTexts(long now) { 
	    try {
			long currentTimeRel = getCurrentTimeRel_DSTcomp(now);
			return db_query(getDbTextColumns(), "TIMESTAMP < " + currentTimeRel);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	  } 




	public void setAsRead(long id) {
		ContentValues val = new ContentValues();
	    val.put(C_READ, "1");

	    SQLiteDatabase db = this.dbHelper.getReadableDatabase();
	    db.update(TABLE_NAME, val, "_id = " + id, null);
	}
	  
	public void setAllAsUnRead() {
		ContentValues val = new ContentValues();
	    val.put(C_READ, "0");

	    SQLiteDatabase db = this.dbHelper.getReadableDatabase();
	    db.update(TABLE_NAME, val, null, null);
	}

	public boolean isAllRead() {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		String[] selectColumns = { C_TIMESTAMP, C_HOUR, C_READ };
		Cursor cur=db.query(true, TABLE_NAME,  selectColumns, C_READ + " == 0", null, null, null, C_TIMESTAMP, "1");
		if (cur != null) {
			int count = cur.getCount();
			cur.close();
			if (count == 0)
				return true;
			
		}

		return false;
	}

	public boolean isAllReadNow() {
		long now = Calendar.getInstance().getTimeInMillis();
	    long currentTimeRel = getCurrentTimeRel_DSTcomp(now); 
	    
		String[] selectColumns = { C_TIMESTAMP, C_HOUR, C_READ };
	    Cursor cur = db_query(selectColumns, "TIMESTAMP < " + currentTimeRel + " AND " + C_READ + " == 0"); 
		
		if (cur != null) {
			int count = cur.getCount();
			cur.close();
			if (count == 0)
				return true;
			
		}

		return false;
	}

	public int getTextCount() {
		Cursor cur = this.getAllBibleTexts(System.currentTimeMillis());
		if (cur == null) return -1;
		int count = cur.getCount();
		cur.close();
		return count;
	}

	
}
