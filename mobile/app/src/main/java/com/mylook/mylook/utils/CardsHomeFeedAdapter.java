package com.mylook.mylook.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.PremiumUser;
import com.mylook.mylook.entities.Store;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.premiumUser.PremiumUserProfileActivity;
import com.mylook.mylook.storeProfile.StoreActivity;

import java.util.List;

public class CardsHomeFeedAdapter extends RecyclerView.Adapter<CardsHomeFeedAdapter.MyViewHolder> {

    private Context mContext;
    private List articlesAndStoresList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nameStore, txtTitle;
        public ImageView articleImage;
        public CardView articleCardView;

        public MyViewHolder(View view) {
            super(view);
            articleCardView =  view.findViewById(R.id.article_card_view);
            nameStore =  view.findViewById(R.id.store_name);
            articleImage =  view.findViewById(R.id.article_image_feed);
            txtTitle= view.findViewById(R.id.txtTitle);
        }
    }


    public CardsHomeFeedAdapter(Context mContext, List articleList) {
        this.mContext = mContext;
        this.articlesAndStoresList = articleList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_card_home, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if(articlesAndStoresList.get(position).getClass()==Article.class){
            final Article article =(Article) articlesAndStoresList.get(position);
            holder.nameStore.setText(article.getStoreName());
            holder.txtTitle.setText(article.getTitle());
            // loading article image using Glide library
            Glide.with(mContext).load(article.getPicture()).into(holder.articleImage);

            holder.articleCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent= new Intent(mContext, ArticleInfoActivity.class);
                    Log.d("info del articulo", "onClick: paso por intent la data del articulo");
                    intent.putExtra("article", article);
                    mContext.startActivity(intent);

                }
            });
            holder.articleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(mContext, ArticleInfoActivity.class);
                    Log.d("info del articulo", "onClick: paso por intent la data del articulo");
                    intent.putExtra("article", article);
                    mContext.startActivity(intent);
                }
            });
        }else
            if(articlesAndStoresList.get(position).getClass()== Store.class){
            final Store store =(Store) articlesAndStoresList.get(position);
            holder.txtTitle.setText(store.getStoreName());
            holder.nameStore.setText("Ver Tienda");
            // loading article image using Glide library
            Glide.with(mContext).load(store.getProfilePh()).into(holder.articleImage);

            holder.articleCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent= new Intent(mContext, StoreActivity.class);
                    Log.d("perfil tienda", "onClick: paso por intent la data del articulo");
                    intent.putExtra("Tienda", store.getStoreName());
                    mContext.startActivity(intent);

                }
            });
            holder.articleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(mContext, StoreActivity.class);
                    Log.d("perfil tienda", "onClick: paso por intent la data del articulo");
                    intent.putExtra("Tienda", store.getStoreName());
                    mContext.startActivity(intent);
                }
            });

        }else{
                final PremiumUser premiumUser =(PremiumUser) articlesAndStoresList.get(position);
                holder.txtTitle.setText(premiumUser.getUserName());
                holder.nameStore.setText("Ver Usuario destacado");
                // loading article image using Glide library
                Glide.with(mContext).load(premiumUser.getProfilePhoto()).into(holder.articleImage);

                holder.articleCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent= new Intent(mContext, PremiumUserProfileActivity.class);
                        Log.d("perfil usuario", "onClick: paso por intent la data del card");
                        intent.putExtra("clientId", premiumUser.getClientId());
                        mContext.startActivity(intent);

                    }
                });
                holder.articleImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent= new Intent(mContext, PremiumUserProfileActivity.class);
                        Log.d("perfil usuario", "onClick: paso por intent la data del card");
                        intent.putExtra("clientId", premiumUser.getClientId());
                        mContext.startActivity(intent);

                    }
                });

            }

    }

    @Override
    public int getItemCount() {
        return articlesAndStoresList.size();
    }
}
