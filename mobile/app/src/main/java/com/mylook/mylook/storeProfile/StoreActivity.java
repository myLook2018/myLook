package com.mylook.mylook.storeProfile;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Store;
import com.mylook.mylook.entities.Visit;
import com.mylook.mylook.utils.SectionsPagerAdapter;

import java.util.Date;

public class StoreActivity extends AppCompatActivity {

    private static ViewPager viewPagerStoreInfo;
    private TabLayout tab;
    private String nombreTiendaPerfil;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Visit visit;
    private String visitId = null;
    private FirebaseUser user;
    private ViewPager viewPagerStoreArticles;
    private String coverPh;
    private StoreInfoFragment infoStoreFragment;
    private StoreContactFragment contactStoreFragment;
    private ReputationFragment reputationFragment;
    private Date registerDateReputacion;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_final);
        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Tienda");
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        tab = findViewById(R.id.tab_store);

        user = FirebaseAuth.getInstance().getCurrentUser();

        viewPagerStoreInfo = findViewById(R.id.storeInfoViewPager);
        viewPagerStoreArticles = findViewById(R.id.storeArticlesViewPager);

        Intent intentStore = getIntent();
        nombreTiendaPerfil = intentStore.getStringExtra("Tienda");
        System.out.println("Store name: " + nombreTiendaPerfil);

        contactStoreFragment = new StoreContactFragment(StoreActivity.this, nombreTiendaPerfil);
        infoStoreFragment = new StoreInfoFragment(StoreActivity.this, nombreTiendaPerfil);
        reputationFragment = new ReputationFragment(nombreTiendaPerfil);
        //loadVisit();
        visit = new Visit(nombreTiendaPerfil, user.getUid());

        setupViewPagerInfo(viewPagerStoreInfo);

        db.collection("stores").whereEqualTo("storeName", nombreTiendaPerfil)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("info de firebase", "onComplete: " + task.getResult().toObjects(Store.class).toString());
                    Store storeAux = task.getResult().getDocuments().get(0).toObject(Store.class);
                    contactStoreFragment.setStoreLocation(storeAux.getStoreCity() + ", " + storeAux.getStoreAddress() + ", " +
                            storeAux.getStoreAddressNumber() + " " + storeAux.getStoreFloor());
                    contactStoreFragment.setStorePhone(storeAux.getStorePhone());
                    contactStoreFragment.setOnClickFacebook(storeAux.getFacebookLink());
                    contactStoreFragment.setOnClickInstagram(storeAux.getInstagramLink());
                    contactStoreFragment.setOnClickTwitter(storeAux.getTwitterLink());

                    infoStoreFragment.setStoreName(storeAux.getStoreName());
                    infoStoreFragment.setTxtDescription(storeAux.getStoreDescription());
                    infoStoreFragment.setStorePhoto(storeAux.getProfilePh());
                    coverPh = storeAux.getCoverPh();

                    reputationFragment.setRegisterDate(storeAux.getRegisterDate());

                    setupViewPager(viewPagerStoreArticles);
                    tab.setupWithViewPager(viewPagerStoreArticles);

                } else {
                    Log.d("Firestore task", "onComplete: " + task.getException());
                }
            }
        });

    }

    private void saveVisit() {
        db.collection("visits").add(visit.toMap());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("store activity", "onStop: ENTRO");
        saveVisit();
    }

    public static void moreInfo() {
        Log.d("store activity", "moreInfo: ENTRO");
        viewPagerStoreInfo.setCurrentItem(1);
    }


    /**
     * Crea una instancia del view pager con los datos
     * predeterminados
     *
     * @param viewPager Nueva instancia
     */
    private void setupViewPager(ViewPager viewPager) {
        Log.e("VIEW PAGER", "CARGAAAAAAAAAA");
        StoreTabAdapter adapter = new StoreTabAdapter(getSupportFragmentManager(), 3);
        adapter.addFragment(0, new ShopwindowFragment(nombreTiendaPerfil, coverPh), "Vidriera");
        adapter.addFragment(1, new CatalogFragment(nombreTiendaPerfil), "Catalogo");
        adapter.addFragment(2, reputationFragment, "Reputación");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        Log.d("view pager adapter tabs", "adapter del fragmento: " + adapter.getPageTitle(0));

    }

    private void setupViewPagerInfo(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(infoStoreFragment);
        adapter.addFragment(contactStoreFragment);
        viewPagerStoreInfo.setAdapter(adapter);
        Log.d("view pager info store", "setupViewPagerInfo: ENTRO");
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d("Store activity", "onSupportNavigateUp: ENTRO ");
        finish();
        return true;
    }
}
