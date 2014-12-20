package com.example.socket;
/**
 *
 * @author Administrator
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.example.beans.Communication;
import com.example.beans.Person;

public class MessageClient {

    //private String host = "222.201.101.15";
    //private String host = "192.168.215.201";
    //private String host = "192.168.207.31";
    //private String host="192.168.207.39";
    //private int port = 8008;
	
    private Socket socket;
    private PrintWriter pw;
    private BufferedReader br;
    private String className = MessageClient.class.getName();
    
    public MessageClient(String host) throws UnknownHostException, IOException{
        socket = new Socket(host, Communication.port);
        Log.e(className, "new socket to: " + host + ":" + Communication.port);
        pw = getWriter(socket);
        br = getReader(socket);
    }
    

    private PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream socketOut = socket.getOutputStream();
        // return new PrintWriter(socketOut,true);
        return new PrintWriter(new OutputStreamWriter(socketOut, "GB2312"), true);
    }

    private BufferedReader getReader(Socket socket) throws IOException {
        InputStream socketIn = socket.getInputStream();
        //return new BufferedReader(new InputStreamReader(socketIn));
        return new BufferedReader(new InputStreamReader(socketIn, "GB2312"));
    }

    public void send(String msg) throws IOException {
        pw.println(msg);
    }

    public void send(byte[] b) throws IOException {
        socket.getOutputStream().write(b);
    }

    public String receive() throws IOException {
        String msg = br.readLine();
        return msg;
    }

    public void close() throws IOException {    	
    	JSONObject sendObject = new JSONObject();
    	try {
			sendObject.put("name", Person.username);
			sendObject.put("text", "bye from system");
	    	sendObject.put("bye", true);
	    	send(sendObject.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	    	
        socket.close();
    }
}
