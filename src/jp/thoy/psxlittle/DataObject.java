package jp.thoy.psxlittle;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DataObject {

	Context mContext;
	final String CNAME = CommTools.getLastPart(this.getClass().getName(),".");
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
	
	public void insertInfo(SQLiteDatabase db,ArrayList<InfoTable> list,String tablename){

		String sql = "";
		try{
			sql = makeBaseSQL("INSERT",tablename);
			if(tablename.equals(PSXValue.INFOTABLE) || tablename.equals(PSXValue.TEMPINFO)){
				sql += "(?,?,?,?,?,?,?,?)";
			} else if(tablename.equals(PSXValue.PREVINFO)){
				sql += "(?,?,?)";
			}
			
			SQLiteStatement stmt = db.compileStatement(sql);
			for(int i = 0;i < list.size();i++){
				if(tablename.equals(PSXValue.INFOTABLE) || tablename.equals(PSXValue.TEMPINFO)){
					if(isDebug){
						Log.w(CNAME,"1" + sql);
					}
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
					if(isDebug){
						Log.w(CNAME,"2" + sql);
					}
				} else if(tablename.equals(PSXValue.PREVINFO)){
					stmt.bindNull(1);
					stmt.bindString(2, list.get(i).name);
					stmt.bindLong(3, list.get(i).ttime);
				}
				if(isDebug){
					Log.w(CNAME,sql);
				}
				stmt.execute();
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
		file = new File(mContext.getExternalFilesDir(null), name + ".csv");
		
		try {

			String sql = makeBaseSQL("SELECT",name);
			mdb = this.dbOpen();
			Cursor cursor = this.dbQuery(mdb, sql);
			if(cursor.getCount() != 0){
				file.createNewFile();
		        if (file != null && file.exists()) {
		        	FileWriter fw = new FileWriter(file, false);
		        	PrintWriter pw = new PrintWriter(fw,true);
		        	
		        	int num = 0;
		        	String line = "";
		        	if(name.equals(PSXValue.INFOTABLE)) {
		        		num = PSXValue.infoColumn.length;
		    			for(int j = 0;j < num;j++){
		        			line += "\"";
			        		line += PSXValue.infoColumn[j];
		        			line += "\",";
			        	}
		        	} else if (name.equals(PSXValue.PREVINFO)){
		        		num = PSXValue.prevColumn.length;
		    			for(int j = 0;j < num;j++){
		        			line += "\"";
			        		line += PSXValue.prevColumn[j];
			        		line += "\",";
			        	}
		        	} else if (name.equals(PSXValue.BATTINFO)){
		        		num = PSXValue.battColumn.length;
		    			for(int j = 0;j < num;j++){
		        			line += "\"";
			        		line += PSXValue.battColumn[j];
			        		line += "\",";
			        	}
		        	}
					line = line.substring(0, line.length() - 1);
		        	pw.println(line);
		        	
		    		while(cursor.getPosition() < cursor.getCount()){
		    			line = "";
		    			for(int j = 0;j < num;j++){
		    				switch(cursor.getType(j)){
		    					case Cursor.FIELD_TYPE_INTEGER:
					        		line += String.valueOf(cursor.getInt(j));
					        		break;
		    					case Cursor.FIELD_TYPE_FLOAT:
					        		line += String.valueOf(cursor.getDouble(j));
					        		break;
		    					case Cursor.FIELD_TYPE_STRING:
					        		line += "\"" + cursor.getString(j) + "\"";
					        		break;
					        	default :
					        		line += "\"" + cursor.getString(j) + "\"";
		    				}
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
			this.dbClose(mdb);
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
