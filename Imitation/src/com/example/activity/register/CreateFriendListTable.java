package com.example.activity.register;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.beans.DataBaseInstance;
import com.example.beans.Publish;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Looper;

public class CreateFriendListTable extends Thread {
	
	private String createTableSQL;
	private String username;	
	private Bitmap bitmap;
	
	public CreateFriendListTable(String username, Bitmap bitmap) {
		// TODO Auto-generated constructor stub
		this.username = username;		
		this.bitmap = bitmap;
	}
	
	public void run() {
		Looper.prepare();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(DataBaseInstance.fullPath, null, SQLiteDatabase.OPEN_READWRITE);
		createTableSQL = "create table if not exists "
				+ username + Publish.friendList
				+ "(uid integer primary key, username text, signature text, status integer default 0, ip text, headphotoversion integer default 0, signatureversion integer default 0)";
		System.out.println(createTableSQL);
		try {
			db.execSQL(createTableSQL);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String externalStorageDirectory = DataBaseInstance.prePath + username + Publish.friendDirectory;		
		String selfStorageDirectory = DataBaseInstance.prePath + username + Publish.selfDirectory;
		
		boolean externalStorageState = android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (externalStorageState) {
			File dir = new File(externalStorageDirectory);
			File dir1 = new File(selfStorageDirectory);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if (!dir1.exists()) {
				dir1.mkdirs();
			}
			// head photo path
			selfStorageDirectory += Publish.headphotoName+".png";
			System.out.println(selfStorageDirectory);
			File file = new File(selfStorageDirectory);
			try {
				if (!file.exists())
					file.createNewFile();
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
				fileOutputStream.flush();
				fileOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("something wrong");
			// do nothing
		}
		Looper.loop();
	}
}
