package com.sunrise.robotdigger.work;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.sunrise.robotdigger.R;
import com.sunrise.robotdigger.utils.MemoryInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Setting Page of Digger
 * 
 */
public class SettingsActivity extends Activity {

	private static final String LOG_TAG = "Digger-"
			+ SettingsActivity.class.getSimpleName();
	private static final String[] list = { "黑色", "白色", "蓝色", "红色", "绿色" };
	private EditText edtTime;
	private EditText server;
	private String time;
	private String settingTempFile;
	private Spinner spinner;
	private CheckBox chkFloat;
	private CheckBox use_memory;
	private CheckBox percentage_memory;
	private CheckBox remain_memory;
	private CheckBox use_cpu;
	private CheckBox alluse_cpu;
	private CheckBox information_flow;
	private EditText use_memory_max;
	private EditText use_cpu_max;
	private EditText alluse_cpu_max;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		Intent intent = this.getIntent();
		settingTempFile = intent.getStringExtra("settingTempFile");
		edtTime = (EditText) findViewById(R.id.time);
		spinner = (Spinner) findViewById(R.id.color);
		server = (EditText) findViewById(R.id.server);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setVisibility(View.VISIBLE);
		chkFloat = (CheckBox) findViewById(R.id.floating);
		use_memory = (CheckBox) findViewById(R.id.use_memory);
		percentage_memory = (CheckBox) findViewById(R.id.percentage_memory);
		remain_memory = (CheckBox) findViewById(R.id.remain_memory);
		use_cpu = (CheckBox) findViewById(R.id.use_cpu);
		alluse_cpu = (CheckBox) findViewById(R.id.alluse_cpu);
		information_flow = (CheckBox) findViewById(R.id.information_flow);
		use_memory_max = (EditText) findViewById(R.id.use_memory_max);
		use_cpu_max = (EditText) findViewById(R.id.use_cpu_max);
		alluse_cpu_max = (EditText) findViewById(R.id.alluse_cpu_max);
		Button btnSave = (Button) findViewById(R.id.save);
		Button btnreturn_1 = (Button) findViewById(R.id.return_1);
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(settingTempFile));
			String interval = properties.getProperty("interval").trim();
			String color = properties.getProperty("color").trim();
			if (color.equals("0"))
				spinner.setSelection(0);
			else if (color.equals("1"))
				spinner.setSelection(1);
			else if (color.equals("2"))
				spinner.setSelection(2);
			else if (color.equals("3"))
				spinner.setSelection(3);
			else if (color.equals("4"))
				spinner.setSelection(4);
			server.setText(properties.getProperty("server").trim());
			time = "".equals(interval) ? "1" : interval;
			chkFloat.setChecked("false".equals(properties
					.getProperty("isfloat").trim()) ? false : true);
			use_memory.setChecked("false".equals(properties.getProperty(
					"use_memory").trim()) ? false : true);
			percentage_memory.setChecked("false".equals(properties.getProperty(
					"percentage_memory").trim()) ? false : true);
			remain_memory.setChecked("false".equals(properties.getProperty(
					"remain_memory").trim()) ? false : true);
			use_cpu.setChecked("false".equals(properties.getProperty("use_cpu")
					.trim()) ? false : true);
			alluse_cpu.setChecked("false".equals(properties.getProperty(
					"alluse_cpu").trim()) ? false : true);
			information_flow.setChecked("false".equals(properties.getProperty(
					"information_flow").trim()) ? false : true);
			use_memory_max.setText(properties.getProperty("use_memory_max").trim());
			use_cpu_max.setText(properties.getProperty("use_cpu_max").trim());
			alluse_cpu_max.setText(properties.getProperty("alluse_cpu_max").trim());
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "FileNotFoundException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(LOG_TAG, "IOException: " + e.getMessage());
			e.printStackTrace();
		}
		edtTime.setText(time);
		btnreturn_1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingsActivity.this.finish();
			}
		});
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MemoryInfo memoryInfo=new MemoryInfo();
				String memory_max = use_memory_max.getText().toString().trim();
				time = edtTime.getText().toString().trim();
				if (!isNumeric(time)) {
					Toast.makeText(SettingsActivity.this, "输入数据无效，请重新输入",
							Toast.LENGTH_LONG).show();
					edtTime.setText("");
				} else if ("".equals(time) || Long.parseLong(time) == 0) {
					Toast.makeText(SettingsActivity.this, "输入数据为空,请重新输入",
							Toast.LENGTH_LONG).show();
					edtTime.setText("");
				} else if (Integer.parseInt(time) > 600) {
					Toast.makeText(SettingsActivity.this, "数据超过最大值600，请重新输入",
							Toast.LENGTH_LONG).show();
				} else if ("".equals(server.getText().toString().trim())
						|| server.getText().toString().trim().length() == 0) {
					Toast.makeText(SettingsActivity.this, "输入数据为空,请重新输入",
							Toast.LENGTH_LONG).show();
				}else if ("".equals(memory_max) || Long.parseLong(memory_max) == 0) {
					Toast.makeText(SettingsActivity.this, "输入内存为空,请重新输入",
							Toast.LENGTH_LONG).show();
					use_memory_max.setText("");
				} else if (Integer.parseInt(memory_max) > memoryInfo.getTotalMemory()/1024) {
					Toast.makeText(SettingsActivity.this, "输入内存超过最大内存，请重新输入",
							Toast.LENGTH_LONG).show();
				} else if ("".equals(use_cpu_max.getText().toString().trim())
						|| use_cpu_max.getText().toString().trim().length() == 0) {
					Toast.makeText(SettingsActivity.this, "输入CPU率为空,请重新输入",
							Toast.LENGTH_LONG).show();
				} else if (Integer.parseInt(use_cpu_max.getText().toString().trim()) >100) {
					Toast.makeText(SettingsActivity.this, "输入CPU率大于100,请重新输入",
							Toast.LENGTH_LONG).show();
				} else if ("".equals(alluse_cpu_max.getText().toString().trim())
						|| alluse_cpu_max.getText().toString().trim().length() == 0) {
					Toast.makeText(SettingsActivity.this, "输入总CPU率为空,请重新输入",
							Toast.LENGTH_LONG).show();
					
				}else if (Integer.parseInt(alluse_cpu_max.getText().toString().trim()) >100) {
					Toast.makeText(SettingsActivity.this, "输入总CPU率大于100,请重新输入",
							Toast.LENGTH_LONG).show();
					
				} else {
					try {
						Properties properties = new Properties();
						properties.load(new FileInputStream(settingTempFile));
						properties.setProperty("interval", time);
						properties.setProperty("color",
								String.valueOf(spinner.getSelectedItemId()));
						properties.setProperty(
								"server",
								String.valueOf(server.getText().toString()
										.trim()));
						properties.setProperty("isfloat",
								chkFloat.isChecked() ? "true" : "false");
						properties.setProperty("use_memory",
								use_memory.isChecked() ? "true" : "false");
						properties.setProperty("percentage_memory",
								percentage_memory.isChecked() ? "true" : "false");
						properties.setProperty("remain_memory",
								remain_memory.isChecked() ? "true" : "false");
						properties.setProperty("use_cpu",
								use_cpu.isChecked() ? "true" : "false");
						properties.setProperty("alluse_cpu",
								alluse_cpu.isChecked() ? "true" : "false");
						properties.setProperty("information_flow",
								information_flow.isChecked() ? "true" : "false");
						properties.setProperty("use_memory_max", memory_max);
						properties.setProperty("use_cpu_max", use_cpu_max.getText().toString().trim());
						properties.setProperty("alluse_cpu_max", alluse_cpu_max.getText().toString().trim());
						FileOutputStream fos = new FileOutputStream(
								settingTempFile);
						properties.store(fos, "Setting Data");
						fos.close();
						Toast.makeText(SettingsActivity.this, "保存成功",
								Toast.LENGTH_LONG).show();
						SettingsActivity.this.finish();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			SettingsActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * is input a number.
	 * 
	 * @param inputStr
	 *            input string
	 * @return true is numeric
	 */
	private boolean isNumeric(String inputStr) {
		for (int i = inputStr.length(); --i >= 0;) {
			if (!Character.isDigit(inputStr.charAt(i))) {
				return false;
			}
		}
		return true;
	}

}
