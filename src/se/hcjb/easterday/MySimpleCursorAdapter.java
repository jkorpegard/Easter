package se.hcjb.easterday;

import android.content.Context;
import android.database.Cursor;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MySimpleCursorAdapter extends SimpleCursorAdapter {
	private Context myContext;

	public MySimpleCursorAdapter(Context context, int layout, Cursor cur,
			String[] from, int[] to) {
		super(context, layout, cur, from, to);
		myContext = context;
	}

	@Override public void setViewText(TextView tv, String text) 
	{
		Cursor cursor = getCursor();
		
		if (tv.getId() ==  R.id.label1)
			tv.setText(BibleTexts.makeDateString(myContext, cursor));
		else {
			super.setViewText(tv, text);
		}
	 }

	@Override public void setViewImage(ImageView iv, String text)
	{
		if (text.equals("0")) {
			iv.setImageResource(R.drawable.new1);
		}
		else {
			iv.setImageResource(R.drawable.check1);
		}
	}

}
