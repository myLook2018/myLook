package com.mylook.mylook.explore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Interaction;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.room.AppDatabase;
import com.mylook.mylook.room.LocalInteraction;
import com.mylook.mylook.room.LocalInteractionDAO;
import com.mylook.mylook.utils.BottomNavigationViewHelper;
import com.mylook.mylook.utils.CardsExploreAdapter;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.SwipeDirection;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ExploreActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 1;

    private Context mContext = ExploreActivity.this;
    private List<Article> mDiscoverableArticles;
    private CardStackView mCardStack;
    private CardsExploreAdapter mCardAdapter;
    private ArrayList<Interaction> interactions;
    private List<LocalInteraction> mLocalInteractions;
    private ProgressBar mProgressBar;
    private TextView mMessage;

    private LocalInteractionDAO localDAO;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private int currentIndex = 0;
    private int totalArticles = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        mCardStack = findViewById(R.id.container);
        mCardStack.setVisibility(View.GONE);
        mProgressBar = findViewById(R.id.explore_progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        mMessage = findViewById(R.id.explore_message);
        mMessage.setVisibility(View.GONE);

        AppDatabase dbSQL = AppDatabase.getDatabase(mContext);
        localDAO = dbSQL.getLocalInteractionDAO();
        mLocalInteractions = localDAO.getAllByUser(user.getUid());

        setupBottomNavigationView();
        getDiscoverableArticles();
        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Explorar");
        setSupportActionBar(tb);


        interactions = new ArrayList<>();
        mCardStack.setCardEventListener(new CardStackView.CardEventListener() {
            @Override
            public void onCardDragging(float percentX, float percentY) {
                Log.d("CardStackView", "onCardDragging");
            }

            @Override
            public void onCardSwiped(SwipeDirection direction) {
                Interaction userInteraction = new Interaction();
                userInteraction.setSavedToCloset(false);
                userInteraction.setClickOnArticle(false);
                userInteraction.setPromotionLevel(mDiscoverableArticles.get(currentIndex).getPromotionLevel());
                userInteraction.setLiked(direction.toString().equalsIgnoreCase("right"));
                userInteraction.setArticleId(mDiscoverableArticles.get(currentIndex).getArticleId());
                userInteraction.setStoreName(mDiscoverableArticles.get(currentIndex).getStoreName());
                userInteraction.setTags(mDiscoverableArticles.get(currentIndex).getTags());
                userInteraction.setUserId(user.getUid());
                interactions.add(userInteraction);

                LocalInteraction local = new LocalInteraction();
                local.setUid(mDiscoverableArticles.get(currentIndex).getArticleId());
                local.setUserId(user.getUid());
                local.setDate(Calendar.getInstance().getTime());
                localDAO.insert(local);

                currentIndex++;
                totalArticles--;
                if (totalArticles == 0) {
                    mCardStack.setVisibility(View.GONE);
                    mMessage.setText("No quedan artículos para explorar.\n Intentá más tarde.");
                    mMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCardReversed() {
                Log.d("CardStackView", "onCardReversed");
            }

            @Override
            public void onCardMovedToOrigin() {
                Log.d("CardStackView", "onCardMovedToOrigin");
            }

            @Override
            public void onCardClicked(int index) {
                Interaction userInteraction = new Interaction();
                userInteraction.setPromotionLevel(mDiscoverableArticles.get(currentIndex).getPromotionLevel());
                userInteraction.setSavedToCloset(false);
                userInteraction.setLiked(false);
                userInteraction.setClickOnArticle(true);
                userInteraction.setArticleId(mDiscoverableArticles.get(currentIndex).getArticleId());
                userInteraction.setStoreName(mDiscoverableArticles.get(currentIndex).getStoreName());
                userInteraction.setTags(mDiscoverableArticles.get(currentIndex).getTags());
                userInteraction.setUserId(user.getUid());
                interactions.add(userInteraction);

                Intent intent = new Intent(mContext, ArticleInfoActivity.class);
                Article art = mDiscoverableArticles.get(index);
                intent.putExtra("article", art);
                intent.putExtra("tags", userInteraction.getTags());
                mContext.startActivity(intent);
            }
        });
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    private void getDiscoverableArticles() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -14);
        Date dateBefore2Weeks = cal.getTime();
        db.collection("articles").whereGreaterThan("creationDate", dateBefore2Weeks).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().isEmpty()) {
                                mProgressBar.setVisibility(View.GONE);
                                mMessage.setVisibility(View.VISIBLE);
                            }
                            else {
                                createArticleList(task.getResult());
                                if (totalArticles != 0) {
                                    mCardAdapter = new CardsExploreAdapter(getApplicationContext(), R.layout.article_card);
                                    mCardAdapter.addAll(mDiscoverableArticles);
                                    mCardStack.setAdapter(mCardAdapter);
                                    mProgressBar.setVisibility(View.GONE);
                                    mMessage.setVisibility(View.GONE);
                                    mCardStack.setVisibility(View.VISIBLE);
                                } else {
                                    mProgressBar.setVisibility(View.GONE);
                                    mMessage.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            mProgressBar.setVisibility(View.GONE);
                            mMessage.setVisibility(View.VISIBLE);
                            Log.w("", task.getException());
                        }
                    }
                });
    }

    private void createArticleList(QuerySnapshot result) {
        mDiscoverableArticles = new ArrayList<>();
        List<Article> promo1 = new ArrayList<>();
        List<Article> promo2 = new ArrayList<>();
        List<Article> promo3 = new ArrayList<>();
        for (QueryDocumentSnapshot document : result) {
            if (isNew(document.getId())) {
                Article art = document.toObject(Article.class);
                art.setArticleId(document.getId());
                int pl = art.getPromotionLevel();
                switch (pl) {
                    case 1:
                        promo1.add(art);
                        break;
                    case 2:
                        promo2.add(art);
                        break;
                    case 3:
                        promo3.add(art);
                        break;
                }
                totalArticles++;
            }
        }
        if (totalArticles == 0) return;
        if (!promo3.isEmpty()) Collections.shuffle(promo3);
        if (!promo2.isEmpty()) Collections.shuffle(promo2);
        if (!promo1.isEmpty()) Collections.shuffle(promo1);
        Random r = new Random();
        int v;
        while (true) {
            if (!promo3.isEmpty()) {
                if (!promo2.isEmpty()) {
                    if (!promo1.isEmpty()) {
                        v = r.nextInt(100);
                        if (v > 54) mDiscoverableArticles.add(promo3.remove(0));
                        else if (v > 21) mDiscoverableArticles.add(promo2.remove(0));
                        else mDiscoverableArticles.add(promo1.remove(0));
                    } else {
                        v = r.nextInt(100);
                        if (v > 35) mDiscoverableArticles.add(promo3.remove(0));
                        else mDiscoverableArticles.add(promo2.remove(0));
                    }
                } else {
                    if (!promo1.isEmpty()) {
                        v = r.nextInt(100);
                        if (v > 32) mDiscoverableArticles.add(promo3.remove(0));
                        else mDiscoverableArticles.add(promo1.remove(0));
                    } else mDiscoverableArticles.add(promo3.remove(0));
                }
            } else {
                if (!promo2.isEmpty()) {
                    if (!promo1.isEmpty()) {
                        v = r.nextInt(100);
                        if (v > 39) mDiscoverableArticles.add(promo2.remove(0));
                        else mDiscoverableArticles.add(promo1.remove(0));
                    } else mDiscoverableArticles.add(promo2.remove(0));
                } else {
                    if (!promo1.isEmpty()) mDiscoverableArticles.add(promo1.remove(0));
                    else return;
                }
            }
        }
    }

    private boolean isNew(String id) {
        Log.d("isNew", "starting " + id);
        for (LocalInteraction li : mLocalInteractions) {
            Log.d("isNew", li.getUid());
            if (li.getUid().equals(id)) {
                Log.d("isNew", "false");
                return false;
            }
        }
        Log.d("isNew", "true");
        return true;
    }

    @Override
    protected void onStop() {
        uploadInteractions();
        super.onStop();
    }

    private void uploadInteractions() {
        for (Interaction interaction : interactions) {
            db.collection("interactions").add(interaction);
        }
        interactions.clear();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item=menu.findItem(R.id.btnSearch);
        SearchView searchView=(SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d("SUBMIT", "onQueryTextSubmit: query->"+s);
                Intent intent = new Intent(mContext, SearchableActivity.class);
                intent.putExtra("query",s);
                startActivity(intent);
                finish();
                return true;            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("CHANGE", "onQueryTextChange: newText->" + s);
               return false;
            }
        });
        Log.e("OPTIONSMENU", "onCreateOptionsMenu: mSearchmenuItem->" + item.getActionView());
        return true;
    }

    public boolean onSearchRequested(){
        Bundle appData = new Bundle();
        startSearch(null, false, appData, false);
        return true;

    }
}
