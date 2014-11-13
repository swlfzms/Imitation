package com.example.activity.main;

import java.io.File;
import java.util.logging.Logger;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;

import com.example.beans.DataBaseInstance;

public class CreateRootDirectory extends Thread {
	
	public final static String externalStorageDirectory = android.os.Environment.getExternalStorageDirectory()
			+ "/Imitation";
	
	public final Logger DebugLogger = Logger.getLogger("Debug." + CreateRootDirectory.class.getName());
	
	public void run() {
		Looper.prepare();
		addHeadPhotoDirectory();
		Looper.loop();
	}
	
	public boolean directoryIsExist() {
		File dir = new File(CreateRootDirectory.externalStorageDirectory);
		
		if (dir.exists()) {
			return true;
		}
		
		return false;
	}
	
	public void changeTable() {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(DataBaseInstance.path, null, SQLiteDatabase.OPEN_READWRITE);
		String sql = "alter table jasonFriendList add ip varchar(30)";
		db.execSQL(sql);
		sql = "alter table welcomeFriendList add ip varchar(30)";
		db.execSQL(sql);
		sql = "alter table test1FriendList add ip varchar(30)";
		db.execSQL(sql);
		db.close();
	}
	
	public void addHeadPhotoDirectory() {
		String path = android.os.Environment.getExternalStorageDirectory().toString();
		String jason = path + "/Imitation/jason/HeadPhoto";
		String welcome = path + "/Imitation/welcome/HeadPhoto";
		String test1 = path + "/Imitation/test1/FirendListHeadPhoto";
		System.out.println(test1);
		File file = new File(test1);		
		file.mkdirs();
		file = new File(jason);
		file.mkdirs();
		file = new File(welcome);
		file.mkdirs();
	}
}
