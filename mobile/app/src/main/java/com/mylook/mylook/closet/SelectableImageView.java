package com.mylook.mylook.closet;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;

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
