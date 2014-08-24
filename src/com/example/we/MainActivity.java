package com.example.we;

import com.example.sharedpreference.SharedPreference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SharedPreference sp = new SharedPreference(this);
		if (sp.isLoggedIn()) {
			Intent i = new Intent(this, Poke.class);
			startActivity(i);
			finish();

		} else {
			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);
			finish();
		}
	}

}
