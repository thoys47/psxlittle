package jp.thoy.psxlittle;

import java.util.ArrayList;

import jp.thoy.psxlittle.R;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<TempTable> {
	final String CNAME = CommTools.getLastPart(this.getClass().getName(),".");
	final static boolean isDebug = false;
	private LayoutInflater mInflater;
	private PackageManager mPackageManager;
	
	public ListAdapter(Context context, ArrayList<TempTable> objects,PackageManager pm) {
		super(context, 0, objects);
		// TODO 自動生成されたコンストラクター・スタブ
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPackageManager = pm;
	}
	@Override
	public void notifyDataSetChanged() {
		// TODO 自動生成されたメソッド・スタブ
		super.notifyDataSetChanged();
	}

	public View getView(final int position,View convertView,ViewGroup parent){
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.rows,null);
		}
		
		final TempTable item = this.getItem(position);
		if(item != null){
			ImageView mIcon = (ImageView)convertView.findViewById(R.id.imgIcon);
			TextView mAppName = (TextView)convertView.findViewById(R.id.textAppName);
			TextView mSysName = (TextView)convertView.findViewById(R.id.textSysName);
			TextView mKey = (TextView)convertView.findViewById(R.id.textKey);
			TextView mSum = (TextView)convertView.findViewById(R.id.textCPUUse);
			Resources resource = parent.getResources();
			if(!item.key.equals("")){
				if(!item.key.equals("system") && !item.key.equals("root")){
					try {
						ApplicationInfo appInfo = mPackageManager.getApplicationInfo(item.SysName, PackageManager.GET_ACTIVITIES);
						Drawable icon = mPackageManager.getApplicationIcon(appInfo);
						if(icon != null){
							BitmapDrawable bmIcon = (BitmapDrawable)icon;
							Bitmap bIcon = bmIcon.getBitmap();
							BitmapDrawable baseIcon = (BitmapDrawable)resource.getDrawable(R.drawable.ic_android);
							Bitmap oIcon = baseIcon.getBitmap();
							
							if(bIcon.getWidth() != oIcon.getWidth() || bIcon.getHeight() != oIcon.getHeight()) {
								Bitmap nIcon = Bitmap.createScaledBitmap(bIcon, oIcon.getWidth(), oIcon.getWidth(), false);
								icon = new BitmapDrawable(resource,nIcon);
							}
							
							mIcon.setImageDrawable(icon);
							if(item.key.equals("com.android.systemui")){
								mIcon.setImageDrawable(resource.getDrawable(R.drawable.ic_android));
							}
	
						} else {
							mIcon.setImageDrawable(resource.getDrawable(R.drawable.ic_android));
						}
						mAppName.setText(mPackageManager.getApplicationLabel(appInfo));
						mSysName.setText(item.SysName);
						mKey.setText(item.key);
					} catch (NameNotFoundException ex) {
						// TODO 自動生成された catch ブロック
						mIcon.setImageDrawable(resource.getDrawable(R.drawable.ic_android));
						mAppName.setText(item.AppName);
						mSysName.setText(item.SysName);
						mKey.setText(item.key);
					}
				} else {
					mIcon.setImageDrawable(resource.getDrawable(R.drawable.ic_android));
					mAppName.setText(item.AppName);
					mSysName.setText(item.SysName);
					mKey.setText(item.key);
				}
				mSum.setText(item.sum + "%");
			} else {
				mIcon.setImageDrawable(null);
			}
			
		}
		return convertView;
	}

	
}
