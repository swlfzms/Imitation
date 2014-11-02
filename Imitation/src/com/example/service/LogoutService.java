package com.example.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Looper;
import android.util.Log;

import com.example.beans.LoginUser;
import com.example.beans.Person;

public class LogoutService extends Thread {
	
	public LogoutService() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Looper.prepare();
		
		//Person 为实际登录的用户
		try {		
			JSONObject jsonObject = new JSONObject();			
			jsonObject.put("id", Person.id);			
			
			Log.e("json object", jsonObject.toString());
			
			String postUrl = LoginUser.logoutUrl;
			HttpPost httpPost = new HttpPost(postUrl);
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.setEntity(new StringEntity(jsonObject.toString()));
			
			//执行发送下线通知
			HttpClient httpClient = new DefaultHttpClient();
			httpClient.execute(httpPost);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Looper.loop();
	}
}
