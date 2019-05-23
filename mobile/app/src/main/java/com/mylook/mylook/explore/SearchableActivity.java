package com.mylook.mylook.explore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.PremiumUser;
import com.mylook.mylook.entities.Store;
import com.mylook.mylook.utils.CardsHomeFeedAdapter;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;


public class SearchableActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List results;
    private CardsHomeFeedAdapter adapter;
    private RecyclerView recyclerView;
    private ImageView backArrow;
    private ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Tu busqueda");
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recycler_view_content);

        progressBar = findViewById(R.id.home_progress_bar);

        results = new ArrayList();
        adapter = new CardsHomeFeedAdapter(this, results);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (intent.hasExtra("query")) {
            String query = stripAccents(intent.getStringExtra("query"));
            System.out.println("Query: " + query);
            recyclerView.removeAllViewsInLayout();
            recyclerView.removeAllViews();
            results.clear(); //new
            doMySearchTags(query);
            doMySearchTitles(query);
            doMySearchStoreNames(query);
            doMySearchStorePremium(query);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void doMySearchStorePremium(final String query) {
        db.collection("premiumUsers")
                //.whereEqualTo("storeName",query)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if (stripAccents(documentSnapshot.get("userName").toString().toLowerCase()).contains(query.toLowerCase())) {
                                    PremiumUser premiumUser = documentSnapshot.toObject(PremiumUser.class);
                                    results.add(premiumUser);
                                    Log.e("Usuario", premiumUser.getUserName());
                                }
                            }
                            //articleList.addAll(task.getResult().toObjects(Article.class));
                            Log.e("Usuario", "successful");
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.d("Firestore task", "onComplete: " + task.getException());
                        }
                    }
                });
    }


    private void doMySearchStoreNames(final String query) {
        //query = Character.toUpperCase(query.charAt(0)) + query.substring(1, query.length());
        Log.d("String QUERY", "doMySearchStoreNames: " + query);
        db.collection("stores")
                //.whereEqualTo("storeName",query)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Log.d("Tienda BD", "onComplete: " + documentSnapshot.get("storeName"));
                                if (stripAccents(documentSnapshot.get("storeName").toString().toLowerCase()).contains(query.toLowerCase())) {
                                    Store store = documentSnapshot.toObject(Store.class);
                                    results.add(store);
                                    Log.e("Tienda", store.getStoreName());
                                    Log.d("ResultQuery", "onComplete: " + stripAccents(documentSnapshot.get("storeName").toString().toLowerCase()));
                                }
                            }
                            //articleList.addAll(task.getResult().toObjects(Article.class));
                            Log.e("Tienda", "successful");
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.d("Firestore task", "onComplete: " + task.getException());
                        }
                    }
                });
    }

    private void doMySearchTitles(final String query) {
        //query = Character.toUpperCase(query.charAt(0)) + query.substring(1, query.length());
        db.collection("articles")
                //.whereGreaterThanOrEqualTo("title", query)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if (stripAccents(documentSnapshot.get("title").toString().toLowerCase()).contains(query.toLowerCase())) {
                                    Article art = documentSnapshot.toObject(Article.class);
                                    art.setArticleId(documentSnapshot.getId());
                                    if (!results.contains(art))
                                        results.add(art);
                                }
                            }
                            //articleList.addAll(task.getResult().toObjects(Article.class));
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("Firestore task", "onComplete: " + task.getException());
                        }
                    }
                });

    }

    private void doMySearchTags(final String query) {
        //query = Character.toUpperCase(query.charAt(0)) + query.substring(1, query.length());
        db.collection("articles")
                //.whereArrayContains("tags", query)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Article art = documentSnapshot.toObject(Article.class);
                                for (String tag : art.getTags()) {
                                    if (stripAccents(tag.toLowerCase()).contains(query.toLowerCase())) {
                                        art.setArticleId(documentSnapshot.getId());
                                        if (!results.contains(art))
                                            results.add(art);
                                    }
                                }

                            }
                            //articleList.addAll(task.getResult().toObjects(Article.class));
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("Firestore task", "onComplete: " + task.getException());
                        }
                    }
                });
    }

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }
}
