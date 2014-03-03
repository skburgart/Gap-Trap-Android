package com.skburgart.pothole;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class AccelerometerManager implements SensorEventListener {

	// Log tag
	private static final String TAG = "AccelerometerManager";
	
	// Sensor variables
	private float[] accValues;
	private static SensorManager mSensorManager;
	private static Sensor mAccelerometer;

	public AccelerometerManager(Context c) {
		super();
	    mSensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
	    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
		// blank
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		accValues = event.values;
	}
	
	public void start() {
		
		Log.i(TAG, "Registering accelerometer");
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
	}
	
	public void stop() {

		Log.i(TAG, "Unegistering accelerometer");
 	    mSensorManager.unregisterListener(this);
	}
	
	public double getGForce() {
		
		if (accValues == null) {
			throw new NullPointerException();
		}
		
		return Math.sqrt(accValues[0] * accValues[0] + accValues[1] * accValues[1] + accValues[2] * accValues[2]) / 9.81;
	}
}
