package com.mylook.mylook.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Subscription;
import com.mylook.mylook.login.LoginActivity;
import com.mylook.mylook.utils.BottomNavigationViewHelper;
import com.mylook.mylook.utils.CardsHomeFeedAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 0;

    private Context mContext = HomeActivity.this;

    private RecyclerView recyclerView;
    private CardsHomeFeedAdapter adapter;
    private List<Article> articleList;
    private ArrayList<Subscription> subscriptionList;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_home);
        setupFirebaseAuth();
        setupBottomNavigationView();
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        recyclerView = findViewById(R.id.recycler_view_content);
        articleList = new ArrayList<>();
        adapter = new CardsHomeFeedAdapter(this, articleList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        //read firestore
        if (mAuth.getCurrentUser() != null) {
            readSubscriptions();
            updateInstallationToken();
        }
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
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                checkCurrentUser(firebaseAuth.getCurrentUser());
            }
        };
    }

    private void updateInstallationToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                final String mToken = instanceIdResult.getToken();
                db.collection("clients").whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Map<String, Object> update = new HashMap<>();
                        update.put("installToken",mToken);
                        db.collection("clients").document(task.getResult().getDocuments().get(0).getId()).set(update, SetOptions.merge());
                    }
                });
            }
        });
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
                                        for (QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                            Log.e("ROPERO", documentSnapshot.getId());
                                            Article art=documentSnapshot.toObject(Article.class);
                                            art.setArticleId(documentSnapshot.getId());
                                            articleList.add(art);
                                        }
                                        //articleList.addAll(task.getResult().toObjects(Article.class));
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
        BottomNavigationView bottomNavigationView =  findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
