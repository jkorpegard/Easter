package se.hcjb.easterday;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
//	static final String DEFAULT_EASTER_DAY_STRING = "-1";
//	private static final int DEFAULT_EASTER_DAY = -1;
	private static final String TAG = "EasterLog:BibleTexts";
// Replacing with full date instead of: int easterDay = DEFAULT_EASTER_DAY;
/////////////////////////////////	Date easterDate = null;
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
				Log.e(TAG, "Re-created database!");

			}
		} catch (Exception e) {
			Log.e(TAG, "Caught Error when creating database: " +e);
			Log.e(TAG, "Stack trace: " + e.getStackTrace().toString());
		}
	    this.dbHelper = new DbHelper(context);
	    Log.i(TAG, "Initialized data");
	}
	
	public void createDatabase(Context cntxt) throws IOException {
		Log.d(TAG, "started createDatabase... ");
		  
		InputStream assetsDB = cntxt.getAssets().open("BibleTexts.db");
		Log.d(TAG, "Opened source file... ");
		OutputStream dbOut = new FileOutputStream(DB_DESTINATION);
		Log.d(TAG, "Opened destination file... ");
		
		byte[] buffer = new byte[1024];
		int length;
		Log.d(TAG, "Starting to copy file... ");
		while ((length = assetsDB.read(buffer))>0){
			dbOut.write(buffer, 0, length);
		}
		Log.d(TAG, "Done copying file... ");
		 
		dbOut.flush();
		dbOut.close();
		assetsDB.close();
		Log.d(TAG, "Closed and flushed all files... ");
		
		createdNewDatabase = true;
	    Log.d(TAG, "Set flag createdNewDatabase to true");
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
	
	Cursor getTexts()  
	{
	    Cursor cursor = null;
    	Calendar now = Calendar.getInstance();
    	cursor = this.getAllBibleTexts(now.getTimeInMillis());
        	
		return cursor;
		
	}

	long getNextEventTime(long currentTime)
	{
/////////////////////////////////		easterDate = getEasterDate();

//			Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>EasterDay_DayOfYear = "+ easterDay);
		
    	Cursor cursor = getNextEvent(currentTime);  
    	if (cursor.getCount()>0) {
        	cursor.moveToFirst();
    		int cTimeStamp= cursor.getColumnIndex(C_TIMESTAMP);
//	    		Log.d(TAG, "+++++++++++++++ timestamp: " + (cursor.getLong(cTimeStamp)/1000));////////////////
    		long ret = cursor.getLong(cTimeStamp) + getEasterDayLong();
       		int cHour = cursor.getColumnIndex(C_HOUR);
       		int hourOfDay = cursor.getInt(cHour);
    		cursor.close();
    		Calendar tempCal = Calendar.getInstance(); 
    		tempCal.setTimeInMillis(ret); 
    		tempCal.set(Calendar.HOUR_OF_DAY, hourOfDay); // To compensate for DST!
       		Log.d(TAG, "Next Event: " + tempCal.get(Calendar.DAY_OF_YEAR) + " - " +
       				String.format("%02d", tempCal.get(Calendar.HOUR_OF_DAY)) + ":" +
       				String.format("%02d", tempCal.get(Calendar.MINUTE)) + ":" +
       				String.format("%02d", tempCal.get(Calendar.SECOND))); 
    		long ret2 = tempCal.getTimeInMillis();
//	    		tempCal.setTimeInMillis(ret - (24*60*60*1000)); ///////////////////
//	       		Log.d(TAG, "Diff: " + (ret2 - ret));
    		return ret2;
    	}
    	cursor.close();

    	Calendar now = Calendar.getInstance();
    	Calendar nextEasterDay = getNextEasterDay(now.getTime());
    	if ((nextEasterDay.getTimeInMillis() - now.getTimeInMillis()) < (1000 * 60 * 60 * 24 * 21)) // three weeks before Easter
    		this.setEasterDate(nextEasterDay);
    	
    	// Move easterDay forward, then try again in 10 seconds...
/* No; let be for now... Get next Easter Day instead later        	Editor prefsEdit = prefs.edit();
        	// TODO: Should be replaced by a more intelligent move forward with the coming years easter day date...
        	int newEasterDay = easterDay  + 7;
        	// TODO: loop until easterday > current day of year... (slightly better temporary solution)
//        	newEasterDay = getNextEasterDay(Calendar.getInstance().getTime());
        	prefsEdit.putString("easterDay_DayOfYear", String.format("%d", newEasterDay));
        	prefsEdit.commit();        	
        	setAllAsUnRead(); */
//    		Log.d(TAG, "Reached end of texts... Change easter day to " + prefs.getString("easterDay_DayOfYear", "<invalid>"));
		return currentTime + (1000 * 60 * 60 * 24); // 100000000; // Wait LONG time!
		
	}

	
	private Calendar getNextEasterDay(Date now) {
		
		if (now.after(new Date(114,3,20)))
			return new GregorianCalendar(2015, Calendar.APRIL, 5); // 2015-04-05 newDate = new Date(115,3,5);
		else if (now.after(new Date(113,2,31)))
			return new GregorianCalendar(2014, Calendar.APRIL, 20); // 2015-04-05 newDate = new Date(114,3,20);
		else if (now.after(new Date(112,3,8)))
			return new GregorianCalendar(2013, Calendar.MARCH, 31); // 2015-04-05 newDate = new Date(113-2-31);
		else if (now.after(new Date(111,3,2)))
			return new GregorianCalendar(2012, Calendar.APRIL,8); // 2015-04-05 newDate = new Date(112,3,8);

		return new GregorianCalendar(2011, Calendar.APRIL,2); // 2015-04-05 newDate = new Date(2011,3,2); // Must be before 2011-04-02
		
	}

	public long getEasterDayLong() {
		Calendar easterDate = getEasterDate();
//		Log.i(TAG, "////////////////////EasterDay_DayOfYear = "+ easterDay);
   		
		if (easterDate != null)
			return easterDate.getTimeInMillis();		
		else 
			return -1;
	}

	
	//////////////////////////////////////////////////////
	//////////// Preparing for database version below...

	static final int VERSION = 1;
	static final String DATABASE = "BibleTexts.db";
	static final String TABLE_VIEW = "all_texts";
	static final String TABLE_VIEW_EN = "all_texts_en";
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
//	public static final String C_DATESTRING= "DATESTRING"; // TODO: Remove DATESTRING from database!!!
	public static final String C_BIBLE_LOC= "BIBLE_LOC";

//	private static final String GET_ALL_ORDER_BY = C_DAY + "," + C_HOUR + "," + C_MINUTE + " DESC";

//	  private static final String[] MAX_ID_AT_COLUMNS = { "max(" + C_ID + ")" };

	private static final String[] DB_TEXT_COLUMNS = { C_TEXT, C_READ, C_DAY, C_HOUR, C_MINUTE, C_BOOK_NAME, C_CHAP_VERSE, C_LOCATION_TEXT, C_ID, C_BIBLE_LOC };

	  // DbHelper implementations
	class DbHelper extends SQLiteOpenHelper {
		Context cntxt;
		
		public DbHelper(Context context) {
			super(context, DATABASE, null, VERSION);
			cntxt = context;
			Log.i(TAG, "DbHelper initiated!");
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, "Creating database: " + DATABASE);
			Log.i(TAG, "... or actually not creating!!!");
			
			// Set correct version number on database if it was just created
			if (createdNewDatabase == true) {
				Log.d(TAG, "Setting version to " + VERSION);
				db.setVersion(VERSION);
			}

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//Log.i(TAG, "Dropping table: " + TABLE_VIEW);
			//db.execSQL("drop table " + TABLE_VIEW);
			Log.d(TAG, "Old version: " + oldVersion + ", New version: " + newVersion);
			this.onCreate(db);
			try {
				createDatabase(baseContext);
			} catch (IOException e) {
				Log.d(TAG,"Error when upgrading database! " + e.toString());
				e.printStackTrace();
			}
			Log.e(TAG, "Re-created database!");

		}
		
	}

	  
	  private final DbHelper dbHelper; 


	  public void close() { 
	    this.dbHelper.close();
	  }

