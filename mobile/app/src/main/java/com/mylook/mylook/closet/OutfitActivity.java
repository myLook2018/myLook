package com.mylook.mylook.closet;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Closet;
import com.mylook.mylook.entities.Favorite;
import com.mylook.mylook.entities.Outfit;
import com.mylook.mylook.info.ArticleInfoActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class OutfitActivity extends AppCompatActivity {

    private GridView gridView;
    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private ArrayList<Favorite> favorites = new ArrayList<>();
    private ArrayList<Favorite> selectedFavorites = new ArrayList<>();
    private HashMap<String, String> outfitItems;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Toolbar tb;
    private String outfitName, category;
    private ImageButton btnSend;
    private ProgressBar mProgressBar;
    private String outfitId, dbUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_outfit);
        gridView = findViewById(R.id.favorites_grid);
        user = FirebaseAuth.getInstance().getCurrentUser();
        btnSend = findViewById(R.id.btnSendOutfit);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOutfit();
            }
        });
        mProgressBar = findViewById(R.id.mProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        initElements();
        getUserId();
    }

    private void setGridView() {
        int widthGrid = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = widthGrid / 3;
        gridView.setColumnWidth(imageWidth);
        gridView.setHorizontalSpacing(8);
        gridView.setNumColumns(3);
        setClickListener();
    }
    private void setClickListener() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                dB.collection("articles").document(((Favorite) parent.getAdapter().getItem(position)).getArticleId()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Article art = task.getResult().toObject(Article.class);
                                    art.setArticleId(task.getResult().getId());
                                    Intent intent = new Intent(OutfitActivity.this, ArticleInfoActivity.class);
                                    intent.putExtra("article", art);
                                    OutfitActivity.this.startActivity(intent);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(OutfitActivity.this, "No se han podido cargar tus favoritos", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v,
                                           int position, long id) {
                if (selectedFavorites == null) {
                    selectedFavorites = new ArrayList();
                }
                Log.d("LONGCLICKED", Boolean.toString(v.isSelected()));
                selectedFavorites.add((Favorite) parent.getAdapter().getItem(position));
                Log.d("LONGCLICKED", Boolean.toString(v.isSelected()));
                Log.d("LONGCLICKED", Integer.toString(selectedFavorites.size()));
                return v.isSelected();
            }
        });
    }

    private final class MyTouchListener implements View.OnLongClickListener {
        public boolean onLongClick(View view) {
            isFromOutfit = true;
            ClipData image = ClipData.newPlainText("position", (String) view.getTag());
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            activeView = (ImageView) view;
            view.startDrag(image, shadowBuilder, view, 0);
            return true;
        }
    }

    private void initElements() {
        tb = findViewById(R.id.toolbar);
        tb.setTitle("Tu conjunto");
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        outfitName = getIntent().getExtras().get("name").toString();
        category = getIntent().getExtras().get("category").toString();
        try {
            outfitId = getIntent().getExtras().get("id").toString();
        } catch (Exception e) {
            Log.e("Outfit", "Its a new outfit");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getUserId() {
        dB.collection("clients").whereEqualTo("userId", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                    dbUserId = task.getResult().getDocuments().get(0).get("userId").toString();
                    if (outfitId != null) {
                        getOutfit();
                    }
                    getCloset();
                } else {
                    dB.collection("clients").whereEqualTo("email", user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                dbUserId = task.getResult().getDocuments().get(0).get("userId").toString();
                                if (outfitId != null) {
                                    getOutfit();
                                }
                                getCloset();
                            }
                        }
                    });
                }
            }
        });
    }

    private void getCloset() {
        dB.collection("closets")
                .whereEqualTo("userID", dbUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                closet = document.toObject(Closet.class);
                                String id = document.getId();
                                dB.collection("closets").document(id).collection("favorites").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    ArrayList<String> arrayList = new ArrayList<>();
                                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                        Favorite fav = documentSnapshot.toObject(Favorite.class);
                                                        favorites.add(fav);
                                                        arrayList.add(fav.getDownloadUri());
                                                    }
                                                    selectedFavorites = favorites;
                                                    setGridView();
                                                    return;
                                                } else
                                                    Log.e("FAVORITES", "Nuuuuuuuuuuuuuuuuuuuuuu");
                                            }
                                        });
                            }
                        } else {
                            Log.e("FAVORITES", "NOOOOOOOOOOOOO");
                        }
                    }
                });
    }

    private void sendOutfit() {
        final Outfit nuevo = createOutfit();
        if (outfitId == null && nuevo.getItems()!=null) {
            mProgressBar.setVisibility(View.VISIBLE);
            dB.collection("closets")
                    .whereEqualTo("userID", dbUserId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                closet = task.getResult().getDocuments().get(0).toObject(Closet.class);
                                String id = task.getResult().getDocuments().get(0).getId();
                                dB.collection("closets").document(id).collection("outfits").add(nuevo).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(OutfitActivity.this, "Se ha creado tu conjunto", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        }
                    });
        } else if(nuevo.getItems()!=null) {
            mProgressBar.setVisibility(View.VISIBLE);
            dB.collection("closets").whereEqualTo("userID", dbUserId).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                String id = task.getResult().getDocuments().get(0).getId();
                                dB.collection("closets").document(id).collection("outfits").document(outfitId)
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
                        }
                    });
        } else {
            Toast.makeText(OutfitActivity.this, "Debes agrergar por lo menos una prenda", Toast.LENGTH_SHORT).show();
        }
    }

    private Outfit createOutfit() {
        Outfit nuevo = new Outfit();
        nuevo.setName(outfitName);
        nuevo.setCategory(category);
        nuevo.setItems(outfitItems);
        return nuevo;
    }

    private void getOutfit() {
        dB.collection("closets")
                .whereEqualTo("userID", dbUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                closet = document.toObject(Closet.class);
                                String id = document.getId();
                                dB.collection("closets").document(id).collection("outfits").document(outfitId).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                Outfit old = task.getResult().toObject(Outfit.class);
                                                outfitItems = old.getItems();
                                                loadOutfit();
                                            }
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
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            View v = null;
                            if (item.getClass().equals(Integer.class))
                                v = findViewById(Integer.parseInt(item));
                            if (v == null) {
                                v = findViewById(getResources().getIdentifier(item, "id", getApplicationContext().getPackageName()));
                            }
                            String art = (String) task.getResult().get("picture");
                            Glide.with(OutfitActivity.this).asBitmap().load(art).apply(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.DATA))
                                    .into((ImageView) v);

                        }
                    }
                });
    }
}
