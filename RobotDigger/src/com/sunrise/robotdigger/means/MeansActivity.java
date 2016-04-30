package com.sunrise.robotdigger.means;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


import com.sunrise.robotdigger.R;
import com.sunrise.robotdigger.utils.FileUtils;
import com.sunrise.robotdigger.utils.FormFile;
import com.sunrise.robotdigger.utils.MailSender;
import com.sunrise.robotdigger.utils.SocketHttpRequester;
import com.sunrise.robotdigger.widget.RefreshView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**
 * 
 * @功能 测试文件Activity
 */
public class MeansActivity extends Activity {

	private String putingTempFile;
	private String[] fileList;
	private String filename;
	private RefreshView list;
	private String num;
	private ProgressDialog pd;
	private String userName;
	private String password;
	private String email;
	private Button m_button_1, m_button_2;
	Bundle savedInstanceState;
	private long exitTime = 0;
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";
	private String SHARE_LOGIN_EMAIL = "MAP_LOGIN_EMAIL";
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			pd.dismiss();
		}
	};

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		this.savedInstanceState = savedInstanceState;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.means);
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		userName = share.getString(SHARE_LOGIN_USERNAME, "");
		password = share.getString(SHARE_LOGIN_PASSWORD, "");
		email = share.getString(SHARE_LOGIN_EMAIL, "");
		list = (RefreshView) findViewById(R.id.ListView01);
		m_button_1 = (Button) findViewById(R.id.m_button_1);
		m_button_2 = (Button) findViewById(R.id.m_button_2);
		m_button_1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(MeansActivity.this)
						.setTitle("清空所有文件")
						.setMessage("清空后，所以测试文件不可恢复。确定要清空?")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										FileUtils.delAllFile(putingTempFile);
										onCreate(savedInstanceState);
									}
								}).setNegativeButton("取消", null).show();

			}
		});
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
		putingTempFile += userName + "/";
		try {
			String string=savedInstanceState.getString("foldername");
			if(string==null)
				throw new NullPointerException(null);
			putingTempFile += string + "/";
		} catch (NullPointerException e) {
			m_button_2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new AlertDialog.Builder(MeansActivity.this)
							.setTitle("备份文件")
							.setMessage("备份后，上次备份不可恢复。确定要备份?")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											pd = ProgressDialog.show(
													MeansActivity.this, "备份文件",
													"正在备份…");
											new Thread() {
												public void run() {
													boolean upload = uploadFiles();
													handler.sendEmptyMessage(0);
													Looper.prepare();
													if (upload)
														new AlertDialog.Builder(
																MeansActivity.this)
																.setTitle(
																		"备份成功")
																.setPositiveButton(
																		"确定",
																		null)
																.show();
													else
														showDialog(3);
													Looper.loop();
												}
											}.start();
										}
									}).setNegativeButton("取消", null).show();
				}
			});
			File putingFile = new File(putingTempFile);
			ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return dir.isDirectory();
				}
			};
			fileList = putingFile.list(filter);
			for (String fileListString : fileList) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemTitle", fileListString);
				listItem.add(map);
			}
			final SimpleAdapter listItemAdapter = new SimpleAdapter(this,
					listItem, R.layout.listview_item,
					new String[] { "ItemTitle" }, new int[] { R.id.ItemTitle });
			list.setAdapter(listItemAdapter);
			list.setonRefreshListener(new com.sunrise.robotdigger.widget.RefreshView.OnRefreshListener() {
				public void onRefresh() {
					new AsyncTask<Void, Void, Void>() {
						protected Void doInBackground(Void... params) {
							try {
								Thread.sleep(1000);
							} catch (Exception e) {
								e.printStackTrace();
							}
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							listItemAdapter.notifyDataSetChanged();
							list.onRefreshComplete();
							onCreate(savedInstanceState);
						}

					}.execute();
				}
			});
			if (fileList.length == 0) {
				HashMap<String, Object> Map = new HashMap<String, Object>();
				Map.put("ItemTitle", "");
				listItem.add(Map);
			} else {
				list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						menu.add(0, 9, 0, "删除");
					}
				});
				list.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (arg2 - 1 == fileList.length)
							return;
						Bundle bundle = new Bundle();
						bundle.putString("foldername", fileList[arg2 - 1]);
						onCreate(bundle);
					}
				});
			}
			return;
		}
		m_button_2.setText("返回");
		m_button_2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onCreate(null);
			}
		});
		File putingFile = new File(putingTempFile);
		System.out.println(putingTempFile);
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("RobotDiggerResult");
			}
		};
		fileList = putingFile.list(filter);
		for (String fileListString : fileList) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemTitle", fileListString);
			listItem.add(map);
		}
		final SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.listview_item, new String[] { "ItemTitle" },
				new int[] { R.id.ItemTitle });
		list.setAdapter(listItemAdapter);
		list.setonRefreshListener(new com.sunrise.robotdigger.widget.RefreshView.OnRefreshListener() {
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						listItemAdapter.notifyDataSetChanged();
						list.onRefreshComplete();
						onCreate(savedInstanceState);
					}

				}.execute();
			}
		});

		if (fileList.length == 0) {
			HashMap<String, Object> Map = new HashMap<String, Object>();
			Map.put("ItemTitle", "");
			listItem.add(Map);
		} else {
			list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

				@Override
				public void onCreateContextMenu(ContextMenu menu, View v,
						ContextMenuInfo menuInfo) {
					menu.setHeaderTitle("功能选择菜单");
					menu.add(0, 10, 0, "显示报表");
					menu.add(0, 0, 0, "应用占用内存PSS(MB)");
					menu.add(0, 1, 0, "应用占用内存比(%)");
					menu.add(0, 2, 0, "机器剩余内存(MB)");
					menu.add(0, 3, 0, "应用占用CPU率(%)");
					menu.add(0, 4, 0, "CPU总使用率(%)");
					menu.add(0, 5, 0, "流量(KB)");
					menu.add(0, 6, 0, "上传文件");
					menu.add(0, 7, 0, "发送到邮箱");
					menu.add(0, 8, 0, "删除");
				}
			});
			list.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if (arg2 - 1 == fileList.length)
						return;
					Intent intent = new Intent();
					intent.setClass(MeansActivity.this, XYChartBuilder.class);
					intent.putExtra("path", putingTempFile + fileList[arg2 - 1]);
					intent.putExtra("type", "0");
					startActivityForResult(intent, Activity.RESULT_FIRST_USER);
				}
			});
		}
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
				.build());
	} // 长按菜单响应函数

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			return new AlertDialog.Builder(this)
					.setTitle("确定删除文件？")
					.setPositiveButton(
							"确定",
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									File file = new File(filename);
									if (file.exists()) {
										file.delete();
										onCreate(savedInstanceState);
									}
								}
							}).setNegativeButton("取消", null).create();
		case 1:
			return new AlertDialog.Builder(this)
					.setTitle("确定上传文件？")
					.setPositiveButton(
							"确定",
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									pd = ProgressDialog
											.show(MeansActivity.this, "上传文件",
													"正在上传…");
									new Thread() {
										public void run() {
											num = uploadFile();
											handler.sendEmptyMessage(0);
											Looper.prepare();
											if (num != null)
												showDialog(2);
											else
												showDialog(3);
											Looper.loop();
										}
									}.start();
								}
							}).setNegativeButton("取消", null).create();
		case 2:
			return new AlertDialog.Builder(MeansActivity.this)
					.setTitle("文件提取码为" + num).setPositiveButton("确定", null)
					.create();
		case 3:
			return new AlertDialog.Builder(MeansActivity.this)
					.setTitle("无法连接至服务器").setPositiveButton("确定", null)
					.create();
		case 4:
			return new AlertDialog.Builder(this)
					.setTitle("确定删除文件夹？")
					.setPositiveButton(
							"确定",
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									FileUtils.delFolder(filename);
									onCreate(savedInstanceState);
								}
							}).setNegativeButton("取消", null).create();
		case 5:
			return new AlertDialog.Builder(MeansActivity.this)
					.setTitle("确定发送邮箱?")
					.setPositiveButton(
							"确定",
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									pd = ProgressDialog
											.show(MeansActivity.this, "发送邮箱",
													"正在发送…");
									new Thread() {
										public void run() {
											String[] receivers = { email };
											boolean isSendSuccessfully = MailSender.sendTextMail(
													MeansActivity.this
															.getString(R.string.email),
													MeansActivity.this
															.getString(R.string.email_password),
													MeansActivity.this
															.getString(R.string.smtpserver),
													"RobotDigger",
													MeansActivity.this
															.getString(R.string.email_file),
													filename, receivers);
											handler.sendEmptyMessage(0);
											Looper.prepare();
											if (isSendSuccessfully)
												new AlertDialog.Builder(
														MeansActivity.this)
														.setTitle("发送成功")
														.setPositiveButton(
																"确定", null)
														.show();
											else
												showDialog(3);
											Looper.loop();
										}
									}.start();

								}
							}).setNegativeButton("取消", null).create();
		default:
			return null;
		}
	}

	public boolean uploadFiles() {
		try {
			String settingTempFile = getBaseContext().getFilesDir().getPath()
					+ "\\DiggerSettings.properties";
			Properties properties = new Properties();
			properties.load(new FileInputStream(settingTempFile));
			String requestUrl = properties.getProperty("server").trim()
					+ "uploadfiles";
			Map<String, String> params = new HashMap<String, String>();
			params.put("username", userName);
			params.put("password", password);
			params.put("appname", "");
			File file = new File(putingTempFile);
			if (file.exists()) {
				FilenameFilter filter = new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return dir.isDirectory();
					}
				};
				if (!SocketHttpRequester.posts(requestUrl, params, null))
					return false;
				String[] folders = file.list(filter);
				for (String folder : folders) {
					params.put("appname", folder);
					String[] tempList = new File(putingTempFile + folder)
							.list();
					FormFile[] formFiles = new FormFile[tempList.length];
					File temp = null;
					for (int i = 0; i < tempList.length; i++) {
						temp = new File(putingTempFile + folder
								+ File.separator + tempList[i]);
						FormFile formfile = new FormFile(temp.getName(), temp,
								"file", "application/octet-stream");
						formFiles[i] = formfile;
					}
					SocketHttpRequester.posts(requestUrl, params, formFiles);
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String uploadFile() {
		File file = new File(filename);
		try {
			String settingTempFile = getBaseContext().getFilesDir().getPath()
					+ "\\DiggerSettings.properties";
			Properties properties = new Properties();
			properties.load(new FileInputStream(settingTempFile));
			String requestUrl = properties.getProperty("server").trim()
					+ "upload";
			Map<String, String> params = new HashMap<String, String>();
			params.put("savePath", "CSV");
			params.put("username", userName);
			params.put("password", password);
			FormFile formfile = new FormFile(file.getName(), file, "file",
					"application/octet-stream");
			return SocketHttpRequester.post(requestUrl, params, formfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		if (info.position - 1 == fileList.length)
			return super.onContextItemSelected(item);
		filename = putingTempFile + fileList[info.position - 1];
		if (item.getItemId() == 6) {
			showDialog(1);
		} else if (item.getItemId() == 7) {
			showDialog(5);
		} else if (item.getItemId() == 8) {
			showDialog(0);
		} else if (item.getItemId() == 9) {
			showDialog(4);
		} else if (item.getItemId() == 10) {
			Intent intent = new Intent();
			intent.setClass(MeansActivity.this, ReadCSVActivity.class);
			intent.putExtra("CSV", filename);
			startActivityForResult(intent, Activity.RESULT_FIRST_USER);
			// TODO Auto-generated method stub
		} else {
			Intent intent = new Intent();
			intent.setClass(MeansActivity.this, XYChartBuilder.class);
			intent.putExtra("path", putingTempFile
					+ fileList[info.position - 1]);
			intent.putExtra("type", item.getTitle());
			startActivityForResult(intent, Activity.RESULT_FIRST_USER);
		}
		return super.onContextItemSelected(item);
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
