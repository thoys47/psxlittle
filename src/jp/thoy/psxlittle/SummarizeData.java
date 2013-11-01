package jp.thoy.psxlittle;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SummarizeData {
	final String TAG = CommTools.getLastPart(this.getClass().getName(),".");
	final static boolean isDebug = false;
	Context mContext;
	
	public SummarizeData(Context context){
		mContext = context;
	}

	public ArrayList<TempTable> calculate(String key,int pos) {
		ArrayList<TempTable> calList = new ArrayList<TempTable>();
		String calTable = "";
		String sumColumn = "";
		String divColumn = "";
 		
		if(key == null){
			if(pos == PSXValue.P_CPU){
				calTable = PSXValue.TEMPCPU;
				sumColumn = "ttime";
				divColumn = "rtime";
			} else {
				calTable = PSXValue.TEMPMEM;
				sumColumn = "tsize";
				divColumn = "rsize";
			}
		} else {
			if(pos == PSXValue.P_CPU){
				calTable = PSXValue.DETAILCPU;
				sumColumn = "ttime";
				divColumn = "rtime";
			} else {
				calTable = PSXValue.DETAILMEM;
				sumColumn = "tsize";
				divColumn = "rsize";
			}
		}
		
		DataObject dObject = new DataObject(mContext);
		SQLiteDatabase db = null;
		try{
			db = dObject.dbOpen();
			String sql = "Select sum(" + sumColumn + ") from " + PSXValue.INFOTABLE;
			if(key != null){
				sql += " where key = '" + key + "'";
			}
			if(isDebug){
				Log.w(TAG,"sql=" + sql);
			}
			Cursor cursor = dObject.dbQuery(db, sql);
			if(cursor.getString(0) == null || cursor == null){
				dObject.dbClose(db);
				return null;
			}
			if(isDebug){
				Log.w(TAG,"cnt="+cursor.getString(0));
			}
			long total = cursor.getLong(0);
			if(key == null){
				sql = "Select key,sum(" + sumColumn + ")"
						+ ",max(" + sumColumn + "/" + divColumn + ")"
						+ ",avg(" + sumColumn + "/" + divColumn + ")" 
						+ " from " + PSXValue.INFOTABLE + " group by key";
			} else {
				sql = "Select name,sum(" + sumColumn + ")"
						+ ",max(" + sumColumn + "/" + divColumn + ")"
						+ ",avg(" + sumColumn + "/" + divColumn + ")" 
						+ " from " + PSXValue.INFOTABLE
						+ " where key = '" + key +"' group by name";
			}
			if(isDebug){
				Log.w(TAG,sql);
			}
			cursor = dObject.dbQuery(db, sql);
			if(cursor == null){
				return null;
			}
			sql = "delete from " + calTable;
			dObject.doSQL(db, sql);
			db.beginTransaction();
			while(cursor.getPosition() < cursor.getCount()){
				sql = "insert into " + calTable + " (ID,KEY,SUM,MAX,AVG) values (";
				sql += "null,'" + cursor.getString(0) + "',";
				double tmp = (double)cursor.getLong(1) * 100.0 /(double)total;
				tmp = (double)((int)((tmp * 100.0) + 0.5) / 100.0);
				sql += String.format("%.2f",tmp) + ",";
				tmp = ((double)cursor.getDouble(2)) * 100.0;
				tmp = (double)((int)((tmp * 100.0) + 0.5) / 100.0);
				sql += String.format("%.2f",tmp) + ",";
				sql += cursor.getString(3) + ")";
				db.execSQL(sql);
				cursor.moveToNext();
			}
			db.setTransactionSuccessful();
			db.endTransaction();
			sql = "Select key,sum,max,avg from " + calTable + " where sum > 0 order by sum desc";
			
			cursor = dObject.dbQuery(db, sql);
			dObject.dbClose(db);
			while(cursor.getPosition() < cursor.getCount()){
				TempTable list = new TempTable();
				list.key = cursor.getString(0);
				
				list.AppName = cursor.getString(0);
				list.SysName = cursor.getString(0);
				
				list.sum = cursor.getDouble(1);
				list.max = cursor.getDouble(2);
				list.avg = cursor.getDouble(3);
				calList.add(list);
				cursor.moveToNext();
			}
			
		} catch (Exception ex){
			if(db != null){
				db.close();
			}
			TraceLog saveTrace = new TraceLog(mContext);
			String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
			saveTrace.saveLog(ex,TAG + mname);
			Log.e(TAG,ex.getMessage());
			ex.printStackTrace();
		}
		return calList;
	}
}
	
