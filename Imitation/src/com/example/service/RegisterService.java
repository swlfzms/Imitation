package com.example.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Looper;
import android.util.Log;

import com.example.activity.register.RegisterActivity;
import com.example.beans.LoginUser;
import com.example.beans.Person;
import com.example.beans.RegisterUser;
import com.example.tools.Encryption;

public class RegisterService extends Thread {
	
	RegisterActivity registerActivity;
	
	public RegisterService(RegisterActivity registerActivity) {
		// TODO Auto-generated constructor stub
		this.registerActivity = registerActivity;
	}
	
	@Override
	public void run() {
		Looper.prepare();
		
		RegisterUser.password = encrption(RegisterUser.password);
		
		JSONObject jsonObject = new JSONObject();
		
		try {
			String ip = getLocalIpAddress();
			
			jsonObject.put("username", RegisterUser.username);
			jsonObject.put("password", RegisterUser.password);
			jsonObject.put("email", RegisterUser.email);
			jsonObject.put("ip", ip);
			
			Log.e("json object", jsonObject.toString());
			
			String postUrl = RegisterUser.registerURL;
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
				
				JSONObject registerResultJsonObject = new JSONObject(content.toString());
				boolean result = registerResultJsonObject.getBoolean("result");
				String message = registerResultJsonObject.getString("message");
				if (result) { // 成功										
					registerActivity.getMyHandler().sendEmptyMessage(1);
				} else { // 失败
					registerActivity.getMyHandler().sendEmptyMessage(0);
				}
				registerActivity.registerMessage = message;
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
}
