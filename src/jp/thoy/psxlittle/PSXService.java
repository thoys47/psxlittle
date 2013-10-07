package jp.thoy.psxlittle;

import java.util.Calendar;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.util.Log;

public class PSXService extends Service {
	static String CNAME;
	final static boolean isDebug = false;
	

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
		pShared.putBefore(Calendar.getInstance());

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

}
