 package com.sunrise.robotdigger.home;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.sunrise.robotdigger.R;
import com.sunrise.robotdigger.widget.FlowIndicator;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.SimpleAdapter;
import android.widget.ListView;
import android.widget.Toast;
/**
 * 
 *@功能 主页Activity
 */
@SuppressWarnings("deprecation")
public class HomeActivity extends Activity{
	static final int SCROLL_ACTION = 0;
	ListView mExpandableListView;
	int[] tags = new int[] { 0, 0, 0, 0, 0 };
	String[] groups = new String[8];
	String[][] childs = new String[5][10];
	Gallery mGallery;
	GalleryAdapter mGalleryAdapter;
	FlowIndicator mMyView;
	Timer mTimer;
	private long exitTime = 0;
	
	
	private String getAvailMemory(){
		ActivityManager am=(ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi=new MemoryInfo();
		am.getMemoryInfo(mi);
		return Formatter.formatFileSize(getBaseContext(), mi.availMem);	
	}
	private String getTotalMemory(){
		String str1="/proc/meminfo";
		String str2;
		String[] arrayOfString;
		long initial_memory=0;
		try{
			FileReader localFileReader=new FileReader(str1);
			BufferedReader localBufferedReader=new BufferedReader(localFileReader,8192);
			str2=localBufferedReader.readLine();
			arrayOfString=str2.split("\\s+");
			for(String num:arrayOfString){
				Log.i(str2, num+"\t");
				initial_memory =Integer.valueOf(arrayOfString[1]).intValue()*1024;
				localBufferedReader.close();
			}
		}
		catch(IOException e){
			
		}
		return Formatter.formatFileSize(getBaseContext(), initial_memory);		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		// 初始化弹出菜单
		
		DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        TelephonyManager tm=(TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);
        
        groups[0]="设备型号:"+android.os.Build.MODEL;
        groups[1]="主屏尺寸:"+metrics.widthPixels + "*" + metrics.heightPixels;
        groups[2]="API Level:"+android.os.Build.VERSION.SDK;
        groups[3]="系统版本号:"+android.os.Build.VERSION.RELEASE;
        groups[4]="设备 ID:"+tm.getDeviceId();
        groups[5]="服务商名称:"+tm.getSimOperatorName();
        groups[6]="系统总内存:"+this.getTotalMemory();
        groups[7]="可用内存:"+this.getAvailMemory();

		prepareView();
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new MyTask(), 0, 5000);
	}

	private void prepareView() {
		mExpandableListView = (ListView) findViewById(R.id.expandableListView1);
		View header = LayoutInflater.from(this).inflate(R.layout.header_view,
				null);
		mGallery = (Gallery) header.findViewById(R.id.home_gallery);
		mMyView = (FlowIndicator) header.findViewById(R.id.myView);
		mGalleryAdapter = new GalleryAdapter(this);
		mMyView.setCount(mGalleryAdapter.getCount());
		mGallery.setAdapter(mGalleryAdapter);
		mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				mMyView.setSeletion(arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		mExpandableListView.addHeaderView(header);
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		for(String group:groups){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("title_view", group);
			listItem.add(map);
		}
		SimpleAdapter listItemAdapter = new SimpleAdapter(this,
				listItem, R.layout.list_group_item,
				new String[] { "title_view" }, new int[] { R.id.title_view });
		mExpandableListView.setAdapter(listItemAdapter);
	}

	private class MyTask extends TimerTask {
		@Override
		public void run() {
			mHandler.sendEmptyMessage(SCROLL_ACTION);
		}
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SCROLL_ACTION:

				MotionEvent e1 = MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN,
						89.333336f, 265.33334f, 0);
				MotionEvent e2 = MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(), MotionEvent.ACTION_UP,
						300.0f, 238.00003f, 0);

				mGallery.onFling(e1, e2, -1300, 0);
				break;

			default:
				break;
			}
		}
	};

	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	     if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
	         if((System.currentTimeMillis()-exitTime) > 2000){  
	             Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                
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
