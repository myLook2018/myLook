package com.mylook.mylook.closet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Closet;
import com.mylook.mylook.entities.Favorite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewOutfitActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private HashMap<String, String> outfitItems;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ConstraintLayout container;
    private ImageView bottomCloth, mediumCloth, topCloth, topAccesory, bottomAccesory, activeView;
    private Toolbar tb;
    private boolean isFromOutfit;
    private String collectionName, category;
    private ImageButton btnSend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_outfit);
        recyclerView = findViewById(R.id.recycleItems);
        container = findViewById(R.id.outfitLayout);
        topCloth = findViewById(R.id.topCloth);
        bottomCloth = findViewById(R.id.bottomCloth);
        mediumCloth = findViewById(R.id.mediumCloth);
        bottomAccesory = findViewById(R.id.bottomAccesory);
        topAccesory = findViewById(R.id.topAccesory);
        user = FirebaseAuth.getInstance().getCurrentUser();
        btnSend = findViewById(R.id.btnSendOutfit);
        initElements();
        hideCreationItems();
        loadOutfit();
    }

    private void loadOutfit() {
        final HashMap<String,String> pictures = new HashMap<>();
        for (final String item : outfitItems.keySet()) {
            String articleId = outfitItems.get(item);
            loadImage(item, outfitItems.get(item), articleId);

        }

    }

    private void loadImage(final String item,final String picture, String articleId){
        dB.collection("articles").document(articleId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            View v = null;
                            if(item.getClass().equals( Integer.class))
                                v = findViewById(Integer.parseInt(item));
                            if(v == null) {
                                v = findViewById(getResources().getIdentifier(item, "id", getApplicationContext().getPackageName()));
                            }
                            String art = (String)task.getResult().get("picture");
                            Glide.with(ViewOutfitActivity.this).asBitmap().load(art)
                                    .into((ImageView) v);

                        }
                    }
                });
    }

    private void hideCreationItems() {
        btnSend.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    private void initElements() {
        collectionName = getIntent().getExtras().get("name").toString();
        category = getIntent().getExtras().get("category").toString();
        outfitItems = (HashMap<String, String>) getIntent().getExtras().get("items");
        tb = findViewById(R.id.toolbar);
        tb.setTitle(collectionName);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
