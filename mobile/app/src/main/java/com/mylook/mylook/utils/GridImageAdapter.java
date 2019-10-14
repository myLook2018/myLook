package com.mylook.mylook.utils;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.PremiumPublication;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.premiumUser.PublicationDetail;

import java.util.ArrayList;

public class GridImageAdapter extends ArrayAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private ArrayList<Article> articles;
    private ArrayList<PremiumPublication> publications;

    public GridImageAdapter(Context context, int layoutResource, ArrayList<Article> articles) {
        super(context, layoutResource, articles);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        this.layoutResource = layoutResource;
        this.articles = articles;
    }

    public GridImageAdapter(Context context, int layoutResource, ArrayList<PremiumPublication> publications, int i) {
        super(context, layoutResource, publications);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        this.layoutResource = layoutResource;
        this.publications = publications;
    }

    private static class ViewHolder {
        SquareImageView image;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();
            holder.image = convertView.findViewById(R.id.grid_image_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String imgUrl;
        if (publications == null) {
            final Article art = (Article) getItem(position);
            imgUrl = art.getPicturesArray().get(0);
            holder.image.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, ArticleInfoActivity.class);
                intent.putExtra("article", art);
                mContext.startActivity(intent);
            });
        } else {
            final PremiumPublication pub = (PremiumPublication) getItem(position);
            imgUrl = pub.getPublicationPhoto();
            holder.image.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, PublicationDetail.class);
                intent.putExtra("publication", pub);
                mContext.startActivity(intent);
            });
        }
        Glide.with(mContext).load(imgUrl).into(holder.image);
        return convertView;
    }


}