package com.sunrise.robotdigger.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

import com.sunrise.robotdigger.R;
import com.sunrise.robotdigger.utils.*;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DiggerService extends Service {

	private static final String LOG_TAG = "Digger-";
	public static BufferedWriter bufferedWriter;
	public static FileOutputStream fileOutputStream;
	public static OutputStreamWriter outputStreamWriter;
	private boolean isServiceStop;
	public static boolean isStop = false;
	private MemoryInfo memoryInfo;
	private DecimalFormat fomart;
	private int pid;
	private int uid;
	private float x;
	private float y;
	private float startX;
	private float startY;
	private float mTouchStartX;
	private float mTouchStartY;
	private String processName;
	private String packageName;
	private String settingTempFile;
	private CpuInfo cpuInfo;
	private boolean isFloating;
	private String time;
	private int delaytime;
	public static String resultFilePath;
	private View viFloatingWindow;
	private TextView txtTotalMem;
	private TextView txtTotaledMem;
	private TextView txtUnusedMem;
	private TextView txtTraffic;
	private ImageView suspended;
	private ImageView stop;
	private boolean isSuspended;
	private WindowManager windowManager;
	private LayoutParams wmParams;
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private Handler handler = new Handler();

	public void onCreate() {
		Log.i(LOG_TAG, "onCreate");
		super.onCreate();
		isServiceStop = false;
		isStop = false;
		memoryInfo = new MemoryInfo();
		fomart = new DecimalFormat();
		fomart.setMaximumFractionDigits(2);
		fomart.setMinimumFractionDigits(0);
	}

	public static void closeOpenedStream() {
		try {
			if (bufferedWriter != null)
				bufferedWriter.close();
			if (outputStreamWriter != null)
				outputStreamWriter.close();
			if (fileOutputStream != null)
				fileOutputStream.close();
		} catch (Exception e) {
			Log.d(LOG_TAG, e.getMessage());
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		startForeground(1, new Notification());
		super.onStart(intent, startId);
		isSuspended = false;
		pid = intent.getExtras().getInt("pid");
		uid = intent.getExtras().getInt("uid");
		processName = intent.getExtras().getString("processName");
		packageName = intent.getExtras().getString("packageName");
		settingTempFile = intent.getExtras().getString("settingTempFile");
		cpuInfo = new CpuInfo(getBaseContext(), pid, Integer.toString(uid));
		readSettingInfo(intent);
		delaytime = Integer.parseInt(time) * 1000;
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(settingTempFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String color = properties.getProperty("color").trim();
		if (isFloating) {
			viFloatingWindow = LayoutInflater.from(this).inflate(
					R.layout.view_float, null);
			txtUnusedMem = (TextView) viFloatingWindow
					.findViewById(R.id.memunused);
			txtTotaledMem = (TextView) viFloatingWindow
					.findViewById(R.id.memunusepercentage);
			txtTotalMem = (TextView) viFloatingWindow
					.findViewById(R.id.memtotal);
			txtTraffic = (TextView) viFloatingWindow.findViewById(R.id.traffic);
			suspended = (ImageView) viFloatingWindow.findViewById(R.id.suspended);
			stop=(ImageView) viFloatingWindow.findViewById(R.id.stop);
			suspended.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isSuspended) {
						suspended.setBackgroundResource(R.drawable.play2);
						isSuspended = false;
					} else {
						suspended.setBackgroundResource(R.drawable.play1);
						isSuspended = true;
					}
				}
			});
			stop.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					stopSelf();
				}
			});
			txtUnusedMem.setText("计算中,请稍后...");
			if (color.equals("0")) {
				txtUnusedMem.setTextColor(android.graphics.Color.BLACK);
				txtTotalMem.setTextColor(android.graphics.Color.BLACK);
				txtTraffic.setTextColor(android.graphics.Color.BLACK);
				txtTotaledMem.setTextColor(android.graphics.Color.BLACK);
			} else if (color.equals("1")) {
				txtUnusedMem.setTextColor(android.graphics.Color.WHITE);
				txtTotalMem.setTextColor(android.graphics.Color.WHITE);
				txtTraffic.setTextColor(android.graphics.Color.WHITE);
				txtTotaledMem.setTextColor(android.graphics.Color.WHITE);
			} else if (color.equals("2")) {
				txtUnusedMem.setTextColor(android.graphics.Color.BLUE);
				txtTotalMem.setTextColor(android.graphics.Color.BLUE);
				txtTraffic.setTextColor(android.graphics.Color.BLUE);
				txtTotaledMem.setTextColor(android.graphics.Color.BLUE);
			} else if (color.equals("3")) {
				txtUnusedMem.setTextColor(android.graphics.Color.RED);
				txtTotalMem.setTextColor(android.graphics.Color.RED);
				txtTraffic.setTextColor(android.graphics.Color.RED);
				txtTotaledMem.setTextColor(android.graphics.Color.RED);
			} else if (color.equals("4")) {
				txtUnusedMem.setTextColor(android.graphics.Color.GREEN);
				txtTotalMem.setTextColor(android.graphics.Color.GREEN);
				txtTraffic.setTextColor(android.graphics.Color.GREEN);
				txtTotaledMem.setTextColor(android.graphics.Color.GREEN);
			}
			createFloatingWindow();
		}
		createResultCsv();
		handler.postDelayed(task, 1000);
	}

	private void createResultCsv() {
		Calendar cal = Calendar.getInstance();//日历对象
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String mDateTime;
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		String userName = share.getString(SHARE_LOGIN_USERNAME, "");
		if ((Build.MODEL.equals("sdk")) || (Build.MODEL.equals("google_sdk")))
			mDateTime = formatter.format(cal.getTime().getTime() + 8 * 60 * 60
					* 1000);
		else
			mDateTime = formatter.format(cal.getTime().getTime());

		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			resultFilePath = android.os.Environment
					.getExternalStorageDirectory()
					+ File.separator
					+ "RobotDiggerCSV/" + userName + "/" + processName + "/";
		} else {
			resultFilePath = getBaseContext().getFilesDir().getPath()
					+ File.separator + "RobotDiggerCSV/" + userName + "/" + processName + "/";
		}
		try {
			File folder = new File(resultFilePath);
			if (!folder.exists())
				folder.mkdirs();
			resultFilePath += "RobotDiggerResult_" + processName + "_"
					+ mDateTime + ".csv";
			File resultFile = new File(resultFilePath);
			resultFile.createNewFile();
			fileOutputStream = new FileOutputStream(resultFile);
			outputStreamWriter = new OutputStreamWriter(fileOutputStream, "GBK");
			bufferedWriter = new BufferedWriter(outputStreamWriter);
			long totalMemorySize = memoryInfo.getTotalMemory();
			String totalMemory = fomart.format((double) totalMemorySize / 1024);
			Properties properties = new Properties();
			properties.load(new FileInputStream(settingTempFile));
			bufferedWriter.write("指定应用的CPU内存监控情况\r\n" + "应用包名:," + packageName
					+ "\r\n" + "应用名称: ," + processName + "\r\n" + "应用PID: ,"
					+ pid + "\r\n" + "机器内存大小(MB)：," + totalMemory + "MB\r\n"
					+ "机器CPU型号:," + cpuInfo.getCpuName() + "\r\n"
					+ "机器android系统版本:," + memoryInfo.getSDKVersion() + "\r\n"
					+ "手机型号:," + memoryInfo.getPhoneType() + "\r\n" + "UID：,"
					+ uid + "\r\n");
			bufferedWriter.write("时间" + ",");
			if ("false".equals(properties.getProperty("use_memory").trim()) ? false
					: true)
				bufferedWriter.write("应用占用内存PSS(MB)" + ",");
			if ("false".equals(properties.getProperty("percentage_memory")
					.trim()) ? false : true)
				bufferedWriter.write("应用占用内存比(%)" + ",");
			if ("false".equals(properties.getProperty("remain_memory").trim()) ? false
					: true)
				bufferedWriter.write("机器剩余内存(MB)" + ",");
			if ("false".equals(properties.getProperty("use_cpu").trim()) ? false
					: true)
				bufferedWriter.write("应用占用CPU率(%)" + ",");
			if ("false".equals(properties.getProperty("alluse_cpu").trim()) ? false
					: true)
				bufferedWriter.write("CPU总使用率(%)" + ",");
			if ("false".equals(properties.getProperty("information_flow")
					.trim()) ? false : true)
				bufferedWriter.write("流量(KB)");
			bufferedWriter.write("\r\n");
			properties.setProperty("lastcsv", resultFilePath);
			FileOutputStream fos = new FileOutputStream(settingTempFile);//文件进行输出
			properties.store(fos, "Setting Data");
			fos.close();
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
	}

	private void createFloatingWindow() {
		SharedPreferences shared = getSharedPreferences("float_flag",
				Activity.MODE_PRIVATE);//数据存储方式
		SharedPreferences.Editor editor = shared.edit();
		editor.putInt("float", 1);
		editor.commit();
		windowManager = (WindowManager) getApplicationContext()
				.getSystemService("window");
		getApplication();
		wmParams = ((MyApplication) getApplication()).getMywmParams();
		wmParams.type = 2002;
		wmParams.flags |= 8;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		wmParams.x = 0;
		wmParams.y = 0;
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.format = 1;
		windowManager.addView(viFloatingWindow, wmParams);
		viFloatingWindow.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				x = event.getRawX();
				y = event.getRawY() - 25;
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// state = MotionEvent.ACTION_DOWN;
					startX = x;
					startY = y;
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					Log.d("startP", "startX" + mTouchStartX + "====startY"
							+ mTouchStartY);
					break;
				case MotionEvent.ACTION_MOVE:
					// state = MotionEvent.ACTION_MOVE;
					updateViewPosition();
					break;

				case MotionEvent.ACTION_UP:
					// state = MotionEvent.ACTION_UP;
					updateViewPosition();
					mTouchStartX = mTouchStartY = 0;
					break;
				}
				return true;
			}
		});
	}

	private void updateViewPosition() {
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);
		windowManager.updateViewLayout(viFloatingWindow, wmParams);
	}

	private void readSettingInfo(Intent intent) {
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(settingTempFile));
			String interval = properties.getProperty("interval").trim();
			isFloating = "true"
					.equals(properties.getProperty("isfloat").trim()) ? true
					: false;
			time = "".equals(interval) ? "1" : interval;
		} catch (IOException e) {
			time = "1";
			isFloating = true;
			Log.e(LOG_TAG, e.getMessage());
		}
	}

	private Runnable task = new Runnable() {

		public void run() {
			if (!isServiceStop) {
				if (!isSuspended)
					dataRefresh();
				handler.postDelayed(this, delaytime);
				if (isFloating)
					windowManager.updateViewLayout(viFloatingWindow, wmParams);
			} else {
				Intent intent = new Intent();
				intent.putExtra("isServiceStop", true);
				intent.setAction("com.sunrise.robotdigger.action.DiggerService");
				sendBroadcast(intent);
				stopSelf();
			}
		}
	};

	private void dataRefresh() {
		int pidMemory = memoryInfo.getPidMemorySize(pid, getBaseContext());
		long freeMemory = memoryInfo.getFreeMemorySize(getBaseContext());
		String freeMemoryKb = fomart.format((double) freeMemory / 1024);
		String processMemory = fomart.format((double) pidMemory / 1024);
		ArrayList<String> processInfo = cpuInfo
				.getCpuRatioInfo(settingTempFile);
		if (isFloating) {
			String processCpuRatio = "0";
			String totalCpuRatio = "0";
			String trafficSize = "0";
			String percent = "0";
			int tempTraffic = 0;
			double trafficMb = 0;
			boolean isMb = false;
			if (!processInfo.isEmpty()) {
				processCpuRatio = processInfo.get(0);
				totalCpuRatio = processInfo.get(1);
				trafficSize = processInfo.get(2);
				percent = processInfo.get(3);
				if ("".equals(trafficSize) && !("-1".equals(trafficSize))) {
					tempTraffic = Integer.parseInt(trafficSize);
					if (tempTraffic > 1024) {
						isMb = true;
						trafficMb = (double) tempTraffic / 1024;
					}
				}
			}
			if ("0".equals(processMemory) && "0.00".equals(processCpuRatio)) {
				closeOpenedStream();
				isServiceStop = true;
				return;
			}
			if (processCpuRatio != null && totalCpuRatio != null) {
				Properties properties = new Properties();
				try {
					properties.load(new FileInputStream(settingTempFile));
					String textString = "";
					if ("false".equals(properties.getProperty("use_memory")
							.trim()) ? false : true)
						textString += "占用内存:" + processMemory + "MB,";
					if ("false".equals(properties.getProperty("remain_memory")
							.trim()) ? false : true)
						textString += " 机器剩余:" + freeMemoryKb + "MB";
					txtUnusedMem.setText(textString);
					textString = "";
					if ("false".equals(properties.getProperty(
							"percentage_memory").trim()) ? false : true)
						textString += " 占用内存比:" + percent + "%";
					txtTotaledMem.setText(textString);
					textString = "";
					if ("false"
							.equals(properties.getProperty("use_cpu").trim()) ? false
							: true)
						textString += "占用CPU:" + processCpuRatio + "%";
					if ("false".equals(properties.getProperty("alluse_cpu")
							.trim()) ? false : true)
						textString += " 总体CPU:" + totalCpuRatio + "%";
					txtTotalMem.setText(textString);
					if ("false".equals(properties.getProperty(
							"information_flow").trim()) ? false : true) {
						if ("-1".equals(trafficSize)) {
							txtTraffic.setText("本程序或本设备不支持流量统计");
						} else if (isMb)
							txtTraffic.setText("消耗流量:"
									+ fomart.format(trafficMb) + "MB");
						else
							txtTraffic.setText("消耗流量:" + trafficSize + "KB");
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		stopForeground(true);
		if (windowManager != null)
			windowManager.removeView(viFloatingWindow);
		handler.removeCallbacks(task);
		closeOpenedStream();
		isStop = true;
		super.onDestroy();
	}
}
