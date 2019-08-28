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
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Interaction;
import com.mylook.mylook.session.MainActivity;
import com.mylook.mylook.storeProfile.StoreActivity;
import com.mylook.mylook.utils.SlidingImageAdapter;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArticleInfoActivity extends AppCompatActivity {

    public static final int RESULT_OK = 1;
    public static final int RESULT_CANCELLED = 2;

    private Context mContext = this;
    private ImageView articleImage;
    private ExpandableListView expandableListView;
    private FloatingActionButton btnCloset;
    private FloatingActionButton btnShare;
    private String articleId,closetId;
    private String downLoadUri, dbUserId;
    private Article article;
    private ArrayList<String> tags, imageArraySlider;
    private LinearLayout lnlSizes, lnlColors;
    private TextView txtMaterial, txtCost, txtTitle, txtStoreName;
    private boolean inCloset;
    private boolean initialInCloset;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private boolean fromDeepLink = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar_more_info);
        invalidateOptionsMenu();
        getArticleFromIntent();
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
            article.setArticleId(id);
            dbUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            downLoadUri=article.getPicture();
            initElements();
            setDetail();
            isArticleInCloset();
        });
    }

    private void initElements() {
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

        btnCloset.setOnClickListener(v -> changeSavedInCloset());
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

    private void changeSavedInCloset() {
        btnCloset.setEnabled(false);
        FieldValue operation = !inCloset ?
                FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()) :
                FieldValue.arrayRemove(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseFirestore.getInstance().collection("articles").document(article.getArticleId())
                .update("favorites", operation)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        inCloset = !inCloset;
                        // TODO mal, no deberia guardar interaccion cada vez, ni tampoco sirve que le hayan sacado favorito. Solo sirve el estado actual (cantidad)
                        sendNewInteraction();
                        if (inCloset) {
                            setResult(RESULT_OK, new Intent().putExtra("removed", false)
                                    .putExtra("id", article.getArticleId()));
                            displayMessage("El artículo se añadió a tu ropero");
                        } else {
                            setResult(RESULT_OK, new Intent().putExtra("removed", true)
                                    .putExtra("id", article.getArticleId()));
                            displayMessage("El artículo se quitó de tu ropero");
                            FirebaseFirestore.getInstance().collection("outfits")
                                    .whereArrayContains("favorites", article.getArticleId())
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful() && task1.getResult() != null && !task1.getResult().isEmpty()) {
                                            List<String> outfitsToChange = new ArrayList<>();
                                            outfitsToChange.addAll(task1.getResult().getDocuments().stream().map(doc -> doc.getId()).collect(Collectors.toList()));
                                            if (outfitsToChange.size() != 0) {
                                                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                                                outfitsToChange.forEach(outfitDoc -> batch.update(FirebaseFirestore.getInstance()
                                                                .collection("outfits").document(outfitDoc),
                                                        "favorites", FieldValue.arrayRemove(article.getArticleId())));
                                                batch.commit().addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful()) {
                                                        displayMessage(outfitsToChange.size() + " conjuntos fueron actualizados");
                                                    }
                                                });
                                            }
                                        }
                                    });


                        }
                        setFavoriteFabIcon(inCloset);
                    } else {
                        if (!inCloset) {
                            displayMessage("Error al añadir al ropero");
                        } else {
                            displayMessage("Error al quitar del ropero");
                        }
                    }
                    btnCloset.setEnabled(true);
                });

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
            intentVisitStore.putExtra("store", article.getStoreName());
            mContext.startActivity(intentVisitStore);
        });
    }

    private void isArticleInCloset() {
        FirebaseFirestore.getInstance().collection("articles").document(article.getArticleId()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Article art = task.getResult().toObject(Article.class);
                        if (art.getFavorites() != null) {
                            initialInCloset = art.getFavorites().contains(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        } else {
                            initialInCloset = false;
                        }
                        inCloset = initialInCloset;
                        setFavoriteFabIcon(inCloset);
                        btnCloset.setEnabled(true);
                    } else {
                        btnCloset.setEnabled(false);
                    }
                    setResult(RESULT_CANCELLED, null);
                });
    }

    private void setFavoriteFabIcon(boolean faved) {
        if (faved) {
            btnCloset.setImageResource(R.drawable.ic_favorite_white_48dp);
        } else {
            btnCloset.setImageResource(R.drawable.ic_favorite_border_white_48dp);
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(fromDeepLink){
            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}