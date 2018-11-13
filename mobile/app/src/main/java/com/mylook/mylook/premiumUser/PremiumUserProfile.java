package com.mylook.mylook.premiumUser;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.PremiumUser;
import com.mylook.mylook.storeProfile.CatalogFragment;
import com.mylook.mylook.storeProfile.ShopwindowFragment;
import com.mylook.mylook.storeProfile.StoreTabAdapter;
import com.mylook.mylook.utils.SectionsPagerAdapter;

public class PremiumUserProfile extends AppCompatActivity {


    private FirebaseUser user;
    private TabLayout tab;
    private ViewPager viewPagerUserInfo;
    private ViewPager viewPagerUserPublications;
    private String clientId;
    private PremiumUserInfoFragment infoFragment;
    private ReputationPremiumFragment reputationFragment;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean isCurrentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_final);
        user = FirebaseAuth.getInstance().getCurrentUser();
        tab = findViewById(R.id.tab);
        viewPagerUserInfo = findViewById(R.id.storeInfoViewPager);
        viewPagerUserPublications = findViewById(R.id.storeViewPager);
        Toolbar tb =  findViewById(R.id.toolbar);
        tb.setTitle("Usuario Destacado");
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        Intent intentStore = getIntent();
        clientId = intentStore.getStringExtra("clientId");
        isCurrentUser=false;
        db.collection("clients").document(clientId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().get("userId").toString().equals(user.getUid())){
                     isCurrentUser=true;
                    }
                }
                infoFragment = new PremiumUserInfoFragment(PremiumUserProfile.this, clientId,isCurrentUser);
                setupViewPagerInfo(viewPagerUserInfo);
                setContentInfo();

            }});
        reputationFragment=new ReputationPremiumFragment(clientId);

    }
    private void setContentInfo(){
        db.collection("premiumUsers").whereEqualTo("clientId",clientId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Log.d("info de firebase", "onComplete: " + task.getResult().toObjects(Store.class));
                    PremiumUser user = task.getResult().getDocuments().get(0).toObject(PremiumUser.class);
                    infoFragment.setTxtLocalization(user.getLocalization());
                    infoFragment.setOnClickFacebook(user.getLinkFacebook());
                    infoFragment.setOnClickInstagram(user.getLinkInstagram());
                    infoFragment.setPremiumName(user.getUserName());
                    infoFragment.setProfilePhoto(user.getProfilePhoto());
                    infoFragment.setTxtEmail(user.getContactMail());
                    reputationFragment.setRegisterDate(user.getPremiumDate());
                    setupViewPager(viewPagerUserPublications);
                    tab.setupWithViewPager(viewPagerUserPublications);
                } else {
                    Log.d("Firestore task", "onComplete: " + task.getException());
                }
            }
        });

    }
   /* private void loadVisit() {
        db.collection("visits").whereEqualTo("storeName", clientId).whereEqualTo("userId",user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().getDocuments().size()==0){
                                visitId=null;
                                visit=new Visit(clientId,user.getUid(),1);
                                //db.collection("visits").add(visit.toMap());

                            }else{
                                Log.e("OLD VISIT","ID: " +visitId);
                                visit = null;
                                visitId=null;
                                visit=task.getResult().getDocuments().get(0).toObject(Visit.class);
                                visit.toVisit();
                                visitId=task.getResult().getDocuments().get(0).getId();
                            }

                        }
                    }
                });

    }
    private void saveVisit(){
        if(visitId!=null){
            Log.e("VISIT","ID: " +visitId);
            db.collection("visits").document(visitId).set(visit.toMap(), SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    finish();
                }
            });
        }else{
            db.collection("visits").add(visit.toMap()).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    finish();
                }
            });
        }
    }
    */
    @Override
    protected void onStop() {
        super.onStop();
        //saveVisit();
    }

    public void moreInfo(){
        viewPagerUserInfo.setCurrentItem(1);
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
        adapter.addFragment(0,new ShopwindowFragment("Baja cali Cba",null),"Publicaciones");
        adapter.addFragment(1,new CatalogFragment("Baja cali Cba"),"Ropero");
        adapter.addFragment(2,reputationFragment,"Reputación");
        viewPager.setAdapter(adapter);
    }
    private void setupViewPagerInfo(ViewPager viewPager){
        SectionsPagerAdapter adapter=new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(infoFragment);
        viewPagerUserInfo.setAdapter(adapter);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
