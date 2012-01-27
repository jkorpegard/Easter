package se.hcjb.easterday;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class HCJBActivity extends ActionBarActivity {

	// TODO Set HCJB arcs in the background instead of black. (Not a todo here really, but in the xml file...) 
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hcjb);
        
		((EasterApplication) getApplication()).activateCommercial(this, R.id.textView3);
        
	    TextView t1 = (TextView) findViewById(R.id.hcjbText);
	    t1.setMovementMethod(LinkMovementMethod.getInstance());
    }
	
}
