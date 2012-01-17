package se.hcjb.easterday;

import android.os.Bundle;

public class StartedActivity extends ActionBarActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.started);

	    ((EasterApplication) getApplication()).activateCommercial(this, R.id.textView3);
    }


}
