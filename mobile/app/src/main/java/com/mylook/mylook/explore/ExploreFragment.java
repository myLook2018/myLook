package com.mylook.mylook.explore;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.utils.CardsExploreAdapter;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExploreFragment extends Fragment implements CardStackListener, CardsExploreAdapter.ArticleVisitListener {

    private static final int ACCESS_FINE_LOCATION_REQUEST = 1;
    private static String TAG = "ExploreFragment";

    private ProgressBar mProgressBar;
    private TextView mMessage;

    private CardStackView mCardStack;
    private CardStackLayoutManager mLayoutManager;
    private CardsExploreAdapter mCardAdapter;
    private LinearLayout buttonsLayout;
    private FrameLayout sliderLayout;

    private List<Article> articles;

    @SuppressLint("StaticFieldLeak")
    private static ExploreFragment exploreInstance;

    private ExploreService exploreService;
    private LocationManager locationManager;

    private double distance;

    @Override
    public void onCardDragging(Direction direction, float ratio) {}

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
    public void onCardRewound() {}

    @Override
    public void onCardCanceled() {}

    @Override
    public void onCardAppeared(View view, int position) {}

    @Override
    public void onCardDisappeared(View view, int position) {}

    @Override
    public void onArticleClick() {
        visitArticle();
    }

    private enum ViewName {
        PROGRESS_BAR,
        MESSAGE,
        MESSAGE_LOCATION,
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

        viewOnly(ViewName.PROGRESS_BAR);
        exploreService = new ExploreService(getContext());
        getArticles(null);
    }

    private void init(View view) {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        mProgressBar = view.findViewById(R.id.explore_progress_bar);
        mMessage = view.findViewById(R.id.explore_message);

        mCardStack = view.findViewById(R.id.explore_card_stack);
        mLayoutManager = new CardStackLayoutManager(getContext(), this);
        setupLayoutManager();
        mCardStack.setLayoutManager(mLayoutManager);
        articles = new ArrayList<>();
        mCardAdapter = new CardsExploreAdapter(Objects.requireNonNull(getContext()), articles, this);
        mCardStack.setAdapter(mCardAdapter);

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

        buttonsLayout = view.findViewById(R.id.explore_buttons_layout);
        buttonsLayout.bringToFront();
        sliderLayout = view.findViewById(R.id.explore_slider_layout);

        FloatingActionButton geolocationButton = view.findViewById(R.id.fab_geolocation);
        geolocationButton.setOnClickListener(v -> setGeolocation());

        TextView distanceLabel = view.findViewById(R.id.explore_label);

        SeekBar distanceSlider = view.findViewById(R.id.explore_slider);
        distanceSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance = progress;
                distanceLabel.setText(progress == 0 ? "Desactivado" : "< " + progress + " metros");
                if (progress == 0) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.slider_thumb_disabled, null));
                    distanceLabel.setTextColor(getResources().getColor(R.color.red, null));
                } else {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.slider_thumb_enabled, null));
                    distanceLabel.setTextColor(getResources().getColor(R.color.primary_text, null));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                confirmGeolocation();
            }
        });

        distance = distanceSlider.getProgress();
        if (distance == 0) {
            distanceLabel.setText("Desactivado");
        } else {
            distanceLabel.setText(String.format("< %d metros", distanceSlider.getProgress()));
        }
    }

    private void setGeolocation() {
        if (isPermissionGranted()) {
            showSlider(true);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST);
        }
    }

    private void confirmGeolocation() {
        if (distance == 0) {
            getArticles(null);
        }
        if (!isPermissionGranted()) {
            displayMessage("Necesitamos permisos para acceder a tu ubicación.");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST);
        } else if (!isGPSActive()) {
            displayMessage("Activa tu GPS para acceder a tu ubicación.");
        } else {
            tryGetArticlesWithLocation();
        }
        showSlider(false);
    }

    private void showSlider(boolean show) {
        if (show) {
            sliderLayout.bringToFront();
            sliderLayout.setAlpha(0f);
            sliderLayout.setVisibility(View.VISIBLE);
            sliderLayout.animate()
                    .alpha(1f)
                    .setDuration(Duration.Fast.duration)
                    .setListener(null);
            buttonsLayout.animate()
                    .alpha(0f)
                    .setDuration(Duration.Fast.duration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            buttonsLayout.setVisibility(View.GONE);
                        }
                    });
        } else {
            buttonsLayout.bringToFront();
            buttonsLayout.setAlpha(0f);
            buttonsLayout.setVisibility(View.VISIBLE);
            buttonsLayout.animate()
                    .alpha(1f)
                    .setDuration(Duration.Fast.duration)
                    .setListener(null);
            sliderLayout.animate()
                    .alpha(0f)
                    .setDuration(Duration.Fast.duration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            sliderLayout.setVisibility(View.GONE);
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCESS_FINE_LOCATION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSlider(true);
            } else {
                displayMessage("Necesitamos permisos para acceder a tu ubicación.");
            }
        }
    }

    private void tryGetArticlesWithLocation() {
        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            viewOnly(ViewName.PROGRESS_BAR);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            locationManager.requestSingleUpdate(criteria, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        getArticles(location);
                    } else {
                        displayMessage("No pudo accederse a tu ubicación");
                        getArticles(null);
                    }
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override
                public void onProviderEnabled(String provider) {}
                @Override
                public void onProviderDisabled(String provider) {}
            }, null);
        } else {
            displayMessage("Necesitamos permisos para acceder a tu ubicación.");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST);
        }
    }

    private void setupLayoutManager() {
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
    }

    private void likeArticle(boolean liked) {
        if (!articles.isEmpty()) {
            Article article = articles.get(mLayoutManager.getTopPosition() - 1);
            exploreService.likeArticle(article, liked);
            if (articles.size() == mLayoutManager.getTopPosition()) {
                viewOnly(ExploreFragment.ViewName.MESSAGE);
            }
        }
    }

    private void visitArticle() {
        if (!articles.isEmpty()) {
            Article article = articles.get(mLayoutManager.getTopPosition());
            exploreService.visitArticle(article);
            Intent intent = new Intent(getContext(), ArticleInfoActivity.class);
            intent.putExtra("article", article);
            Objects.requireNonNull(getContext()).startActivity(intent);
        }
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
                mMessage.setText("¡No encontramos artículos!\nIntentá más tarde");
                break;
            case MESSAGE_LOCATION:
                mMessage.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mCardStack.setVisibility(View.GONE);
                mMessage.setText("¡No encontramos artículos!\nAumentá el radio de búsqueda\no intentá más tarde");
                break;
            case CARD_STACK:
                mCardStack.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mMessage.setVisibility(View.GONE);
                break;
        }
    }

    private void getArticles(Location location) {
        viewOnly(ViewName.PROGRESS_BAR);
        articles.clear();
        exploreService.getArticles().addOnCompleteListener(task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                viewOnly(ViewName.MESSAGE);
                Log.e(TAG, "getArticles", task.getException());
            } else if (task.getResult().isEmpty()) {
                viewOnly(ViewName.MESSAGE);
                Log.d(TAG, "getArticles - No articles found");
            } else {
                articles.addAll(exploreService.createExploreArticleList(task.getResult(), location, distance));
                mCardAdapter.notifyDataSetChanged();
                Log.d(TAG, "getArticles - Articles found: " + articles.size());
                if (articles.isEmpty()) {
                    viewOnly(ViewName.MESSAGE_LOCATION);
                } else {
                    viewOnly(ViewName.CARD_STACK);
                }
            }
        });
    }

    @Override
    public void onStop() {
        if (exploreService != null) {
            exploreService.uploadInteractions();
        }
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
                Intent intent = new Intent(getContext(), SearchableActivity.class);
                intent.putExtra("query", s);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
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
        if (isPermissionGranted() && isGPSActive() && distance != 0) {
            tryGetArticlesWithLocation();
        } else {
            getArticles(null);
        }
    }

    private void displayMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private boolean isGPSActive() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean isPermissionGranted() {
        return getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}