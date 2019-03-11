package com.mylook.mylook.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
import com.mylook.mylook.utils.CardsHomeFeedAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private CardsHomeFeedAdapter adapter;
    private List list;
    private ArrayList<Subscription> subscriptionList;

    private List<LocalInteraction> mLocalInteractions;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressBar mProgressBar;
    private ImageView starImage;
    private TextView emptyArticles;
    private int currentIndex = 0;
    private int totalArticles = 0;
    private Context mContext;
    final static String TAG = "Home Fragment";

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_home, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getContext();
        recyclerView = view.findViewById(R.id.recycler_view_content);
        mProgressBar = view.findViewById(R.id.home_progress_bar);
        starImage = view.findViewById(R.id.empty_star);
        emptyArticles = view.findViewById(R.id.emptyText);
        mProgressBar.setVisibility(View.VISIBLE);
        list = new ArrayList<>();
        adapter = new CardsHomeFeedAdapter(mContext, list);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        mContext = getContext();
        setupFirebaseAuth();
        mAuth = FirebaseAuth.getInstance();

        if (user != null) {
            Log.e(TAG, "User" + user.getDisplayName());
            readSubscriptions();
            updateInstallationToken();

        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //readSubscriptions();
    }

    private void checkCurrentUser(FirebaseUser user) {
        if (user == null) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            try {
                finalize();
            } catch (Throwable e) {

            }
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
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this.getActivity(), new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                final String mToken = instanceIdResult.getToken();
                db.collection("clients").whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Map<String, Object> update = new HashMap<>();
                        update.put("installToken", mToken);
                        if (task.getResult().getDocuments().size() > 0) {
                            db.collection("clients").document(task.getResult().getDocuments().get(0).getId()).set(update, SetOptions.merge());
                            mProgressBar.setVisibility(View.GONE);
                        } else {

                        }
                    }
                });
            }
        });
    }

    private void readSubscriptions() {
        Log.e(TAG, "Begin Read suscriptions");
        db.collection("subscriptions")
                .whereEqualTo("userId", user.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        subscriptionList = new ArrayList<Subscription>();
                        subscriptionList.addAll(task.getResult().toObjects(Subscription.class));
                        Log.e(TAG, "getSubscriptions");
                        for (Subscription sub : subscriptionList) {
                            db.collection("articles")
                                    .whereEqualTo("storeName", sub.getStoreName())
                                    .orderBy("creationDate", Query.Direction.DESCENDING)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Log.e(TAG,"Documents Size: "+ task.getResult().getDocuments().size());
                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                            Article art = documentSnapshot.toObject(Article.class);
                                            art.setArticleId(documentSnapshot.getId());
                                            //list.add(art); // Si se usa esta lista están ordenados por fecha
                                        }
                                        if (createArticleList(task.getResult())) { //Con esta por la probabilidad de las promos, pero no por fecha
                                            for (Object art: list) {
                                                Log.e(TAG, ((Article) art).getArticleId() + " - "+((Article) art).getCreationDate() +" - Promo: "+((Article) art).getPromotionLevel());
                                            }
                                            emptyArticles.setVisibility(View.GONE);
                                            starImage.setVisibility(View.GONE);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            emptyArticles.setVisibility(View.VISIBLE);
                                            starImage.setVisibility(View.VISIBLE);
                                        }
                                        mProgressBar.setVisibility(View.GONE);
                                    } else {
                                        Log.e("Firestore task", "onComplete: " + task.getException());
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
                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                            Log.e("ROPERO", documentSnapshot.getId());
                                            PremiumUser premiumUser = documentSnapshot.toObject(PremiumUser.class);
                                            list.add(premiumUser);
                                        }
                                        adapter.notifyDataSetChanged();
                                        Log.e("On complete", "Tamaño adapter " + adapter.getItemCount());

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
            Log.e(TAG, "N° " + art.getPromotionLevel());
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

}
