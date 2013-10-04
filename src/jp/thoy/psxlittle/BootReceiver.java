package jp.thoy.psxlittle;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.BatteryManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	public final static String NAME_BATT = "Battery";
	static String CNAME;
	final static boolean isDebug = false;

	
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
			param.sParam = PSXService.BOOT;
			param.clParam = CNAME;
			aTask.execute(param);
			PSXShared pShared = new PSXShared(context);
			pShared.putBefore(Calendar.getInstance());
		}
		if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
			storeBatteryInfo(context,intent);
		}
		
		RegistTask rTask = new RegistTask(context);
		rTask.StartCommand();

		TraceLog saveLog = new TraceLog(context);
		saveLog.saveDebug(CommTools.getLastPart(intent.getAction() ,"."));
		if(isDebug) Log.w(CNAME,"action=" + intent.getAction());
	}
	
	private void storeBatteryInfo(Context context,Intent intent){
		
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		String datetime = CommTools.CalendarToString(calendar, CommTools.DATETIMELONG);
		GetBatteryInfo batteryInfo = new GetBatteryInfo();
		BatteryInfo bInfo = batteryInfo.getInfo(intent);
		DataObject dObject = new DataObject(context);
		SQLiteDatabase mdb = dObject.dbOpen();
		String sql = DataObject.makeBaseSQL("INSERT", DataObject.BATTINFO);
		sql += "null,";
		sql += String.valueOf(bInfo.rLevel) + ",";
		sql += "'" + bInfo.status + "',";
		sql += "'" + bInfo.plugged + "',";
		sql += String.valueOf(bInfo.temp) + ',';
		sql += "'" + NAME_BATT + "',";
		sql += "'" + datetime + "')";
		dObject.doSQL(mdb, sql);
		dObject.dbClose(mdb);
	}
	
}
