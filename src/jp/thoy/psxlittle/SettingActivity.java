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
		PSXShared pShared = new PSXShared(context);
		int interval = pShared.getInterval();
		int length = pShared.getLength();

		RadioGroup rInterval = (RadioGroup)findViewById(R.id.radioInterval);
		RadioGroup rCount = (RadioGroup)findViewById(R.id.radioCount);
		RadioGroup rLength = (RadioGroup)findViewById(R.id.radioLength);
	
		switch(interval){
		case PSXService.MIN10:
			rInterval.check(R.id.radio10);
			break;
		case PSXService.MIN15:
			rInterval.check(R.id.radio15);
			break;
		case PSXService.MIN20:
			rInterval.check(R.id.radio20);
			break;
		default:
			;
		}
		
		switch(length){
		case PSXService.HOUR12:
			rLength.check(R.id.radiolen12);
			break;
		case PSXService.HOUR24:
			rLength.check(R.id.radiolen24);
			break;
		case PSXService.HOUR48:
			rLength.check(R.id.radiolen48);
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
						interval = PSXService.MIN10;
						break;
					case R.id.radio15:
						interval = PSXService.MIN15;
						break;
					case R.id.radio20:
						interval = PSXService.MIN20;
						break;
					default:
						interval = 10;	
				}
				PSXShared pShared = new PSXShared(getApplicationContext());
				pShared.putInterval(interval);
			}
		});

		rInterval.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO 自動生成されたメソッド・スタブ
				int length;
				switch(checkedId){
					case R.id.radiolen12:
						length = PSXService.HOUR12;
						break;
					case R.id.radiolen24:
						length = PSXService.HOUR24;
						break;
					case R.id.radiolen48:
						length = PSXService.HOUR48;
						break;
					default:
						length = 24;	
				}
				PSXShared pShared = new PSXShared(getApplicationContext());
				pShared.putLength(length);
				Toast.makeText(getApplicationContext(), "len=" + length, Toast.LENGTH_SHORT).show();
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
