package com.mylook.mylook.storeProfile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Store;
import com.mylook.mylook.entities.Visit;
import com.mylook.mylook.session.MainActivity;
import com.mylook.mylook.utils.SectionsPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity {

    private ViewPager viewPagerStoreInfo;
    private Store store;
    private StoreInfoFragment infoStoreFragment;
    private StoreContactFragment contactStoreFragment;
    private ReputationFragment reputationFragment;
    private ShareActionProvider mShareActionProvider;
    private boolean fromDeepLink;
    private ShopwindowFragment shopwindowFragment;
    private CatalogFragment catalogFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_store_final);
        init();
        super.onCreate(savedInstanceState);
    }

    private void init() {
        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Tienda");
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        invalidateOptionsMenu();
        String storeName = getIncomingIntent();

        //TODO loadVisit();
        // visit = new Visit(storeName, FirebaseAuth.getInstance().getCurrentUser().getUid());
        loadStore(storeName);
    }

    private void loadStore(String storeName) {
        FirebaseFirestore.getInstance().collection("stores")
                .whereEqualTo("storeName", storeName).get()
                .addOnCompleteListener(task -> {
                    Log.d("STORE FOUND", "loadStore: ");
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Store> results = new ArrayList<>(task.getResult().toObjects(Store.class));
                        store = results.get(0);
                        setFragments();
                    }
                });
    }

    private String getIncomingIntent(){
        Intent intentStore = getIntent();
        if (intentStore.hasExtra("store")) {
            fromDeepLink = false;
            return intentStore.getStringExtra("store");
        }
        else {
            try {
                fromDeepLink = true;
                if(intentStore.getData().getQueryParameter("storeName")!=null)
                    return Uri.decode(intentStore.getData().getQueryParameter("storeName"));
                return "";
            } catch (Exception e){
                return "";
                //return Uri.decode(intentStore.getStringExtra("storeName").replace("%20"," "));
            }
        }
    }


    /*private void loadVisit() {
        FirebaseFirestore.getInstance().collection("visits")
                .whereEqualTo("storeName", nombreTiendaPerfil)
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().size() == 0) {
                                visitId = null;
                                visit = new Visit(nombreTiendaPerfil, FirebaseAuth.getInstance().getCurrentUser().getUid());
                                //db.collection("visits").add(visit.toMap());
                            } else {
                                Log.e("OLD VISIT", "ID: " + visitId);
                                visit = task.getResult().getDocuments().get(0).toObject(Visit.class);
                                visitId = task.getResult().getDocuments().get(0).getId();
                            }

                        }
                    }
                });
    }*/

    private void setFragments() {
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setTitle(store.getStoreName());

        Bundle bundle = new Bundle();
        bundle.putString("name", store.getStoreName());
        bundle.putString("photo", store.getProfilePh());
        bundle.putString("description", store.getStoreDescription());
        bundle.putString("facebook", store.getFacebookLink());
        bundle.putString("twitter", store.getTwitterLink());
        bundle.putString("instagram", store.getInstagramLink());
        bundle.putLong("phone", store.getStorePhone());
        bundle.putString("location", store.getStoreAddress() + " " + store.getStoreAddressNumber() +
                " - " + store.getStoreFloor() + " - " + store.getStoreCity());
        bundle.putString("email", store.getStoreMail());
        bundle.putString("cover", store.getCoverPh());
        bundle.putSerializable("registerDate", store.getRegisterDate());

        TabLayout tab = findViewById(R.id.tab);

        infoStoreFragment = new StoreInfoFragment();
        infoStoreFragment.setArguments(bundle);
        contactStoreFragment = new StoreContactFragment();
        contactStoreFragment.setArguments(bundle);
        viewPagerStoreInfo = findViewById(R.id.storeInfoViewPager);
        setupViewPagerInfo(viewPagerStoreInfo);

        shopwindowFragment = new ShopwindowFragment();
        shopwindowFragment.setArguments(bundle);
        catalogFragment = new CatalogFragment();
        catalogFragment.setArguments(bundle);
        reputationFragment = new ReputationFragment();
        reputationFragment.setArguments(bundle);
        ViewPager viewPagerStoreArticles = findViewById(R.id.storeViewPager);
        setupViewPagerArticles(viewPagerStoreArticles);
        tab.setupWithViewPager(viewPagerStoreArticles);

        saveVisit();
    }

    private void saveVisit() {
        FirebaseFirestore.getInstance().collection("visits")
                .add(new Visit(store.getStoreName(), FirebaseAuth.getInstance().getCurrentUser().getUid()).toMap());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.store_menu, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.share_store);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("Title","Item selected");
        int id = item.getItemId();
        if (id == R.id.share_store) {
            Log.e("Share", "Share store");
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Mirá esta tienda wachin! https://www.mylook.com/store?storeName=" + Uri.encode(store.getStoreName()));
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share via"));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fromDeepLink){
            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}