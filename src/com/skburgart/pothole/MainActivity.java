package com.skburgart.pothole;

import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
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

public class MainActivity extends FragmentActivity {

	// Log tag
	private static final String TAG = "MainActivity";
	
	// Tab variables
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	
	// Sensor variables
	private static AccelerometerManager acc;
	private static double gForce;
	
	// Graph
	private static RealtimeGraph graph;
	
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
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

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
					 .setTabListener(new SimpleTabListener(mViewPager)));
		}
		
		acc = new AccelerometerManager(this);
		graph = new RealtimeGraph(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public static class DetectorFragment extends Fragment{
		
	    View rootView;
	    ToggleButton button;

	    @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	    }
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_detector, container, false);
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
	        
	        // Add real time graph
	        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.gforceGraph);
	        layout.addView(graph.getView());

			return rootView;
		}

		@Override
		public void onPause() {
			
			super.onPause();
			stopDetection();
		}
		
		private void stopDetection() {
     	    button.setChecked(false);
     	    acc.stop();
     	    graph.reset();
     	    mHandler.removeCallbacks(mGForceTask);
		}
		
		private void startDetection() {
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
		
		graph.update(gForce);
	}
}

