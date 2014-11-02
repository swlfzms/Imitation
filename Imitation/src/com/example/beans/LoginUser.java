package com.example.beans;

import java.io.Serializable;

import com.example.tools.Encryption;

public class LoginUser implements Serializable{
	
	public static String username;
	public static String password;
	public static String loginUrl;
	public static String logoutUrl;
	public static int id;
	
	public LoginUser(String username, String password){
		
		LoginUser.username = username;
		LoginUser.password = password;
	}
	
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

	public static String getLoginUrl() {
		return loginUrl;
	}

	public static void setLoginUrl(String loginUrl) {
		LoginUser.loginUrl = loginUrl;
	}

	public static String getLogoutUrl() {
		return logoutUrl;
	}

	public static void setLogoutUrl(String logoutUrl) {
		LoginUser.logoutUrl = logoutUrl;
	}

	public static int getId() {
		return id;
	}

	public static void setId(int id) {
		LoginUser.id = id;
	}
		
}
