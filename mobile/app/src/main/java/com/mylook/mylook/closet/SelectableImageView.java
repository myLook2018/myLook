package com.mylook.mylook.closet;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;

import com.mylook.mylook.R;

public class SelectableImageView extends AppCompatImageView {

    SelectableImageView(Context context) {
        super(context);
    }

    public void displayAsSelected(boolean isSelected) {
        if (isSelected) setForeground(ContextCompat.getDrawable(getContext(), R.drawable.selected_article_grid_item));
        else setForeground(ContextCompat.getDrawable(getContext(), R.drawable.not_selected_article_grid_item));
    }

}
