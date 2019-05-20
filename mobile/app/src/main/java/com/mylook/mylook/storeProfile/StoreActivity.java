package com.mylook.mylook.storeProfile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Store;
import com.mylook.mylook.entities.Visit;
import com.mylook.mylook.utils.SectionsPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity {

    private ViewPager viewPagerStoreInfo;
    private Store store;
    private TabLayout tab;
    private ViewPager viewPagerStoreArticles;
    private StoreInfoFragment infoStoreFragment;
    private StoreContactFragment contactStoreFragment;
    private ReputationFragment reputationFragment;
    private ShopwindowFragment shopwindowFragment;
    private CatalogFragment catalogFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_store_final);
        tab = findViewById(R.id.tab);
        viewPagerStoreInfo = findViewById(R.id.storeInfoViewPager);
        viewPagerStoreArticles = findViewById(R.id.storeViewPager);

        Intent intentStore = getIntent();
        loadStore(intentStore.getStringExtra("Tienda"));

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        super.onCreate(savedInstanceState);
    }

    private void loadStore(String storeName) {

        FirebaseFirestore.getInstance().collection("stores")
                .whereEqualTo("storeName", storeName).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Store> results = new ArrayList<>(task.getResult().toObjects(Store.class));
                        store = results.get(0);
                        setFragments();
                    }
                });
    }

    private void setFragments() {
        getSupportActionBar().setTitle(store.getStoreName());

        Bundle bundle = new Bundle();
        bundle.putString("name", store.getStoreName());
        bundle.putString("photo", store.getProfilePh());
        bundle.putString("description", store.getStoreDescription());
        bundle.putString("facebook", store.getFacebookLink());
        bundle.putString("twitter", store.getTwitterLink());
        bundle.putString("instagram", store.getInstagramLink());
        bundle.putString("phone", store.getStorePhone());
        bundle.putString("location", store.getStoreAddress() + " " + store.getStoreAddressNumber() +
                " - " + store.getStoreFloor() + " - " + store.getStoreCity());
        bundle.putString("email", store.getStoreMail());
        bundle.putString("cover", store.getCoverPh());
        bundle.putString("registerDate", store.getRegisterDate().toString());

        contactStoreFragment = new StoreContactFragment();
        contactStoreFragment.setArguments(bundle);
        infoStoreFragment = new StoreInfoFragment();
        infoStoreFragment.setArguments(bundle);
        setupViewPagerInfo(viewPagerStoreInfo);

        shopwindowFragment = new ShopwindowFragment();
        shopwindowFragment.setArguments(bundle);
        catalogFragment = new CatalogFragment();
        catalogFragment.setArguments(bundle);
        reputationFragment = new ReputationFragment();
        reputationFragment.setArguments(bundle);
        setupViewPagerArticles(viewPagerStoreArticles);
        tab.setupWithViewPager(viewPagerStoreArticles);

        saveVisit();
    }

    private void saveVisit() {
        FirebaseFirestore.getInstance().collection("visits")
                .add(new Visit(store.getStoreName(),
                        FirebaseAuth.getInstance().getCurrentUser().getUid()).toMap())
                .addOnCompleteListener(task -> finish());
    }

    public void moreInfo() {
        viewPagerStoreInfo.setCurrentItem(1);
    }

    private void setupViewPagerInfo(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(infoStoreFragment);
        adapter.addFragment(contactStoreFragment);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
    }

    private void setupViewPagerArticles(ViewPager viewPager) {
        StoreTabAdapter adapter = new StoreTabAdapter(getSupportFragmentManager());
        adapter.addFragment(0, shopwindowFragment, "Vidriera");
        adapter.addFragment(1, catalogFragment, "Catálogo");
        adapter.addFragment(2, reputationFragment, "Reputación");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
