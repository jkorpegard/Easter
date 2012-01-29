package se.hcjb.easterday;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class Start extends Activity implements OnClickListener {
	private boolean dateSet = false;
	EasterApplication easterApp = null;
	boolean allRead = false;
	private static final String TAG = "EasterLog:StartView";
    

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
        Button v3 = (Button) findViewById(R.id.button3);
        Button v4 = (Button) findViewById(R.id.button4);
        Button v5 = (Button) findViewById(R.id.button5);
        LinearLayout ll0 = (LinearLayout) findViewById(R.id.linearLayout0);
        if (allRead) {
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
          Log.d(TAG, "Prefs selected in menu");
          startActivity(new Intent(this, PrefsActivity.class));  
          Log.d(TAG, "startActivity launced");
        break;
      case R.id.itemRestartWeek:
    	  easterApp.bibleTexts.setEasterDate(null);
    	  this.easterApp.bibleTexts.setAllAsUnRead();
    	  Log.d(TAG, "Read-flag cleared for all items");
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
	    	else
	    		startActivity(new Intent(this, SetDateActivity.class));
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
