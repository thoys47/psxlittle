package jp.thoy.psxlittle;

import android.content.Intent;
import android.os.BatteryManager;

public class GetBatteryInfo {
	public final static String STATUS_CHARGE = "Charge";
	public final static String STATUS_DISCHARGE = "Discharge";
	public final static String STATUS_FULL = "Full";
	public final static String PLUGGED_AC = "AC";
	public final static String PLUGGED_USB = "USB";
	public final static String PLUGGED_WIRELESS = "WIRELESS";

	public BatteryInfo getInfo(Intent intent){
		BatteryInfo bInfo = new BatteryInfo();
		
		bInfo.level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		bInfo.scale= intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
	
		bInfo.rLevel = (int)((double)bInfo.level * 100.0/(double)bInfo.scale);
		
		int istatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		switch(istatus){
			case BatteryManager.BATTERY_STATUS_CHARGING :
				bInfo.status = STATUS_CHARGE;
				break;
			case BatteryManager.BATTERY_STATUS_DISCHARGING :
				bInfo.status = STATUS_DISCHARGE;
				break;
			case BatteryManager.BATTERY_STATUS_FULL :
				bInfo.status = STATUS_FULL;
				break;
			default :
				bInfo.status = STATUS_DISCHARGE;
		}
		int iplugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		switch(iplugged){
			case BatteryManager.BATTERY_PLUGGED_AC :
				bInfo.plugged = PLUGGED_AC;
				break;
			case BatteryManager.BATTERY_PLUGGED_USB :
				bInfo.plugged = PLUGGED_USB;
				break;
			case BatteryManager.BATTERY_PLUGGED_WIRELESS :
				bInfo.plugged = PLUGGED_WIRELESS;
				break;
			default :
				bInfo.plugged = PLUGGED_AC;
		}
		bInfo.temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
		return bInfo;
	}

}
