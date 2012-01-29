package se.hcjb.easterday;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

public class SetDateActivity extends ActionBarActivity {
	private TextView mDateDisplay;
	private Button mPickDate;
	private int mYear;
	private int mMonth;
	private int mDay;

	static final int DATE_DIALOG_ID = 0; 
	
	static final String TAG = "EasterLog:SetDateActivity";
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_date);

        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
        mPickDate = (Button) findViewById(R.id.pickDate);
        mPickDate.setOnClickListener((android.view.View.OnClickListener) this); 

        ((Button) findViewById(R.id.set_date_next)).setOnClickListener(this);
        		
        Calendar now = Calendar.getInstance();
        now.setFirstDayOfWeek(Calendar.MONDAY);
        
        Calendar c = (Calendar) now.clone();
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        
        while (c.getTimeInMillis() < (now.getTimeInMillis() + (EasterApplication.DAY_IN_MILLIS * 4))) { // At least 4 days left to Easter Day
        	c.add(Calendar.DAY_OF_YEAR, 7);
        }        
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        updateDisplay();
		findViewById(R.id.vbtActionBarNext).setOnClickListener(this);

    }
    
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
    	if ((v.getId() == R.id.set_date_next) || (v.getId() == R.id.vbtActionBarNext)) {
    		Calendar cal = Calendar.getInstance();
    		cal.set(Calendar.YEAR, mYear);
    		cal.set(Calendar.MONTH, mMonth);
    		cal.set(Calendar.DAY_OF_MONTH, mDay);
    		
    		
    		EasterApplication easterApp = (EasterApplication) getApplication();
    		easterApp.bibleTexts.setEasterDate(cal);
    		easterApp.initTimer(); // If first event is before next timer hit
    		if (easterApp.bibleTexts.getTextCount() > 0)
    			startActivity(new Intent(this, Easter.class)); 
    		else
	    		startActivity(new Intent(this, StartedActivity.class));
    	}
    	else if (v.getId() == R.id.pickDate)
            showDialog(DATE_DIALOG_ID);
    }
    
    
    private void updateDisplay() {
        mDateDisplay.setText("" + String.format("%d", mYear) + "-" + String.format("%02d", mMonth+1) + "-" + String.format("%02d", mDay));
        Log.d(TAG,"Date set: " + mYear + "-" + (mMonth + 1) + "-" + mDay);
    }
    
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                	
                    Calendar c = Calendar.getInstance();
                    c.setFirstDayOfWeek(Calendar.MONDAY);
                    c.set(Calendar.YEAR, year);
            		c.set(Calendar.MONTH, monthOfYear);
            		c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                    updateDisplay();
                }
            };    
            
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
	        case DATE_DIALOG_ID:
	            return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
        }
        return null;
    }

}
