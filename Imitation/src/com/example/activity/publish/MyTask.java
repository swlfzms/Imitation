 package com.example.activity.publish;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.beans.Friend;
import com.example.beans.Person;
import com.example.service.Service;

public class MyTask extends AsyncTask<Void, Void, Void> {
	
	private String requestMessage;
	private boolean result;
	private String message;
	private PublishActivity publishActivity;
	private String type;
	
	public MyTask(String requestMessage, PublishActivity publishActivity, String type) {
		this.requestMessage = requestMessage;
		this.publishActivity = publishActivity;
		this.type = type;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		JSONObject sendObject = new JSONObject();
		try {
			if (type.equals("signature")) {
				System.out.println("signature");
				signatureAction(sendObject);
			} else if (type.equals("addfriend")) {
				System.out.println("addfriend");
				addFriendAction(sendObject);
			} else {
				
			}
			
		} catch (Exception e) {
			message = e.getMessage();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		Toast.makeText(publishActivity, message, Toast.LENGTH_SHORT).show();
	}
	
	public void signatureAction(JSONObject sendObject) {
		try {
			sendObject.put("id", Person.id);
			sendObject.put("signature", this.requestMessage);
			System.out.println(this.requestMessage);
			String url = this.publishActivity.getSignatureUrl();
			
			Service service = new Service(sendObject, url);
			JSONObject receiveObject = service.getResult();
			
			result = receiveObject.getBoolean("result");
			message = receiveObject.getString("message");
		} catch (Exception e) {
			message = e.getMessage();
		}
	}
	
	public void addFriendAction(JSONObject sendObject) {
		try {
			result = isFriendAlready(this.requestMessage);
			if (result == true) {
				message = "那家伙已经是你的好友";
			} else {
				sendObject.put("friendName", this.requestMessage);
				System.out.println(this.requestMessage);
				String url = this.publishActivity.getAddFriendUrl();
				
				Service service = new Service(sendObject, url);
				JSONObject receiveObject = service.getResult();
				
				result = receiveObject.getBoolean("result");
				message = receiveObject.getString("message");
				
				if (result) {
					int id = receiveObject.getInt("id");
					int status = receiveObject.getInt("status");
					String signature = receiveObject.getString("signature");
					String ip = receiveObject.getString("ip");
					
					AddFriendToDatabase addFriendToDatabase = new AddFriendToDatabase(id, this.requestMessage,
							signature, ip, status);
					addFriendToDatabase.start();
					
					
					
				}
			}
		} catch (Exception e) {
			message = e.getMessage();
		}
	}
	
	public boolean isFriendAlready(String username) {
		// 全部转成小写字母
		for (int i = 0; i < Friend.friendUsername.length; i++) {
			if (Friend.friendUsername[i].toLowerCase().equals(username.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	
}
