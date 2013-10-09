package jp.thoy.psxlittle;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

	String mLimit;
	PackageManager mPackageManager;
	Context mContext;
	
	public PagerAdapter(FragmentManager fm) {
		super(fm);
		//Log.d(TAG,"PagerAdapter");
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	public void setPackageManager(PackageManager pm){
		mPackageManager = pm;
	}

	public void setContext(Context context){
		mContext = context;
	}
	
	@Override
	public Fragment getItem(int position) {
		// getItem is called to instantiate the fragment for the given page.
		// Return a DummySectionFragment (defined as a static inner class
		// below) with the page number as its lone argument.
		//Log.d(TAG,"getItem");
		ViewFragment fragment = new ViewFragment();
		fragment.setPackageManager(mPackageManager);
		fragment.setContext(mContext);
		Bundle args = new Bundle();
		args.putInt(PSXValue.K_PAGE, position);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public int getCount() {
		// Show 3 total pages.
		return PSXValue.TAB_NUM;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case PSXValue.P_CPU:
			return mContext.getResources().getString(R.string.ttlCPU);
		case PSXValue.P_MEM:
			return mContext.getResources().getString(R.string.ttlMEM);
		case PSXValue.P_BATT:
			return mContext.getResources().getString(R.string.ttlBATT);
		}
		return null;
	}

}
