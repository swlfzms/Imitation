package com.example.beans;

public class RegisterUser {
	
	public static String username;
	public static String password;
	public static String email;
	public static String registerURL;
	
	public RegisterUser() {
		// TODO Auto-generated constructor stub
	}
	
	public RegisterUser(String username, String password, String email) {
		// TODO Auto-generated constructor stub
		RegisterUser.username = username;
		RegisterUser.password = password;
		RegisterUser.email = email;
	}
	
	public static String getUsername() {
		return username;
	}
	
	public static void setUsername(String username) {
		RegisterUser.username = username;
	}
	
	public static String getPassword() {
		return password;
	}
	
	public static void setPassword(String password) {
		RegisterUser.password = password;
	}
	
	public static String getEmail() {
		return email;
	}
	
	public static void setEmail(String email) {
		RegisterUser.email = email;
	}

	public static String getRegisterURL() {
		return registerURL;
	}

	public static void setRegisterURL(String registerURL) {
		RegisterUser.registerURL = registerURL;
	}
	
}
