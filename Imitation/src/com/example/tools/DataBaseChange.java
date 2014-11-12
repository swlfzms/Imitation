package com.example.tools;

import java.io.File;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;

public class DataBaseChange extends Thread{
	
	private String path = "/data/data/com.example.imitation/files/imitation.db";
	
	public void run(){
		Looper.prepare();
		
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path, null);
		String createTableSQL = "create table if not exists jasonFriendList(uid integer primary key, username text, signature text, status integer)";
		db.execSQL(createTableSQL);
		createTableSQL = "create table if not exists welcomeFriendList(uid integer primary key, username text, signature text, status integer)";
		db.execSQL(createTableSQL);
		createTableSQL = "insert into jasonFriendList(uid,username,signature,status) values(1,'welcome','hello world',0)";
		db.execSQL(createTableSQL);
		createTableSQL = "insert into welcomeFriendList(uid,username,signature,status) values(2,'jason','hello world',0)";
		db.execSQL(createTableSQL);
		
		db.close();
		Looper.loop();
	}
}
