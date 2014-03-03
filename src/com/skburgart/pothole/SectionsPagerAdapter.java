package com.skburgart.pothole;

import java.util.Locale;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.skburgart.pothole.fragment.DetectorFragment;
import com.skburgart.pothole.fragment.MapFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public final static int DETECTOR_VIEW = 0;
    public final static int MAP_VIEW = 1;

    private final MainActivity mParent;

    public SectionsPagerAdapter(FragmentManager fm, MainActivity parent) {

        super(fm);
        mParent = parent;
    }

    @Override
    public int getCount() {

        return 2;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
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
                return mParent.getString(R.string.title_detector_fragment).toUpperCase(l);
            case MAP_VIEW:
                return mParent.getString(R.string.title_map_fragment).toUpperCase(l);
        }

        return null;
    }
}
