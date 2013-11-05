package jp.thoy.psxlittle;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DataObject {

	Context mContext;
	final String TAG = CommTools.getLastPart(this.getClass().getName(),".");
	final static boolean isDebug = false;
	
	
	final Calendar calendar = Calendar.getInstance();

	public DataObject(Context context){
		mContext = context;
		Thread.setDefaultUncaughtExceptionHandler(new TraceLog(context));
	}
	

	public static String makeBaseSQL(String option,String tablename) {
		String sql = null;
		if(option.equals("CREATE")){
			if(tablename.equals(PSXValue.INFOTABLE)){
				sql = "CREATE TABLE IF NOT EXISTS " + tablename + " (";
				for(int i = 0;i < PSXValue.infoColumn.length;i++){
					sql += PSXValue.infoColumn[i] + " " + PSXValue.infoType[i] + ",";
				}
			} else if(tablename.equals(PSXValue.TEMPINFO)){
				sql = "CREATE TABLE IF NOT EXISTS " + tablename + " (";
				for(int i = 0;i < PSXValue.infoColumn.length;i++){
					sql += PSXValue.infoColumn[i] + " " + PSXValue.infoType[i] + ",";
				}
			} else if(tablename.equals(PSXValue.PREVINFO)){
				sql = "CREATE TABLE IF NOT EXISTS " + tablename + " (";
				for(int i = 0;i < PSXValue.prevColumn.length;i++){
					sql += PSXValue.prevColumn[i] + " " + PSXValue.prevType[i] + ",";
				}
			} else if(tablename.equals(PSXValue.BATTINFO)){
				sql = "CREATE TABLE IF NOT EXISTS " + tablename + " (";
				for(int i = 0;i < PSXValue.battColumn.length;i++){
					sql += PSXValue.battColumn[i] + " " + PSXValue.battType[i] + ",";
				}
			} else if(tablename.equals(PSXValue.TEMPCPU) || tablename.equals(PSXValue.TEMPMEM)){
				sql = "CREATE TABLE IF NOT EXISTS " + tablename + " (";
				for(int i = 0;i < PSXValue.tempColumn.length;i++){
					sql += PSXValue.tempColumn[i] + " " + PSXValue.tempType[i] + ",";
				}
			} else if(tablename.equals(PSXValue.DETAILCPU) || tablename.equals(PSXValue.DETAILMEM)){
				sql = "CREATE TABLE IF NOT EXISTS " + tablename + " (";
				for(int i = 0;i < PSXValue.detailColumn.length;i++){
					sql += PSXValue.detailColumn[i] + " " + PSXValue.detailType[i] + ",";
				}
			}
			sql = sql.substring(0, sql.length() - 1);
			sql += ")";
		} else if (option.equals("INSERT")){
			if(tablename.equals(PSXValue.INFOTABLE)){
				sql = "INSERT INTO " + tablename + " (";
				for(int i = 0;i < PSXValue.infoColumn.length;i++){
					sql += PSXValue.infoColumn[i] + ","; 
				}
			} else if(tablename.equals(PSXValue.TEMPINFO)){
				sql = "INSERT INTO " + tablename + " (";
				for(int i = 0;i < PSXValue.infoColumn.length;i++){
					sql += PSXValue.infoColumn[i] + ","; 
				}
			} else if(tablename.equals(PSXValue.PREVINFO)){
				sql = "INSERT INTO " + tablename + " (";
				for(int i = 0;i < PSXValue.prevColumn.length;i++){
					sql += PSXValue.prevColumn[i] + ","; 
				}
			} else if(tablename.equals(PSXValue.BATTINFO)){
				sql = "INSERT INTO " + tablename + " (";
				for(int i = 0;i < PSXValue.battColumn.length;i++){
					sql += PSXValue.battColumn[i] + ","; 
				}
			} else if(tablename.equals(PSXValue.TEMPCPU) || tablename.equals(PSXValue.TEMPMEM)){
				sql = "INSERT INTO " + tablename + " (";
				for(int i = 0;i < PSXValue.tempColumn.length;i++){
					sql += PSXValue.tempColumn[i] + ","; 
				}
			} else if(tablename.equals(PSXValue.DETAILCPU) || tablename.equals(PSXValue.DETAILMEM)){
				sql = "INSERT INTO " + tablename + " (";
				for(int i = 0;i < PSXValue.detailColumn.length;i++){
					sql += PSXValue.detailColumn[i] + ","; 
				}
			}
			sql = sql.substring(0, sql.length() - 1);
			sql += ") values ";
		} else if(option.equals("SELECT")){
			if(tablename.equals(PSXValue.INFOTABLE)){
				sql = "SELECT ";
				for(int i = 0;i < PSXValue.infoColumn.length;i++){
					sql += PSXValue.infoColumn[i] + ","; 
				}
				sql = sql.substring(0, sql.length() - 1);
				sql += " from " + tablename;
			} else if(tablename.equals(PSXValue.PREVINFO)){
				sql = "SELECT ";
				for(int i = 0;i < PSXValue.prevColumn.length;i++){
					sql += PSXValue.prevColumn[i] + ","; 
				}
				sql = sql.substring(0, sql.length() - 1);
				sql += " from " + tablename;
			} else if(tablename.equals(PSXValue.BATTINFO)){
				sql = "SELECT ";
				for(int i = 0;i < PSXValue.battColumn.length;i++){
					sql += PSXValue.battColumn[i] + ","; 
				}
				sql = sql.substring(0, sql.length() - 1);
				sql += " from " + tablename;
			}
		}
		return sql;
	}	
	
	public void doSQL(SQLiteDatabase mdb,String s) {
		String sql = s;
		if(sql == null || sql.equals("")){
			return;
		} else {
			try{
				mdb.execSQL(sql);
			} catch (Exception ex){
				TraceLog saveTrace = new TraceLog(mContext);
				String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
				saveTrace.saveLog(ex,TAG + mname);
				Log.e(TAG,ex.getMessage());
				ex.printStackTrace();
			}
		}
		if(isDebug) Log.w(TAG,"sql=" + sql);
		return;
	}
	
	public SQLiteDatabase dbOpen(){
		try{
			SQLiteDatabase mdb = null;
			DBOpenHelper mHelper = new DBOpenHelper(mContext,PSXValue.DatabaseName,null,PSXValue.DatabaseVersion);
			mdb = mHelper.getWritableDatabase();
			return mdb;
		} catch (Exception ex) {
			TraceLog saveTrace = new TraceLog(mContext);
			String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
			saveTrace.saveLog(ex,TAG + mname);
			Log.e(TAG,ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}
	
	public Cursor dbQuery(SQLiteDatabase db, String s){
		Cursor cursor = null;
		if(db != null) {
			cursor = db.rawQuery(s, null);
			cursor.moveToFirst();
		}
		return cursor;
	}
	
	public void dbClose(SQLiteDatabase db){
		try{
			db.close();
		} catch (Exception ex) {
			TraceLog saveTrace = new TraceLog(mContext);
			String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
			saveTrace.saveLog(ex,TAG + mname);
			Log.e(TAG,ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public void insertInfo(SQLiteDatabase db,ArrayList<InfoTable> list,String tablename){

		String sql = "";
		boolean dFlag = false;
		
		if(list.size() == 0) {
			return;
		}
		
		try{
			
			if(tablename.equals(PSXValue.INFOTABLE)){
				sql = "select count(id) from " + tablename 
						+ " where datetime = '" + list.get(0).datetime + "'";
				Cursor cursor = this.dbQuery(db, sql);
				if(cursor.getInt(0) != 0){
					dFlag = true;
					TraceLog saveLog = new TraceLog(mContext);
					saveLog.saveDebug("double:" + list.get(0).datetime);
				}
			}
			
			if(dFlag){
				String update = "update " + PSXValue.INFOTABLE + " set "
						+ "TTIME=?,RTIME=?,TSIZE=?,RSIZE=? where id=?";
				String select = DataObject.makeBaseSQL("SELECT", tablename);
				SQLiteStatement stmt = db.compileStatement(update);
				for(int i = 0;i < list.size();i++){
					Cursor cursor = this.dbQuery(db, select + " where name = '" + list.get(i).name 
							+ "' and datetime = '" + list.get(i).datetime + "'");
					
					// {"ID","NAME","KEY","TTIME","RTIME","TSIZE","RSIZE","DATETIME"};
					if(cursor.getCount() == 0){
						String insert = makeBaseSQL("INSERT",tablename);
						insert += "(null,";
						insert += "'" + list.get(i).name + "',";
						insert += "'" + list.get(i).key + "',";
						insert += list.get(i).ttime + ",";
						insert += list.get(i).rtime + ",";
						insert += list.get(i).tsize + ",";
						insert += list.get(i).rsize + ",";
						insert += "'" + list.get(i).datetime + "')";
						this.doSQL(db, insert);
					} else {
						stmt.bindLong(1, list.get(i).ttime + cursor.getLong(3));
						stmt.bindDouble(2, list.get(i).rtime + cursor.getDouble(4));
						stmt.bindLong(3, (list.get(i).tsize + cursor.getLong(5)) / 2);
						stmt.bindDouble(4, (list.get(i).rsize + cursor.getDouble(6)) / 2);
						stmt.bindLong(5, cursor.getLong(0));
						stmt.execute();
						if(isDebug) {
							Log.w(TAG,"new ttime=" + list.get(i).ttime + " old=" +  cursor.getLong(3) + " ans=" + list.get(i).ttime + cursor.getLong(3));
							Log.w(TAG,"new rtime=" + list.get(i).rtime + " old=" +  cursor.getLong(4) + " ans=" + list.get(i).rtime + cursor.getLong(4));
							Log.w(TAG,"new tsize=" + list.get(i).tsize + " old=" +  cursor.getLong(5) + " ans=" + (list.get(i).tsize + cursor.getLong(5)) / 2);
							Log.w(TAG,"new rsize=" + list.get(i).rsize + " old=" +  cursor.getLong(6) + " ans=" + (list.get(i).rsize + cursor.getLong(6)) / 2);
						}
					}
					
				}		
			
			} else {
				sql = makeBaseSQL("INSERT",tablename);
				if(tablename.equals(PSXValue.INFOTABLE) || tablename.equals(PSXValue.TEMPINFO)){
					sql += "(?,?,?,?,?,?,?,?)";
				} else if(tablename.equals(PSXValue.PREVINFO)){
					sql += "(?,?,?)";
				}
				
				SQLiteStatement stmt = db.compileStatement(sql);
				for(int i = 0;i < list.size();i++){
					if(tablename.equals(PSXValue.INFOTABLE) || tablename.equals(PSXValue.TEMPINFO)){
						stmt.bindNull(1);
						stmt.bindString(2, list.get(i).name);
						stmt.bindString(3, list.get(i).key);
						stmt.bindLong(4, list.get(i).ttime);
						if(list.get(i).rtime != null){
							stmt.bindDouble(5, list.get(i).rtime);
						} else {
							stmt.bindNull(5);
						}
						stmt.bindLong(6, list.get(i).tsize);
						if(list.get(i).rsize != null){
							stmt.bindDouble(7, list.get(i).rsize);
						} else {
							stmt.bindNull(7);
						}
						if(list.get(i).datetime != null) {
							stmt.bindString(8, list.get(i).datetime);
						} else {
							stmt.bindNull(8);
						}
					} else if(tablename.equals(PSXValue.PREVINFO)){
						stmt.bindNull(1);
						stmt.bindString(2, list.get(i).name);
						stmt.bindLong(3, list.get(i).ttime);
					}
					stmt.execute();
				}
			}
	
		} catch (Exception ex) {
			TraceLog saveTrace = new TraceLog(mContext);
			String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
			saveTrace.saveLog(ex,TAG + mname);
			Log.e(TAG,ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public int countTable(String name){
		SQLiteDatabase mdb = null;
		int ret = 0;
		Cursor cursor = null;
		
		try {
			mdb = dbOpen();
			cursor = dbQuery(mdb,"select count(id) from " + name);
			ret = cursor.getInt(0);
			dbClose(mdb);
		} catch (Exception ex) {
			TraceLog saveTrace = new TraceLog(mContext);
			String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
			saveTrace.saveLog(ex,TAG + mname);
			Log.e(TAG,ex.getMessage());
			ex.printStackTrace();
		}
		return ret;
	}

}
