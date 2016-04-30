/**
 * 
 */
package com.sunrise.robotdigger.more;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import com.sunrise.robotdigger.R;
import com.sunrise.robotdigger.utils.FileUtils;
import com.sunrise.robotdigger.work.FeedbackActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 
 * @功能 更多Activity
 */
public class MoreActivity extends Activity {

	private boolean isNetError = false;
	private boolean nullfile = false;
	private long exitTime = 0;
	private RelativeLayout restoreBackup,assistance,concerning,message;
	private ProgressDialog pd;
	private String userName;
	private String password;
	private String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			pd.dismiss();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		userName = share.getString(SHARE_LOGIN_USERNAME, "");
		password = share.getString(SHARE_LOGIN_PASSWORD, "");
		message= (RelativeLayout) findViewById(R.id.message);
		restoreBackup = (RelativeLayout) findViewById(R.id.restoreBackup);
		assistance = (RelativeLayout) findViewById(R.id.assistance);
		concerning = (RelativeLayout) findViewById(R.id.concerning);
		message.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MoreActivity.this, MessageActivity.class);
				startActivity(intent);
			}
		});
		restoreBackup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(MoreActivity.this)
						.setTitle("还原备份")
						.setMessage("还原备份后，未备份的文件不可恢复。确定要还原?")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										pd = ProgressDialog.show(
												MoreActivity.this, "还原备份",
												"正在还原…");
										new Thread() {
											public void run() {
												boolean state = restoreBackup();
												handler.sendEmptyMessage(0);
												Looper.prepare();
												if (state)
													new AlertDialog.Builder(
															MoreActivity.this)
															.setTitle("还原备份成功")
															.setPositiveButton(
																	"确定", null)
															.show();
												else if (isNetError)
													new AlertDialog.Builder(
															MoreActivity.this)
															.setTitle("连接服务器失败")
															.setPositiveButton(
																	"确定", null)
															.show();
												else if (nullfile)
													new AlertDialog.Builder(
															MoreActivity.this)
															.setTitle("无备份")
															.setPositiveButton(
																	"确定", null)
															.show();
												else
													new AlertDialog.Builder(
															MoreActivity.this)
															.setTitle(
																	"登陆失败,请输入正确的用户名和密码")
															.setPositiveButton(
																	"确定", null)
															.show();
												Looper.loop();
											}
										}.start();
									}
								}).setNegativeButton("取消", null).show();
			}
		});
		
		
		concerning.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				new AlertDialog.Builder(MoreActivity.this)     
				.setTitle("关于") 
				.setMessage("制作人:mjx\n邮    箱: 938611049@qq.com \n本应用为测试应用,扩展性较高!") 
				.setPositiveButton("确定", null) 
				.show();
				
				
				
			}
		});
		
		
		assistance.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent();
				intent.setClass(MoreActivity.this, FeedbackActivity.class);
				startActivity(intent);
				
				
				
			}
		});
		
	}

	
	private boolean restoreBackup() {
		nullfile = false;
		isNetError = false;
		try {
			String settingTempFile = getBaseContext().getFilesDir().getPath()
					+ "\\DiggerSettings.properties";
			Properties properties = new Properties();
			properties.load(new FileInputStream(settingTempFile));
			String serverUrl = properties.getProperty("server").trim();
			String folderlistString = restoreHttp(new URL(serverUrl
					+ "downloadFolders?username=" + userName + "&password="
					+ password));
			System.out.println(folderlistString);
			if (folderlistString != null) {
				if (folderlistString.equals("0")) {
					nullfile = true;
					return false;
				}
				if (folderlistString.equals("-1"))
					return false;
				String resultPath;
				if (android.os.Environment.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED)) {
					resultPath = android.os.Environment
							.getExternalStorageDirectory()
							+ File.separator
							+ "RobotDiggerCSV/" + userName + "/";
				} else {
					resultPath = getBaseContext().getFilesDir().getPath()
							+ File.separator + "RobotDiggerCSV/" + userName
							+ "/";
				}
				FileUtils.delAllFile(resultPath);
				while (folderlistString.indexOf(",") != -1) {
					String resultfolder = resultPath
							+ folderlistString.substring(0,
									folderlistString.indexOf(",")) + "/";
					File folder = new File(resultfolder);
					if (!folder.exists())
						folder.mkdirs();
					String filelistString = restoreHttp(new URL(serverUrl
							+ "downloadFiles?username="
							+ userName
							+ "&foldername="
							+ new String(folderlistString.substring(0,
									folderlistString.indexOf(",")).getBytes(
									"UTF-8"), "ISO8859-1")));
					while (filelistString.indexOf(",") != -1) {
						downLoadFile(serverUrl
								+ "downloadFile?username="
								+ userName
								+ "&foldername="
								+ new String(folderlistString.substring(0,
										folderlistString.indexOf(","))
										.getBytes("UTF-8"), "ISO8859-1")+"&CSVname="+new String(filelistString.substring(0,
												filelistString.indexOf(","))
												.getBytes("UTF-8"), "ISO8859-1"),resultfolder,filelistString.substring(0, filelistString.indexOf(",")));
						filelistString = filelistString
								.substring(filelistString.indexOf(",") + 1);
					}
					folderlistString = folderlistString
							.substring(folderlistString.indexOf(",") + 1);
				}
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			isNetError = true;
			Log.d(this.toString(), e.getMessage() + "  127 line");
		}
		return false;
	}

	
	private String restoreHttp(URL url) throws IOException {
		HttpURLConnection conn = null;
		DataInputStream dis = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.connect();
			dis = new DataInputStream(conn.getInputStream());
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				Log.d(this.toString(),
						"getResponseCode() not HttpURLConnection.HTTP_OK");
				isNetError = true;
				return null;
			}
			String string=dis.readUTF();
			return string;
		} catch (Exception e) {
			return null;
		} finally {
			if(dis!=null){
				dis.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	
	public int downLoadFile(String strurl, String path, String fileName) {

		InputStream inputstream = null;
		FileUtils fileutils = new FileUtils();
		if (fileutils.isFileExist(path + fileName)) {
			return 1;
		} else {
			try {
				URL url = new URL(strurl);
				System.out.println(strurl);
				HttpURLConnection urlConn = (HttpURLConnection) url
						.openConnection();
				inputstream = urlConn.getInputStream();
				File file = fileutils.inputSD(path, fileName, inputstream);
				if (file == null) {
					return -1;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			} finally {
				try {
					inputstream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
