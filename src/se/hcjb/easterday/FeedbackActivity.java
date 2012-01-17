package se.hcjb.easterday;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class FeedbackActivity extends ActionBarActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        
	    TextView t1 = (TextView) findViewById(R.id.hcjbText);
	    t1.setMovementMethod(LinkMovementMethod.getInstance());

	    findViewById(R.id.vbtHome).setOnClickListener(this);

	    ((EasterApplication) getApplication()).activateCommercial(this, R.id.textView3);
        
    }



}
