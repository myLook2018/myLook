package com.mylook.mylook.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.PremiumUser;
import com.mylook.mylook.entities.Subscription;
import com.mylook.mylook.login.LoginActivity;
import com.mylook.mylook.profile.AccountActivity;
import com.mylook.mylook.session.Session;
import com.mylook.mylook.session.Sesion;
import com.mylook.mylook.utils.CardsHomeFeedAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private CardsHomeFeedAdapter adapter;
    private static ArrayList<Article> list;
    private ArrayList<Subscription> subscriptionList;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressBar mProgressBar;
    private ImageView starImage;
    private TextView emptyArticles;
    private SwipeRefreshLayout refreshLayout;
    private int totalArticles = 0;
    private Context mContext;
    final static String TAG = "HomeFragment";
    private Sesion currentSesion = Sesion.getInstance();
    private static HomeFragment homeInstance;

    public static HomeFragment getInstance() {
        if (homeInstance == null) homeInstance = new HomeFragment();
        return homeInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_home, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        mContext = getContext();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_content);
        mProgressBar = view.findViewById(R.id.home_progress_bar);
        starImage = view.findViewById(R.id.empty_star);
        emptyArticles = view.findViewById(R.id.emptyText);

        if (list == null) {
            list = new ArrayList<>();
        }
        adapter = new CardsHomeFeedAdapter(mContext, list);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        refreshLayout = view.findViewById(R.id.refresh_layout_home);
        refreshLayout.setOnRefreshListener(this);

        setupFirebaseAuth();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            loadFragment();
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void loadFragment() {
        if (Session.getInstance().doesHomeUpdate()) {
            readSubscriptions();
        }
        updateInstallationToken();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings_menu) {
            Intent intent = new Intent(getContext(), AccountActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    private void checkCurrentUser(FirebaseUser user) {
        if (user == null) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            // TODO sacar esto
            try {
                finalize();
            } catch (Throwable e) {

            }
        }
    }

    private void setupFirebaseAuth() {
        mAuthListener = firebaseAuth -> checkCurrentUser(firebaseAuth.getCurrentUser());
    }

    private void updateInstallationToken() {
        FragmentActivity act = getActivity();
        if (act != null)
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(getActivity(), instanceIdResult -> {
                final String mToken = instanceIdResult.getToken();
                FirebaseFirestore.getInstance().collection("clients").whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        Map<String, Object> update = new HashMap<>();
                        update.put("installToken", mToken);
                        FirebaseFirestore.getInstance().collection("clients").document(task.getResult().getDocuments().get(0).getId()).set(update, SetOptions.merge());
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            });
    }

    private void readSubscriptions() {
        //Devuelve los ultimos meses, TODO Cambiar esto para probar en serio
        final Calendar myCalendar = Calendar.getInstance();
        myCalendar.set(Calendar.MONTH, myCalendar.get(Calendar.MONTH) - 7);
        Log.e(TAG, list.toString());
        Log.e(TAG, "Begin read Subscriptions- Uid:" + FirebaseAuth.getInstance().getCurrentUser());
        if (list.size() == 0) {
            FirebaseFirestore.getInstance().collection("subscriptions")
                    .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (!task.getResult().isEmpty()) {
                        subscriptionList = new ArrayList<>();
                        subscriptionList.addAll(task.getResult().toObjects(Subscription.class));
                        for (Subscription sub : subscriptionList) {
                            FirebaseFirestore.getInstance().collection("articles")
                                    .whereEqualTo("storeName", sub.getStoreName())
                                    .orderBy("creationDate", Query.Direction.DESCENDING)
                                    .whereGreaterThan("creationDate", myCalendar.getTime())
                                    .get().addOnCompleteListener(task12 -> {
                                if (task12.isSuccessful() && task.getResult() != null) {
                                    for (QueryDocumentSnapshot documentSnapshot : task12.getResult()) {
                                        Article art = documentSnapshot.toObject(Article.class);
                                        art.setArticleId(documentSnapshot.getId());
                                        //list.add(art); // Si se usa esta lista están ordenados por fecha
                                    }

                                    if (createArticleList(task12.getResult())) { //Con esta por la probabilidad de las promos, pero no por fecha
                                        for (Article art : list) {
                                            Log.e(TAG, (art.getArticleId() + " - " + art.getCreationDate() + " - Promo: " + art.getPromotionLevel()));
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
                                    Log.e("Firestore task", "onComplete: " + task12.getException());
                                }
                            });
                        }
                    } else {
                        emptyArticles.setVisibility(View.VISIBLE);
                        starImage.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            });

            FirebaseFirestore.getInstance().collection("premiumUsersSubscriptions")
                    .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        subscriptionList = new ArrayList<>();
                        subscriptionList.addAll(task.getResult().toObjects(Subscription.class));

                        for (Subscription sub : subscriptionList) {
                            FirebaseFirestore.getInstance().collection("premiumUsers")
                                    .whereEqualTo("clientId", sub.getStoreName())
                                    .get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                        Log.e("ROPERO", documentSnapshot.getId());
                                        // TODO ver esto
                                        PremiumUser premiumUser = documentSnapshot.toObject(PremiumUser.class);
                                        //list.add(premiumUser);
                                    }
                                    adapter.notifyDataSetChanged();
                                    Log.e("On complete", "Tamaño adapter " + adapter.getItemCount());

                                } else {
                                    Log.d("Firestore task", "onComplete: " + task1.getException());
                                }
                            });
                        }
                    }
                }
                refreshLayout.setRefreshing(false);
            });
        }
    }

    private void orderByDateAndPromo(List<Article> promo1, List<Article> promo2, List<Article> promo3) {
        if (!promo3.isEmpty()) {
            Collections.sort(promo3, (o1, o2) -> o2.getCreationDate().compareTo(o1.getCreationDate()));
            list.addAll(promo3);
        }
        if (!promo2.isEmpty()) {
            Collections.sort(promo2, (o1, o2) -> o2.getCreationDate().compareTo(o1.getCreationDate()));
            list.addAll(promo2);
        }
        if (!promo1.isEmpty()) {
            Collections.sort(promo1, (o1, o2) -> o2.getCreationDate().compareTo(o1.getCreationDate()));
            list.addAll(promo1);
        }
    }

    // TODO para que seria esto? lo de explorar pero en el home?
    private void orderByPromoProb(List<Article> promo1, List<Article> promo2, List<Article> promo3) {
        Random r = new Random();
        int v;
        while (true) {
            if (!promo3.isEmpty()) {
                if (!promo2.isEmpty()) {
                    if (!promo1.isEmpty()) {
                        Log.e("Shuffle", "Los tres != vacio");
                        v = r.nextInt(100);
                        if (v > 35) {
                            list.add(promo3.remove(0));
                        } else if (v > 15) {
                            list.add(promo2.remove(0));
                        } else {
                            list.add(promo1.remove(0));
                        }
                    } else {
                        Log.e("Shuffle", "3 y 2 != vacio");
                        v = r.nextInt(100);
                        if (v > 20) {
                            list.add(promo3.remove(0));
                        } else {
                            list.add(promo2.remove(0));
                        }
                    }
                } else {
                    if (!promo1.isEmpty()) {
                        Log.e("Shuffle", "3 y 1 != vacio");
                        v = r.nextInt(100);
                        if (v > 20) {
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
    }

    private boolean createArticleList(QuerySnapshot result) {
        List<Article> promo1 = new ArrayList<>();
        List<Article> promo2 = new ArrayList<>();
        List<Article> promo3 = new ArrayList<>();

        for (QueryDocumentSnapshot document : result) {
            Article art = document.toObject(Article.class);
            art.setArticleId(document.getId());
            int pl = art.getPromotionLevel();
            switch (pl) {
                case 1:
                    promo1.add(art);
                    break;
                case 2:
                    promo2.add(art);
                    break;
                case 3:
                    promo3.add(art);
                    break;
            }
            totalArticles++;

        }
        if (totalArticles == 0) {
            return false;
        }
        orderByDateAndPromo(promo1, promo2, promo3);
        //orderByPromoProb(promo1, promo2, promo3);
        return true;
    }

    @Override
    public void onRefresh() {
        readSubscriptions();
    }
}
