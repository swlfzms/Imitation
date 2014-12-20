package com.example.activity.main;

import java.io.File;
import java.util.logging.Logger;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;

import com.example.beans.Communication;
import com.example.beans.DataBaseInstance;

public class CreateRootDirectory extends Thread {
	
	private SQLiteDatabase db;
	public final Logger DebugLogger = Logger.getLogger("Debug." + CreateRootDirectory.class.getName());
	
	public void run() {
		Looper.prepare();
		createDirectory();
		databaseInit();
		Looper.loop();
	}
	
	/***
	 * 
	  * databaseInit("创建数据库")
	  * TODO(这里描述这个方法适用条件 – 可选)
	  * TODO(这里描述这个方法的执行流程 – 可选)
	  * TODO(这里描述这个方法的使用方法 – 可选)
	  * TODO(这里描述这个方法的注意事项 – 可选)
	  *
	  * @Title: databaseInit
	  * @Description: TODO
	  * @param     设定文件
	  * @return void    返回类型
	  * @throws
	 */
	private void databaseInit() {
		// TODO Auto-generated method stub
		
		System.out.println("create database");
		db = SQLiteDatabase.openOrCreateDatabase(DataBaseInstance.fullPath, null);
		System.out.println("database have been created");
		db.close();
	}
	
	/***
	 * 
	  * directoryIsExist(这里用一句话描述这个方法的作用)
	  * TODO(这里描述这个方法适用条件 – 可选)
	  * TODO(这里描述这个方法的执行流程 – 可选)
	  * TODO(这里描述这个方法的使用方法 – 可选)
	  * TODO(这里描述这个方法的注意事项 – 可选)
	  *
	  * @Title: directoryIsExist
	  * @Description: TODO
	  * @param @return    设定文件
	  * @return boolean    返回类型
	  * @throws
	 */
	public boolean directoryIsExist() {
		File dir = new File(DataBaseInstance.path);
		if (dir.exists()) {
			return true;
		}
		return false;
	}
	
	/***
	 * 
	  * createDirectory(这里用一句话描述这个方法的作用)
	  * TODO(这里描述这个方法适用条件 – 可选)
	  * TODO(这里描述这个方法的执行流程 – 可选)
	  * TODO(这里描述这个方法的使用方法 – 可选)
	  * TODO(这里描述这个方法的注意事项 – 可选)
	  *
	  * @Title: createDirectory
	  * @Description: TODO
	  * @param     设定文件
	  * @return void    返回类型
	  * @throws
	 */
	public void createDirectory() {
		File dir = new File(DataBaseInstance.path);
		if (!dir.exists())
			dir.mkdirs();
		dir = new File(Communication.chatDirectory);
		if(!dir.exists()){
			dir.mkdirs();
		}
	}
			
}
