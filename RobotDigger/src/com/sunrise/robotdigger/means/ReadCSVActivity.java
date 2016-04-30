package com.sunrise.robotdigger.means;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ReadCSVActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = this.getIntent();
		String csv = intent.getStringExtra("CSV");
		TableView table = new TableView(this,csv,getBaseContext().getFilesDir().getPath()
				+ "\\DiggerSettings.properties");
		setContentView(table);
	}
}
