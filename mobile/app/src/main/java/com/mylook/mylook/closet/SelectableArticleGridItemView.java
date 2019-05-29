package com.mylook.mylook.closet;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mylook.mylook.R;

public class SelectableArticleGridItemView extends FrameLayout {

    private ImageView imageView;

    public SelectableArticleGridItemView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.selectable_article_grid_item, this);
        imageView = getRootView().findViewById(R.id.selectable_article_grid_item_image);
    }

    public void display(boolean isSelected) {
        imageView.setBackgroundResource(isSelected ? R.drawable.selected_article_grid_item : null);
    }

    public ImageView getImageView() {
        return imageView;
    }

}
