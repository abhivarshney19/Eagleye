
package com.example.newgeolocation;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.array;


public class RegisterActivity extends Activity {
	Button btnRegister;
	Button btnLinkToLogin;
	EditText inputFullName;
	EditText inputUsername;
	EditText inputPassword;
	EditText inputMobile;
	TextView registerErrorMsg;
	
	private ProgressDialog pDialog;
	
	// Session Manager Class
			SessionManager session;
			
			JSONParser jsonParser = new JSONParser();
			
	

	// url to create new product
	private static String url_create_product = "http://techfest.orgfree.com/techfest/abhi_create_product.php";

	// JSON Node names
			
	private static String KEY_SUCCESS = "success";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		// Importing all assets like buttons, text fields
		inputFullName = (EditText) findViewById(R.id.registerName);
		inputUsername = (EditText) findViewById(R.id.registerEmail);
		inputPassword = (EditText) findViewById(R.id.registerPassword);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		
		inputMobile = (EditText) findViewById(R.id.mobile1);
		btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
		registerErrorMsg = (TextView) findViewById(R.id.register_error);
		
		 // Session Manager
        session = new SessionManager(getApplicationContext());   
		
		
		// Register Button Click event
		
		btnRegister.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View view) {
				String name = inputFullName.getText().toString();
				String email = inputUsername.getText().toString();
				String password = inputPassword.getText().toString();
				String mobile = inputMobile.getText().toString();
				new CreateNewProduct().execute();	 
					// creating new product in background thread
					
				
			}
		});
		
		
		// Link to Login Screen
		btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						Login.class);
				startActivity(i);
				// Close Registration View
				finish();
			}
		});
	}
	/**
	 * Background Async Task to Create new product
	 * */
	class CreateNewProduct extends AsyncTask<String, String, String> {
		
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RegisterActivity.this);
			pDialog.setMessage("registering please wait ..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {
			String username = inputUsername.getText().toString();
			String password = inputPassword.getText().toString();
			String name = inputFullName.getText().toString();
			String mobile = inputMobile.getText().toString();
			
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			List<NameValuePair> params1 = new ArrayList<NameValuePair>();
			
			params.add(new BasicNameValuePair("username", username));
			params.add(new BasicNameValuePair("password", password));
			params.add(new BasicNameValuePair("name", name));
			params.add(new BasicNameValuePair("mobile", mobile));

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_create_product,
					"GET", params);
			JSONObject json1 = jsonParser.makeHttpRequest("http://techfest.orgfree.com/techfest/abhi_get_uid.php",
					"GET", params1);
			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(KEY_SUCCESS);
				String uid = json1.getString("uid");
				if (success == 1) {
					session.createLoginSession(uid,name,"college",mobile);
					// successfully created product
					Intent i = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(i);
					
					// closing this screen
					finish();
				} else {
					// failed to create product
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();
		}

	}
}


