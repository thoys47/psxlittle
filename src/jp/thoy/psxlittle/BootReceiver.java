package jp.thoy.psxlittle;

import java.util.Calendar;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
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
		
		RegistTask rTask = new RegistTask(context);
		rTask.StartCommand();

		TraceLog saveLog = new TraceLog(context);
		saveLog.saveDebug(CommTools.getLastPart(intent.getAction() ,"."));
		if(isDebug) Log.w(CNAME,"action=" + intent.getAction());
	}

}
