package com.mylook.mylook.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mylook.mylook.entities.Favorite;

import java.util.ArrayList;


public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    private ArrayList<Favorite> favorites;

    public ImageAdapter(Context c,ArrayList favorites) {
        mContext = c;
        this.favorites=favorites;
        //getCloset();

    }
    public int getCount() {
        Log.e("FAVORITES",String.valueOf(favorites.size()));
        return favorites.size();
    }
    public Object getItem(int position) {
        return favorites.get(position).getArticleId();
    }
    public long getItemId(int position) {
        return 0;
    }
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {

            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setPadding(1, 1, 1, 1);
        } else {
            imageView = (ImageView) convertView;
        }
        Glide.with(mContext).asBitmap().load(favorites.get(position).getDownloadUri()).into(imageView);
        return imageView;
    }
}