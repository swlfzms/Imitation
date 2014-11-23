package com.example.activity.friend;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;

import com.example.beans.Friend;
import com.example.beans.Person;
import com.example.imitation.R;

public class FriendList extends Thread {
	
	private String path;
	private String friendDataPath;
	private String tableName = Person.username + "FriendList";
	private FriendActivity friendActivity;	
	
	public FriendList(String path, String friendDataPath) {
		// TODO Auto-generated constructor stub
		this.path = path;
		this.friendDataPath = friendDataPath;
	}
	
	public void setFriendActivity(FriendActivity friendActivity) {
		this.friendActivity = friendActivity;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Looper.prepare();
		System.out.println("开始获取好友列表信息");
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path, null);
		
		Cursor c = db.query(tableName, new String[] { "uid", "username", "signature", "status", "headphotoversion",
				"signatureversion" }, null, null, null, null, "uid");
		int uidIndex = c.getColumnIndex("uid");
		int usernameIndex = c.getColumnIndex("username");
		int signatureIndex = c.getColumnIndex("signature");
		int statusIndex = c.getColumnIndex("status");
		int headphotoversionIndex = c.getColumnIndex("headphotoversion");
		int signatureversionIndex = c.getColumnIndex("signatureversion");
		
		int[] friendUid = new int[c.getCount()];
		String[] friendUsername = new String[c.getCount()];
		String[] friendSignature = new String[c.getCount()];
		int[] friendStatus = new int[c.getCount()];
		int[] friendheadphotoversion = new int[c.getCount()];
		int[] friendsignatureversion = new int[c.getCount()];
		Bitmap[] friendHeadphoto = new Bitmap[c.getCount()];
		
		int i = 0;
		for (c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()) {
			friendUid[i] = c.getInt(uidIndex);
			friendUsername[i] = c.getString(usernameIndex);
			friendSignature[i] = c.getString(signatureIndex);
			friendStatus[i] = c.getInt(statusIndex);
			friendheadphotoversion[i] = c.getInt(headphotoversionIndex);
			friendsignatureversion[i] = c.getInt(signatureversionIndex);
			System.out.println("friend: "+friendUid[i]+", "+friendUsername[i]);
			i++;
		}
		String headphotopath = android.os.Environment.getExternalStorageDirectory() + "/Imitation/" + Person.username
				+ "/FriendListHeadPhoto/";
		
		File file;
		for (int j = 0; j < i; j++) {
			// 判断类型，就两种jpg和png
			String filepath = headphotopath + friendUid[j] + ".jpg";
			System.out.println(filepath);
			file = new File(filepath);
			String type;
			if (file.exists()) { // jpg
				type = ".jpg";
			} else {// png
				type = ".png";
			}
			System.out.println(filepath);
			FileInputStream f;
			try {
				headphotopath = headphotopath + friendUid[j] + type;
				System.out.println("search path: " + headphotopath);
				f = new FileInputStream(headphotopath);
				Bitmap bm = null;
				BitmapFactory.Options options = new BitmapFactory.Options();
				// options.inSampleSize = 8;//图片的长宽都是原来的1/8
				BufferedInputStream bis = new BufferedInputStream(f);
				bm = BitmapFactory.decodeStream(bis, null, options);
				friendHeadphoto[j] = bm;
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Friend.friendUid = friendUid;
		Friend.friendUsername = friendUsername;
		Friend.friendSignature = friendSignature;
		Friend.friendStatus = friendStatus;
		Friend.friendHeadphoto = friendHeadphoto;
		Friend.friendheadphotoversion = friendheadphotoversion;
		Friend.friendsignatureversion = friendsignatureversion;
		
		// 更改列表内容
		this.friendActivity.getMyHandler().sendEmptyMessage(1);
		
		c.close();
		db.close();
		
		//如果存在好友
		if(Friend.friendUid.length>0){
			// 获取数据，对比数据库内容进行更改		
			FriendData friendData = new FriendData(friendDataPath);
			friendData.run();
		}		
		System.out.println("搞定刷新内容");
		Looper.loop();
	}
	
	public void flush(){
		System.out.println("flush 开始获取好友列表信息");
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path, null);
		
		Cursor c = db.query(tableName, new String[] { "uid", "username", "signature", "status", "headphotoversion",
				"signatureversion" }, null, null, null, null, "uid");
		int uidIndex = c.getColumnIndex("uid");
		int usernameIndex = c.getColumnIndex("username");
		int signatureIndex = c.getColumnIndex("signature");
		int statusIndex = c.getColumnIndex("status");
		int headphotoversionIndex = c.getColumnIndex("headphotoversion");
		int signatureversionIndex = c.getColumnIndex("signatureversion");
		
		int[] friendUid = new int[c.getCount()];
		String[] friendUsername = new String[c.getCount()];
		String[] friendSignature = new String[c.getCount()];
		int[] friendStatus = new int[c.getCount()];
		int[] friendheadphotoversion = new int[c.getCount()];
		int[] friendsignatureversion = new int[c.getCount()];
		Bitmap[] friendHeadphoto = new Bitmap[c.getCount()];
		
		int i = 0;
		for (c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()) {
			friendUid[i] = c.getInt(uidIndex);
			friendUsername[i] = c.getString(usernameIndex);
			friendSignature[i] = c.getString(signatureIndex);
			friendStatus[i] = c.getInt(statusIndex);
			friendheadphotoversion[i] = c.getInt(headphotoversionIndex);
			friendsignatureversion[i] = c.getInt(signatureversionIndex);
			System.out.println("friend: "+friendUid[i]+", "+friendUsername[i]);
			i++;
		}
		String headphotopath = android.os.Environment.getExternalStorageDirectory() + "/Imitation/" + Person.username
				+ "/FriendListHeadPhoto/";
		
		File file;
		for (int j = 0; j < i; j++) {
			// 判断类型，就两种jpg和png
			String filepath = headphotopath + friendUid[j] + ".jpg";
			System.out.println(filepath);
			file = new File(filepath);
			String type;
			if (file.exists()) { // jpg
				type = ".jpg";
			} else {// png
				type = ".png";
			}
			System.out.println(filepath);
			FileInputStream f;
			try {
				headphotopath = headphotopath + friendUid[j] + type;
				System.out.println("search path: " + headphotopath);
				f = new FileInputStream(headphotopath);
				Bitmap bm = null;
				BitmapFactory.Options options = new BitmapFactory.Options();
				// options.inSampleSize = 8;//图片的长宽都是原来的1/8
				BufferedInputStream bis = new BufferedInputStream(f);
				bm = BitmapFactory.decodeStream(bis, null, options);
				friendHeadphoto[j] = bm;
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Friend.friendUid = friendUid;
		Friend.friendUsername = friendUsername;
		Friend.friendSignature = friendSignature;
		Friend.friendStatus = friendStatus;
		Friend.friendHeadphoto = friendHeadphoto;
		Friend.friendheadphotoversion = friendheadphotoversion;
		Friend.friendsignatureversion = friendsignatureversion;		
		
		c.close();
		db.close();
		
		//如果存在好友
		if(Friend.friendUid.length>0){
			// 获取数据，对比数据库内容进行更改		
			FriendData friendData = new FriendData(friendDataPath);
			friendData.run();
		}		
		System.out.println("flush 搞定刷新内容");
	}
}
