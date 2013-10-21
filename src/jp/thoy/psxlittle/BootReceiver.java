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

		String action = intent.getAction(); 
		
		if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
			PSXAsyncTask aTask = new PSXAsyncTask();
			Param  param = new Param();
			param.cParam = context;
			param.sParam = PSXValue.BOOT;
			aTask.execute(param);
			PSXShared pShared = new PSXShared(context);
			pShared.putLastExec(Calendar.getInstance());
		} else {
			RegistTask rTask = new RegistTask(context);
			rTask.StartCommand();
			TraceLog saveLog = new TraceLog(context);
			saveLog.saveDebug(CommTools.getLastPart(intent.getAction() ,"."));
		}
		
		Log.w(CNAME,"action=" + intent.getAction());
		
	}
	
	
}
