package com.example.activity.main.function;

import java.util.Timer;
import java.util.TimerTask;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.example.imitation.R;
import com.example.service.LogoutService;

public class MainFunctionActivity extends TabActivity {
	
	private TabHost m_tabHost;
	private RadioGroup m_radioGroup;
	private int keyBackClickCount = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainfunction);
		init();
	}
	
	private void init() {
		m_tabHost = getTabHost();
		
		int count = Constant.mTabClassArray.length;
		for (int i = 0; i < count; i++) {
			TabSpec tabSpec = m_tabHost.newTabSpec(Constant.mTextviewArray[i]).setIndicator(Constant.mTextviewArray[i])
					.setContent(getTabItemIntent(i));
			m_tabHost.addTab(tabSpec);
		}
		
		m_radioGroup = (RadioGroup) findViewById(R.id.main_radiogroup);
		m_radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.main_tab_friend:
					m_tabHost.setCurrentTabByTag(Constant.mTextviewArray[0]);
					break;
				case R.id.main_tab_visitor:
					m_tabHost.setCurrentTabByTag(Constant.mTextviewArray[1]);
					break;
				case R.id.main_tab_publish:
					m_tabHost.setCurrentTabByTag(Constant.mTextviewArray[2]);
					break;
				case R.id.main_tab_diary:
					m_tabHost.setCurrentTabByTag(Constant.mTextviewArray[3]);
					break;
				}
			}
		});
		
		((RadioButton) m_radioGroup.getChildAt(0)).toggle();
	}
	
	private Intent getTabItemIntent(int index) {
		Intent intent = new Intent(this, Constant.mTabClassArray[index]);
		
		return intent;
	}
	
	// 监听返回键
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
				
			switch (keyBackClickCount++) {
			case 0:
				Toast.makeText(MainFunctionActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						keyBackClickCount = 0;
					}
				}, 3000);
				break;
			case 1:
 				LogoutService logoutService = new LogoutService();
 				logoutService.start();
				this.finish();
				break;			
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
}
