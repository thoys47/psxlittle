package jp.thoy.psxlittle;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

	final String CNAME = CommTools.getLastPart(this.getClass().getName(),".");
	final static boolean isDebug = false;
	
	String mLimit;
	public PackageManager mPackageManager;
	
	public PagerAdapter(FragmentManager fm) {
		super(fm);
		//Log.d(TAG,"PagerAdapter");
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	public void setPackageManager(PackageManager pm){
		mPackageManager = pm;
	}
	@Override
	public Fragment getItem(int position) {
		// getItem is called to instantiate the fragment for the given page.
		// Return a DummySectionFragment (defined as a static inner class
		// below) with the page number as its lone argument.
		//Log.d(TAG,"getItem");
		ViewFragment mFragment = new ViewFragment();
		mFragment.setPackageManager(mPackageManager);
		Bundle args = new Bundle();
		args.putInt(MainActivity.K_PAGE, position);
		mFragment.setArguments(args);

		return mFragment;
	}

	@Override
	public int getCount() {
		// Show 3 total pages.
		return MainActivity.TAB_NUM;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case 0:
			return "CPU info";
		case 1:
			return "Memory Info";
		}
		return null;
	}

}
