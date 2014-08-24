package com.example.globals;

import android.content.Context;
import android.content.Intent;

public class GlobalDeclares {
	//static String HOST = "http://victorsline.biz/gcm/api/";
	static String HOST = "http://app.settimoscript.com/android/api/";
	public static String SERVER_URL = HOST + "checkLogin";
	public static String NEW_REGISTRATION = HOST + "newAccount";
	public static final String POKE = HOST + "poke";
	public static final String UPDATE_GCM = HOST + "updateGcm_id";

	public static String getMY_ID() {
		return MY_ID;
	}

	public static void setMY_ID(String mY_ID) {
		MY_ID = mY_ID;
	}

	public static final String SENDER_ID = "148555787413";
	public static String GCM_ID = "gcm_id";
	public static String MY_ID = "my_id";

	public static final String DISPLAY_MESSAGE_ACTION = "com.example.we.DISPLAY_MESSAGE";

	public static final String EXTRA_MESSAGE = "message";

	public static String getGcmId() {
		return GCM_ID;
	}

	public static void setGcmId(String gcm_id) {
		GCM_ID = gcm_id;
	}

	public static void displayMessage(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}

}
