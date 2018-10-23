package com.mylook.mylook.explore;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.utils.BottomNavigationViewHelper;
import com.mylook.mylook.utils.CardsDataAdapter;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.SwipeDirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExploreActivity extends AppCompatActivity implements ExploreStartFragment.OnFragmentInteractionListener, ExploreSearchFragment.OnFragmentInteractionListener {

    private static final int ACTIVITY_NUM = 1;

    private Context mContext = ExploreActivity.this;
    private List<Article> mDiscoverableArticles;
    //private CardStack mCardStack;
    private CardStackView mCardStack;
    private CardsDataAdapter mCardAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        mCardStack = findViewById(R.id.container);
        //mCardStack.setContentResource(R.layout.article_card);

        setupBottomNavigationView();
        getDiscoverableArticles();
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle("Explorar");
        setSupportActionBar(tb);

        mCardStack.setCardEventListener(new CardStackView.CardEventListener() {
            @Override
            public void onCardDragging(float percentX, float percentY) {
                Log.d("CardStackView", "onCardDragging");
            }

            @Override
            public void onCardSwiped(SwipeDirection direction) {
                Log.d("CardStackView", "onCardSwiped: " + direction.toString());
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
                Log.d("CardStackView", "onCardClicked: " + index);
                Intent intent = new Intent(mContext, ArticleInfoActivity.class);
                Log.d("info del articulo", "onClick: paso por intent la data del articulo");
                Article art = mDiscoverableArticles.get(index);
                intent.putExtra("Colores", art.getColors());
                intent.putExtra("Costo", art.getCost());
                intent.putExtra("Stock", art.getInitial_stock());
                intent.putExtra("Material", art.getMaterial());
                intent.putExtra("Talle", art.getSize());
                intent.putExtra("Tienda", art.getStoreName());
                intent.putExtra("Foto", art.getPicture());
                intent.putExtra("Title",art.getTitle());
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
        db.collection("articles").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mDiscoverableArticles = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Article art = document.toObject(Article.class);
                                mDiscoverableArticles.add(art);
                            }
                            if (!mDiscoverableArticles.isEmpty())
                                Collections.shuffle(mDiscoverableArticles);
                            mCardAdapter = new CardsDataAdapter(getApplicationContext(), R.layout.article_card);
                            mCardAdapter.addAll(mDiscoverableArticles);
                            mCardStack.setAdapter(mCardAdapter);
                        } else {
                            Log.w("", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
