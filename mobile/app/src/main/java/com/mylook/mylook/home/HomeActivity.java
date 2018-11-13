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
import com.mylook.mylook.entities.PremiumUser;
import com.mylook.mylook.entities.Subscription;
import com.mylook.mylook.login.LoginActivity;
import com.mylook.mylook.room.LocalInteraction;
import com.mylook.mylook.utils.BottomNavigationViewHelper;
import com.mylook.mylook.utils.CardsHomeFeedAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class HomeActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 0;

    private Context mContext = HomeActivity.this;

    private RecyclerView recyclerView;
    private CardsHomeFeedAdapter adapter;
    private List list;
    private ArrayList<Subscription> subscriptionList;

    private List<LocalInteraction> mLocalInteractions;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private int currentIndex = 0;
    private int totalArticles = 0;

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
        list = new ArrayList<>();
        adapter = new CardsHomeFeedAdapter(this, list);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


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
                                       // for (QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                       //     Log.e("ROPERO", documentSnapshot.getId());
                                        //     Article art=documentSnapshot.toObject(Article.class);
                                        //    art.setArticleId(documentSnapshot.getId());
                                       //     list.add(art);
                                        //}
                                       if(createArticleList(task.getResult())){
                                           adapter.notifyDataSetChanged();
                                       }
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
        db.collection("premiumUsersSubscriptions")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        subscriptionList = new ArrayList<Subscription>();
                        subscriptionList.addAll(task.getResult().toObjects(Subscription.class));

                        for (Subscription sub : subscriptionList) {
                            db.collection("premiumUsers")
                                    .whereEqualTo("clientId", sub.getStoreName())
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                            Log.e("ROPERO", documentSnapshot.getId());
                                            PremiumUser premiumUser=documentSnapshot.toObject(PremiumUser.class);
                                            list.add(premiumUser);
                                        }
                                        adapter.notifyDataSetChanged();
                                        Log.e("On complete", "Tamaño adapter "+adapter.getItemCount());

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

    private boolean createArticleList(QuerySnapshot result) {
        List<Article> promo1 = new ArrayList<>();
        List<Article> promo2 = new ArrayList<>();
        List<Article> promo3 = new ArrayList<>();

        for (QueryDocumentSnapshot document : result) {
                Article art = document.toObject(Article.class);
                art.setArticleId(document.getId());
                Log.e("Promotion Level", "N° "+art.getPromotionLevel());
                int pl = art.getPromotionLevel();
                switch (pl) {
                    case 1:
                        Log.d("ADDING", "PROMO1");
                        promo1.add(art);
                        break;
                    case 2:
                        Log.d("ADDING", "PROMO2");
                        promo2.add(art);
                        break;
                    case 3:
                        Log.d("ADDING", "PROMO3");
                        promo3.add(art);
                        break;
                }
                totalArticles++;

        }
        if (totalArticles == 0) {
            return false;
        }
        if (!promo3.isEmpty()) {
            Collections.shuffle(promo3);
        }
        if (!promo2.isEmpty()) {
            Collections.shuffle(promo2);
        }
        if (!promo1.isEmpty()) {
            Collections.shuffle(promo1);
        }
        Random r = new Random();
        int v;
        while (true) {
            if (!promo3.isEmpty()) {
                if (!promo2.isEmpty()) {
                    if (!promo1.isEmpty()) {
                        Log.e("Shuffle", "Los tres != vacio");
                        v = r.nextInt(100);
                        if (v > 54) {
                            list.add(promo3.remove(0));
                        } else if (v > 21) {
                            list.add(promo2.remove(0));
                        } else {
                            list.add(promo1.remove(0));
                        }
                    } else {
                        Log.e("Shuffle", "3 y 2 != vacio");
                        v = r.nextInt(100);
                        if (v > 35) {
                            list.add(promo3.remove(0));
                        } else {
                            list.add(promo2.remove(0));
                        }
                    }
                } else {
                    if (!promo1.isEmpty()) {
                        Log.e("Shuffle", "3 y 1 != vacio");
                        v = r.nextInt(100);
                        if (v > 32) {
                            list.add(promo3.remove(0));
                        } else {
                            list.add(promo1.remove(0));
                        }
                    } else {
                        Log.e("Shuffle", "Tres != vacio");
                        list.add(promo3.remove(0));
                    }
                }
            } else {
                if (!promo2.isEmpty()) {
                    if (!promo1.isEmpty()) {
                        Log.e("Shuffle", "2 y 1 != vacio");
                        v = r.nextInt(100);
                        if (v > 39) {
                            list.add(promo2.remove(0));
                        } else {
                            list.add(promo1.remove(0));
                        }
                    } else {
                        Log.e("Shuffle", "2 != vacio");
                        list.add(promo2.remove(0));
                    }
                } else {
                    if (!promo1.isEmpty()) {
                        Log.e("Shuffle", "1 != vacio");
                        list.add(promo1.remove(0));
                    } else {
                        Log.e("Shuffle", "Los tres vacios");
                        break;
                    }
                }
            }
        }
        Log.e("HOme", "Retorno true el getArticleLIst");
        return true;
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
