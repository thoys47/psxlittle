package jp.thoy.psxlittle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class PSXAsyncTask extends AsyncTask<Param, Integer, Result> {
	private final String CNAME = CommTools.getLastPart(this.getClass().getName(),".");
	private final static boolean isDebug = true;
	Context mContext;
	
	
	@Override
	protected void onPostExecute(Result result) {
		// TODO 自動生成されたメソッド・スタブ
		super.onPostExecute(result);
		
	}

	@Override
	protected Result doInBackground(Param... param) {
		// TODO 自動生成されたメソッド・スタブ
		Thread.setDefaultUncaughtExceptionHandler(new TraceLog(mContext));

		ArrayList<InfoTable> prevList = new ArrayList<InfoTable>();
		ArrayList<InfoTable> finalList = new ArrayList<InfoTable>();
		Calendar calendar = Calendar.getInstance();
		String execFrom = param[0].sParam;
		mContext = param[0].cParam;
		TraceLog saveTrace = new TraceLog(mContext);
		if(isDebug){
			Log.w(CNAME,"from=" + execFrom);
			saveTrace.saveDebug("Start:" + execFrom);
		}
		
		Result result = new Result();
		Long totalTime = Long.valueOf(0);
		Long totalSize = Long.valueOf(0);
		Long prevTime = Long.valueOf(0);
		//Long ntotalTime = Long.valueOf(0);

		try {
			String command = "ps -x";
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			String[] temp1 = new String[12];
			String[] temp2 = new String[2];
			while ((line = reader.readLine()) != null) {
				temp1 = line.split("[\\s]+");
				if(!temp1[1].equals("PID")){
					//if(!temp1[4].equals("0") && !temp1[9].equals("(u:0") && !temp1[10].equals("s:0)")) {
					if(!temp1[4].equals("0") && !temp1[9].equals("(u:0") && !temp1[10].equals("s:0)")) {
						InfoTable dList = new InfoTable();
						initList(dList);
						dList.uid = temp1[0];
						dList.pid = temp1[1];
						dList.name = temp1[8];
						if(dList.uid.equals("system")) {
							dList.key = "system";
						} else if(dList.uid.equals("root")){
							dList.key = "root";
						} else if(dList.uid.equals("shell")){
							dList.key = "root";
						} else {
							dList.key = dList.name;
						}
						dList.rss = Integer.parseInt(temp1[4]);
						dList.tsize = Integer.parseInt(temp1[4]);
						dList.datetime = CommTools.CalendarToString(calendar,CommTools.DATETIMELONG);
						temp2 = temp1[9].split(":");
						String ut = String.valueOf(temp2[1]);
						dList.utime = Integer.parseInt(ut.substring(0, ut.length() - 1));
						temp2 = temp1[10].split(":");
						String st = String.valueOf(temp2[1]);
						dList.stime = Integer.parseInt(st.substring(0, st.length() - 1));
	
						dList.ttime = dList.utime + dList.stime;
						
						totalTime += dList.ttime;
						totalSize += dList.tsize;
						
						finalList.add(dList);
					}
				}
			}
			reader.close();
			process.waitFor();
		} catch (Exception ex) {
			saveTrace = new TraceLog(mContext);
			String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
			saveTrace.saveLog(ex,CNAME + mname);
			Log.w(CNAME,ex.getMessage());
			ex.printStackTrace();
			result.bResult = false;
			return result;
		}

		if(isDebug){
			Log.w(CNAME,"Check point 1");
		}
		DataObject dObject;
		SQLiteDatabase db = null;
		try{
			dObject = new DataObject(mContext);
			Cursor cursor;
			if(execFrom.equals(PSXService.BOOT)) {
				//BootReceiver
				if(isDebug) {
					Log.w(CNAME,"Start Boot");
				}
				db = dObject.dbOpen();
				db.beginTransaction();
				dObject.doSQL(db,"delete from " + DataObject.PREVINFO);
				dObject.insertInfo(db,finalList, DataObject.PREVINFO);
				db.setTransactionSuccessful();
				db.endTransaction();
				dObject.dbClose(db);
				if(isDebug){
					Log.w(CNAME,"End Boot");
				}
			} else if(execFrom.equals(PSXService.INSTALL)) {
				if(isDebug) {
					Log.w(CNAME,"Start Install");
				}
				db = dObject.dbOpen();
				db.beginTransaction();
				dObject.insertInfo(db,finalList, DataObject.PREVINFO);
				db.setTransactionSuccessful();
				db.endTransaction();
				dObject.dbClose(db);
				if(isDebug)	{
					Log.w(CNAME,"End Install");
				}
			} else {
				db = dObject.dbOpen();
				cursor = dObject.dbQuery(db, "select count(id) from " + DataObject.PREVINFO);
				if(cursor.getString(0).equals("0")){
					if(isDebug)	{
						Log.w(CNAME,"prev deleted");
					}
					db = dObject.dbOpen();
					db.beginTransaction();
					dObject.insertInfo(db,finalList, DataObject.PREVINFO);
					db.setTransactionSuccessful();
					db.endTransaction();
					dObject.dbClose(db);
					if(isDebug)	{
						Log.w(CNAME,"End prev deleted");
					}
				} else {
					
					cursor = dObject.dbQuery(db, "select sum(ttime) from " + DataObject.PREVINFO);
					dObject.dbClose(db);
					prevTime = Long.parseLong(cursor.getString(0));
					
					if(isDebug){
						Log.w(CNAME,"prev = " + prevTime + " total = " + totalTime);
					}
					
					if(totalTime <= prevTime){
						if(isDebug){
							Log.w(CNAME,"Counter is back");
						}
						db = dObject.dbOpen();
						db.beginTransaction();
						dObject.doSQL(db,"delete from " + DataObject.PREVINFO);
						dObject.insertInfo(db,finalList, DataObject.PREVINFO);
						db.setTransactionSuccessful();
						db.endTransaction();
						dObject.dbClose(db);
						if(isDebug){
							Log.w(CNAME,"End back");
						}
					} else {
						totalTime = totalTime - prevTime;
						for(int i = 0; i < finalList.size();i++){
							InfoTable dList = new InfoTable();
							dList.pid = finalList.get(i).pid;
							dList.name = finalList.get(i).name;
							dList.utime = finalList.get(i).utime;
							dList.stime = finalList.get(i).stime;
							dList.ttime = finalList.get(i).ttime;
							prevList.add(dList);
						}
						if(isDebug){
							Log.w(CNAME,"info size=" + finalList.size() + " prev size=" + prevList.size());
						}
						if(isDebug){
							Log.w(CNAME,"Start Second");
						}
						for(int i = 0;i < finalList.size();i++){
							String sql = "select utime,stime from " + DataObject.PREVINFO;
							sql += " where pid = '" + finalList.get(i).pid + "' and name = '" + finalList.get(i).name + "'";
							db = dObject.dbOpen();
							cursor = dObject.dbQuery(db, sql);
							dObject.dbClose(db);
							if(cursor.getCount() != 0) {
								if(finalList.get(i).utime >= Integer.parseInt(cursor.getString(0))){
									finalList.get(i).utime = finalList.get(i).utime - Integer.parseInt(cursor.getString(0));
								}
								if(finalList.get(i).stime >= Integer.parseInt(cursor.getString(1))){
									finalList.get(i).stime = finalList.get(i).stime - Integer.parseInt(cursor.getString(1));
								}
							} else if(cursor.getCount() > 1) {
								Log.e(CNAME,"Too many Items in previnfo");
							}
							finalList.get(i).ttime = finalList.get(i).utime + finalList.get(i).stime;
							double ttmp = (double)(finalList.get(i).ttime * 100) / (double)totalTime;
							finalList.get(i).rtime = (double)((int)((ttmp * 100) + 0.5)) / 100;
							double stmp = (double)(finalList.get(i).tsize * 100) / (double)totalSize;
							finalList.get(i).rsize = (double)((int)((stmp * 100) + 0.5)) / 100;
							if(finalList.get(i).rsize > 100){
								Log.e(CNAME,"name=" + finalList.get(i).name + " totalSize" + totalSize + " rsime=" + finalList.get(i).rsize);
							}
							//ntotalTime += finalList.get(i).ttime;
						}
						if(isDebug){
							Log.w(CNAME,"before insert");
						}
						db = dObject.dbOpen();
						db.beginTransaction();
						dObject.insertInfo(db,finalList, DataObject.INFOTABLE);
						dObject.doSQL(db,"delete from " + DataObject.PREVINFO);
						
						PSXShared pShared = new PSXShared(mContext);
						int length = pShared.getLength();
						calendar.add(Calendar.HOUR_OF_DAY, (-1) * length);
						dObject.doSQL(db,"delete from " + DataObject.INFOTABLE
								+ " where datetime < '" + CommTools.CalendarToString(calendar,CommTools.DATETIMELONG) + "'");
						dObject.insertInfo(db,prevList, DataObject.PREVINFO);
						db.setTransactionSuccessful();
						db.endTransaction();
	
						if(isDebug){
							Log.w(CNAME,"after insert");
						}
	
						dObject.dbClose(db);
						if(isDebug){
							Log.w(CNAME,"End Second");
						}
					}//if delete previnfo data.
				}//2nd execute
			}//1st execute
			result.bResult = true;
		} catch (Exception ex) {
			if (db != null){
				db.endTransaction();
				db.close();
			}
			saveTrace = new TraceLog(mContext);
			String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
			saveTrace.saveLog(ex,CNAME + mname);
			Log.e(CNAME,ex.getMessage());
			ex.printStackTrace();
			result.bResult = false;
		}
		
		return result;
	}

	private void initList(InfoTable dList){
		dList.uid = "";
		dList.pid = "";
		dList.name = "";
		dList.key = "";
		dList.datetime = "";
		dList.utime = 0;
		dList.stime = 0;
		dList.ttime = 0;
		dList.rtime = 0.0;
		dList.rss = 0;
		dList.tsize = 0;
		dList.rsize = 0.0;
	}
	
}
