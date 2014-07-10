package com.skburgart.pothole.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.skburgart.pothole.AccelerometerManager;
import com.skburgart.pothole.GPSManager;
import com.skburgart.pothole.R;
import com.skburgart.pothole.RealtimeGraph;
import com.skburgart.pothole.net.SendReport;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class DetectorFragment extends Fragment {

    // Log tag
    private static final String TAG = "DetectorFragment";
    private static final double TRIGGER_GFORCE = 3.25;
    private static String ANDROID_ID;

    // UI variables
    private static TextView mLocationText;
    private static Context mContext;
    private static ToggleButton detectButton;
    private static RealtimeGraph mGraph;
    private static boolean mTriggered = false;

    // Sensor variables
    private static GPSManager mGPS;
    private static Location mLocation;
    private static AccelerometerManager mAccelerometer;
    private static double mGForce;

    // Timer variables
    private final static int INTERVAL = 20; // milliseconds
    private final static Handler mHandler = new Handler();
    private final static Runnable mGForceTask = new Runnable() {
        @Override
        public void run() {
            updateGForce();
            mHandler.postDelayed(mGForceTask, INTERVAL);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "Infalting detector fragment");
        View rootView = inflater.inflate(R.layout.fragment_detector, container, false);
        mLocationText = (TextView) rootView.findViewById(R.id.locationText);
        detectButton = (ToggleButton) rootView.findViewById(R.id.buttonDetector);
        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked detector button");

                if (((ToggleButton) v).isChecked()) {
                    startDetection();
                } else {
                    stopDetection();
                }
            }
        });

        // Add real time graph
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.gforceGraph);
        layout.addView(mGraph.getView());

        return rootView;
    }

    private void stopDetection() {

        Log.v(TAG, "Stopping detection");
        detectButton.setChecked(false);
        mAccelerometer.stop();
        mGPS.stop();
        mGraph.reset();
        mLocationText.setText("");
        mHandler.removeCallbacks(mGForceTask);
    }

    private void startDetection() {

        Log.d(TAG, "Starting detection");
        detectButton.setChecked(true);
        mAccelerometer.start();
        mGPS.start();
        mGForceTask.run();
        mLocationText.setText(R.string.pothole_location_loading);
        mTriggered = false;
    }

    public static void updateGForce() {

        try {
            mGForce = mAccelerometer.getGForce();
        } catch (NullPointerException npe) {
            return; // values not yet populated,
        }

        mGraph.update(mGForce);

        if (mGForce >= TRIGGER_GFORCE && !mTriggered) {

            Log.i(TAG, "Pothole detected");
            mTriggered = true;
            playNotification();
            getDialog(mContext, mGForce).show();
        }
    }

    private static void playNotification() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(mContext, notification);
        r.play();
    }

    private static Dialog getDialog(Context c, final double gForce) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setMessage(R.string.pothole_alert_message);
        builder.setTitle(R.string.pothole_alert_title);
        builder.setPositiveButton(R.string.pothole_alert_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Log.i(TAG, "Pothole confirmed");
                mLocation = mGPS.getLocation();

                if (mLocation == null) {
                    Log.w(TAG, "Location not ready");
                    Crouton.makeText((Activity) mContext, R.string.pothole_location_not_ready, Style.ALERT).show();
                } else {
                    SendReport.report(mContext, ANDROID_ID, mLocation.getLatitude(), mLocation.getLongitude(), gForce);
                }
                mTriggered = false;
            }
        });
        builder.setNegativeButton(R.string.pothole_alert_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Log.i(TAG, "Pothole rejected");
                mTriggered = false;
            }
        });
        builder.setOnCancelListener(new AlertDialog.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.i(TAG, "Pothole dialog dismissed");
                mTriggered = false;
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }

    public void updateLocationText(Location l) {

        mLocationText.setText(String.format("location %.03f %.03f within %.02fm", l.getLatitude(), l.getLongitude(), l.getAccuracy()));
    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        mContext = activity;
        mAccelerometer = new AccelerometerManager(activity);
        mGraph = new RealtimeGraph(activity);
        mGPS = new GPSManager(activity, this);
        ANDROID_ID = Secure.getString(activity.getContentResolver(), Secure.ANDROID_ID);
    }

    @Override
    public void onPause() {

        super.onPause();
        stopDetection();
    }
}
