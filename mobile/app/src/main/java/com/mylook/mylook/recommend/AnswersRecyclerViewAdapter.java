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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Store;
import com.mylook.mylook.home.StoreActivity;

import java.util.HashMap;
import java.util.List;


public class AnswersRecyclerViewAdapter extends RecyclerView.Adapter<AnswersRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "AnswersRecyclerViewAdapter";
    private Context mContext;
    private List<HashMap<String, String>> answersList;
    private FirebaseFirestore dB;


    public AnswersRecyclerViewAdapter(Context mContext, List<HashMap<String,String>> answersList) {
        this.mContext = mContext;
        this.answersList = answersList;
        dB=FirebaseFirestore.getInstance();
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
                /*
                Log.d("AnswerRecyclerViewAdap", "onClick: clicked on: " + position);
                DocumentReference docRef = dB.collection("articles").document(answer.get("articleUID"));
                ApiFuture<DocumentSnapshot> future = docRef.get();

                DocumentSnapshot document = docRef.get().get();
                Article art = null;
                if (document.exists()) {
                    // convert document to POJO
                    art = document.toObject(Article.class);
                    Intent intent = new Intent(mContext, ArticleInfoActivity.class);
                    intent.putExtra("Tienda", art.getStoreName());
                    intent.putExtra("Costo", art.getCost());
                    intent.putExtra("Costo", art.getCost());
                    intent.putExtra("Stock", art.getInitial_stock());
                    intent.putExtra("Colores", art.getColors());
                    intent.putExtra("Material", art.getMaterial());
                    intent.putExtra("Talles", art.getSize());
                    mContext.startActivity(intent);
            }*/
        }});

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
                                intent.putExtra("Tienda", store.getStoreName());
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
