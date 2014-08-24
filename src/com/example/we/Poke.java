package com.example.we;

import static com.example.globals.GlobalDeclares.DISPLAY_MESSAGE_ACTION;
import static com.example.globals.GlobalDeclares.EXTRA_MESSAGE;
import static com.example.globals.GlobalDeclares.SENDER_ID;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.globals.AlertDialogManager;
import com.example.globals.GlobalDeclares;
import com.example.interfaces.ServerUtilitiesCallBackInterface;
import com.example.server.ServerUtilities;
import com.example.sharedpreference.SharedPreference;
import com.google.android.gcm.GCMRegistrar;

public class Poke extends Activity implements OnClickListener,
		ServerUtilitiesCallBackInterface {

	private TextView log_out;
	private Button poke_button;
	private EditText whom;
	ServerUtilitiesCallBackInterface delegate = null;
	AlertDialogManager alert = new AlertDialogManager();
	SharedPreference sharedPreferance;
	private TextView lblMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_poke);
		SharedPreference sp = new SharedPreference(this);
		if (!sp.isLoggedIn()) {
			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);
			finish();

		}
		lblMessage = (TextView) findViewById(R.id.textView3);
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		sharedPreferance = new SharedPreference(getApplicationContext());

		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));

		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);

		// Check if regid already presents
		if (regId.equals("")) {
			// Registration is not present, register now with GCM
			GCMRegistrar.register(this, SENDER_ID);

			String regId2 = GCMRegistrar.getRegistrationId(this);
			new UpdateGcm_id().execute(sharedPreferance.getMyId(), regId);
		} else {
			String regId2 = GCMRegistrar.getRegistrationId(this);
			new UpdateGcm_id().execute(sharedPreferance.getMyId(), regId);
		}
		this.delegate = this;
		log_out = (TextView) findViewById(R.id.textView2);
		whom = (EditText) findViewById(R.id.editText1);

		log_out.setPaintFlags(log_out.getPaintFlags()
				| Paint.UNDERLINE_TEXT_FLAG);
		log_out.setOnClickListener(this);
		poke_button = (Button) findViewById(R.id.button1);
		poke_button.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.textView2:

			SharedPreference sp = new SharedPreference(getApplicationContext());
			sp.deactivateSession();
			Intent intent = new Intent(getApplicationContext(),
					LoginActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.button1:
			if (whom.getText().length() > 0) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				poke_button.setEnabled(false);
				String postEntity = "from=" + sharedPreferance.getMyId()
						+ "&to=" + whom.getText().toString();
				new ServerUtilities(getApplicationContext(),
						GlobalDeclares.POKE, postEntity, delegate);
			} else {
				Toast.makeText(getApplicationContext(),
						"Name required to poke!", Toast.LENGTH_LONG).show();
			}
		default:
			break;
		}

	}

	@Override
	public void serverCallback(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject j = new JSONObject(result);
			if (j.optString("success").equalsIgnoreCase("1")) {
				Toast.makeText(getApplicationContext(),
						"you are successfully poked!", Toast.LENGTH_LONG)
						.show();
				poke_button.setEnabled(true);
			} else if (j.optString("failure").equalsIgnoreCase("1")) {
				poke_button.setEnabled(true);
				Toast.makeText(getApplicationContext(), "Poke failed!",
						Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			/**
			 * Take appropriate action on this message depending upon your app
			 * requirement For now i am just displaying it on the screen
			 * */

			// Showing received message
			// alert.showAlertDialog(Poke.this, "hi,", newMessage + "\n",
			// false);
			// lblMessage.append(newMessage + "\n");
			lblMessage.setText(newMessage);
			Toast.makeText(getApplicationContext(),
					"New Message: " + newMessage, Toast.LENGTH_LONG).show();

			// Releasing wake lock
			WakeLocker.release();
		}
	};

	public class UpdateGcm_id extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			// TODO Auto-generated method stub
			HttpClient httpClient = new DefaultHttpClient();
			InputStream responseBody = null;
			HttpPost post = new HttpPost(GlobalDeclares.UPDATE_GCM);
			post.addHeader("Accept", "application/json");
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			try {
				post.setEntity(new StringEntity("my_id=" + params[0]
						+ "&gcm_id=" + params[1]));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				HttpResponse response = httpClient.execute(post);
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					responseBody = entity.getContent();
					BufferedReader buffer = new BufferedReader(
							new InputStreamReader(responseBody));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						result += s;
					}
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				JSONObject j = new JSONObject(result);
				if (j.has("status")) {
					if (j.optString("status") == "true") {
						Toast.makeText(getApplicationContext(),
								"GCM ID updated!", Toast.LENGTH_LONG).show();
						return;
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"GCM ID update failed!", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	void unregister(final Context context, final String regId)
			throws IOException {

		// post(serverUrl, params);
		GCMRegistrar.unregister(this);
		GCMRegistrar.setRegisteredOnServer(context, false);
		String message = context.getString(R.string.server_unregistered);
		GlobalDeclares.displayMessage(context, message);

	}

}
