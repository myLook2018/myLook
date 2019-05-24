package com.mylook.mylook.premiumUser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.PremiumUser;
import com.mylook.mylook.storeProfile.StoreTabAdapter;
import com.mylook.mylook.utils.SectionsPagerAdapter;

public class PremiumUserProfileActivity extends AppCompatActivity {


    private FirebaseUser user;
    private TabLayout tab;
    private ViewPager viewPagerUserInfo;
    private ViewPager viewPagerUserPublications;
    private String clientId;
    private PremiumUserInfoFragment infoFragment;
    private ReputationPremiumFragment reputationFragment;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean isCurrentUser;
    private FloatingActionButton fab;
    private String premiumUserId; //el userUID del usuario destacado NO EL ACTUAL

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_final);
        user = FirebaseAuth.getInstance().getCurrentUser();
        tab = findViewById(R.id.tab_store);
        viewPagerUserInfo = findViewById(R.id.storeInfoViewPager);
        viewPagerUserPublications = findViewById(R.id.storeArticlesViewPager);
        Toolbar tb =  findViewById(R.id.toolbar);
        fab=findViewById(R.id.fab);
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
                     fab.setVisibility(View.VISIBLE);

                    }
                    premiumUserId=task.getResult().get("userId").toString();
                }
                infoFragment = new PremiumUserInfoFragment(PremiumUserProfileActivity.this, clientId,isCurrentUser);
                setupViewPagerInfo(viewPagerUserInfo);
                setContentInfo();

            }});
        reputationFragment=new ReputationPremiumFragment(clientId);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewPublicationActivity.class);
                intent.putExtra("clientId",clientId);
                startActivity(intent);
            }
        });
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
                }
            });
        }else{
            db.collection("visits").add(visit.toMap());
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
        adapter.addFragment(0,new PremiumPublicationsFragment(premiumUserId),"Publicaciones");
        adapter.addFragment(1,new PublicClosetFragment(premiumUserId),"Ropero");
        adapter.addFragment(2,reputationFragment,"Reputación");
        viewPager.setAdapter(adapter);


        viewPagerUserPublications.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onPageSelected(int i) {
                switch (i){
                    case 0:
                        fab.setVisibility(View.VISIBLE);
                        break;
                    default:
                        fab.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

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
