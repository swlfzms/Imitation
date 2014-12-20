package com.example.beans;

public class Person {
	
	public static String username;
	public static String password;
	public static int id;
	public static String ip;
	public static String headPhotoPath;
	
	public static String getUsername() {
		return username;
	}
	public static void setUsername(String username) {
		Person.username = username;
	}
	public static String getPassword() {
		return password;
	}
	public static void setPassword(String password) {
		Person.password = password;
	}
	public static int getId() {
		return id;
	}
	public static void setId(int id) {
		Person.id = id;
	}	
		
}
