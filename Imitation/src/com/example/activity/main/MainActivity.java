package com.example.activity.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;

import com.example.activity.login.LoginActivity;
import com.example.activity.welcomeview.WelcomeActivity;
import com.example.beans.DataBaseInstance;
import com.example.beans.Friend;
import com.example.beans.LoginUser;
import com.example.beans.Screen;
import com.example.imitation.R;

public class MainActivity extends Activity {
	
	String database_name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		DataBaseInstance.path = getResources().getString(R.string.database_path);
		DataBaseInstance.prePath = getResources().getString(R.string.database_prepath);
		screenInit();
		urlInit();
		directoryInit(); // Ŀ¼��ʼ��
		getFilesDir().getPath();
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
	
	// ͨ�ŵ�ַ��ʼ��
	private void urlInit() {
		// ��¼��ַ��ʼ��
		String loginUrl = getResources().getString(R.string.url_login_loginIp);
		String logoutUrl = getResources().getString(R.string.url_logout_logoutIp);
		
		LoginUser.loginUrl = loginUrl;
		LoginUser.logoutUrl = logoutUrl;
		
		Friend.imageDownLoadPath = getResources().getString(R.string.url_publish_imagedownloadpath);
	}
	
	private void directoryInit() {
		// �����ⲿ�洢Ŀ¼
		CreateRootDirectory createRootDirectory = new CreateRootDirectory();
		
		boolean directory = createRootDirectory.directoryIsExist();
		System.out.println("·���Ƿ���ڣ� " + directory);
		if (directory) { // ֱ�ӽ����¼ҳ��
		
			Intent loginActivityIntent = new Intent();
			loginActivityIntent.setClass(MainActivity.this, LoginActivity.class);
			startActivity(loginActivityIntent);
		} else { // �״ν���
		
			createRootDirectory.start();
			Intent loginActivityIntent = new Intent();
			loginActivityIntent.setClass(MainActivity.this, LoginActivity.class);
			startActivity(loginActivityIntent);
			/*
			Intent welcomeIntent = new Intent();
			welcomeIntent.setClass(MainActivity.this, WelcomeActivity.class);
			startActivity(welcomeIntent);
			*/
		}
		
		// �رյ�ǰҳ
		MainActivity.this.finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
