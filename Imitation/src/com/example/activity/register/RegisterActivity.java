package com.example.activity.register;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.activity.login.LoginActivity;
import com.example.beans.RegisterUser;
import com.example.imitation.R;
import com.example.service.RegisterService;

public class RegisterActivity extends Activity {
	
	private Button btnRegisterOk;
	private Button btnRegisterBack;
	private EditText edRegisterName;
	private EditText edRegisterPass;
	private EditText edRegisterRePass;
	private EditText edRegisterEmail;
	
	private ProgressDialog progressDialog;
	
	public String registerMessage;
	
	private Handler myHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			
			// ע��ʧ��
			case 0:
				Toast.makeText(RegisterActivity.this, registerMessage, Toast.LENGTH_SHORT).show();
				progressDialog.cancel();
				break;
			
			// ע��ɹ�
			case 1:
				Toast.makeText(RegisterActivity.this, registerMessage, Toast.LENGTH_SHORT).show();											
				progressDialog.cancel();
				break;
			}
		}
	};
	
	public Handler getMyHandler() {
		return myHandler;
	}
	
	public boolean checkString(String s) {
		Pattern pattern = Pattern.compile("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(s);
		return matcher.matches();
	}
	
	public boolean checkSpace(String str) {
		if (str == null || str.equals("") || str.contains(" "))
			return false;
		return true;
	}
	
	public View.OnClickListener btnRegisterOkListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			final String email = edRegisterEmail.getText().toString();
			final String username = edRegisterName.getText().toString();
			final String password = edRegisterPass.getText().toString();
			final String rePass = edRegisterRePass.getText().toString();
			
			if (!checkString(email)) {
				Toast.makeText(RegisterActivity.this, "email��ʽ����ȷ", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!checkSpace(email)) {
				Toast.makeText(RegisterActivity.this, "email��Ϊ�գ��Ҳ����пո�", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!checkSpace(username)) {
				Toast.makeText(RegisterActivity.this, "�û�����Ϊ�գ��Ҳ����пո�", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!checkSpace(password)) {
				Toast.makeText(RegisterActivity.this, "���벻Ϊ�գ��Ҳ����пո�", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!checkSpace(rePass)) {
				Toast.makeText(RegisterActivity.this, "ȷ�����벻Ϊ�գ��Ҳ����пո�", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!password.equals(rePass)) {
				Toast.makeText(RegisterActivity.this, "���벻��ͬ", Toast.LENGTH_SHORT).show();
				return;
			}
			
			RegisterUser.username = username;
			RegisterUser.password = password;
			RegisterUser.email = email;
			
			// ��ʾѭ������Ȧ
			progressDialog = ProgressDialog.show(RegisterActivity.this, "���Ժ�", "����������...", true, true);
			progressDialog.setCanceledOnTouchOutside(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
			
			RegisterUser.registerURL = getResources().getString(R.string.url_register_registerIp);
			RegisterService registerService = new RegisterService(RegisterActivity.this);
			registerService.start();
		}
	};
	public View.OnClickListener btnRegisterBackListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent LoginActivityIntent = new Intent();
			LoginActivityIntent.setClass(RegisterActivity.this, LoginActivity.class);
			startActivity(LoginActivityIntent);
			RegisterActivity.this.finish();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		btnRegisterOk = (Button) findViewById(R.id.btnRegisterOk);
		btnRegisterBack = (Button) findViewById(R.id.btnRegisterBack);
		edRegisterName = (EditText) findViewById(R.id.etRegisterName);
		edRegisterPass = (EditText) findViewById(R.id.etRegisterPass);
		edRegisterRePass = (EditText) findViewById(R.id.etRegisterRePass);
		edRegisterEmail = (EditText) findViewById(R.id.etRegisterEmail);
		btnRegisterOk.setOnClickListener(btnRegisterOkListener);
		btnRegisterBack.setOnClickListener(btnRegisterBackListener);
	}
	
}
