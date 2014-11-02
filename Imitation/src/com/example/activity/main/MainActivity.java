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
		
		directoryInit();  //Ŀ¼��ʼ��
		urlInit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void directoryInit() {
		// �����ⲿ�洢Ŀ¼
		CreateRootDirectory createRootDirectory = new CreateRootDirectory();
		
		boolean directory = createRootDirectory.directoryIsExist();
		if (directory) { // ֱ�ӽ����¼ҳ��
		
			Intent loginActivityIntent = new Intent();
			loginActivityIntent.setClass(MainActivity.this, LoginActivity.class);
			startActivity(loginActivityIntent);
		} else { // �״ν���
			createRootDirectory.start();
			Intent welcomeIntent = new Intent();
			welcomeIntent.setClass(MainActivity.this, WelcomeActivity.class);
			startActivity(welcomeIntent);
		}
		
		// �رյ�ǰҳ
		MainActivity.this.finish();
	}
	
	//ͨ�ŵ�ַ��ʼ��
	private void urlInit(){
		//��¼��ַ��ʼ��
		String loginUrl = getResources().getString(R.string.url_login_loginIp);
		String logoutUrl = getResources().getString(R.string.url_logout_logoutIp);
		
		LoginUser.loginUrl = loginUrl;
		LoginUser.logoutUrl = logoutUrl;
	}
}
