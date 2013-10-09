package jp.thoy.psxlittle;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	static String CNAME;
	final static boolean isDebug = PSXValue.isDebug;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO 自動生成されたメソッド・スタブ
		Thread.setDefaultUncaughtExceptionHandler(new TraceLog(context));
		CNAME = CommTools.getLastPart(context.getClass().getName(),".");

		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			PSXAsyncTask aTask = new PSXAsyncTask();
			Param  param = new Param();
			ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Activity.ACTIVITY_SERVICE);
			param.cParam = context;
			param.aParam = mActivityManager;
			param.sParam = PSXValue.BOOT;
			param.clParam = CNAME;
			aTask.execute(param);
			PSXShared pShared = new PSXShared(context);
			pShared.putBefore(Calendar.getInstance());
		}
		if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
			storeBatteryInfo(context,intent);
		} else {
			RegistTask rTask = new RegistTask(context);
			rTask.StartCommand();
			TraceLog saveLog = new TraceLog(context);
			saveLog.saveDebug(CommTools.getLastPart(intent.getAction() ,"."));
		}
		
		if(isDebug) Log.w(CNAME,"action=" + intent.getAction());
	}
	
	private void storeBatteryInfo(Context context,Intent intent){
		
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.add(Calendar.MINUTE, (-1) * (calendar.get(Calendar.MINUTE) % 5));
		calendar.add(Calendar.SECOND, (-1) * (calendar.get(Calendar.SECOND)));
		String datetime = CommTools.CalendarToString(calendar, CommTools.DATETIMELONG);
		
		GetBatteryInfo batteryInfo = new GetBatteryInfo();
		BatteryInfo bInfo = batteryInfo.getInfo(intent);
		DataObject dObject = new DataObject(context);
		SQLiteDatabase mdb = dObject.dbOpen();
		String sql = DataObject.makeBaseSQL("INSERT", PSXValue.BATTINFO);
		sql += "null,";
		sql += String.valueOf(bInfo.rLevel) + ",";
		sql += "'" + bInfo.status + "',";
		sql += "'" + bInfo.plugged + "',";
		sql += String.valueOf((bInfo.temp + 5)/ 10) + ',';
		sql += "'" + PSXValue.NAME_BATT + "',";
		sql += "'" + datetime + "')";
		dObject.doSQL(mdb, sql);
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
