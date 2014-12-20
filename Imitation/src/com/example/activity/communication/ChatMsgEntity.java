
package com.example.activity.communication;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatMsgEntity implements Serializable{
    private static final String TAG = ChatMsgEntity.class.getSimpleName();

    private String name;

    private String date;

    private String text;
    
    private String time;
    
    private boolean bye;

    public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	private boolean isComMeg = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getMsgType() {
        return isComMeg;
    }

    public void setMsgType(boolean isComMsg) {
    	isComMeg = isComMsg;
    }

    
    public boolean isBye() {
		return bye;
	}

	public void setBye(boolean bye) {
		this.bye = bye;
	}

	public ChatMsgEntity() {
    }

    public ChatMsgEntity(String name, String date, String text, boolean isComMsg) {
        super();
        this.name = name;
        this.date = date;
        this.text = text;
        this.isComMeg = isComMsg;
    }

    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	JSONObject sendObject = new JSONObject();
    	try {
			sendObject.put("name", name);
			sendObject.put("text", text);
			sendObject.put("bye", bye);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println(sendObject.toString());
    	return sendObject.toString();
    }
}
