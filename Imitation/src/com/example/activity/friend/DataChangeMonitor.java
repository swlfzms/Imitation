package com.example.activity.friend;

import com.example.beans.Friend;

public class DataChangeMonitor extends Thread{
	
	private FriendActivity friendActivity;
	
	public DataChangeMonitor(FriendActivity friendActivity){
		this.friendActivity = friendActivity;
	}
	
	@Override
	public void run(){
		
		while(true){
			//更改数据
			if(Friend.dataChanged){				
				this.friendActivity.getMyHandler().sendEmptyMessage(2);
				Friend.dataChanged = false;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
