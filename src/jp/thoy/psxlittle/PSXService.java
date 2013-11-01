package jp.thoy.psxlittle;

import java.util.Calendar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PSXService extends Service {
	static String TAG;
	final static boolean isDebug = false;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO 自動生成されたメソッド・スタブ
		Context context = getApplicationContext();
		Thread.setDefaultUncaughtExceptionHandler(new TraceLog(context));

		TAG = CommTools.getLastPart(context.getClass().getName(),".");

		PSXAsyncTask aTask = new PSXAsyncTask();
		Param  param = new Param();
		
		if(isDebug) {
			Log.w(TAG,"c=" + context.toString());
			Log.w(TAG,"s=" + intent.getAction());
		}
		
		param.cParam = context;
		if(intent == null){
			param.sParam = PSXValue.REPEAT;
		} else{
			param.sParam = intent.getAction();
		}
		aTask.execute(param);

		PSXShared pShared = new PSXShared(context);
		pShared.putLastExec(Calendar.getInstance());
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
