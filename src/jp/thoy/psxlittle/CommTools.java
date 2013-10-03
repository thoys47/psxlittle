package jp.thoy.psxlittle;

import java.util.Calendar;

import android.content.Context;

public class CommTools {
	Context mContext;
	
	public final static String DATE = "YYYY/MM/DD";
	public final static String DATETIMELONG = "YYYY/MM/DD HH:MM:SS";
	public final static String DATETIMESHORT = "YYYY/MM/DD HH:MM";
	public final static String TIMELONG = "HH:MM:SS";
	public final static String TIMESHORT = "HH:MM";
	final String CNAME = CommTools.getLastPart(this.getClass().getName(),".");
	final static boolean isDebug = false;
	
	
	public CommTools(Context context){
		mContext = context;
	}

	public static String getLastPart(String str,String delim){
		return str.substring(str.lastIndexOf(delim) + 1, str.length());
	}

	public static String CalendarToString(Calendar calendar,String format){
		String ret = "";
		if(format.equals(DATE)){
			ret = getDate(calendar);
		} else if (format.equals(DATETIMELONG)) {
			ret = getDate(calendar) + " " + getTime(calendar,1);
		} else if (format.equals(DATETIMESHORT)) {
			ret = getDate(calendar) + " " + getTime(calendar,0);
		} else if (format.equals(TIMELONG)){
			ret = getTime(calendar,1);
		} else if (format.equals(TIMESHORT)){
			ret = getTime(calendar,0);
		}
		return ret;
	}
	
	public static int DateStr2Int(String date,int flag){
		//Calendar.YEAR,MONTH,DAY‚¾‚¯‚È‚çString=yyyy/mm/dd
		//Calendar.YEAR,MONTH,DAY,HOUR,MIN,SEC‚¾‚¯‚È‚çString=yyyy/mm/dd hh:mm:ss
		//Calendar.HOUR,MIN,SEC‚¾‚¯‚È‚çString=hh:mm:ss
		int len = date.length();
		
		switch(flag){
		case Calendar.YEAR:
			return Integer.parseInt(date.substring(0, 4));
		case Calendar.MONTH:
			return Integer.parseInt(date.substring(5, 7));
		case Calendar.DAY_OF_MONTH:
			return Integer.parseInt(date.substring(8, 10));
		case Calendar.HOUR_OF_DAY:
			if(len == 19){
				return Integer.parseInt(date.substring(11, 13));
			} else {
				return Integer.parseInt(date.substring(0,2));

			}
		case Calendar.MINUTE:
			if(len == 19){
				return Integer.parseInt(date.substring(14, 16));
			} else {
				return Integer.parseInt(date.substring(3,5));
			}
		case Calendar.SECOND:
			if(len == 19){
				return Integer.parseInt(date.substring(17, 19));
			} else {
				return Integer.parseInt(date.substring(6,8));
			}
		}
		return 0;
	}

	public static String intDate2String(int year,int month,int day,int hour,int min,int sec,String format){
		String ret = "";
		if(format.equals(DATE)){
			ret = String.format("%04d",year)+ "/";
			ret += String.format("%02d",month) + "/";
			ret += String.format("%02d",day);
		} else if(format.equals(DATETIMELONG)) {
			ret = String.format("%04d",year)+ "/";
			ret += String.format("%02d",month) + "/";
			ret += String.format("%02d",day) + " ";
			ret += String.format("%02d", hour) + ":";
			ret += String.format("%02d", min) + ":";
			ret += String.format("%02d", sec);
		} else if(format.equals(DATETIMESHORT)) {
			ret = String.format("%04d",year)+ "/";
			ret += String.format("%02d",month) + "/";
			ret += String.format("%02d",day) + " ";
			ret += String.format("%02d", hour) + ":";
			ret += String.format("%02d", min);
		} else if(format.equals(TIMELONG)){
			ret = String.format("%02d",hour) + ":";
			ret += String.format("%02d", min) + ":";
			ret += String.format("%02d", sec);
		} else if(format.equals(TIMESHORT)){
			ret = String.format("%02d",hour) + ":";
			ret += String.format("%02d", min);
		}
		return ret;
	}

	public static String strTimeShort(int hour,int min,int sec){
		String ret;
		ret = String.format("%02d",hour) + ":" + String.format("%02d",min);
		if(sec != -1){
			ret += ":" + String.format("%02d",sec);
		}
		return ret;

	}

	public static long strToMillis(String datetime19){
		Calendar ret = Calendar.getInstance();
		ret.set(DateStr2Int(datetime19,Calendar.YEAR),
				DateStr2Int(datetime19,Calendar.MONTH) - 1,
				DateStr2Int(datetime19,Calendar.DAY_OF_MONTH),
				DateStr2Int(datetime19,Calendar.HOUR_OF_DAY),
				DateStr2Int(datetime19,Calendar.MINUTE),
				DateStr2Int(datetime19,Calendar.SECOND));
		return ret.getTimeInMillis();
	}

	public static String millisToStr(long millis){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return CalendarToString(calendar,DATETIMELONG);
	}
	

	public static String replaceSymbol(String date){
		String ret;
		
		ret = date.replace("/", "");
		ret = ret.replace(":", "");
		ret = ret.replace(" ", "");
		return ret;
	}
	

	private static String getDate(Calendar calendar){
		String ret = "";
		ret = String.format("%04d", calendar.get(Calendar.YEAR)) + "/";
		ret += String.format("%02d", calendar.get(Calendar.MONTH) + 1) + "/";
		ret += String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
		return ret;
	}
	
	private static String getTime(Calendar calendar,int flag){
		String ret = "";
		ret = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":";
		ret += String.format("%02d", calendar.get(Calendar.MINUTE));
		if(flag != 0){
			ret += String.format(":%02d", calendar.get(Calendar.SECOND));
		}
		return ret;
	}

}
