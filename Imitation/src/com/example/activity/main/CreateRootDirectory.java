package com.example.activity.main;

import java.io.File;
import java.util.logging.Logger;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;

import com.example.beans.DataBaseInstance;

public class CreateRootDirectory extends Thread {
	
	public final static String externalStorageDirectory = android.os.Environment.getExternalStorageDirectory()
			+ "/Imitation";
	private SQLiteDatabase db;
	public final Logger DebugLogger = Logger.getLogger("Debug." + CreateRootDirectory.class.getName());
	
	public void run() {
		Looper.prepare();
		createDirectory();
		databaseInit();
		Looper.loop();
	}
	private void databaseInit() {
		// TODO Auto-generated method stub
		System.out.println("create database");	
		
		db = SQLiteDatabase.openOrCreateDatabase(DataBaseInstance.path, null);				
		System.out.println("database have been created");
		db.close();
	}
	public boolean directoryIsExist() {
		File dir = new File(CreateRootDirectory.externalStorageDirectory);
		
		if (dir.exists()) {
			return true;
		}
		
		return false;
	}
	public void createDirectory(){
		File dir = new File(CreateRootDirectory.externalStorageDirectory);
		if(!dir.exists())
			dir.mkdirs();
		dir = new File(DataBaseInstance.prePath);
		if(!dir.exists())
			dir.mkdirs();
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
	
	public void changeTableAddVersion(){
		//headphotoversion
		SQLiteDatabase db = SQLiteDatabase.openDatabase(DataBaseInstance.path, null, SQLiteDatabase.OPEN_READWRITE);
		String sql = "alter table jasonFriendList add headphotoversion integer default 0";
		db.execSQL(sql);
		sql = "alter table welcomeFriendList add headphotoversion integer default 0";
		db.execSQL(sql);
		sql = "alter table test1FriendList add headphotoversion integer default 0";
		db.execSQL(sql);
		sql = "alter table jasonFriendList add signatureversion integer default 0";
		db.execSQL(sql);
		sql = "alter table welcomeFriendList add signatureversion integer default 0";
		db.execSQL(sql);
		sql = "alter table test1FriendList add signatureversion integer default 0";
		db.execSQL(sql);
		db.close();
	}
	
	public void addHeadPhotoDirectory() {
		String path = android.os.Environment.getExternalStorageDirectory().toString();
		String jason = path + "/Imitation/jason/HeadPhoto";
		String welcome = path + "/Imitation/welcome/HeadPhoto";
		String test1 = path + "/Imitation/test1/FriendListHeadPhoto";
		System.out.println(test1);
		File file = new File(test1);		
		file.mkdirs();
		file = new File(jason);
		file.mkdirs();
		file = new File(welcome);
		file.mkdirs();
	}
	
	public void updateTest1Friend(){
		SQLiteDatabase db = SQLiteDatabase.openDatabase(DataBaseInstance.path, null, SQLiteDatabase.OPEN_READWRITE);
		String sql = "update test1FriendList set uid=0 where username='jason';";
		db.execSQL(sql);		
		db.close();
	}
}
