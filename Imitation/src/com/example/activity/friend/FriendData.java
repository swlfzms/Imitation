package com.example.activity.friend;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;

import com.example.beans.DataBaseInstance;
import com.example.beans.Friend;
import com.example.beans.Person;
import com.example.beans.Publish;
import com.example.service.Service;

public class FriendData extends Thread {
	
	private String path;
	private String tableName = Person.username + Publish.friendList;
	
	public FriendData(String path) {
		this.path = path;
	}
	
	/*
	 * 
	 * 
	 * <p>Title: run</p> <p>���ȣ���ȡ���ݿ���friend�����ݵ�ʱ���ǰ���uid����ģ�Ȼ���ź����uid���������JSONArray���鷢�͵�server.
	 * ���ţ���server��ȡ������ͬ���ǰ���uid������JSONArray�� eg: �����ȡ��ǩ������changedSignatureId={0,2,4},�������Friend.uid�����еı�ţ�����������uid������
	 * Ҫͨ��Friend.uid[0],Friend.uid[2],Friend.uid[4]������ȡ������uid��ͬ���ȡͷ��</p>
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		JSONObject sendObject = new JSONObject();
		try {
			
			JSONArray uidJsonArray = new JSONArray();
			for (int i = 0; i < Friend.friendUid.length; i++) {
				uidJsonArray.put(i, Friend.friendUid[i]);
			}
			sendObject.put("friendUid", uidJsonArray);
			
			JSONArray headphotoJsonArray = new JSONArray();
			for (int i = 0; i < Friend.friendUid.length; i++) {
				headphotoJsonArray.put(i, Friend.friendheadphotoversion[i]);
			}
			sendObject.put("friendheadphotoversion", headphotoJsonArray);
			
			JSONArray signatureJsonArray = new JSONArray();
			for (int i = 0; i < Friend.friendUid.length; i++) {
				signatureJsonArray.put(i, Friend.friendsignatureversion[i]);
			}
			sendObject.put("friendsignatureversion", signatureJsonArray);
			
			System.out.println("�������ݣ� " + sendObject);
			Service service = new Service(sendObject, path);
			JSONObject receiveObject = service.getResult();
			
			JSONArray changedSignatureId = (JSONArray) receiveObject.get("changedSignatureId");
			JSONArray changedSignatureContent = (JSONArray) receiveObject.get("changedSignatureContent");
			JSONArray changedHeadphotoId = (JSONArray) receiveObject.get("changedHeadphotoId");
			JSONArray changedphototype = (JSONArray) receiveObject.get("changedphototype");
			System.out.println("�������ݣ�" + changedSignatureId + " " + changedHeadphotoId);
			
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DataBaseInstance.fullPath, null);
			String sql = "";
			for (int i = 0; i < changedSignatureId.length(); i++) {
				System.out.println("��ʼ����" + Friend.friendUsername[changedSignatureId.getInt(i)] + "��ǩ��");
				Friend.friendSignature[changedSignatureId.getInt(i)] = changedSignatureContent
						.getString(changedSignatureId.getInt(i));
				
				sql = "update " + tableName + " set signatureversion="
						+ (Friend.friendsignatureversion[changedSignatureId.getInt(i)] + 1) + ",signature='"
						+ Friend.friendSignature[changedSignatureId.getInt(i)] + "' where uid="
						+ Friend.friendUid[changedSignatureId.getInt(i)] + ";";
				db.execSQL(sql);
				System.out.println(Friend.friendUsername[changedSignatureId.getInt(i)] + "��ǩ���������");
			}
			/*
			 * // �޸�ǩ�� if(changedSignatureId.length()>0){ Friend.dataChanged = true; }
			 */
			
			String type = "";
			int id = 0;
			int index = 0;
			String downFileName = "";
			String headPhotoSavePath = DataBaseInstance.prePath + Person.username + Publish.friendDirectory;
			String saveFileName = "";
			Bitmap bitmap = null;
			for (int i = 0; i < changedHeadphotoId.length(); i++) { // download image
			
				type = changedphototype.getString(i);
				index = changedHeadphotoId.getInt(i);
				id = Friend.friendUid[index];
				System.out.println("��ʼ����" + Friend.friendUsername[index] + "��ͷ��");
				downFileName = Friend.imageDownLoadPath + id + "." + type;
				System.out.println(downFileName);
				
				// �Ƴ�֮ǰ��ͼƬjpg��png
				File tmp = new File(headPhotoSavePath + id + ".jpg");
				if (tmp.exists()) {
					tmp.delete();
				} else {
					tmp = new File(headPhotoSavePath + id + ".png");
					if (tmp.exists()) {
						tmp.delete();
					}
				}
				
				saveFileName = headPhotoSavePath + id + "." + type;
				bitmap = download(downFileName, saveFileName);
				Friend.friendHeadphoto[index] = bitmap;
				
				/*
				 * ContentValues values = new ContentValues(); values.put("headphotoversion",
				 * Friend.friendheadphotoversion[changedHeadphotoId.getInt(i)] + 1); String whereClause = "uid=";
				 * String[] whereArgs = new String[] { "" + Friend.friendUid[changedHeadphotoId.getInt(i)] };
				 */
				sql = "update " + tableName + " set headphotoversion="
						+ (Friend.friendheadphotoversion[changedHeadphotoId.getInt(i)] + 1) + " where uid="
						+ Friend.friendUid[changedHeadphotoId.getInt(i)] + ";";
				db.execSQL(sql);
				System.out.println(Friend.friendUsername[index] + "��ͷ��������");
			}
			// �޸�ͷ��
			/*
			 * if(changedHeadphotoId.length()>0){ Friend.dataChanged = true; }
			 */
			db.close();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Bitmap download(String imageURL, String fileName) {
		Bitmap bitmap = null;
		try {
			URL url = new URL(imageURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(9000);
			int state = conn.getResponseCode();
			if (state == 200) {
				int length = (int) conn.getContentLength();
				InputStream is = conn.getInputStream();
				File file = new File(fileName);
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
				if (length != -1) {
					byte[] image = new byte[length];
					byte[] buf = new byte[512];
					int readLen = 0;
					int destPos = 0;
					while ((readLen = is.read(buf)) != -1) {
						System.arraycopy(buf, 0, image, destPos, readLen);
						destPos += readLen;
						bufferedOutputStream.write(buf, 0, readLen);
					}
					bufferedOutputStream.flush();
					bufferedOutputStream.close();
					bitmap = BitmapFactory.decodeByteArray(image, 0, length);
				}
				return bitmap;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
		
	}
}
