package com.mylook.mylook.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Store;
import com.mylook.mylook.entities.Subscription;
import com.mylook.mylook.utils.GridImageAdapter;

import java.util.ArrayList;

public class StoreActivity extends AppCompatActivity {

    private Context mContext = StoreActivity.this;
    private ImageView backArrow, storePhoto;
    private Button btnSubscribe;
    private TextView storeName, storeLocation, storeShedule, storePhone;
    private GridView gridArticlesStore;
    private String nombreTiendaPerfil;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int NUM_COLUMNS = 3;
    private ArrayList<Store> storeList;
    private boolean mSubscribed;
    private String documentId="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_final);

        backArrow = (ImageView) findViewById(R.id.backArrow);
        storePhoto = (ImageView) findViewById(R.id.store_profile_photo);
        btnSubscribe = (Button) findViewById(R.id.btn_subscribe);
        storeName = (TextView) findViewById(R.id.profile_store_name);
        storeLocation = (TextView) findViewById(R.id.store_location);
        storeShedule = (TextView) findViewById(R.id.store_schedule);
        storePhone = (TextView) findViewById(R.id.store_phone);
        gridArticlesStore = (GridView) findViewById(R.id.profile_store_grid_view);

        storeList = new ArrayList<Store>();

        //retrieve store name from intent
        Intent intentStore = getIntent();
        nombreTiendaPerfil = intentStore.getStringExtra("Tienda");
        storeName.setText(nombreTiendaPerfil);

        db.collection("stores").whereEqualTo("storeName", nombreTiendaPerfil)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("info de firebase", "onComplete: " + task.getResult().toObjects(Store.class));

                    storeList.addAll(task.getResult().toObjects(Store.class));

                    Store storeAux = storeList.get(0);

                    storeLocation.setText(storeAux.getStoreAddress() + " " +
                            storeAux.getStoreAddressNumber() + " " + storeAux.getStoreFloor());

                    storePhone.setText(storeAux.getStorePhone());

                    Glide.with(mContext).load(storeAux.getProfilePh()).into(storePhoto);

                } else {
                    Log.d("Firestore task", "onComplete: " + task.getException());
                }
            }
        });

        //retrieve data for grid view
        setupGridView();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //following system
        checkFollow();

        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSubscribe.setEnabled(false);
                if (!mSubscribed) {

                    Subscription newSubscription = new Subscription(nombreTiendaPerfil, FirebaseAuth.getInstance().getCurrentUser().getUid());

                    db.collection("subscriptions").add(newSubscription).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Firestore task", "DocumentSnapshot written with ID: " + documentReference.getId());
                            documentId = documentReference.getId();
                            setupButtonSubscribe(true);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Firestore task", "Error adding document", e);
                            btnSubscribe.setEnabled(true);
                        }
                    });

                } else {
                    db.collection("subscriptions").document(documentId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                setupButtonSubscribe(false);
                                documentId = "";
                            }
                            btnSubscribe.setEnabled(true);
                        }
                    });
                }
            }
        });
    }

    private void checkFollow() {

        db.collection("subscriptions")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("storeName", nombreTiendaPerfil)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        //read subscription id
                        documentId = task.getResult().getDocuments().get(0).getId();
                    }
                    setupButtonSubscribe(!task.getResult().isEmpty());
                }
            }
        });
    }

    private void setupButtonSubscribe(boolean subscribed) {

        if (subscribed) {
            btnSubscribe.setText("Subscripto");
            btnSubscribe.setBackgroundColor(getResources().getColor(R.color.primary_dark));
            mSubscribed = true;
        } else {
            btnSubscribe.setText("Subscribirse");
            btnSubscribe.setBackgroundColor(getResources().getColor(R.color.accent));
        }

        btnSubscribe.setEnabled(true);
    }


    private void setupGridView() {

        Log.d("Store gridView", "setupGridView: Setting up store grid.");
        final ArrayList<Article> storeArticles = new ArrayList<Article>();
        db.collection("articles").whereEqualTo("storeName", nombreTiendaPerfil).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    storeArticles.addAll(task.getResult().toObjects(Article.class));

                    int widthGrid = getResources().getDisplayMetrics().widthPixels;
                    int imageWidth = widthGrid / NUM_COLUMNS;
                    gridArticlesStore.setColumnWidth(imageWidth);

                    ArrayList<String> articlesPhotosUrls = new ArrayList<String>();
                    for (int i = 0; i < storeArticles.size(); i++) {
                        articlesPhotosUrls.add(storeArticles.get(i).getPicture());
                    }

                    GridImageAdapter gridAdapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview, articlesPhotosUrls);
                    gridArticlesStore.setAdapter(gridAdapter);

                } else {
                    Log.d("Firestore task", "onComplete: " + task.getException());
                }
            }
        });

    }
}
