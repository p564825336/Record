package com.pb.record;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import android.util.Log;

/**
 * 录音工具类
 * 
 * @author rendongwei
 * 
 */
public class RecordUtil {
	private static final int SAMPLE_RATE_IN_HZ = 8000;
	private static final String TAG = "RecordUtil";
	private MediaRecorder recorder = new MediaRecorder();
	
	// 录音的路径
	private String mPath;
	
	public RecordUtil(String path) {
		mPath = path;
	}

	/**
	 * 开始录音
	 * 
	 * @throws IOException
	 */
	public void start() throws IOException {
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			throw new IOException("SD Card is not mounted,It is  " + state
					+ ".");
		}
		Log.i(TAG, "mPath: " + mPath);
		File directory = new File(mPath).getParentFile();
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IOException("Path to file could not be created");
		}
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		recorder.setOutputFile(mPath);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//		recorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);
		recorder.prepare();
		recorder.start();
	}

	/**
	 * 结束录音
	 * 
	 * @throws IOException
	 */
	public void stop() throws IOException {
		recorder.stop();
		recorder.release();
	}

	/**
	 * 获取麦克风音量大小
	 * @return
	 */
	public double getAmplitude() {
		if (recorder != null) {
			return (recorder.getMaxAmplitude());
		}
		return 0;
	}
	
}
