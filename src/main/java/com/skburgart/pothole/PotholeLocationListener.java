package com.skburgart.pothole;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.skburgart.pothole.fragment.DetectorFragment;

public class PotholeLocationListener implements LocationListener{

    // Log tag
    private static final String TAG = "PotholeLocationListener";

    private Location mLocation = null;
    private DetectorFragment mDetectorFragment = null;

    public PotholeLocationListener(DetectorFragment d) {

        mDetectorFragment = d;
    }

    public Location getLocation() {

        return mLocation;
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.i(TAG, String.format("Location updated [%f, %f]", location.getLongitude(), location.getLatitude()));
        mDetectorFragment.updateLocationText(location);
        mLocation = location;
    }

    @Override
    public void onProviderDisabled(String provider) {

        // blank
    }

    @Override
    public void onProviderEnabled(String provider) {

        // blank
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        // blank
    }
}
