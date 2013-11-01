package jp.thoy.psxlittle;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper {

	final String TAG = CommTools.getLastPart(this.getClass().getName(),".");
	final static boolean isDebug = PSXValue.isDebug;
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
		if(isDebug) Log.w(TAG,"enter helper");
		try{
			mdb.execSQL(DataObject.makeBaseSQL("CREATE",PSXValue.INFOTABLE));
			mdb.execSQL(DataObject.makeBaseSQL("CREATE",PSXValue.TEMPINFO));
			mdb.execSQL(DataObject.makeBaseSQL("CREATE",PSXValue.PREVINFO));
			mdb.execSQL(DataObject.makeBaseSQL("CREATE",PSXValue.BATTINFO));
			mdb.execSQL(DataObject.makeBaseSQL("CREATE",PSXValue.TEMPCPU));
			mdb.execSQL(DataObject.makeBaseSQL("CREATE",PSXValue.TEMPMEM));
			mdb.execSQL(DataObject.makeBaseSQL("CREATE",PSXValue.DETAILCPU));
			mdb.execSQL(DataObject.makeBaseSQL("CREATE",PSXValue.DETAILMEM));
			mdb.execSQL("CREATE INDEX INFO_IDX01 ON " + PSXValue.INFOTABLE + "(DATETIME)");
			mdb.execSQL("CREATE INDEX INFO_IDX02 ON " + PSXValue.INFOTABLE + "(KEY)");
			mdb.execSQL("CREATE INDEX PREV_IDX03 ON " + PSXValue.PREVINFO + "(NAME)");
			mdb.execSQL("CREATE INDEX BATT_IDX01 ON " + PSXValue.BATTINFO + "(DATETIME)");
			mdb.execSQL("CREATE INDEX BATT_IDX02 ON " + PSXValue.BATTINFO + "(NAME)");
		} catch (Exception ex){
			TraceLog saveTrace = new TraceLog(mContext);
			String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
			saveTrace.saveLog(ex,TAG + mname);
			Log.e(TAG,ex.getMessage());
			ex.printStackTrace();
		}
		if(isDebug) Log.w(TAG,"exit helper");
	}

	@Override
	public void onUpgrade(SQLiteDatabase mdb, int oldVersion, int newVersion) throws SQLException {
		// TODO 自動生成されたメソッド・スタブ
		try{
			if(oldVersion == 1){
				mdb.execSQL(DataObject.makeBaseSQL("CREATE",PSXValue.BATTINFO));
				mdb.execSQL("CREATE INDEX PREV_IDX03 ON " + PSXValue.PREVINFO + "(NAME)");
				mdb.execSQL("CREATE INDEX BATT_IDX01 ON " + PSXValue.BATTINFO + "(DATETIME)");
				mdb.execSQL("CREATE INDEX BATT_IDX02 ON " + PSXValue.BATTINFO + "(NAME)");
				mdb.execSQL("DROP INDEX INFO_IDX03");
				mdb.execSQL("DROP INDEX INFO_IDX04");
				mdb.execSQL("DROP INDEX INFO_IDX05");
				mdb.execSQL("DROP INDEX INFO_IDX06");
			}
			if(oldVersion == 2){
				mdb.execSQL(DataObject.makeBaseSQL("CREATE",PSXValue.TEMPINFO));
			}
		} catch (Exception ex){
			TraceLog saveTrace = new TraceLog(mContext);
			String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
			saveTrace.saveLog(ex,TAG + mname);
			Log.e(TAG,ex.getMessage());
			ex.printStackTrace();
		}
	}
}
