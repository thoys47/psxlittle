package jp.thoy.psxlittle;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SettingActivity extends Activity implements OnCheckedChangeListener {
	final String TAG = CommTools.getLastPart(this.getClass().getName(),".");
	final static boolean isDebug = false;

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
		RadioGroup rLength = (RadioGroup)findViewById(R.id.radioLength);
		RadioGroup rCount = (RadioGroup)findViewById(R.id.radioCount);
		
		switch(interval){
		case PSXValue.MIN10:
			rInterval.check(R.id.radio10);
			break;
		case PSXValue.MIN15:
			rInterval.check(R.id.radio15);
			break;
		case PSXValue.MIN20:
			rInterval.check(R.id.radio20);
			break;
		default:
			;
		}
		
		switch(length){
		case PSXValue.HOUR12:
			rLength.check(R.id.radiolen12);
			break;
		case PSXValue.HOUR24:
			rLength.check(R.id.radiolen24);
			break;
		case PSXValue.HOUR48:
			rLength.check(R.id.radiolen48);
			break;
		default:
			;
				
		}
		
		rInterval.setOnCheckedChangeListener(this);
		rLength.setOnCheckedChangeListener(this);
		rCount.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO 自動生成されたメソッド・スタブ
		int interval;
		int length;
		Context context = getApplicationContext();

		PSXShared pShared = new PSXShared(context);
		int groupId =  group.getId();
		String name;
		DataObject dObject = new DataObject(context);
		
		if(isDebug) Log.w(TAG,"id=" + checkedId);
		
		switch(groupId){
			case R.id.radioInterval:
				switch(checkedId){
					case R.id.radio10:
						interval = PSXValue.MIN10;
						break;
					case R.id.radio15:
						interval = PSXValue.MIN15;
						break;
					case R.id.radio20:
						interval = PSXValue.MIN20;
						break;
					default:
						interval = 10;	
				}
				pShared.putInterval(interval);
				break;
			case R.id.radioLength:
				switch(checkedId){
					case R.id.radiolen12:
						length = PSXValue.HOUR12;
						break;
					case R.id.radiolen24:
						length = PSXValue.HOUR24;
						break;
					case R.id.radiolen48:
						length = PSXValue.HOUR48;
						break;
					default:
						length = 24;	
				}
				pShared.putLength(length);
				break;
			case R.id.radioCount:
				switch(checkedId){
					case R.id.radioInfo:
						name = PSXValue.INFOTABLE;
						break;
					case R.id.radioPrev:
						name = PSXValue.PREVINFO;
						break;
					case R.id.radioBatt:
						name = PSXValue.BATTINFO;
						break;
					default:
						name = PSXValue.INFOTABLE;	
				}
				Toast.makeText(context,String.valueOf(dObject.countTable(name)),Toast.LENGTH_SHORT).show();
				break;
		}
	}
}
