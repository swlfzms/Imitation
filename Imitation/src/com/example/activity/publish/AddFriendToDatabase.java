package com.example.activity.publish;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Looper;

import com.example.activity.friend.FriendActivity;
import com.example.beans.DataBaseInstance;
import com.example.beans.Friend;
import com.example.beans.Person;

public class AddFriendToDatabase extends Thread {
	
	private int id;
	private String username;
	private String signature;
	private String ip;
	private int status;
	
	public AddFriendToDatabase(int id, String username, String signature, String ip, int status) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.username = username;
		this.signature = signature;
		this.ip = ip;
		this.status = status;
	}
	
	public void run() {
		Looper.prepare();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(DataBaseInstance.path, null, SQLiteDatabase.OPEN_READWRITE);
		ContentValues values = new ContentValues();
		values.put("id", this.id);
		values.put("username", this.username);
		values.put("signature", this.signature);
		values.put("ip", this.ip);
		values.put("status", this.status);
		db.insert(Person.username + "FriendList", null, values);
		
		int length = Friend.friendUid.length;
		int[] friendUid = new int[length + 1];
		String[] friendUsername = new String[length + 1];
		String[] friendSignature = new String[length + 1];
		int[] friendStatus = new int[length + 1];
		Bitmap[] friendHeadphoto = new Bitmap[length + 1];
		
		copy(friendUid, Friend.friendUid, length);
		copy(friendUsername, Friend.friendUsername, length);
		copy(friendSignature, Friend.friendSignature, length);
		copy(friendStatus, Friend.friendStatus, length);
		copy(friendHeadphoto, Friend.friendHeadphoto, length);
		
		friendUid[length] = id;
		friendUsername[length] = this.username;
		friendSignature[length] = signature;
		friendStatus[length] = status;
		friendHeadphoto[length] = friendHeadphoto[length-1];
		
		Friend.friendUid = friendUid;
		Friend.friendUsername = friendUsername;
		Friend.friendSignature = friendSignature;
		Friend.friendStatus = friendStatus;
		Friend.friendHeadphoto = friendHeadphoto;
		
		Friend.dataChanged = true;
		db.close();
		Looper.loop();
	}
	
	public void copy(int[] obj1, int[] obj2, int length) {
		
		for (int i = 0; i < length; i++) {
			obj1[i] = obj2[i];
		}
	}
	
	public void copy(String[] obj1, String[] obj2, int length) {
		
		for (int i = 0; i < length; i++) {
			obj1[i] = obj2[i];
		}
	}
	
	public void copy(Bitmap[] obj1, Bitmap[] obj2, int length) {
		
		for (int i = 0; i < length; i++) {
			obj1[i] = obj2[i];
		}
	}
}
