package com.sunrise.robotdigger.work;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.sunrise.robotdigger.R;
import com.sunrise.robotdigger.home.HomeActivity;
import com.sunrise.robotdigger.login.LoginActivity;
import com.sunrise.robotdigger.means.MeansActivity;
import com.sunrise.robotdigger.more.MoreActivity;
import com.sunrise.robotdigger.show.ShowActivity;
import com.sunrise.robotdigger.utils.MemoryInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

@SuppressLint("ShowToast")
@SuppressWarnings("deprecation")
public class MainTabActivity extends TabActivity implements
		OnCheckedChangeListener {

	FrameLayout fmpan;
	TabHost tabHost;
	ImageView image;
	FrameLayout fm;
	LayoutInflater inflater;
	public boolean isReverse = false;
	PopupWindow popup;
	private RadioGroup mainTab;
	private TabHost mTabHost;

	private Intent mHomeIntent;
	private Intent mNewsIntent;
	private Intent mInfoIntent;
	private Intent mSearchIntent;

	private final static String TAB_TAG_HOME = "tab_tag_home";
	private final static String TAB_TAG_NEWS = "tab_tag_news";
	private final static String TAB_TAG_INFO = "tab_tag_info";
	private final static String TAB_TAG_SEARCH = "tab_tag_search";

	private static final int ITEM_1 = Menu.FIRST;
	private static final int ITEM_2 = Menu.FIRST + 1;
	private static final int ITEM_3 = Menu.FIRST + 2;

	private static final String LOG_TAG = "Digger-";
	private String settingTempFile;
	private String putingTempFile;
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";

	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		String userName = share.getString(SHARE_LOGIN_USERNAME, "");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab);
		createNewFile();
		mainTab = (RadioGroup) findViewById(R.id.main_tab);
		mainTab.setOnCheckedChangeListener(this);
		prepareIntent();
		setupIntent();
		initView();
		if(userName.equals("")){
			Intent intent_1 = new Intent();
			intent_1.setClass(MainTabActivity.this, LoginActivity.class);
			startActivity(intent_1);
			MainTabActivity.this.finish();
		}
	}

	private void prepareIntent() {
		mHomeIntent = new Intent(this, HomeActivity.class);
		mNewsIntent = new Intent(this, ShowActivity.class);
		mInfoIntent = new Intent(this, MeansActivity.class);
		mInfoIntent.putExtra("putingTempFile", putingTempFile);
		mNewsIntent.putExtra("settingTempFile", settingTempFile);
		mSearchIntent = new Intent(this, MoreActivity.class);
	}

	/**
	 * 
	 */
	private void setupIntent() {
		this.mTabHost = getTabHost();
		TabHost localTabHost = this.mTabHost;
		localTabHost.addTab(buildTabSpec(TAB_TAG_HOME, R.string.dynamic,
				R.drawable.icon_1_n, mHomeIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_NEWS, R.string.related,
				R.drawable.icon_2_n, mNewsIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_INFO, R.string.homepage,
				R.drawable.icon_3_n, mInfoIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_SEARCH, R.string.application,
				R.drawable.icon_4_n, mSearchIntent));
	}

	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
			final Intent content) {
		return this.mTabHost
				.newTabSpec(tag)
				.setIndicator(getString(resLabel),
						getResources().getDrawable(resIcon))
				.setContent(content);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio_button0:
			this.mTabHost.setCurrentTabByTag(TAB_TAG_HOME);
			break;
		case R.id.radio_button1:
			this.mTabHost.setCurrentTabByTag(TAB_TAG_NEWS);
			break;
		case R.id.radio_button2:
			this.mTabHost.setCurrentTabByTag(TAB_TAG_INFO);
			break;
		case R.id.radio_button3:
			this.mTabHost.setCurrentTabByTag(TAB_TAG_SEARCH);
			break;

		}
	}

	private void initView() {
		inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		fmpan = (FrameLayout) findViewById(R.id.tab1);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, ITEM_1, 0, "设置");
		menu.add(0, ITEM_2, 0, "切换账户");
		menu.add(0, ITEM_3, 0, "退出");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case ITEM_1:
			Intent intent = new Intent();
			intent.setClass(MainTabActivity.this, SettingsActivity.class);
			intent.putExtra("settingTempFile", settingTempFile);
			startActivityForResult(intent, Activity.RESULT_FIRST_USER);
			break;
		case ITEM_2:
			Intent intent_1 = new Intent();
			intent_1.setClass(MainTabActivity.this, LoginActivity.class);
			startActivity(intent_1);
			MainTabActivity.this.finish();
			break;
		case ITEM_3:
			 new AlertDialog.Builder(MainTabActivity.this)
					.setTitle("退出程序")
					.setMessage("退出后，你将收不到新的消息。确定要退出?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Toast.makeText(MainTabActivity.this,
											"退出程序", Toast.LENGTH_LONG).show();
									MainTabActivity.this.finish();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Toast.makeText(MainTabActivity.this,
											"程序不退出", Toast.LENGTH_LONG);
								}
							}).show();
			break;
		}
		return true;

	}

	public void paiz() {
		Toast.makeText(MainTabActivity.this, "程序不退出", Toast.LENGTH_LONG).show();
	}

	private void createNewFile() {
		Log.i(LOG_TAG, "create new file to save setting data");
		settingTempFile = getBaseContext().getFilesDir().getPath()
				+ "\\DiggerSettings.properties";
		Log.i(LOG_TAG, "settingFile = " + settingTempFile);
		File settingFile = new File(settingTempFile);
		if (!settingFile.exists()) {
			try {
				settingFile.createNewFile();
				Properties properties = new Properties();
				properties.setProperty("interval", "1");
				properties.setProperty("isfloat", "true");
				properties.setProperty("color", "0");
				properties.setProperty("lastcsv", "null");
				properties.setProperty("use_memory", "true");
				properties.setProperty("percentage_memory", "true");
				properties.setProperty("remain_memory", "true");
				properties.setProperty("use_cpu", "true");
				properties.setProperty("alluse_cpu", "true");
				properties.setProperty("information_flow", "true");
				MemoryInfo memoryInfo=new MemoryInfo();
				properties.setProperty("use_memory_max", String.valueOf(memoryInfo.getTotalMemory()/1024));
				properties.setProperty("use_cpu_max", String.valueOf(100));
				properties.setProperty("alluse_cpu_max", String.valueOf(100));
				properties.setProperty("server",
						"http://192.168.0.200:8080/RobotDigger_Web/");
				FileOutputStream fos = new FileOutputStream(settingTempFile);
				properties.store(fos, "Setting Data");
				fos.close();
			} catch (IOException e) {
				Log.d(LOG_TAG, "create new file exception :" + e.getMessage());
			}
		}
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			putingTempFile = android.os.Environment
					.getExternalStorageDirectory()
					+ File.separator
					+ "/RobotDiggerCSV/";
		} else {
			putingTempFile = getBaseContext().getFilesDir().getPath()
					+ File.separator + "/RobotDiggerCSV/";
		}
		File putingFile = new File(putingTempFile);
		if (!putingFile.exists()) {
			putingFile.mkdirs();
		}
	}
}