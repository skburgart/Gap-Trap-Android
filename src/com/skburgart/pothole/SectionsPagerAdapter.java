package com.skburgart.pothole;

import java.util.Locale;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.skburgart.pothole.fragment.DetectorFragment;
import com.skburgart.pothole.fragment.ViewFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public final static int DETECTOR_FRAGMENT = 0;
    public final static int VIEW_FRAGMENT = 1;

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
            case DETECTOR_FRAGMENT:
                return new DetectorFragment();
            case VIEW_FRAGMENT:
                return new ViewFragment();
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        Locale l = Locale.getDefault();

        switch (position) {
            case DETECTOR_FRAGMENT:
                return mParent.getString(R.string.title_detector_fragment).toUpperCase(l);
            case VIEW_FRAGMENT:
                return mParent.getString(R.string.title_map_fragment).toUpperCase(l);
        }

        return null;
    }
}
