package com.mylook.mylook.recommend;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Store;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.storeProfile.StoreActivity;

import java.util.HashMap;
import java.util.List;

public class AnswersRecyclerViewAdapter extends RecyclerView.Adapter<AnswersRecyclerViewAdapter.ViewHolder> {

    private final FirebaseFirestore dB;
    private Context mContext;
    private List<HashMap<String, String>> answersList;


    AnswersRecyclerViewAdapter(Context mContext, List<HashMap<String,String>> answersList) {
        this.mContext = mContext;
        this.answersList = answersList;
        dB= FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d("CardAnswer", "onBindViewHolder: called.");

        final HashMap<String,String> answer = answersList.get(position);

        Glide.with(mContext).asBitmap().load(answer.get("storePhoto")).into(holder.imgStore);
        Glide.with(mContext).asBitmap().load(answer.get("articlePhoto")).into(holder.imgArticle);
        holder.txtStore.setText(answer.get("storeName"));
        holder.txtDescription.setText(answer.get("description"));
        if(answer.containsKey("feedback") && !answer.get("feedBack").equals("")) {
            holder.ratingBar.setRating(Float.parseFloat(answer.get("feedBack")));
            holder.ratingBar.setEnabled(false);
        }
        else
            holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) ->
                    answer.put("feedBack",String.valueOf(rating)));

        holder.imgArticle.setOnClickListener(view -> {
            Log.d("AnswerRecyclerViewAdap", "onClick: clicked on: " + position);
            dB.collection("articles")
                    .document(answer.get("articleUID")).get().addOnCompleteListener(task -> {
                        if(task.isSuccessful())
                        {
                            Article art= task.getResult().toObject(Article.class);
                            art.setArticleId(answer.get("articleUID"));
                            Intent intent = new Intent(mContext, ArticleInfoActivity.class);
                            intent.putExtra("article", art);
                            mContext.startActivity(intent);
                        }
                    });
        });

        holder.txtStore.setOnClickListener(view -> {
            Log.d("AnswerRecyclerViewAdap", "onClick: clicked on: " + position);
            dB.collection("stores")
                    .whereEqualTo("storeName",answer.get("storeName")).get()
                    .addOnCompleteListener(task -> {
                        Store store = task.getResult().toObjects(Store.class).get(0);
                        Intent intent = new Intent(mContext, StoreActivity.class);
                        intent.putExtra("Tienda", store.getStoreName());
                        mContext.startActivity(intent);
                    }
                    );
        });
    }

    @Override
    public int getItemCount() {
        return answersList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgArticle;
        TextView txtStore;
        TextView txtDescription;
        RatingBar ratingBar;
        RelativeLayout parentLayout;
        ImageView imgStore;

        ViewHolder(View itemView) {
            super(itemView);
            imgArticle = itemView.findViewById(R.id.imgArticle);
            txtStore = itemView.findViewById(R.id.txtStore);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            txtDescription= itemView.findViewById(R.id.txtDescription);
            parentLayout=itemView.findViewById(R.id.parentLayout);
            imgStore=itemView.findViewById(R.id.imgStore);
        }
    }
}
