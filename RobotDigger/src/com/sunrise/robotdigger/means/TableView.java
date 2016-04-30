package com.sunrise.robotdigger.means;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.http.util.EncodingUtils;

import com.sunrise.robotdigger.utils.MemoryInfo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TableView extends ViewGroup {
	private static final int STARTX = 0;// 起始X坐标
	private static final int STARTY = 0;// 起始Y坐标
	private static final int BORDER = 2;// 表格边框宽度
	private static final String[] head = { "类别", "最大值", "临界值" };
	private int mRow;// 行数
	private int mCol;// 列数
	private List<String> title;
	private List<List<Double>> value;
	private String settingTempFile;

	public TableView(Context context, String path, String settingTempFile) {
		super(context);
		this.settingTempFile = settingTempFile;
		File file = new File(path);
		try {
			if (file.exists()) {
				FileInputStream fin = new FileInputStream(path);
				int length = fin.available();
				byte[] buffer = new byte[length];
				fin.read(buffer);
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String res = EncodingUtils.getString(buffer, "GBK");
				try {
					res = res.substring(res.indexOf("时间"));
				} catch (Exception e) {
					file.delete();
					fin.close();
					return;
				}
				String titles = res.substring(0, res.indexOf("\n"));
				titles = titles.substring(res.indexOf(",") + 1);
				title = new ArrayList<String>();
				while (titles.indexOf(",") != -1) {
					title.add(titles.substring(0, titles.indexOf(",")));
					titles = titles.substring(titles.indexOf(",") + 1);
				}
				title.add(titles);
				res = res.substring(res.indexOf("\n") + 1);
				value = new ArrayList<List<Double>>();
				boolean remove = false;
				List<Date> time = new ArrayList<Date>();
				while (res.indexOf("\n") != -1) {
					String t = res.substring(0, res.indexOf("\n"));
					time.add(formatter.parse(t.substring(0, t.indexOf(","))));
					t = t.substring(t.indexOf(",") + 1);
					int i = 0;
					while (t.indexOf(",") != -1) {
						List<Double> list = null;
						try {
							list = value.get(i);
						} catch (IndexOutOfBoundsException e) {
							value.add(new ArrayList<Double>());
							list = value.get(i);
						}
						list.add(Double.valueOf(t.substring(0, t.indexOf(","))));
						t = t.substring(t.indexOf(",") + 1);
						i++;
					}
					if (!t.trim().equals("本程序或本设备不支持流量统计")
							&& !t.trim().equals("")) {
						List<Double> list = null;
						try {
							list = value.get(i);
						} catch (IndexOutOfBoundsException e) {
							value.add(new ArrayList<Double>());
							list = value.get(i);
						}

						list.add(Double.valueOf(t));
					} else {
						remove = true;
					}
					res = res.substring(res.indexOf("\n") + 1);
				}
				if (remove)
					title.remove(title.size() - 1);
				fin.close();
				for (int i = title.size() - 1; i >= 0; i--) {
					if (title.get(i).indexOf("机器剩余内存(MB)") != -1
							|| title.get(i).indexOf("流量(KB)") != -1) {
						System.out.println(title.get(i));
						title.remove(i);
						value.remove(i);
					}
				}
				mRow = title.size() + 1;
				mCol = 3;
				this.addOtherView(context);
			}
		} catch (Exception e) {
		}
	}

	public void addOtherView(Context context) throws FileNotFoundException,
			IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(settingTempFile));
		String use_memory_max = properties.getProperty("use_memory_max").trim();
		String use_cpu_max = properties.getProperty("use_cpu_max").trim();
		String alluse_cpu_max = properties.getProperty("alluse_cpu_max").trim();
		for (int j = 0; j < mCol; j++) {
			TextView view = new TextView(context);
			view.setText(head[j]);
			view.setTextColor(Color.BLACK);
			view.setTextSize(30);
			view.setGravity(Gravity.CENTER);
			view.setBackgroundColor(Color.WHITE);
			this.addView(view);
		}
		for (int i = 0; i < mRow - 1; i++) {
			boolean exceed=false;
			String exceednum = null;
			Double maxDouble = null;
			if(title.get(i).indexOf("应用占用内存PSS")!=-1){
				exceednum=use_memory_max;
				maxDouble=max(i);
				if(maxDouble>Double.valueOf(exceednum))
					exceed=true;
			}else if (title.get(i).indexOf("应用占用内存比")!=-1) {
				MemoryInfo memoryInfo=new MemoryInfo();
				DecimalFormat df=new DecimalFormat(".##");
				exceednum=df.format(Double.valueOf(use_memory_max)/(memoryInfo.getTotalMemory()/1024)*100);
				maxDouble=max(i);
				if(maxDouble>Double.valueOf(exceednum))
					exceed=true;
			}else if (title.get(i).indexOf("应用占用CPU率")!=-1) {
				exceednum=use_cpu_max;
				maxDouble=max(i);
				if(maxDouble>Double.valueOf(exceednum))
					exceed=true;
			}else if (title.get(i).indexOf("CPU总使用率")!=-1) {
				exceednum=alluse_cpu_max;
				maxDouble=max(i);
				if(maxDouble>Double.valueOf(exceednum))
					exceed=true;
			}
			for (int j = 0; j < mCol; j++) {
				TextView view = new TextView(context);
				if (j == 0)
					view.setText(title.get(i));
				else if (j == 1) {
					view.setText(String.valueOf(maxDouble));
				} else if (j == 2) {
					view.setText(exceednum);
				}
				if(exceed)
					view.setTextColor(Color.RED);
				else
				view.setTextColor(Color.BLACK);
				view.setGravity(Gravity.CENTER);
				if (i % 2 == 0) {
					view.setBackgroundColor(Color.LTGRAY);
				} else {
					view.setBackgroundColor(Color.GRAY);
				}
				this.addView(view);
			}
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setStrokeWidth(BORDER);
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);
		// 绘制外部边框
		canvas.drawRect(STARTX, STARTY, getWidth() - STARTX, getHeight()
				- STARTY, paint);
		// 画列分割线
		for (int i = 1; i < mCol; i++) {
			canvas.drawLine((getWidth() / mCol) * i, STARTY,
					(getWidth() / mCol) * i, getHeight() - STARTY, paint);
		}
		// 画行分割线
		for (int j = 1; j < mRow; j++) {
			canvas.drawLine(STARTX, (getHeight() / mRow) * j, getWidth()
					- STARTX, (getHeight() / mRow) * j, paint);
		}
		super.dispatchDraw(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int x = STARTX + BORDER;
		int y = STARTY + BORDER;
		int i = 0;
		int count = getChildCount();
		for (int j = 0; j < count; j++) {
			View child = getChildAt(j);
			child.layout(x, y, x + getWidth() / mCol - BORDER * 2, y
					+ getHeight() / mRow - BORDER * 2);
			if (i >= (mCol - 1)) {
				i = 0;
				x = STARTX + BORDER;
				y += getHeight() / mRow;
			} else {
				i++;
				x += getWidth() / mCol;
			}
		}
	}
	private Double max(int i){
		Double  maxDouble;
		List<Double> list = value.get(i);
		maxDouble = (double) 0;
		for (Double d : list) {
			if (d > maxDouble)
				maxDouble = d;
		}
		return maxDouble;
	}

	public void setRow(int row) {
		this.mRow = row;
	}

	public void setCol(int col) {
		this.mCol = col;
	}

}