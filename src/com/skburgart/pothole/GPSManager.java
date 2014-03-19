package com.skburgart.pothole;

import com.skburgart.pothole.fragment.DetectorFragment;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class GPSManager {
    
    private LocationManager mLocationMangaer;  
    private PotholeLocationListener mLocationListener;
    
    public GPSManager(Context c, DetectorFragment d) {
        
        mLocationMangaer = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);  
        mLocationListener = new PotholeLocationListener(d);  
    }
    
    public void start() {
        mLocationMangaer.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 10, mLocationListener);  
    }
    
    public void stop() {
        
        mLocationMangaer.removeUpdates(mLocationListener);
    }
    
    public Location getLocation() {
        return mLocationListener.getLocation();
    }
    
}