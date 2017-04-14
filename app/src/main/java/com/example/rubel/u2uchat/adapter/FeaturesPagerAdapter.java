package com.example.rubel.u2uchat.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by rubel on 4/14/2017.
 */

public class FeaturesPagerAdapter extends FragmentStatePagerAdapter {


    private List<Fragment> appFragments;

    public FeaturesPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.appFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return appFragments.get(position);
    }

    @Override
    public int getCount() {
        return appFragments.size();
    }
}
