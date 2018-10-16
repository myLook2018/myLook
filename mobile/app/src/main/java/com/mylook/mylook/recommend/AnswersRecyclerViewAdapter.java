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
import com.mylook.mylook.R;
import com.mylook.mylook.profile.ProfileActivity;

import java.util.HashMap;
import java.util.List;


public class AnswersRecyclerViewAdapter extends RecyclerView.Adapter<AnswersRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "AnswersRecyclerViewAdapter";
    private Context mContext;
    private List<HashMap<String, String>> answersList;


    public AnswersRecyclerViewAdapter(Context mContext, List<HashMap<String,String>> answersList) {
        this.mContext = mContext;
        this.answersList = answersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_card, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Log.d("CardAnswer", "onBindViewHolder: called.");

        final HashMap<String,String> answer = answersList.get(position);

        Glide.with(mContext).asBitmap().load(answer.get("storePhoto")).into(holder.imgStore);
        Glide.with(mContext).asBitmap().load(answer.get("articlePhoto")).into(holder.imgArticle);
        holder.txtStore.setText(answer.get("storeName"));
        holder.txtDescription.setText(answer.get("description"));
        if(!answer.get("feedBack").equals(""))
            holder.ratingBar.setRating(Float.parseFloat(answer.get("feedBack")));
        else
            holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    answer.put("feedBack",String.valueOf(rating));
                }
            });

        holder.imgArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("AnswerRecyclerViewAdap", "onClick: clicked on: " + position);

                Intent intent = new Intent(mContext, ProfileActivity.class);
                //intent.putExtra("requestRecommendation", answer.);
                mContext.startActivity(intent);
            }
        });
        holder.txtStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return answersList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgArticle;
        TextView txtStore;
        TextView txtDescription;
        RatingBar ratingBar;
        RelativeLayout parentLayout;
        ImageView imgStore;

        public ViewHolder(View itemView) {
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
