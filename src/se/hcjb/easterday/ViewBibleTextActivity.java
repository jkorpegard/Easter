package se.hcjb.easterday;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewBibleTextActivity extends Activity implements OnClickListener{
	private static final String TAG = "EasterLog:ViewBibleTextActivity";
	private int id = -1;
	Toast myToast = null; 
	private EasterApplication eApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewbibletext);
	    
        eApp = (EasterApplication) getApplication();
        try {
		    id = PreferenceManager.getDefaultSharedPreferences(this).getInt("lastReadId", -1); // TODO: Replace with intent data!
		} catch (Exception e) {
			id = -1;
		}

		if (id==-1) {
			id = eApp.id;
		    Editor ed = PreferenceManager.getDefaultSharedPreferences(this).edit(); 
			ed.putInt("lastReadId", id);
			ed.commit();
		}

		Log.d(TAG, "Started OnCreate with id = " + id);
	    findViewById(R.id.vbtNext).setOnClickListener(this);
	    findViewById(R.id.vbtPrev).setOnClickListener(this);
	    findViewById(R.id.vbtHome).setOnClickListener(this);
	    findViewById(R.id.viewBibleTextTranslation).setOnClickListener(this);

	    Cursor cursor = eApp.bibleTexts.getBibleTextsById(id);
    	startManagingCursor(cursor);
    	cursor.moveToFirst();

        int newId = setTextViewsContent(cursor);
        
		if ( newId==1 ) {
			((ImageView) findViewById(R.id.vbtPrev)).setVisibility(View.INVISIBLE);
		}

        if ((newId >= PreferenceManager.getDefaultSharedPreferences(this).getInt("maxId", 0)) && (! eApp.bibleTexts.isAllRead()) ) {
        	((ImageView) findViewById(R.id.vbtNext)).setVisibility(View.INVISIBLE);
		}

    }
    
    /* Set text boxes to the content of the cursor at the current position
     * 
     */
	private int setTextViewsContent(Cursor cursor) {
        //EasterApplication eApp = (EasterApplication) getApplication();
		TextView tView1 = (TextView) findViewById(R.id.textView1);
        TextView tView2 = (TextView) findViewById(R.id.textView2);

        
		int cBibleLoc= cursor.getColumnIndex(BibleTexts.C_BIBLE_LOC);
		int cText= cursor.getColumnIndex(BibleTexts.C_TEXT);
		int cId = cursor.getColumnIndex(BibleTexts.C_ID);
		int cLocation = cursor.getColumnIndex(BibleTexts.C_LOCATION_TEXT);
		id = cursor.getInt(cId);

		String text = cursor.getString(cText);
		String datestring = BibleTexts.makeDateString(this, cursor);

		String bibleLoc = cursor.getString(cBibleLoc);
		
        tView1.setText(datestring);
        tView2.setText(text + "\n\n" + bibleLoc + eApp.bibleTexts.getBibleSourceString(getBaseContext()));
        
        ((TextView) findViewById(R.id.viewBibleTextTranslation)).setText(eApp.bibleTexts.getBibleSourceStringShort(getBaseContext()));
        
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView1);
        scrollView.scrollTo(0, 0);
        eApp.bibleTexts.setAsRead(id);
        
        if (myToast == null)
        	myToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
        
        myToast.setText(datestring + "\n" + cursor.getString(cLocation));
        myToast.show();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean wasAllRead = prefs.getBoolean("isAllRead", false);
	    Editor e = prefs.edit(); 
		e.putInt("lastReadId", id);
        
		eApp.activateCommercial(this, R.id.textView3);
	            
		if (eApp.bibleTexts.isAllRead() && (! wasAllRead) ) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.alert_last_text_text)
			       .setCancelable(false)
			       .setPositiveButton(R.string.alert_last_text_ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   ;
			           }
			       });
			AlertDialog alert = builder.create();		
			alert.show();

			e.putBoolean("isAllRead", true);
		}

		e.commit();
		return id;
	}

	private void refresh(){
		Cursor cursor = eApp.bibleTexts.getBibleTextsById(id);
   	    try {
			cursor.moveToFirst();
			setTextViewsContent(cursor); 
			cursor.close();
		} catch (Exception e) {
			Log.e(TAG, "Crached when refreshing!" + e.getMessage());
			e.printStackTrace();
			cursor.close();
		}
	}
	
    public void onClick(View v) {
    	int count=-1;
    	if (v.getId() == R.id.vbtNext ) {
    		if (eApp.bibleTexts.isAllRead() && (id == PreferenceManager.getDefaultSharedPreferences(this).getInt("maxId", 0))) {
    		    final Intent intent = new Intent(this, NextStepActivity.class);
    		    intent.setFlags (Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
    		    startActivity (intent);
    		}
    		((ImageView) findViewById(R.id.vbtPrev)).setVisibility(View.VISIBLE);
    		if (id >= PreferenceManager.getDefaultSharedPreferences(this).getInt("maxId", 0)) {
    			return;
    		}
    		Cursor cursor = eApp.bibleTexts.getNextBibleTextsById(id);
    		startManagingCursor(cursor);
    		if (cursor!=null && (count=cursor.getCount())>0) {
    			cursor.moveToFirst();
        		int newId = setTextViewsContent(cursor);
        		if ((newId >= PreferenceManager.getDefaultSharedPreferences(this).getInt("maxId", 0)) && 
        				(! eApp.bibleTexts.isAllRead()) ) {
        			((ImageView) findViewById(R.id.vbtNext)).setVisibility(View.INVISIBLE);
        		}
    		}
    	}
    	else if (v.getId() == R.id.vbtPrev) {

    		Cursor cursor = eApp.bibleTexts.getPrevBibleTextsById(id);
    		if (cursor!=null) {
    			startManagingCursor(cursor); 
    			if ((count = cursor.getCount()) > 0) {
    				cursor.moveToLast();
    				setTextViewsContent(cursor);
            		if (count<2) {
            			((ImageView) findViewById(R.id.vbtPrev)).setVisibility(View.INVISIBLE);
            		}
        			((ImageView) findViewById(R.id.vbtNext)).setVisibility(View.VISIBLE);
    			}
    		}
    	}
    	else if (v.getId() == R.id.vbtHome) {
		    final Intent intent = new Intent(this, Start.class);
		    intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    startActivity (intent);
    		
    	}
    	else if (v.getId() == R.id.viewBibleTextTranslation) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.select_bible_translation)
			       .setCancelable(false)
			       .setPositiveButton(R.string.bibleTranslationShortNET, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
				        	eApp.bibleTexts.setTranslation(EasterApplication.TRANSLATION_NET);
				        	refresh();
			           }
			       })
			       .setNegativeButton(R.string.bibleTranslationShortSFB, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
				        	eApp.bibleTexts.setTranslation(EasterApplication.TRANSLATION_SFB);
				        	refresh();
			           }
			       });
			AlertDialog alert = builder.create();		
			alert.show();
    		
    	}
    }

}
