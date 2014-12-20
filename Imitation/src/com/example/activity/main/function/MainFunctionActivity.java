package com.example.activity.main.function;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.example.activity.communication.ChatActivity;
import com.example.activity.communication.ChatMsgEntity;
import com.example.beans.Communication;
import com.example.beans.Friend;
import com.example.imitation.R;
import com.example.service.LogoutService;

public class MainFunctionActivity extends TabActivity {
	
	private TabHost m_tabHost;
	private RadioGroup m_radioGroup;
	private int keyBackClickCount = 0;
	private NotificationManager nm;
	private static final int NOTIFICATION_ID = 0x123;
	
	private String className = MainFunctionActivity.class.getName();
	// 保持所启动的Service的IBinder对象
	BindService.MyBinder binder;
	// 定义一个ServiceConnection对象
	private ServiceConnection conn = new ServiceConnection() {
		// 当该Activity与Service连接成功时回调该方法
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			System.out.println("");
			Log.e(className, "--Service Connected--");
			// 获取Service的onBind方法所返回的MyBinder对象
			binder = (BindService.MyBinder) service;
		}
		
		// 当该Activity与Service断开连接时回调该方法
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.e(className, "--Service Disconnected--");
		}
	};
	
	class WriteChatRecord implements Runnable {
		
		private String name;
		private String content;
		
		public WriteChatRecord(String name, String content) {
			this.name = name;
			this.content = content;
		}
		
		public void run() {
			try {
				Looper.prepare();
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(Communication.chatDirectory + name
						+ ".txt"), true));
				bw.write("true^^" + content + "^^" + getDate() + "\n");
				Log.e(className, "写入数据:" + "true^^" + content + "^^" + getDate() + "到" + name + ".txt");
				bw.flush();
				bw.close();
				Looper.loop();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	private String getDate() {
		Calendar c = Calendar.getInstance();
		
		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = String.valueOf(c.get(Calendar.MONTH));
		String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
		String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		String mins = String.valueOf(c.get(Calendar.MINUTE));
		
		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins);
		
		return sbBuffer.toString();
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			ChatMsgEntity chatMsgEntity = (ChatMsgEntity) data.get("entity");
			
			switch (msg.what) {
			case 0:				
				sendNotification(chatMsgEntity);
				break;
			case 1:
				break;
			default:				
			}
		};
	};
	
	public int getID(String name) {
		
		for (int i = 0; i < Friend.friendUsername.length; i++) {
			if (Friend.friendUsername[i].equals(name))
				return Friend.friendUid[i];
		}
		return 0;
	}
	
	public Handler getHandler() {
		return handler;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainfunction);
		init();
		// serviceInit();
		
		// 获取系统的NotificationManager服务
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		ReceiverServerThread receiverServerThread;
		try {
			receiverServerThread = new ReceiverServerThread(this);
			Thread thread = new Thread(receiverServerThread);
			thread.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void sendNotification(ChatMsgEntity chatMsgEntity) {
		// 创建一个启动其他Activity的Intent
		Intent intent = new Intent(MainFunctionActivity.this, ChatActivity.class);
		Bundle bundle = new Bundle();
		int id = getID(chatMsgEntity.getName());
		// Toast.makeText(this, "--sendNotification method: id："+id + ", name:"+chatMsgEntity.getName(),
		// Toast.LENGTH_LONG).show();
		bundle.putInt("id", id);
		bundle.putString("contact", chatMsgEntity.getName());
		intent.putExtra("bundle", bundle);
		// String contact =
		PendingIntent pi = PendingIntent.getActivity(MainFunctionActivity.this, 0, intent, 0);
		Notification notify = new Notification.Builder(this)
		// 设置打开该通知，该通知自动消失
				.setAutoCancel(true)
				// 设置显示在状态栏的通知提示信息
				.setTicker("有新消息")
				// 设置通知的图标
				.setSmallIcon(R.drawable.app)
				// 设置通知内容的标题
				.setContentTitle("一条新通知")
				// 设置通知内容
				.setContentText(chatMsgEntity.getText())
				// // 设置使用系统默认的声音、默认LED灯
				// .setDefaults(Notification.DEFAULT_SOUND
				// |Notification.DEFAULT_LIGHTS)
				.setWhen(System.currentTimeMillis())
				// 设改通知将要启动程序的Intent
				.setContentIntent(pi).build();
		// 发送通知
		nm.notify(NOTIFICATION_ID, notify);
	}
	
	public void serviceInit() {
		final Intent intent = new Intent();
		// 为Intent设置Action属性
		intent.setAction("com.example.activity.main.function.BIND_SERVICE");
		// 绑定指定Serivce
		bindService(intent, conn, Service.BIND_AUTO_CREATE);
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
	
	public String getCurrentActivityName() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		RunningTaskInfo info = manager.getRunningTasks(1).get(0);
		String shortClassName = info.topActivity.getShortClassName(); // 类名
		return shortClassName;
		// return getCurrentActivityName();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// unbindService(conn);
		ReceiverServerThread.exit = true;
		
		LogoutService logoutService = new LogoutService();
		logoutService.start();
		Thread thread = new Thread(RemoveChatRecord);
		thread.start();
		super.onDestroy();
	}
	
	Runnable RemoveChatRecord = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			File file = new File(Communication.chatDirectory);
			File[] fileArray = file.listFiles();
			for (int i = 0; i < fileArray.length; i++) {
				if (fileArray[i].length() > 0) {
					try {
						BufferedWriter bw = new BufferedWriter(new FileWriter(fileArray[i]));
						bw.write("");
						bw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e(className, e.getMessage());
					}
				}
			}
			Looper.loop();
		}
	};
}
