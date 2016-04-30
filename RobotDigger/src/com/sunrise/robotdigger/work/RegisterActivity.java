package com.sunrise.robotdigger.work;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sunrise.robotdigger.R;
import com.sunrise.robotdigger.login.LoginActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	private EditText view_userName;
	private EditText view_password;
	private EditText view_passwordConfirm;
	private EditText view_email;
	private Button view_submit;
	private Button view_clearAll;
	private Button view_return;
	private static final int MENU_EXIT = Menu.FIRST - 1;
	private static final int MENU_ABOUT = Menu.FIRST;
	private String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";
	private String SHARE_LOGIN_EMAIL = "MAP_LOGIN_EMAIL";
	private boolean isNetError;
	private ProgressDialog proDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		findViews();
		setListener();
		// 然后执行注册的事情,访问远程服务器注册用户
	}

	Handler loginHandler = new Handler() {
		public void handleMessage(Message msg) {
			isNetError = msg.getData().getBoolean("isNetError");
			if (proDialog != null) {
				proDialog.dismiss();
			}
			if (isNetError) {
				Toast.makeText(RegisterActivity.this,
						"注册失败:\n1.请检查您网络连接.\n2.请联系我们.!", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(RegisterActivity.this, "注册失败,此用户名已存在!",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	/** 1.初始化注册view组件 */
	private void findViews() {
		view_userName = (EditText) findViewById(R.id.registerUserName);
		view_password = (EditText) findViewById(R.id.registerPassword);
		view_passwordConfirm = (EditText) findViewById(R.id.registerPasswordConfirm);
		view_submit = (Button) findViewById(R.id.registerSubmit);
		view_clearAll = (Button) findViewById(R.id.registerClear);
		view_return = (Button) findViewById(R.id.button_1);
		view_email = (EditText) findViewById(R.id.registerEmail);
	}

	private void setListener() {
		view_submit.setOnClickListener(submitListener);
		view_clearAll.setOnClickListener(clearListener);
		view_return.setOnClickListener(submitRetrun);
	}

	/** 监听注册确定按钮 */
	private OnClickListener submitListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String userName = view_userName.getText().toString();
			String password = view_password.getText().toString();
			String passwordConfirm = view_passwordConfirm.getText().toString();
			String email = view_email.getText().toString();
			if (validateForm(userName, password, passwordConfirm, email)) {
				proDialog = ProgressDialog.show(RegisterActivity.this, "连接中..",
						"连接中..请稍后....", true, true);
				Thread loginThread = new Thread(new Runnable() {

					@Override
					public void run() {
						String userName = view_userName.getText().toString();
						String password = view_password.getText().toString();
						String email = view_email.getText().toString();
						String settingTempFile = getBaseContext().getFilesDir()
								.getPath() + "\\DiggerSettings.properties";
						Properties properties = new Properties();
						try {
							properties
									.load(new FileInputStream(settingTempFile));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						String validateURL = properties.getProperty("server")
								.trim()
								+ "phoneRegister?username="
								+ userName
								+ "&password=" + password + "&email=" + email;
						boolean RegisterState = validateLocalLogin(userName,
								password, validateURL);

						if (RegisterState) {
							proDialog.dismiss();
							Intent intent = new Intent();
							intent.setClass(RegisterActivity.this,
									MainTabActivity.class);
							startActivity(intent);
							RegisterActivity.this.finish();
						} else {
							Message message = new Message();
							Bundle bundle = new Bundle();
							bundle.putBoolean("isNetError", isNetError);
							message.setData(bundle);
							loginHandler.sendMessage(message);
						}
					}
				});
				loginThread.start();
			}

		}
	};

	private boolean validateLocalLogin(String userName, String password,
			String validateUrl) {
		boolean registerState = false;
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
			int registerStateInt = dis.readInt();
			if (registerStateInt == 1) {
				registerState = true;
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
		if (registerState) {
			saveSharePreferences(true, true);
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
		}
		return registerState;
	}

	private void saveSharePreferences(boolean saveUserName, boolean savePassword) {
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		if (saveUserName) {
			Log.d(this.toString(), "saveUserName="
					+ view_userName.getText().toString());
			share.edit()
					.putString(SHARE_LOGIN_USERNAME,
							view_userName.getText().toString()).commit();
		}
		if (savePassword) {
			share.edit()
					.putString(SHARE_LOGIN_PASSWORD,
							view_password.getText().toString()).commit();
		}
		share.edit()
				.putString(SHARE_LOGIN_EMAIL, view_email.getText().toString())
				.commit();
		share = null;
	}

	/** 清空监听按钮 */
	private OnClickListener clearListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			clearForm();
		}
	};

	private OnClickListener submitRetrun = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(RegisterActivity.this, LoginActivity.class);
			startActivity(intent);
			RegisterActivity.this.finish();
		}
	};

	/** 检查注册表单 */
	private boolean validateForm(String userName, String password,
			String password2, String email) {
		StringBuilder suggest = new StringBuilder();
		Log.d(this.toString(), "validate");
		if (userName.length() < 1) {
			suggest.append(getText(R.string.suggust_userName) + "\n");
		}
		if (password.length() < 1 || password2.length() < 1) {
			suggest.append(getText(R.string.suggust_passwordNotEmpty) + "\n");
		}
		if (!password.equals(password2)) {
			suggest.append(getText(R.string.suggest_passwordNotSame) + "\n");
		}
		if (email.length() < 1 || !checkMailFormat(email)) {
			suggest.append(getText(R.string.suggest_email)+ "\n");
		}
		if (suggest.length() > 0) {
			Toast.makeText(this, suggest.subSequence(0, suggest.length() - 1),
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private boolean checkMailFormat(String mail) {
		String strPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*"
				+ "[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(mail);
		return m.matches();
	}

	/** 清空表单 */
	private void clearForm() {
		view_userName.setText("");
		view_password.setText("");
		view_passwordConfirm.setText("");

		view_userName.requestFocus();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_EXIT, 0, R.string.MENU_EXIT);
		menu.add(0, MENU_ABOUT, 0, R.string.MENU_ABOUT);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		switch (item.getItemId()) {
		case MENU_EXIT:
			RegisterActivity.this.finish();
			break;
		case MENU_ABOUT:
			alertAbout();
			break;
		}
		;
		return true;
	}

	/** 弹出关于对话框 */
	private void alertAbout() {
		new AlertDialog.Builder(RegisterActivity.this)
				.setTitle(R.string.MENU_ABOUT)
				.setMessage(R.string.aboutInfo)
				.setPositiveButton(R.string.ok_label,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
							}
						}).show();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			Intent intent = new Intent();
			intent.setClass(RegisterActivity.this, LoginActivity.class);
			startActivity(intent);
			RegisterActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
