package jp.thoy.psxlittle;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper {

	final String CNAME = CommTools.getLastPart(this.getClass().getName(),".");
	final static boolean isDebug = false;
	Context mContext;

	public DBOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO 自動生成されたコンストラクター・スタブ
		mContext = context;
		Thread.setDefaultUncaughtExceptionHandler(new TraceLog(context));
	}
	

	@Override
	public void onCreate(SQLiteDatabase mdb){
		// TODO 自動生成されたメソッド・スタブ
		if(isDebug) Log.w(CNAME,"enter helper");
		try{
			mdb.execSQL(DataObject.makeBaseSQL("CREATE",DataObject.INFOTABLE));
			mdb.execSQL(DataObject.makeBaseSQL("CREATE",DataObject.PREVINFO));
			mdb.execSQL(DataObject.makeBaseSQL("CREATE",DataObject.BATTINFO));
			mdb.execSQL(DataObject.maketempSQL("CREATE",DataObject.TEMPCPU));
			mdb.execSQL(DataObject.maketempSQL("CREATE",DataObject.TEMPMEM));
			mdb.execSQL(DataObject.makedetailSQL("CREATE",DataObject.DETAILCPU));
			mdb.execSQL(DataObject.makedetailSQL("CREATE",DataObject.DETAILMEM));
			mdb.execSQL("CREATE INDEX INFO_IDX01 ON " + DataObject.INFOTABLE + "(DATETIME)");
			mdb.execSQL("CREATE INDEX INFO_IDX02 ON " + DataObject.INFOTABLE + "(KEY)");
			mdb.execSQL("CREATE INDEX PREV_IDX02 ON " + DataObject.PREVINFO + "(PID)");
			mdb.execSQL("CREATE INDEX PREV_IDX03 ON " + DataObject.PREVINFO + "(NAME)");
			mdb.execSQL("CREATE INDEX BATT_IDX01 ON " + DataObject.BATTINFO + "(DATETIME)");
			mdb.execSQL("CREATE INDEX BATT_IDX02 ON " + DataObject.BATTINFO + "(NAME)");
		} catch (Exception ex){
			TraceLog saveTrace = new TraceLog(mContext);
			String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
			saveTrace.saveLog(ex,CNAME + mname);
			Log.e(CNAME,ex.getMessage());
			ex.printStackTrace();
		}
		if(isDebug) Log.w(CNAME,"exit helper");
	}

	@Override
	public void onUpgrade(SQLiteDatabase mdb, int oldVersion, int newVersion) throws SQLException {
		// TODO 自動生成されたメソッド・スタブ
		try{
			if(oldVersion == 1){
				mdb.execSQL(DataObject.makeBaseSQL("CREATE",DataObject.BATTINFO));
				mdb.execSQL("CREATE INDEX PREV_IDX03 ON " + DataObject.PREVINFO + "(NAME)");
				mdb.execSQL("CREATE INDEX BATT_IDX01 ON " + DataObject.BATTINFO + "(DATETIME)");
				mdb.execSQL("CREATE INDEX BATT_IDX02 ON " + DataObject.BATTINFO + "(NAME)");
				mdb.execSQL("DROP INDEX INFO_IDX03");
				mdb.execSQL("DROP INDEX INFO_IDX04");
				mdb.execSQL("DROP INDEX INFO_IDX05");
				mdb.execSQL("DROP INDEX INFO_IDX06");
			}
		} catch (Exception ex){
			TraceLog saveTrace = new TraceLog(mContext);
			String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
			saveTrace.saveLog(ex,CNAME + mname);
			Log.e(CNAME,ex.getMessage());
			ex.printStackTrace();
		}
	}
}
