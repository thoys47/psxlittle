package jp.thoy.psxlittle;

import jp.thoy.psxlittle.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SettingActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_main);
		Context context = getApplicationContext();
		SharedPreferences mPreference = context.getSharedPreferences(PSXService.PREFILENAME,Activity.MODE_PRIVATE);
		int interval = mPreference.getInt(PSXService.INTERVAL,10);

		RadioGroup rInterval = (RadioGroup)findViewById(R.id.radioInterval);
		RadioGroup rCount = (RadioGroup)findViewById(R.id.radioCount);
	
		switch(interval){
		case 10:
			rInterval.check(R.id.radio10);
			break;
		case 15:
			rInterval.check(R.id.radio15);
			break;
		case 20:
			rInterval.check(R.id.radio20);
			break;
		default:
			;
		}
		rInterval.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO 自動生成されたメソッド・スタブ
				int interval;
				switch(checkedId){
					case R.id.radio10:
						interval = 10;
						break;
					case R.id.radio15:
						interval = 15;
						break;
					case R.id.radio20:
						interval = 20;
						break;
					default:
						interval = 10;	
				}
	            SharedPreferences mPreferences = getSharedPreferences(PSXService.PREFILENAME,MODE_PRIVATE);
	            Editor mEdit = mPreferences.edit();
	            mEdit.putInt(PSXService.INTERVAL,interval);
	            mEdit.commit();
			}
		});

		rCount.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO 自動生成されたメソッド・スタブ
				String name;
				switch(checkedId){
					case R.id.radioInfo:
						name = DataObject.INFOTABLE;
						break;
					case R.id.radioPrev:
						name = DataObject.PREVINFO;
						break;
					default:
						name = DataObject.INFOTABLE;	
				}
				DataObject mDO = new DataObject(getApplicationContext());
				Toast.makeText(getApplicationContext(),String.valueOf(mDO.countTable(name)),Toast.LENGTH_SHORT).show();
			}
		});
	}
}
