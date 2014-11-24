package com.example.activity.publish;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;

import com.example.beans.DataBaseInstance;
import com.example.beans.Friend;
import com.example.beans.Person;
import com.example.beans.Publish;

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
		SQLiteDatabase db = SQLiteDatabase.openDatabase(DataBaseInstance.fullPath, null, SQLiteDatabase.OPEN_READWRITE);
		ContentValues values = new ContentValues();
		values.put("uid", this.id);
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
		
		String type = ".png";
		String friendHeadPhoto = DataBaseInstance.prePath + Person.username
				+ Publish.friendDirectory + id + ".png";
		String selfHeadPhoto = DataBaseInstance.prePath + Person.username
				+ Publish.selfDirectory + Publish.headphotoName;
		
		File fileSelfHeadPhoto = new File(selfHeadPhoto + type);
		File fileFriendHeadPhoto = new File(friendHeadPhoto);
		if (fileSelfHeadPhoto.exists()) {
			copy(fileSelfHeadPhoto,fileFriendHeadPhoto);
		} else {
			type = ".jpg";
			fileSelfHeadPhoto = new File(selfHeadPhoto + type);
			copy(fileSelfHeadPhoto,fileFriendHeadPhoto);
		}
		
		try {
			Bitmap bm = null;
			BitmapFactory.Options options = new BitmapFactory.Options();
			// options.inSampleSize = 8;//图片的长宽都是原来的1/8
			BufferedInputStream bis;
			bis = new BufferedInputStream(new FileInputStream(fileFriendHeadPhoto));
			bm = BitmapFactory.decodeStream(bis, null, options);
			friendHeadphoto[length] = bm;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		Friend.friendUid = friendUid;
		Friend.friendUsername = friendUsername;
		Friend.friendSignature = friendSignature;
		Friend.friendStatus = friendStatus;
		Friend.friendHeadphoto = friendHeadphoto;
		
		Friend.addFriend = true;
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
	
	public void copy(File inFile, File outFile) {
		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(inFile));
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(outFile));
			byte[] buf = new byte[512];
			int length = 0;
			while ((length = bufferedInputStream.read(buf)) != -1) {
				bufferedOutputStream.write(buf, 0, length);
			}
			bufferedOutputStream.flush();
			bufferedInputStream.close();
			bufferedOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
