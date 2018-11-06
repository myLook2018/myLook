package com.mylook.mylook.info;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.utils.GridImageAdapter;

import java.util.ArrayList;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

public class StoreFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private final FirebaseFirestore dB;
    private static String storeName;
    private static String coverPh;


    public StoreFragment() {
        dB = FirebaseFirestore.getInstance();
    }

    public static StoreFragment newInstance(int sectionNumber, String name, String cover) {
        StoreFragment fragment = new StoreFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        storeName=name;
        coverPh=cover;
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shopwindow, container, false);

        // Obtención del grid view
        GridViewWithHeaderAndFooter grid = rootView.findViewById(R.id.gridview);

        // Inicializar el grid view
        setUpGridView(grid);

        return rootView;
    }
    /**
     * Infla el grid view del fragmento dependiendo de la sección
     *
     * @param grid Instancia del grid view
     */
    private void setUpGridView(GridViewWithHeaderAndFooter grid) {
        int section_number = getArguments().getInt(ARG_SECTION_NUMBER);
        switch (section_number) {
            case 1: //Vidriera
                grid.addHeaderView(createHeaderView());
                setupGridView(grid); //Vidriera
                //grid.setAdapter(new GridImageAdapter( StoreFragment.this,R.layout.fragment_shopwindow,));
                break;
            case 2://Catalogo
               // grid.addHeaderView(createHeaderView(6, Products.getTablets()));
               // grid.setAdapter(new GridImageAdapter(StoreFragment.this,R.layout.fragment_shopwindow,));
                break;
            case 3: //reputacion
                //grid.addHeaderView(createHeaderView(6, Products.getPortatiles()));
               // grid.setAdapter(new GridImageAdapter( StoreFragment.this,R.layout.fragment_shopwindow, ));
                break;
        }
    }
   private View createHeaderView() {

        View view;

        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.grid_header, null, false);
        // Seteando Imagen
        ImageView image =  view.findViewById(R.id.header);
        Log.e("COVERRRRRRRRRRRRRRRR",coverPh);
        Glide.with(image.getContext()).load(coverPh).into(image);

        return view;
    }

    private void setupGridView(final GridViewWithHeaderAndFooter grid) {

        Log.d("Store gridView", "setupGridView: Setting up store grid.");
        final ArrayList<Article> storeArticles = new ArrayList<Article>();
        final ArrayList<String> articlesPhotosUrls = new ArrayList<String>();
        dB.collection("articles").whereEqualTo("storeName", storeName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentReference:task.getResult().getDocuments()){
                        Article art= documentReference.toObject(Article.class);
                        art.setArticleId(documentReference.getId());
                        storeArticles.add(art);
                        articlesPhotosUrls.add(art.getPicture());
                    }
                    Log.e("CONTEXT", getActivity().getLocalClassName());
                    grid.setAdapter(new GridImageAdapter( getActivity(),R.layout.layout_grid_imageview,storeArticles));
                } else {
                    Log.d("Firestore task", "onComplete: " + task.getException());
                }
            }
        });

    }
}
