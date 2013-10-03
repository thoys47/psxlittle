package jp.thoy.psxlittle;

import java.util.ArrayList;

import jp.thoy.psxlittle.R;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class ViewFragment extends Fragment {
	final String CNAME = CommTools.getLastPart(this.getClass().getName(),".");
	final static boolean isDebug = false;
	PackageManager mPackageManager;
	
	int[] layouts;
	int[] ids;

	public void setPackageManager(PackageManager pm){
		mPackageManager = pm;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);

		layouts = new int[MainActivity.TAB_NUM];
		ids = new int[MainActivity.TAB_NUM];
		
		layouts[0] = R.layout.cpuinfo;
		layouts[1] = R.layout.meminfo;
		ids[0] = R.id.listCPU;
		ids[1] = R.id.listMEM;
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		int page = getArguments().getInt(MainActivity.K_PAGE);
		
		View rootView = inflater.inflate(this.layouts[page], container,false);
		ListView mListView = (ListView)rootView.findViewById(ids[page]);
		ArrayList<TempTable> infoList = new ArrayList<TempTable>();
		Context context = rootView.getContext();
		
		try{
			if(page == 0){
				TempTable cList = new TempTable();
				cList.AppName = "";
				cList.SysName = "";
				cList.key = "";
				cList.sum = "";
				cList.max = "";
				cList.avg = "";
				cList.DateTime = "";
				infoList.add(cList);
				ListAdapter adapter = new ListAdapter(context,infoList,mPackageManager);
				mListView.setAdapter(adapter);
			}
			if(page == 1){
				TempTable mList = new TempTable();
				mList.AppName = "";
				mList.SysName = "";
				mList.key = "";
				mList.sum = "";
				mList.max = "";
				mList.avg = "";
				mList.DateTime = "";
				infoList.add(mList);
				ListAdapter adapter = new ListAdapter(context,infoList,mPackageManager);
				mListView.setAdapter(adapter);
			}
		} catch (SQLException ex) {
			TraceLog saveTrace = new TraceLog(context);
			String mname = ":" + Thread.currentThread().getStackTrace()[2].getMethodName();
			saveTrace.saveLog(ex,CNAME + mname);
			Log.e(CNAME,ex.getMessage());
			ex.printStackTrace();
		}
		return rootView;
	}
}
