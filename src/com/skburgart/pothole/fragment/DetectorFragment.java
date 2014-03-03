package com.skburgart.pothole.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.skburgart.pothole.AccelerometerManager;
import com.skburgart.pothole.R;
import com.skburgart.pothole.RealtimeGraph;

public class DetectorFragment extends Fragment {

    // Log tag
    private static final String TAG = "DetectorFragment";

    private static final double TRIGGER_GFORCE = 3.25;

    // UI variables
    private static Context mContext;
    private static ToggleButton detectButton;
    private static boolean mTriggered = false;

    // Sensor variables
    private static AccelerometerManager mAccelerometer;
    private static double mGForce;

    // Graph
    private static RealtimeGraph mGraph;

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

        View rootView = inflater.inflate(R.layout.fragment_detector, container, false);
        detectButton = (ToggleButton) rootView.findViewById(R.id.buttonDetector);
        detectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Clicked detector button");

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
        mGraph.reset();
        mHandler.removeCallbacks(mGForceTask);
    }

    private void startDetection() {

        Log.v(TAG, "Starting detection");
        detectButton.setChecked(true);
        mAccelerometer.start();
        mGForceTask.run();
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
            getDialog(mContext).show();
        }
    }
    
    private static void playNotification() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(mContext, notification);
        r.play();
    }
    
    private static Dialog getDialog(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setMessage("Was that a pothole?");
        builder.setTitle("Pothole Detected");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i(TAG, "Pothole confirmed");
                mTriggered = false;
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        mContext = activity;
        mAccelerometer = new AccelerometerManager(activity);
        mGraph = new RealtimeGraph(activity);
    }

    @Override
    public void onPause() {

        super.onPause();
        stopDetection();
    }
}
