package com.example.activity.publish;

import java.io.FileNotFoundException;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;

import com.example.imitation.R;

public class MyClickListener implements OnItemClickListener{
	
	
	private PublishActivity publishActivity;
	private String signature;
	private String friendName;
	
	public MyClickListener(PublishActivity publishActivity){
		this.publishActivity = publishActivity;
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position,
			long arg3) {
		// TODO Auto-generated method stub
		switch(position){//看看别人
		case 0:
			break;
		case 1://发表签名
			final EditText etSignature = new EditText(publishActivity);				
			//Toast.makeText(PublishActivity.this, "click me", Toast.LENGTH_SHORT).show();
			new AlertDialog.Builder(publishActivity).setTitle("更新状态")
			.setMessage("请输入你的签名").setIcon(R.drawable.p_signature)
			.setView(etSignature)
			.setPositiveButton("确定", new OnClickListener() {					
				@Override
				public void onClick(DialogInterface dialog, int which) {
					signature = etSignature.getText().toString();
					MyTask myTask = new MyTask(signature, publishActivity,"signature");
					myTask.execute();
				}
			}).setNegativeButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub					
				}
			}).show();
			break;
		case 2://查找好友
			final EditText etFriendName = new EditText(publishActivity);				
			//Toast.makeText(PublishActivity.this, "click me", Toast.LENGTH_SHORT).show();
			new AlertDialog.Builder(publishActivity).setTitle("随便找找")
			.setMessage("请输入要查找的好友名字").setIcon(R.drawable.p_signature)
			.setView(etFriendName)
			.setPositiveButton("确定", new OnClickListener() {					
				@Override
				public void onClick(DialogInterface dialog, int which) {
					friendName = etFriendName.getText().toString();
					MyTask myTask = new MyTask(friendName, publishActivity,"addfriend");
					myTask.execute();
				}
			}).setNegativeButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub					
				}
			}).show();
			break;
		case 3://更换头像
			Intent inte = new Intent();
			inte.setType("image/*");
			//使用Intent.ACTION_GET_CONTENT这个Action 
			inte.setAction(Intent.ACTION_GET_CONTENT);
			 //取得相片后返回本画面 
			publishActivity.startActivityForResult(inte, 1);
			break;
		default:				
			break;
		}
	}	

}
