package com.mylook.mylook.storeProfile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Store;
import com.mylook.mylook.entities.Visit;
import com.mylook.mylook.utils.SectionsPagerAdapter;

import java.util.ArrayList;

public class StoreActivity extends AppCompatActivity {

    private static ViewPager viewPagerStoreInfo;
    private Context mContext = StoreActivity.this;
    private ImageView backArrow, storePhoto;
    private GridView gridArticlesStore;
    private String nombreTiendaPerfil;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int NUM_COLUMNS = 3;
    private ArrayList<Store> storeList;
    private Visit visit;
    private String visitId=null;
    private FirebaseUser user;
    private TabLayout tab;
    private ViewPager viewPagerStoreArticles;
    //private ViewPager viewPagerStoreInfo;
    private StoreInfoFragment infoStoreFragment;
    private StoreContactFragment contactStoreFragment;
    private String coverPh;
    private ReputationFragment reputationFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_final);
        user = FirebaseAuth.getInstance().getCurrentUser();
        loadVisit();
        backArrow = findViewById(R.id.backArrow);
        tab = findViewById(R.id.tab);
        viewPagerStoreInfo = findViewById(R.id.storeInfoViewPager);
        viewPagerStoreArticles = findViewById(R.id.storeViewPager);

        storeList = new ArrayList<Store>();
        Intent intentStore = getIntent();
        nombreTiendaPerfil = intentStore.getStringExtra("Tienda");
        contactStoreFragment = new StoreContactFragment(StoreActivity.this, nombreTiendaPerfil);
        infoStoreFragment = new StoreInfoFragment(StoreActivity.this, nombreTiendaPerfil);
        reputationFragment=new ReputationFragment(nombreTiendaPerfil);

        setupViewPagerInfo(viewPagerStoreInfo);

        db.collection("stores").whereEqualTo("storeName", nombreTiendaPerfil)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Log.d("info de firebase", "onComplete: " + task.getResult().toObjects(Store.class));
                    storeList.addAll(task.getResult().toObjects(Store.class));
                    Store storeAux = storeList.get(0);
                    contactStoreFragment.setStoreLocation(storeAux.getStoreCity() + ", " + storeAux.getStoreAddress() + ", " +
                            storeAux.getStoreAddressNumber() + " " + storeAux.getStoreFloor());
                    contactStoreFragment.setStorePhone(storeAux.getStorePhone());
                    contactStoreFragment.setOnClickFacebook(storeAux.getFacebookLink());
                    contactStoreFragment.setOnClickInstagram(storeAux.getInstagramLink());
                    contactStoreFragment.setOnClickTwitter(storeAux.getTwitterLink());
                    infoStoreFragment.setStoreName(storeAux.getStoreName());
                    infoStoreFragment.setTxtDescription(storeAux.getStoreDescription());
                    infoStoreFragment.setStorePhoto(storeAux.getProfilePh());
                    coverPh=storeAux.getCoverPh();
                    reputationFragment.setRegisterDate(storeAux.getRegisterDate());
                    setupViewPager(viewPagerStoreArticles);
                    tab.setupWithViewPager(viewPagerStoreArticles);
                } else {
                    Log.d("Firestore task", "onComplete: " + task.getException());
                }
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void loadVisit() {
        db.collection("visits").whereEqualTo("storeName",nombreTiendaPerfil).whereEqualTo("userId",user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().getDocuments().size()==0){
                                visit=new Visit(nombreTiendaPerfil,user.getUid(),1);
                                db.collection("visits").add(visit.toMap());

                            }else{
                                visit=task.getResult().getDocuments().get(0).toObject(Visit.class);
                                visit.toVisit();
                                visitId=task.getResult().getDocuments().get(0).getId();
                                db.collection("visits").document(visitId).set(visit.toMap(), SetOptions.merge());

                            }
                        }
                    }
                });

    }
    private void saveVisit(){
        if(visitId!=null){
            Log.e("VISIT","ID: " +visitId);
            db.collection("visits").document(visitId).set(visit.toMap(), SetOptions.merge());
        }else{
            db.collection("visits").add(visit.toMap());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
       // saveVisit();
    }

    public static void moreInfo(){
        viewPagerStoreInfo.setCurrentItem(1);
    }





   /* private void setupGridView() {

        Log.d("Store gridView", "setupGridView: Setting up store grid.");
        final ArrayList<Article> storeArticles = new ArrayList<Article>();
        db.collection("articles").whereEqualTo("storeName", nombreTiendaPerfil).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    storeArticles.addAll(task.getResult().toObjects(Article.class));

                    int widthGrid = getResources().getDisplayMetrics().widthPixels;
                    int imageWidth = widthGrid / NUM_COLUMNS;
                    gridArticlesStore.setColumnWidth(imageWidth);

                    ArrayList<String> articlesPhotosUrls = new ArrayList<String>();
                    for (int i = 0; i < storeArticles.size(); i++) {
                        articlesPhotosUrls.add(storeArticles.get(i).getPicture());
                    }

                    GridImageAdapter gridAdapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview, articlesPhotosUrls);
                    gridArticlesStore.setAdapter(gridAdapter);

                } else {
                    Log.d("Firestore task", "onComplete: " + task.getException());
                }
            }
        });

    }*/


    /**
     * Crea una instancia del view pager con los datos
     * predeterminados
     *
     * @param viewPager Nueva instancia
     */
    private void setupViewPager(ViewPager viewPager) {
        StoreTabAdapter adapter = new StoreTabAdapter(getSupportFragmentManager(),3);
        Log.e("VIEW PAGER","CARGAAAAAAAAAA");
        adapter.addFragment(0,new ShopwindowFragment(nombreTiendaPerfil,coverPh),"Vidriera");
        adapter.addFragment(1,new CatalogFragment(nombreTiendaPerfil,coverPh),"Catalogo");


        adapter.addFragment(2,reputationFragment,"Reputación");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        viewPager.setCurrentItem(0);
    }
    private void setupViewPagerInfo(ViewPager viewPager){
        SectionsPagerAdapter adapter=new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(infoStoreFragment);

        adapter.addFragment(contactStoreFragment);
        viewPagerStoreInfo.setAdapter(adapter);
    }
}
