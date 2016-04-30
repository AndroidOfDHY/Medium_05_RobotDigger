package com.sunrise.robotdigger.show;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import com.sunrise.robotdigger.R;
import com.sunrise.robotdigger.means.XYChartBuilder;
import com.sunrise.robotdigger.service.DiggerService;
import com.sunrise.robotdigger.utils.ProcessInfo;
import com.sunrise.robotdigger.utils.Programe;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ShowActivity extends Activity {

	private static final String LOG_TAG = "Digger-";
	private static final int TIMEOUT = 20000;
	private String processName, packageName;
	private ListView lstViProgramme;
	private Button submitButton;
	private Intent monitorService;
	private UpdateReceiver receiver;
	private boolean isServiceStop = false;
	private ProcessInfo processInfo;
	private int pid, uid;
	private List<Programe> processList;
	private boolean isRadioChecked = false;
	private String settingTempFile;
	private long exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show);
		processInfo = new ProcessInfo();
		lstViProgramme = (ListView) findViewById(R.id.processList);
		submitButton = (Button) findViewById(R.id.submit);
		settingTempFile = getBaseContext().getFilesDir().getPath()
				+ "\\DiggerSettings.properties";
		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				monitorService = new Intent();
				monitorService.setClass(ShowActivity.this, DiggerService.class);
				if ("开始测试".equals(submitButton.getText().toString())) {
					if (isRadioChecked) {
						Intent intent = getPackageManager()
								.getLaunchIntentForPackage(packageName);
						Log.d(LOG_TAG, packageName);
						try {
							startActivity(intent);
						} catch (NullPointerException e) {
							Toast.makeText(ShowActivity.this, "该程序无法启动",
									Toast.LENGTH_LONG).show();
							return;
						}
						waitForAppStart(packageName);
						monitorService.putExtra("processName", processName);
						monitorService.putExtra("pid", pid);
						monitorService.putExtra("uid", uid);
						monitorService.putExtra("packageName", packageName);
						monitorService.putExtra("settingTempFile",
								settingTempFile);
						ShowActivity.this.startService(monitorService);
						submitButton.setText("停止测试");
					} else {
						Toast.makeText(ShowActivity.this, "请选择需要测试的应用程序",
								Toast.LENGTH_LONG).show();
					}
				} else {
					submitButton.setText("开始测试");
					ShowActivity.this.stopService(monitorService);
					monitorService = null;
					Intent intent = new Intent();
					intent.setClass(ShowActivity.this, XYChartBuilder.class);
					Properties properties = new Properties();
					try {
						properties.load(new FileInputStream(settingTempFile));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					intent.putExtra("path", properties.getProperty("lastcsv")
							.trim());
					intent.putExtra("type", "0");
					startActivityForResult(intent, Activity.RESULT_FIRST_USER);
				}
			}
		});
	}

	private void waitForAppStart(String packageName) {
		Log.d(LOG_TAG, "wait for app start");
		boolean isProcessStarted = false;
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() < startTime + TIMEOUT) {
			processList = processInfo.getRunningProcess(getBaseContext());
			for (Programe programe : processList) {
				if ((programe.getPackageName() != null)
						&& (programe.getPackageName().equals(packageName))) {
					pid = programe.getPid();
					Log.d(LOG_TAG, "pid:" + pid);
					uid = programe.getUid();
					if (pid != 0) {
						isProcessStarted = true;
						break;
					}
				}
			}
			if (isProcessStarted) {
				break;
			}
		}
	}

	public class UpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			isServiceStop = intent.getExtras().getBoolean("isServiceStop");
			if (isServiceStop) {
				submitButton.setText("开始测试");
				monitorService = null;
				Intent intent1 = new Intent();
				intent1.setClass(ShowActivity.this, XYChartBuilder.class);
				Properties properties = new Properties();
				try {
					properties.load(new FileInputStream(settingTempFile));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				intent1.putExtra("path", properties.getProperty("lastcsv")
						.trim());
				intent1.putExtra("type", "0");
				startActivityForResult(intent1, Activity.RESULT_FIRST_USER);
			}
		}
	}

	public void onResume() {
		super.onResume();
		Log.d(LOG_TAG, "onResume");
		if (DiggerService.isStop) {
			submitButton.setText("开始测试");
		}
		lstViProgramme.setAdapter(new ListAdapter());
	}

	@Override
	protected void onStart() {
		Log.d(LOG_TAG, "onStart");
		receiver = new UpdateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.sunrise.robotdigger.action.DiggerService");
		this.registerReceiver(receiver, filter);
		super.onStart();
	}

	private class ListAdapter extends BaseAdapter {
		List<Programe> programe;
		int tempPosition = -1;

		/**
		 * save status of all installed processes
		 */
		class Viewholder {
			TextView txtAppName;
			ImageView imgViAppIcon;
			RadioButton rdoBtnApp;
		}

		public ListAdapter() {
			programe = processInfo.getRunningProcess(getBaseContext());
		}

		@Override
		public int getCount() {
			return programe.size();
		}

		@Override
		public Object getItem(int position) {
			return programe.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Viewholder holder = new Viewholder();
			final int i = position;
			convertView = ShowActivity.this.getLayoutInflater().inflate(
					R.layout.list_item, null);
			holder.imgViAppIcon = (ImageView) convertView
					.findViewById(R.id.image);
			holder.txtAppName = (TextView) convertView.findViewById(R.id.text);
			holder.rdoBtnApp = (RadioButton) convertView.findViewById(R.id.rb);
			holder.rdoBtnApp.setId(position);
			holder.rdoBtnApp
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {
								isRadioChecked = true;
								// Radio function
								if (tempPosition != -1) {
									RadioButton tempButton = (RadioButton) findViewById(tempPosition);
									if ((tempButton != null)
											&& (tempPosition != i)) {
										tempButton.setChecked(false);
									}
								}

								tempPosition = buttonView.getId();
								packageName = programe.get(tempPosition)
										.getPackageName();
								processName = programe.get(tempPosition)
										.getProcessName();
							}
						}
					});
			if (tempPosition == position) {
				if (!holder.rdoBtnApp.isChecked())
					holder.rdoBtnApp.setChecked(true);
			}
			Programe pr = (Programe) programe.get(position);
			holder.imgViAppIcon.setImageDrawable(pr.getIcon());
			holder.txtAppName.setText(pr.getProcessName());
			return convertView;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				if(isServiceStop){
					ShowActivity.this.stopService(monitorService);
				}
				ShowActivity.this.finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void finish() {
		super.finish();
	}

	protected void onStop() {
		unregisterReceiver(receiver);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
