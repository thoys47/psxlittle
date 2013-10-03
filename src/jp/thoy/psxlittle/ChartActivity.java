package jp.thoy.psxlittle;

import jp.thoy.psxlittle.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ChartActivity extends Activity {
	final String CNAME = CommTools.getLastPart(this.getClass().getName(),".");
	final static boolean isDebug = false;
    Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chart_main);
		
		mContext = getApplicationContext();
		
		Intent intent = getIntent();
		String key = intent.getStringExtra(MainActivity.K_KEY);
		String page = intent.getStringExtra(MainActivity.K_PAGE);
		if(isDebug) Log.w(CNAME,"key=" + key);
		ChartDrawTask chartTask = new ChartDrawTask(mContext,this);
		Param param = new Param();
		param.cParam = getApplicationContext();
		param.acParam = this;
		param.key = key;
		param.page = page;
		chartTask.execute(param);

	}
}
