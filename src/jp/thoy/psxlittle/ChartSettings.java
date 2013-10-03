package jp.thoy.psxlittle;

import java.util.Date;
import java.util.List;

import org.achartengine.chart.PointStyle;

public class ChartSettings {
	String chartName;
	String xString;
	String yString;
	int max;
	int min;
	
	String[] titles;
	List<Date[]> x;
	//List<String[]> x;
	List<double[]> values;
    int[] colors;
    PointStyle[] styles;
    float[] lineWidth;
    
}
