package com.example.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Looper;
import android.util.Log;

import com.example.activity.login.LoginActivity;
import com.example.beans.LoginUser;
import com.example.beans.Person;
import com.example.tools.Encryption;

public class LoginService extends Thread {
	
	LoginActivity loginActivity;
	
	public LoginService(LoginActivity loginActivity) {
		this.loginActivity = loginActivity;
	}
	
	@Override
	public void run() {
		Looper.prepare();
		LoginUser.password = encrption(LoginUser.password); // 加密密码；
		
		try {			
			
			JSONObject jsonObject = new JSONObject();
 
            jsonObject.put("username", LoginUser.username);
            jsonObject.put("password", LoginUser.password);
            jsonObject.put("ip", Person.ip);
            
            Log.e("json object", jsonObject.toString());                     
			
			String postUrl = LoginUser.loginUrl;
			HttpPost httpPost = new HttpPost(postUrl);
			httpPost.addHeader("Content-Type", "application/json");			
			httpPost.setEntity(new StringEntity(jsonObject.toString()));					
			
			
			int state = 500;
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpPost);
			state = httpResponse.getStatusLine().getStatusCode();
			
			if (state == 200) {				
				
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity()
						.getContent()));								
				StringBuilder content = new StringBuilder();
				String tmp;
				while ((tmp = bufferedReader.readLine()) != null) {
					content.append(tmp);
				}
				bufferedReader.close();
				System.out.println(content.toString());
				
				
				JSONObject loginUserJsonObject = new JSONObject(content.toString());
				boolean result = loginUserJsonObject.getBoolean("result");
				String message = loginUserJsonObject.getString("message");				
				
				if (result) { // 成功
					//记录当前登录用户					
					Person.username = LoginUser.username;
					Person.password = LoginUser.password;
					int id = loginUserJsonObject.getInt("id");
					Person.id = id;
					loginActivity.getMyHandler().sendEmptyMessage(1);
				} else { // 失败
					loginActivity.getMyHandler().sendEmptyMessage(0);
				}
				loginActivity.loginMessage = message;
				System.out.println("connection done");
			}
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
	
	// 加密方法
	public String encrption(String str) {
		return Encryption.getMD5ofStr(str, 7);
	}
	
	// 不再使用的方法
	@Deprecated
	@SuppressWarnings("unchecked")
	public boolean post(String username, String password) {
		
		LoginUser loginUser = new LoginUser(username, password);
		
		try {
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("loginUser", loginUser);
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("jsonString", jsonObject.toString()));
			
			String postUrl = LoginUser.loginUrl;
			HttpPost httpPost = new HttpPost(postUrl);
			HttpParams httpParams = new BasicHttpParams();
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
			
			int state = 500;
			HttpClient httpClient = new DefaultHttpClient();
			state = httpClient.execute(httpPost).getStatusLine().getStatusCode();
			if (state == 200) {
				HttpResponse httpResponse = httpClient.execute(httpPost);
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity()
						.getContent()));
				
				String tmp;
				StringBuilder content = new StringBuilder();
				while ((tmp = bufferedReader.readLine()) != null) {
					content.append(tmp);
				}
				
				JSONObject loginUserJsonObject = new JSONObject(builder.toString());
				boolean result = loginUserJsonObject.getBoolean("result");
				return result;
			}
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
		return false;
	}
}
