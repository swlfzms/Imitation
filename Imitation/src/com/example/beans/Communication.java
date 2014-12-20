package com.example.beans;

public class Communication {
	
	public static int port;
	public static String url;
	public static String chatDirectory;
	public static String voiceSavePath;
	
	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		Communication.port = port;
	}
	
}
