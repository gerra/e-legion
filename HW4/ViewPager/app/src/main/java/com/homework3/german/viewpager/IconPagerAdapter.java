package com.homework3.german.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by root on 13.10.15.
 */
public class IconPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments;

    public IconPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new LinkedList<>();
        mFragments.add(PlaceholderFragment.newInstance(R.drawable.android));
        mFragments.add(PlaceholderFragment.newInstance(R.drawable.apple));
        mFragments.add(PlaceholderFragment.newInstance(R.drawable.github));
        mFragments.add(PlaceholderFragment.newInstance(R.drawable.playmarket));
    }

    public void removePage(int position) {
        mFragments.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
