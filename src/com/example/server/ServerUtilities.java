package com.example.server;

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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.globals.GlobalDeclares;
import com.example.interfaces.ServerUtilitiesCallBackInterface;
import com.example.we.R;
import com.google.android.gcm.GCMRegistrar;

public class ServerUtilities extends AsyncTask<String, Integer, String> {

	private Context context;
	private ProgressDialog pd;
	private String entityString;
	private InputStream responseBody;
	private ServerUtilitiesCallBackInterface delegate;

	public ServerUtilities(Context context, String URL, String postEntity,
			ServerUtilitiesCallBackInterface delegate) {
		this.context = context;
		pd = new ProgressDialog(context);
		pd.setMessage("loading....");
		this.entityString = postEntity;
		this.delegate = delegate;
		execute(URL);

	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		String response = "";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(params[0]);
		// post.addHeader("Accept", "application/json");
		post.addHeader("Content-Type", "application/x-www-form-urlencoded");
		try {
			post.setEntity(new StringEntity(entityString));
			try {
				HttpResponse httpResponse = httpClient.execute(post);
				HttpEntity entity = httpResponse.getEntity();
				if (entity != null) {
					responseBody = entity.getContent();
					BufferedReader buffer = new BufferedReader(
							new InputStreamReader(responseBody));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						response += s;
					}
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		this.delegate.serverCallback(result);
	}

	
}
