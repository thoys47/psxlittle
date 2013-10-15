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
	final static boolean isDebug = PSXValue.isDebug;
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO 自動生成されたメソッド・スタブ
		Context context = getApplicationContext();
		Thread.setDefaultUncaughtExceptionHandler(new TraceLog(context));
		CNAME = CommTools.getLastPart(context.getClass().getName(),".");

		//if((calendar.get(Calendar.MINUTE) % interval) == 0  || checkBefore(context,interval)) {
		PSXAsyncTask aTask = new PSXAsyncTask();
		Param  mParam = new Param();
		ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Activity.ACTIVITY_SERVICE);
		mParam.cParam = context;
		mParam.aParam = mActivityManager;
		mParam.sParam = intent.getAction();
		mParam.clParam = CNAME;
		aTask.execute(mParam);

		PSXShared pShared = new PSXShared(context);
		pShared.putLastExec(Calendar.getInstance());
		
		storeBatteryInfo(context);

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
		
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.add(Calendar.MINUTE, (-1) * (calendar.get(Calendar.MINUTE) % 5));
		calendar.add(Calendar.SECOND, (-1) * (calendar.get(Calendar.SECOND)));
		String datetime = CommTools.CalendarToString(calendar, CommTools.DATETIMELONG);
		
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent intent = context.registerReceiver(null, ifilter);
		GetBatteryInfo batteryInfo = new GetBatteryInfo();
		BatteryInfo bInfo = batteryInfo.getInfo(intent);

		DataObject dObject = new DataObject(context);
		SQLiteDatabase mdb = dObject.dbOpen();
		String sql = DataObject.makeBaseSQL("INSERT", PSXValue.BATTINFO);
		sql += "null,";
		sql += String.valueOf(bInfo.rLevel) + ",";
		sql += "'" + bInfo.status + "',";
		sql += "'" + bInfo.plugged + "',";
		sql += String.valueOf(bInfo.temp) + ',';
		sql += "'" + PSXValue.NAME_BATT + "',";
		sql += "'" + datetime + "')";
		dObject.doSQL(mdb, sql);
		if(isDebug){
			Log.w(CNAME,sql);
		}
		PSXShared pShared = new PSXShared(context);
		int length = pShared.getLength();
		calendar.add(Calendar.HOUR_OF_DAY, (-1) * length);
		sql = "delete from " + PSXValue.BATTINFO
				+ " where datetime < '" + CommTools.CalendarToString(calendar,CommTools.DATETIMELONG) + "'";
		dObject.doSQL(mdb,sql);
		if(isDebug){
			Log.w(CNAME,sql);
		}
		dObject.dbClose(mdb);
	}


}
