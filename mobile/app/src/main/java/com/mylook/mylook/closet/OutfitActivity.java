package com.mylook.mylook.closet;

import android.content.ClipData;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Closet;
import com.mylook.mylook.entities.Favorite;

import java.util.ArrayList;

public class OutfitActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private ArrayList<Favorite> favoritos = new ArrayList<>();
    private ArrayList<Favorite> favoritosModificados = new ArrayList<>();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Closet closet;
    private ConstraintLayout container;
    private ImageView bottomCloth, mediumCloth, topCloth, topAccesory, bottomAccesory, activeView;
    private Toolbar tb;
    private boolean isFromOutfit;


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
        initElements();
        getCloset();
        setupDragListener();

    }

    private void setupDragListener() {
        bottomCloth.setOnDragListener(new MyDragListener());
        bottomCloth.setOnLongClickListener(new MyTouchListener());
        topCloth.setOnDragListener(new MyDragListener());
        topCloth.setOnLongClickListener(new MyTouchListener());
        mediumCloth.setOnDragListener(new MyDragListener());
        mediumCloth.setOnLongClickListener(new MyTouchListener());
        topAccesory.setOnDragListener(new MyDragListener());
        topAccesory.setOnLongClickListener(new MyTouchListener());
        bottomAccesory.setOnDragListener(new MyDragListener());
        bottomAccesory.setOnLongClickListener(new MyTouchListener());
    }

    private class MyDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    ImageView nuevaPrenda = new ImageView(getApplicationContext());
                    String positionData = event.getClipData().getItemAt(0).getText().toString();
                    int position = Integer.parseInt(positionData);
                    String uri = favoritos.get(position).getDownloadUri();
                    nuevaPrenda.setAdjustViewBounds(true);
                    nuevaPrenda.setMaxHeight(150);
                    nuevaPrenda.setMaxWidth(150);
                    ((ImageView)v).setImageDrawable(null);
                    Glide.with(getApplicationContext()).asBitmap().load(uri).into((ImageView) v);
                    nuevaPrenda.setZ(5);
                    v.setTag(String.valueOf(position));
                    if (!isFromOutfit) {
                        favoritosModificados.remove(position);
                        loadRecycleViewer();
                        isFromOutfit = false;

                    } else {
                        activeView.setImageDrawable(getDrawable(R.drawable.white_background));
                    }

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                default:
                    break;
            }
            return true;
        }
    }

    private void loadRecycleViewer() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        OutfitRecycleViewAdapter adapter = new OutfitRecycleViewAdapter(getApplicationContext(), favoritosModificados);
        recyclerView.setAdapter(adapter);
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
    }

    private void getCloset() {
        dB.collection("closets")
                .whereEqualTo("userID", user.getUid())
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
                                                        favoritos.add(fav);
                                                        arrayList.add(fav.getDownloadUri());
                                                    }
                                                    favoritosModificados = favoritos;
                                                    loadRecycleViewer();
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
}
