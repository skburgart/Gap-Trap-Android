package com.skburgart.pothole;

import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	// Log tag
	private static final String TAG = "MainActivity";
	
	// Tag variables
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	
	// Sensor variables
	private static AccelerometerManager acc;
	private static double gForce;

	// Graph variables
    private static GraphView graphView;
	private static GraphViewSeries accelerometerSeries;
	private static int x;
	
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return fragments
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections select the corresponding tab
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		// Initialize accelerometer
		acc = new AccelerometerManager(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public final static int DETECTOR_VIEW = 0;
		public final static int MAP_VIEW = 1;
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public int getCount() { 
			return 2;
		}

		@Override
		public Fragment getItem(int position) {
			
			switch(position) {
				case DETECTOR_VIEW:
					return new DetectorFragment();
				case MAP_VIEW:
					return new MapFragment();
			}
			
			return null;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
				case DETECTOR_VIEW:
					return getString(R.string.title_detector_fragment).toUpperCase(l);
				case MAP_VIEW:
					return getString(R.string.title_map_fragment).toUpperCase(l);
			}
			return null;
		}
	}

	public static class DetectorFragment extends Fragment{
		
	    private MainActivity mActivity;
	    View rootView;
	    ToggleButton button;

	    @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        mActivity = (MainActivity) activity;
	    }
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_detector, container, false);
			button = (ToggleButton) rootView.findViewById(R.id.buttonDetector);
	        button.setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View v) {
	        		Log.i(TAG, "Clicked detector button");
	        		
	        		if (((ToggleButton) v).isChecked()) {
	        			startAccelerometer();
	        		} else {
		         	    stopAccelerometer();
	        		}
	            }
	        });
	        
	        final float SCALE = getActivity().getResources().getDisplayMetrics().density;
	        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.gforceGraph);
	        accelerometerSeries = new GraphViewSeries(new GraphViewData[] {});
	        graphView = new LineGraphView(getActivity(), "G-Force Monitor");
	        graphView.addSeries(accelerometerSeries);
	        graphView.setManualYAxisBounds(4, 0);
	        graphView.getGraphViewStyle().setNumVerticalLabels(5);
	        graphView.getGraphViewStyle().setNumHorizontalLabels(1);
	        graphView.getGraphViewStyle().setTextSize(SCALE * 16.0f);
	        layout.addView(graphView);

			return rootView;
		}

		@Override
		public void onPause() {
			
			super.onPause();
			stopAccelerometer();
		}
		
		private void stopAccelerometer() {
     	    button.setChecked(false);
     	    acc.start();
     	    accelerometerSeries.resetData(new GraphViewData[] {});
     	    mHandler.removeCallbacks(mGForceTask);
		}
		
		private void startAccelerometer() {
			x = 0;
     	    button.setChecked(true);
     	    acc.start();
     	    mGForceTask.run();
		}
	}
	
	public static class MapFragment extends Fragment{
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_map, container, false);
			return rootView;
		}
	}
	
	public static void updateGForce() {
		
		try {
			gForce = acc.getGForce();
			Log.i(TAG, String.format("G force detected: %f", gForce));	
		} catch (NullPointerException npe) {
			return; // values not yet populated, 
		}
		
		accelerometerSeries.appendData(new GraphViewData(x++, gForce), false, 50);
		graphView.redrawAll();
	}
}

