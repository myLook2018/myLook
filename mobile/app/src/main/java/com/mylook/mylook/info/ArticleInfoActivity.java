package com.mylook.mylook.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Interaction;
import com.mylook.mylook.session.MainActivity;
import com.mylook.mylook.session.Sesion;
import com.mylook.mylook.entities.Outfit;
import com.mylook.mylook.storeProfile.StoreActivity;
import com.mylook.mylook.utils.SlidingImageAdapter;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class ArticleInfoActivity extends AppCompatActivity {

    private Context mContext = ArticleInfoActivity.this;
    private ImageView backArrow, articleImage;

    private ExpandableListView expandableListView;
    private FloatingActionButton btnCloset;
    private FloatingActionButton btnShare;
    private String articleId,closetId;
    private String downLoadUri, dbUserId;
    private Article article;
    private FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore dB =FirebaseFirestore.getInstance();
    private boolean fromDeepLink = false;
    private ArrayList<String> tags, imageArraySlider;
    private LinearLayout lnlSizes, lnlColors;
    private TextView txtMaterial, txtCost, txtTitle, txtStoreName;
    private boolean inCloset;
    private boolean alreadyInCloset;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar_more_info);
        invalidateOptionsMenu();
        getArticleFromIntent();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(fromDeepLink){
            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void changeSavedInCloset() {
        if (alreadyInCloset) {
            displayMessage("Ya se encuentra en favoritos");
            return;
        }
        inCloset = !inCloset;
        if (inCloset) btnCloset.setBackgroundDrawable(ContextCompat.getDrawable(this,
                R.drawable.ic_favorite_border_white_48dp));
        else btnCloset.setBackgroundDrawable(ContextCompat.getDrawable(this,
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

    private void initElements() {
        backArrow = findViewById(R.id.backArrow);
        btnCloset=findViewById(R.id.btnCloset);
        articleImage=findViewById(R.id.article_image);
        txtTitle=findViewById(R.id.txtTitle);
        txtStoreName=findViewById(R.id.txtStoreName);
        txtMaterial=findViewById(R.id.txtMaterial);
        txtCost=findViewById(R.id.txtCost);
        lnlSizes=findViewById(R.id.lnlSizes);
        lnlColors=findViewById(R.id.lnlColors);
        btnShare =  findViewById(R.id.btnShare);
        //Glide.with(mContext).load(downLoadUri).into(articleImage);
        backArrow.setOnClickListener(view -> finish());

        btnCloset.setOnClickListener(v -> saveOnCloset());
        btnShare.setOnClickListener(v -> shareArticle());
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

    private void shareArticle(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Mirá esta prenda! https://www.mylook.com/article?articleId="+articleId);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share via"));
    }

    private void getArticleFromIntent(){
        //retrieve data from intent
        Intent intent = getIntent();
        if(intent.hasExtra("article")) {
            article= (Article) intent.getSerializableExtra("article");
            articleId=article.getArticleId();
            fromDeepLink = false;
            initElements();
            setDetail();
            isArticleInCloset();
        } else{
            fromDeepLink = true;
            try {
                articleId = intent.getData().getQueryParameter("articleId");
            } catch (Exception e){
                articleId = intent.getStringExtra("articleId");
            }
            getArticleFromId(articleId);
        }
    }

    private void getArticleFromId(String id){
        dB.collection("articles").document(id).get().addOnCompleteListener(task -> {
            article = task.getResult().toObject(Article.class);
            dbUserId = Sesion.getInstance().userId;
            downLoadUri=article.getPicture();
            initElements();
            setDetail();
        });
    }



    private void saveOnCloset() {

        final Map<String, Object> favorites = new HashMap<>();
        favorites.put("articleId", articleId);
        favorites.put("downloadUri", downLoadUri);
        favorites.put("collecion", null);

        dB.collection("closets")
                .whereEqualTo("userID",dbUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult().getDocuments().size()>0){
                        closetId=task.getResult().getDocuments().get(0).getId();
                        dB.collection("closets").document(closetId).collection("favorites")
                                .whereEqualTo("articleId",articleId).get()
                                .addOnCompleteListener(task1 -> {
                                    if(task1.getResult().getDocuments().size()==0){
                                        Log.e("CLOSET", closetId);
                                        dB.collection("closets").document(closetId).collection("favorites").add(favorites);
                                        sendNewInteraction();
                                        displayMessage("Se añadió a tu ropero");
                                        Sesion.getInstance().updateActivitiesStatus(Sesion.HOME_FRAGMENT, Sesion.CLOSET_FRAGMENT);
                                    }else
                                    {
                                        displayMessage("Ya está en tu ropero");
                                    }
                                });
                    }else
                    {
                        displayMessage("No tienes un ropero asociado");
                    }
                });
    }
    @Override
    protected void onDestroy() {
        if (inCloset) {
            FirebaseFirestore.getInstance().collection("articles").document(article.getArticleId())
                    .update("favorites", FieldValue.arrayUnion(Sesion.getInstance().userId))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            sendNewInteraction();
                            displayMessage("Se añadió a tu ropero");
                        } else {
                            displayMessage("Error al añadir al ropero");                        
                        }
                    });
        }
        super.onDestroy();
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
                if (Objects.equals(document.get("favorites"), Sesion.getInstance().userId)) {
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
        userInteraction.setUserId(Sesion.getInstance().userId);
        FirebaseFirestore.getInstance().collection("interactions").add(userInteraction);
    }

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}
