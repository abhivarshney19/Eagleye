package com.example.newgeolocation;

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



public class Login extends Activity {

		// Progress Dialog
		private ProgressDialog pDialog;

		// Session Manager Class
		SessionManager session;
		
		JSONParser jsonParser = new JSONParser();
		Button btnLogin;
		Button btnLinkToRegister;
		EditText inputEmail;
		EditText inputPassword;
		TextView loginErrorMsg;
		
		// url to create new product
		private static String url_create_product = "http://techfest.orgfree.com/techfest/abhi_get_user.php";
		// JSON Node names
				private static final String TAG_SUCCESS = "success";
				private static final String TAG_PRODUCTS = "product";
				private static final String TAG_PID = "uid";
				private static final String TAG_NAME = "name";
				private static final String TAG_COLLEGE = "college";
				private static final String TAG_MOBILE = "mobile";

				// products JSONArray
				JSONArray products = null;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.login);
			// Importing all assets like buttons, text fields
			inputEmail = (EditText) findViewById(R.id.loginEmail);
			inputPassword = (EditText) findViewById(R.id.loginPassword);
			btnLogin = (Button) findViewById(R.id.btnLogin);
			btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
			loginErrorMsg = (TextView) findViewById(R.id.login_error);

			
			 // Session Manager
	        session = new SessionManager(getApplicationContext());   
			
			
	    	// Login button Click Event
			btnLogin.setOnClickListener(new View.OnClickListener() {

				public void onClick(View view) {
					
					
					new CreateRegister().execute();
					
				}
			});

			// Link to Register Screen
			btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

				public void onClick(View view) {
					Intent i = new Intent(getApplicationContext(),
							RegisterActivity.class);
					startActivity(i);
					finish();
				}
			});
		}

		/**
		 * Background Async Task to Create new product
		 * */
		class CreateRegister extends AsyncTask<String, String, String> {

			/**
			 * Before starting background thread Show Progress Dialog
			 * */
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(Login.this);
				pDialog.setMessage("logging in...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(true);
				pDialog.show();
			}

			/**
			 * Creating product
			 * */
			protected String doInBackground(String... args) {
	
				
				String username = inputEmail.getText().toString();
				String password = inputPassword.getText().toString();
				
				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				params1.add(new BasicNameValuePair("username", username));
				params1.add(new BasicNameValuePair("password", password));
				
				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(
						url_create_product, "GET", params1);
				
				try {	
				// json success tag
			int	success = json.getInt(TAG_SUCCESS);
			String  ss=String.valueOf(success);
			
				if (success == 1) {
					// Creating user login session
					// For testing i am stroing name, email as follow
					// Use user real data
					// products found
					// Getting Array of Products
					
					products = json.getJSONArray(TAG_PRODUCTS);
					
					// looping through All Products
					for (int i1 = 0; i1 < products.length(); i1++) {
						JSONObject c = products.getJSONObject(i1);

						// Storing each json item in variable
						String id = c.getString(TAG_PID);
						String name = c.getString(TAG_NAME);
						String college = c.getString(TAG_COLLEGE);
						String mobile = c.getString(TAG_MOBILE);
						
					session.createLoginSession(id, name,college,mobile);
					
					// successfully created product
					Intent i = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(i);
					}
				}else {
					// no products found
					
				}
			} 
			catch (JSONException e) {
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