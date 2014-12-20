package com.example.activity.main.function;

import com.example.activity.diary.DiaryActivity;
import com.example.activity.friend.FriendActivity;
import com.example.activity.publish.PublishActivity;
import com.example.activity.visitor.VisitorActivity;


public class Constant {

	public static String mTextviewArray[] = {"我的好友", "最近访客", "快速发布", "我的日志"};
	
	public static Class mTabClassArray[]= {FriendActivity.class,
		VisitorActivity.class,
		PublishActivity.class,
		DiaryActivity.class};
}
