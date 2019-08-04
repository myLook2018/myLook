package com.mylook.mylook.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Interaction;
import com.mylook.mylook.session.Session;
import com.mylook.mylook.storeProfile.StoreActivity;
import com.mylook.mylook.utils.SlidingImageAdapter;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Objects;

public class ArticleInfoActivity extends AppCompatActivity {

    private Context mContext = ArticleInfoActivity.this;
    private Article article;
    private FloatingActionButton btnCloset;
    private ArrayList<String> tags, imageArraySlider;
    private LinearLayout lnlSizes, lnlColors;
    private TextView txtMaterial, txtCost, txtTitle, txtStoreName;
    private boolean inCloset;
    private boolean alreadyInCloset;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);
        getArticleFromIntent();
        initElements();
        setDetail();
        isArticleInCloset();
    }

    private void getArticleFromIntent() {
        Intent intent = getIntent();
        article = (Article) intent.getSerializableExtra("article");
        if (article.getPicturesArray() != null) {
            imageArraySlider = new ArrayList<>();
            imageArraySlider.addAll(article.getPicturesArray());
        }
        tags = intent.getStringArrayListExtra("tags");
    }

    private void initElements() {
        Toolbar tb = findViewById(R.id.article_info_toolbar);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);

        btnCloset = findViewById(R.id.btnCloset);
        txtTitle = findViewById(R.id.txtTitle);
        txtStoreName = findViewById(R.id.txtStoreName);
        txtMaterial = findViewById(R.id.txtMaterial);
        txtCost = findViewById(R.id.txtCost);
        lnlSizes = findViewById(R.id.lnlSizes);
        lnlColors = findViewById(R.id.lnlColors);
        btnCloset.setOnClickListener(v -> changeSavedInCloset());
        ViewPager articlePager;
        articlePager = findViewById(R.id.view_pager_article);
        if (imageArraySlider == null) {
            ArrayList<String> arrayAux = new ArrayList<>();
            arrayAux.add(0, article.getPicture());
            articlePager.setAdapter(new SlidingImageAdapter(mContext, arrayAux));
            CirclePageIndicator indicator = findViewById(R.id.circle_page_indicator);
            indicator.setViewPager(articlePager);
            indicator.setRadius(5 * getResources().getDisplayMetrics().density);
        } else {
            articlePager.setAdapter(new SlidingImageAdapter(mContext, imageArraySlider));
            CirclePageIndicator indicator = findViewById(R.id.circle_page_indicator);
            indicator.setViewPager(articlePager);
            indicator.setRadius(5 * getResources().getDisplayMetrics().density);
        }
    }

    private void changeSavedInCloset() {
        if (alreadyInCloset) {
            displayMessage("Ya se encuentra en favoritos.");
            return;
        }
        inCloset = !inCloset;
        if (inCloset) btnCloset.setBackground(ContextCompat.getDrawable(this,
                R.drawable.ic_favorite_border_white_48dp));
        else btnCloset.setBackground(ContextCompat.getDrawable(this,
                R.drawable.ic_favorite_white_48dp));
    }

    private void setDetail() {
        txtStoreName.setText(article.getStoreName());
        txtTitle.setText(article.getTitle());
        txtMaterial.setText(article.getMaterial());
        txtCost.setText(String.format("$%s", String.valueOf(article.getCost())));
        LinearLayout lnl = new LinearLayout(this);
        lnl.setOrientation(LinearLayout.VERTICAL);
        for (String size : article.getSizes()) {
            TextView item = new TextView(this);
            item.setText(size);
            lnl.addView(item);
        }
        lnlSizes.addView(lnl);
        LinearLayout lnl2 = new LinearLayout(this);
        for (String color : article.getColors()) {
            TextView item = new TextView(this);
            item.setText(color);
            lnl2.addView(item);
        }
        lnlColors.addView(lnl2);
        lnl2.setOrientation(LinearLayout.VERTICAL);
        txtStoreName.setOnClickListener(v -> {
            Intent intentVisitStore = new Intent(mContext, StoreActivity.class);
            intentVisitStore.putExtra("Tienda", article.getStoreName());
            mContext.startActivity(intentVisitStore);
        });
    }

    private void isArticleInCloset() {
        FirebaseFirestore.getInstance().collection("articles").document(article.getArticleId()).get()
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document == null || !document.exists()) {
                    btnCloset.setEnabled(false);
                    return;
                }
                if (Objects.equals(document.get("favorites"), FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    btnCloset.setEnabled(true);
                    alreadyInCloset = true;
                    inCloset = true;
                } else {
                    btnCloset.setEnabled(true);
                    alreadyInCloset = false;
                    inCloset = false;
                }
            } else {
                btnCloset.setEnabled(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (inCloset) {
            FirebaseFirestore.getInstance().collection("articles").document(article.getArticleId())
                    .update("favorites", FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            sendNewInteraction();
                            displayMessage("Se añadió a tu ropero");
                            Session.getInstance().setFavoriteAdded(true);
                        } else {
                            displayMessage("Error al añadir al ropero");
                        }
                    });
        }
        super.onDestroy();
    }

    private void sendNewInteraction() {
        Interaction userInteraction = new Interaction();
        Log.e("PROMOTION LEVEL", article.getPromotionLevel() + "");
        userInteraction.setPromotionLevel(article.getPromotionLevel());
        userInteraction.setSavedToCloset(true);
        userInteraction.setLiked(false);
        userInteraction.setClickOnArticle(false);
        userInteraction.setArticleId(this.article.getArticleId());
        userInteraction.setStoreName(this.article.getStoreName());
        userInteraction.setTags(tags);
        userInteraction.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseFirestore.getInstance().collection("interactions").add(userInteraction);
    }

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
