package com.example.activity.register;

import java.io.File;

import com.example.activity.main.CreateRootDirectory;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;

public class CreateFriendListTable extends Thread {
	
	private String createTableSQL;
	private String username;
	private String path;
		
	public CreateFriendListTable(String username, String path) {
		// TODO Auto-generated constructor stub
		this.username = username;
		this.path = path;
	}
	
	public void run() {
		Looper.prepare();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
		createTableSQL = "create table if not exists " + username
				+ "FriendList(uid integer primary key, username text, signature text, status integer)";		
		System.out.println(createTableSQL);
		try {
			db.execSQL(createTableSQL);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String externalStorageDirectory = android.os.Environment.getExternalStorageDirectory() + "/Imitation/"
				+ username + "/FriendListHeadPhoto";
		boolean externalStorageState = android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (externalStorageState) {
			File dir = new File(externalStorageDirectory);
			if (!dir.exists()) {
				dir.mkdir();
			}
		} else {
			System.out.println("something wrong");
			// do nothing
		}
		Looper.loop();
	}
}
