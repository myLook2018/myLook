package com.mylook.mylook.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ActionProvider;
import androidx.core.view.MenuItemCompat;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Notification;
import com.mylook.mylook.entities.PremiumUser;
import com.mylook.mylook.entities.Subscription;
import com.mylook.mylook.login.LoginActivity;
import com.mylook.mylook.notifications.NotificationCenter;
import com.mylook.mylook.profile.AccountActivity;
import com.mylook.mylook.utils.CardsHomeFeedAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ru.nikartm.support.BadgePosition;
import ru.nikartm.support.ImageBadgeView;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private CardsHomeFeedAdapter adapter;
    private static List<Object> list;
    private ArrayList<Subscription> subscriptionList;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ImageView starImage;
    private TextView emptyArticles;
    private SwipeRefreshLayout refreshLayout;
    private int totalArticles = 0;
    private Context mContext;
    final static String TAG = "HomeFragment";
    private static HomeFragment homeInstance;
    private TextView textCartItemCount;
    private int notificationCount = 0;
    private ImageBadgeView notificationItem;
    private View notifications;
    private TextView txtViewCount;
    RecyclerView recyclerView;
    public static HomeFragment getInstance() {
        if (homeInstance == null) {
            homeInstance = new HomeFragment();
            if (list == null) {
                list = new ArrayList<>();
            }
        }
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
        recyclerView = view.findViewById(R.id.recycler_view_content);
        starImage = view.findViewById(R.id.empty_star);
        emptyArticles = view.findViewById(R.id.emptyText);
        emptyArticles.setVisibility(View.GONE);
        starImage.setVisibility(View.GONE);

        adapter = new CardsHomeFeedAdapter(mContext, list);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        refreshLayout = view.findViewById(R.id.refresh_layout_home);
        refreshLayout.setOnRefreshListener(this);
        loadFragment();
    }

    private void loadFragment() {
        readSubscriptions(false);
        updateInstallationToken();
    }

    private void countNotifications(){

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        notifications = menu.findItem(R.id.notifications_menu).getActionView();
        notifications.setOnClickListener(l -> {
            Intent intent = new Intent(getContext(), NotificationCenter.class);
            startActivity(intent);
        });
        txtViewCount = (TextView) notifications.findViewById(R.id.txtCount);
        txtViewCount.setOnClickListener(l -> {
            Intent intent = new Intent(getContext(), NotificationCenter.class);
            startActivity(intent);
        });
        FirebaseFirestore.getInstance().collection("notifications")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getUid())
                .whereEqualTo("openedNotification", false)
                .orderBy("creationDate", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(v -> {
            notificationCount = v.getDocuments().size();
            if (notificationCount > 0){
                txtViewCount.setVisibility(View.VISIBLE);
                Log.e("Notifications", ""+v.getDocuments().size());
                txtViewCount.setText(String.valueOf(notificationCount));
            } else {
                txtViewCount.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseFirestore.getInstance().collection("notifications")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getUid())
                .whereEqualTo("openedNotification", false)
                .orderBy("creationDate", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(v -> {
            notificationCount = v.getDocuments().size();
            if (notificationCount > 0){
                txtViewCount.setVisibility(View.VISIBLE);
                Log.e("Notifications", ""+v.getDocuments().size());
                txtViewCount.setText(String.valueOf(notificationCount));
            } else {
                txtViewCount.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings_menu) {
            Intent intent = new Intent(getContext(), AccountActivity.class);
            startActivityForResult(intent,1);
        } else if (id == R.id.notifications_menu){
            Intent intent = new Intent(getContext(), NotificationCenter.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==0){
                ((MyLookActivity) getContext()).setPremiumMenu();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
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
                    }
                });
            });
    }

    private void readPremiumSubscriptions(){
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
                                    list.add(premiumUser);
                                }
                                if(list.isEmpty()){
                                    emptyArticles.setVisibility(View.VISIBLE);
                                    starImage.setVisibility(View.VISIBLE);
                                } else {
                                    emptyArticles.setVisibility(View.GONE);
                                    starImage.setVisibility(View.GONE);
                                }
                                adapter.notifyDataSetChanged();
                                Log.e("On complete", "Tamaño adapter " + adapter.getItemCount());

                            } else {
                                Log.d("Firestore task", "onComplete: " + task1.getException());
                                if(list.isEmpty()){
                                    emptyArticles.setVisibility(View.VISIBLE);
                                    starImage.setVisibility(View.VISIBLE);
                                } else {
                                    emptyArticles.setVisibility(View.GONE);
                                    starImage.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                } else {
                    if(list.isEmpty()){
                        emptyArticles.setVisibility(View.VISIBLE);
                        starImage.setVisibility(View.VISIBLE);
                    } else {
                        emptyArticles.setVisibility(View.GONE);
                        starImage.setVisibility(View.GONE);
                    }
                }
            }
            refreshLayout.setRefreshing(false);
        });
    }

    public void readSubscriptions(boolean isRefresh) {
        final Calendar myCalendar = Calendar.getInstance();
        myCalendar.set(Calendar.DATE, myCalendar.get(Calendar.DATE) - 15);
        if(isRefresh){
            list.clear();
        }
        if (list.size() == 0 ) {
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
                                        if (task12.isSuccessful() && task.getResult() != null) {// Si se usa esta lista están ordenados por fecha
                                            /*for (QueryDocumentSnapshot documentSnapshot : task12.getResult()) {
                                                Article art = documentSnapshot.toObject(Article.class);
                                                art.setArticleId(documentSnapshot.getId());
                                                list.add(art);
                                            }*/

                                            if (createArticleList(task12.getResult())) {

                                                adapter.notifyDataSetChanged();
                                            }
                                            if(list.isEmpty()){
                                                emptyArticles.setVisibility(View.VISIBLE);
                                                starImage.setVisibility(View.VISIBLE);
                                            } else {
                                                emptyArticles.setVisibility(View.GONE);
                                                starImage.setVisibility(View.GONE);
                                            }
                                        } else {
                                            Log.e("Firestore task", "onComplete: " + task12.getException());
                                            if(list.isEmpty()){
                                                emptyArticles.setVisibility(View.VISIBLE);
                                                starImage.setVisibility(View.VISIBLE);
                                            } else {
                                                emptyArticles.setVisibility(View.GONE);
                                                starImage.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }
                                readPremiumSubscriptions();
                            } else {
                                readPremiumSubscriptions();
                            }
                        }else {
                            readPremiumSubscriptions();
                        }
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
        try{
            recyclerView.getRecycledViewPool().clear();
            adapter.notifyDataSetChanged();
            readSubscriptions(true);
        }catch (Exception e)
        {
            list.clear();
            Log.e(TAG, "Problema refrescando");
        }
    }

    public void clear() {
        list=null;
        homeInstance=null;
    }
}