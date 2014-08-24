package com.example.we;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.globals.AlertDialogManager;
import com.example.globals.ConnectionDetector;
import com.example.globals.GlobalDeclares;
import com.example.interfaces.ServerUtilitiesCallBackInterface;
import com.example.server.ServerUtilities;
import com.example.sharedpreference.SharedPreference;

public class Registration extends Activity implements OnClickListener,
		ServerUtilitiesCallBackInterface {

	private EditText ur_name;
	private EditText email;
	private EditText re_pass;
	private EditText pass;
	private Button reg_but;
	ServerUtilitiesCallBackInterface delegate = null;
	private ConnectionDetector cd;
	private AlertDialogManager alert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_registration);
		this.delegate = this;
		cd = new com.example.globals.ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(Registration.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}

		ur_name = (EditText) findViewById(R.id.editText1);
		email = (EditText) findViewById(R.id.editText2);
		pass = (EditText) findViewById(R.id.editText3);
		re_pass = (EditText) findViewById(R.id.editText4);
		reg_but = (Button) findViewById(R.id.button1);
		reg_but.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button1:
			registration();
			break;

		default:
			break;
		}

	}

	private void registration() {
		// TODO Auto-generated method stub
		if (ur_name.getText().length() > 0 && email.getText().length() > 0
				&& pass.getText().length() > 0
				&& re_pass.getText().length() > 0) {
			if (pass.getText().toString()
					.equalsIgnoreCase(re_pass.getText().toString())) {
				String postEntity = "user_name=" + ur_name.getText().toString()
						+ "&email=" + email.getText().toString() + "&password="
						+ pass.getText().toString() + "&gcm_id="
						+ GlobalDeclares.getGcmId();
				new ServerUtilities(getApplicationContext(),
						GlobalDeclares.NEW_REGISTRATION, postEntity, delegate);

			} else {
				Toast.makeText(getApplicationContext(),
						"Password does not match!", Toast.LENGTH_LONG).show();
			}

		} else {
			Toast.makeText(getApplicationContext(),
					"You need to fill all the fields!", Toast.LENGTH_LONG)
					.show();
		}

	}

	@Override
	public void serverCallback(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject j = new JSONObject(result);
			if (j.optString("status").equalsIgnoreCase("true")) {
				Toast.makeText(getApplicationContext(),
						"Registration success!", Toast.LENGTH_LONG).show();

				Intent intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				startActivity(intent);
				finish();

			} else {
				Toast.makeText(getApplicationContext(),
						"Registration Failed! Check your registrion details!",
						Toast.LENGTH_LONG).show();
				ur_name.setText("");
				email.setText("");
				pass.setText("");
				re_pass.setText("");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
