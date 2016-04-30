package com.sunrise.robotdigger.more;

import com.sunrise.robotdigger.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MessageActivity extends Activity {
	
	private Button button;
	private String userName;
	private String email;
	private String time;
	private String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_EMAIL = "MAP_LOGIN_EMAIL";
	private String SHARE_LOGIN_TIME = "MAP_LOGIN_TIME";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.information);
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		userName = share.getString(SHARE_LOGIN_USERNAME, "");
		email = share.getString(SHARE_LOGIN_EMAIL, "");
		time = share.getString(SHARE_LOGIN_TIME, "");
		TextView username=(TextView)findViewById(R.id.username);
		TextView email=(TextView)findViewById(R.id.email);
		TextView joindate=(TextView)findViewById(R.id.joindate);
		username.setText(userName);
		email.setText(this.email);
		joindate.setText(time);
		button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MessageActivity.this.finish();
			}
		});
	}
	

}
