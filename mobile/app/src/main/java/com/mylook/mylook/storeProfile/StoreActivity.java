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
import com.google.common.base.Strings;
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

        loadStore(storeName);
    }

    private void loadStore(String storeName) {
        System.out.println(String.format("Loading store with name %s", storeName));
        FirebaseFirestore.getInstance().collection("stores")
                .whereEqualTo("storeName", storeName).get()
                .addOnCompleteListener(task -> {
                    Log.d("STORE FOUND", "Store name: " + storeName);
                    if (task.isSuccessful() && task.getResult() != null) {
                        store = task.getResult().getDocuments().get(0).toObject(Store.class);
                        System.out.println("El nombre de la tienda traida desde firebase es " + store.getStoreName());
                        setFragments();
                    }
                });
    }

    private String getIncomingIntent() {
        String result = "";
        Intent intentStore = getIntent();
        if (intentStore.hasExtra("store")) {
            fromDeepLink = false;
            result = intentStore.getStringExtra("store");
        } else {
            try {
                //Esto es para cuando se quiere entrar desde notificaciones
                fromDeepLink = true;
                if (intentStore.getData().getQueryParameter("storeName") != null)
                    result = Uri.decode(intentStore.getData().getQueryParameter("storeName"));
            } catch (NullPointerException e) {
                System.out.println("El parametro storeName del deepLink es nulo. El mensaje de error es: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error obteniendo el parametro storeName desde el deepLink. El error es: " + e.getMessage());
                //return Uri.decode(intentStore.getStringExtra("storeName").replace("%20"," "));
            }
        }
        return result;
    }

    private void setFragments() {
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();

        //Pongo el nombre de la tienda en la action bar
        if (ab != null) ab.setTitle(store.getStoreName());

        //Se crea el Bundle para pasarle info a los fragmentos
        Bundle bundle = new Bundle();
        bundle.putString("storeName", store.getStoreName());
        bundle.putString("photo", store.getProfilePh());
        bundle.putString("description", store.getStoreDescription());
        bundle.putString("facebook", store.getFacebookLink());
        bundle.putString("twitter", store.getTwitterLink());
        bundle.putString("instagram", store.getInstagramLink());
        bundle.putString("phone", store.getStorePhone());
        //La key location es el nombre de la direccion
        bundle.putString("location", createLocationInfo());
        //Paso la latitud y longitud
        bundle.putDouble("latitude", store.getStoreLatitude());
        bundle.putDouble("longitude", store.getStoreLongitude());
        bundle.putString("email", store.getStoreMail());
        bundle.putString("cover", store.getCoverPh());
        bundle.putSerializable("registerDate", store.getRegisterDate()); //TODO esta info no esta en firebase. No existe como atributo

        TabLayout tab = findViewById(R.id.tab);

        //Se crean los fragmentos de info de la tienda
        infoStoreFragment = new StoreInfoFragment();
        infoStoreFragment.setArguments(bundle);
        contactStoreFragment = new StoreContactFragment();
        contactStoreFragment.setArguments(bundle);
        viewPagerStoreInfo = findViewById(R.id.storeInfoViewPager);
        setupViewPagerInfo(viewPagerStoreInfo);

        //Se crean los fragmentos de Vidriera/Catalogo/Reputacion
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

    private String createLocationInfo() {
        StringBuilder resultLocationInfo = new StringBuilder();
        if (!Strings.isNullOrEmpty(store.getStoreAddress())) {
            resultLocationInfo.append("Direccion: " + store.getStoreAddress());
        } else {
            resultLocationInfo.append("Direccion: N/A");
        }

        if (!Strings.isNullOrEmpty(store.getStoreDept().trim())) {
            resultLocationInfo.append(" - Dpto: " + store.getStoreDept());
        } else {
            resultLocationInfo.append(" - Dpto: N/A");
        }

        if (!Strings.isNullOrEmpty(store.getStoreFloor().trim())) {
            resultLocationInfo.append(" - Piso: " + store.getStoreFloor());
        } else {
            resultLocationInfo.append(" - Piso: N/A");
        }

        if (!Strings.isNullOrEmpty(store.getStoreTower())) {
            resultLocationInfo.append(" - Torre: " + store.getStoreTower());
        } else {
            resultLocationInfo.append(" - Torre: N/A");
        }

        return resultLocationInfo.toString();
    }

    private void saveVisit() {
        try {
            FirebaseFirestore.getInstance().collection("visits")
                    .add(new Visit(store.getStoreName(), FirebaseAuth.getInstance().getCurrentUser().getUid()).toMap());
            Log.d("SAVE VISIT", "Se guardo la visita para la tienda " + store.getStoreName());
        } catch (Exception e) {
            Log.d("SAVE VISIT", String.format("No se pudo agregar la visita a la tienda %s. El error fue: %s", store.getStoreName(), e.getMessage()));

        }
    }

    public void moreInfo() {
        viewPagerStoreInfo.setCurrentItem(1);
    }

    public void returnToStoreInfo() {
        viewPagerStoreInfo.setCurrentItem(0);
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
        Log.e("Title", "Item selected");
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
        if (fromDeepLink) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}