package com.mylook.mylook.closet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Outfit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OutfitCreateEditActivity extends AppCompatActivity {

    public static final int OUTFIT_EDIT_REQUEST = 1;
    public static final int OUTFIT_CREATE_REQUEST = 2;
    private GridView gridView;
    private OutfitCreateEditAdapter adapter;
    private ProgressBar progressBar;
    private EditText editText;
    private Outfit outfit;
    private List<Integer> selected;
    private List<Article> all;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outfit_create_edit_layout);
        initElements();
        getAll();
    }

    private void initElements() {
        Toolbar tb = findViewById(R.id.outfit_create_edit_toolbar);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.outfit_create_edit_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        editText = findViewById(R.id.outfit_create_edit_name);
        gridView = findViewById(R.id.outfit_create_edit_grid);
        gridView.setColumnWidth(getResources().getDisplayMetrics().widthPixels / 3);
        gridView.setHorizontalSpacing(8);
        gridView.setOnItemClickListener((parent, v, position, id) -> selectForOutfit(v, position));
    }

    private void getAll() {
        all = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("articles")
                .whereArrayContains("favorites", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        all.addAll(task.getResult().getDocuments().stream().map(document -> {
                            Article art = document.toObject(Article.class);
                            art.setArticleId(document.getId());
                            return art;
                        }).collect(Collectors.toList()));
                    }
                    getDataFromIntent();
                    adapter = new OutfitCreateEditAdapter(this, all, selected);
                    adapter.getSelected().stream().forEach(art -> {
                        Log.d("", "selectForOutfit: selected " + art);
                    });
                    gridView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        ActionBar ab = getSupportActionBar();
        selected = new ArrayList<>();
        if (intent.getBooleanExtra("create", false)) {
            Log.d("", "getDataFromIntent: creado");
            outfit = new Outfit();
            if (ab != null) ab.setTitle("Nuevo conjunto");
        } else {
            Log.d("", "getDataFromIntent: editando");
            outfit = (Outfit) intent.getSerializableExtra("outfit");
            outfit.getArticles().forEach(artSel -> all.forEach(artAll -> {
                if (artAll.getArticleId().equals(artSel.getArticleId())) {
                    Log.d("", "getDataFromIntent: match");
                    // indexOf no funciona, porque el objeto no es el mismo
                    selected.add(outfit.getArticles().indexOf(artAll));
                }
            }));
            editText.setText(outfit.getName());
            if (ab != null) ab.setTitle("Editar conjunto");
        }
    }

    private void selectForOutfit(View v, int position) {
        adapter.getSelected().stream().forEach(art -> {
            Log.d("", "selectForOutfit: selected " + position);
        });
        if (adapter.getSelected().contains(position)) {
            Log.d("", "selectForOutfit: not selected");
            adapter.getSelected().remove(position);
            ((SelectableImageView) v).displayAsSelected(false);
        } else {
            Log.d("", "selectForOutfit: selected");
            adapter.getSelected().add(position);
            ((SelectableImageView) v).displayAsSelected(true);
        }
        adapter.getSelected().stream().forEach(art -> {
            Log.d("", "selectForOutfit: selected " + position);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }
}
