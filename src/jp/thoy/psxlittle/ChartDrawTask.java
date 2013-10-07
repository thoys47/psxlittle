package jp.thoy.psxlittle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import jp.thoy.psxlittle.R;

import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
		LinearLayout lLayout = (LinearLayout)mActivity.findViewById(R.id.chart_area);
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
		String[] titles = new String[]{"CPU","Memory"};
		int[] colors = new int[]{Color.rgb(255, 127, 0),Color.rgb(0, 127, 255)};
		PointStyle[] styles = new PointStyle[]{PointStyle.CIRCLE,PointStyle.DIAMOND};
		
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
			calendar.add(Calendar.SECOND, (-1) * calendar.get(Calendar.SECOND)); 
			String fString = CommTools.CalendarToString(calendar, CommTools.DATETIMELONG);

			String sql = "select datetime,ttime/rtime,tsize/rsize from " + PSXValue.INFOTABLE
					+ " where key = '" + params[0].key + "' and datetime >= '" + fString + "'"
					+ " order by datetime";
			if(isDebug){
				Log.w(CNAME,sql);
			}
			
			SQLiteDatabase mdb = dObject.dbOpen();
			Cursor cursor = dObject.dbQuery(mdb, sql);
			mdb.close();
			if(cursor == null || cursor.getCount() == 0){
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
			int lost = 0;

			for(int i = 0; i < totalnum;i++){
				x[i] = calendar.getTime();

				fString = CommTools.CalendarToString(calendar, CommTools.DATETIMELONG);
				calendar.add(Calendar.MINUTE, interval);
				String tString = CommTools.CalendarToString(calendar, CommTools.DATETIMELONG);
				if(isDebug){
					Log.w(CNAME,"data=" + cursor.getString(0) + " fString=" + fString + " tString=" + tString);
				}
				
				if(cursor.getString(0).compareTo(fString) >= 0 && cursor.getString(0).compareTo(tString) <= 0){
					if(isDebug){
						Log.w(CNAME,"data=" + cursor.getString(1));
					}
					switch(page){
					case 0:
						values[i] = Double.parseDouble(cursor.getString(1)) * 100.0;
						break;
					case 1:
						values[i] = Double.parseDouble(cursor.getString(2)) * 100.0;
						break;
					}
					if(chartSettings.max < (int)((values[i] * 1.3) + 0.5)){
						chartSettings.max = (int)((values[i] * 1.3) + 0.5);
					}
					if(cursor.getPosition() < cursor.getCount() - 1){
						cursor.moveToNext();
					}
					lost = i;
				} else {
					if(lost == i - 1 && cursor.getPosition() < cursor.getCount() - 1){
						cursor.moveToNext();
					}
					values[i] = 0.0;
				}
			}
			if(chartSettings.max < 1){
				chartSettings.max = 1;
			}
			chartSettings.x.add(x);
			chartSettings.values.add(values);
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
	
}
