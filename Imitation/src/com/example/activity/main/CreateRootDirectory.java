package com.example.activity.main;

import java.io.File;
import java.util.logging.Logger;

import com.example.beans.DataBaseInstance;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;

public class CreateRootDirectory extends Thread {
	
	public final static String externalStorageDirectory = android.os.Environment.getExternalStorageDirectory()
			+ "/Imitation";	
	
	public final Logger DebugLogger = Logger.getLogger("Debug." + CreateRootDirectory.class.getName());
	
	public void run() {
		Looper.prepare();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(DataBaseInstance.path, null, SQLiteDatabase.OPEN_READWRITE);		
		String sql = "alter table jasonFriendList add ip varchar(30)";
		db.execSQL(sql);
		sql = "alter table welcomeFriendList add ip varchar(30)";
		db.execSQL(sql);
		sql = "alter table test1FriendList add ip varchar(30)";
		db.execSQL(sql);
		db.close();
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
