package com.example.activity.main;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;

import com.example.activity.login.LoginActivity;
import com.example.activity.welcomeview.WelcomeActivity;
import com.example.beans.DataBaseInstance;
import com.example.beans.LoginUser;
import com.example.beans.Screen;
import com.example.imitation.R;
import com.example.tools.DataBaseChange;

public class MainActivity extends Activity {
	
	SQLiteDatabase db;
	String database_name;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);				
		DataBaseInstance.path = getResources().getString(R.string.database_path);
		CreateRootDirectory fuck = new CreateRootDirectory();
		//fuck.start();
		screenInit();
		urlInit();
		directoryInit(); // 目录初始化
		
		/*new Thread() {
			public void run() {
				databaseInit();
			}
		}.start();*/
	}
	
	private void screenInit() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		
		Screen.width = displayMetrics.widthPixels;
		Screen.height = displayMetrics.heightPixels;
	}
	
	// 通信地址初始化
	private void urlInit() {
		// 登录地址初始化
		String loginUrl = getResources().getString(R.string.url_login_loginIp);
		String logoutUrl = getResources().getString(R.string.url_logout_logoutIp);
		
		LoginUser.loginUrl = loginUrl;
		LoginUser.logoutUrl = logoutUrl;
				
		
	}
	
	private void directoryInit() {
		// 创建外部存储目录
		CreateRootDirectory createRootDirectory = new CreateRootDirectory();
		
		boolean directory = createRootDirectory.directoryIsExist();
		if (directory) { // 直接进入登录页面
		
			Intent loginActivityIntent = new Intent();
			loginActivityIntent.setClass(MainActivity.this, LoginActivity.class);
			startActivity(loginActivityIntent);
		} else { // 首次进入
			databaseInit();
			
			createRootDirectory.start();
			Intent welcomeIntent = new Intent();
			welcomeIntent.setClass(MainActivity.this, WelcomeActivity.class);
			startActivity(welcomeIntent);
		}
		
		// 关闭当前页
		MainActivity.this.finish();
	}
	
	private void databaseInit() {
		// TODO Auto-generated method stub
		System.out.println("create database");
		database_name = getResources().getString(R.string.database_name);
		String path = this.getFilesDir().getPath();
		db = SQLiteDatabase.openOrCreateDatabase(path + database_name, null);	
		/*
		// SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path, null);
		String createTableSQL = "create table if not exists jasonFriendList(uid integer primary key, username text, signature text, status integer)";
		db.execSQL(createTableSQL);
		System.out.println(createTableSQL);
		createTableSQL = "create table if not exists welcomeFriendList(uid integer primary key, username text, signature text, status integer)";
		db.execSQL(createTableSQL);
		System.out.println(createTableSQL);
		createTableSQL = "insert into jasonFriendList(uid,username,signature,status) values(1,'welcome','hello world',0)";
		db.execSQL(createTableSQL);
		System.out.println(createTableSQL);
		createTableSQL = "insert into welcomeFriendList(uid,username,signature,status) values(2,'jason','hello world',0)";
		db.execSQL(createTableSQL);
		System.out.println(createTableSQL);
		*/
		db.close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
