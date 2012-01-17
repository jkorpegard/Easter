package se.hcjb.easterday;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class FindChurchActivity extends ActionBarActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_church);
        
		((EasterApplication) getApplication()).activateCommercial(this, R.id.textView3);
        
	    TextView t1 = (TextView) findViewById(R.id.hcjbText);
	    t1.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
