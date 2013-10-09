package jp.thoy.psxlittle;

import jp.thoy.psxlittle.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ChartActivity extends Activity {

    Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chart_main);
		
		mContext = getApplicationContext();
		
		Intent intent = getIntent();
		String key = intent.getStringExtra(PSXValue.K_KEY);
		String page = intent.getStringExtra(PSXValue.K_PAGE);
		ChartDrawTask chartTask = new ChartDrawTask(mContext,this);
		Param param = new Param();
		param.cParam = getApplicationContext();
		param.acParam = this;
		param.key = key;
		param.page = page;
		param.chartId = R.id.chart_area;
		chartTask.execute(param);

	}
}
