package com.mylook.mylook.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Favorite;
import com.mylook.mylook.entities.Outfit;

import java.util.ArrayList;

public class OutfitAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Outfit> outfits;
    public OutfitAdapter(Context c,ArrayList favorites) {
        mContext = c;
        this.outfits=favorites;

    }


    public int getCount() {
        return outfits.size();
    }

    public Object getItem(int position) {
        return outfits.get(position);
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
        imageView.setAdjustViewBounds(true);
        Glide.with(mContext).asDrawable().load(R.drawable.photo_gallery).into(imageView);
        return imageView;
    }
}
