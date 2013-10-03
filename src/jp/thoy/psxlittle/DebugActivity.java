package jp.thoy.psxlittle;

import java.io.IOException;
import java.util.ArrayList;

import jp.thoy.psxlittle.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;

public class DebugActivity extends Activity {
	final String CNAME = CommTools.getLastPart(this.getClass().getName(),".");
	final static boolean isDebug = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO 自動生成されたメソッド・スタブ
		Context context = getApplicationContext();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug_main);
		RadioGroup radioDebug = (RadioGroup)findViewById(R.id.radioDebug);
		int radio = R.id.debugTrace;
		TraceLog readTrace = new TraceLog(getApplicationContext());
		try{
			ArrayList<String> tLog = readTrace.readFile(radio);
			ListView listDebug = (ListView)findViewById(R.id.listDebug);
			if(tLog != null){
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.row_debug,R.id.textDebug, tLog);
				listDebug.setAdapter(adapter);
			} else {
				tLog = new ArrayList<String>();
				tLog.add(getString(R.string.strnone2));
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.row_debug,R.id.textDebug, tLog);
				listDebug.setAdapter(adapter);
			}
		} catch (IOException ex){
			ex.printStackTrace();
		}
		
		radioDebug.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO 自動生成されたメソッド・スタブ
				int radio = checkedId;
				Context context = getApplicationContext();
				if(isDebug) Log.w(CNAME,"radio=" + radio);
				switch(radio){
					case R.id.debugTrace:
					case R.id.debugLog:
						TraceLog readTrace = new TraceLog(getApplicationContext());
						try{
							ArrayList<String> tLog = readTrace.readFile(radio);
							ListView listDebug = (ListView)findViewById(R.id.listDebug);
							if(tLog != null){
								ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.row_debug,R.id.textDebug, tLog);
								listDebug.setAdapter(adapter);
							} else {
								tLog = new ArrayList<String>();
								tLog.add(getString(R.string.strnone2));
								ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.row_debug,R.id.textDebug, tLog);
								listDebug.setAdapter(adapter);
							}
						} catch (IOException ex){
							ex.printStackTrace();
						}
						break;
				}
			}
		});


	}

	@Override
	protected void onDestroy() {
		// TODO 自動生成されたメソッド・スタブ
		super.onDestroy();
	}

}
