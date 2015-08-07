package com.pb.record;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends Activity implements OnClickListener{


	private static final String TAG = "MainActivity";
	/**
	 * 是否分享到火星儿
	 */
	private static final int FAIL = 1;
	private static final int SUCCESS = 0;
	
	private VoiceProgressView mRoundProgressBar1;
	private RelativeLayout rl_voice;
	private ImageView iv_play;
	private int progress = 0;
	private long time_temp = 1000 * 60;
	private boolean record_state = true; //是否可以录音
	private boolean play_state = false; //是否播放录音
	boolean isPause = false; //是否暂停
	private boolean record_finish = false; //录音是否完成
	private boolean isRecording;
	private String voice_name;
	private RecordUtil mRecordUtil;
	private File voice_file;
	private MediaPlayer mediaPlayer;
	boolean isCanShowVoice = true;
	private ImageView iv_voice;
	private TextView tv_voice_time;
	private TextView tv_del_voice;
	private LinearLayout ll_voice;
	private RelativeLayout rl_add_sign;
	private boolean isAction_Cancel = false;
	
	
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				
			case 11: //
				mRoundProgressBar1.setVisibility(View.GONE);
				iv_play.setVisibility(View.VISIBLE);
				record_finish = true;
				tv_voice_time.setText(time_temp/1000 + "”");
				tv_del_voice.setVisibility(View.VISIBLE);
				if (mRecordUtil != null) {
					try {
						mRecordUtil.stop();//录音完成,释放资源
					} catch (IOException e) {
						e.printStackTrace();
					} 
				}
				break;

			case 12:
				mRoundProgressBar1.setVisibility(View.GONE);
				iv_play.setVisibility(View.VISIBLE);
				iv_play.setBackgroundResource(R.drawable.pay_voice);
				isPause = false;
				play_state = false;
				if (mediaPlayer != null) {
					mediaPlayer.reset();
					mediaPlayer.release();
					
				}
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		ll_voice = (LinearLayout) findViewById(R.id.ll_voice);
		rl_voice = (RelativeLayout) findViewById(R.id.rl_voice);
		mRoundProgressBar1 = (VoiceProgressView) findViewById(R.id.roundProgressBar1);
		iv_play = (ImageView) findViewById(R.id.iv_play);		
		iv_play.setVisibility(View.GONE);
		tv_voice_time = (TextView) findViewById(R.id.tv_voice_time);
		tv_del_voice = (TextView) findViewById(R.id.tv_del_voice);
		tv_del_voice.setVisibility(View.INVISIBLE);
		tv_del_voice.setOnClickListener(this);
	
		aboutVoice();
	}

	private void aboutVoice() {
		mRoundProgressBar1.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					isAction_Cancel  = false;
					rl_voice.setBackgroundResource(R.drawable.play_voice_press);
					if (record_finish) {
						playVoice();
					}else {
						Log.i(TAG, "MotionEvent.ACTION_DOWN...............");
						if (record_state) {
							tv_voice_time.setText("松开结束");
							recordVoice();
						}
					}
					
					return true;
				case MotionEvent.ACTION_UP:
					Log.i(TAG, "录音: MotionEvent.ACTION_UP");
					rl_voice.setBackgroundResource(R.drawable.play_voice_normal);
					if (record_finish) {
						
					}else {
						time_temp = progress;
						record_state = false;
						mRoundProgressBar1.setCricleProgressColor(Color.GREEN);
						mRoundProgressBar1.setVisibility(View.GONE);
						iv_play.setVisibility(View.VISIBLE);
						time_temp = progress;
						record_finish = true;
						tv_voice_time.setText(time_temp/1000 + "”");
						tv_del_voice.setVisibility(View.VISIBLE);
						if (mRecordUtil != null) {
							try {
								mRecordUtil.stop();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					
					break;
					
				case MotionEvent.ACTION_CANCEL:
					Log.i(TAG, "录音: MotionEvent.ACTION_CANCEL");
//					time_temp = progress;
//					record_state = false;
//					time_temp = progress;
//					mRoundProgressBar1.setCricleProgressColor(Color.GREEN);
//					Log.i(TAG, "MotionEvent.ACTION_CANCEL...............");
//					rl_voice.setBackgroundResource(R.drawable.play_voice_normal);
//					mRoundProgressBar1.setVisibility(View.GONE);
//					iv_play.setVisibility(View.VISIBLE);
					isAction_Cancel  = true;
					progress = 0;
					time_temp = 1000 * 60;
					record_state = true; //是否可以录音
					play_state = false; //是否播放录音
					isPause = false; //是否暂停
					record_finish = false; //录音是否完成
					tv_del_voice.setVisibility(View.INVISIBLE);
					tv_voice_time.setText("按住录音");
					iv_play.setVisibility(View.GONE);
					mRoundProgressBar1.setMax(1000 * 60);
					mRoundProgressBar1.setProgress(progress);
					mRoundProgressBar1.setCricleProgressColor(Color.GREEN);
					mRoundProgressBar1.setVisibility(View.VISIBLE);
					rl_voice.setBackgroundResource(R.drawable.play_voice_normal);
					if (mRecordUtil != null) {
						try {
							mRecordUtil.stop();//录音完成,释放资源
						} catch (IOException e) {
							e.printStackTrace();
						} 
					}
					break;

				default:
					break;
				}
				return true;
			}
		});
		iv_play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				playVoice();
			}
		});
		
		tv_del_voice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*if (mediaPlayer != null && mediaPlayer.isPlaying()) {
					mediaPlayer.reset();
					mediaPlayer.release();
				}*/
				if (voice_file.exists()) {
					voice_file.delete();
				}
				progress = 0;
				time_temp = 1000 * 60;
				record_state = true; //是否可以录音
				play_state = false; //是否播放录音
				isPause = false; //是否暂停
				record_finish = false; //录音是否完成
				tv_del_voice.setVisibility(View.INVISIBLE);
				tv_voice_time.setText("按住录音");
				iv_play.setVisibility(View.GONE);
				mRoundProgressBar1.setMax(1000 * 60);
				mRoundProgressBar1.setProgress(progress);
				mRoundProgressBar1.setCricleProgressColor(Color.GREEN);
				mRoundProgressBar1.setVisibility(View.VISIBLE);
			}
		});
	}
	

	public void playVoice(){
		if (play_state) {
			iv_play.setBackgroundResource(R.drawable.pay_voice);
			mRoundProgressBar1.setCricleProgressColor(Color.GREEN);
			mRoundProgressBar1.setVisibility(View.VISIBLE);
			Log.i(TAG, "当前的progress: " + progress);
			mRoundProgressBar1.setProgress(progress);
			mRoundProgressBar1.setMax((int)time_temp);
			play_state = false;
			isPause = true;
			pauseVoice();
		}else {
			iv_play.setBackgroundResource(R.drawable.stop_voice);
			mRoundProgressBar1.setCricleProgressColor(Color.GREEN);
			mRoundProgressBar1.setMax((int)time_temp);
			if (isPause) {
				
			}else {
				progress = 0;
				mRoundProgressBar1.setProgress(0);
			}
			mRoundProgressBar1.setVisibility(View.VISIBLE);
			play_state = true;
			isPause = false;

			mediaPlayer = new MediaPlayer();
			try {
				mediaPlayer.setDataSource(voice_file.getAbsolutePath());
				mediaPlayer.prepare();
				mediaPlayer.start();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			showVoice();
		}
	}
	
	private void pauseVoice() {
		mediaPlayer.pause();
	}
	
	private void showVoice() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (progress <= time_temp) {
					progress += 100;
					mRoundProgressBar1.setProgress(progress);
					
					if (progress > time_temp) {
						Message message = new Message();
						message.what = 12;
						handler.sendMessage(message);
					}
					
					if (!play_state) {
						break;
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				if (mediaPlayer != null) {
					mediaPlayer.stop();
				}
				iv_play.setBackgroundResource(R.drawable.pay_voice);
			}
		});
	}
	
	public void recordVoice(){
		isRecording = true;
		File file = new File(Constant.apkfile);
		if(!file.exists()){
			file.mkdir();
		}
		
		File soundfile = new File(Constant.soundfile);
		if (!soundfile.exists()) {
			soundfile.mkdirs();
		}
		
		
		voice_name = System.currentTimeMillis() + ".amr";

		voice_file = new File(Constant.soundfile + voice_name);
		mRecordUtil = new RecordUtil(Constant.soundfile + voice_name);
		try {
			// 开始录音
			mRecordUtil.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Log.i(TAG, "录音: case MotionEvent.ACTION_DOWN");
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (progress <= time_temp) {
					progress += 100;
					mRoundProgressBar1.setProgress(progress);
					if (progress > 1000 *  30) {
						mRoundProgressBar1.setCricleProgressColor(Color.RED);
						
						Log.i("pengbo", "progress: " + progress);
						if (progress > 1000 * 60) {
							mRoundProgressBar1.setCricleProgressColor(Color.GREEN);
							Message message = new Message();
							message.what = 11;
							handler.sendMessage(message);
						}
						
						
					}
					
					if (isAction_Cancel) {//针对部分手机或安全软件弹出提醒
						break;
					}

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/*case R.id.iv_voice:
			
			if (isCanShowVoice ) {
				ll_voice.setVisibility(View.VISIBLE);
				isCanShowVoice = false;
			}else {
				ll_voice.setVisibility(View.GONE);
				isCanShowVoice = true;
			}
			break;
		*/
		
		
		default:
			break;
		}
	}
	
	
}
