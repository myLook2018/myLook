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
import com.mylook.mylook.info.ArticleInfoActivity;

import java.util.List;

public class CardsHomeFeedAdapter extends RecyclerView.Adapter<CardsHomeFeedAdapter.MyViewHolder> {

    private Context mContext;
    private List<Article> articleList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nameStore, costArticle,stockArticle;
        public ImageView articleImage;
        public CardView articleCardView;

        public MyViewHolder(View view) {
            super(view);
            articleCardView = (CardView) view.findViewById(R.id.article_card_view);
            nameStore = (TextView) view.findViewById(R.id.store_name);
            costArticle = (TextView) view.findViewById(R.id.cost_article);
            stockArticle = (TextView) view.findViewById(R.id.stock_article);
            articleImage = (ImageView) view.findViewById(R.id.article_image_feed);

            /*articleCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent= new Intent(mContext, ArticleInfoActivity.class);;
                    mContext.startActivity(intent);

                }
            });*/
        }
    }


    public CardsHomeFeedAdapter(Context mContext, List<Article> articleList) {
        this.mContext = mContext;
        this.articleList = articleList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_card_feed, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Article article = articleList.get(position);
        holder.nameStore.setText(article.getStoreName());
        holder.costArticle.setText("$" + article.getCost());
        holder. stockArticle.setText("Stock: " + article.getInitial_stock());

        // loading article image using Glide library
        Glide.with(mContext).load(article.getPicture()).into(holder.articleImage);

        holder.articleCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(mContext, ArticleInfoActivity.class);
                Log.d("info del articulo", "onClick: paso por intent la data del articulo");
                intent.putExtra("Colores", article.getColors());
                intent.putExtra("Costo", article.getCost());
                intent.putExtra("Stock", article.getInitial_stock());
                intent.putExtra("Material", article.getMaterial());
                intent.putExtra("Talle", article.getSize());
                intent.putExtra("Tienda", article.getStoreName());
                intent.putExtra("Foto", article.getPicture());
                intent.putExtra("Title",article.getTitle());
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }
}
