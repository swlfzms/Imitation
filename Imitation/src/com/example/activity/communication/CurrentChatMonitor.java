package com.example.activity.communication;

import android.os.Bundle;
import android.os.Looper;
import android.os.Message;

public class CurrentChatMonitor implements Runnable {
	
	private boolean exit;
	private ChatActivity chatActivity;
	public static boolean newMessage;
	public static ChatMsgEntity chatMsgEntity;
	
	public CurrentChatMonitor(ChatActivity chatActivity) {
		// TODO Auto-generated constructor stub
		this.chatActivity = chatActivity;
	}
	
	public void setExit(boolean exit) {
		this.exit = exit;
	}

	@Override
	public void run() {
		Looper.prepare();
		while (!exit) {
			if(newMessage){
				newMessage = false;				
				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putSerializable("chatMsgEntity", chatMsgEntity);
				message.setData(bundle);
				chatActivity.getMyHandler().sendMessage(message);
			}			
		}
		Looper.loop();
	}
}
