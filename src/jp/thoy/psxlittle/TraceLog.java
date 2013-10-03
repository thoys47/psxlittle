package jp.thoy.psxlittle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Calendar;

import jp.thoy.psxlittle.R;
import android.content.Context;

public class TraceLog implements UncaughtExceptionHandler {
	
	final Context mContext;
	final String CNAME = CommTools.getLastPart(this.getClass().getName(),".");
	final static boolean isDebug = false;

	static final String TRACEFILE =  "psxlittle.trc";
	static final String DEBUGFILE =  "psxlittle.debug";

	final Calendar calendar = Calendar.getInstance();

	public TraceLog(Context context){
		mContext = context;
	}
	
	public void saveLog(Exception ex,String msg) {
		File file = null;
		file = new File(mContext.getExternalFilesDir(null), TRACEFILE);
		
		try {
		
			file.createNewFile();
	
	        if (file != null && file.exists()) {
	        	FileWriter fw = new FileWriter(file, false);
	        	PrintWriter pw = new PrintWriter(fw,true);
	    		pw.println(CommTools.CalendarToString(calendar,CommTools.DATETIMELONG));
	        	pw.println(msg);
	        	ex.printStackTrace(pw);
	        	pw.close();
	        } 
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void saveDebug(String msg){

		File file = null;
		file = new File(mContext.getExternalFilesDir(null), DEBUGFILE);
		
		try{
			file.createNewFile();
	
	        if (file != null && file.exists()) {
	        	FileReader fr = new FileReader(file);
	        	BufferedReader br = new BufferedReader(fr);
	    		ArrayList<String> debug = new ArrayList<String>();
	        	String ln;
	        	int line = 0;
	        	while((ln = br.readLine()) != null){
	        		debug.add(ln);
	        		line++;
	        	}
	        	br.close();
	        	fr.close();
	        	
	        	FileWriter fw = new FileWriter(file, false);
	        	PrintWriter pw = new PrintWriter(fw,true);
	        	
	        	int i = 0;
	        	if(line > 99){
	        		i = line - 99;
	        	}
	        	for(;i < line;i ++){
	        		pw.println(debug.get(i));
	        	}
	        	
	    		pw.print(CommTools.CalendarToString(calendar,CommTools.DATETIMESHORT) + ":");
	        	pw.println(msg);
	        	pw.close();
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public ArrayList<String> readFile(int radio) throws IOException {
		
		ArrayList<String> ret = new ArrayList<String>();
		ArrayList<String> ar = new ArrayList<String>();
		File file = null;

    	switch(radio){
			case R.id.debugTrace:
				file = new File(mContext.getExternalFilesDir(null), TRACEFILE);
				break;
			case R.id.debugLog:
				file = new File(mContext.getExternalFilesDir(null), DEBUGFILE);
				break;
		}
        if (file.exists()) {
        	FileReader fr = new FileReader(file);
        	BufferedReader br = new BufferedReader(fr);
        	String ln;
        	int line = 0;
        	while((ln = br.readLine()) != null){
        		ar.add(ln);
        		line++;
        	}
        	br.close();
        	fr.close();
        	
        	switch(radio){
    			case R.id.debugTrace:
    				for(int i = 0;i < line;i++){
    					ret.add(ar.get(i));
    				}
    				break;
    			case R.id.debugLog:
    				for(int i = line - 1;i >= 0;i--){
    					ret.add(ar.get(i));
    				}
    				break;
    		}
        	return(ret);
        }
        return null;
	}

	public String getCName(String cname){
		return cname.substring(cname.lastIndexOf(".") + 1, cname.length());
	}

	public String getAction(String action){
		return action.substring(action.lastIndexOf(".") + 1, action.length());
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO 自動生成されたメソッド・スタブ
		saveLog((Exception)ex,thread.getName());
		
	}
}
