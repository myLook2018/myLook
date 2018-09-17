package com.mylook.myapp.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SectionsStatePagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();

    //con HashMap(key,output) me permite obtener por ejemplo, si tengo fragment como key puedo
    // obtener el integer correspondiente a ese fragment

    private final HashMap<Fragment,Integer> mFragments = new HashMap<>();
    private final HashMap<String,Integer> mFragmentsNumbers = new HashMap<>();
    private final HashMap<Integer,String> mFragmentsNames = new HashMap<>();

    public SectionsStatePagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

   public void addFragment(Fragment fragment, String fragmentName){
        mFragmentList.add(fragment);
        //usamos put para el Hashmap
        mFragments.put(fragment,mFragmentList.size()-1);
        mFragmentsNumbers.put(fragmentName, mFragmentList.size()-1);
        mFragmentsNames.put(mFragmentList.size()-1,fragmentName);
    }

    //retorna el num de fragment a partir de su correspondiente nombre
    private  Integer getFragmentNumber(String fragmentName){
        if(mFragmentsNumbers.containsKey(fragmentName)){
            return mFragmentsNumbers.get(fragmentName);
        }else{
            return null;
        }
    }

    //retorna el num de fragment a partir del objeto fragment
    private  Integer getFragmentNumber(Fragment fragment){
        if(mFragmentsNumbers.containsKey(fragment)){
            return mFragmentsNumbers.get(fragment);
        }else{
            return null;
        }
    }

    //retorna el nombre de fragment a partir del num de fragment
    private  String getFragmentName(Integer fragmentNumber){
        if(mFragmentsNames.containsKey(fragmentNumber)){
            return mFragmentsNames.get(fragmentNumber);
        }else{
            return null;
        }
    }


}
