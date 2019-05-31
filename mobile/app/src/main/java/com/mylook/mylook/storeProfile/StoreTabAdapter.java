package com.mylook.mylook.storeProfile;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class StoreTabAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private final ArrayList<String> mFragmentTitleList = new ArrayList<>();

    public StoreTabAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        Log.d("Constructor", "StoreTabAdapter: ENTRO");
    }

    public void addFragment(int index, Fragment fragment, String title) {
        Log.d("Store Tab Adapter", "addFragment: " + index);
        Log.d("Store Tab Adapter", "addFragment: " + title);
        mFragmentList.add(index, fragment);
        mFragmentTitleList.add(index, title);
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("Store tab adapter", "getItem: ENTRO a la posicion " + position);
        switch (position) {
            case 0:
                Log.d("case 0", "getItem: ENTRO" + mFragmentTitleList.get(0));
                Log.d("switch", "getItem: Size list" + getCount());
                return mFragmentList.get(0);
            case 1:
                Log.d("case 1", "getItem: ENTRO" + mFragmentTitleList.get(1));
                Log.d("switch", "getItem: Size list" + getCount());
                return mFragmentList.get(1);
            case 2:
                Log.d("case 2", "getItem: ENTRO" + mFragmentTitleList.get(2));
                Log.d("switch", "getItem: Size list" + getCount());
                return mFragmentList.get(2);
            default:
                return null;
        }
        //return mFragmentList.get(position);
    }


    @Override
    public int getCount() {
        return mFragmentList.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
