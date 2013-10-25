package jp.thoy.psxlittle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import jp.thoy.psxlittle.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnItemClickListener, OnItemLongClickListener {

	private final String CNAME = CommTools.getLastPart(this.getClass().getName(),".");
	private final static boolean isDebug = PSXValue.isDebug;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Context context = getApplicationContext();

		Thread.setDefaultUncaughtExceptionHandler(new TraceLog(context));
		
		try{
			
			PackageManager packageManager = getPackageManager();
			PagerAdapter pagerAdapter;
			ViewPager viewPager;

			DataObject mDataObject = new DataObject(context);
			SQLiteDatabase mdb = mDataObject.dbOpen();
			mDataObject.dbClose(mdb);
			pagerAdapter = new PagerAdapter(getSupportFragmentManager());
			pagerAdapter.setPackageManager(packageManager);
			pagerAdapter.setContext(context);
			viewPager = (ViewPager)findViewById(R.id.viewPager);
			viewPager.setAdapter(pagerAdapter);
			
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
		DataObject dObject = new DataObject(context);
		PSXShared pShared = new PSXShared(context);
		
		long before = pShared.getLastExec();
		if(before == 0L){
			if(isDebug){
				Log.w(CNAME,"install from main count" + dObject.countTable(PSXValue.PREVINFO));
			}
			PSXAsyncTask aTask = new PSXAsyncTask();
			Param  mParam = new Param();
			mParam.cParam = context;
			mParam.sParam = PSXValue.INSTALL;
			aTask.execute(mParam);

			pShared.putLastExec(Calendar.getInstance());
		}
		
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(Intent.ACTION_DATE_CHANGED);
		iFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
		iFilter.addAction(Intent.ACTION_TIME_CHANGED);
		iFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		//iFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

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
		
		int length = pShared.getLength();
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.add(Calendar.HOUR_OF_DAY, (-1) * length);
		SQLiteDatabase db = dObject.dbOpen();
		String sql = "delete from " + PSXValue.INFOTABLE
				+ " where datetime < '" + CommTools.CalendarToString(calendar,CommTools.DATETIMELONG) + "'";
		dObject.doSQL(db,sql);
		calendar.add(Calendar.HOUR_OF_DAY, (-1) * length);
		sql = "delete from " + PSXValue.BATTINFO
				+ " where datetime < '" + CommTools.CalendarToString(calendar,CommTools.DATETIMELONG) + "'";
		dObject.doSQL(db,sql);
		if(isDebug){
			Log.w(CNAME,sql);
		}

		dObject.dbClose(db);
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
		Context context = getApplicationContext();
		ArrayList<TempTable> list;
		ListAdapter adapter = null;
		ListView mListView = null;
		int ids[] = new int[]{R.id.listCPU,R.id.listMEM};
		DataObject mDO = new DataObject(context);
		
		switch(item.getItemId()){
			case R.id.action_reload:
				ViewPager vPager = (ViewPager)findViewById(R.id.viewPager);
				PackageManager pManager = getPackageManager();
				switch(vPager.getCurrentItem()){
					case PSXValue.P_CPU:
					case PSXValue.P_MEM:
						int cnt = mDO.countTable(PSXValue.INFOTABLE);
						if(cnt == 0){
							Toast.makeText(context, getString(R.string.strNoData), Toast.LENGTH_SHORT).show();
							return super.onMenuItemSelected(featureId, item);
						}
						SummarizeData iCalc = new SummarizeData(context);
						list = iCalc.calculate(null,vPager.getCurrentItem());
						mListView = (ListView)findViewById(ids[vPager.getCurrentItem()]);
						adapter = new ListAdapter(this,list,pManager);
						list = iCalc.calculate(null,vPager.getCurrentItem());
						if(list == null){
							return super.onMenuItemSelected(featureId, item);
						}
						break;
					case PSXValue.P_BATT:
						IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
						Intent intent = context.registerReceiver(null, ifilter);
						GetBatteryInfo batteryInfo = new GetBatteryInfo();
						BatteryInfo bInfo = batteryInfo.getInfo(intent);
						
						TextView tLebel = (TextView)findViewById(R.id.txtBattery);
						tLebel.setText(String.valueOf(bInfo.rLevel) + " %");
						TextView tTemp = (TextView)findViewById(R.id.txtTemp);
						tTemp.setText(String.valueOf(bInfo.temp) + " ");
						TextView tPlugged = (TextView)findViewById(R.id.txtPlugged);
						tPlugged.setText(bInfo.plugged + " ");
						TextView tCharge = (TextView)findViewById(R.id.txtCharge);
						tCharge.setText(bInfo.status + " ");

						cnt = mDO.countTable(PSXValue.BATTINFO);
						if(cnt == 0){
							Toast.makeText(context, getString(R.string.strNoData), Toast.LENGTH_SHORT).show();
							return super.onMenuItemSelected(featureId, item);
						}
						
						ChartDrawTask chartTask = new ChartDrawTask(context,this);
						Param param = new Param();
						param.cParam = getApplicationContext();
						param.acParam = this;
						param.key = PSXValue.NAME_BATT;
						param.page = String.valueOf(vPager.getCurrentItem());
						param.chartId = R.id.battery_area;
						chartTask.execute(param);
						break;
					default :
						mListView = null;
						adapter = null;
				}
				if(adapter != null && mListView != null){
					mListView.setAdapter(adapter);
					mListView.setOnItemClickListener(this);
					mListView.setOnItemLongClickListener(this);
				}
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


	@Override
	public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
		// TODO 自動生成されたメソッド・スタブ
		TextView tView = (TextView)view.findViewById(R.id.textKey);
		ViewPager vPager = (ViewPager)findViewById(R.id.viewPager);
		Intent intent;
		if(isDebug) Log.w(CNAME,"getText=" + tView.getText());
		//if(!tView.getText().equals("root") && !tView.getText().equals("system")){ 
			intent = new Intent(view.getContext(),ChartActivity.class);
			intent.putExtra("PAGE",String.valueOf(vPager.getCurrentItem()));
			intent.putExtra("KEY",tView.getText());
			startActivity(intent);
		//} else {
		//	mIntent = new Intent(view.getContext(),DetailActivity.class);
		//	mIntent.putExtra("PAGE",String.valueOf(vPager.getCurrentItem()));
		//	mIntent.putExtra("KEY",tView.getText());
		//	startActivity(mIntent);
		//}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
		// TODO 自動生成されたメソッド・スタブ
		PackageManager pManager = getPackageManager();
		TextView tView = (TextView)view.findViewById(R.id.textSysName);
		Intent intent = pManager.getLaunchIntentForPackage((tView.getText()).toString());
		if(!tView.getText().equals("root") && !tView.getText().equals("system")){ 
			try{
				startActivity(intent);
			} catch (Exception ex) {
				Toast.makeText(view.getContext(), getString(R.string.strDontExec), Toast.LENGTH_SHORT).show();
			}
		} else {
			ViewPager vPager = (ViewPager)findViewById(R.id.viewPager);
			intent = new Intent(view.getContext(),DetailActivity.class);
			intent.putExtra("PAGE",String.valueOf(vPager.getCurrentItem()));
			intent.putExtra("KEY",tView.getText());
			startActivity(intent);
		}
		return false;
	}
}
