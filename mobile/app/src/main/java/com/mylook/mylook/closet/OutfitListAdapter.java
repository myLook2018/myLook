package com.mylook.mylook.closet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Outfit;

import java.util.List;

public class OutfitListAdapter extends RecyclerView.Adapter<OutfitListAdapter.OutfitViewHolder> {

    private List<Outfit> mOutfits;
    private Context mContext;
    private OutfitClickListener mClickListener;

    OutfitListAdapter(Context mContext, List<Outfit> mOutfits) {
        this.mContext = mContext;
        this.mOutfits = mOutfits;
    }

    @NonNull
    @Override
    public OutfitViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.outfit_element_layout, viewGroup,
                false);
        return new OutfitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutfitViewHolder outfitViewHolder, int i) {
        Outfit outfit = mOutfits.get(i);
        outfitViewHolder.name.setText(outfit.getName());
        int count = outfit.getArticles().size();
        if (count >= 1) Glide.with(mContext).asDrawable()
                .load(outfit.getArticles().get(0).getPicture())
                .into(outfitViewHolder.image1);
        else outfitViewHolder.image1.setVisibility(View.GONE);
        if (count >= 2) Glide.with(mContext).asDrawable()
                .load(outfit.getArticles().get(1).getPicture())
                .into(outfitViewHolder.image2);
        else outfitViewHolder.image2.setVisibility(View.GONE);
        if (count >= 3) Glide.with(mContext).asDrawable()
                .load(outfit.getArticles().get(2).getPicture())
                .into(outfitViewHolder.image3);
        else outfitViewHolder.image3.setVisibility(View.GONE);
        if (count >= 4) {
            int extra = count - 3;
            outfitViewHolder.extra.setText(String.format("+%d", extra));
        } else {
            outfitViewHolder.extra.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mOutfits.size();
    }

    class OutfitViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, extra;
        ImageView image1, image2, image3;

        OutfitViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.outfit_name);
            extra = itemView.findViewById(R.id.outfit_extra_articles);
            image1 = itemView.findViewById(R.id.outfit_article_image1);
            image2 = itemView.findViewById(R.id.outfit_article_image2);
            image3 = itemView.findViewById(R.id.outfit_article_image3);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onOutfitClick(v, getAdapterPosition());
        }
    }

    Outfit getItem(int position) {
        return mOutfits.get(position);
    }

    void setClickListener(OutfitClickListener outfitClickListener) {
        this.mClickListener = outfitClickListener;
    }

    interface OutfitClickListener {
        void onOutfitClick(View view, int position);
    }
}
