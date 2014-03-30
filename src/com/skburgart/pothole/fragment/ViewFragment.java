package com.skburgart.pothole.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.skburgart.pothole.AccelerometerManager;
import com.skburgart.pothole.GPSManager;
import com.skburgart.pothole.R;
import com.skburgart.pothole.RealtimeGraph;
import com.skburgart.pothole.net.GetReports;
import com.skburgart.pothole.net.Report;
import com.skburgart.pothole.net.NetConfig.Callback;

public class ViewFragment extends Fragment {

    private static final String TAG = "ViewFragment";
    private static final LatLng DEFAULT_LOCATION = new LatLng(38.989, -76.942); // College Park
    private static final int DEFAULT_ZOOM = 10;
    
    private Context mContext;
    private GoogleMap map;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_view, container, false);   
        
        map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
        
        GetReports.get(mContext, new GetReportsCallBack());
        
        return rootView;
    }
    
    private class GetReportsCallBack implements Callback {

        @Override
        public void getReseponse(String response) {
            
            Gson gson = new Gson();
            Report[] reports = gson.fromJson(response, Report[].class);

            for (Report r : reports) {
                plotReport(r);
            }
        }
    }
    
    private void plotReport(Report r) {

        Log.i(TAG, "Plotting [" + r.getLatitude() + ", " + r.getLongitude() + "]");
        
        LatLng pos = new LatLng(r.getLatitude(), r.getLongitude());
        map.addMarker(new MarkerOptions().position(pos));
    }
    
    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        mContext = activity;
    }
}
