package com.mylook.mylook.recommend;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Store;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.storeProfile.StoreActivity;

import java.util.HashMap;
import java.util.List;


public class AnswersRecyclerViewAdapter extends RecyclerView.Adapter<AnswersRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "AnswersRecyclerViewAdapter";
    private final FirebaseFirestore dB;
    private Context mContext;
    private List<HashMap<String, String>> answersList;


    public AnswersRecyclerViewAdapter(Context mContext, List<HashMap<String,String>> answersList) {
        this.mContext = mContext;
        this.answersList = answersList;
        dB= FirebaseFirestore.getInstance();
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
        if(answer.containsKey("feedBack") && !answer.get("feedBack").equals("")) {
            holder.ratingBar.setRating(Float.parseFloat(answer.get("feedBack")));
            holder.ratingBar.setEnabled(false);
        }
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
                dB.collection("articles")
                        .document(answer.get("articleUID")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Article art= task.getResult().toObject(Article.class);
                            art.setArticleId(answer.get("articleUID"));
                            Intent intent = new Intent(mContext, ArticleInfoActivity.class);
                            intent.putExtra("article", art);
                            mContext.startActivity(intent);
                        }
                    }
                });
            }
        });

        holder.txtStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("AnswerRecyclerViewAdap", "onClick: clicked on: " + position);
                dB.collection("stores")
                        .whereEqualTo("storeName",answer.get("storeName")).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                       Store store = (Store) task.getResult().toObjects(Store.class).get(0);
                                                       Intent intent = new Intent(mContext, StoreActivity.class);
                                                       intent.putExtra("store", store.getStoreName());
                                                       mContext.startActivity(intent);
                                                   }
                                               }
                        );
            }});
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
