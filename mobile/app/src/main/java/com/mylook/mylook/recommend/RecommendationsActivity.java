package com.mylook.mylook.recommend;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.RequestRecommendation;
import com.mylook.mylook.utils.BottomNavigationViewHelper;

import java.util.ArrayList;
import java.util.List;

public class RecommendationsActivity extends AppCompatActivity implements RecommendFragment.OnFragmentInteractionListener {

    private static final int ACTIVITY_NUM = 2;
    private FloatingActionButton fab;
    private Context mContext = RecommendationsActivity.this;
    private RecyclerView recyclerView;
    private FirebaseFirestore dB;
    private List<RequestRecommendation> requestRecommendationsList;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);
        user = FirebaseAuth.getInstance().getCurrentUser();

        this.dB = FirebaseFirestore.getInstance();
        setupBottomNavigationView();
        initRecyclerView();
        fab= findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), RecommendActivityAddDesc.class);
                startActivity(intent);
            }
        });
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle("Recomendaciones solicitadas");
        setSupportActionBar(tb);
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    private void initRecyclerView(){
        recyclerView = findViewById(R.id.recyclerViewRecommend);
        getRequestRecommendations();

    }
    public void  getRequestRecommendations(){
        dB.collection("requestRecommendations")
                .whereEqualTo("user",user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            requestRecommendationsList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                RequestRecommendation requestRecommendation = document.toObject(RequestRecommendation.class);
                                requestRecommendationsList.add(requestRecommendation);
                            }
                            RecyclerViewAdapter adapter = new RecyclerViewAdapter(RecommendationsActivity.this,requestRecommendationsList);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        }
                    }
                });
    }
}
