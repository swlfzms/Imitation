package com.example.activity.friendcircle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.ImageView.ScaleType;

import com.example.beans.Screen;
import com.example.imitation.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 14-12-18.
 */
public class FriendCircleActivity extends Activity {

    private BaseAdapter baseAdapter;
    private MyCircleListView listView;

    private List dataList;
    private List tmpList;
    private int count = 10;
    private int num = 0;

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //下拉刷新
                case 0:
                    tmpList.addAll(dataList);
                    dataList.clear();
                    if(tmpList.size()>0)
                        dataList.addAll(tmpList);
                    listView.onRefreshComplete();
                    baseAdapter.notifyDataSetChanged();
                    break;
                //上拉加载
                case 1:
                    if(tmpList.size()>0)
                        dataList.addAll(tmpList);
                    listView.onLoadComplete();
                    listView.setResultSize(tmpList.size());
                    //listView.setSelection(listView.getCount() -1);
                    baseAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    Toast.makeText(FriendCircleActivity.this,"数据已经全部加载完了......",Toast.LENGTH_LONG).show();
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendcirclemainlist);
        tmpList = new ArrayList<String>();
        dataInit();
        initAdapter();
        listView = (MyCircleListView) findViewById(R.id.friendCircleListView);
        listView.setAdapter(baseAdapter);
        listView.setOnRefreshListener(new MyCircleListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MyRefreshListener myRefreshListener = new MyRefreshListener();
                myRefreshListener.execute();
            }
        });

        listView.setOnLoadListener(new MyCircleListView.OnLoadListener() {
            @Override
            public void load() {
                MyLoadListener myLoadListener = new MyLoadListener();
                myLoadListener.execute();
            }
        });
    }

    public void dataInit() {
        dataList = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            dataList.add("new Object " + i);
        }

    }

    private class MyLoadListener extends AsyncTask<Void, Void, Void> {
        //上拉加载数据
        @Override
        protected Void doInBackground(Void... voids) {
            tmpList.clear();
            if(num >= 10){
                FriendCircleActivity.this.myHandler.sendEmptyMessage(2);
            }else{
                for (int i=0;i<3 && num < 10;i++, num++){
                    tmpList.add("drag up with: " + num);
                    //dataList.add("drag up with: " + num);
                }
                if(num == 10){
                    FriendCircleActivity.this.myHandler.sendEmptyMessage(2);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (baseAdapter != null && listView != null) {
                FriendCircleActivity.this.myHandler.sendEmptyMessage(1);
            }
        }
    }

    private class MyRefreshListener extends AsyncTask<Void, Void, Void> {
        //下拉刷新数据
        @Override
        protected Void doInBackground(Void... voids) {
            //do something to update data;
            tmpList.clear();
            tmpList.add("add new Object");
            return null;
        }

        //notify system to update UI display.
        @Override
        protected void onPostExecute(Void result) {
            if (baseAdapter != null && listView != null) {
                FriendCircleActivity.this.myHandler.sendEmptyMessage(0);
            }
        }
    }

    public void initAdapter() {
        baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return dataList.size();
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
                iv.setImageDrawable(getResources().getDrawable(R.drawable.app));
                //iv.setImageBitmap(null);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (96 * rate), (int) (96 * rate));
                lp.setMargins((int) (24 * rate), (int) (15 * rate), (int) (24 * rate), (int) (15 * rate));
                iv.setLayoutParams(lp);
                iv.setScaleType(ScaleType.FIT_CENTER);

                TextView tvTitle = (TextView) convertView.findViewById(R.id.textView_item);
                tvTitle.setText("hello world: " + dataList.get(position));
                tvTitle.setTextColor(getResources().getColor(R.color.friend_listview_tv_title));
                int textsize = (int) (19 * rate);
                tvTitle.setTextSize(textsize); // 29* Screen.width
                tvTitle.setPadding(0, (int) (15 * rate), 0, 0);

                TextView tvContent = (TextView) convertView.findViewById(R.id.textView_content);
                tvContent.setPadding(0, 0, 0, (int) (14 * rate));
                tvContent.setTextSize((int) (15 * rate)); // 24* Screen.width
                tvContent.setText("welcome" + dataList.get(position));
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
