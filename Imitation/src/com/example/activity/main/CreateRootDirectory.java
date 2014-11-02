package com.example.activity.main;

import java.io.File;
import java.util.logging.Logger;

import android.os.Looper;

public class CreateRootDirectory extends Thread {
	
	public final static String externalStorageDirectory = android.os.Environment.getExternalStorageDirectory()
			+ "/Imitation";	
	
	public final Logger DebugLogger = Logger.getLogger("Debug." + CreateRootDirectory.class.getName());
	
	public void run() {
		Looper.prepare();
				
		boolean externalStorageState = android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);		
		
		if(externalStorageState){
			File dir = new File(CreateRootDirectory.externalStorageDirectory);
			
			if(!dir.exists()){
				dir.mkdir();
			}
				
		}else{
			// do nothing
		}		
		Looper.loop();
	}
	
	public boolean directoryIsExist(){
		File dir = new File(CreateRootDirectory.externalStorageDirectory);
		
		if(dir.exists()){
			return true; 
		}
		
		return false;
	}
}
