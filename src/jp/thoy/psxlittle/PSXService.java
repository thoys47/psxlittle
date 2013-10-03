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
	public final static String BOOT = "PSXLittle.Boot";
	public final static String INSTALL = "PSXLittle.Install";
	public final static String REPEAT = "PSXLittle.Repeat";
	public final static String INTERVAL = "Interval";
	public final static String BEFORE = "Before";
	public final static String LENGTH = "Length";
	public final static int DEFINTER = 10;
	public final static int DEFLEN = 24;
	public final static long DEFBEF = 0L;
	public final static String LASTEXEC = "LastExec";
	public final static String PREFILENAME = "psxprefer";

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
	
	private boolean checkBefore(Context context,int interval){
		SharedPreferences mPreference = context.getSharedPreferences(PSXService.PREFILENAME,Activity.MODE_PRIVATE);
		Calendar calendar = Calendar.getInstance();
		Long now = calendar.getTimeInMillis();
		Long before = mPreference.getLong(PSXService.BEFORE,now);
		Long linter = Integer.valueOf(interval) * 1000L * 60L;
		if(isDebug) Log.w(CNAME,"now=" + now + " before=" + before + " sum=" + (now - before) + " inter=" + linter);
		if(now - before >= linter){
			return true;
		} else {
			return false;
		}
	}


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
