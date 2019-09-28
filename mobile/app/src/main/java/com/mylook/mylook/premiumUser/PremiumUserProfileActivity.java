package com.mylook.mylook.premiumUser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.PremiumUser;
import com.mylook.mylook.storeProfile.StoreTabAdapter;
import com.mylook.mylook.utils.SectionsPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PremiumUserProfileActivity extends AppCompatActivity {


    private PremiumUser premiumUser;
    private TabLayout tab;
    private ViewPager viewPagerUserInfo;
    private String clientId;
    private PremiumUserInfoFragment infoFragment;
    private ReputationPremiumFragment reputationFragment;
    private PremiumPublicationsFragment publicationsFragment;
    private PublicClosetFragment publicClosetFragment;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean isCurrentUser=false;
    private FloatingActionButton fab;
    private String premiumUserId; //el userUID del usuario destacado NO EL ACTUAL

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_final);
        tab = findViewById(R.id.tab);
        Toolbar tb =  findViewById(R.id.toolbar);
        fab=findViewById(R.id.fab);
        tb.setTitle("Usuario Destacado");
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        if(ab !=null){
            ab.setDisplayHomeAsUpEnabled(true);
        }
        invalidateOptionsMenu();
        Intent inconmingIntent = getIntent();
        clientId = inconmingIntent.getStringExtra("clientId");
        if(inconmingIntent.hasExtra("isCurrent")){
            isCurrentUser=true;
        }

        setContentInfo();
    }
    private void setContentInfo(){
        FirebaseFirestore.getInstance().collection("premiumUsers")
                .whereEqualTo("clientId",clientId)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult()!=null) {
                        List<PremiumUser> results = new ArrayList<>(task.getResult().toObjects(PremiumUser.class));
                        if(!results.isEmpty()){
                            premiumUser = results.get(0);
                            premiumUserId= premiumUser.getUserId();
                            setFragments();
                        }
                    } else {
                        Log.d("Firestore task", "onComplete: " + task.getException());
                    }
                });

    }

    private void setFragments() {
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setTitle(premiumUser.getUserName());
        Bundle bundle = new Bundle();
        bundle.putString("name", premiumUser.getUserName());
        bundle.putString("photo", premiumUser.getProfilePhoto());
        bundle.putString("facebook", premiumUser.getLinkFacebook());
        bundle.putString("instagram", premiumUser.getLinkInstagram());
        bundle.putString("location",premiumUser.getLocalization());
        bundle.putString("email", premiumUser.getContactMail());
        bundle.putSerializable("registerDate", premiumUser.getPremiumDate());
        TabLayout tab = findViewById(R.id.tab);

        infoFragment = new PremiumUserInfoFragment(clientId,isCurrentUser);
        infoFragment.setArguments(bundle);
        viewPagerUserInfo = findViewById(R.id.storeInfoViewPager);
        setupViewPagerInfo(viewPagerUserInfo);

        publicationsFragment=new PremiumPublicationsFragment(premiumUserId);
        publicationsFragment.setArguments(bundle);

        publicClosetFragment=new PublicClosetFragment(premiumUserId);
        publicClosetFragment.setArguments(bundle);

        reputationFragment=new ReputationPremiumFragment(clientId);
        reputationFragment.setArguments(bundle);

        ViewPager viewPagerUserPublications = findViewById(R.id.storeViewPager);
        setupViewPager(viewPagerUserPublications);
        tab.setupWithViewPager(viewPagerUserPublications);
        //saveVisit

    }
    @Override
    protected void onStop() {
        super.onStop();
    }


    /**
     * Crea una instancia del view pager con los datos
     * predeterminados
     *
     * @param viewPager Nueva instancia
     */
    private void setupViewPager(ViewPager viewPager) {
        StoreTabAdapter adapter = new StoreTabAdapter(getSupportFragmentManager());
        Log.e("VIEW PAGER","CARGAAAAAAAAAA");
        adapter.addFragment(0,publicationsFragment,"Publicaciones");
        adapter.addFragment(1,publicClosetFragment,"Ropero");
        adapter.addFragment(2,reputationFragment,"Reputación");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
    }
    private void setupViewPagerInfo(ViewPager viewPager){
        SectionsPagerAdapter adapter=new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(infoFragment);
        viewPagerUserInfo.setAdapter(adapter);
    }
    @Override
    public boolean onSupportNavigateUp(){
        this.finish();
        return true;
    }
}
