package com.example.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.example.beans.RegisterUser;

public class Service {
	
	private JSONObject sendObject;
	private JSONObject receiveObject;
	private String url;
	
	public Service(JSONObject sendObject, String url) {
		this.sendObject = sendObject;		
		this.url = url;
	}	
	
	public JSONObject getResult() {
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.setEntity(new StringEntity(sendObject.toString()));
			
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
				
				receiveObject = new JSONObject(content.toString());
				return receiveObject;
			}
		} catch (Exception e) {
			try{
				receiveObject.put("result", false);
				receiveObject.put("message", "unknow error");
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return receiveObject;
	}
}
