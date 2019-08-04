package com.mylook.mylook.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.RippleDrawable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mylook.mylook.R;
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
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.ripple_image_view, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Article art = getItem(position);
        Glide.with(context).asBitmap().load(art.getPicture())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA))
                .into(viewHolder.imageView);

        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;

        ViewHolder(View view) {
            imageView = view.findViewById(R.id.ripple_image_view_image);
        }
    }
}