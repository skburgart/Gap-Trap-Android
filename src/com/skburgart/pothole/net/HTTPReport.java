package com.skburgart.pothole.net;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.skburgart.pothole.R;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class HTTPReport {

    private static final String TAG = "HTTPReport";
	public static final String REPORT_URL = "http://10.0.0.103:8080/pothole/AddReport";
	
	public static void report(final Context context, String androidid, double latitude, double longitude, double gforce) {
		
    	Log.i(TAG, String.format("Sending report -> [%s][%f][%f %f]", androidid, gforce, latitude, longitude));
    	Crouton.makeText((Activity) context, R.string.pothole_reporting, Style.INFO).show();
		
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("androidid", androidid);
		params.put("latitude", String.valueOf(latitude));
		params.put("longitude", String.valueOf(longitude));
		params.put("gforce", String.valueOf(gforce));
		client.get(REPORT_URL, params, new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		        if (response.equals("true")) {
			    	Log.i(TAG, String.format("Request Succeeded -> Report Succeeded"));
			    	Crouton.makeText((Activity) context, R.string.pothole_reporting_success, Style.CONFIRM).show();
		        } else {
			    	Log.i(TAG, String.format("Request Succeeded -> Report Failed (%s)", response));
			    	Crouton.makeText((Activity) context, R.string.pothole_reporting_server_error, Style.ALERT).show();
		        }
		    }
		    @Override
		    public void onFailure(Throwable error) {
		    	Log.i(TAG, String.format("Request Failed -> %s", error.getMessage()));
		    	Crouton.makeText((Activity) context, String.format("reporting failed\n%s", error.getMessage()), Style.ALERT).show();
		    }
		});
	} 
}
