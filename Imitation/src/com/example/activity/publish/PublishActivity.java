package com.example.activity.publish;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.activity.login.LoginActivity;
import com.example.beans.Person;
import com.example.imitation.R;
import com.example.service.Service;

public class PublishActivity extends Activity {
	
	private int[] image = new int[] { R.drawable.p_look, R.drawable.p_signature, R.drawable.p_search,
			R.drawable.p_shoot };
	private String[] info = new String[] { "看看别人", "发表签名", "查找好友", "更换头像" };
	private MyClickListener myClickListener;
	
	private String signatureUrl;
	private String addFriendUrl;
	
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish);
		signatureUrl = getResources().getString(R.string.url_publish_signatureIp);
		addFriendUrl = getResources().getString(R.string.url_publish_addfriendIp);
		
		ListView list = (ListView) findViewById(R.id.lvpublish);
		list.setAdapter(new MyAdapter());
		myClickListener = new MyClickListener(this);
		list.setOnItemClickListener(myClickListener);
	}
	
	public String getSignatureUrl() {
		return signatureUrl;
	}
	
	public String getAddFriendUrl() {
		return addFriendUrl;
	}
	
	private class MyAdapter extends BaseAdapter {
		
		private Context context;
		
		public MyAdapter() {
			
		}
		
		public MyAdapter(Context context) {
			this.context = context;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return image.length;
		}
		
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LinearLayout ll = new LinearLayout(PublishActivity.this);
			// ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			ll.setOrientation(LinearLayout.HORIZONTAL);
			ll.setGravity(Gravity.CENTER);
			ll.setBackgroundColor(Color.WHITE);
			
			ImageView iv = new ImageView(PublishActivity.this);
			iv.setImageDrawable(getResources().getDrawable(image[position]));
			
			TextView tv = new TextView(PublishActivity.this);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
			tv.setText(info[position]);
			tv.setTextColor(getResources().getColor(R.color.publishlistTitle));
			tv.setPadding(24, 0, 0, 0);
			
			LinearLayout tvll = new LinearLayout(PublishActivity.this);
			tvll.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
			tvll.setOrientation(LinearLayout.HORIZONTAL);
			tvll.setGravity(Gravity.LEFT | Gravity.CENTER);
			tvll.addView(tv);
			
			ll.addView(iv);
			ll.addView(tvll);
			return ll;
		}
		
	}
	
	public byte[] Bitmap2Bytes(Bitmap bm, String type) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (type.equals("png"))
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		else if (type.equals("jpg"))
			bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}
	
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	
	// 更换头像
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (resultCode == RESULT_OK) {
			Uri uri = data.getData();
			ContentResolver cr = this.getContentResolver();
			
			String[] proj = { MediaStore.Images.Media.DATA };
			// 好像是android多媒体数据库的封装接口，具体的看Android文档
			Cursor cursor = managedQuery(uri, proj, null, null, null);
			// 按我个人理解 这个是获得用户选择的图片的索引值
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			// 将光标移至开头 ，这个很重要，不小心很容易引起越界
			cursor.moveToFirst();
			// 最后根据索引值获取图片路径
			String path = cursor.getString(column_index);
			try {
				// 显示循环进度圈
				progressDialog = ProgressDialog.show(PublishActivity.this, "请稍候", "图片玩命上传中...", true, true);
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.setCancelable(false);
				progressDialog.show();
				
				Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
				UploadImage uploadImage = new UploadImage(path, bitmap);
				uploadImage.start();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	class UploadImage extends Thread {
		
		private String imagePath;
		private Bitmap bitmap;
		
		public UploadImage(String imagePath, Bitmap bitmap) {
			// TODO Auto-generated constructor stub
			this.imagePath = imagePath;
			this.bitmap = bitmap;
		}
		
		@Override
		public void run() {
			Looper.prepare();
			try {							
				int index = imagePath.lastIndexOf(".");
				String type = imagePath.substring(index + 1);
				if (!type.equals("png") && !type.equals("jpg")) {
					Toast.makeText(PublishActivity.this, "图片格式只可以是png和jpg", Toast.LENGTH_LONG).show();
					return;
				}
				
				byte[] img = Bitmap2Bytes(bitmap, type);
				String image = bytesToHexString(img);
				System.out.println(image);
				
				JSONObject sendObject = new JSONObject();
				sendObject.put("id", Person.id);
				sendObject.put("length", img.length);
				sendObject.put("type", type);
				sendObject.put("image", image);
				
				String url_publish_imageuploadpath = getResources().getString(R.string.url_publish_imageuploadpath);
				System.out.println(url_publish_imageuploadpath);
				Service service = new Service(sendObject, url_publish_imageuploadpath);
				JSONObject receiveObject = service.getResult();
				progressDialog.cancel();
				
				boolean result = receiveObject.getBoolean("result");
				String message = receiveObject.getString("message");
				System.out.println("收到的信息:"+message);								
				// 得到结果，取消进度条				
				if (result) { // 成功并在本地更换头像。
					String headPhotoSavePath = android.os.Environment.getExternalStorageDirectory() + "/Imitation/"
							+ Person.username + "/HeadPhoto/headphoto0";
					// 移除之前的图片jpg和png
					File tmp = new File(headPhotoSavePath + ".jpg");
					if (tmp.exists()) {
						tmp.delete();
					} else {
						tmp = new File(headPhotoSavePath + ".png");
						if (tmp.exists()) {
							tmp.delete();
						}
					}
					
					// 本地保存图片
					tmp = new File(headPhotoSavePath + "." + type);
					FileOutputStream fileOutputStream = new FileOutputStream(tmp);
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
					bufferedOutputStream.write(img, 0, img.length);
					bufferedOutputStream.flush();
					bufferedOutputStream.close();					
				} else { // 失败
				}				
				Toast.makeText(PublishActivity.this, message, Toast.LENGTH_LONG).show();
			} catch (FileNotFoundException e) {
				Log.e("Exception", e.getMessage(), e);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Looper.loop();
		}
	}
}
