/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.activity.main.function;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.activity.communication.ChatActivity;
import com.example.activity.communication.ChatMsgEntity;
import com.example.activity.communication.CurrentChatMonitor;
import com.example.beans.Communication;
import com.example.beans.Friend;

/**
 * 
 * @author Administrator
 */
public class ReceiverServerThread implements Runnable {
	
	private int port = 9084;
	private ServerSocket serverSocket;
	private ExecutorService executorService; // 线程池
	private final int POOL_SIZE = 4; // 单个CPU时线程池中工作线程的数目
	public static boolean exit;
	private static final int NOTIFICATION_ID = 0x123;
	private MainFunctionActivity mainFunctionActivity;
	private String className = ReceiverServerThread.class.getName();
	
	public ReceiverServerThread(MainFunctionActivity mainFunctionActivity) throws IOException {
		this.mainFunctionActivity = mainFunctionActivity;
		serverSocket = new ServerSocket(port);
		// 创建线程池
		// Runtime的availableProcessors()方法返回当前系统的CPU的数目
		// 系统的CPU越多，线程池中工作线程的数目也越多
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
		System.out.println("服务器启动");
		
	}
	
	public void run() {
		Looper.prepare();
		while (!exit) {
			System.out.println("开始socket监听");
			Socket socket = null;
			try {
				socket = serverSocket.accept(); // 监听客户请求, 处于阻塞状态.
				// 接受一个客户请求,从线程池中拿出一个线程专门处理该客户.
				executorService.execute(new CommunicationHandler(socket));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Looper.loop();
	}
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		System.out.println("ReceiverServerThread 销毁");
		exit = true;
		super.finalize();
	}
	
	class CommunicationHandler implements Runnable {
		
		private Socket socket;
		private BufferedWriter bw = null;
		
		public CommunicationHandler(Socket socket) {
			this.socket = socket;
		}
		
		private PrintWriter getWriter(Socket socket) throws IOException {
			OutputStream socketOut = socket.getOutputStream();
			return new PrintWriter(new OutputStreamWriter(socketOut, "GB2312"), true);
		}
		
		private BufferedReader getReader(Socket socket) throws IOException {
			InputStream socketIn = socket.getInputStream();
			return new BufferedReader(new InputStreamReader(socketIn, "GB2312"));
		}
		
		public String echo(String msg) {
			return "echo:" + msg;
		}
		
		public void run() {
			try {
				Looper.prepare();
				
				System.out.println("New connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
				
				BufferedReader br = getReader(socket);
				PrintWriter pw = getWriter(socket);
				
				String ip = socket.getInetAddress().toString();
				boolean first = true;
				String msg = null;
				
				while ((msg = br.readLine()) != null) {
					
					System.out.println(msg);
					
					JSONObject receiveObject = new JSONObject(msg);
					String name = receiveObject.getString("name");
					String message = receiveObject.getString("text");
					
					boolean bye = receiveObject.getBoolean("bye");
					
					if (bye) { // close the chat room
						break;
					} else {
						Friend.map.put(name, ip);
						ChatMsgEntity chatMsgEntity = new ChatMsgEntity();
						chatMsgEntity.setText(message);
						chatMsgEntity.setName(name);
						chatMsgEntity.setMsgType(true);
						chatMsgEntity.setDate(getDate());
						Log.e(className, "当前activity: " + mainFunctionActivity.getCurrentActivityName());
						Log.e(className, "当前chatGuy:　" + ChatActivity.currentGuy);
						
						if (bw == null)
							bw = new BufferedWriter(new FileWriter(
									new File(Communication.chatDirectory + name + ".txt"),true));
						bw.write("true^^" + message + "^^" + getDate() + "\n");
						Log.e(className, "写入消息：" + "true^^" + message + "^^" + getDate());
						bw.flush();
						if (mainFunctionActivity.getCurrentActivityName().equals(
								"com.example.activity.communication.ChatActivity")) {
							
							if (name.equals(ChatActivity.currentGuy)) {
								Message sendMsg = new Message();
								sendMsg.what = 1;
								Bundle data = new Bundle();
								data.putSerializable("entity", chatMsgEntity);
								sendMsg.setData(data);
								mainFunctionActivity.getHandler().sendMessage(sendMsg);
								
								CurrentChatMonitor.chatMsgEntity = chatMsgEntity;
								CurrentChatMonitor.newMessage = true;
							} else {
								Message sendMsg = new Message();
								sendMsg.what = 0;
								Bundle data = new Bundle();
								data.putSerializable("entity", chatMsgEntity);
								sendMsg.setData(data);
								mainFunctionActivity.getHandler().sendMessage(sendMsg);
							}
						} else {
							Log.e(className, "不会吧，到了这里？？？？？");
							Message sendMsg = new Message();
							sendMsg.what = 0;
							Bundle data = new Bundle();
							data.putSerializable("entity", chatMsgEntity);
							sendMsg.setData(data);
							mainFunctionActivity.getHandler().sendMessage(sendMsg);
						}
						// mAdapter.notifyDataSetChanged();
						// Intent intent = new Intent();
					}
				}
				if (bw != null) {					
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (bw != null) {
						Log.e(className, "bw 关闭");						
						bw.close();
					}
					if (socket != null) {
						Log.e(className, "socket 关闭");
						socket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Looper.loop();
		}
		
		private String getDate() {
			Calendar c = Calendar.getInstance();
			
			String year = String.valueOf(c.get(Calendar.YEAR));
			String month = String.valueOf(c.get(Calendar.MONTH));
			String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
			String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
			String mins = String.valueOf(c.get(Calendar.MINUTE));
			
			StringBuffer sbBuffer = new StringBuffer();
			sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins);
			
			return sbBuffer.toString();
		}
		
		@Override
		protected void finalize() throws Throwable {
			// TODO Auto-generated method stub
			if (bw != null) {				
				bw.close();
			}
			Log.e(className, "监控消息接收进程关闭");
			super.finalize();
		}
	}
	
}
