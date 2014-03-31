package com.skburgart.pothole.net;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.skburgart.pothole.net.NetConfig.Callback;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class GetReports {

    private static final String TAG = "GetReports";
    private static final String GET_URL = NetConfig.BASE_URL + "/GetReports";

    public static void get(final Context context, final Callback c) {

        Log.i(TAG, String.format("Getting reports"));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(GET_URL, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Log.i(TAG, String.format("Get reports success"));
                c.processReseponse(response);
            }
            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, String.format("Get reports failed -> %s", error.getMessage()));
                Crouton.makeText((Activity) context, String.format("failed to fetch reports\n%s", error.getMessage()), Style.ALERT).show();
            }
        });
    }
}
