package com.example.activity.main;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.widget.Toast;

import com.example.activity.login.LoginActivity;
import com.example.activity.welcomeview.WelcomeActivity;
import com.example.beans.Communication;
import com.example.beans.DataBaseInstance;
import com.example.beans.Friend;
import com.example.beans.LoginUser;
import com.example.beans.Publish;
import com.example.beans.Screen;
import com.example.imitation.R;

public class MainActivity extends Activity {
	
	String database_name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// 判断存储卡是否存在
		boolean externalStorageState = android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (!externalStorageState) {
			Toast.makeText(this, "请插入外置存储卡", Toast.LENGTH_LONG).show();
			return;
		}
		
		DataBaseInstance.externalStorage = android.os.Environment.getExternalStorageDirectory().toString();
		DataBaseInstance.prePath = DataBaseInstance.externalStorage
				+ getResources().getString(R.string.database_prepath);
		DataBaseInstance.path = DataBaseInstance.externalStorage + getResources().getString(R.string.database_path);
		DataBaseInstance.name = getResources().getString(R.string.database_name);
		DataBaseInstance.fullPath = DataBaseInstance.path + DataBaseInstance.name;
		
		Publish.friendDirectory = getResources().getString(R.string.publish_friendDirectory);
		Publish.selfDirectory = getResources().getString(R.string.publish_selfDirectory);
		Publish.headphotoName = getResources().getString(R.string.publish_headphotoName);
		Publish.friendList = getResources().getString(R.string.publish_FriendList);
		
		Communication.port = Integer.parseInt(getResources().getString(R.string.communication_port));
		Communication.url = getResources().getString(R.string.communication_address);
		Communication.chatDirectory = DataBaseInstance.prePath + getResources().getString(R.string.chat_directory);
		Communication.voiceSavePath = DataBaseInstance.externalStorage + getResources().getString(R.string.communication_voiceSavePath);

		screenInit();
		urlInit();
		directoryInit(); // 目录初始化
		
		new Thread(){
		
			public void run(){
				File dir = new File(Communication.chatDirectory);
				if(!dir.exists()){
					dir.mkdirs();
				}
                dir = new File(Communication.voiceSavePath);
                if(!dir.exists()){
                    dir.mkdirs();
                }
			};
		}.start();
		/*
		 * new Thread() { public void run() { databaseInit(); } }.start();
		 */
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
		
		Friend.imageDownLoadPath = getResources().getString(R.string.url_publish_imagedownloadpath);
	}
	
	private void directoryInit() {
		// 创建外部存储目录
		CreateRootDirectory createRootDirectory = new CreateRootDirectory();
		
		boolean directory = createRootDirectory.directoryIsExist();
		System.out.println("路径是否存在： " + directory);
		if (directory) { // 直接进入登录页面
		
			Intent loginActivityIntent = new Intent();
			loginActivityIntent.setClass(MainActivity.this, LoginActivity.class);
			startActivity(loginActivityIntent);
		} else { // 首次进入
		
			createRootDirectory.start();
			/*
			 * Intent loginActivityIntent = new Intent(); loginActivityIntent.setClass(MainActivity.this,
			 * LoginActivity.class); startActivity(loginActivityIntent);
			 */
			
			Intent welcomeIntent = new Intent();
			welcomeIntent.setClass(MainActivity.this, WelcomeActivity.class);
			startActivity(welcomeIntent);
			
		}
		
		// 关闭当前页
		MainActivity.this.finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
