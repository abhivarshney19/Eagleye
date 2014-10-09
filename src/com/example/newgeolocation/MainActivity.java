package com.example.newgeolocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements LocationListener {
	private TextView latituteField;
	private TextView longitudeField;
	private LocationManager locationManager;
	private String provider;
	private TextView myAddress;
	Double lat, lng;
	
	// Session Manager Class
	SessionManager session;
	
	JSONParser jsonParser = new JSONParser();
	
	// url to create new product
		private static String url_create_product = "http://techfest.orgfree.com/techfest/abhi_update_product.php";

		// JSON Node names
				
		private static String KEY_SUCCESS = "success";

	
	
	// Button Logout
	Button btnLogout;
	
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		session = new SessionManager(getApplicationContext());
        session.checkLogin();
        
		
		latituteField = (TextView) findViewById(R.id.txt1);
		longitudeField = (TextView) findViewById(R.id.txt2);
		myAddress = (TextView) findViewById(R.id.myaddress);

		// Get the location manager

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Define the criteria how to select the locatioin provider -> use
		// default

		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);

		// Initialize the location fields

		if (location != null) {
			System.out.println("Provider " + provider + " has been selected.");
			onLocationChanged(location);
			new Location123().execute();
		} else {
			latituteField.setText("Location not available");
			longitudeField.setText("Location not available");
		}
		// Button logout
	    btnLogout = (Button) findViewById(R.id.btnLogout);
		
		
	btnLogout.setOnClickListener(new View.OnClickListener() {

	@Override
	public void onClick(View arg0) {
		// Clear the session data
		// This will clear all session data and 
		// redirect user to LoginActivity
		session.logoutUser();
	}
	});

		
	
	}
	

	/* Request updates at startup */
	@Override
	protected void onResume() {
		super.onResume();
		locationManager
				.requestLocationUpdates(provider, 100 * 60 * 5, 10, this);

		// LocationManager service = (LocationManager)
		// getSystemService(LOCATION_SERVICE);
		boolean enabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// check if enabled and if not send user to the GSP settings
		// Better solution would be to display a dialog and suggesting to
		// go to the settings
		if (!enabled) {

			Toast.makeText(getApplicationContext(),
					"Enable GPS and press back", Toast.LENGTH_LONG).show();

			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}

	}

	/* Remove the locationlistener updates when Activity is paused */

	@Override
	public void onLocationChanged(Location location) {
		
		
		
		

		Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);

		StringBuilder strReturnedAddress;
		try {
			 lat = (location.getLatitude());
			lng = (location.getLongitude());
			
			List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);

			if (addresses != null) {
				Address returnedAddress = addresses.get(0);
				strReturnedAddress = new StringBuilder("Address:\n");
				for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
					strReturnedAddress
							.append(returnedAddress.getAddressLine(i)).append(
									"\n");
				}
				myAddress.setText(strReturnedAddress.toString());
			} else {
				myAddress.setText("No Address returned!");
			}
		} catch (Exception E) {
			// TODO Auto-generated catch block
			E.printStackTrace();
			myAddress.setText("Can not get Address!");
		}

		

	}
	class Location123 extends AsyncTask<String, String, String> {

		
		protected String doInBackground(String... args) {
			// Building Parameters
			
			String lat1 = String.valueOf(lat);
			String lng1 = String.valueOf(lng);
			latituteField.setText(String.valueOf(lat));
			longitudeField.setText(String.valueOf(lng));
			 HashMap<String, String> user = session.getUserDetails();
		        
		        // name
		        String uid = user.get(SessionManager.KEY_ID);
		        String add="aaa";
		        
			List<NameValuePair> params = new ArrayList<NameValuePair>();
					
			 // get user data from session
	       
			
			params.add(new BasicNameValuePair("uid", uid));
			params.add(new BasicNameValuePair("latitude", lat1));
			params.add(new BasicNameValuePair("longitude", lng1));
			params.add(new BasicNameValuePair("address",add ));
			Log.d("X",uid+lat1+lng1+add);
			// getting JSON Object
			// Note that create product url accepts POST method
			
			Log.d("abcd",params.toString());
			JSONObject json = jsonParser.makeHttpRequest(url_create_product,
					"GET", params);
			Log.d("abcd", json.toString());
			

			
			return null;
		}

		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}
}