package com.sunrise.robotdigger.work;

import com.sunrise.robotdigger.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FeedbackActivity extends Activity {
	

	private Button f_button_1,f_button_2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		f_button_1 = (Button) findViewById(R.id.feed_button_1);
		f_button_2 = (Button) findViewById(R.id.feed_button_2);
		
		
		f_button_1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FeedbackActivity.this.finish();
			}
		});
		
		f_button_2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});


	}

}
