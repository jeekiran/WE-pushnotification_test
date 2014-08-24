package com.example.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreference {

	static SharedPreferences SharedPreference;
	Editor editor;
	Context context;
	private static final String IS_LOGGED_IN = "false";
	private static final String MY_ID = "my_id";
	private static final String PREF_NAME = "we";

	private static final int PRIVATE_MODE = 0;

	public SharedPreference(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		SharedPreference = context
				.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = SharedPreference.edit();
	}

	public Boolean isLoggedIn() {
		return SharedPreference.getBoolean(IS_LOGGED_IN, false);
	}

	public static String getMyId() {
		return SharedPreference.getString(MY_ID, null);
	}

	public Boolean activateSession(String status, String my_id) {
		if (status.equalsIgnoreCase("true")) {
			editor.putBoolean(IS_LOGGED_IN, true);
			editor.putString(MY_ID, my_id);
		}
		editor.commit();
		return true;
	}

	public void deactivateSession() {
		editor.putBoolean(IS_LOGGED_IN, false);
		editor.putString(MY_ID, "");

		editor.commit();

	}
}
