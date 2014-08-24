package com.example.we;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.globals.AlertDialogManager;
import com.example.globals.ConnectionDetector;
import com.example.globals.GlobalDeclares;
import com.example.interfaces.ServerUtilitiesCallBackInterface;
import com.example.server.ServerUtilities;
import com.example.sharedpreference.SharedPreference;
import com.google.android.gcm.GCMRegistrar;

public class LoginActivity extends Activity implements OnClickListener,
		ServerUtilitiesCallBackInterface {

	private TextView register;
	private EditText user_name;
	private EditText password;
	private Button log_but;
	ServerUtilitiesCallBackInterface delegate = null;
	private ConnectionDetector cd;
	AlertDialogManager alert = new AlertDialogManager();
	private BroadcastReceiver mHandleMessageReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_login);
		this.delegate = this;
		SharedPreference sp = new SharedPreference(this);
		if (sp.isLoggedIn()) {
			Intent i = new Intent(this, Poke.class);
			startActivity(i);
			finish();

		}
		cd = new com.example.globals.ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(LoginActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		// GCMRegistrar.getRegistrationId(this);

		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				GlobalDeclares.DISPLAY_MESSAGE_ACTION));
		final String regId = GCMRegistrar.getRegistrationId(this);

		// Check if regid already presents
		if (regId.equals("")) {
			// Registration is not present, register now with GCM
			GCMRegistrar.register(this, GlobalDeclares.SENDER_ID);
			GlobalDeclares.setGcmId(GCMRegistrar.getRegistrationId(this));
		} else {
			GlobalDeclares.setGcmId(regId);
		}
		user_name = (EditText) findViewById(R.id.editText1);
		password = (EditText) findViewById(R.id.editText2);
		log_but = (Button) findViewById(R.id.button1);
		log_but.setOnClickListener(this);
		register = (TextView) findViewById(R.id.textView1);
		register.setPaintFlags(register.getPaintFlags()
				| Paint.UNDERLINE_TEXT_FLAG);
		register.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.textView1:
			register();
			break;
		case R.id.button1:
			login();
		default:
			break;
		}

	}

	private void login() {
		// TODO Auto-generated method stub
		if (user_name.getText().toString().length() > 0
				|| password.getText().toString().length() > 0) {
			String post_entity = "user_name=" + user_name.getText().toString()
					+ "&password=" + password.getText().toString();
			new ServerUtilities(getApplicationContext(),
					GlobalDeclares.SERVER_URL, post_entity, delegate);
		} else {
			Toast.makeText(getApplicationContext(),
					"Check your login deatils!", Toast.LENGTH_LONG).show();

		}
	}

	private void register() {
		// TODO Auto-generated method stub
		Intent i = new Intent(getApplicationContext(), Registration.class);
		startActivity(i);

	}

	@Override
	public void serverCallback(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject j = new JSONObject(result);
			if (j.optString("status").equalsIgnoreCase("false")) {
				Toast.makeText(getApplicationContext(), "Login failed",
						Toast.LENGTH_LONG).show();
				user_name.setText("");
				password.setText("");
			} else {
				GlobalDeclares.setMY_ID(j.optString("status"));
				SharedPreference sp = new SharedPreference(
						getApplicationContext());
				// GlobalDeclares.setMY_ID(j.optString("status").toString());
				sp.activateSession("true", j.optString("status").toString());
				Intent i = new Intent(getApplicationContext(), Poke.class);
				startActivity(i);
				finish();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
