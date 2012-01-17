package se.hcjb.easterday;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class ActionBarActivity extends Activity implements OnClickListener {

    @Override
    protected void onResume(){
    	super.onResume();
        try {
			((EasterApplication) getApplication()).activateCommercial(this, R.id.textView3);
			findViewById(R.id.vbtHome).setOnClickListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

    }	
	
	public void onClick(View v) {
        if (v.getId() == R.id.vbtHome) {
		    final Intent intent = new Intent(this, Start.class);
		    intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    startActivity (intent);
    		
    	}
    }


}
