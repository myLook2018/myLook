package com.mylook.mylook.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Outfit;

import java.util.ArrayList;
import java.util.List;

public class OutfitListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Outfit> outfits;

    public OutfitListAdapter(Context context, ArrayList outfits) {
        mContext = context;
        this.outfits = outfits;
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

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View myView = inflater.inflate(R.layout.outfit_element_layout, null);
        TextView name = myView.findViewById(R.id.name);
        name.setText(outfits.get(position).getName());

        int count = outfits.get(position).getArticles().size();
        switch (count) {

        }


        imageView.setAdjustViewBounds(true);
        int i = 0;

        for (String k : outfits.get(position).getItems().keySet()) {
            String id = outfits.get(position).getItems().get(k);
            if (i == 0) {
                loadImage(imageView, id);
            }
            if (i == 1) {
                imageView = myView.findViewById(R.id.bottomRightCloth);
                loadImage(imageView, id);
            }
            if (i == 2) {
                imageView = myView.findViewById(R.id.topRightCloth);
                loadImage(imageView, id);
            }
            if (i == 3) {
                imageView = myView.findViewById(R.id.bottomLeftCloth);
                loadImage(imageView, id);
                break;
            }


            i++;

        }


        TextView text = myView.findViewById(R.id.outfitName);
        text.setText(outfits.get(position).getName());
        return myView;
    }

    private void loadImage(final View v, String articleId) {
        dB.collection("articles").document(articleId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Glide.with(mContext).asDrawable().load(task.getResult().get("picture")).into((ImageView) v);
            }
        });
    }

    public void setOutfits(List<Outfit> outfits) {
    }
}
