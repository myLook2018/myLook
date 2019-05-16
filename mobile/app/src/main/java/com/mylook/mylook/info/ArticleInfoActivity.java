package com.mylook.mylook.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Interaction;
import com.mylook.mylook.storeProfile.StoreActivity;
import com.mylook.mylook.utils.SlidingImageAdapter;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Objects;

public class ArticleInfoActivity extends AppCompatActivity {

    private Context mContext = ArticleInfoActivity.this;
    private Article article;
    private FloatingActionButton btnCloset;
    private String articleId;
    private ArrayList<String> tags;
    private String downLoadUri;
    private String dbUserId = Sesion.getInstance().getSessionUserId();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private TextView txtTitle;
    private TextView txtStoreName;
    private LinearLayout lnlSizes;
    private LinearLayout lnlColors;
    private TextView txtMaterial;
    private TextView txtCost;
    private ArrayList<String> imageArraySlider;
    private boolean inCloset;

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
        articleId = article.getArticleId();
        downLoadUri = article.getPicture();
        if (article.getPicturesArray() != null) {
            imageArraySlider = new ArrayList<>();
            imageArraySlider.addAll(article.getPicturesArray());
        }
        tags = intent.getStringArrayListExtra("tags");
    }

    private void initElements() {
        ImageView backArrow = findViewById(R.id.backArrow);
        btnCloset = findViewById(R.id.btnCloset);
        txtTitle = findViewById(R.id.txtTitle);
        txtStoreName = findViewById(R.id.txtStoreName);
        txtMaterial = findViewById(R.id.txtMaterial);
        txtCost = findViewById(R.id.txtCost);
        lnlSizes = findViewById(R.id.lnlSizes);
        lnlColors = findViewById(R.id.lnlColors);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnCloset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("", "pressed: ");
                if (inCloset) {
                    removeFromCloset();
                } else {
                    saveInCloset();
                }
                inCloset = !inCloset;
            }
        });

        ViewPager articlePager;
        if (imageArraySlider == null) {
            ArrayList<String> arrayAux = new ArrayList<>();
            arrayAux.add(0, downLoadUri);
            articlePager = findViewById(R.id.view_pager_article);
            articlePager.setAdapter(new SlidingImageAdapter(mContext, arrayAux));
            CirclePageIndicator indicator = findViewById(R.id.circle_page_indicator);
            indicator.setViewPager(articlePager);
            indicator.setRadius(5 * getResources().getDisplayMetrics().density);
        } else {
            articlePager = findViewById(R.id.view_pager_article);
            articlePager.setAdapter(new SlidingImageAdapter(mContext, imageArraySlider));
            CirclePageIndicator indicator = findViewById(R.id.circle_page_indicator);
            indicator.setViewPager(articlePager);
            indicator.setRadius(5 * getResources().getDisplayMetrics().density);
        }
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
        txtStoreName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVisitStore = new Intent(mContext, StoreActivity.class);
                intentVisitStore.putExtra("Tienda", article.getStoreName());
                mContext.startActivity(intentVisitStore);
            }
        });
    }

    private void isArticleInCloset() {
        Log.d("", "isArticleInCloset: ");
        dB.collection("articles").document(articleId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document == null || !document.exists()) {
                        btnCloset.setEnabled(false);
                        return;
                    }
                    if (Objects.equals(document.get("favorites"), dbUserId)) {
                        Log.d("", "onComplete: WTFFFFFFF");
                        btnCloset.setEnabled(true);
                        inCloset = true;
                        return;
                    }
                    btnCloset.setEnabled(true);
                    inCloset = false;
                } else {
                    btnCloset.setEnabled(false);
                }
            }
        });
    }

    private void removeFromCloset() {
        btnCloset.setEnabled(false);
        dB.collection("articles").document(article.getArticleId())
                .update("favorites", FieldValue.arrayRemove(dbUserId)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    sendNewInteraction();
                    displayMessage("Se eliminó de tu ropero");
                    Sesion.getInstance().updateActivitiesStatus(Sesion.HOME_FRAGMENT, Sesion.CLOSET_FRAGMENT);
                } else {
                    displayMessage("Error al eliminar del ropero");
                }
                btnCloset.setEnabled(true);
            }
        });
    }

    private void saveInCloset() {
        btnCloset.setEnabled(false);
        dB.collection("articles").document(article.getArticleId())
                .update("favorites", FieldValue.arrayUnion(dbUserId)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    sendNewInteraction();
                    displayMessage("Se añadió a tu ropero");
                    Sesion.getInstance().updateActivitiesStatus(Sesion.HOME_FRAGMENT, Sesion.CLOSET_FRAGMENT);
                } else {
                    displayMessage("Error al añadir al ropero");
                }
                btnCloset.setEnabled(true);
            }
        });
    }

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    // TODO eliminar y contar desde articulos... hace falta refactorizar relacionados
    private void sendNewInteraction() {
        Interaction userInteraction = new Interaction();
        Log.e("PROMOTION LEVEL", article.getPromotionLevel() + "");
        userInteraction.setPromotionLevel(article.getPromotionLevel());
        userInteraction.setSavedToCloset(true);
        userInteraction.setLiked(false);
        userInteraction.setClickOnArticle(false);
        userInteraction.setArticleId(this.articleId);
        userInteraction.setStoreName(this.article.getStoreName());
        userInteraction.setTags(tags);
        userInteraction.setUserId(user.getUid());
        dB.collection("interactions").add(userInteraction);
    }
}
