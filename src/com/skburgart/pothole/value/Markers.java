package com.skburgart.pothole.value;

import java.util.ArrayList;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Markers {

    private static final String TAG = "Markers";
    private static ArrayList<Marker> markers = new ArrayList<Marker>();
    private static GoogleMap map;
    
    public Markers(GoogleMap m) {
        map = m;
    }
    
    public void add(Report r) {

        Log.d(TAG, "Adding [" + r.getLatitude() + ", " + r.getLongitude() + "]");
        LatLng pos = new LatLng(r.getLatitude(), r.getLongitude());
        Marker m = map.addMarker(new MarkerOptions().position(pos).title("pothole").snippet("gforce " + r.getGforce()));
        markers.add(m);
    }
    
    public void add(Report[] reports) {

        for (Report r : reports) {
            add(r);
        }
    }
    public void clear() {

        Log.i(TAG, "Clearing markers");
        for (Marker m : markers) {
            m.remove();
        }
        
        markers.clear();
    }
}
