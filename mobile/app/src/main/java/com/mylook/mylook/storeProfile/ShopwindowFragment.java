package com.mylook.mylook.storeProfile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.utils.GridImageAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

public class ShopwindowFragment extends Fragment {

    private static String storeName;
    private static String coverPh;

    public ShopwindowFragment() {
    }

    @SuppressLint("ValidFragment")
    public ShopwindowFragment(String name, String cover) {
        storeName=name;
        coverPh=cover;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate( R.layout.fragment_shopwindow, container, false);

        // Obtención del grid view
        GridViewWithHeaderAndFooter grid = rootView.findViewById(R.id.gridview);
        // Inicializar el grid view
        if(coverPh!=null)
            grid.addHeaderView(createHeaderView());
        setupShopWindowGridView(grid);
        return rootView;
    }

    private View createHeaderView() {

        View view;

        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.grid_header, null, false);
        // Seteando Imagen
        ImageView image =  view.findViewById(R.id.header);
        Glide.with(image.getContext()).load(coverPh).into(image);
        return view;
    }

    private void setupShopWindowGridView(final GridViewWithHeaderAndFooter grid) {

        Log.d("Store Catalog gridView", "setupGridView: Setting up store grid.");
        final ArrayList<Article> storeShopWindowArticles = new ArrayList<Article>();
        final String[] documentID = new String[1];
        FirebaseFirestore.getInstance().collection("storeFronts").whereEqualTo("storeName", storeName).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                                documentID[0] = task.getResult().getDocuments().get(0).getId();
                                FirebaseFirestore.getInstance().collection("storeFronts").document(documentID[0]).collection("storeFronts").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                ArrayList<HashMap> array = (ArrayList<HashMap>) task.getResult().getDocuments().get(0).get("articles");
                                                for (HashMap o : array) {
                                                    Article art = new Article();
                                                    storeShopWindowArticles.add(art.toObject(o));
                                                }
                                            }
                                        });
                                Log.e("VIDRIERAAAA", getActivity().getLocalClassName());
                                grid.setAdapter(new GridImageAdapter( getActivity(),R.layout.layout_grid_imageview,storeShopWindowArticles));

                        } else {
                            if(task.getException()!=null)
                            Log.e("Firestore task", "onComplete: " + task.getException());
                            else
                                Log.e("Firestore task", "onComplete: No existe vidriera");

                        }
                    }
                });

    }

}


