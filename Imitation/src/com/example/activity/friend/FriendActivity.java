package com.example.activity.friend;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.activity.friend.MyListView.OnRefreshListener;
import com.example.beans.Friend;
import com.example.beans.Screen;
import com.example.imitation.R;

public class FriendActivity extends Activity {
		
	private MyAdater adapter;
	private MyListView listView;
	private String databasepath;
	
	private Handler myHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1://登录
				if (adapter == null) {
					adapter = new MyAdater();
					listView.setAdapter(adapter);
					listView.setonRefreshListener(new OnRefreshListener() {
						public void onRefresh() {
							MyTask myTask = new MyTask();
							myTask.execute();
						}
					});
				} else {
				}
				break;
			case 2://增加friend
				MyTask myTask = new MyTask();
				myTask.execute();
				break;
			}
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainlist);
		databasepath = getResources().getString(R.string.database_path);
		listView = (MyListView) findViewById(R.id.listView);
		
		//获取数据
		FriendList friendList = new FriendList(databasepath);
		friendList.setFriendActivity(this);
		friendList.start();
		//监控数据变化
		DataChangeMonitor changeMonitor = new DataChangeMonitor(this);
		changeMonitor.start();
		
		/*
		 * data = new LinkedList<String>(); for (int i = 0; i < 10; i++) { data.add(String.valueOf(i) + ", 我的好友"); }
		 * adapter = new BaseAdapter() { public View getView(int position, View convertView, ViewGroup parent) {
		 * convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item, null); double rate =
		 * Screen.width * 1.0 / 720; convertView.setLayoutParams(new
		 * AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, (int) (127 * rate)));
		 * 
		 * // iv.setLayoutParams(new LinearLayout.LayoutParams(96 * Screen.width / 720, 96 * Screen.width / 720)); // //
		 * 图片显示大小 ImageView iv = (ImageView) convertView.findViewById(R.id.imageView_item);
		 * iv.setImageDrawable(getResources().getDrawable(R.drawable.app)); LinearLayout.LayoutParams lp = new
		 * LinearLayout.LayoutParams((int) (96 * rate), (int) (96 * rate)); lp.setMargins((int) (24 * rate), (int) (15 *
		 * rate), (int) (24 * rate), (int) (15 * rate)); iv.setLayoutParams(lp); iv.setScaleType(ScaleType.FIT_CENTER);
		 * 
		 * TextView tvTitle = (TextView) convertView.findViewById(R.id.textView_item);
		 * tvTitle.setText(data.get(position));
		 * tvTitle.setTextColor(getResources().getColor(R.color.friend_listview_tv_title)); int textsize = (int) (19 *
		 * rate); tvTitle.setTextSize(textsize); // 29* Screen.width Toast.makeText(FriendActivity.this, "" + textsize,
		 * Toast.LENGTH_LONG).show(); tvTitle.setPadding(0, (int) (15 * rate), 0, 0);
		 * 
		 * TextView tvContent = (TextView) convertView.findViewById(R.id.textView_content); tvContent.setPadding(0, 0,
		 * 0, (int) (14 * rate)); tvContent.setTextSize((int) (15 * rate)); // 24* Screen.width
		 * tvContent.setText(data.get(position));
		 * tvContent.setTextColor(getResources().getColor(R.color.friend_listview_tv_content));
		 * 
		 * return convertView; }
		 * 
		 * public long getItemId(int position) { return position; }
		 * 
		 * public Object getItem(int position) { return data.get(position); }
		 * 
		 * public int getCount() { return data.size(); } };
		 */
		
	}
	
	public Handler getMyHandler() {
		return myHandler;
	}
	
	class MyAdater extends BaseAdapter {
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item, null);
			double rate = Screen.width * 1.0 / 720;
			convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT,
					(int) (127 * rate)));
			
			// iv.setLayoutParams(new LinearLayout.LayoutParams(96 * Screen.width / 720, 96 * Screen.width / 720));
			// // 图片显示大小
			ImageView iv = (ImageView) convertView.findViewById(R.id.imageView_item);
			//iv.setImageDrawable(getResources().getDrawable(R.drawable.app));
			iv.setImageBitmap(Friend.friendHeadphoto[position]);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (96 * rate), (int) (96 * rate));
			lp.setMargins((int) (24 * rate), (int) (15 * rate), (int) (24 * rate), (int) (15 * rate));
			iv.setLayoutParams(lp);
			iv.setScaleType(ScaleType.FIT_CENTER);
			
			TextView tvTitle = (TextView) convertView.findViewById(R.id.textView_item);
			tvTitle.setText(Friend.friendUsername[position]);
			tvTitle.setTextColor(getResources().getColor(R.color.friend_listview_tv_title));
			int textsize = (int) (19 * rate);
			tvTitle.setTextSize(textsize); // 29* Screen.width			
			tvTitle.setPadding(0, (int) (15 * rate), 0, 0);
			
			TextView tvContent = (TextView) convertView.findViewById(R.id.textView_content);
			tvContent.setPadding(0, 0, 0, (int) (14 * rate));
			tvContent.setTextSize((int) (15 * rate)); // 24* Screen.width
			tvContent.setText(Friend.friendSignature[position]);
			tvContent.setTextColor(getResources().getColor(R.color.friend_listview_tv_content));
			
			return convertView;
		}
		
		public long getItemId(int position) {
			return position;
		}
		
		public Object getItem(int position) {
			return null;
		}
		
		public int getCount() {
			return Friend.friendUid.length;
		}
	}
	
	private class MyTask extends AsyncTask<Void, Void, Void> {
		
		protected Void doInBackground(Void... params) {
			try {
				FriendList friendList = new FriendList(databasepath);
				friendList.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// data.addFirst("刷新后的内容");
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if (adapter != null) {
				adapter.notifyDataSetChanged();
				listView.onRefreshComplete();
			}
		}
		
	}
	
}
