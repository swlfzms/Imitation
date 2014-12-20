package com.example.activity.communication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beans.Communication;
import com.example.beans.Person;
import com.example.imitation.R;
import com.example.service.Service;
import com.example.socket.MessageClient;

public class ChatActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	
	private TextView tvContact;
	private Button mBtnSend;
	private TextView mBtnRcd;
	private Button mBtnBack;
	private EditText mEditTextContent;
	private RelativeLayout mBottom;
	private ListView mListView;
	private ChatMsgViewAdapter mAdapter;
	private List<ChatMsgEntity> mDataArrays;
	private boolean isShosrt = false;
	private LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding, voice_rcd_hint_tooshort;
	private ImageView img1, sc_img1;
	private SoundMeter mSensor;
	private View rcChat_popup;
	private LinearLayout del_re;
	private ImageView chatting_mode_btn, volume;
	private boolean btn_vocie = false;
	private int flag = 1;
	private Handler mHandler = new Handler();
	private String voiceName;
	private long startVoiceT, endVoiceT;
	private MessageClient messageClient;
	private int id;
	private String contact;
	private String ip;
	private String className = ChatActivity.class.getName();
	private CurrentChatMonitor currentChatMonitor;
	public static String currentGuy;
	private BufferedWriter bw;
	
	private Handler myHandler = new Handler(){
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			ChatMsgEntity chatMsgEntity = (ChatMsgEntity) data.get("chatMsgEntity");
			mDataArrays.add(chatMsgEntity);
			mAdapter.notifyDataSetChanged();
			setListViewToLastItem();
		}		
	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chat);
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("bundle");
		contact = bundle.getString("contact");
		id = bundle.getInt("id");
		
		initView();
		initData();
		
		tvContact.setText(contact);
		currentGuy = contact;
		
		// Toast.makeText(this, "contact: "+contact +"id: "+id, Toast.LENGTH_LONG).show();
		Log.e(className, "contact: " + contact + "id: " + id);
		Thread socketThread = new Thread(SocketInit);
		socketThread.start();
		
		currentChatMonitor = new CurrentChatMonitor(this);
		Thread currentChatMonitorThread =  new Thread(currentChatMonitor);
		currentChatMonitorThread.start();
	}
	
	public Handler getMyHandler() {
		return myHandler;
	}

	Runnable SocketInit = new Runnable() {
		
		@Override
		public void run() {
			Looper.prepare();
			File file = new File(Communication.chatDirectory + contact + ".txt");
			if (!file.exists()) {
				try {
					file.createNewFile();
					//Toast.makeText(ChatActivity.this, file.getName() + "不存在", Toast.LENGTH_LONG).show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					bw = new BufferedWriter(new FileWriter(file, true));
					String tmp = "";
					if (mDataArrays != null)
						mDataArrays.clear();
					// Toast.makeText(ChatActivity.this, "开始读取文件", Toast.LENGTH_SHORT).show();
					Log.e(className, "开始读取文件" + file.getName());
					while ((tmp = br.readLine()) != null) {
						Log.e(className, "文件内容为：" + tmp);
						String[] content = tmp.split("\\^\\^");
						ChatMsgEntity chatMsgEntity = new ChatMsgEntity();
						if (content[0].equals("true")) {
							chatMsgEntity.setMsgType(true);
						} else {
							chatMsgEntity.setMsgType(false);
						}
						chatMsgEntity.setText(content[1]);
						chatMsgEntity.setName(ChatActivity.currentGuy);
						chatMsgEntity.setDate(content[2]);
						mDataArrays.add(chatMsgEntity);
					}
					
					br.close();
					
					mAdapter.notifyDataSetChanged();
					// 选中最后一条记录
					mListView.setSelection(mListView.getCount() - 1);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			String url = getResources().getString(R.string.communication_address);
			JSONObject sendObject = new JSONObject();
			try {
				sendObject.put("friendID", id);
				Log.e(className, sendObject.toString());
				Service service = new Service(sendObject, url);
				JSONObject receiveObject = service.getResult();
				ip = receiveObject.getString("ip");
				int status = Integer.parseInt(receiveObject.getString("status"));
				if (status == 1) {
					// Toast.makeText(ChatActivity.this, "ip地址： "+ip, Toast.LENGTH_LONG).show();
					Log.e(className, "ip地址： " + ip);
					messageClient = new MessageClient(ip);
					mBtnSend.setEnabled(true);
					// System.out.println("message client initial completly, ip: " + ip);
					Log.e(className, "message client initial completly, ip: " + ip);
				} else {
					Toast.makeText(ChatActivity.this, "天哪，那友仔有突然下线了，你不能再给那家伙发消息了", Toast.LENGTH_LONG).show();
					mBtnSend.setEnabled(false);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Looper.loop();
		}
	};
	
	public void initView() {
		tvContact = (TextView) findViewById(R.id.tv_contact);
		mListView = (ListView) findViewById(R.id.listview);
		mBtnSend = (Button) findViewById(R.id.btn_send);
		mBtnRcd = (TextView) findViewById(R.id.btn_rcd);
		mBtnSend.setOnClickListener(this);
		mBtnBack = (Button) findViewById(R.id.btn_back);
		mBottom = (RelativeLayout) findViewById(R.id.btn_bottom);
		mBtnBack.setOnClickListener(this);
		chatting_mode_btn = (ImageView) this.findViewById(R.id.ivPopUp);
		volume = (ImageView) this.findViewById(R.id.volume);
		rcChat_popup = this.findViewById(R.id.rcChat_popup);
		img1 = (ImageView) this.findViewById(R.id.img1);
		sc_img1 = (ImageView) this.findViewById(R.id.sc_img1);
		del_re = (LinearLayout) this.findViewById(R.id.del_re);
		voice_rcd_hint_rcding = (LinearLayout) this.findViewById(R.id.voice_rcd_hint_rcding);
		voice_rcd_hint_loading = (LinearLayout) this.findViewById(R.id.voice_rcd_hint_loading);
		voice_rcd_hint_tooshort = (LinearLayout) this.findViewById(R.id.voice_rcd_hint_tooshort);
		mSensor = new SoundMeter();
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		
		// 语音文字切换按钮
		chatting_mode_btn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				if (btn_vocie) {
					mBtnRcd.setVisibility(View.GONE);
					mBottom.setVisibility(View.VISIBLE);
					btn_vocie = false;
					chatting_mode_btn.setImageResource(R.drawable.chatting_setmode_msg_btn);
					
				} else {
					mBtnRcd.setVisibility(View.VISIBLE);
					mBottom.setVisibility(View.GONE);
					chatting_mode_btn.setImageResource(R.drawable.chatting_setmode_voice_btn);
					btn_vocie = true;
				}
			}
		});
		mBtnRcd.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// 按下语音录制按钮时返回false执行父类OnTouch
				return false;
			}
		});
	}
	
	private String[] msgArray = new String[] { "有人就有恩怨", "有恩怨就有江湖", "人就是江湖", "你怎么退出？ ", "生命中充满了巧合", "两条平行线也会有相交的一天。" };
	
	private String[] dataArray = new String[] { "2012-10-31 18:00", "2012-10-31 18:10", "2012-10-31 18:11",
			"2012-10-31 18:20", "2012-10-31 18:30", "2012-10-31 18:35" };
	private final static int COUNT = 6;
	
	// 初始化数据，放入实体集
	public void initData() {
		if (mDataArrays == null)
			mDataArrays = new ArrayList<ChatMsgEntity>();
		if (mAdapter == null)
			mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);
		
	}
	
	// 单击事件
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_send:
			send();
			break;
		case R.id.btn_back:
			mDataArrays.clear();
			finish();
			break;
		}
	}
	
	class WriteData implements Runnable {
		private String data;
		
		public WriteData(String data) {
			this.data = data;
		}
		
		@Override
		public void run() {
			Looper.prepare();
			if (bw != null) {
				try {
					bw.write(data);
					bw.flush();
					Log.e(className, "写入消息："+data);					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Looper.loop();
		}
	};
	
	private void send() {
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0) {
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(getDate());
			entity.setName(Person.getUsername());
			entity.setMsgType(false);
			entity.setText(contString);
			
			try {
				// System.out.println("发送内容：" + entity.toString());
				if (messageClient != null) {
					mDataArrays.add(entity);
					mAdapter.notifyDataSetChanged();
					
					mEditTextContent.setText("");
					// 选中最后一条记录
					mListView.setSelection(mListView.getCount() - 1);
					messageClient.send(entity.toString());
					
					WriteData writeData = new WriteData("false^^" + entity.getText() + "^^" + entity.getDate() + "\n");
					Thread thread = new Thread(writeData);
					thread.start();
					
				} else {
					Toast.makeText(ChatActivity.this, "那友仔突然下线了，消息发送失败", Toast.LENGTH_LONG).show();
					mBtnSend.setEnabled(false);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private String getDate() {
		Calendar c = Calendar.getInstance();
		
		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = String.valueOf(c.get(Calendar.MONTH));
		String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
		String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		String mins = String.valueOf(c.get(Calendar.MINUTE));
		
		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins);
		
		return sbBuffer.toString();
	}
	
	// 按下语音录制按钮时
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//getExternalExtSDStorageState

		if (!Environment.getExternalStorageDirectory().exists()) {
			Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
			return false;
		}

		if (btn_vocie) {
			// System.out.println("1");
			Log.e(className, "1");
			int[] location = new int[2];
			mBtnRcd.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
			int btn_rc_Y = location[1];
			int btn_rc_X = location[0];
			int[] del_location = new int[2];
			del_re.getLocationInWindow(del_location);
			int del_Y = del_location[1];
			int del_x = del_location[0];
			if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
				if (!Environment.getExternalStorageDirectory().exists()) {
					Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
					return false;
				}
				Log.e(className, "2");
				// System.out.println("2");
				if (event.getY() > btn_rc_Y && event.getX() > btn_rc_X) {// 判断手势按下的位置是否是语音录制按钮的范围内
					// System.out.println("3");
					Log.e(className, "3");
					mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_pressed);
					rcChat_popup.setVisibility(View.VISIBLE);
					voice_rcd_hint_loading.setVisibility(View.VISIBLE);
					voice_rcd_hint_rcding.setVisibility(View.GONE);
					voice_rcd_hint_tooshort.setVisibility(View.GONE);
					mHandler.postDelayed(new Runnable() {
						public void run() {
							if (!isShosrt) {
								voice_rcd_hint_loading.setVisibility(View.GONE);
								voice_rcd_hint_rcding.setVisibility(View.VISIBLE);
							}
						}
					}, 300);
					img1.setVisibility(View.VISIBLE);
					del_re.setVisibility(View.GONE);
					// startVoiceT = SystemClock.currentThreadTimeMillis();
					startVoiceT = System.currentTimeMillis();
					voiceName = startVoiceT + ".amr";
					start(voiceName);
					flag = 2;
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) {// 松开手势时执行录制完成
				// System.out.println("4");
				Log.e(className, "4");
				mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_nor);
				if (event.getY() >= del_Y && event.getY() <= del_Y + del_re.getHeight() && event.getX() >= del_x
						&& event.getX() <= del_x + del_re.getWidth()) {
					rcChat_popup.setVisibility(View.GONE);
					img1.setVisibility(View.VISIBLE);
					del_re.setVisibility(View.GONE);
					stop();
					flag = 1;
					File file = new File(Communication.voiceSavePath + voiceName);
					if (file.exists()) {
						file.delete();
					}
				} else {
					
					voice_rcd_hint_rcding.setVisibility(View.GONE);
					stop();
					// endVoiceT = SystemClock.currentThreadTimeMillis();
					endVoiceT = System.currentTimeMillis();
					flag = 1;
					int time = (int) ((endVoiceT - startVoiceT) / 1000);
					if (time < 1) {
						isShosrt = true;
						voice_rcd_hint_loading.setVisibility(View.GONE);
						voice_rcd_hint_rcding.setVisibility(View.GONE);
						voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
						mHandler.postDelayed(new Runnable() {
							public void run() {
								voice_rcd_hint_tooshort.setVisibility(View.GONE);
								rcChat_popup.setVisibility(View.GONE);
								isShosrt = false;
							}
						}, 500);
						return false;
					}
					ChatMsgEntity entity = new ChatMsgEntity();
					entity.setDate(getDate());
					entity.setName(Person.getUsername());
					entity.setMsgType(false);
					entity.setTime(time + "\"");
					entity.setText(voiceName);
					mDataArrays.add(entity);
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(mListView.getCount() - 1);
					rcChat_popup.setVisibility(View.GONE);
					
				}
			}
			if (event.getY() < btn_rc_Y) {// 手势按下的位置不在语音录制按钮的范围内
				// System.out.println("5");
				Log.e(className, "5");
				Animation mLitteAnimation = AnimationUtils.loadAnimation(this, R.anim.cancel_rc);
				Animation mBigAnimation = AnimationUtils.loadAnimation(this, R.anim.cancel_rc2);
				img1.setVisibility(View.GONE);
				del_re.setVisibility(View.VISIBLE);
				del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg);
				if (event.getY() >= del_Y && event.getY() <= del_Y + del_re.getHeight() && event.getX() >= del_x
						&& event.getX() <= del_x + del_re.getWidth()) {
					del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg_focused);
					sc_img1.startAnimation(mLitteAnimation);
					sc_img1.startAnimation(mBigAnimation);
				}
			} else {
				
				img1.setVisibility(View.VISIBLE);
				del_re.setVisibility(View.GONE);
				del_re.setBackgroundResource(0);
			}
		}
		return super.onTouchEvent(event);
	}
	
	private static final int POLL_INTERVAL = 300;
	
	private Runnable mSleepTask = new Runnable() {
		public void run() {
			stop();
		}
	};
	private Runnable mPollTask = new Runnable() {
		public void run() {
			double amp = mSensor.getAmplitude();
			updateDisplay(amp);
			mHandler.postDelayed(mPollTask, POLL_INTERVAL);
			
		}
	};
	
	private void start(String name) {
		mSensor.start(name);
		mHandler.postDelayed(mPollTask, POLL_INTERVAL);
	}
	
	private void stop() {
		mHandler.removeCallbacks(mSleepTask);
		mHandler.removeCallbacks(mPollTask);
		mSensor.stop();
		volume.setImageResource(R.drawable.amp1);
	}
	
	private void updateDisplay(double signalEMA) {
		
		switch ((int) signalEMA) {
		case 0:
		case 1:
			volume.setImageResource(R.drawable.amp1);
			break;
		case 2:
		case 3:
			volume.setImageResource(R.drawable.amp2);
			
			break;
		case 4:
		case 5:
			volume.setImageResource(R.drawable.amp3);
			break;
		case 6:
		case 7:
			volume.setImageResource(R.drawable.amp4);
			break;
		case 8:
		case 9:
			volume.setImageResource(R.drawable.amp5);
			break;
		case 10:
		case 11:
			volume.setImageResource(R.drawable.amp6);
			break;
		default:
			volume.setImageResource(R.drawable.amp7);
			break;
		}
	}
	
	public void head_xiaohei(View v) { // 标题栏 返回按钮
	
	}
	
	public ChatMsgViewAdapter getmAdapter() {
		return mAdapter;
	}
	
	public List<ChatMsgEntity> getmDataArrays() {
		return mDataArrays;
	}
		
	public void setListViewToLastItem() {
		mListView.setSelection(mListView.getCount() - 1);
	}
	
	@Override
	protected void onDestroy() {
		Thread thread = new Thread(CloseJob);
		thread.start();
		currentChatMonitor.setExit(true);		
		super.onDestroy();
	}
	
	Runnable CloseJob = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			if(messageClient!=null){
				try {
					messageClient.close();
					Log.e(className, "close the socket");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (bw != null) {
				try {					
					bw.close();					
					Log.e(className, "close the bufferwriter");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Looper.loop();
		}
	};
}