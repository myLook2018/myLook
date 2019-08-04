package com.mylook.mylook.explore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.services.ExploreService;
import com.mylook.mylook.utils.CardsExploreAdapter;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.List;
import java.util.Objects;

public class ExploreFragment extends Fragment implements CardStackListener, CardsExploreAdapter.ArticleVisitListener {

    private static String TAG = "ExploreFragment";

    private ProgressBar mProgressBar;
    private TextView mMessage;

    private FloatingActionButton expandNavbarButton;

    private OnNavbarToggle listener;
    private boolean navbarShowing;

    private CardStackView mCardStack;
    private CardStackLayoutManager mLayoutManager;
    private CardsExploreAdapter mCardAdapter;
    private List<Article> articles;

    @SuppressLint("StaticFieldLeak")
    private static ExploreFragment exploreInstance;

    private ExploreService exploreService;

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {
        switch (direction) {
            case Left:
                likeArticle(false);
                break;
            case Right:
                likeArticle(true);
                break;
        }
    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {

    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }

    @Override
    public void onArticleClick() {
        visitArticle();
    }

    private enum ViewName {
        PROGRESS_BAR,
        MESSAGE,
        CARD_STACK
    }

    public static ExploreFragment getInstance() {
        if (exploreInstance == null) exploreInstance = new ExploreFragment();
        return exploreInstance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_explore, container, false); // maybe remove container and add null
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        if (exploreService == null) {
            getArticles();
        } else {
            mCardStack.setAdapter(mCardAdapter);
            viewOnly(ViewName.CARD_STACK);
        }
    }

    private void init(View view) {
        mProgressBar = view.findViewById(R.id.explore_progress_bar);
        mMessage = view.findViewById(R.id.explore_message);
        mCardStack = view.findViewById(R.id.container);
        setupLayoutManager();

        expandNavbarButton = view.findViewById(R.id.fab_expand_navbar);
        navbarShowing = true;
        expandNavbarButton.setOnClickListener(v -> {
            AnimationSet animSet = new AnimationSet(true);
            animSet.setInterpolator(new DecelerateInterpolator());
            animSet.setFillAfter(true);
            animSet.setFillEnabled(true);

            final RotateAnimation animRotate;
            if (navbarShowing) {
                animRotate = new RotateAnimation(0.0f, 180.0f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            } else {
                animRotate = new RotateAnimation(180.0f, 0.0f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            }
            animRotate.setDuration(300);
            animRotate.setFillAfter(true);
            animSet.addAnimation(animRotate);
            v.startAnimation(animSet);
            if (navbarShowing) {
                listener.onHide();
            } else {
                listener.onShow();
            }
            navbarShowing = !navbarShowing;
        });

        FloatingActionButton dislikeButton = view.findViewById(R.id.fab_dislike_article);
        dislikeButton.setOnClickListener(v -> {
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Right)
                    .setDuration(Duration.Slow.duration)
                    .setInterpolator(new AccelerateInterpolator())
                    .build();
            mLayoutManager.setSwipeAnimationSetting(setting);
            mCardStack.swipe();
        });

        FloatingActionButton likeButton = view.findViewById(R.id.fab_like_article);
        likeButton.setOnClickListener(v -> {
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Slow.duration)
                    .setInterpolator(new AccelerateInterpolator())
                    .build();
            mLayoutManager.setSwipeAnimationSetting(setting);
            mCardStack.swipe();
        });
    }

    private void setupLayoutManager() {
        mLayoutManager = new CardStackLayoutManager(getContext());
        mLayoutManager.setStackFrom(StackFrom.Top);
        mLayoutManager.setVisibleCount(3);
        mLayoutManager.setTranslationInterval(4.0f);
        mLayoutManager.setScaleInterval(0.95f);
        mLayoutManager.setMaxDegree(0.0f);
        mLayoutManager.setDirections(Direction.HORIZONTAL);
        mLayoutManager.setSwipeThreshold(0.4f);
        mLayoutManager.setCanScrollHorizontal(true);
        mLayoutManager.setCanScrollVertical(true);
        mLayoutManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        mCardStack.setLayoutManager(mLayoutManager);
    }

    private void likeArticle(boolean liked) {
        Article article = articles.remove(0);
        exploreService.likeArticle(article, liked);
        if (mCardAdapter.getItemCount() == 0) {
            viewOnly(ExploreFragment.ViewName.MESSAGE);
        }
    }

    private void visitArticle() {
        Article article = articles.get(0);
        exploreService.visitArticle(article);
        Intent intent = new Intent(getContext(), ArticleInfoActivity.class);
        intent.putExtra("article", article);
        Objects.requireNonNull(getContext()).startActivity(intent);
    }

    private void viewOnly(ViewName view) {
        switch (view) {
            case PROGRESS_BAR:
                mProgressBar.setVisibility(View.VISIBLE);
                mMessage.setVisibility(View.GONE);
                mCardStack.setVisibility(View.GONE);
                break;
            case MESSAGE:
                mMessage.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mCardStack.setVisibility(View.GONE);
                break;
            case CARD_STACK:
                mCardStack.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mMessage.setVisibility(View.GONE);
                break;
        }
    }

    private void getArticles() {
        viewOnly(ViewName.PROGRESS_BAR);
        exploreService = new ExploreService(getContext());
        exploreService.getArticles().addOnCompleteListener(task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                viewOnly(ExploreFragment.ViewName.MESSAGE);
                Log.e(TAG, "getArticles", task.getException());
            } else if (task.getResult().isEmpty()) {
                viewOnly(ExploreFragment.ViewName.MESSAGE);
                Log.d(TAG, "getArticles - No articles found");
            } else {
                articles = exploreService.createExploreArticleList(task.getResult());
                Log.d(TAG, "getArticles - Articles found: " + articles.size());
                mCardAdapter = new CardsExploreAdapter(Objects.requireNonNull(getContext()), articles, this);
                mCardStack.setAdapter(mCardAdapter);
                viewOnly(ExploreFragment.ViewName.CARD_STACK);
                expandNavbarButton.performClick();
            }
        });
    }

    @Override
    public void onStop() {
        exploreService.uploadInteractions();
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d("SUBMIT", "onQueryTextSubmit: query->" + s);
                Intent intent = new Intent(getContext(), SearchableActivity.class);
                intent.putExtra("query", s);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("CHANGE", "onQueryTextChange: newText->" + s);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                return true;
            case R.id.refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refresh() {
        exploreService.uploadInteractions();
        if (mCardAdapter.getItemCount() == 0) {
            getArticles();
        } else {
            Toast.makeText(getContext(), "Todavía tenés artículos para explorar!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnNavbarToggle {
        void onHide();
        void onShow();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnNavbarToggle) context;
        } catch (ClassCastException castException) {
            Log.e(TAG, "onAttach: activity does not implement the listener", castException);
        }
    }
}
