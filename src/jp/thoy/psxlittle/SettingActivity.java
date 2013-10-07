package jp.thoy.psxlittle;

import jp.thoy.psxlittle.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnCheckedChangeListener {
	
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
		RadioGroup rExport = (RadioGroup)findViewById(R.id.radioExport);
	
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
		rExport.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO 自動生成されたメソッド・スタブ
		String name;
		int interval;
		int length;

		PSXShared pShared = new PSXShared(getApplicationContext());
		int groupId =  group.getId();
		DataObject mDO = new DataObject(getApplicationContext());
		
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
					default:
						name = PSXValue.INFOTABLE;	
				}
				Toast.makeText(getApplicationContext(),String.valueOf(mDO.countTable(name)),Toast.LENGTH_SHORT).show();
				break;
			case R.id.radioExport:
				switch(checkedId){
					case R.id.radioExInfo:
						name = PSXValue.INFOTABLE;
						break;
					case R.id.radioExPrev:
						name = PSXValue.PREVINFO;
						break;
					default:
						name = PSXValue.INFOTABLE;
				}
				Toast.makeText(getApplicationContext(), "exported items = " + mDO.exportData(name),Toast.LENGTH_SHORT).show();
				break;
		}
		

	}
}
