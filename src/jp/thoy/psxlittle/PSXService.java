package jp.thoy.psxlittle;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class PSXService extends Service {
	static String CNAME;
	final static boolean isDebug = true;
	

	@Override
	public void onLowMemory() {
		// TODO 自動生成されたメソッド・スタブ
		super.onLowMemory();
		Context context = getApplicationContext();
		TraceLog saveTrace = new TraceLog(context);
		saveTrace.saveDebug("onLowMemory");
		Log.e(CNAME,"onLowMemory");
	}


	@Override
	public void onTrimMemory(int level) {
		// TODO 自動生成されたメソッド・スタブ
		super.onTrimMemory(level);
		Context context = getApplicationContext();
		TraceLog saveTrace = new TraceLog(context);
		saveTrace.saveDebug("onTrimMemory");
		Log.e(CNAME,"onTrimMemory");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO 自動生成されたメソッド・スタブ
		Context context = getApplicationContext();
		Thread.setDefaultUncaughtExceptionHandler(new TraceLog(context));
		CNAME = CommTools.getLastPart(context.getClass().getName(),".");
		if(isDebug) Log.w(CNAME,"service 1");

		PSXAsyncTask aTask = new PSXAsyncTask();
		Param  param = new Param();
		
		if(isDebug) {
			Log.w(CNAME,"c=" + context.toString());
			Log.w(CNAME,"s=" + intent.getAction());
		}
		
		param.cParam = context;
		param.sParam = intent.getAction();
		aTask.execute(param);

		if(isDebug) Log.w(CNAME,"service 2");

		PSXShared pShared = new PSXShared(context);
		pShared.putLastExec(Calendar.getInstance());
		
		if(isDebug) Log.w(CNAME,"service 3");
		storeBatteryInfo(context);
		if(isDebug) Log.w(CNAME,"service 4");

		/*
		RegistTask rTask = new RegistTask(context);
		rTask.StartCommand();
		*/
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
	private void storeBatteryInfo(Context context){
		
		int interval;
		PSXShared pShared = new PSXShared(context);
		interval = pShared.getInterval();
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.add(Calendar.MINUTE, (-1) * calendar.get(Calendar.MINUTE) % interval);
		calendar.add(Calendar.SECOND, (-1) * calendar.get(Calendar.SECOND));
		String datetime = CommTools.CalendarToString(calendar, CommTools.DATETIMELONG);
		
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent intent = context.registerReceiver(null, ifilter);
		GetBatteryInfo batteryInfo = new GetBatteryInfo();
		BatteryInfo bInfo = batteryInfo.getInfo(intent);

		DataObject dObject = new DataObject(context);
		SQLiteDatabase db = dObject.dbOpen();
		String sql = DataObject.makeBaseSQL("INSERT", PSXValue.BATTINFO);
		sql += "(null,";
		sql += String.valueOf(bInfo.rLevel) + ",";
		sql += "'" + bInfo.status + "',";
		sql += "'" + bInfo.plugged + "',";
		sql += String.valueOf(bInfo.temp) + ',';
		sql += "'" + PSXValue.NAME_BATT + "',";
		sql += "'" + datetime + "')";
		dObject.doSQL(db, sql);
		if(isDebug){
			Log.w(CNAME,sql);
		}
		dObject.dbClose(db);
	}


}
