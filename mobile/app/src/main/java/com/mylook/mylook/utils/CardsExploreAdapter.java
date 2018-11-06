package com.mylook.mylook.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;

public class CardsExploreAdapter extends ArrayAdapter<Article> {

    public CardsExploreAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View contentView, @NonNull ViewGroup parent) {
        /*Article a = getItem(position);
        ImageView i = contentView.findViewById(R.id.image_content);
        Glide.with(getContext()).load(a.getPicture()).into(i);
        TextView t = contentView.findViewById(R.id.text_content);
        t.setText(a.getStoreName());*/

        ViewHolder holder;

        if (contentView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            contentView = inflater.inflate(R.layout.article_card, parent, false);
            holder = new ViewHolder(contentView);
            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }

        Article a = getItem(position);
        holder.name.setText(a.getStoreName());
        Glide.with(getContext()).load(a.getPicture()).into(holder.image);

        if (a.getPromotionLevel() > 1) {
            contentView.findViewById(R.id.ad_layout).setVisibility(View.VISIBLE);
        } else {
            contentView.findViewById(R.id.ad_layout).setVisibility(View.INVISIBLE);
        }

        return contentView;
    }

    private static class ViewHolder {
        public TextView name;
        public ImageView image;

        public ViewHolder(View view) {
            this.name = (TextView) view.findViewById(R.id.text_content);
            this.image = (ImageView) view.findViewById(R.id.image_content);
        }
    }

}
