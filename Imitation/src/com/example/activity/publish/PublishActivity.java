package com.example.activity.publish;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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

import com.example.imitation.R;

public class PublishActivity extends Activity {
	
	private int[] image = new int[] { R.drawable.p_look, R.drawable.p_signature, R.drawable.p_search,
			R.drawable.p_shoot };
	private String[] info = new String[] { "看看别人", "发表签名", "查找好友", "更换头像" };
	private MyClickListener myClickListener;
	
	private String signatureUrl;
	private String addFriendUrl;
	
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
			//ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
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
			tvll.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT));
			tvll.setOrientation(LinearLayout.HORIZONTAL);
			tvll.setGravity(Gravity.LEFT|Gravity.CENTER);			
			tvll.addView(tv);
			
			ll.addView(iv);
			ll.addView(tvll);
			return ll;
		}
		
	}
}
