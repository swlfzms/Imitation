package com.example.beans;

import java.io.Serializable;

import android.graphics.Bitmap;

public class Friend implements Serializable {
	
	/**
	 * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	 */
	
	private static final long serialVersionUID = -464767809042591618L;
	public static int[] friendUid;
	public static String[] friendUsername;
	public static String[] friendSignature;
	public static int[] friendStatus;
	public static Bitmap[] friendHeadphoto;
	
	public static int[] friendheadphotoversion;
	public static int[] friendsignatureversion;
	//public static String[] friendIp;
	
	public static String imageDownLoadPath;
	
	public static boolean addFriend  = false;
	public static boolean dataChanged = false;
	
	public Friend(){
		
	}
	
	public Friend(int[] friendUid, String[] friendUsername, String[] friendSignature, int[] friendStatus) {
		// TODO Auto-generated constructor stub
		Friend.friendUid = friendUid;
		Friend.friendUsername = friendUsername;
		Friend.friendSignature = friendSignature;
		Friend.friendStatus = friendStatus;
	}
		
}
