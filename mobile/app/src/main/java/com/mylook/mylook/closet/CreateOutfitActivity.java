package com.mylook.mylook.closet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Outfit;
import com.mylook.mylook.info.ArticleInfoActivity;

public class CreateOutfitActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String outfitName, outfitCategory;
    private ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_outfit);
        recyclerView = findViewById(R.id.create_outfit_recyclerview);
        ImageButton btnSend = findViewById(R.id.btnSendOutfit);
        btnSend.setOnClickListener(v -> sendOutfit());
        mProgressBar = findViewById(R.id.mProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        initElements();
        setRecyclerView();
    }

    private void setRecyclerView() {
        CreateOutfitAdapter adapter = new CreateOutfitAdapter(this, );
        recyclerView.setAdapter(adapter);
        recyclerView.setOnItemClickListener((parent, v, position, id) -> {
            Intent intent = new Intent(getApplicationContext(), ArticleInfoActivity.class);
            intent.putExtra("article", favorites.get(position));
            getApplicationContext().startActivity(intent);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendOutfit() {
        Outfit createdOutfit = new Outfit(outfitName, outfitCategory, selectedArticles);
        if (createdOutfit.getFavorites() != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            FirebaseFirestore.getInstance().collection("outfits")
                    .add(createdOutfit).addOnCompleteListener(task -> {
                Toast.makeText(CreateOutfitActivity.this,
                        "Se ha creado tu conjunto", Toast.LENGTH_SHORT).show();
                finish();
            });
        } else {
            Toast.makeText(CreateOutfitActivity.this,
                    "Debes agrergar por lo menos una prenda", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}