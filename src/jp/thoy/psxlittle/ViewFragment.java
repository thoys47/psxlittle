package jp.thoy.psxlittle;

import java.util.ArrayList;

import jp.thoy.psxlittle.R;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class ViewFragment extends Fragment {

	PackageManager mPackageManager;
	Context mContext;
	
	int[] layouts;
	int[] ids;

	public void setPackageManager(PackageManager pm){
		mPackageManager = pm;
	}

	public void setContext(Context context){
		mContext = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);

		layouts = new int[PSXValue.TAB_NUM];
		ids = new int[PSXValue.TAB_NUM];
		
		layouts[PSXValue.P_CPU] = R.layout.cpuinfo;
		layouts[PSXValue.P_MEM] = R.layout.meminfo;
		layouts[PSXValue.P_BATT] = R.layout.batt_main;
		ids[PSXValue.P_CPU] = R.id.listCPU;
		ids[PSXValue.P_MEM] = R.id.listMEM;
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		int page = getArguments().getInt(PSXValue.K_PAGE);
		
		View rootView = inflater.inflate(this.layouts[page], container,false);
		ListView mListView = (ListView)rootView.findViewById(ids[page]);
		ArrayList<TempTable> infoList = new ArrayList<TempTable>();
		Context context = rootView.getContext();
		
		if(page == PSXValue.P_CPU){
			TempTable cList = new TempTable();
			cList.AppName = "";
			cList.SysName = "";
			cList.key = "";
			cList.sum = 0.0;
			cList.max = 0.0;
			cList.avg = 0.0;
			cList.DateTime = "";
			infoList.add(cList);
			ListAdapter adapter = new ListAdapter(context,infoList,mPackageManager);
			mListView.setAdapter(adapter);
		}
		if(page == PSXValue.P_MEM){
			TempTable mList = new TempTable();
			mList.AppName = "";
			mList.SysName = "";
			mList.key = "";
			mList.sum = 0.0;
			mList.max = 0.0;
			mList.avg = 0.0;
			mList.DateTime = "";
			infoList.add(mList);
			ListAdapter adapter = new ListAdapter(context,infoList,mPackageManager);
			mListView.setAdapter(adapter);
		}
		return rootView;
	}
}
