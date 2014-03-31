package com.skburgart.pothole.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.skburgart.pothole.R;
import com.skburgart.pothole.net.GetReports;
import com.skburgart.pothole.net.NetConfig.Callback;
import com.skburgart.pothole.value.Markers;
import com.skburgart.pothole.value.Report;

public class ViewFragment extends Fragment {

    private static final String TAG = "ViewFragment";
    private static final LatLng DEFAULT_LOCATION = new LatLng(38.989, -76.942); // College Park
    private static final int DEFAULT_ZOOM = 10;

    private Context mContext;
    private GoogleMap mMap;
    private Markers mMarkers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_view, container, false);
        Log.d(TAG, "Infalting view fragment");

        mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
        mMarkers = new Markers(mMap);

        return rootView;
    }

    private class GetReportsCallBack implements Callback {

        @Override
        public void getReseponse(String response) {

            Gson gson = new Gson();
            Report[] reports = gson.fromJson(response, Report[].class);
            mMarkers.add(reports);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        GetReports.get(mContext, new GetReportsCallBack());
    };

    @Override
    public void onPause() {
        super.onPause();

        mMarkers.clear();
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mContext = activity;
    }
}
