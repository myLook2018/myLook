package com.mylook.mylook.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.RequestRecommendation;
import com.mylook.mylook.entities.Subscription;
import com.mylook.mylook.login.LoginActivity;
import com.mylook.mylook.recommend.RecommendationsActivity;
import com.mylook.mylook.recommend.RequestRecyclerViewAdapter;
import com.mylook.mylook.utils.BottomNavigationViewHelper;
import com.mylook.mylook.utils.CardsHomeFeedAdapter;
import com.mylook.mylook.utils.SectionsPagerAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.mylook.mylook.utils.Count.setCounting;

public class HomeActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 0;

    private Context mContext = HomeActivity.this;

    private RecyclerView recyclerView;
    private CardsHomeFeedAdapter adapter;
    private List<Article> articleList;
    private ArrayList<Subscription> subscriptionList;
    private FirebaseFirestore dB;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MenuItem itemRecommend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupFirebaseAuth();

        setupBottomNavigationView();
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_content);

        articleList = new ArrayList<>();
        adapter = new CardsHomeFeedAdapter(this, articleList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //read firestore
        readSubscriptions();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

     private void checkCurrentUser(FirebaseUser user) {
        if (user == null) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                checkCurrentUser(firebaseAuth.getCurrentUser());
            }
        };
    }

    private void readSubscriptions() {
        db.collection("subscriptions")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        subscriptionList = new ArrayList<Subscription>();
                        subscriptionList.addAll(task.getResult().toObjects(Subscription.class));

                        for (Subscription sub : subscriptionList) {
                            db.collection("articles")
                                    .whereEqualTo("storeName", sub.getStoreName())
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        articleList.addAll(task.getResult().toObjects(Article.class));
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Log.d("Firestore task", "onComplete: " + task.getException());
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        int myRequests = 12;
        Menu menu = bottomNavigationView.getMenu();
        //menu.findItem(R.id.ic_recommend).getActionView();
        //getRequestRecomendations();
//        if (myRequests > 0) {
//            setRequestNotification(bottomNavigationView,myRequests);
//        }
        //bottomNavigationView.inflateMenu(R.id.);

        //getMenuInflater().inflate(R.menu.ic_recommend_badge, menu);
        //bottomNavigationView.inflateMenu(R.menu.ic_recommend_badge);
        itemRecommend = menu.findItem(R.id.ic_recommend);
        LayerDrawable icon = (LayerDrawable) itemRecommend.getIcon();
        String a = "12";
        setCounting(getBaseContext(), icon, a);
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    private int getRequestRecomendations() {
        int myRequests = 0;
        final ArrayList<RequestRecommendation> requestArray = new ArrayList<>();

        dB.collection("requestRecommendations")
                .whereEqualTo("userId", user.getUid())
                .whereEqualTo("seen", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                RequestRecommendation requestRecommendation = document.toObject(RequestRecommendation.class);
                                requestArray.add(requestRecommendation);
                            }
                        }
                    }
                });
        myRequests = requestArray.size();
        return myRequests;
    }
}
