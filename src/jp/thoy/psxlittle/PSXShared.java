package jp.thoy.psxlittle;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PSXShared {
	
	public Context mContext;
	
	
	public PSXShared(Context context){
		mContext = context;
	}
	
	public SharedPreferences getSharedPrefs(){
		return mContext.getSharedPreferences(PSXValue.PREFILENAME,Activity.MODE_PRIVATE);
	}
	
	public int getInterval(){
		SharedPreferences preference = getSharedPrefs();
		return preference.getInt(PSXValue.INTERVAL,PSXValue.DEFINTER);
	}
	
	public int getLength(){
		SharedPreferences preferences = getSharedPrefs();
		return preferences.getInt(PSXValue.LENGTH,PSXValue.DEFLEN);
	}
	
	public Long getBefore(){
		SharedPreferences preferences = getSharedPrefs();
		return preferences.getLong(PSXValue.BEFORE,PSXValue.DEFBEF);
	}
	public Long getPrevTime(){
		SharedPreferences preferences = getSharedPrefs();
		return preferences.getLong(PSXValue.PREVTIME,PSXValue.DEFPREV);
	}
	
	public void putBefore(Calendar calendar){
		SharedPreferences preferences = getSharedPrefs();
		
		Editor editor = preferences.edit();
		editor.putLong(PSXValue.BEFORE, calendar.getTimeInMillis());
		editor.commit();
	}

	public void putInterval(int interval){
		SharedPreferences preferences = getSharedPrefs();
		
		Editor editor = preferences.edit();
		editor.putInt(PSXValue.INTERVAL, interval);
		editor.commit();
	}

	public void putLength(int length){
		SharedPreferences preferences = getSharedPrefs();
		
		Editor editor = preferences.edit();
		editor.putInt(PSXValue.LENGTH, length);
		editor.commit();
	}

	public void putPrevTime(Long time){
		SharedPreferences preferences = getSharedPrefs();
		
		Editor editor = preferences.edit();
		editor.putLong(PSXValue.PREVTIME, time);
		editor.commit();
	}
}
