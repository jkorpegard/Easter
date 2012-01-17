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

        // capture our View elements
        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
        mPickDate = (Button) findViewById(R.id.pickDate);
        // add a click listener to the button
        mPickDate.setOnClickListener((android.view.View.OnClickListener) this); 

        ((Button) findViewById(R.id.set_date_next)).setOnClickListener(this);
        		
        // get the current date
        Calendar now = Calendar.getInstance();
        now.setFirstDayOfWeek(Calendar.MONDAY);
        Calendar c = now;
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        while (c.getTimeInMillis() < now.getTimeInMillis())
        	c.add(Calendar.DAY_OF_YEAR, 7);
        
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // display the current date (this method is below)
        updateDisplay();
		findViewById(R.id.vbtActionBarNext).setOnClickListener(this);

// now in super class...	    ((EasterApplication) getApplication()).activateCommercial(this, R.id.textView3);
    }
    
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
    	if ((v.getId() == R.id.set_date_next) || (v.getId() == R.id.vbtActionBarNext)) {
    		Calendar cal = Calendar.getInstance();
    		cal.set(Calendar.YEAR, mYear);
    		cal.set(Calendar.MONTH, mMonth);
    		cal.set(Calendar.DAY_OF_MONTH, mDay);
    		
    		((EasterApplication) getApplication()).bibleTexts.setEasterDate(cal);
    		
	    	startActivity(new Intent(this, Easter.class)); 
    		
    	}
    	else if (v.getId() == R.id.pickDate)
            showDialog(DATE_DIALOG_ID);
    }
    
    
    // updates the date in the TextView
    private void updateDisplay() {
        mDateDisplay.setText("" + String.format("%d", mYear) + "-" + String.format("%02d", mMonth+1) + "-" + String.format("%02d", mDay));
//            new StringBuilder()
                    // Month is 0 based so add 1
//            		.append(mYear).append("-")
//                    .append(mMonth + 1).append("-")
//                    .append(mDay).append(" "));
        Log.d(TAG,"Date set: " + mYear + "-" + (mMonth + 1) + "-" + mDay);
    }
    
    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                	
                    Calendar c = Calendar.getInstance();
                    c.setFirstDayOfWeek(Calendar.MONDAY);
                    c.set(Calendar.YEAR, year);
            		c.set(Calendar.MONTH, monthOfYear);
            		c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            		Log.d(TAG, "1Day of month: " + c.get(Calendar.DAY_OF_MONTH));
                    c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            		Log.d(TAG, "2Day of month: " + c.get(Calendar.DAY_OF_MONTH));
                    
                    Log.d(TAG, "Calendar date: " + c.toString());

                    Log.d(TAG,"Calendar is now: " + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH));
                    
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                    Log.d(TAG,"Trying to set: " + mYear + "-" + (mMonth + 1) + "-" + mDay);
                    updateDisplay();
                }
            };    
            
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
        }
        return null;
    }

}
