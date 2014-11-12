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

public class FriendList extends Thread {
	
	private String path;
	private String tableName = Person.username + "FriendList";	
	private FriendActivity friendActivity;
	
	public FriendList( String path) {
		// TODO Auto-generated constructor stub		
		this.path = path;
	}
	
	
	public void setFriendActivity(FriendActivity friendActivity) {
		this.friendActivity = friendActivity;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		Looper.prepare();
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path, null);
		
		Cursor c = db.query(tableName, new String[] { "uid", "username", "signature", "status" }, null, null, null,
				null, null);
		int uidIndex = c.getColumnIndex("uid");
		int usernameIndex = c.getColumnIndex("username");
		int signatureIndex = c.getColumnIndex("signature");
		int statusIndex = c.getColumnIndex("status");
		int[] friendUid = new int[c.getCount()];
		String[] friendUsername = new String[c.getCount()];
		String[] friendSignature = new String[c.getCount()];
		int[] friendStatus = new int[c.getCount()];
		Bitmap[] friendHeadphoto = new Bitmap[c.getCount()];
		
		int i = 0;
		for (c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()) {
			friendUid[i] = c.getInt(uidIndex);
			friendUsername[i] = c.getString(usernameIndex);
			friendSignature[i] = c.getString(signatureIndex);
			friendStatus[i] = c.getInt(statusIndex);
			i++;
		}
		String headphotopath = android.os.Environment.getExternalStorageDirectory() + "/Imitation/" + Person.username
				+ "/FriendListHeadPhoto/";
		
		File file;		
		for (int j = 0; j < i; j++) {
			//判断类型，就两种jpg和png		
			String filepath = headphotopath+friendUid[j]+".jpg";
			System.out.println(filepath);
			file = new File(filepath);
			String type;
			if(file.exists()){ //jpg					
				type=".jpg";
			}else{//png
				type=".png";
			}			 
			System.out.println(type);
			FileInputStream f;
			try {
				headphotopath = headphotopath+friendUid[j]+type;
				System.out.println("search path: "+headphotopath);
				f = new FileInputStream(headphotopath);
				Bitmap bm = null; 
				BitmapFactory.Options options = new BitmapFactory.Options(); 
				//options.inSampleSize = 8;//图片的长宽都是原来的1/8 
				BufferedInputStream bis = new BufferedInputStream(f); 
				bm = BitmapFactory.decodeStream(bis, null, options); 
				friendHeadphoto[j]=bm;
				
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
		
		c.close();
		db.close();
		
		this.friendActivity.getMyHandler().sendEmptyMessage(1);
		
		Looper.loop();
	}
}
