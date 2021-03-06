package jp.thoy.psxlittle;

import java.util.Calendar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RegistTask {
	
	final String TAG = CommTools.getLastPart(this.getClass().getName(),".");
	final static boolean isDebug = PSXValue.isDebug;
	
	Context mContext;

	public RegistTask(Context context){
		mContext = context;
	}
	
	public void StartCommand() {
		// TODO 自動生成されたメソッド・スタブ
		Thread.setDefaultUncaughtExceptionHandler(new TraceLog(mContext));
		Intent rIntent = new Intent(mContext,PSXService.class);
		rIntent.setAction(PSXValue.REPEAT);
		
		AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Activity.ALARM_SERVICE);
		PendingIntent pIntent = PendingIntent.getService(mContext, 0, rIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		PSXShared pShared = new PSXShared(mContext);
		int interval = pShared.getInterval();
		
		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.MINUTE, interval - (calendar.get(Calendar.MINUTE) % interval));
		calendar.add(Calendar.SECOND, (-1) * calendar.get(Calendar.SECOND));
		
		if(isDebug){
			Log.w(TAG,CommTools.CalendarToString(calendar, CommTools.TIMELONG));
			//TraceLog saveLog = new TraceLog(mContext);
			//saveLog.saveDebug("next:" + CommTools.CalendarToString(calendar, CommTools.TIMELONG));
		}
		
		//mAlarmManager.cancel(pIntent);
		alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), interval * 1000 * 60, pIntent);
		//mAlarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pIntent);
		//Log.e(TAG,"next:" + CommTools.CalendarToString(calendar, CommTools.DATETIMELONG));
		Intent dIntent = new Intent(mContext,DataDeleteService.class);
		PendingIntent pdIntent = PendingIntent.getService(mContext, 0, dIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, pdIntent);
	}


}

