package com.mylook.mylook.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;

public class CardsDataAdapter extends ArrayAdapter<Article> {

    public CardsDataAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View contentView, @NonNull ViewGroup parent) {
        Article a = getItem(position);
        ImageView i = contentView.findViewById(R.id.image_content);
        //i.setImageResource(R.drawable.ic_launcher);
        Glide.with(getContext()).load(a.getPicture()).into(i);
        TextView t = contentView.findViewById(R.id.text_content);
        t.setText(a.getStoreName());

        return contentView;
    }

}
