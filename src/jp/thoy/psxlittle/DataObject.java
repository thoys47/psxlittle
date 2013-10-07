package jp.thoy.psxlittle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataObject {

	String mSql;
	Context mContext;
	final String CNAME = CommTools.getLastPart(this.getClass().getName(),".");
	final static boolean isDebug = false;
	
	final static String[] infoColumn = {"ID","PID","UID","NAME","KEY","UTIME","STIME","TTIME","RTIME",
										"RSS","TSIZE","RSIZE","DATETIME"};
	final static String[] prevColumn = {"ID","PID","NAME","UTIME","STIME","TTIME"};
	final static String[] infoType = {"INTEGER PRIMARY KEY AUTOINCREMENT","TEXT","TEXT","TEXT","TEXT","INTEGER","INTEGER","INTEGER","REAL",
										"INTEGER","INTEGER","REAL","TEXT"};
	final static String[] prevType = {"INTEGER PRIMARY KEY AUTOINCREMENT","TEXT","TEXT","INTEGER","INTEGER","INTEGER"};
	
	final static String [] battColumn = {"ID","LEBEL","STATUS","PLUGGED","TEMP","NAME","DATETIME"};
	final static String [] battType = {"INTEGER PRIMARY KEY AUTOINCREMENT","INTEGER","TEXT","TEXT","INTEGER","TEXT","TEXT"};
	
	final static String[] tempColumn = {"ID","KEY","SUM","MAX","AVG"};
	final static String[] tempType = {"INTEGER PRIMARY KEY AUTOINCREMENT","TEXT","REAL","REAL","REAL"};

	final static String[] detailColumn = {"ID","KEY","SUM","MAX","AVG"};
	final static String[] detailType = {"INTEGER PRIMARY KEY AUTOINCREMENT","TEXT","REAL","REAL","REAL"};

	
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
				for(int i = 0;i < infoColumn.length;i++){
					sql += infoColumn[i] + " " + infoType[i] + ",";
				}
			} else if(tablename.equals(PSXValue.PREVINFO)){
				sql = "CREATE TABLE IF NOT EXISTS " + tablename + " (";
				for(int i = 0;i < prevColumn.length;i++){
					sql += prevColumn[i] + " " + infoType[i] + ",";
				}
			} else if(tablename.equals(PSXValue.BATTINFO)){
				sql = "CREATE TABLE IF NOT EXISTS " + tablename + " (";
				for(int i = 0;i < battColumn.length;i++){
					sql += battColumn[i] + " " + battType[i] + ",";
				}
			}
			sql = sql.substring(0, sql.length() - 1);
			sql += ")";
		} else if (option.equals("INSERT")){
			if(tablename.equals(PSXValue.INFOTABLE)){
				sql = "INSERT INTO " + tablename + " (";
				for(int i = 0;i < infoColumn.length;i++){
					sql += infoColumn[i] + ","; 
				}
			} else if(tablename.equals(PSXValue.PREVINFO)){
				sql = "INSERT INTO " + tablename + " (";
				for(int i = 0;i < prevColumn.length;i++){
					sql += prevColumn[i] + ","; 
				}
			} else if(tablename.equals(PSXValue.BATTINFO)){
				sql = "INSERT INTO " + tablename + " (";
				for(int i = 0;i < battColumn.length;i++){
					sql += battColumn[i] + ","; 
				}
			}
			sql = sql.substring(0, sql.length() - 1);
			sql += ") values (";
		} else if(option.equals("SELECT")){
			if(tablename.equals(PSXValue.INFOTABLE)){
				sql = "SELECT ";
				for(int i = 0;i < infoColumn.length;i++){
					sql += infoColumn[i] + ","; 
				}
				sql = sql.substring(0, sql.length() - 1);
				sql += " from " + tablename;
			} else if(tablename.equals(PSXValue.PREVINFO)){
				sql = "SELECT ";
				for(int i = 0;i < prevColumn.length;i++){
					sql += prevColumn[i] + ","; 
				}
				sql = sql.substring(0, sql.length() - 1);
				sql += " from " + tablename;
			}
			
		}
		return sql;
	}	
	
	public static String maketempSQL(String option,String tablename) {
		String sql = null;
		if(option.equals("CREATE")){
			sql = "CREATE TABLE IF NOT EXISTS " + tablename + " (";
			for(int i = 0;i < tempColumn.length;i++){
				sql += tempColumn[i] + " " + tempType[i] + ",";
			}
			sql = sql.substring(0, sql.length() - 1);
			sql += ")";
		} else if (option.equals("INSERT")){
			sql = "INSERT INTO " + tablename + " (";
			for(int i = 0;i < tempColumn.length;i++){
				sql += tempColumn[i] + ","; 
			}
			sql = sql.substring(0, sql.length() - 1);
			sql += ") values (";
		}
		return sql;
	}	

	public static String makedetailSQL(String option,String tablename) {
		String sql = null;
		if(option.equals("CREATE")){
			sql = "CREATE TABLE IF NOT EXISTS " + tablename + " (";
			for(int i = 0;i < detailColumn.length;i++){
				sql += detailColumn[i] + " " + detailType[i] + ",";
			}
			sql = sql.substring(0, sql.length() - 1);
			sql += ")";
		} else if (option.equals("INSERT")){
			sql = "INSERT INTO " + tablename + " (";
			for(int i = 0;i < detailColumn.length;i++){
				sql += detailColumn[i] + ","; 
			}
			sql = sql.substring(0, sql.length() - 1);
			sql += ") values (";
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
				saveTrace.saveLog(ex,CNAME + mname);
				Log.e(CNAME,ex.getMessage());
				ex.printStackTrace();
			}
		}
		if(isDebug) Log.w(CNAME,"sql=" + sql);
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
			saveTrace.saveLog(ex,CNAME + mname);
			Log.e(CNAME,ex.getMessage());
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
			saveTrace.saveLog(ex,CNAME + mname);
			Log.e(CNAME,ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public void insertInfo(SQLiteDatabase mdb,ArrayList<InfoTable> list,String tablename){

		String sql = "";
		try{
			for(int i = 0;i < list.size();i++){
				if(tablename.equals(PSXValue.INFOTABLE)){
					sql = makeBaseSQL("INSERT",tablename);
					sql += "null,";
					sql += "'" + list.get(i).pid + "',";
					sql += "'" + list.get(i).uid + "',";
					sql += "'" + list.get(i).name + "',";
					sql += "'" + list.get(i).key + "',";
					sql += String.valueOf(list.get(i).utime) + ",";
					sql += String.valueOf(list.get(i).stime) + ",";
					sql += String.valueOf(list.get(i).ttime) + ",";
					sql += String.valueOf(list.get(i).rtime) + ",";
					sql += String.valueOf(list.get(i).rss) + ",";
					sql += String.valueOf(list.get(i).tsize) + ",";
					sql += String.valueOf(list.get(i).rsize) + ",";
					sql += "'" + list.get(i).datetime + "')";
				} else if(tablename.equals(PSXValue.PREVINFO)){
					sql = makeBaseSQL("INSERT",tablename);
					sql += "null,";
					sql += "'" + list.get(i).pid + "',";
					sql += "'" + list.get(i).name + "',";
					sql += String.valueOf(list.get(i).utime) + ",";
					sql += String.valueOf(list.get(i).stime) + ",";
					sql += String.valueOf(list.get(i).ttime) + ")";
				}
				mdb.execSQL(sql);
				if(isDebug){
					Log.w(CNAME,sql);
				}
			}
	
		} catch (Exception ex) {
			TraceLog saveTrace = new TraceLog(mContext);
			String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
			saveTrace.saveLog(ex,CNAME + mname);
			Log.e(CNAME,ex.getMessage());
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
			saveTrace.saveLog(ex,CNAME + mname);
			Log.e(CNAME,ex.getMessage());
			ex.printStackTrace();
		}
		return ret;
	}

	public int exportData(String name){
		SQLiteDatabase mdb = null;
		int ret = 0;

		File file = null;
		file = new File(mContext.getExternalFilesDir(null), PSXValue.EXPORTFILE);
		
		try {

			String sql = makeBaseSQL("SELECT",name);
			mdb = this.dbOpen();
			Cursor cursor = this.dbQuery(mdb, sql);
			this.dbClose(mdb);
			if(cursor.getCount() != 0){
				file.createNewFile();
		        if (file != null && file.exists()) {
		        	FileWriter fw = new FileWriter(file, false);
		        	PrintWriter pw = new PrintWriter(fw,true);
		    		while(cursor.getPosition() < cursor.getCount()){
			        	int num = 0;
			        	if(name.equals(PSXValue.INFOTABLE)) {
			        		num = infoColumn.length;
			        	} else if (name.equals(PSXValue.PREVINFO)){
			        		num = prevColumn.length;
			        	}
			        	String line = "";
			        	for(int j = 0;j < num;j++){
			        		line += cursor.getString(j);
			        		if(j != num - 1) {
			        			line += ",";
			        		}
			        	}
			        	pw.println(line);
			        	ret++;
			        	cursor.moveToNext();
		    		}
		        	pw.close();
		        } 
			}
		} catch (Exception ex) {
			TraceLog saveTrace = new TraceLog(mContext);
			String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
			saveTrace.saveLog(ex,CNAME + mname);
			Log.e(CNAME,ex.getMessage());
			ex.printStackTrace();
		}
		return ret;
	}
}
