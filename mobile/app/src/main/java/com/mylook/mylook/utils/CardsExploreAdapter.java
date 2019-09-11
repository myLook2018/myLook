package com.mylook.mylook.utils;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;

import java.util.List;

public class CardsExploreAdapter extends RecyclerView.Adapter<CardsExploreAdapter.ViewHolder> {

    private Context context;
    private List<Article> articles;
    private ArticleVisitListener listener;

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView image;
        TextView ad;
        FrameLayout nearby;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.text_content);
            image = view.findViewById(R.id.image_content);
            ad = view.findViewById(R.id.ad_layout);
            nearby = view.findViewById(R.id.nearby_layout);
        }

    }

    public CardsExploreAdapter(@NonNull Context context, List<Article> articles, ArticleVisitListener listener) {
        this.context = context;
        this.articles = articles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articles.get(position);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if ( !activity.isFinishing() ) {
                Glide.with(context).load(article.getPicturesArray().get(0)).into(holder.image);
            }
        }
        holder.image.setOnClickListener(v -> listener.onArticleClick());
        holder.name.setText(article.getStoreName());
        if (article.getPromotionLevel() == 0) {
            holder.ad.setVisibility(View.GONE);
        }
        if (article.isNearby()) {
            holder.nearby.setVisibility(View.VISIBLE);
        } else {
            holder.nearby.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public interface ArticleVisitListener {
        void onArticleClick();
    }

}