package jp.thoy.psxlittle;

import android.content.Intent;
import android.os.BatteryManager;

public class GetBatteryInfo {

	public BatteryInfo getInfo(Intent intent){
		BatteryInfo bInfo = new BatteryInfo();
		
		bInfo.level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		bInfo.scale= intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
	
		bInfo.rLevel = (int)((double)bInfo.level * 100.0/(double)bInfo.scale);
		
		int istatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		switch(istatus){
			case BatteryManager.BATTERY_STATUS_CHARGING :
				bInfo.status = PSXValue.STATUS_CHARGE;
				break;
			case BatteryManager.BATTERY_STATUS_DISCHARGING :
				bInfo.status = PSXValue.STATUS_DISCHARGE;
				break;
			case BatteryManager.BATTERY_STATUS_FULL :
				bInfo.status = PSXValue.STATUS_FULL;
				break;
			default :
				bInfo.status = PSXValue.STATUS_DISCHARGE;
		}
		int iplugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		switch(iplugged){
			case BatteryManager.BATTERY_PLUGGED_AC :
				bInfo.plugged = PSXValue.PLUGGED_AC;
				break;
			case BatteryManager.BATTERY_PLUGGED_USB :
				bInfo.plugged = PSXValue.PLUGGED_USB;
				break;
			case BatteryManager.BATTERY_PLUGGED_WIRELESS :
				bInfo.plugged = PSXValue.PLUGGED_WIRELESS;
				break;
			default :
				bInfo.plugged = PSXValue.PLUGGED_NONE;
		}
		bInfo.temp = (intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) + 5) / 10;
		return bInfo;
	}

}
