package jp.thoy.psxlittle;

import java.util.ArrayList;

import jp.thoy.psxlittle.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class DetailActivity extends Activity {
	final String TAG = CommTools.getLastPart(this.getClass().getName(),".");
	final static boolean isDebug = PSXValue.isDebug;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_main);

		Intent intent = getIntent();
		String key = intent.getStringExtra("KEY");
		int pos = Integer.parseInt(intent.getStringExtra("PAGE"));
		
		PackageManager pManager = getPackageManager();
		SummarizeData iCalc = new SummarizeData(getApplicationContext());
		ArrayList<TempTable> list = null;
		ListAdapter adapter;
		ListView mListView;
		mListView = (ListView)findViewById(R.id.listDetail);

		switch(pos){
		case 0:
			list = iCalc.calculate(key,0);
			adapter = new ListAdapter(this,list,pManager);
			break;
		case 1:
			list = iCalc.calculate(key,1);
			adapter = new ListAdapter(this,list,pManager);
			break;
		default :
			mListView = null;
			adapter = null;
		}
		if(isDebug){
			Log.w(TAG,"cnt=" + list.size());
		}
		mListView.setAdapter(adapter);

	}

}
