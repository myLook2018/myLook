package com.mylook.mylook.premiumUser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.PremiumUser;
import com.mylook.mylook.session.MainActivity;
import com.mylook.mylook.session.Session;
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
    private PremiumDiffusionFragment publicDifusionFragment;
    private boolean isCurrentUser = false;
    private boolean fromDeepLink;
    private String premiumUserId; //el userUID del usuario destacado NO EL ACTUAL

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_final);
        tab = findViewById(R.id.tab);
        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("");
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        invalidateOptionsMenu();

        if (getIncomingIntent()) {
            setContentInfo();
        }
    }

    private void setContentInfo() {
        FirebaseFirestore.getInstance().collection("premiumUsers")
                .whereEqualTo("clientId", clientId)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<PremiumUser> results = new ArrayList<>(task.getResult().toObjects(PremiumUser.class));
                if (!results.isEmpty()) {
                    premiumUser = results.get(0);
                    premiumUserId = premiumUser.getUserId();
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
        bundle.putString("location", premiumUser.getLocalization());
        bundle.putString("email", premiumUser.getContactMail());
        bundle.putSerializable("registerDate", premiumUser.getPremiumDate());
        TabLayout tab = findViewById(R.id.tab);

        infoFragment = new PremiumUserInfoFragment(clientId, isCurrentUser);
        infoFragment.setArguments(bundle);
        viewPagerUserInfo = findViewById(R.id.storeInfoViewPager);
        setupViewPagerInfo(viewPagerUserInfo);

        publicationsFragment = new PremiumPublicationsFragment(premiumUserId);
        publicationsFragment.setArguments(bundle);

        publicDifusionFragment = new PremiumDiffusionFragment(premiumUserId);
        publicDifusionFragment.setArguments(bundle);

        reputationFragment = new ReputationPremiumFragment(FirebaseAuth.getInstance().getUid());
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
        Log.e("VIEW PAGER", "CARGAAAAAAAAAA");
        adapter.addFragment(0, publicationsFragment, "Posts");
        adapter.addFragment(1, publicDifusionFragment, "Difusiones");
        adapter.addFragment(2, reputationFragment, "Reputación");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
    }

    private void setupViewPagerInfo(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(infoFragment);
        viewPagerUserInfo.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_menu, menu);
        MenuItem item = menu.findItem(R.id.share_menu);
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("Title", "Item selected");
        int id = item.getItemId();
        if (id == R.id.share_menu) {
            Log.e("Share", "Share User");
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Mirá este Usuario Destacado! https://www.mylook.com/user?clientIdDL=" + Uri.encode(premiumUser.getClientId()));
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

    private boolean getIncomingIntent() {

        Intent inconmingIntent = getIntent();
        if (inconmingIntent.hasExtra("clientId")) {
            fromDeepLink = false;
            clientId = inconmingIntent.getStringExtra("clientId");
            isCurrentUser = Session.clientId.equals(clientId);
        } else {
            fromDeepLink = true;
            try {
                Log.e("Deeploink", getIntent().getData().toString());
                if (inconmingIntent.getData().getQueryParameter("clientIdDL") != null) {
                    clientId =Uri.decode(inconmingIntent.getData().getQueryParameter("clientIdDL"));
                    isCurrentUser = Session.clientId.equals(clientId);
                    return true;
                }
            } catch (Exception e) {

                Log.e("clientIdDL", getIntent().getData().toString());
                clientId = Uri.decode(inconmingIntent.getStringExtra("clientIdDL").replace("%20", " "));
                isCurrentUser = Session.clientId.equals(clientId);

            }
            return false;
        }
        return true;
    }
}


