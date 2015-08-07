package com.pb.record;

import java.io.File;

import android.os.Environment;

public interface Constant {

	String apkfile = Environment.getExternalStorageDirectory() + File.separator + "recordok" + File.separator;
	String soundfile = apkfile + "sound" + File.separator; // ÉùÒôÎÄ¼ş´æ´¢Â·¾¶

}
