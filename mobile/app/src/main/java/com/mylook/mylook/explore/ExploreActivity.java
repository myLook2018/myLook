package com.mylook.mylook.explore;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.mylook.mylook.utils.BottomNavigationViewHelper;
import com.mylook.mylook.utils.CardsDataAdapter;
import com.wenchao.cardstack.CardStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExploreActivity extends AppCompatActivity implements ExploreStartFragment.OnFragmentInteractionListener, ExploreSearchFragment.OnFragmentInteractionListener{

    private static final int ACTIVITY_NUM = 1;

    private Context mContext = ExploreActivity.this;
    private List<Article> mDiscoverableArticles;
    private CardStack mCardStack;
    private CardsDataAdapter mCardAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        mCardStack = findViewById(R.id.container);
        mCardStack.setContentResource(R.layout.article_card);
        mCardStack.setStackMargin(20);

        setupBottomNavigationView();
        getDiscoverableArticles();
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
                            if (!mDiscoverableArticles.isEmpty()) Collections.shuffle(mDiscoverableArticles);
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
