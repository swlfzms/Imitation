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
		switch(position){//��������
		case 0:
			break;
		case 1://����ǩ��
			final EditText etSignature = new EditText(publishActivity);				
			//Toast.makeText(PublishActivity.this, "click me", Toast.LENGTH_SHORT).show();
			new AlertDialog.Builder(publishActivity).setTitle("����״̬")
			.setMessage("���������ǩ��").setIcon(R.drawable.p_signature)
			.setView(etSignature)
			.setPositiveButton("ȷ��", new OnClickListener() {					
				@Override
				public void onClick(DialogInterface dialog, int which) {
					signature = etSignature.getText().toString();
					MyTask myTask = new MyTask(signature, publishActivity,"signature");
					myTask.execute();
				}
			}).setNegativeButton("ȡ��", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub					
				}
			}).show();
			break;
		case 2://���Һ���
			final EditText etFriendName = new EditText(publishActivity);				
			//Toast.makeText(PublishActivity.this, "click me", Toast.LENGTH_SHORT).show();
			new AlertDialog.Builder(publishActivity).setTitle("�������")
			.setMessage("������Ҫ���ҵĺ�������").setIcon(R.drawable.p_signature)
			.setView(etFriendName)
			.setPositiveButton("ȷ��", new OnClickListener() {					
				@Override
				public void onClick(DialogInterface dialog, int which) {
					friendName = etFriendName.getText().toString();
					MyTask myTask = new MyTask(friendName, publishActivity,"addfriend");
					myTask.execute();
				}
			}).setNegativeButton("ȡ��", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub					
				}
			}).show();
			break;
		case 3://����ͷ��
			Intent inte = new Intent();
			inte.setType("image/*");
			//ʹ��Intent.ACTION_GET_CONTENT���Action 
			inte.setAction(Intent.ACTION_GET_CONTENT);
			 //ȡ����Ƭ�󷵻ر����� 
			publishActivity.startActivityForResult(inte, 1);
			break;
		default:				
			break;
		}
	}	

}
