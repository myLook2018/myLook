package com.mylook.mylook.closet;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.info.ArticleInfoActivity;

import java.util.List;

public class CreateOutfitAdapter extends RecyclerView.Adapter<CreateOutfitAdapter.ViewHolder> {

    private Context mContext;
    private List<Article> articles;
    private List<Article> selectedArticles;
    private int currentPosition = 0;

    public CreateOutfitAdapter(Context mContext, List<Article> articles, List<Article> selectedArticles) {
        this.mContext = mContext;
        this.articles = articles;
        this.selectedArticles = selectedArticles;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        final Article article = articles.get(i);
        Glide.with(mContext).asBitmap().load(article.getPicture()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_create_outfit_card, parent, false);
        return new ViewHolder(view, i);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout parentLayout;
        ImageView image;
        TextView title, store;
        CheckBox checkBox;

        ViewHolder(View itemView, int position) {
            super(itemView);
            image = itemView.findViewById(R.id.favorite_create_outfit_image);
            title = itemView.findViewById(R.id.favorite_create_outfit_title);
            store = itemView.findViewById(R.id.favorite_create_outfit_store);
            checkBox = itemView.findViewById(R.id.favorite_create_outfit_checkbox);

            parentLayout = itemView.findViewById(R.id.parentLayout);
            itemView.setTag(String.valueOf(currentPosition));
            image.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, ArticleInfoActivity.class);
                intent.putExtra("article",
                        articles.get(position));
                mContext.startActivity(intent);
            });
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedArticles.add(articles.get(position));
                } else {
                    selectedArticles.remove(articles.get(position));
                }
            });
            currentPosition++;
        }
    }
}