/*	  public void insertOrIgnore(ContentValues values) {  
	    Log.d(TAG, "insertOrIgnore on " + values);
	    SQLiteDatabase db = this.dbHelper.getWritableDatabase();  
	    try {
	      db.insertWithOnConflict(TABLE_VIEW, null, values,
	          SQLiteDatabase.CONFLICT_IGNORE);  
	    } finally {
	      db.close(); 
	    }
	  }*/

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
		  return db.query(true, getTableViewName(),  selectColumns, "TIMESTAMP > " + currentTimeRel, null, null, null, C_TIMESTAMP, "1");
	  }


		public String getBibleSourceString(Context context) {
			String strLanguage = PreferenceManager.getDefaultSharedPreferences(baseContext).getString("bibleLanguage", "-1");

			if (strLanguage.equals("1"))
				return context.getString(R.string.bibleSource);
			else
				return context.getString(R.string.bibleSourceNET);
		}

	  
	  private String getTableViewName() {
		  String strLanguage = PreferenceManager.getDefaultSharedPreferences(baseContext).getString("bibleLanguage", "-1");

		  if (strLanguage.equals("1"))
			  return TABLE_VIEW;
		  else
			  return TABLE_VIEW_EN;
	  }


	  private String[] getDbTextColumns() {
		  return DB_TEXT_COLUMNS;
	  }
	  
	/**
	   *
	   * @return Cursor where the columns are _id, created_at, user, txt
	   */
	  public Cursor getBibleTextsById(int id) {  
	    SQLiteDatabase db = this.dbHelper.getReadableDatabase();
	    return db.query(getTableViewName(),  getDbTextColumns() , C_ID + " = " + id, null, null, null, C_TIMESTAMP); 
	  } 

	public Cursor getPrevBibleTextsById(int id) {
		  SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		  Cursor tmpCursor = db.query(getTableViewName(),  getDbTextColumns(), C_ID + " < " + id, null, null, null, C_TIMESTAMP); 
		  if (tmpCursor != null && tmpCursor.getCount() > 0) {
			  return tmpCursor;
		  }
		  return null;
	  }

	  public Cursor getNextBibleTextsById(int id) {
		  SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		  Cursor tmpCursor = db.query(getTableViewName(),  getDbTextColumns(), C_ID + " > " + id, null, null, null, C_TIMESTAMP); 
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
	    SQLiteDatabase db = this.dbHelper.getReadableDatabase();
	    //long currentTimeRel = now - getEasterDayLong();
	    long currentTimeRel = getCurrentTimeRel_DSTcomp(now);
	    return db.query(getTableViewName(),  getDbTextColumns(), "TIMESTAMP < " + currentTimeRel, null, null, null, C_TIMESTAMP); 
	  } 




	public void setAsRead(long id) {
		Log.d(TAG, "Setting " + id + " as read in database");
		ContentValues val = new ContentValues();
	    val.put(C_READ, "1");

	    SQLiteDatabase db = this.dbHelper.getReadableDatabase();
	    db.update(TABLE_NAME, val, "_id = " + id, null);
	}
	  
	public void setAllAsUnRead() {
		Log.d(TAG, "Setting all as unread in database");
		ContentValues val = new ContentValues();
	    val.put(C_READ, "0");

	    SQLiteDatabase db = this.dbHelper.getReadableDatabase();
	    db.update(TABLE_NAME, val, null, null);
	}

	public boolean isAllRead() {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		String[] selectColumns = { C_TIMESTAMP, C_HOUR, C_READ };
		Cursor cur=db.query(true, getTableViewName(),  selectColumns, C_READ + " == 0", null, null, null, C_TIMESTAMP, "1");
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
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
	    //long currentTimeRel = now - getEasterDayLong();
	    long currentTimeRel = getCurrentTimeRel_DSTcomp(now);
	    
		String[] selectColumns = { C_TIMESTAMP, C_HOUR, C_READ };
	    Cursor cur = db.query(getTableViewName(),  selectColumns, "TIMESTAMP < " + currentTimeRel + " AND " + C_READ + " == 0", null, null, null, C_TIMESTAMP); 
		
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
		int count = cur.getCount();
		cur.close();
		return count;
	}

	
}
