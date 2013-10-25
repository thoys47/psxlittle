package jp.thoy.psxlittle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class PSXAsyncTask extends AsyncTask<Param, Integer, Result> {
	private final String CNAME = CommTools.getLastPart(this.getClass().getName(),".");
	private final static boolean isDebug = false;
	Context mContext;
	
	@Override
	protected void onPreExecute() {
		// TODO 自動生成されたメソッド・スタブ
		super.onPreExecute();
	}

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
		ArrayList<InfoTable> tempList = new ArrayList<InfoTable>();
		ArrayList<InfoTable> finalList = new ArrayList<InfoTable>();
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		String execFrom = param[0].sParam;
		mContext = param[0].cParam;
		TraceLog saveTrace = new TraceLog(mContext);
		if(isDebug){
			Log.w(CNAME,"from=" + execFrom);
			saveTrace.saveDebug("Start:" + execFrom);
		}

		storeBatteryInfo(mContext);
		
		Result result = new Result();
		Long totalTime = 0L;
		Long totalSize = 0L;
		Long prevTime = 0L;

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
					if(!temp1[4].equals("0") && !temp1[9].equals("(u:0") && !temp1[10].equals("s:0)")) {
						InfoTable dList = new InfoTable();
						dList.name = temp1[8];
						if(temp1[0].equals("system")) {
							dList.key = "system";
						} else if(temp1[0].equals("root")){
							dList.key = "root";
						} else if(temp1[0].equals("shell")){
							dList.key = "root";
						} else {
							dList.key = dList.name;
						}
						dList.tsize = Integer.parseInt(temp1[4]);
						temp2 = temp1[9].split(":");
						String ut = String.valueOf(temp2[1]);
						temp2 = temp1[10].split(":");
						String st = String.valueOf(temp2[1]);
	
						dList.ttime = Integer.parseInt(ut.substring(0, ut.length() - 1))
								+ Integer.parseInt(st.substring(0, st.length() - 1));
						dList.datetime = "";
						
						//totalTime += dList.ttime;
						//totalSize += dList.tsize;
						tempList.add(dList);
					}
				}
			}
			reader.close();
			process.waitFor();
			
			command = "cat /proc/stat";
			process = runtime.exec(command);
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String[] temp3 = new String[11];
			while ((line = reader.readLine()) != null) {
				temp3 = line.split("[\\s]+");
				if(temp3[0].equals("cpu")){
					totalTime = Long.parseLong(temp3[1]) + Long.parseLong(temp3[2]) + Long.parseLong(temp3[3]) + Long.parseLong(temp3[4]);
					break;
				}
			}
			reader.close();
			process.waitFor();
			PSXShared pShared = new PSXShared(mContext);
			prevTime = pShared.getPrevTime();
			pShared.putPrevTime(totalTime);
			
		} catch (Exception ex) {
			String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
			saveTrace.saveLog(ex,CNAME + mname);
			Log.w(CNAME,ex.getMessage());
			ex.printStackTrace();
			result.bResult = false;
			return result;
		}

		try{
			DataObject dObject;
			SQLiteDatabase db = null;
			String sql;
			dObject = new DataObject(mContext);
			Cursor cursor;

			if(isDebug) Log.w(CNAME,"Async 1st part");
			//Same name process is merged
			db = dObject.dbOpen();
			db.beginTransaction();
			sql = "delete from " + PSXValue.TEMPINFO;
			dObject.doSQL(db, sql);
			dObject.insertInfo(db, tempList, PSXValue.TEMPINFO);
			db.setTransactionSuccessful();
			db.endTransaction();
			
			sql = "select name,key,sum(ttime),sum(tsize) from "
							+ PSXValue.TEMPINFO + " group by name order by name desc";
			cursor = dObject.dbQuery(db, sql);
			
			PSXShared pShared = new PSXShared(mContext);
			int interval = pShared.getInterval();
			calendar.add(Calendar.MINUTE, (-1) * calendar.get(Calendar.MINUTE) % interval);
			calendar.add(Calendar.SECOND, (-1) * calendar.get(Calendar.SECOND));
			String datetime = CommTools.CalendarToString(calendar, CommTools.DATETIMELONG);

			while(cursor.getPosition() < cursor.getCount()){
				InfoTable fList = new InfoTable();
				fList.name = cursor.getString(0);
				fList.key = cursor.getString(1);
				fList.ttime = cursor.getInt(2);
				fList.tsize = cursor.getInt(3);
				fList.datetime = datetime;
				finalList.add(fList);
				totalSize += fList.tsize;
				cursor.moveToNext();
			}
			dObject.dbClose(db);
			
			for(int i = 0; i < finalList.size();i++){
				InfoTable dList = new InfoTable();
				dList.name = finalList.get(i).name;
				dList.ttime = finalList.get(i).ttime;
				prevList.add(dList);
			}

			if(isDebug) Log.w(CNAME,"Async 2nd part");

			if(execFrom.equals(PSXValue.BOOT) || execFrom.equals(PSXValue.INSTALL)) {
				
				db = dObject.dbOpen();
				db.beginTransaction();
				dObject.doSQL(db,"delete from " + PSXValue.PREVINFO);
				dObject.insertInfo(db,prevList, PSXValue.PREVINFO);
				db.setTransactionSuccessful();
				db.endTransaction();
				dObject.dbClose(db);
				if(isDebug) Log.w(CNAME,"End " + execFrom);
			} else {
				if(dObject.countTable(PSXValue.PREVINFO) == 0) {
					db = dObject.dbOpen();
					db.beginTransaction();
					dObject.insertInfo(db,prevList, PSXValue.PREVINFO);
					db.setTransactionSuccessful();
					db.endTransaction();
					dObject.dbClose(db);
					if(isDebug) Log.w(CNAME,"Prev has gone");
					saveTrace.saveDebug("Prev has gone");
				} else {
					if(prevTime == 0L){
						db = dObject.dbOpen();
						cursor = dObject.dbQuery(db, "select sum(ttime) from " + PSXValue.PREVINFO);
						prevTime = Long.parseLong(cursor.getString(0));
						dObject.dbClose(db);
					}
					if(totalTime <= prevTime){
						db = dObject.dbOpen();
						db.beginTransaction();
						dObject.doSQL(db,"delete from " + PSXValue.PREVINFO);
						dObject.insertInfo(db,prevList, PSXValue.PREVINFO);
						dObject.insertInfo(db,finalList, PSXValue.INFOTABLE);
						db.setTransactionSuccessful();
						db.endTransaction();
						dObject.dbClose(db);
						
						if(isDebug) Log.w(CNAME,"Decrease cpu time");
						saveTrace.saveDebug("ttl=" + totalTime + " pre=" + prevTime);
					} else {
						if(isDebug) Log.w(CNAME,"Async 3rd part");
						//Log.w(CNAME,"t=" + totalTime + " p=" + prevTime);
						totalTime = totalTime - prevTime;
						Long nTotalTime = 0L;
						sql = "select name,ttime from " + PSXValue.PREVINFO;
						db = dObject.dbOpen();
						cursor = dObject.dbQuery(db, sql);
						for(int i = 0;i < finalList.size();i++){
							if(cursor.getCount() != 0) {
								while(cursor.getPosition() < cursor.getCount()){
									if(finalList.get(i).name.equals(cursor.getString(0))){
										if(finalList.get(i).ttime >= Integer.parseInt(cursor.getString(1))){
											finalList.get(i).ttime = finalList.get(i).ttime - Integer.parseInt(cursor.getString(1));
										}
									}
									cursor.moveToNext();
								}
								cursor.moveToFirst();
							}
							//finalList.get(i).rtime = (double)totalTime;
							finalList.get(i).rsize = (double)totalSize;
							nTotalTime += finalList.get(i).ttime;
							//Log.w(CNAME,"n=" + finalList.get(i).name + " t=" + finalList.get(i).ttime);
						}
						dObject.dbClose(db);

						if(nTotalTime > totalTime){
							for(int i = 0;i < finalList.size();i++){
								finalList.get(i).rtime = (double)nTotalTime;
							}
						} else {
							for(int i = 0;i < finalList.size();i++){
								finalList.get(i).rtime = (double)totalTime;
							}
						}
						//	Log.e(CNAME,"nttl=" + nTotalTime + " ttl=" + totalTime);
						//	saveTrace.saveDebug("nttl=" + nTotalTime + " ttl=" + totalTime);
						//}						
						if(isDebug) Log.w(CNAME,"Async 4th part");
						db = dObject.dbOpen();
						db.beginTransaction();
						dObject.insertInfo(db,finalList, PSXValue.INFOTABLE);
						sql = "delete from " + PSXValue.PREVINFO;
						dObject.doSQL(db,sql);
						dObject.insertInfo(db,prevList, PSXValue.PREVINFO);
						db.setTransactionSuccessful();
						db.endTransaction();
						dObject.dbClose(db);

						if(isDebug){
							Log.w(CNAME,"End Second");
						}
					}//totalTime <= prevTime
				}//2nd execute
			}//execute boot or install
			result.bResult = true;
		} catch (Exception ex) {
			String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
			saveTrace.saveLog(ex,CNAME + mname);
			Log.e(CNAME,ex.getMessage());
			ex.printStackTrace();
			result.bResult = false;
		}
		result.bResult = true;
		return result;
	}
	private void storeBatteryInfo(Context context){
		
		int interval;
		PSXShared pShared = new PSXShared(context);
		interval = pShared.getInterval();
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.add(Calendar.MINUTE, (-1) * calendar.get(Calendar.MINUTE) % interval);
		calendar.add(Calendar.SECOND, (-1) * calendar.get(Calendar.SECOND));
		String datetime = CommTools.CalendarToString(calendar, CommTools.DATETIMELONG);
		
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent intent = context.registerReceiver(null, ifilter);
		GetBatteryInfo batteryInfo = new GetBatteryInfo();
		BatteryInfo bInfo = batteryInfo.getInfo(intent);

		DataObject dObject = new DataObject(context);
		SQLiteDatabase db = dObject.dbOpen();
		String sql = DataObject.makeBaseSQL("INSERT", PSXValue.BATTINFO);
		sql += "(null,";
		sql += String.valueOf(bInfo.rLevel) + ",";
		sql += "'" + bInfo.status + "',";
		sql += "'" + bInfo.plugged + "',";
		sql += String.valueOf(bInfo.temp) + ',';
		sql += "'" + PSXValue.NAME_BATT + "',";
		sql += "'" + datetime + "')";
		dObject.doSQL(db, sql);
		dObject.dbClose(db);
	}

}
