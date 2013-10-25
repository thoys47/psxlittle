package jp.thoy.psxlittle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import jp.thoy.psxlittle.R;

import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.util.MathHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;

public class ChartDrawTask extends AsyncTask<Param, Integer, Result> {
	private final String CNAME = CommTools.getLastPart(this.getClass().getName(),".");
	private final static boolean isDebug = false;
	
	Context mContext;
	ProgressDialog pDialog;
	Activity mActivity;
	int mChartId;
	
	public ChartDrawTask(Context context,Activity activity){
		mContext = context;
		mActivity = activity;
	}
	
	@Override
	protected void onPostExecute(Result result) {
		// TODO 自動生成されたメソッド・スタブ
		super.onPostExecute(result);
		if(result == null){
			return;
		}
		LinearLayout lLayout = (LinearLayout)mActivity.findViewById(mChartId);
		ChartExecuter eChart = new ChartExecuter(result.chartSettings);
		GraphicalView gView = eChart.execute(mContext);
		lLayout.addView(gView);
		pDialog.dismiss();
	}

	@Override
	protected void onPreExecute() {
		// TODO 自動生成されたメソッド・スタブ
		super.onPreExecute();
		pDialog = new ProgressDialog(mActivity);
		pDialog.setMessage(mActivity.getString(R.string.strLoading));
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	@Override
	protected Result doInBackground(Param... params) {
		// TODO 自動生成されたメソッド・スタブ

		String key = params[0].key;
		int page = Integer.parseInt(params[0].page);
		String[] titles = new String[]{"CPU","Memory","Battery"};
		int[] colors = new int[]{Color.rgb(255, 127, 0),Color.rgb(0, 127, 255),Color.rgb(0,255,127)};
		PointStyle[] styles = new PointStyle[]{PointStyle.CIRCLE,PointStyle.DIAMOND,PointStyle.SQUARE};
		mChartId = params[0].chartId;
		
		ChartSettings chartSettings = new ChartSettings();
		chartSettings.titles = new String[]{titles[page]};
		chartSettings.colors = new int[]{colors[page]};
		chartSettings.styles = new PointStyle[]{styles[page]};
		chartSettings.lineWidth = new float[]{10.0f};
		chartSettings.chartName = key;
		chartSettings.xString = "Time";
		chartSettings.yString = "Rate(%)";
		chartSettings.max = 0;
		chartSettings.min = 0;
	    
		Result result = new Result();
		
		try{
			DataObject dObject = new DataObject(mContext);
			
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			PSXShared pShared = new PSXShared(mContext);
			int length = pShared.getLength();
			int interval = pShared.getInterval();
			calendar.add(Calendar.HOUR_OF_DAY, (-1) * length);
			calendar.add(Calendar.MINUTE, ((-1) * (calendar.get(Calendar.MINUTE) % interval)) + interval);
			calendar.add(Calendar.SECOND, (-1) * calendar.get(Calendar.SECOND)); 
			String fString = CommTools.CalendarToString(calendar, CommTools.DATETIMELONG);
			String sql = "";
			switch(page){
				case PSXValue.P_CPU:
					sql = "select datetime,sum(ttime)/rtime from " + PSXValue.INFOTABLE
					+ " where key = '" + params[0].key + "' and rtime > 0 and datetime >= '" + fString + "'"
					+ " group by key,datetime order by datetime";
					break;
				case PSXValue.P_MEM:	
					sql = "select datetime,sum(tsize)/rsize from " + PSXValue.INFOTABLE
					+ " where key = '" + params[0].key + "' and rsize > 0 and datetime >= '" + fString + "'"
					+ " group by key,datetime order by datetime";
					break;
				case PSXValue.P_BATT:
					sql = "select datetime,max(level),max(status),max(plugged) from " + PSXValue.BATTINFO
					+ " where name = '" + params[0].key + "' and datetime >= '" + fString + "'"
					+ " group by datetime order by datetime";
					break;
			}
			if(isDebug){
				Log.w(CNAME,sql);
			}
			
			SQLiteDatabase db = dObject.dbOpen();
			Cursor cursor = dObject.dbQuery(db, sql);
			if(cursor == null || cursor.getCount() == 0){
				dObject.dbClose(db);
				return null;
			}
			
			int totalnum = (length * 60) / interval;
			int datanum = cursor.getCount();
			chartSettings.x = new ArrayList<Date[]>();
			chartSettings.values = new ArrayList<double[]>();
			Date[] x = new Date[totalnum];
			double[] values = new double[totalnum];

			if(isDebug){
				Log.w(CNAME,"datanum = " + datanum + " totalnum = " + totalnum);
			}

			for(int i = 0; i < totalnum;i++){
				x[i] = calendar.getTime();

				fString = CommTools.CalendarToString(calendar, CommTools.DATETIMELONG);
				calendar.add(Calendar.MINUTE, interval);
				String tString = CommTools.CalendarToString(calendar, CommTools.DATETIMELONG);
				values[i] = getValue(fString,tString,cursor,page);
				if(page == PSXValue.P_BATT){
					if(values[i] == MathHelper.NULL_VALUE && i > 1){
						values[i] = values[i - 1];
					}
				}
				if(values[i] != MathHelper.NULL_VALUE){
					if(values[i] * 1.3 > chartSettings.max){
						chartSettings.max = (int)(values[i] * 1.3);
					}
				}
			}
			if(chartSettings.max > 5) {
				chartSettings.max += 5 - (chartSettings.max % 5);
			} else {
				chartSettings.max += 1;
			}
			
			if(page == PSXValue.P_BATT) {
				chartSettings.max = 120;
			}
			chartSettings.x.add(x);
			chartSettings.values.add(values);
			dObject.dbClose(db);
		} catch (Exception ex) {
			TraceLog saveTrace = new TraceLog(mContext);
			String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
			saveTrace.saveLog(ex,CNAME + mname);
			Log.e(CNAME,ex.getMessage());
			ex.printStackTrace();
		}
		result.chartSettings = chartSettings;
		return result;
	}
	
	private double getValue(String fString,String tString,Cursor cursor,int page) {
		double ret = MathHelper.NULL_VALUE;
		cursor.moveToFirst();
		while(cursor.getPosition() < cursor.getCount()){
			String datetime = cursor.getString(0);
			if(datetime.compareTo(fString) >= 0 && datetime.compareTo(tString) < 0){
				if(isDebug){
					Log.w(CNAME,"date=" + datetime + " data=" + cursor.getString(1));
				}
				switch(page){
				case PSXValue.P_CPU:
					ret = cursor.getDouble(1) * 100.0;
					break;
				case PSXValue.P_MEM:
					ret = cursor.getDouble(1) * 100.0;
					break;
				case PSXValue.P_BATT:
					ret = cursor.getDouble(1);
					break;
				}
				break;
			}
			cursor.moveToNext();
		}
		if(isDebug) Log.w(CNAME,"r="+ret);
		return ret;
	}
	
	
}
