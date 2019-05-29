package com.mylook.mylook.closet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Outfit;
import com.mylook.mylook.utils.ArticlesGridAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OutfitCreateEditActivity extends AppCompatActivity {

    public static final int OUTFIT_EDIT_REQUEST = 1;
    public static final int OUTFIT_CREATE_REQUEST = 2;
    private GridView gridView;
    private OutfitCreateEditAdapter adapter;
    private ProgressBar progressBar;
    private Outfit outfit;
    private List<Article> selected;
    private List<Article> all;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outfit_create_edit_layout);
        initElements();
        getAll();
        getDataFromIntent();
        adapter = new OutfitCreateEditAdapter(this, all, selected);
        fillGrid();
    }

    private void initElements() {
        Toolbar tb = findViewById(R.id.outfit_articles_toolbar);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.outfit_articles_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        gridView = findViewById(R.id.outfit_articles_grid);
    }

    private void getAll() {
        FirebaseFirestore.getInstance().collection("articles")
                .whereArrayContains("favorites", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        all = task.getResult().getDocuments().stream().map(document -> {
                            Article art = document.toObject(Article.class);
                            art.setArticleId(document.getId());
                            return art;
                        }).collect(Collectors.toList());
                    }
                });
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        ActionBar ab = getSupportActionBar();
        if (intent.getBooleanExtra("create", false)) {
            outfit = new Outfit();
            selected = new ArrayList<>();
            if (ab != null) ab.setTitle("Nuevo conjunto");
        } else {
            outfit = (Outfit) intent.getSerializableExtra("outfit");
            selected = outfit.getArticles();
            if (ab != null) ab.setTitle("Editar conjunto");
        }
        gridView.setAdapter(adapter);
    }

    private void fillGrid() {
        gridView.setColumnWidth(getResources().getDisplayMetrics().widthPixels / 3);
        gridView.setHorizontalSpacing(8);
        gridView.setNumColumns(3);
        gridView.setOnItemClickListener((parent, v, position, id) -> selectForOutfit(v, position));
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    private void selectForOutfit(View v, int position) {
        Article clicked = adapter.getItem(position);
        if (adapter.getSelected().contains(clicked)) {
            adapter.getSelected().remove(clicked);
            ((SelectableArticleGridItemView) v).display(false);
        } else {
            adapter.getSelected().add(clicked);
            ((SelectableArticleGridItemView) v).display(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }
}
