package com.mylook.mylook.explore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
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

    private AppDatabase dbSQL;
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

        dbSQL = AppDatabase.getDatabase(mContext);
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
                userInteraction.setSavedToCloset(false);
                userInteraction.setLiked(false);
                userInteraction.setClickOnArticle(true);
                userInteraction.setArticleId(mDiscoverableArticles.get(currentIndex).getArticleId());
                userInteraction.setStoreName(mDiscoverableArticles.get(currentIndex).getStoreName());
                userInteraction.setTags(mDiscoverableArticles.get(currentIndex).getTags());
                userInteraction.setUserId(user.getUid());
                interactions.add(userInteraction);

                Log.d("CardStackView", "onCardClicked: " + index);
                Intent intent = new Intent(mContext, ArticleInfoActivity.class);
                Log.d("info del articulo", "onClick: paso por intent la data del articulo");
                Article art = mDiscoverableArticles.get(index);
                intent.putExtra("article", art);
                intent.putExtra("tags", userInteraction.getTags());
                mContext.startActivity(intent);
            }
        });
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
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
                            if (task.getResult().isEmpty()) {
                                mProgressBar.setVisibility(View.GONE);
                                mMessage.setVisibility(View.VISIBLE);
                            }
                            else {
                                createArticleList(task.getResult());
                                mCardAdapter = new CardsExploreAdapter(getApplicationContext(), R.layout.article_card);
                                mCardAdapter.addAll(mDiscoverableArticles);
                                mCardStack.setAdapter(mCardAdapter);
                                mProgressBar.setVisibility(View.GONE);
                                mMessage.setVisibility(View.GONE);
                                mCardStack.setVisibility(View.VISIBLE);
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
                        Log.d("ADDING", "PROMO1");
                        promo1.add(art);
                        break;
                    case 2:
                        Log.d("ADDING", "PROMO2");
                        promo2.add(art);
                        break;
                    case 3:
                        Log.d("ADDING", "PROMO3");
                        promo3.add(art);
                        break;
                }
                totalArticles++;
            }
        }
        if (totalArticles == 0) {
            mProgressBar.setVisibility(View.GONE);
            mMessage.setVisibility(View.VISIBLE);
            return;
        }
        if (!promo3.isEmpty()) {
            Collections.shuffle(promo3);
        }
        if (!promo2.isEmpty()) {
            Collections.shuffle(promo2);
        }
        if (!promo1.isEmpty()) {
            Collections.shuffle(promo1);
        }
        Random r = new Random();
        int v;
        while (true) {
            if (!promo3.isEmpty()) {
                if (!promo2.isEmpty()) {
                    if (!promo1.isEmpty()) {
                        v = r.nextInt(100);
                        if (v > 54) {
                            mDiscoverableArticles.add(promo3.remove(0));
                        } else if (v > 21) {
                            mDiscoverableArticles.add(promo2.remove(0));
                        } else {
                            mDiscoverableArticles.add(promo1.remove(0));
                        }
                    } else {
                        v = r.nextInt(100);
                        if (v > 35) {
                            mDiscoverableArticles.add(promo3.remove(0));
                        } else {
                            mDiscoverableArticles.add(promo2.remove(0));
                        }
                    }
                } else {
                    if (!promo1.isEmpty()) {
                        v = r.nextInt(100);
                        if (v > 32) {
                            mDiscoverableArticles.add(promo3.remove(0));
                        } else {
                            mDiscoverableArticles.add(promo1.remove(0));
                        }
                    } else {
                        mDiscoverableArticles.add(promo3.remove(0));
                    }
                }
            } else {
                if (!promo2.isEmpty()) {
                    if (!promo1.isEmpty()) {
                        v = r.nextInt(100);
                        if (v > 39) {
                            mDiscoverableArticles.add(promo2.remove(0));
                        } else {
                            mDiscoverableArticles.add(promo1.remove(0));
                        }
                    } else {
                        mDiscoverableArticles.add(promo2.remove(0));
                    }
                } else {
                    if (!promo1.isEmpty()) {
                        mDiscoverableArticles.add(promo1.remove(0));
                    } else {
                        break;
                    }
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
    protected void onDestroy() {
        super.onDestroy();
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
}
