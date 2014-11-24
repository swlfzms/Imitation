package com.example.activity.main;

import java.io.File;
import java.util.logging.Logger;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;

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
	  * databaseInit("�������ݿ�")
	  * TODO(����������������������� �C ��ѡ)
	  * TODO(�����������������ִ������ �C ��ѡ)
	  * TODO(�����������������ʹ�÷��� �C ��ѡ)
	  * TODO(�����������������ע������ �C ��ѡ)
	  *
	  * @Title: databaseInit
	  * @Description: TODO
	  * @param     �趨�ļ�
	  * @return void    ��������
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
	  * directoryIsExist(������һ�仰�����������������)
	  * TODO(����������������������� �C ��ѡ)
	  * TODO(�����������������ִ������ �C ��ѡ)
	  * TODO(�����������������ʹ�÷��� �C ��ѡ)
	  * TODO(�����������������ע������ �C ��ѡ)
	  *
	  * @Title: directoryIsExist
	  * @Description: TODO
	  * @param @return    �趨�ļ�
	  * @return boolean    ��������
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
	  * createDirectory(������һ�仰�����������������)
	  * TODO(����������������������� �C ��ѡ)
	  * TODO(�����������������ִ������ �C ��ѡ)
	  * TODO(�����������������ʹ�÷��� �C ��ѡ)
	  * TODO(�����������������ע������ �C ��ѡ)
	  *
	  * @Title: createDirectory
	  * @Description: TODO
	  * @param     �趨�ļ�
	  * @return void    ��������
	  * @throws
	 */
	public void createDirectory() {
		File dir = new File(DataBaseInstance.path);
		if (!dir.exists())
			dir.mkdirs();
	}
			
}
