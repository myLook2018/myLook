package com.mylook.mylook.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Favorite;
import com.mylook.mylook.entities.Outfit;

import java.util.ArrayList;

public class OutfitAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Outfit> outfits;

    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    public OutfitAdapter(Context c,ArrayList favorites) {
        mContext = c;
        this.outfits=favorites;

    }


    public int getCount() {
        return outfits.size();
    }

    public Object getItem(int position) {
        return outfits.get(position);
    }
    public long getItemId(int position) {
        return 0;
    }
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater factory = LayoutInflater.from(mContext);
        View myView = factory.inflate(R.layout.outfit_item, null);
        ImageView imageView = myView.findViewById(R.id.topLeftCloth);

        imageView.setAdjustViewBounds(true);
        int i  = 0;
        for (String k : outfits.get(position).getItems().keySet()) {
            String id = outfits.get(position).getItems().get(k);
            if(i == 0){
                loadImage(imageView,id);
            }
            if(i == 1){
                imageView = myView.findViewById(R.id.topRightCloth);
                loadImage(imageView,id);
            }
            if(i==2){
                imageView = myView.findViewById(R.id.bottomLeftCloth);
                loadImage(imageView,id);
            }
            if(i==3){
                imageView = myView.findViewById(R.id.bottomRightCloth);
                loadImage(imageView,id);
                break;
            }


            i++;

        }



        TextView text = myView.findViewById(R.id.outfitName);
        text.setText(outfits.get(position).getName());
        return myView;
    }

    private void loadImage(final View v,String articleId){
        dB.collection("articles").document(articleId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Glide.with(mContext).asDrawable().load(task.getResult().get("picture")).into((ImageView) v);
                }
        });
    }
}
