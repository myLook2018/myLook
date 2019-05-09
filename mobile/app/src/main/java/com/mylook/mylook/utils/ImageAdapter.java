package com.mylook.mylook.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mylook.mylook.entities.Article;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Article> articles;

    public ImageAdapter(Context context, ArrayList articles) {
        this.context = context;
        this.articles = articles;
    }

    public int getCount() {
        return articles.size();
    }

    public Object getItem(int position) {
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
}