package com.mylook.mylook.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mylook.mylook.entities.Article;

import java.util.List;

public class ArticlesGridAdapter extends BaseAdapter {

    private Context context;
    private List<Article> articles;

    public ArticlesGridAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.articles = articles;
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
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
        } else {
            imageView = (ImageView) convertView;
        }
        Glide.with(context).asBitmap().load(articles.get(position).getPicture())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA))
                .into(imageView);
        return imageView;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }
}