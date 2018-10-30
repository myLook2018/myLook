package com.mylook.mylook.closet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.recommend.RequestRecyclerViewAdapter;

import java.util.List;

public class OutfitRecycleViewAdapter extends RecyclerView.Adapter<OutfitRecycleViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Article> articleList;


    public OutfitRecycleViewAdapter(Context mContext, List<Article> articleList) {
        this.mContext = mContext;
        this.articleList = articleList;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        final Article article = articleList.get(i);
        Glide.with(mContext).asBitmap().load(article.getPicture()).into(holder.requestPhoto);

    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item_request_recommendation, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;

    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView requestPhoto;

        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            requestPhoto = itemView.findViewById(R.id.imgRequestPhoto);
            parentLayout=itemView.findViewById(R.id.parentLayout);
        }
    }
}
