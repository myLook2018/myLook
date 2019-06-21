package com.mylook.mylook.storeProfile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageView;

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
import com.mylook.mylook.session.MainActivity;
import com.mylook.mylook.utils.SectionsPagerAdapter;

import java.net.URI;
import java.net.URLEncoder;
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
    private ShareActionProvider mShareActionProvider;
    private boolean fromDeepLink = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_final);
        user = FirebaseAuth.getInstance().getCurrentUser();
        tab = findViewById(R.id.tab);
        viewPagerStoreInfo = findViewById(R.id.storeInfoViewPager);
        viewPagerStoreArticles = findViewById(R.id.storeViewPager);

        Toolbar tb =  findViewById(R.id.toolbar);
        tb.setTitle("Tienda");
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        invalidateOptionsMenu();
        storeList = new ArrayList<Store>();
        getIncomingIntent();
        contactStoreFragment = new StoreContactFragment(StoreActivity.this, nombreTiendaPerfil);
        infoStoreFragment = new StoreInfoFragment(StoreActivity.this, nombreTiendaPerfil);
        reputationFragment=new ReputationFragment(nombreTiendaPerfil);
        //loadVisit();
        visit=new Visit(nombreTiendaPerfil,user.getUid());
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



    }

    private void getIncomingIntent(){
        Intent intentStore = getIntent();
        if(intentStore.hasExtra("Tienda")) {
            nombreTiendaPerfil = intentStore.getStringExtra("Tienda");
            fromDeepLink = false;
        }
        else {
            try {
                fromDeepLink = true;
                nombreTiendaPerfil = Uri.decode(intentStore.getData().getQueryParameter("storeName"));
            } catch (Exception e){
                nombreTiendaPerfil= Uri.decode(intentStore.getStringExtra("storeName").replace("%20"," "));
            }
        }
    }


    private void loadVisit() {
        db.collection("visits").whereEqualTo("storeName",nombreTiendaPerfil).whereEqualTo("userId",user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().getDocuments().size()==0){
                                visitId=null;
                                visit=new Visit(nombreTiendaPerfil,user.getUid());
                                //db.collection("visits").add(visit.toMap());

                            }else{
                                Log.e("OLD VISIT","ID: " +visitId);
                                visit = null;
                                visitId=null;
                                visit=task.getResult().getDocuments().get(0).toObject(Visit.class);
                                visitId=task.getResult().getDocuments().get(0).getId();
                                }

                            }
                        }
                    });

    }
    private void saveVisit(){
        db.collection("visits").add(visit.toMap()).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
        @Override
        public void onComplete(@NonNull Task<DocumentReference> task) {
           finish();
        }});

    }

    @Override
    protected void onStop() {
        super.onStop();
       saveVisit();
    }

    public static void moreInfo(){
        viewPagerStoreInfo.setCurrentItem(1);
    }


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
        adapter.addFragment(1,new CatalogFragment(nombreTiendaPerfil),"Catalogo");


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
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.store_menu, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.share_store);

        // Fetch and store ShareActionProvider
        //mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
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
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Mirá esta tienda wachin! https://www.mylook.com/store?storeName=" + Uri.encode(nombreTiendaPerfil));
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share via"));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(fromDeepLink){
            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
