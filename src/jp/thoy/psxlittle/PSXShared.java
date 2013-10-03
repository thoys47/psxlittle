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
		return mContext.getSharedPreferences(PSXService.PREFILENAME,Activity.MODE_PRIVATE);
	}
	
	public int getInterval(){
		SharedPreferences preference = getSharedPrefs();
		return preference.getInt(PSXService.INTERVAL,PSXService.DEFINTER);
	}
	
	public int getLength(){
		SharedPreferences preferences = getSharedPrefs();
		return preferences.getInt(PSXService.LENGTH,PSXService.DEFLEN);
	}
	
	public Long getBefore(){
		SharedPreferences preferences = getSharedPrefs();
		return preferences.getLong(PSXService.BEFORE,PSXService.DEFBEF);
	}
	
	public void putBefore(Calendar calendar){
		SharedPreferences preferences = getSharedPrefs();
		
		Editor editor = preferences.edit();
		editor.putLong(PSXService.BEFORE, calendar.getTimeInMillis());
		editor.commit();
	}

	public void putInterval(int interval){
		SharedPreferences preferences = getSharedPrefs();
		
		Editor editor = preferences.edit();
		editor.putInt(PSXService.INTERVAL, interval);
		editor.commit();
	}

	public void putLength(int length){
		SharedPreferences preferences = getSharedPrefs();
		
		Editor editor = preferences.edit();
		editor.putInt(PSXService.LENGTH, length);
		editor.commit();
	}

}
