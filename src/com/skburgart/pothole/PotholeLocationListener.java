package com.skburgart.pothole;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class PotholeLocationListener implements LocationListener{

    // Log tag
    private static final String TAG = "DetectorFragment";
    
    private Location mLocation = null;
    
    public Location getLocation() {
        
        return mLocation;
    }
    
    @Override
    public void onLocationChanged(Location location) {
        
        Log.i(TAG, String.format("Location updated [%f, %f]", location.getLongitude(), location.getLatitude()));
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
