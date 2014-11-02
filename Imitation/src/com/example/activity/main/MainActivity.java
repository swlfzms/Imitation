package com.example.activity.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.example.activity.login.LoginActivity;
import com.example.activity.welcomeview.WelcomeActivity;
import com.example.beans.LoginUser;
import com.example.imitation.R;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		directoryInit();  //目录初始化
		urlInit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
			createRootDirectory.start();
			Intent welcomeIntent = new Intent();
			welcomeIntent.setClass(MainActivity.this, WelcomeActivity.class);
			startActivity(welcomeIntent);
		}
		
		// 关闭当前页
		MainActivity.this.finish();
	}
	
	//通信地址初始化
	private void urlInit(){
		//登录地址初始化
		String loginUrl = getResources().getString(R.string.url_login_loginIp);
		String logoutUrl = getResources().getString(R.string.url_logout_logoutIp);
		
		LoginUser.loginUrl = loginUrl;
		LoginUser.logoutUrl = logoutUrl;
	}
}
