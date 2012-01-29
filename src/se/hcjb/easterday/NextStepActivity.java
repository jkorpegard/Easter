package se.hcjb.easterday;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class NextStepActivity extends ActionBarActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.next_step);

	    View v1 = findViewById(R.id.next_step_btn1);
	    v1.setOnClickListener(this);
        
	    v1 = findViewById(R.id.next_step_btn2);
	    v1.setOnClickListener(this);
        
	    v1 = findViewById(R.id.next_step_btn3);
	    v1.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		super.onClick(v);
    	if (v.getId() == R.id.next_step_btn1 ) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.next_step_salvation_prayer)
			       .setCancelable(false)
			       .setPositiveButton(R.string.next_step_popup_ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   ;
			           }
			       });
			AlertDialog alert = builder.create();		
			alert.show();
    	}		
    	else if (v.getId() == R.id.next_step_btn2 ) {
		    final Intent intent = new Intent(this, FindChurchActivity.class);
		    startActivity (intent);
    	}		
    	else if (v.getId() == R.id.next_step_btn3 ) {
		    final Intent intent = new Intent(this, FeedbackActivity.class);
		    startActivity (intent);
    	}		
	}

}
