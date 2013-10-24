package jp.thoy.psxlittle;

import java.util.Calendar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PSXService extends Service {
	static String CNAME;
	final static boolean isDebug = false;
	

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
		if(intent == null){
			param.sParam = PSXValue.REPEAT;
		} else{
			param.sParam = intent.getAction();
		}
		aTask.execute(param);

		if(isDebug) Log.w(CNAME,"service 2");

		PSXShared pShared = new PSXShared(context);
		pShared.putLastExec(Calendar.getInstance());
		
		if(isDebug) Log.w(CNAME,"service 3");

		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}


}
