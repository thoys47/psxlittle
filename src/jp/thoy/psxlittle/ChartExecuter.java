/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.thoy.psxlittle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;

public class ChartExecuter extends AbChart {

	private ChartSettings mChartSettings;
	private final String TAG = CommTools.getLastPart(this.getClass().getName(),".");
	private final static boolean isDebug = false;


	public ChartExecuter(ChartSettings chartList){
		mChartSettings = chartList;
	}

	public GraphicalView execute(Context context) {
		List<Date[]> x = new ArrayList<Date[]>();	
		//List<String[]> x = new ArrayList<String[]>();	
		List<double[]> values = new ArrayList<double[]>();
		
		for(int i = 0;i < mChartSettings.titles.length;i++){
			Date[] xtmp = new Date[mChartSettings.x.get(i).length];
			//String[] xtmp = new String[mChartSettings.x.get(i).length];
			xtmp = mChartSettings.x.get(i);
			x.add(xtmp);
			double[] vtmp = new double[mChartSettings.values.get(i).length];
			vtmp = mChartSettings.values.get(i);
			values.add(vtmp);
		}
		
		if(isDebug) Log.w(TAG,"x cnt=" + x.size());
		
		XYMultipleSeriesRenderer renderer = buildRenderer(mChartSettings.colors, mChartSettings.styles, mChartSettings.lineWidth);
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
		  ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
		}
		setChartSettings(renderer, mChartSettings.chartName, mChartSettings.xString, mChartSettings.yString, 
				x.get(0)[0].getTime(), x.get(0)[x.get(0).length - 1].getTime(), 
				//-24.0,0.0,
				mChartSettings.min, mChartSettings.max, Color.LTGRAY, Color.LTGRAY);

		renderer.setXLabels(10);
		renderer.setYLabels(10);
	    renderer.setAxisTitleTextSize(30);
	    renderer.setChartTitleTextSize(37);
	    renderer.setLabelsTextSize(25);
	    renderer.setLegendTextSize(25);
	    renderer.setShowGrid(true);
	    
		int[] margins = new int[]{0,70,50,0};

		renderer.setMargins(margins);
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.RIGHT);
		GraphicalView gView = ChartFactory.getTimeChartView(context, 
				buildDateDataset(mChartSettings.titles, x, values), renderer,"HH:mm");
		//GraphicalView gView = ChartFactory.getLineChartView(context,
		//		buildDataset(mChartSettings.titles, values), renderer);
		return gView;
	}

}
