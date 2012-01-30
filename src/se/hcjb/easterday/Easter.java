package se.hcjb.easterday;

import java.io.IOException;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class Easter extends ListActivity implements OnClickListener /*, OnItemClickListener*/ {
	  Button startButton; 
	  Button stopButton;
	  Cursor listViewCursor = null;
	  Cursor cursor;

	  MySimpleCursorAdapter cursorAdapter;  
	  static final String[] FROM = { BibleTexts.C_HOUR, BibleTexts.C_LOCATION_TEXT, BibleTexts.C_READ};
	  static final int[] TO = { R.id.label1, R.id.label2, R.id.ivstatus}; 
	  
	  ArrayAdapter<String> adapter;
	  EasterApplication easterApp = null;

	private static final String TAG = "EasterLog:EasterView";
	
	private void startBibleText(int id)
	{
	    Editor e = PreferenceManager.getDefaultSharedPreferences(this).edit(); 
		e.putInt("lastReadId", id);
		e.commit();
		
		
    	// TODO: Pass ID in the intent instead of global variable!!!
		easterApp.id = id;
        startActivity(new Intent(this, ViewBibleTextActivity.class));
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        easterApp = ((EasterApplication) getApplication());
        setContentView(R.layout.main);
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
    	BibleTexts bt = easterApp.bibleTexts;
    	
	    findViewById(R.id.vbtHome).setOnClickListener(this);
        findViewById(R.id.vbtActionBarNext).setOnClickListener(this);

    	boolean noDatabase = false;
	    int newPosition = -1;
	    
    	
    	int cnt=-1;
		try {
			listViewCursor = bt.getTexts();
			startManagingCursor(listViewCursor);
			cnt = listViewCursor.getCount();

			int maxId = 0;
			if (cnt>0) {
				newPosition=0;
				listViewCursor.moveToLast();
				maxId = listViewCursor.getInt(listViewCursor.getColumnIndex(BibleTexts.C_ID));
			}
			Editor ed = PreferenceManager.getDefaultSharedPreferences(this).edit(); 
			ed.putInt("maxId", maxId);
			ed.commit();

			listViewCursor.moveToFirst();
			while (!listViewCursor.isAfterLast()) {
				if (listViewCursor.getInt(listViewCursor.getColumnIndex(BibleTexts.C_READ))==0)
					break;
				newPosition++;
				listViewCursor.moveToNext();
			}
		} catch (Exception e1) {
			Log.e(TAG, "Could not find database! " + e1);
			e1.printStackTrace();
			noDatabase = true;
		}

    	if (noDatabase) {
    		try {
				this.easterApp.bibleTexts.createDatabase(getBaseContext());
			} catch (IOException e) {
				Log.e(TAG, "Coud not create database... " + e.toString());
				e.printStackTrace();
			}
    	}
    	
        try {
			cursorAdapter = new MySimpleCursorAdapter(this, R.layout.row, listViewCursor, FROM, TO);
		} catch (Exception e) {
			Log.e(TAG, "Could not set adapter... " + e.toString());
			e.printStackTrace();
		}
        setListAdapter(cursorAdapter);	 
	    
        if (newPosition > -1) {
        	this.getListView().setSelection(newPosition);
        	this.getListView().setItemChecked(newPosition,true);        	
        }
        else {
		    this.getListView().setSelection(this.getListView().getCount()-1);
		    this.getListView().setItemChecked(this.getListView().getCount()-1,true);
        }
        
	    
	    // Set commercial and make it clickable
		((EasterApplication) getApplication()).activateCommercial(this, R.id.textView3);
        
	    // Always re-init timer when starting (or resuming) application...
	    easterApp.initTimer();
	    
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        LinearLayout ll0 = (LinearLayout) findViewById(R.id.linearLayout0);
        if (prefs.getBoolean("isAllRead", false)){
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        		ll0.setBackgroundDrawable(getResources().getDrawable(R.drawable.emptytomb));
        	else
        		ll0.setBackgroundDrawable(getResources().getDrawable(R.drawable.emptytomb_landscape));        		
        }
    }
    
    public void onClick(View v) {
        if (v.getId() == R.id.vbtHome) {
		    final Intent intent = new Intent(this, Start.class);
		    intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    startActivity (intent);
    	}
        else if (v.getId() ==R.id.vbtActionBarNext) {
        	int gotoBibleText=-1;
			listViewCursor.moveToFirst();
			while (!listViewCursor.isAfterLast()) {
				if (listViewCursor.getInt(listViewCursor.getColumnIndex(BibleTexts.C_READ))==0) {
					gotoBibleText = listViewCursor.getInt(listViewCursor.getColumnIndex(BibleTexts.C_ID));
					break;
				}
				listViewCursor.moveToNext();
			}
        	if (gotoBibleText > -1)
        		startBibleText(gotoBibleText);
        	else {
    			int cnt = listViewCursor.getCount();
    			if (cnt>0) {
    				listViewCursor.moveToLast();
    				startBibleText(listViewCursor.getInt(listViewCursor.getColumnIndex(BibleTexts.C_ID)));
    			}
        	}
        }
    }

    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
	    Editor ed = PreferenceManager.getDefaultSharedPreferences(this).edit(); 
		ed.putInt("lastPosition", position);
		ed.commit();

    	Cursor cursor = easterApp.bibleTexts.getBibleTextsById((int) id);
    	startManagingCursor(cursor);
    	cursor.moveToFirst();
		
		startBibleText((int) id);

	}    
    

}