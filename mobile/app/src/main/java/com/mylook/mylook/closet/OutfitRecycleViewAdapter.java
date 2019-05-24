package com.mylook.mylook.closet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Favorite;

import java.util.ArrayList;
import java.util.List;

public class OutfitRecycleViewAdapter extends RecyclerView.Adapter<OutfitRecycleViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Favorite> articleList;
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
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
        return new ViewHolder(view, i);

    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView requestPhoto;
        ConstraintLayout parentLayout;

        ViewHolder(View itemView, int position) {
            super(itemView);
            requestPhoto = itemView.findViewById(R.id.requestPhoto);
            parentLayout=itemView.findViewById(R.id.parentLayout);
            itemView.setTag(String.valueOf(posicionActual));
            itemView.setOnClickListener(v -> {

            });
            itemView.setOnLongClickListener(v -> false);
            posicionActual++;
        }
    }
}
