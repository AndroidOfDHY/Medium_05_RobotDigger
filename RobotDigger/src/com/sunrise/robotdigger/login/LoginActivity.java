package com.sunrise.robotdigger.login;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;


import com.sunrise.robotdigger.R;
import com.sunrise.robotdigger.work.FeedbackActivity;
import com.sunrise.robotdigger.work.MainTabActivity;
import com.sunrise.robotdigger.work.RegisterActivity;
import com.sunrise.robotdigger.work.SettingsActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private String userName;
	private String password;
	/** 以下是UI */
	private EditText view_userName;
	private EditText view_password;
	private Button view_loginSubmit;
	private Button view_loginRegister;
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";
	private String SHARE_LOGIN_EMAIL = "MAP_LOGIN_EMAIL";
	private String SHARE_LOGIN_TIME = "MAP_LOGIN_TIME";
	/** 如果登陆失败,这个可以给用户确切的消息显示,true是网络连接失败,false是用户名和密码错误 */
	private boolean isNetError;
	private ProgressDialog proDialog;
	private String settingTempFile;
	private Button view_retrievePassword;

	/** 登录后台通知更新UI线程,主要用于登录失败,通知UI线程更新界面 */
	Handler loginHandler = new Handler() {
		public void handleMessage(Message msg) {
			isNetError = msg.getData().getBoolean("isNetError");
			boolean isSend = msg.getData().getBoolean("isSend");
			boolean isNoUser = msg.getData().getBoolean("isNoUser");
			if (proDialog != null) {
				proDialog.dismiss();
			}
			if (isNetError) {
				Toast.makeText(LoginActivity.this,
						"登陆失败:\n1.请检查您网络连接.\n2.请联系我们.!", Toast.LENGTH_SHORT)
						.show();
			} else if (isSend) {
				Toast.makeText(LoginActivity.this,
						LoginActivity.this.getString(R.string.email_success),
						Toast.LENGTH_SHORT).show();
			} else if (isNoUser) {
				Toast.makeText(LoginActivity.this,
						LoginActivity.this.getString(R.string.email_false),
						Toast.LENGTH_SHORT).show();
			}
			// 用户名和密码错误
			else {
				Toast.makeText(LoginActivity.this, "登陆失败,请输入正确的用户名和密码!",
						Toast.LENGTH_SHORT).show();
				// 清除以前的SharePreferences密码
				clearSharePassword();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		findViewsById();
		isNetError = false;
		settingTempFile = getBaseContext().getFilesDir().getPath()
				+ "\\DiggerSettings.properties";
		initView(false);
		// 需要去submitListener里面设置URL
		setListener();
	}

	/** 初始化注册View组件 */
	private void findViewsById() {
		view_userName = (EditText) findViewById(R.id.editText_1);
		view_password = (EditText) findViewById(R.id.editText_2);
		view_loginSubmit = (Button) findViewById(R.id.button_1);
		view_loginRegister = (Button) findViewById(R.id.button_3);
		view_retrievePassword = (Button) findViewById(R.id.button_2);
	}

	/**
	 * 初始化界面
	 * 
	 * @param isRememberMe
	 *            如果当时点击了RememberMe,并且登陆成功过一次,则saveSharePreferences(true,ture)后,
	 *            则直接进入
	 * */
	private void initView(boolean isRememberMe) {
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		String userName = share.getString(SHARE_LOGIN_USERNAME, "");
		String password = share.getString(SHARE_LOGIN_PASSWORD, "");
		Log.d(this.toString(), "userName=" + userName + " password=" + password);
		if (!"".equals(userName)) {
			view_userName.setText(userName);
		}
		if (!"".equals(password)) {
			view_password.setText(password);
		}
		// 如果密码也保存了,则直接让登陆按钮获取焦点
		if (view_password.getText().toString().length() > 0) {
			// view_loginSubmit.requestFocus();
			// view_password.requestFocus();
		}
		share = null;
	}

	/**
	 * 检查用户登陆,服务器通过DataOutputStream的dos.writeInt(int);来判断是否登录成功(
	 * 服务器返回int>0登陆成功,否则失败),登陆成功后根据isRememberMe来判断是否保留密码(用户名是会保留的),
	 * 如果连接服务器超过5秒,也算连接失败.
	 * 
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @param validateUrl
	 *            检查登陆的地址
	 * */
	private boolean validateLocalLogin(String userName, String password,
			String validateUrl) {
		// 用于标记登陆状态
		boolean loginState = false;
		HttpURLConnection conn = null;
		DataInputStream dis = null;
		String loginStateString = null;
		try {
			URL url = new URL(validateUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.connect();
			dis = new DataInputStream(conn.getInputStream());
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				Log.d(this.toString(),
						"getResponseCode() not HttpURLConnection.HTTP_OK");
				isNetError = true;
				return false;
			}
			// 读取服务器的登录状态码
			loginStateString = dis.readUTF();
			if (!loginStateString.equals("0")) {
				if(loginStateString.indexOf("!")!=-1)
				loginState = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			isNetError = true;
			Log.d(this.toString(), e.getMessage() + "  127 line");
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		// 登陆成功
		if (loginState) {
			saveSharePreferences(loginStateString);
			String putingTempFile;
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
			SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
			putingTempFile += share.getString(SHARE_LOGIN_USERNAME, "");
			File putingFile = new File(putingTempFile);
			if (!putingFile.exists()) {
				putingFile.mkdirs();
			}
		} else {
			// 如果不是网络错误
			if (!isNetError) {
				clearSharePassword();
			}
		}
		return loginState;
	}

	/**
	 * 如果登录成功过,则将登陆用户名和密码记录在SharePreferences
	 * 
	 * @param saveUserName
	 *            是否将用户名保存到SharePreferences
	 * @param savePassword
	 *            是否将密码保存到SharePreferences
	 * */
	private void saveSharePreferences(String string) {
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		Log.d(this.toString(), "saveUserName="
				+ view_userName.getText().toString());
		share.edit()
				.putString(SHARE_LOGIN_USERNAME,
						view_userName.getText().toString()).commit();
		share.edit()
				.putString(SHARE_LOGIN_PASSWORD,
						view_password.getText().toString()).commit();
		share.edit().putString(SHARE_LOGIN_EMAIL, string.substring(0, string.indexOf("!"))).commit();
		share.edit().putString(SHARE_LOGIN_TIME, string.substring(string.indexOf("!")+1)).commit();				
		share = null;
	}

	/** 登录Button Listener */
	private OnClickListener submitListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			proDialog = ProgressDialog.show(LoginActivity.this, "连接中..",
					"连接中..请稍后....", true, true);
			// 开一个线程进行登录验证,主要是用于失败,成功可以直接通过startAcitivity(Intent)转向
			Thread loginThread = new Thread(new LoginFailureHandler());
			loginThread.start();
		}
	};

	/** 注册Listener */
	private OnClickListener registerLstener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, RegisterActivity.class);
			// 转向注册页面
			startActivity(intent);
			LoginActivity.this.finish();
		}
	};

	private OnClickListener retrievePassword = new OnClickListener() {

		@Override
		public void onClick(View v) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork()
					.penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
					.build());
			proDialog = ProgressDialog.show(LoginActivity.this, "连接中..",
					"连接中..请稍后....", true, true);
			// 开一个线程进行登录验证,主要是用于失败,成功可以直接通过startAcitivity(Intent)转向
			Thread retrieveThread = new Thread(new Runnable() {

				@Override
				public void run() {
					String userName = view_userName.getText().toString();
					String settingTempFile = getBaseContext().getFilesDir()
							.getPath() + "\\DiggerSettings.properties";
					Properties properties = new Properties();
					try {
						properties.load(new FileInputStream(settingTempFile));
						String validateURL = properties.getProperty("server")
								.trim()
								+ "phoneRetrievePassword?username="
								+ userName;
						boolean State = validateLocalRetrievePassword(userName,
								validateURL);

						if (State) {
							proDialog.dismiss();
							Message message = new Message();
							Bundle bundle = new Bundle();
							bundle.putBoolean("isSend", State);
							message.setData(bundle);
							loginHandler.sendMessage(message);
						} else {
							Message message = new Message();
							Bundle bundle = new Bundle();
							bundle.putBoolean("isNoUser", true);
							message.setData(bundle);
							loginHandler.sendMessage(message);
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			retrieveThread.start();
		}
	};

	private boolean validateLocalRetrievePassword(String userName,
			String validateUrl) {
		boolean State = false;
		HttpURLConnection conn = null;
		DataInputStream dis = null;
		try {
			URL url = new URL(validateUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.connect();
			dis = new DataInputStream(conn.getInputStream());
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				isNetError = true;
				Log.d(this.toString(),
						"getResponseCode() not HttpURLConnection.HTTP_OK");
				return false;
			}
			// 读取服务器的登录状态码
			int StateInt = dis.readInt();
			if (StateInt == 1) {
				State = true;
			}
		} catch (Exception e) {
			isNetError = true;
			e.printStackTrace();
			Log.d(this.toString(), e.getMessage() + "  127 line");
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return State;
	}

	/** 设置监听器 */
	private void setListener() {
		view_loginSubmit.setOnClickListener(submitListener);
		view_loginRegister.setOnClickListener(registerLstener);
		view_retrievePassword.setOnClickListener(retrievePassword);
	}

	/** 弹出关于对话框 */
	/*
	 * private void alertAbout() { new
	 * AlertDialog.Builder(Login.this).setTitle(R.string.MENU_ABOUT)
	 * .setMessage(R.string.aboutInfo).setPositiveButton( R.string.ok_label, new
	 * DialogInterface.OnClickListener() { public void onClick( DialogInterface
	 * dialoginterface, int i) { } }).show(); }
	 */

	/** 清除密码 */
	private void clearSharePassword() {
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		share.edit().putString(SHARE_LOGIN_PASSWORD, "").commit();
		share = null;
	}

	class LoginFailureHandler implements Runnable {
		@Override
		public void run() {
			userName = view_userName.getText().toString();
			password = view_password.getText().toString();
			// 这里换成你的验证地址
			String settingTempFile = getBaseContext().getFilesDir().getPath()
					+ "\\DiggerSettings.properties";
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream(settingTempFile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String validateURL = properties.getProperty("server").trim()
					+ "phonelogin?username=" + userName + "&password="
					+ password;
			boolean loginState = validateLocalLogin(userName, password,
					validateURL);
			Log.d(this.toString(), "validateLogin");

			// 登陆成功
			if (loginState) {
				// 需要传输数据到登陆后的界面,
				proDialog.dismiss();
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, MainTabActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
			} else {
				// 通过调用handler来通知UI主线程更新UI,
				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putBoolean("isNetError", isNetError);
				message.setData(bundle);
				loginHandler.sendMessage(message);
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		menu.add(0, 1, 0, "设置");
		menu.add(0, 2, 0, "意见反馈");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			Intent intent2 = new Intent();
			intent2.setClass(LoginActivity.this, SettingsActivity.class);
			intent2.putExtra("settingTempFile", settingTempFile);
			startActivityForResult(intent2, Activity.RESULT_FIRST_USER);
			break;
		case 2:
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, FeedbackActivity.class);
			startActivity(intent);
			break;
		}
		return true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
			String userName = share.getString(SHARE_LOGIN_USERNAME, "");
			if (userName != null && userName.length() > 0) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, MainTabActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
			} else {
				return super.onKeyDown(keyCode, event);
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
