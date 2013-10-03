package jp.thoy.psxlittle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jp.thoy.psxlittle.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	public static final int TAB_NUM = 2;
	public final static String K_PAGE = "PAGE";
	public final static String K_KEY = "KEY";
	private final String CNAME = CommTools.getLastPart(this.getClass().getName(),".");
	private final static boolean isDebug = true;
	
	final static Calendar calendar = Calendar.getInstance();
	final static int year = calendar.get(Calendar.YEAR);
	final static int month = calendar.get(Calendar.MONTH);
	final static int day = calendar.get(Calendar.DAY_OF_MONTH);
	final static int hour = calendar.get(Calendar.HOUR_OF_DAY);
	final static int min = calendar.get(Calendar.MINUTE);

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	PagerAdapter mPagerAdapter;
	
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Context context = getApplicationContext();

		Thread.setDefaultUncaughtExceptionHandler(new TraceLog(context));
		
		PackageManager mPackageManager = getPackageManager();
        
		try{
			
			DataObject mDataObject = new DataObject(context);
			SQLiteDatabase mdb = mDataObject.dbOpen();
			mDataObject.dbClose(mdb);
			mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
			mPagerAdapter.setPackageManager(mPackageManager);
			mViewPager = (ViewPager)findViewById(R.id.viewPager);
			mViewPager.setAdapter(mPagerAdapter);
			
		} catch (Exception ex){
			TraceLog saveTrace = new TraceLog(context);
			String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
			saveTrace.saveLog(ex,CNAME + mname);
			Log.e(CNAME,ex.getMessage());
			ex.printStackTrace();
		}

	}

	@Override
	protected void onStart() {
		// TODO 自動生成されたメソッド・スタブ
		super.onStart();

		Context context = getApplicationContext();
		DataObject mDO = new DataObject(context);
		PSXShared pShared = new PSXShared(context);
		
		long before = pShared.getBefore();
		if(before == 0L){
			if(isDebug){
				Log.w(CNAME,"install from main count" + mDO.countTable(DataObject.PREVINFO));
			}
			PSXAsyncTask aTask = new PSXAsyncTask();
			Param  mParam = new Param();
			ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Activity.ACTIVITY_SERVICE);
			mParam.cParam = context;
			mParam.aParam = mActivityManager;
			mParam.sParam = PSXService.INSTALL;
			mParam.clParam = CNAME;
			aTask.execute(mParam);

			pShared.putBefore(Calendar.getInstance());
		}
		
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(Intent.ACTION_DATE_CHANGED);
		iFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
		iFilter.addAction(Intent.ACTION_TIME_CHANGED);
		iFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

		BootReceiver mReceiver = new BootReceiver();
		try{
			context.unregisterReceiver(mReceiver);
		} catch (IllegalArgumentException ex) {
			;
		}
		try{
			context.registerReceiver(mReceiver, iFilter);
		} catch (Exception ex){
			ex.printStackTrace();
		}
		RegistTask rTask = new RegistTask(getApplicationContext());
		rTask.StartCommand();
		if(isDebug){
			Log.w(CNAME,"OnStart");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO 自動生成されたメソッド・スタブ
		Intent mIntent;

		switch(item.getItemId()){
		case R.id.action_reload:
			ViewPager vPager = (ViewPager)findViewById(R.id.viewPager);
			PackageManager pManager = getPackageManager();
			SummarizeData iCalc = new SummarizeData(getApplicationContext());
			ArrayList<TempTable> list;
			ListAdapter adapter;
			ListView mListView;
			switch(vPager.getCurrentItem()){
			case 0:
				list = iCalc.calculate(null,0);
				if(list == null){
					Toast.makeText(getApplicationContext(), getString(R.string.strNoData), Toast.LENGTH_SHORT).show();
					return super.onMenuItemSelected(featureId, item);
				}
				mListView = (ListView)findViewById(R.id.listCPU);
				adapter = new ListAdapter(this,list,pManager);
				break;
			case 1:
				list = iCalc.calculate(null,1);
				if(list == null){
					Toast.makeText(getApplicationContext(), getString(R.string.strNoData), Toast.LENGTH_SHORT).show();
					return super.onMenuItemSelected(featureId, item);
				}
				mListView = (ListView)findViewById(R.id.listMEM);
				adapter = new ListAdapter(this,list,pManager);
				break;
			default :
				mListView = null;
				adapter = null;
			}
			mListView.setAdapter(adapter);
			mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO 自動生成されたメソッド・スタブ
					TextView tView = (TextView)view.findViewById(R.id.textKey);
					ViewPager vPager = (ViewPager)findViewById(R.id.viewPager);
					Intent mIntent;
					if(isDebug) Log.w(CNAME,"getText=" + tView.getText());
					if(!tView.getText().equals("root") && !tView.getText().equals("system")){ 
						mIntent = new Intent(view.getContext(),ChartActivity.class);
						mIntent.putExtra("PAGE",String.valueOf(vPager.getCurrentItem()));
						mIntent.putExtra("KEY",tView.getText());
						startActivity(mIntent);
					} else {
						mIntent = new Intent(view.getContext(),DetailActivity.class);
						mIntent.putExtra("PAGE",String.valueOf(vPager.getCurrentItem()));
						mIntent.putExtra("KEY",tView.getText());
						startActivity(mIntent);
					}
				}
			});

			mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO 自動生成されたメソッド・スタブ
					PackageManager pManager = getPackageManager();
					TextView tView = (TextView)view.findViewById(R.id.textSysName);
					Intent intent = pManager.getLaunchIntentForPackage((tView.getText()).toString());
					try{
						startActivity(intent);
					} catch (Exception ex) {
						Toast.makeText(view.getContext(), getString(R.string.strDontExec), Toast.LENGTH_SHORT).show();
					}
					return false;
				}
				
			});
			break;
		case R.id.action_debug:
			mIntent = new Intent(this, DebugActivity.class);
	    	startActivity(mIntent);
			break;
		case R.id.action_setting:
			mIntent = new Intent(this, SettingActivity.class);
	    	startActivity(mIntent);
			break;
		default:
				;
		}

		return super.onMenuItemSelected(featureId, item);
	}
	
	boolean isServiceRunning(String className) {
	    ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
	    List<ActivityManager.RunningServiceInfo> serviceInfos = am.getRunningServices(Integer.MAX_VALUE);
	    int serviceNum = serviceInfos.size();
	    for (int i = 0; i < serviceNum; i++) {
	    	if (serviceInfos.get(i).service.getClassName().equals(className)) {
	    		return true;
	        }
	    }
	    return false;
	}
}
