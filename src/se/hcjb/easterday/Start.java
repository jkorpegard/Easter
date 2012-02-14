package se.hcjb.easterday;


import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Start extends Activity implements OnClickListener {
	private boolean dateSet = false;
	EasterApplication easterApp = null;
	boolean allRead = false;
//	private static final String TAG = "EasterLog:StartView";
    

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        easterApp = ((EasterApplication) getApplication());
        
        setContentView(R.layout.start);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);

        findViewById(R.id.vbtActionBarNext).setOnClickListener(this);
	    
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    int appMode = prefs.getInt("applicationMode", EasterApplication.APP_MODE_NOT_STARTED);
	    if (appMode == EasterApplication.APP_MODE_STARTED) {
	    	((Button) findViewById(R.id.button2)).setText(R.string.continue_easter_button);
	    	startActivity(new Intent(this, Easter.class));
	    }
	    

	}
	
    @Override
    protected void onResume(){
    	super.onResume();
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    this.allRead = prefs.getBoolean("isAllRead", false);
        Button v2 = (Button) findViewById(R.id.button2);
        Button v3 = (Button) findViewById(R.id.button3);
        Button v4 = (Button) findViewById(R.id.button4);
        Button v5 = (Button) findViewById(R.id.button5);
        LinearLayout ll0 = (LinearLayout) findViewById(R.id.linearLayout0);
        if (allRead) {
        	v2.setText(R.string.read_again_easter_button);
        	v3.setText(R.string.start_next_step);   v3.setVisibility(View.VISIBLE);
        	v4.setText(R.string.start_feedback);    v4.setVisibility(View.VISIBLE);
        	v5.setText(R.string.start_find_church); v5.setVisibility(View.VISIBLE);
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        		ll0.setBackgroundDrawable(getResources().getDrawable(R.drawable.emptytomb));
        	else
        		ll0.setBackgroundDrawable(getResources().getDrawable(R.drawable.emptytomb_landscape));        		
        }
        else {
        	v3.setText("Locked"); v3.setVisibility(View.INVISIBLE);
        	v4.setText("Locked"); v4.setVisibility(View.INVISIBLE);
        	v5.setText("Locked"); v5.setVisibility(View.INVISIBLE);
        	if (easterApp.bibleTexts.getEasterDate() == null)
        		v2.setText(R.string.start_easter_button);
        	else
        		v2.setText(R.string.continue_easter_button);
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        		ll0.setBackgroundDrawable(getResources().getDrawable(R.drawable.background));
        	else
        		ll0.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_landscape));        		
        }
		((EasterApplication) getApplication()).activateCommercial(this, R.id.textView3);
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.menu_main, menu);
      return true; 
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      case R.id.itemPrefs:
          startActivity(new Intent(this, PrefsActivity.class));  
        break;
      case R.id.itemRestartWeek:
    	  Calendar now = Calendar.getInstance();
    	  Calendar nextEasterDay = easterApp.bibleTexts.getNextEasterDay(now.getTime());
    	  if ((nextEasterDay.getTimeInMillis() - now.getTimeInMillis() ) < (EasterApplication.DAY_IN_MILLIS * 20)) {
    		  Toast.makeText(this, R.string.toastTextNoReset, Toast.LENGTH_LONG).show();
    		  break;
    	  }
    	  
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.confirmRestartWeek)
			       .setCancelable(false)
			       .setPositiveButton(R.string.confirmRestartWeekYes, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			         	  easterApp.bibleTexts.setEasterDate(null);
			        	  easterApp.bibleTexts.setAllAsUnRead();
			           }
			       })
			       .setNegativeButton(R.string.confirmRestartWeekNo, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   ; // Do nothing!
			           }
			       });
			AlertDialog alert = builder.create();		
			alert.show();
    	  
		  break;
      }

      return true;
    }
    
    
    
	@Override
	public void onClick(View v) {
    	if (v.getId() == R.id.button1) {
    		startActivity(new Intent (this, AboutActivity.class));
    	}
    	else if ((v.getId() == R.id.button2) || ((v.getId() == R.id.vbtActionBarNext) && !allRead) ) {
    	    if (easterApp.bibleTexts.getEasterDate() != null)
    	    	dateSet = true;
    	    else
    	    	dateSet = false;
    	    	
    	    // TODO Add FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS and other flags...???
	    	if (dateSet && (easterApp.bibleTexts.getTextCount() > 0))
	    		startActivity(new Intent(this, Easter.class));
	    	else if (dateSet)
	    		startActivity(new Intent(this, StartedActivity.class));
	    	else {
	    		Intent intent = new Intent(this, SetDateActivity.class);
	    		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	    		startActivity(intent);
	    	}
    	}
    	else if ((v.getId() == R.id.button3) || ((v.getId() == R.id.vbtActionBarNext) && allRead) ) {
    		if (this.allRead) startActivity(new Intent (this, NextStepActivity.class));
    	}
    	else if (v.getId() == R.id.button4) {
    		if (this.allRead) startActivity(new Intent (this, FeedbackActivity.class));
    	}
    	else if (v.getId() == R.id.button5) {
    		if (this.allRead) startActivity(new Intent (this, FindChurchActivity.class));
    	}
    	else if (v.getId() == R.id.button6) {
    		startActivity(new Intent (this, HCJBActivity.class));
    	}
	}
}
