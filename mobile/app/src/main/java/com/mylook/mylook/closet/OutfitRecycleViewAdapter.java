package com.mylook.mylook.closet;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Favorite;
import com.mylook.mylook.recommend.RequestRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class OutfitRecycleViewAdapter extends RecyclerView.Adapter<OutfitRecycleViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Favorite> articleList = new ArrayList<>();
    private int posicionActual = 0;


    public OutfitRecycleViewAdapter(Context mContext, List<Favorite> articleList) {
        this.mContext = mContext;
        this.articleList = articleList;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        final Favorite article = articleList.get(i);
        Glide.with(mContext).asBitmap().load(article.getDownloadUri()).into(holder.requestPhoto);

    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_image, parent, false);
        ViewHolder holder = new ViewHolder(view, i);
        return holder;

    }




    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView requestPhoto;
        ConstraintLayout parentLayout;
        String uri;


        public ViewHolder(View itemView, int position) {
            super(itemView);
            requestPhoto = itemView.findViewById(R.id.requestPhoto);
            parentLayout=itemView.findViewById(R.id.parentLayout);
            itemView.setTag(String.valueOf(posicionActual));
            itemView.setOnLongClickListener(new MyTouchListener());
            posicionActual++;
        }

        private final class MyTouchListener implements View.OnLongClickListener {
            public boolean onLongClick(View view) {
                    ClipData image = ClipData.newPlainText("position", (String) view.getTag());
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(image, shadowBuilder, view, 0);
                    view.setVisibility(View.INVISIBLE);
                    return true;
            }
        }




    }
}
