package com.mylook.mylook.closet;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mylook.mylook.entities.Article;

import java.util.List;

public class OutfitCreateEditAdapter extends BaseAdapter {

    private Context context;
    private List<Article> articles;
    private List<Article> selected;

    public OutfitCreateEditAdapter(Context context, List<Article> articles, List<Article> selected) {
        this.context = context;
        this.articles = articles;
        this.selected = selected;
    }

    public int getCount() {
        return articles.size();
    }

    public Article getItem(int position) {
        return articles.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        SelectableArticleGridItemView gridItemView;
        ImageView imageView;
        if (convertView == null) {
            gridItemView = new SelectableArticleGridItemView(context);
            imageView = gridItemView.getImageView();
        } else {
            gridItemView = (SelectableArticleGridItemView) convertView;
            imageView = gridItemView.getImageView();
        }
        Glide.with(context).asBitmap().load(articles.get(position).getPicture())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA))
                .into(imageView);
        gridItemView.display(selected.contains(getItem(position)));
        return gridItemView;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }

    public List<Article> getSelected() {
        return selected;
    }

    public boolean isSelected(int position) {
        return selected.contains(getItem(position));
    }

}
