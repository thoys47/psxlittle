package jp.thoy.psxlittle;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SummarizeData {
	final String CNAME = CommTools.getLastPart(this.getClass().getName(),".");
	final static boolean isDebug = false;
	Context mContext;
	
	public SummarizeData(Context context){
		mContext = context;
	}

	public ArrayList<TempTable> calculate(String key,int pos) {
		ArrayList<TempTable> calList = new ArrayList<TempTable>();
		String calTable = "";
		String sumColumn = "";
		
		if(key == null){
			if(pos == 0){
				calTable = PSXValue.TEMPCPU;
				sumColumn = "ttime";
			} else {
				calTable = PSXValue.TEMPMEM;
				sumColumn = "tsize";
			}
		} else {
			if(pos == 0){
				calTable = PSXValue.DETAILCPU;
				sumColumn = "ttime";
			} else {
				calTable = PSXValue.DETAILMEM;
				sumColumn = "tsize";
			}
		}
		
		
		DataObject mData = new DataObject(mContext);
		SQLiteDatabase mdb = mData.dbOpen();
		String sql = "Select count(id) from " + PSXValue.INFOTABLE;
		if(key != null){
			sql += " where key = '" + key + "'";
		}
		Cursor cursor = mData.dbQuery(mdb, sql);
		if(cursor.getString(0).equals("0") || cursor == null){
			mData.dbClose(mdb);
			return null;
		}
		if(isDebug){
			Log.w(CNAME,"cnt="+cursor.getString(0));
		}
		try{
			sql = "Select Sum(" + sumColumn +") from " + PSXValue.INFOTABLE;
			if(key != null){
				sql += " where key = '" + key + "'";
			}
			cursor = mData.dbQuery(mdb, sql);
			if(cursor == null){
				return null;
			}
			Long total = Long.parseLong(cursor.getString(0)); 
			//é¿ç€ÇÃåvéZ
			if(key == null){
				sql = "Select key,sum(" + sumColumn + "),max(" + sumColumn + "),avg(" + sumColumn + ") from " + PSXValue.INFOTABLE + " group by key";
			} else {
				sql = "Select name,sum(" + sumColumn + "),max(" + sumColumn + "),avg(" + sumColumn + ") from " + PSXValue.INFOTABLE + " where key = '" + key +"' group by name";
			}
			cursor = mData.dbQuery(mdb, sql);
			mdb.execSQL("delete from " + calTable);
			mdb.beginTransaction();
			for(int i = 0;i < cursor.getCount();i++){
				double stmp = (double)(Long.parseLong(cursor.getString(1)) * 100) / (double)total;
				stmp = (double)((int)((stmp * 100) + 0.5)) / 100;
				double atmp = (double)(Double.parseDouble(cursor.getString(3)));
				atmp = (double)((int)((atmp * 100) + 0.5)) / 100;
	
				sql = "insert into " + calTable + " (ID,KEY,SUM,MAX,AVG) values (";
				sql += "null,'" + cursor.getString(0) + "',";
				sql += String.format("%.2f", stmp) + ",";
				sql += cursor.getString(2) + ",";
				sql += String.format("%.2f", atmp) + ")";
				mdb.execSQL(sql);
				cursor.moveToNext();
			}
			mdb.setTransactionSuccessful();
			mdb.endTransaction();
			sql = "Select key,sum,max,avg from " + calTable + " order by sum desc";
			cursor = mData.dbQuery(mdb, sql);
			for(int i = 0;i < cursor.getCount();i++){
				TempTable list = new TempTable();
				list.key = cursor.getString(0);
				
				list.AppName = cursor.getString(0);
				list.SysName = cursor.getString(0);
				
				list.sum = cursor.getString(1);
				list.max = cursor.getString(2);
				list.avg = cursor.getString(3);
				calList.add(list);
				cursor.moveToNext();
			}
			
			mData.dbClose(mdb);
		} catch (Exception ex){
			if(mdb != null){
				mdb.endTransaction();
				mdb.close();
			}
			TraceLog saveTrace = new TraceLog(mContext);
			String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
			saveTrace.saveLog(ex,CNAME + mname);
			Log.e(CNAME,ex.getMessage());
			ex.printStackTrace();
		}
		return calList;
	}
}
	
