package com.example.activity.friendcircle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.ImageView.ScaleType;

import com.example.beans.Screen;
import com.example.imitation.R;

/**
 * Created by Administrator on 14-12-18.
 */
public class FriendCircleActivity extends Activity{

    private BaseAdapter baseAdapter;
    private MyCircleListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendcirclemainlist);
        initAdapter();
        listView = (MyCircleListView)findViewById(R.id.friendCircleListView);
        listView.setOnRefreshListener(new MyCircleListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MyRefreshListener myRefreshListener = new MyRefreshListener();
                myRefreshListener.execute();
            }
        });
    }

    private class MyRefreshListener extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            //do something to update data;
            return null;
        }
        //notify system to update UI display.
        @Override
        protected void onPostExecute(Void result) {
            baseAdapter.notifyDataSetChanged();
            listView.onRefreshComplete();
        }
    }
    public void initAdapter(){
        baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup viewGroup) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item, null);
                double rate = Screen.width * 1.0 / 720;
                convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT,
                        (int) (127 * rate)));

                // iv.setLayoutParams(new LinearLayout.LayoutParams(96 * Screen.width / 720, 96 * Screen.width / 720));
                // // 图片显示大小
                ImageView iv = (ImageView) convertView.findViewById(R.id.imageView_item);
                // iv.setImageDrawable(getResources().getDrawable(R.drawable.app));
                iv.setImageBitmap(null);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (96 * rate), (int) (96 * rate));
                lp.setMargins((int) (24 * rate), (int) (15 * rate), (int) (24 * rate), (int) (15 * rate));
                iv.setLayoutParams(lp);
                iv.setScaleType(ScaleType.FIT_CENTER);

                TextView tvTitle = (TextView) convertView.findViewById(R.id.textView_item);
                tvTitle.setText("hello world");
                tvTitle.setTextColor(getResources().getColor(R.color.friend_listview_tv_title));
                int textsize = (int) (19 * rate);
                tvTitle.setTextSize(textsize); // 29* Screen.width
                tvTitle.setPadding(0, (int) (15 * rate), 0, 0);

                TextView tvContent = (TextView) convertView.findViewById(R.id.textView_content);
                tvContent.setPadding(0, 0, 0, (int) (14 * rate));
                tvContent.setTextSize((int) (15 * rate)); // 24* Screen.width
                tvContent.setText("welcome");
                tvContent.setTextColor(getResources().getColor(R.color.friend_listview_tv_content));

                final int tmp = position;
                convertView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        //Toast.makeText(FriendActivity.this, Friend.friendUsername[tmp], Toast.LENGTH_LONG).show();
                    }
                });

                return convertView;
            }
        };
    }
}
