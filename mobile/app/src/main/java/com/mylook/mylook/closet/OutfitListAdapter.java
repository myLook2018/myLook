package com.mylook.mylook.closet;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

    private OutfitClickListener clickListener;
    private List<Outfit> outfits;
    private Context context;
    private Drawable placeholder;

    OutfitListAdapter(Context context, List<Outfit> outfits, OutfitClickListener mClickListener) {
        this.context = context;
        this.outfits = outfits;
        this.clickListener = mClickListener;
        this.placeholder = ContextCompat.getDrawable(context, R.drawable.placeholder_outfit_item);
    }

    @NonNull
    @Override
    public OutfitViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.outfit_element_layout,
                viewGroup, false);
        return new OutfitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutfitViewHolder outfitViewHolder, int i) {
        Outfit outfit = outfits.get(i);
        outfitViewHolder.name.setText(outfit.getName());
        int count = outfit.getArticles().size();
        outfitViewHolder.extra.setText(count == 1 ?
                String.format("%d artículo", count) : String.format("%d artículos", count));
        outfitViewHolder.btnView.setOnClickListener(v -> clickListener.showOutfit(v, i));
        outfitViewHolder.btnEdit.setOnClickListener(v -> clickListener.editOutfit(v, i));
        outfitViewHolder.btnDelete.setOnClickListener(v -> clickListener.deleteOutfit(v, i));

        /*
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.one:
                                Toast.makeText(getApplicationContext(), "1",
                                        Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.two:
                                Toast.makeText(getApplicationContext(), "2",
                                        Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.three:
                                Toast.makeText(getApplicationContext(), "3",
                                        Toast.LENGTH_SHORT).show();
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.main);
                popupMenu.show();
            }
        });
        */


        switch (count > 3 ? 3 : count) {
            case 3:
                Glide.with(context).asDrawable()
                        .load(outfit.getArticles().get(2).getPicture())
                        .into(outfitViewHolder.image3);
            case 2:
                Glide.with(context).asDrawable()
                        .load(outfit.getArticles().get(1).getPicture())
                        .into(outfitViewHolder.image2);
            case 1:
                Glide.with(context).asDrawable()
                        .load(outfit.getArticles().get(0).getPicture())
                        .into(outfitViewHolder.image1);
        }
    }

    @Override
    public int getItemCount() {
        return outfits.size();
    }

    class OutfitViewHolder extends RecyclerView.ViewHolder {
        TextView name, extra;
        ImageView image1, image2, image3;
        MaterialButton btnView, btnEdit, btnDelete;

        OutfitViewHolder(View itemView) {
            super(itemView);
            image1 = itemView.findViewById(R.id.outfit_article_image1);
            image2 = itemView.findViewById(R.id.outfit_article_image2);
            image3 = itemView.findViewById(R.id.outfit_article_image3);
            name = itemView.findViewById(R.id.outfit_name);
            extra = itemView.findViewById(R.id.outfit_quantity_articles);
            btnView = itemView.findViewById(R.id.view_outfit_btn);
            btnEdit = itemView.findViewById(R.id.edit_outfit_btn);
            btnDelete = itemView.findViewById(R.id.delete_outfit_btn);
        }
    }

    Outfit getItem(int position) {
        return outfits.get(position);
    }

    public interface OutfitClickListener {
        void showOutfit(View v, int position);

        void editOutfit(View v, int position);

        void deleteOutfit(View v, int position);
    }

}
