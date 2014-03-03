package com.skburgart.pothole.fragment;

import android.app.Activity;
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
import com.skburgart.pothole.MainActivity;
import com.skburgart.pothole.R;
import com.skburgart.pothole.RealtimeGraph;

public class DetectorFragment extends Fragment{

	// Log tag
	private static final String TAG = "DetectorFragment";
	
	// Activity variables
	private MainActivity mParent;
    private ToggleButton button;

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
		button = (ToggleButton) rootView.findViewById(R.id.buttonDetector);
        button.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		Log.i(TAG, "Clicked detector button");
        		
        		if (((ToggleButton) v).isChecked()) {
        			startDetection();
        		} else {
	         	    stopDetection();
        		}
            }
        });
        
		mAccelerometer = new AccelerometerManager(mParent);
		mGraph = new RealtimeGraph(mParent);
        
        // Add real time graph
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.gforceGraph);
        layout.addView(mGraph.getView());

		return rootView;
	}
	
	private void stopDetection() {
 	    button.setChecked(false);
 	    mAccelerometer.stop();
 	    mGraph.reset();
 	    mHandler.removeCallbacks(mGForceTask);
	}
	
	private void startDetection() {
 	    button.setChecked(true);
 	    mAccelerometer.start();
 	    mGForceTask.run();
	}
	
	public static void updateGForce() {
		
		try {
			mGForce = mAccelerometer.getGForce();
			Log.i(TAG, String.format("G force detected: %f", mGForce));	
		} catch (NullPointerException npe) {
			return; // values not yet populated, 
		}
		
		mGraph.update(mGForce);
	}
	
	public DetectorFragment setParent(MainActivity parent) {
		
		mParent = parent;
		return this;
	}
    
    @Override
    public void onAttach(Activity activity) {
    	
        super.onAttach(activity);
    }

	@Override
	public void onPause() {
		
		super.onPause();
		stopDetection();
	}
}