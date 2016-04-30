package com.sunrise.robotdigger.means;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;
import org.apache.http.util.EncodingUtils;

import com.sunrise.robotdigger.R;
import com.sunrise.robotdigger.show.ShowActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 
 * @功能 绘图Activity
 */
public class XYChartBuilder extends Activity {
	public static final String TYPE = "type";
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private TimeSeries mCurrentSeries;
	private XYSeriesRenderer mCurrentRenderer;
	private String mDateFormat;
	private List<String> seriesname = new ArrayList<String>();
	private GraphicalView mChartView;
	private int color[] = { Color.RED, Color.CYAN, Color.MAGENTA, Color.GREEN,
			Color.WHITE, Color.YELLOW };
	private List<Date> time = new ArrayList<Date>();

	/*提取保存数据 恢复Activity状态*/
	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		mDataset = (XYMultipleSeriesDataset) savedState
				.getSerializable("dataset");
		mRenderer = (XYMultipleSeriesRenderer) savedState
				.getSerializable("renderer");
		mCurrentSeries = (TimeSeries) savedState
				.getSerializable("current_series");
		mCurrentRenderer = (XYSeriesRenderer) savedState
				.getSerializable("current_renderer");
		mDateFormat = savedState.getString("date_format");
	}

/* 在Activity销毁前 保存数据状态   */   
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("dataset", mDataset);
		outState.putSerializable("renderer", mRenderer);
		outState.putSerializable("current_series", mCurrentSeries);
		outState.putSerializable("current_renderer", mCurrentRenderer);
		outState.putString("date_format", mDateFormat);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chart);
		Intent intent = this.getIntent();
		String path = intent.getStringExtra("path");
		File file = new File(path);
		try {
			if (file.exists()) {
				mRenderer.setApplyBackgroundColor(true);// 设置是否显示背景色
				mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
				mRenderer.setAxisTitleTextSize(16); // 设置轴标题文字的大小
				mRenderer.setChartTitleTextSize(20);// ?设置整个图表标题文字大小
				mRenderer.setLabelsTextSize(15);// 设置刻度显示文字的大小(XY轴都会被设置)
				mRenderer.setLegendTextSize(15);// 图例文字大小
				mRenderer.setMargins(new int[] { 0, 10, 0, 10 });
				mRenderer.setZoomButtonsVisible(true);// 是否显示放大缩小按钮
				mRenderer.setPointSize(5);// 设置点的大小(图上显示的点的大小和图例中点的大小都会被设置)
				mRenderer.setShowGrid(true);

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
					// TODO: handle exception
					file.delete();
					fin.close();
					XYChartBuilder.this.finish();
					return;
				}
				String titles = res.substring(0, res.indexOf("\n"));
				titles = titles.substring(res.indexOf(",") + 1);
				List<String> title = new ArrayList<String>();
				while (titles.indexOf(",") != -1) {
					title.add(titles.substring(0, titles.indexOf(",")));
					titles = titles.substring(titles.indexOf(",") + 1);
				}
				title.add(titles);
				res = res.substring(res.indexOf("\n") + 1);
				List<List<Double>> value = new ArrayList<List<Double>>();
				boolean remove = false;
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
				if (intent.getStringExtra("type").equals("0")) {
					for (int i = 0; i < title.size(); i++) {
						addSeries(title.get(i), time, value.get(i));
					}
					if (mChartView != null) {
						mChartView.repaint();// 重画图表
					}
				} else {
					for (int i = 0; i < title.size(); i++) {
						if (title.get(i).trim()
								.equals(intent.getStringExtra("type"))) {
							addSeries(title.get(i), time, value.get(i));
							if (mChartView != null) {
								mChartView.repaint();// 重画图表
							}
							break;
						}
					}
				}
			} else {
				XYChartBuilder.this.finish();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/*数据绘制*/
	public void addSeries(String seriesname, List<Date> x, List<Double> y) {
		TimeSeries series = new TimeSeries(seriesname);// 定义XYSeries
		mDataset.addSeries(series);// 在XYMultipleSeriesDataset中添加XYSeries
		mCurrentSeries = series;// 设置当前需要操作的XYSeries
		XYSeriesRenderer renderer = new XYSeriesRenderer();// 定义XYSeriesRenderer
		mRenderer.addSeriesRenderer(renderer);// 将单个XYSeriesRenderer增加到XYMultipleSeriesRenderer
		renderer.setColor(color[mDataset.getSeriesCount() - 1]);
		renderer.setPointStyle(PointStyle.CIRCLE);// 点的类型是圆形
		renderer.setFillPoints(true);// 设置点是否实心
		mCurrentRenderer = renderer;
		this.seriesname.add(seriesname);
		for (int i = 0; i < x.size(); i++) {
			mCurrentSeries.add(x.get(i), y.get(i));
		}
	}

/* 在 onResume 里进行绘制 在横屏切换时自动调用   */  
	@Override
	protected void onResume() {
		super.onResume();
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getTimeChartView(this, mDataset,
					mRenderer, "HH:mm:ss");
			mRenderer.setClickEnabled(true);// 设置图表是否允许点击
			mRenderer.setSelectableBuffer(10);// 设置点的缓冲半径值(在某点附件点击时,多大范围内都算点击这个点)
			mChartView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// 这段代码处理点击一个点后,获得所点击的点在哪个序列中以及点的坐标.
					// -->start
					SeriesSelection seriesSelection = mChartView
							.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {
					} else {
						SimpleDateFormat formatter = new SimpleDateFormat(
								"HH:mm:ss");
						Toast.makeText(
								XYChartBuilder.this,
								"现在点击的是 "
										+ (String) seriesname
												.get(seriesSelection
														.getSeriesIndex())
										+ " 第"
										+ (seriesSelection.getPointIndex() + 1)
										+ " 个点"
										+ "  时间为"
										+ formatter.format(time
												.get(seriesSelection
														.getPointIndex()))
										+ ", "
										+ (String) seriesname
												.get(seriesSelection
														.getSeriesIndex())
										+ "为" + seriesSelection.getValue(),
								Toast.LENGTH_LONG).show();
					}
					// -->end
				}
			});
			mChartView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					SeriesSelection seriesSelection = mChartView
							.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {
						Toast.makeText(XYChartBuilder.this,
								"No chart element was long pressed",
								Toast.LENGTH_SHORT);
						return false; // no chart element was long pressed, so
										// let something
						// else handle the event
					} else {
						Toast.makeText(XYChartBuilder.this,
								"Chart element in series index "
										+ seriesSelection.getSeriesIndex()
										+ " data point index "
										+ seriesSelection.getPointIndex()
										+ " was long pressed",
								Toast.LENGTH_SHORT);
						return true; // the element was long pressed - the event
										// has been
						// handled
					}
				}
			});
			// 这段代码处理放大缩小
			// -->start
			mChartView.addZoomListener(new ZoomListener() {
				public void zoomApplied(ZoomEvent e) {
					String type = "out";
					if (e.isZoomIn()) {
						type = "in";
					}
				}

				public void zoomReset() {
					System.out.println("Reset");
				}
			}, true, true);
			// -->end
			// 设置拖动图表时后台打印出图表坐标的最大最小值.

			layout.addView(mChartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
			mChartView.repaint();
		}
	}

}