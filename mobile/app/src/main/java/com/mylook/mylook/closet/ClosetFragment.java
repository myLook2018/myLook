package com.mylook.mylook.closet;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mylook.mylook.R;

public class ClosetFragment extends Fragment {

    private TabLayout tabLayout;
    private static FavoritesTab favoritesTab;
    private static OutfitsTab outfitsTab;
    private static ClosetFragment closetInstance;

    public static ClosetFragment getInstance() {
        if (closetInstance == null) {
            closetInstance = new ClosetFragment();
            favoritesTab = new FavoritesTab();
            outfitsTab = new OutfitsTab();
        }
        return closetInstance;
    }

    public static void refreshStatus() {
        if (closetInstance != null) {
            favoritesTab.refreshStatus();
        }
    }

    public ClosetFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_closet_tablayout, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createTabLayout(view);
    }

    private void createTabLayout(View view) {
        tabLayout = view.findViewById(R.id.tablayout);
        ViewPager viewPager = view.findViewById(R.id.closetViewPager);
        setupViewPager(viewPager);
    }


    private void setupViewPager(ViewPager viewPager) {
        ClosetTabAdapter adapter = new ClosetTabAdapter(getChildFragmentManager());
        adapter.addFragment(favoritesTab, "Prendas");
        adapter.addFragment(outfitsTab, "Conjuntos");
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        tabLayout.setupWithViewPager(viewPager);
    }

    public void clear() {
        closetInstance=null;
    }
}