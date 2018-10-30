package com.mylook.mylook.closet;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;

import java.util.ArrayList;

public class OutfitActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore dB;
    private ArrayList<Article> favoritos;
    private FirebaseUser user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_outfit);
        recyclerView = findViewById(R.id.recycleItems);
        user = FirebaseAuth.getInstance().getCurrentUser();
        loadRecycleViewer();
        favoritos = getIntent().getExtras().getParcelable("favoritos");


    }

    private void loadRecycleViewer(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        OutfitRecycleViewAdapter adapter = new OutfitRecycleViewAdapter(getApplicationContext(),favoritos);
        recyclerView.setAdapter(adapter);
    }
}
