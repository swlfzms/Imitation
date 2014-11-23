package com.example.activity.login;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.activity.main.function.MainFunctionActivity;
import com.example.activity.register.RegisterActivity;
import com.example.beans.LoginUser;
import com.example.beans.Person;
import com.example.imitation.R;
import com.example.service.LoginService;

public class LoginActivity extends Activity {
	
	public static final String SP_INFO = "SPDATA_Files";
	public static final String USERID = "UserID";
	public static final String PASSWORD = "PassWord";
	
	private ProgressDialog progressDialog;
	
	private Button btnLogin;
	private Button btnRegist;
	private Button btnExit;
	
	private EditText etusername;
	private EditText etpassword;
	
	private static CheckBox cb;
	
	private static String uidstr;
	private static String pwdstr;
	
	public String loginMessage;
	
	private boolean result; // login result; true or false;
	
	private Handler myHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			
			// 登录失败
			case 0:
				Toast.makeText(LoginActivity.this, loginMessage, Toast.LENGTH_LONG).show();
				progressDialog.cancel();
				break;
			
			// 登录成功
			case 1:
				Toast.makeText(LoginActivity.this, loginMessage, Toast.LENGTH_SHORT).show();
				Toast.makeText(LoginActivity.this, "ip address: "+ Person.ip, Toast.LENGTH_LONG).show();
				Intent mainFunctionActivityIntent = new Intent();
				mainFunctionActivityIntent.setClass(LoginActivity.this, MainFunctionActivity.class);
				startActivity(mainFunctionActivityIntent);
				
				// 清除当前的登录activity
				LoginActivity.this.finish();
				
				progressDialog.cancel();
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		initParameter();
	}
	
	public Handler getMyHandler() {
		return myHandler;
	}
	
	// 登录
	private final View.OnClickListener btnLoginListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			final String strUsername = etusername.getText().toString().trim();
			final String strPassword = etpassword.getText().toString().trim();
			
			if (strUsername == null || strUsername.equals("")) {
				Toast.makeText(LoginActivity.this, "用户名不为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (strPassword == null || strPassword.equals("")) {
				Toast.makeText(LoginActivity.this, "密码不为空", Toast.LENGTH_SHORT).show();
				return;
			}
			
			Person.ip = getLocalIpAddress();			
			
			// 显示循环进度圈
			progressDialog = ProgressDialog.show(LoginActivity.this, "请稍候", "玩命加载中...", true, true);
			progressDialog.setCanceledOnTouchOutside(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
			
			// 待验证的登录信息
			LoginUser.username = strUsername;
			LoginUser.password = strPassword;
			
			LoginService loginService = new LoginService(LoginActivity.this);
			loginService.start();
			
			/*
			 * if (result) { // 登陆成功 Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show(); } else { //
			 * 登陆失败 Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show(); }
			 */
		}
	};
	public String getLocalIpAddress()  
    {  
        try  
        {  
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)  
            {  
               NetworkInterface intf = en.nextElement();  
               for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)  
               {  
                   InetAddress inetAddress = enumIpAddr.nextElement();  
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(inetAddress.getHostAddress()))  
                   {  
                       return inetAddress.getHostAddress().toString();  
                   }  
               }  
           }  
        }  
        catch (SocketException ex)  
        {  
            Log.e("WifiPreference IpAddress", ex.toString());  
        }  
        return null;  
    }  
	
	
	// 注册
	private final View.OnClickListener btnRegistListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, RegisterActivity.class);
			startActivity(intent);
		}
	};
	
	// 退出
	private final View.OnClickListener btnExitListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			System.exit(0);
		}
		
	};
	
	// 初始化
	public void initParameter() {
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnRegist = (Button) findViewById(R.id.btnRegist);
		btnExit = (Button) findViewById(R.id.btnExit);
		
		etusername = (EditText) findViewById(R.id.etUsername);
		etpassword = (EditText) findViewById(R.id.etPassword);
		
		btnLogin.setOnClickListener(btnLoginListener);
		btnRegist.setOnClickListener(btnRegistListener);
		btnExit.setOnClickListener(btnExitListener);
		
		cb = (CheckBox) findViewById(R.id.cbremeber);
		
		new Thread(new Runnable() {
			public void run() {
				checkIfRemeber();
			}
		}).start();
		
	}
	
	public void checkIfRemeber() {
		SharedPreferences sp = getSharedPreferences(SP_INFO, MODE_PRIVATE);
		uidstr = sp.getString(USERID, null);
		pwdstr = sp.getString(PASSWORD, null);
		
		if (uidstr != null && pwdstr != null) {
			etusername.setText(uidstr);
			etpassword.setText(pwdstr);
			cb.setChecked(true);
		}
	}
	
	public void remeberMe(String uid, String pwd) {
		SharedPreferences sp = getSharedPreferences(SP_INFO, MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		
		editor.putString(USERID, uid);
		editor.putString(PASSWORD, pwd);
		editor.commit();
	}
	
	protected void onStop() {
		super.onStop();
		if (cb.isChecked()) {
			uidstr = etusername.getText().toString().trim();
			pwdstr = etpassword.getText().toString().trim();
			remeberMe(uidstr, pwdstr);
		} else {
			remeberMe(null, null);
		}
	}
}
