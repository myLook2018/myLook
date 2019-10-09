package com.mylook.mylook.closet;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mylook.mylook.entities.Article;

import java.util.List;

public class OutfitCreateEditAdapter extends BaseAdapter {

    private Context context;
    private List<Article> articles;
    private List<Integer> selected;

    OutfitCreateEditAdapter(Context context, List<Article> articles, List<Integer> selected) {
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
        Log.d("", "getView: rendering grid view item" + position);
        SelectableImageView imageView;
        if (convertView == null) {
            imageView = new SelectableImageView(context);
        } else {
            imageView = (SelectableImageView) convertView;
        }
        Glide.with(context).asBitmap().load(articles.get(position).getPicturesArray().get(0))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA))
                .into(imageView);
        imageView.displayAsSelected(selected.contains(position));
        return imageView;
    }

    List<Integer> getSelected() {
        return selected;
    }

}
