package com.mylook.mylook.closet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Favorite;
import com.mylook.mylook.entities.Outfit;
import com.mylook.mylook.info.ArticleInfoActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateOutfitActivity extends AppCompatActivity {

    private GridView gridView;
    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private ArrayList<Favorite> favorites = new ArrayList<>();
    private ArrayList<Favorite> checkedFavorites = new ArrayList<>();
    private HashMap<String, String> outfitItems;
    private String outfitName, outfitCategory;
    private ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_outfit);
        gridView = findViewById(R.id.favorites_grid);
        ImageButton btnSend = findViewById(R.id.btnSendOutfit);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOutfit();
            }
        });
        mProgressBar = findViewById(R.id.mProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        initElements();
    }

    private void setGridView() {
        Log.d("GRIDVIEW", "setGridView: wtf oe ps agg tmr grid chucha tu mare");
        int imageWidth = getResources().getDisplayMetrics().widthPixels / 2;
        gridView.setColumnWidth(imageWidth);
        gridView.setHorizontalSpacing(8);
        gridView.setNumColumns(2);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ArticleInfoActivity.class);
                intent.putExtra("article", favorites.get(position));
                getApplicationContext().startActivity(intent);
            }
        });
    }

    private void initElements() {
        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Tu conjunto");
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        outfitName = getIntent().getExtras().get("name").toString();
        outfitCategory = getIntent().getExtras().get("category").toString();
        outfitName = getIntent().getExtras().get("name").toString();
        getOutfits();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getOutfits() {
        dB.collection("outfits")
                .whereEqualTo("userID", Sesion.getInstance().getSessionUserId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String outfitId = document.getId();
                        }
                        setGridView();
                    }
                });
    }

    private void sendOutfit() {
        final Outfit nuevo = createOutfit();
        if (outfitName == null && nuevo.getItems() != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            dB.collection("closets")
                    .whereEqualTo("userID", Sesion.getSessionUserId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String id = task.getResult().getDocuments().get(0).getId();
                            dB.collection("closets").document(id).collection("outfits").add(nuevo).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(CreateOutfitActivity.this, "Se ha creado tu conjunto", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    });
        } else if (nuevo.getItems() != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            dB.collection("closets").whereEqualTo("userID", Sesion.getSessionUserId()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String id = task.getResult().getDocuments().get(0).getId();
                            dB.collection("closets").document(id).collection("outfits").document(outfitName)
                                    .update("items", outfitItems).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), "Cambiaste tu conjunto", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), ClosetActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivityIfNeeded(intent, 0);
                                }
                            });
                        }
                    });
        } else {
            Toast.makeText(CreateOutfitActivity.this, "Debes agrergar por lo menos una prenda", Toast.LENGTH_SHORT).show();
        }
    }

    private Outfit createOutfit() {
        Outfit nuevo = new Outfit();
        nuevo.setName(outfitName);
        nuevo.setCategory(outfitCategory);
        nuevo.setItems(outfitItems);
        return nuevo;
    }

    private void getOutfit() {
        dB.collection("closets")
                .whereEqualTo("userID", Sesion.getInstance().getSessionUserId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                dB.collection("closets").document(id).collection("outfits").document(outfitName).get()
                                        .addOnCompleteListener(task1 -> {
                                            Outfit old = task1.getResult().toObject(Outfit.class);
                                            outfitItems = old.getItems();
                                            loadOutfit();
                                        });
                            }
                        } else {
                            Log.e("FAVORITES", "NOOOOOOOOOOOOO");
                        }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadOutfit() {
        for (final String item : outfitItems.keySet()) {
            String articleId = outfitItems.get(item);
            loadImage(item, articleId);
        }

    }

    private void loadImage(final String item, String articleId) {
        dB.collection("articles").document(articleId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        View v = null;
                        if (item.getClass().equals(Integer.class))
                            v = findViewById(Integer.parseInt(item));
                        if (v == null) {
                            v = findViewById(getResources().getIdentifier(item, "id", getApplicationContext().getPackageName()));
                        }
                        String art = (String) task.getResult().get("picture");
                        Glide.with(CreateOutfitActivity.this).asBitmap().load(art).apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.DATA))
                                .into((ImageView) v);

                    }
                });
    }
}
