package se.hcjb.easterday;

import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.widget.TextView;

public class StartedActivity extends ActionBarActivity {
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.started);
        
        Calendar now = Calendar.getInstance();
        EasterApplication eApp = (EasterApplication) getApplication();
        
        long nextTextMillis = eApp.bibleTexts.getNextEventTime(now.getTimeInMillis());
        Date nextTextDate = new Date(nextTextMillis);
        long millisLeft = nextTextMillis - now.getTimeInMillis();
        
        if (now.get(Calendar.DATE) == nextTextDate.getDate())
        	((TextView) findViewById(R.id.textViewNumDays)).setText(R.string.started_today);
        else if (millisLeft > (2 * EasterApplication.DAY_IN_MILLIS))
        	((TextView) findViewById(R.id.textViewNumDays)).setText(
        			getString(R.string.started_number_days1) + " " + 
        			(millisLeft/EasterApplication.DAY_IN_MILLIS) + " " + getString(R.string.started_number_days2));
        else 
        	((TextView) findViewById(R.id.textViewNumDays)).setText(R.string.started_tomorrow);

	    eApp.activateCommercial(this, R.id.textView3);
    }


}
